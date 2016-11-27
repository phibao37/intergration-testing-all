/**
 * Setting dialog
 * @file SettingDialog.java
 * @author (SDV)[phibao37]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.guifx.setting;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.stage.Stage;
import sdv.testingall.guifx.ImageSet;
import sdv.testingall.guifx.Setting;

/**
 * Setting dialog
 * 
 * @author phibao37
 *
 * @date 2016-11-27 phibao37 created
 */
public class SettingDialog extends Dialog<ButtonType> implements Initializable {

	private DialogPane dialog;

	/**
	 * Create new setting dialog instance
	 * 
	 * @param setting
	 *            setting data object
	 */
	public SettingDialog(Setting setting)
	{
		// Load resource
		Locale appLocale = setting.APP_LOCALE.get();
		ResourceBundle mainRes = ResourceBundle.getBundle("sdv.testingall.guifx.setting.SettingDialog", appLocale);
		FXMLLoader loader = new FXMLLoader(getClass().getResource("SettingDialog.fxml"), mainRes);
		loader.setController(this);
		try {
			dialog = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Set dialog
		setDialogPane(dialog);
		setTitle(mainRes.getString("app.title"));
		((Stage) dialog.getScene().getWindow()).getIcons().add(ImageSet.SETTING);

		// Set button control
		dialog.getButtonTypes().add(ButtonType.CLOSE);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources)
	{
		//
	}
}
