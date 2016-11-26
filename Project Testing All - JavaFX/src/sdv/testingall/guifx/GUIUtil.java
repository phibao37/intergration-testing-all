/**
 * Utility for graphic interface
 * @file GUIUtil.java
 * @author (SDV)[phibao37]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.guifx;

import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
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
		return alert.showAndWait();
	}
}
