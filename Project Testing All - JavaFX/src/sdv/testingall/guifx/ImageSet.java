/**
 * Set of image used frequently in application
 * @file ImageSet.java
 * @author (SDV)[phibao37]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.guifx;

import javafx.scene.image.Image;

/**
 * Set of image used frequently in application
 * 
 * @author phibao37
 *
 * @date 2016-11-26 phibao37 created
 */
public class ImageSet {

	/** Application icon */
	public static final Image APPLICATION = new Image(ImageSet.class.getResourceAsStream("/guifx/application.png"));

	/** Clear icon */
	public static final Image CLEAR = new Image(ImageSet.class.getResourceAsStream("/guifx/clear.png"));

	/** Setting icon */
	public static final Image SETTING = new Image(ImageSet.class.getResourceAsStream("/guifx/setting.png"));

	/** Menu CFG icon */
	public static final Image MENU_CFG = new Image(ImageSet.class.getResourceAsStream("/guifx/menu.cfg.png"));

	/** Menu generate test icon */
	public static final Image MENU_GENTEST = new Image(ImageSet.class.getResourceAsStream("/guifx/menu.gentest.png"));

	/** Menu view source icon */
	public static final Image MENU_VIEWSOURCE = new Image(
			ImageSet.class.getResourceAsStream("/guifx/menu.viewsource.png"));

	/** Menu open project folder icon */
	public static final Image MENU_PR_FOLDER = new Image(
			ImageSet.class.getResourceAsStream("/guifx/menu.project.folder.png"));

	/** Menu open project configuration icon */
	public static final Image MENU_PR_CONFIG = new Image(
			ImageSet.class.getResourceAsStream("/guifx/menu.project.config.png"));

}
