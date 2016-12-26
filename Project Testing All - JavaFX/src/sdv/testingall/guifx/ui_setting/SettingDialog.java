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
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.beans.property.IntegerProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import sdv.testingall.core.testreport.Coverage;
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

	private DialogPane		dialog;
	private Setting			setting;
	private ResourceBundle	appRes;
	private Stage			dialogStage;

	private @FXML ComboBox<Locale>	entry_language;
	private @FXML ComboBox<Charset>	entry_charset;
	private @FXML Spinner<Integer>	entry_graphic_maxrecent;
	private @FXML TextField			entry_cext;
	private @FXML TextField			entry_cppext;
	private @FXML CheckBox			entry_log_errdiv;
	private @FXML ListView<String>	entry_ccpp_includedir;
	private @FXML TextArea			entry_ccpp_marco;
	private @FXML CheckBox			entry_auto_viewsource;
	private @FXML Spinner<Integer>	entry_cpp_size_char;
	private @FXML Spinner<Integer>	entry_cpp_size_short;
	private @FXML Spinner<Integer>	entry_cpp_size_int;
	private @FXML Spinner<Integer>	entry_cpp_size_long;
	private @FXML Spinner<Integer>	entry_cpp_size_longlong;

	private @FXML CheckBox	cover_statement;
	private @FXML CheckBox	cover_branch;
	private @FXML CheckBox	cover_subcondition;
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
		dialogStage = (Stage) dialog.getScene().getWindow();
		dialogStage.getIcons().add(ImageSet.SETTING);

		// Set button control
		dialog.getButtonTypes().add(GUIUtil.BUTTON_CLOSE);
	}

	@Override
	public void initialize(URL location, ResourceBundle res)
	{
		appRes = res;

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

		IntegerSpinnerValueFactory valueFactory = new IntegerSpinnerValueFactory(2, 20,
				setting.RECENT_PROJECT_MAXSIZE.get());
		entry_graphic_maxrecent.setValueFactory(valueFactory);
		// valueFactory.valueProperty().bindBidirectional(setting.RECENT_PROJECT_MAXSIZE.asObject());
		entry_graphic_maxrecent.valueProperty().addListener((obv, oldValue, newValue) -> {
			setting.RECENT_PROJECT_MAXSIZE.set(newValue);
		});
		entry_auto_viewsource.selectedProperty().bindBidirectional(setting.TREE_AUTO_VIEWSOURCE);

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

		entry_ccpp_marco.setText(convertMarcoToString(setting.CPP_MARCO_MAP));
		entry_ccpp_marco.focusedProperty().addListener((obs, oldValue, newValue) -> {
			if (!newValue) {
				setting.CPP_MARCO_MAP.clear();
				setting.CPP_MARCO_MAP.putAll(convertStringToMapMarco(entry_ccpp_marco.getText()));
			}
		});

		cover_statement.setUserData(Coverage.STATEMENT);
		cover_branch.setUserData(Coverage.BRANCH);
		cover_subcondition.setUserData(Coverage.SUBCONDITION);
		EventHandler<ActionEvent> changeCoverage = event -> {
			CheckBox target = (CheckBox) event.getTarget();
			Coverage data = (Coverage) target.getUserData();
			if (target.isSelected()) {
				setting.GEN_COVERAGE_LIST.add(data);
			} else {
				if (setting.GEN_COVERAGE_LIST.size() == 1) {
					target.setSelected(true);
					GUIUtil.alert(AlertType.WARNING, setting.resString("gui.warning"), null,
							appRes.getString("cover.warn.atleastone"), ImageSet.SETTING);
				} else {
					setting.GEN_COVERAGE_LIST.remove(data);
				}
			}
		};
		CheckBox[] coverArray = new CheckBox[] { cover_statement, cover_branch, cover_subcondition };

		for (CheckBox cbCover : coverArray) {
			cbCover.setOnAction(changeCoverage);
			cbCover.setSelected(setting.GEN_COVERAGE_LIST.contains(cbCover.getUserData()));
		}

		List<Spinner<Integer>> cppSizeList = Arrays.asList(entry_cpp_size_char, entry_cpp_size_short,
				entry_cpp_size_int, entry_cpp_size_long, entry_cpp_size_longlong);
		IntegerProperty[] cppSizePropArray = new IntegerProperty[] { setting.CPP_SIZE_CHAR, setting.CPP_SIZE_SHORT,
				setting.CPP_SIZE_INT, setting.CPP_SIZE_LONG, setting.CPP_SIZE_LONGLONG };
		int iter = 0;
		for (Spinner<Integer> spinner : cppSizeList) {
			IntegerProperty prop = cppSizePropArray[iter++];
			IntegerSpinnerValueFactory sizevalueFactory = new IntegerSpinnerValueFactory(1, 32, prop.get());
			spinner.setValueFactory(sizevalueFactory);
			spinner.getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_HORIZONTAL);

			spinner.valueProperty().addListener((obv, oldValue, newValue) -> {
				prop.set(newValue);
				if (!setting.checkValidCppSize()) {
					sizevalueFactory.setValue(oldValue);
				}
			});
		}
	}

	protected static String convertMarcoToString(Map<String, String> marcoMap)
	{
		if (marcoMap.isEmpty()) {
			return "";
		}
		StringBuilder b = new StringBuilder();
		for (Map.Entry<String, String> entry : marcoMap.entrySet()) {
			b.append(entry.getKey());
			if (!entry.getValue().isEmpty()) {
				b.append(" = ").append(entry.getValue());
			}
			b.append('\n');
		}
		return b.toString();
	}

	protected static Map<String, String> convertStringToMapMarco(String text)
	{
		Map<String, String> marcoMap = new LinkedHashMap<>();
		for (String line : text.split("\n")) {
			line = line.trim();
			if (line.isEmpty()) {
				continue;
			}
			int index = line.indexOf('=');

			if (index == -1) {
				marcoMap.put(line, "");
			} else {
				String key = line.substring(0, index).trim();
				String val = line.substring(index + 1).trim();
				if (key.isEmpty()) {
					continue;
				}
				marcoMap.put(key, val);
			}
		}
		return marcoMap;
	}

	protected @FXML void handleIncludeDirAdd()
	{
		MultipleSelectionModel<String> models = entry_ccpp_includedir.getSelectionModel();
		cppHeaderChooser.setInitialDirectory(null);
		if (!models.isEmpty()) {
			File initial = new File(models.getSelectedItem());
			if (GUIUtil.isFolderReadable(initial)) {
				cppHeaderChooser.setInitialDirectory(initial);
			}
		}

		File dir = cppHeaderChooser.showDialog(dialogStage);
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

	protected @FXML void handleIncludeDirEdit()
	{
		MultipleSelectionModel<String> models = entry_ccpp_includedir.getSelectionModel();
		List<String> items = entry_ccpp_includedir.getItems();
		List<String> selectedItem = models.getSelectedItems();
		if (selectedItem.size() != 1) {
			return;
		}

		{
			File initial = new File(selectedItem.get(0));
			cppHeaderChooser.setInitialDirectory(GUIUtil.isFolderReadable(initial) ? initial : null);
		}
		File dir = cppHeaderChooser.showDialog(dialogStage);
		if (dir == null) {
			return;
		}

		String dirPath = dir.getAbsolutePath();
		int index = items.indexOf(selectedItem.get(0)), index2 = items.indexOf(dirPath);

		// New directory exist in current list
		if (index2 != -1) {
			if (index2 == index) {
				return;
			}
			items.remove(index2);
			if (index2 < index) {
				index--;
			}
		}

		items.set(index, dirPath);
	}

	protected @FXML void handleIncludeDirDelete()
	{
		List<String> selectedItems = entry_ccpp_includedir.getSelectionModel().getSelectedItems();
		List<String> items = entry_ccpp_includedir.getItems();
		if (selectedItems.isEmpty()) {
			return;
		}

		Optional<ButtonType> result = GUIUtil.alert(AlertType.CONFIRMATION, setting.resString("gui.confirmation"), null,
				appRes.getString("prompt.deleteheaderconfirm"), ImageSet.SETTING);
		if (result.isPresent() && result.get().getButtonData() == ButtonData.OK_DONE) {
			items.removeAll(selectedItems);
		}
	}

	protected @FXML void handleIncludeDirKeepValid()
	{
		setting.CPP_INCLUDE_DIR.removeIf(dir -> !GUIUtil.isFolderReadable(new File(dir)));
	}

}
