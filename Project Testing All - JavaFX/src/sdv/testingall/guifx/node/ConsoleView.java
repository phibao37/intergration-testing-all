/**
 * Console area view
 * @file ConsoleView.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.guifx.node;

import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.StyleClassedTextArea;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

/**
 * Console area view
 * 
 * @author VuSD
 *
 * @date 2016-11-25 VuSD created
 */
public class ConsoleView extends VirtualizedScrollPane<StyleClassedTextArea> {

	private StyleClassedTextArea consoleView;

	/**
	 * Create new console view
	 */
	public ConsoleView()
	{
		super(new StyleClassedTextArea());
		consoleView = getContent();
		getStyleClass().add("console-area");
		consoleView.getStyleClass().add("content");

		consoleView.setEditable(false);
		consoleView.wrapTextProperty().set(true);

		consoleView.richChanges().filter(ch -> !ch.getInserted().equals(ch.getRemoved())).subscribe(change -> {
			consoleView.setStyleSpans(0, computeHighlighting(consoleView.getText()));
		});
	}

	/**
	 * Append new text to the console
	 * 
	 * @param text
	 *            string to be append
	 */
	public void appendText(String text)
	{
		consoleView.appendText(text);
	}

	/**
	 * Clear all text inside the console
	 */
	public void clear()
	{
		consoleView.clear();
	}

	/**
	 * Create style format from content and pattern
	 * 
	 * @param text
	 *            source content
	 * @param pattern
	 *            pattern to detect language token
	 * @return styled format
	 */
	static StyleSpans<Collection<String>> computeHighlighting(String text)
	{
		Matcher matcher = PATTERNS.matcher(text);
		int lastKwEnd = 0;
		StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
		while (matcher.find()) {
			String styleClass = findStyle(matcher, TOKENS);
			/* never happens */ assert styleClass != null;
			spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
			spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
			lastKwEnd = matcher.end();
		}
		spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
		return spansBuilder.create();
	}

	/**
	 * Find the corresponding class name from detected matching group
	 * 
	 * @param match
	 *            Regex matcher object
	 * @param groups
	 *            list of class name to look in
	 * @return found class name
	 */
	static String findStyle(Matcher match, String[] groups)
	{
		for (String group : groups) {
			if (match.group(group) != null) {
				return group;
			}
		}
		return null;
	}

	/*----------------------------------------------------------------------------------------------------*/

	static Pattern	PATTERNS	= Pattern
			.compile("(?<error>\\[ERROR\\])" + "|(?<warning>\\[WARNING\\])" + "|(?<info>\\[INFO\\])");
	static String[]	TOKENS		= { "error", "warning", "info" };

}
