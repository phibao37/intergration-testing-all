/**
 * Utility for graphic interface
 * @file GUIUtil.java
 * @author (SDV)[phibao37]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.guifx;

import java.io.File;
import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Utility for graphic interface
 * 
 * @author phibao37
 *
 * @date 2016-11-26 phibao37 created
 */
public class GUIUtil {

	/** Redefine "Apply" button with localization */
	public static ButtonType BUTTON_APPLY = ButtonType.APPLY;

	/** Redefine "OK" button with localization */
	public static ButtonType BUTTON_OK = ButtonType.OK;

	/** Redefine "Cancel" button with localization */
	public static ButtonType BUTTON_CANCEL = ButtonType.CANCEL;

	/** Redefine "Close" button with localization */
	public static ButtonType BUTTON_CLOSE = ButtonType.CLOSE;

	/** Redefine "Yes" button with localization */
	public static ButtonType BUTTON_YES = ButtonType.YES;

	/** Redefine "No" button with localization */
	public static ButtonType BUTTON_NO = ButtonType.NO;

	/** Redefine "Finish" button with localization */
	public static ButtonType BUTTON_FINISH = ButtonType.FINISH;

	/** Redefine "Next" button with localization */
	public static ButtonType BUTTON_NEXT = ButtonType.NEXT;

	/** Redefine "Previous" button with localization */
	public static ButtonType BUTTON_PREVIOUS = ButtonType.PREVIOUS;

	/**
	 * Set up static field, data based on setting
	 * 
	 * @param setting
	 *            setting data
	 */
	static void setupGUIUtil(Setting setting)
	{
		BUTTON_APPLY = new ButtonType(setting.resString("btn.apply"), ButtonData.APPLY);
		BUTTON_OK = new ButtonType(setting.resString("btn.ok"), ButtonData.OK_DONE);
		BUTTON_CANCEL = new ButtonType(setting.resString("btn.cancel"), ButtonData.CANCEL_CLOSE);
		BUTTON_CLOSE = new ButtonType(setting.resString("btn.close"), ButtonData.CANCEL_CLOSE);
		BUTTON_YES = new ButtonType(setting.resString("btn.yes"), ButtonData.YES);
		BUTTON_NO = new ButtonType(setting.resString("btn.no"), ButtonData.NO);
		BUTTON_FINISH = new ButtonType(setting.resString("btn.finish"), ButtonData.FINISH);
		BUTTON_NEXT = new ButtonType(setting.resString("btn.next"), ButtonData.NEXT_FORWARD);
		BUTTON_PREVIOUS = new ButtonType(setting.resString("btn.previous"), ButtonData.BACK_PREVIOUS);
	}

	/**
	 * Create new alert box and show in current windows
	 * 
	 * @param type
	 *            alert box type
	 * @param title
	 *            windows title
	 * @param header
	 *            header text
	 * @param content
	 *            content text
	 * @param icon
	 *            windows icon
	 * @return button that have been choose
	 */
	public static Optional<ButtonType> alert(AlertType type, String title, String header, String content, Image icon)
	{
		Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);
		if (icon != null) {
			((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(icon);
		}

		switch (type) {
		case CONFIRMATION:
			alert.getButtonTypes().setAll(BUTTON_OK, BUTTON_CANCEL);
			break;
		case NONE:
			break;
		default:
			alert.getButtonTypes().setAll(BUTTON_OK);
		}

		return alert.showAndWait();
	}

	/**
	 * Check if a folder is readable, which include: <code>folder != null</code>, existed, is folder, readable
	 * 
	 * @param folder
	 *            folder object to check
	 * @return readable state
	 */
	public static boolean isFolderReadable(File folder)
	{
		return folder != null && folder.isDirectory() && folder.canRead();
	}
}
