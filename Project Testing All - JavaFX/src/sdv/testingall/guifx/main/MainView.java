/**
 * Controller for MainView
 * @file MainView.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.guifx.main;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import org.eclipse.jdt.annotation.NonNull;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import sdv.testingall.cdt.loader.CppLoaderConfig;
import sdv.testingall.cdt.loader.CppProjectLoader;
import sdv.testingall.core.logger.BaseLogger;
import sdv.testingall.core.logger.ILogger;
import sdv.testingall.core.node.IFileNode;
import sdv.testingall.core.node.INode;
import sdv.testingall.core.node.ProjectNode;
import sdv.testingall.guifx.node.ConsoleView;
import sdv.testingall.guifx.node.LightTabPane;
import sdv.testingall.guifx.node.ProjectExplorer;
import sdv.testingall.guifx.node.SyntaxTextArea;

/**
 * Controller for MainView
 * 
 * @author VuSD
 *
 * @date 2016-11-22 VuSD created
 */
public class MainView implements Initializable {

	private @FXML Menu				menu_open_recent;
	private @FXML ProjectExplorer	project_tree;
	private @FXML LightTabPane		source_view;
	private @FXML ConsoleView		console_area;
	private @FXML Button			btn_console_clear;

	private Stage				primaryStage;
	private DirectoryChooser	fileOpenChooser;

	@Override
	public void initialize(URL location, ResourceBundle res)
	{
		// menu_open_recent.getItems().addAll(new MenuItem("Project 1"), new MenuItem("Project 2"));
		fileOpenChooser = new DirectoryChooser();
		fileOpenChooser.setTitle(res.getString("file.openproject"));

		project_tree.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
			INode selected = newValue.getValue();
			if (selected instanceof IFileNode) {
				File source = ((IFileNode) selected).getFile();

				if (source.isFile()) {
					openSourceFile(source);
				}
			}
		});

		btn_console_clear.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/guifx/clear.png"))));

		// openProject(new File("D:/QC/trunk/other/fromTSDV/TestingForVNUProducts/Testing-R1/SampleSource"));
	}

	/**
	 * Send data from main entry point to this controller
	 * 
	 * @param primaryStage
	 *            main application stage
	 */
	public void initData(Stage primaryStage)
	{
		this.primaryStage = primaryStage;
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
			}

			@Override
			protected void succeeded()
			{
				project_tree.setRoot(getValue());
			}

			@Override
			protected ProjectNode call() throws Exception
			{
				CppLoaderConfig config = new CppLoaderConfig();
				config.setLogger(new BaseLogger() {

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
				loader.setLoaderConfig(config);

				config.getLogger().log(ILogger.INFO, "Loading project %s ...", root.getName());
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
	 */
	public void openSourceFile(File source)
	{
		try {
			source_view.openTab(source.getName(), source.getAbsolutePath(),
					SyntaxTextArea.class.getConstructor(File.class), source);
		} catch (Exception e) {
			e.printStackTrace();
		}
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

}
