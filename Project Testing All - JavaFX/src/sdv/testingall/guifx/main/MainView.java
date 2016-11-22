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

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Menu;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

/**
 * Controller for MainView
 * 
 * @author VuSD
 *
 * @date 2016-11-22 VuSD created
 */
public class MainView implements Initializable {

	private @FXML Menu menu_open_recent;

	private Stage				primaryStage;
	private DirectoryChooser	fileOpenChooser;

	@Override
	public void initialize(URL location, ResourceBundle res)
	{
		// menu_open_recent.getItems().addAll(new MenuItem("Project 1"), new MenuItem("Project 2"));
		fileOpenChooser = new DirectoryChooser();
		fileOpenChooser.setTitle(res.getString("file.openproject"));
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

	protected void openProject(File root)
	{
		System.out.printf("Opening %s\n", root);
		//
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

}
