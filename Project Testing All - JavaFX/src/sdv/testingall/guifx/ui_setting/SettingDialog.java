/**
 * Setting dialog
 * @file SettingDialog.java
 * @author (SDV)[phibao37]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.guifx.ui_setting;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Accordion;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import sdv.testingall.guifx.GUIUtil;
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
	private @FXML Spinner<Integer>	entry_graphic_maxrecent;
	private @FXML TextField			entry_cext;
	private @FXML TextField			entry_cppext;
	private @FXML CheckBox			entry_log_errdiv;
	private @FXML ListView<String>	entry_ccpp_includedir;

	private @FXML Label		toggle_restart;
	private @FXML Accordion	acc_cpp;

	private DirectoryChooser cppHeaderChooser;

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
		ResourceBundle mainRes = ResourceBundle.getBundle("sdv.testingall.guifx.ui_setting.SettingDialog", appLocale);
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
		dialog.getButtonTypes().add(GUIUtil.BUTTON_CLOSE);
	}

	@Override
	public void initialize(URL location, ResourceBundle res)
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
		toggle_restart.visibleProperty().bind(entry_language.valueProperty().isNotEqualTo(entry_language.getValue()));

		entry_charset.getItems().addAll(Charset.availableCharsets().values());
		entry_charset.valueProperty().bindBidirectional(setting.APP_CHARSET);

		IntegerSpinnerValueFactory valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(2, 20);
		entry_graphic_maxrecent.setValueFactory(valueFactory);
		valueFactory.valueProperty().bindBidirectional(setting.RECENT_PROJECT_MAXSIZE.asObject());

		acc_cpp.setExpandedPane(acc_cpp.getPanes().get(0));
		entry_cext.setText(String.join(" ", setting.CPP_CEXTENSION));
		entry_cppext.setText(String.join(" ", setting.CPP_CPPEXTENSION));
		entry_cext.textProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue == null || newValue.isEmpty()) {
				return;
			}
			setting.CPP_CEXTENSION.setAll(newValue.split(" "));
		});
		entry_cppext.textProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue == null || newValue.isEmpty()) {
				return;
			}
			setting.CPP_CPPEXTENSION.setAll(newValue.split(" "));
		});
		entry_log_errdiv.selectedProperty().bindBidirectional(setting.CPP_LOG_ERROR_DIRECTIVE);

		entry_ccpp_includedir.itemsProperty().bindBidirectional(setting.CPP_INCLUDE_DIR);
		entry_ccpp_includedir.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		cppHeaderChooser = new DirectoryChooser();
		cppHeaderChooser.setTitle(res.getString("browse.cppheader"));
	}

	protected @FXML void handleIncludeDirAdd()
	{
		File dir = cppHeaderChooser.showDialog(getOwner());
		if (dir == null) {
			return;
		}
		String dirPath = dir.getAbsolutePath();
		List<String> items = entry_ccpp_includedir.getItems();
		int index = items.indexOf(dirPath);

		if (index == -1) {
			index = items.size();
			items.add(dirPath);
		}
		entry_ccpp_includedir.getSelectionModel().clearAndSelect(index);
		entry_ccpp_includedir.scrollTo(index);
	}

}
