/**
 * Controller for MainView
 * @file MainView.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.guifx.ui_main;

import java.io.File;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.beans.binding.When;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import sdv.testingall.cdt.gentestdata.CppStaticTestDataGeneration;
import sdv.testingall.cdt.gentestdata.solver.CppZ3SolverFactory;
import sdv.testingall.cdt.loader.CppProjectLoader;
import sdv.testingall.core.gentestdata.GenerationController;
import sdv.testingall.core.logger.BaseLogger;
import sdv.testingall.core.logger.ILogger;
import sdv.testingall.core.node.FolderNode;
import sdv.testingall.core.node.FunctionNode;
import sdv.testingall.core.node.IFileNode;
import sdv.testingall.core.node.IInsideFileNode;
import sdv.testingall.core.node.INode;
import sdv.testingall.core.node.ProjectNode;
import sdv.testingall.core.testreport.IFunctionReport;
import sdv.testingall.guifx.GUIUtil;
import sdv.testingall.guifx.ImageSet;
import sdv.testingall.guifx.Setting;
import sdv.testingall.guifx.node.ConsoleView;
import sdv.testingall.guifx.node.LightTabPane;
import sdv.testingall.guifx.node.ProjectExplorer;
import sdv.testingall.guifx.node.SyntaxTextArea;
import sdv.testingall.guifx.ui_setting.SettingDialog;
import sdv.testingall.util.SDVUtils;

/**
 * Controller for MainView
 * 
 * @author VuSD
 *
 * @date 2016-11-22 VuSD created
 */
public class MainView implements Initializable {

	private @FXML Menu				menu_open_recent;
	private @FXML MenuItem			menu_open_project;
	private @FXML MenuItem			menu_open_recent_empty;
	private @FXML Pane				project_mask_view;
	private @FXML ProgressIndicator	project_load_indicator;
	private @FXML ProjectExplorer	project_tree;
	private @FXML LightTabPane		source_view;
	private @FXML ConsoleView		console_area;
	private @FXML Button			btn_console_clear;

	private @FXML Label	status_encoding;
	private @FXML Label	status_keylock;

	private Stage					primaryStage;
	private DirectoryChooser		fileOpenChooser;
	private Setting					setting;
	private ResourceBundle			appRes;
	private ToggleGroup				menu_open_recent_group;
	private SimpleBooleanProperty	propLoadingProject	= new SimpleBooleanProperty(false);

	private ProjectNode currentProject;

	@Override
	public void initialize(URL location, ResourceBundle res)
	{
		fileOpenChooser = new DirectoryChooser();
		fileOpenChooser.setTitle(res.getString("file.openproject"));

		appRes = res;
		setupProjectTreeContextMenu();

		btn_console_clear.setGraphic(new ImageView(ImageSet.CLEAR));
		menu_open_recent_group = new ToggleGroup();

		menu_open_project.disableProperty().bind(propLoadingProject);
		menu_open_recent.disableProperty().bind(propLoadingProject);
		project_mask_view.visibleProperty().bind(propLoadingProject);
		project_load_indicator.visibleProperty().bind(propLoadingProject);
		project_load_indicator.progressProperty()
				.bind(new When(propLoadingProject).then(ProgressIndicator.INDETERMINATE_PROGRESS).otherwise(0.0));
	}

