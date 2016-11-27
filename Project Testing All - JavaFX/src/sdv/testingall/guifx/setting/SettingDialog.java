/**
 * Setting dialog
 * @file SettingDialog.java
 * @author (SDV)[phibao37]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.guifx.setting;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ListCell;
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

	private DialogPane	dialog;
	private Setting		setting;

	private @FXML ComboBox<Locale>	entry_language;
	private @FXML ComboBox<Charset>	entry_charset;

	/**
	 * Create new setting dialog instance
	 * 
	 * @param setting
	 *            setting data object
	 */
	public SettingDialog(Setting setting)
	{
		// Load resource
		this.setting = setting;
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
		setResizable(true);
		Stage dialogStage = (Stage) dialog.getScene().getWindow();
		dialogStage.getIcons().add(ImageSet.SETTING);

		// Set button control
		dialog.getButtonTypes().add(ButtonType.CLOSE);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources)
	{
		LinkedHashMap<Locale, String> countryMap = new LinkedHashMap<>();
		countryMap.put(Locale.ENGLISH, "English");
		countryMap.put(new Locale("vi"), "Tiếng Việt");
		entry_language.getItems().addAll(countryMap.keySet());
		entry_language.valueProperty().bindBidirectional(setting.APP_LOCALE);
		entry_language.setCellFactory(listView -> {
			return new ListCell<Locale>() {

				@Override
				protected void updateItem(Locale item, boolean empty)
				{
					super.updateItem(item, empty);
					if (item == null || empty) {
						setGraphic(null);
					} else {
						setText(countryMap.get(item));
					}
				}

			};
		});
		entry_language.setButtonCell(entry_language.getCellFactory().call(null));

		entry_charset.getItems().addAll(Charset.availableCharsets().values());
		entry_charset.valueProperty().bindBidirectional(setting.APP_CHARSET);
	}

}
