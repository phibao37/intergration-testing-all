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
import javafx.scene.image.Image;
import javafx.stage.Stage;

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
		// Default locale to EN. Internationalization can be extended
		Locale appLocale = Locale.ENGLISH;
		ResourceBundle appRes = ResourceBundle.getBundle("sdv.testingall.guifx.main.MainView", appLocale);

		// Load node hierarchy
		Parent root = FXMLLoader.load(getClass().getResource("main/MainView.fxml"), appRes);
		Scene scene = new Scene(root);

		// Display main application
		primaryStage.setTitle(appRes.getString("app.title"));
		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/guifx/application.png")));
		primaryStage.setScene(scene);
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
