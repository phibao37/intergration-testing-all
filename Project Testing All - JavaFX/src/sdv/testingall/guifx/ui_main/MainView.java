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

import org.eclipse.jdt.annotation.NonNull;

import javafx.application.Platform;
import javafx.beans.binding.When;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import sdv.testingall.cdt.loader.CppProjectLoader;
import sdv.testingall.core.logger.BaseLogger;
import sdv.testingall.core.logger.ILogger;
import sdv.testingall.core.node.FunctionNode;
import sdv.testingall.core.node.IFileNode;
import sdv.testingall.core.node.IInsideFileNode;
import sdv.testingall.core.node.INode;
import sdv.testingall.core.node.ProjectNode;
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

	private Stage				primaryStage;
	private DirectoryChooser	fileOpenChooser;
	private Setting				setting;
	private ResourceBundle		appRes;
	private ToggleGroup			menu_open_recent_group;

	private SimpleBooleanProperty propLoadingProject = new SimpleBooleanProperty(false);

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
				project_tree.setRoot(getValue());

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
					public @NonNull ILogger log(int type, @NonNull String message, Object @NonNull... args)
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

		itemViewsource
				.setOnAction(e -> handleOpenSourceView(project_tree.getSelectionModel().getSelectedItem().getValue()));

		// project_tree.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		project_tree.getContextMenu().getItems().addAll(itemOpenCFG, itemGenTest, itemViewsource);
		project_tree.setMenuHandle(listNode -> {
			if (listNode.isEmpty()) {
				return false;
			}
			// Currently work with single selection
			INode node = listNode.get(0);
			boolean hasSource = false;
			boolean isFunction = node instanceof FunctionNode;

			if (node instanceof IFileNode) {
				File source = ((IFileNode) node).getFile();
				hasSource = source.isFile();
			} else if (node instanceof IInsideFileNode) {
				hasSource = true;
			}

			itemOpenCFG.setVisible(isFunction);
			itemGenTest.setVisible(isFunction);
			itemViewsource.setVisible(hasSource && !setting.TREE_AUTO_VIEWSOURCE.get());
			return true;
		});
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

}