	/**
	 * Send data from main entry point to this controller
	 * 
	 * @param primaryStage
	 *            main application stage
	 * @param setting
	 *            application setting
	 */
	public void initData(Stage primaryStage, Setting setting)
	{
		this.primaryStage = primaryStage;
		this.setting = setting;

		// Add recent projects to menu
		if (setting.RECENT_PROJECT.size() > 0) {
			List<MenuItem> list_recent = menu_open_recent.getItems();
			list_recent.clear();
			for (File root : setting.RECENT_PROJECT) {
				list_recent.add(createRecentProjectItem(root));
			}
		}

		// Update status bar
		status_encoding.textProperty().bind(setting.APP_CHARSET.asString());
		status_keylock.setText(computeKeyLock());
		primaryStage.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
			if (e.getCode() == KeyCode.CAPS || e.getCode() == KeyCode.NUM_LOCK) {
				status_keylock.setText(computeKeyLock());
			}
		});
	}

	/**
	 * Open and load project
	 * 
	 * @param root
	 *            root project file
	 */
	public void openProject(File root)
	{
		// Create a worker task
		Task<ProjectNode> loadProjectTask = new Task<ProjectNode>() {

			@Override
			protected void scheduled()
			{
				clearOldData();
				propLoadingProject.set(true);
			}

			@Override
			protected void succeeded()
			{
				finished();
				project_tree.setRoot(currentProject = getValue());

				File root = getValue().getFile();
				addLoadedProjectToRecent(root);
				fileOpenChooser.setInitialDirectory(root);
			}

			protected void finished()
			{
				propLoadingProject.set(false);
			}

			@Override
			protected void cancelled()
			{
				setting.getLogger().log(ILogger.INFO, setting.resString("loader.canceled"));
				finished();
			}

			@Override
			protected void failed()
			{
				setting.getLogger().log(ILogger.ERROR, setting.resString("loader.error.loadproject"),
						SDVUtils.gxceptionMsg(getException()));
				finished();
			}

			@Override
			protected ProjectNode call() throws Exception
			{
				setting.setLogger(new BaseLogger() {

					@Override
					public ILogger log(int type, String message, Object... args)
					{
						StringBuilder b = new StringBuilder(type == ILogger.ERROR ? "[ERROR] " : "[INFO] ");
						b.append(String.format(message, args)).append('\n');

						// Must be update from Application thread
						updateMessage(b.toString());
						return super.log(type, message, args);
					}

				});
				CppProjectLoader loader = new CppProjectLoader(root);
				loader.setLoaderConfig(setting);

				setting.getLogger().log(ILogger.INFO, setting.resString("gui.loadingproject"), root.getName());
				return loader.loadProject();
			}

		};

		// Register console logger
		loadProjectTask.messageProperty().addListener((obs, oldValue, newValue) -> {
			console_area.appendText(newValue);
		});

		// Start the worker thread
		Thread thread = new Thread(loadProjectTask);
		thread.setDaemon(true);
		thread.start();
	}

	/**
	 * Beginning generate test data for given function
	 * 
	 * @param function
	 *            function to generate test data
	 */
	public void generateTestData(FunctionNode function)
	{
		// Create generating task
		Task<IFunctionReport> taskGenTest = new Task<IFunctionReport>() {

			@Override
			protected void scheduled()
			{
				//
			}

			@Override
			protected void succeeded()
			{
				//
			}

			@Override
			protected void cancelled()
			{
				//
			}

			@Override
			protected void failed()
			{
				//
			}

			@Override
			protected IFunctionReport call() throws Exception
			{
				setting.setLogger(new BaseLogger() {
					@Override
					public ILogger log(int type, String message, Object... args)
					{
						StringBuilder b = new StringBuilder();
						b.append(String.format(message, args)).append('\n');

						// Must be update from Application thread
						updateMessage(b.toString());
						return super.log(type, message, args);
					}
				});

				GenerationController testgen = new GenerationController(currentProject, function, setting);
				testgen.addSolver(new CppZ3SolverFactory());
				testgen.addStraitgy(new CppStaticTestDataGeneration(currentProject, function, setting));

				return testgen.generateData();
			}
		};

		// Start the worker thread
		Thread thread = new Thread(taskGenTest);
		thread.setDaemon(true);
		thread.start();
	}

	/**
	 * Open a source code view
	 * 
	 * @param source
	 *            source file to open
	 * @return opened tab content
	 */
	public SyntaxTextArea openSourceFile(File source)
	{
		try {
			return source_view.openTab(source.getName(), source.getAbsolutePath(),
					SyntaxTextArea.class.getConstructor(File.class, Charset.class), source, setting.APP_CHARSET.get());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Save setting to file
	 */
	public void saveSetting()
	{
		try {
			setting.save();
		} catch (Exception e) {
			e.printStackTrace();
			GUIUtil.alert(AlertType.ERROR, setting.resString("gui.errorhappen"), null, SDVUtils.gxceptionMsg(e),
					ImageSet.APPLICATION);
		}
	}

	/**
	 * Handle when open source view requested
	 * 
	 * @param node
	 *            node item to view source
	 */
	protected void handleOpenSourceView(INode node)
	{
		if (node instanceof IFileNode) {
			File source = ((IFileNode) node).getFile();

			if (source.isFile()) {
				openSourceFile(source);
			}
		} else if (node instanceof IInsideFileNode) {
			IInsideFileNode insideNode = (IInsideFileNode) node;
			openSourceFile(insideNode.getFile()).setSelected(insideNode);
		}
	}

	/**
	 * Setup project tree context menu handle
	 */
	protected void setupProjectTreeContextMenu()
	{
		// Select listener
		project_tree.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue == null || !setting.TREE_AUTO_VIEWSOURCE.get()) {
				return;
			}
			handleOpenSourceView(newValue.getValue());
		});

		// Context menu
		MenuItem itemOpenCFG = new MenuItem(appRes.getString("mitem.opencfg"), new ImageView(ImageSet.MENU_CFG));
		MenuItem itemGenTest = new MenuItem(appRes.getString("mitem.gentest"), new ImageView(ImageSet.MENU_GENTEST));
		MenuItem itemViewsource = new MenuItem(appRes.getString("mitem.viewsource"),
				new ImageView(ImageSet.MENU_VIEWSOURCE));
		MenuItem itemPrFolder = new MenuItem(appRes.getString("mitem.project.folder"),
				new ImageView(ImageSet.MENU_PR_FOLDER));
		MenuItem itemPrConfig = new MenuItem(appRes.getString("mitem.project.config"),
				new ImageView(ImageSet.MENU_PR_CONFIG));

		itemGenTest.setOnAction(e -> generateTestData((FunctionNode) getProjectSelectedItem()));
		itemViewsource.setOnAction(e -> handleOpenSourceView(getProjectSelectedItem()));
		itemPrFolder.setOnAction(e -> {
			try {
				File root = ((IFileNode) getProjectSelectedItem()).getFile();
				if (root.isFile()) {
					root = root.getParentFile();
				}
				java.awt.Desktop.getDesktop().open(root);
			} catch (Exception e1) {
				e1.printStackTrace();
				GUIUtil.alert(AlertType.ERROR, setting.resString("gui.errorhappen"), null,
						setting.resString("gui.unsupportoperator"), ImageSet.APPLICATION);
			}
		});

		// project_tree.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		project_tree.getContextMenu().getItems().addAll(itemOpenCFG, itemGenTest, itemViewsource, itemPrFolder,
				itemPrConfig);
		project_tree.setMenuHandle(listNode -> {
			if (listNode.isEmpty()) {
				return false;
			}
			// Currently work with single selection
			INode node = listNode.get(0);
			boolean hasSource = false;
			boolean isFunction = node instanceof FunctionNode;
			boolean isProject = node instanceof ProjectNode;
			boolean isFolder = node instanceof FolderNode;

			if (node instanceof IFileNode) {
				File source = ((IFileNode) node).getFile();
				hasSource = source.isFile();
			} else if (node instanceof IInsideFileNode) {
				hasSource = true;
			}

			itemOpenCFG.setVisible(isFunction);
			itemGenTest.setVisible(isFunction);
			itemViewsource.setVisible(hasSource && !setting.TREE_AUTO_VIEWSOURCE.get());
			itemPrFolder.setVisible(isProject || isFolder);
			itemPrConfig.setVisible(isProject);
			return true;
		});
	}

	/**
	 * Get the selecting item in project view.<br/>
	 * Project view must selecting one or more item when calling this method
	 * 
	 * @return selecting item
	 */
	protected INode getProjectSelectedItem()
	{
		return project_tree.getSelectionModel().getSelectedItem().getValue();
	}

	/**
	 * Create new recent project entry
	 * 
	 * @param root
	 *            project root file
	 * @return corresponding menu item
	 */
	protected RadioMenuItem createRecentProjectItem(File root)
	{
		RadioMenuItem item = new RadioMenuItem(root.getName());
		item.setMnemonicParsing(false);
		item.setToggleGroup(menu_open_recent_group);
		item.setUserData(root);
		item.setOnAction(e -> {
			RadioMenuItem menuItem = (RadioMenuItem) e.getSource();
			File rootData = (File) menuItem.getUserData();

			if (!rootData.exists()) {
				GUIUtil.alert(AlertType.ERROR, setting.resString("gui.errorhappen"), null,
						setting.resString("gui.project_not_exist"), ImageSet.APPLICATION);
				removeErrorProjectFromRecent(rootData);
			} else {
				openProject(rootData);
			}
		});
		return item;
	}

	/**
	 * Add opened project to project recent
	 * 
	 * @param root
	 *            project root file
	 */
	protected void addLoadedProjectToRecent(File root)
	{
		RadioMenuItem item = null;
		int index;
		List<MenuItem> list_recent = menu_open_recent.getItems();

		// Check if recent entry is blank or not
		if (setting.RECENT_PROJECT.size() == 0) {
			list_recent.clear();
		} else {
			index = setting.RECENT_PROJECT.indexOf(root);

			// Check if recent entry contains opened project
			if (index >= 0) {
				item = findMenuItemByData(root);

				// Move entry to first
				if (index > 0) {
					setting.RECENT_PROJECT.remove(index);
					setting.RECENT_PROJECT.add(0, root);
				}
			}
		}

		// Opened project not in recent entry, create a new one
		if (item == null) {
			item = createRecentProjectItem(root);
			list_recent.add(0, item);
			setting.RECENT_PROJECT.add(0, root);

			// Remove if exceed maximum
			if (setting.RECENT_PROJECT.size() > setting.RECENT_PROJECT_MAXSIZE.get()) {
				index = setting.RECENT_PROJECT.size() - 1;
				File removed = setting.RECENT_PROJECT.remove(index);
				list_recent.remove(findMenuItemByData(removed));
			}
		}

		item.setSelected(true);
		saveSetting();
	}

	/**
	 * Find the menu item associated with given data
	 * 
	 * @param data
	 *            project root file
	 * @return menu item
	 */
	protected RadioMenuItem findMenuItemByData(File data)
	{
		for (MenuItem item : menu_open_recent.getItems()) {
			if (item.getUserData().equals(data)) {
				return (RadioMenuItem) item;
			}
		}
		return null;
	}

	/**
	 * Remove the project from project recent
	 * 
	 * @param root
	 *            project root file
	 */
	protected void removeErrorProjectFromRecent(File root)
	{
		List<MenuItem> list_recent = menu_open_recent.getItems();
		setting.RECENT_PROJECT.remove(root);
		list_recent.remove(findMenuItemByData(root));

		if (list_recent.size() == 0) {
			list_recent.add(menu_open_recent_empty);
		}

		saveSetting();
	}

	/**
	 * Clear all old project data
	 */
	protected void clearOldData()
	{
		console_area.clear();
	}

	@FXML
	protected void handleMenuOpen()
	{
		File selected = fileOpenChooser.showDialog(primaryStage);
		if (selected != null) {
			openProject(selected);
		}
	}

	@FXML
	protected void handleMenuQuit()
	{
		Platform.exit();
		System.exit(0);
	}

	@FXML
	protected void handleBtnClearConsole()
	{
		console_area.clear();
	}

	@FXML
	protected void handleMenuPreference()
	{
		SettingDialog dialog = new SettingDialog(setting);
		dialog.showAndWait();
		saveSetting();
	}

	/**
	 * Computing key lock status
	 * 
	 * @return [CAPS] [NUMS]
	 */
	static String computeKeyLock()
	{
		// TODO Change to JavaFX
		java.awt.Toolkit kit = java.awt.Toolkit.getDefaultToolkit();
		String cap = kit.getLockingKeyState(java.awt.event.KeyEvent.VK_CAPS_LOCK) ? "CAPS " : "     ";
		String num = kit.getLockingKeyState(java.awt.event.KeyEvent.VK_NUM_LOCK) ? "NUMS" : "    ";
		return cap + num;
	}

}
