/**
 * Main JavaFX application
 * @file GUIMainFX.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.guifx;

import java.util.Locale;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sdv.testingall.guifx.main.MainView;

/**
 * Main JavaFX application
 * 
 * @author VuSD
 *
 * @date 2016-11-22 VuSD created
 */
public class GUIMainFX extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception
	{
		Setting setting = Setting.loadSetting();
		Locale appLocale = setting.APP_LOCALE.get();
		Locale.setDefault(appLocale);
		GUIUtil.setupGUIUtil(setting);
		ResourceBundle mainRes = ResourceBundle.getBundle("sdv.testingall.guifx.main.MainView", appLocale);

		// Load node hierarchy
		FXMLLoader loader = new FXMLLoader(getClass().getResource("main/MainView.fxml"), mainRes);
		Parent root = loader.load();
		Scene scene = new Scene(root);
		MainView controller = loader.getController();
		controller.initData(primaryStage, setting);

		// Display main application
		primaryStage.setTitle(mainRes.getString("app.title"));
		primaryStage.getIcons().add(ImageSet.APPLICATION);
		primaryStage.setScene(scene);
		primaryStage.setMaximized(true);
		primaryStage.show();
	}

	/**
	 * Main launch entry point
	 * 
	 * @param args
	 *            optional arguments
	 */
	public static void main(String[] args)
	{
		launch(args);
	}

}
