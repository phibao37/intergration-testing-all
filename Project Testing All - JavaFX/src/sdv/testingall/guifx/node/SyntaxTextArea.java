/**
 * Code syntax highlight view
 * @file SyntaxTextArea.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.guifx.node;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import sdv.testingall.guifx.node.LightTabPane.EqualsTabConstruct;

/**
 * Code syntax highlight view
 * 
 * @author VuSD
 *
 * @date 2016-11-24 VuSD created
 */
public class SyntaxTextArea extends VirtualizedScrollPane<CodeArea> implements EqualsTabConstruct {

	/**
	 * Define supported language to format
	 */
	public enum Language {

		/** C and C++ language */
		C_CPP(0),

		/** Java language */
		JAVA(1);

		Language(int code)
		{
			this.code = code;
		}

		int code;
	}

	private Language	lang	= Language.C_CPP;
	private File		sourceFile;
	private CodeArea	contentArea;

	/**
	 * Create new syntax view from file, default char-set will be used.<br/>
	 * Default language format is C/C++
	 * 
	 * @param source
	 *            source code file
	 */
	public SyntaxTextArea(File source)
	{
		super(createNewAreaView(source));
		sourceFile = source;
		contentArea = getContent();

		// Get the file content
		String content = null;
		try {
			content = FileUtils.readFileToString(source, Charset.defaultCharset());
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Register text change
		contentArea.richChanges().filter(ch -> !ch.getInserted().equals(ch.getRemoved())).subscribe(change -> {
			contentArea.setStyleSpans(0, computeHighlighting(contentArea.getText(), getLang()));
		});
		contentArea.replaceText(0, contentArea.getLength(), content);
		contentArea.moveTo(0);
	}

	/**
	 * Get the language to format
	 * 
	 * @return the language
	 */
	public Language getLang()
	{
		return lang;
	}

	/**
	 * Set the language to format
	 * 
	 * @param lang
	 *            the language
	 */
	public void setLang(Language lang)
	{
		contentArea.getStyleClass().remove(LANGS[this.lang.code]);
		this.lang = lang;

		contentArea.getStyleClass().add(LANGS[lang.code]);
		contentArea.setStyleSpans(0, computeHighlighting(contentArea.getText(), lang));
	}

	@Override
	public boolean equalsConstruct(Object... constructItem)
	{
		return sourceFile.equals(constructItem[0]);
	}

	/**
	 * Initialize new CodeArea object inside the scroll bar
	 * 
	 * @param source
	 *            source code file
	 * @return CodeArea object
	 */
	static CodeArea createNewAreaView(File source)
	{
		// Create object and line number format
		CodeArea codeArea = new CodeArea();
		codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea, digits -> "%" + digits + "d"));

		// Set other property
		codeArea.setEditable(false);
		codeArea.wrapTextProperty().set(true);
		codeArea.getStylesheets().add(SyntaxTextArea.class.getResource("SyntaxTextArea.css").toExternalForm());
		return codeArea;
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
	static StyleSpans<Collection<String>> computeHighlighting(String text, Language lang)
	{
		Matcher matcher = PATTERNS[lang.code].matcher(text);
		int lastKwEnd = 0;
		StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
		while (matcher.find()) {
			String styleClass = findStyle(matcher, TOKENS[lang.code]);
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

	static String[]		LANGS		= { "c_cpp", "java" };
	static Pattern[]	PATTERNS	= { C_CPP.PATTERN, JAVA.PATTERN };
	static String[][]	TOKENS		= {

			{ "keyword", "paren", "brace", "bracket", "semicolon", "string", "comment", "number" },			// C++
			{ "keyword", "paren", "brace", "bracket", "semicolon", "string", "comment", "number" }			// Java

	};

	static class C_CPP {
		private static final String[] KEYWORDS = new String[] { "alignas", "alignof", "and", "and_eq", "asm",
				"atomic_cancel", "atomic_commit", "atomic_noexcept", "auto", "bitand", "bitor", "bool", "break", "case",
				"catch", "char", "char16_t", "char32_t", "class", "compl", "concept", "const", "constexpr",
				"const_cast", "continue", "decltype", "default", "delete", "do", "double", "dynamic_cast", "else",
				"enum", "explicit", "export", "extern", "false", "float", "for", "friend", "goto", "if", "import",
				"inline", "int", "long", "module", "mutable", "namespace", "new", "noexcept", "not", "not_eq",
				"nullptr", "operator", "or", "or_eq", "private", "protected", "public", "register", "reinterpret_cast",
				"requires", "return", "short", "signed", "sizeof", "static", "static_assert", "static_cast", "struct",
				"switch", "synchronized", "template", "this", "thread_local", "throw", "true", "try", "typedef",
				"typeid", "typename", "union", "unsigned", "using", "virtual", "void", "volatile", "wchar_t", "while",
				"xor", "xor_eq" };

		private static final String	KEYWORD_PATTERN		= "\\b(" + String.join("|", KEYWORDS) + ")\\b";
		private static final String	PAREN_PATTERN		= "\\(|\\)";
		private static final String	BRACE_PATTERN		= "\\{|\\}";
		private static final String	BRACKET_PATTERN		= "\\[|\\]";
		private static final String	SEMICOLON_PATTERN	= "\\;";
		private static final String	STRING_PATTERN		= "\"([^\"\\\\]|\\\\.)*\"";
		private static final String	COMMENT_PATTERN		= "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/";
		private static final String	NUMBER_PATTERN		= "\\b[+-]?\\d+(\\.\\d*)?\\b";

		private static final Pattern PATTERN = Pattern.compile("(?<keyword>" + KEYWORD_PATTERN + ")" + "|(?<paren>"
				+ PAREN_PATTERN + ")" + "|(?<brace>" + BRACE_PATTERN + ")" + "|(?<bracket>" + BRACKET_PATTERN + ")"
				+ "|(?<semicolon>" + SEMICOLON_PATTERN + ")" + "|(?<string>" + STRING_PATTERN + ")" + "|(?<comment>"
				+ COMMENT_PATTERN + ")" + "|(?<number>" + NUMBER_PATTERN + ")");
	}

	static class JAVA {
		private static final String[] KEYWORDS = new String[] { "abstract", "assert", "boolean", "break", "byte",
				"case", "catch", "char", "class", "const", "continue", "default", "do", "double", "else", "enum",
				"extends", "final", "finally", "float", "for", "goto", "if", "implements", "import", "instanceof",
				"int", "interface", "long", "native", "new", "package", "private", "protected", "public", "return",
				"short", "static", "strictfp", "super", "switch", "synchronized", "this", "throw", "throws",
				"transient", "try", "void", "volatile", "while" };

		private static final String	KEYWORD_PATTERN		= "\\b(" + String.join("|", KEYWORDS) + ")\\b";
		private static final String	PAREN_PATTERN		= "\\(|\\)";
		private static final String	BRACE_PATTERN		= "\\{|\\}";
		private static final String	BRACKET_PATTERN		= "\\[|\\]";
		private static final String	SEMICOLON_PATTERN	= "\\;";
		private static final String	STRING_PATTERN		= "\"([^\"\\\\]|\\\\.)*\"";
		private static final String	COMMENT_PATTERN		= "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/";
		private static final String	NUMBER_PATTERN		= "\\d+(\\.\\d*)?";

		private static final Pattern PATTERN = Pattern.compile("(?<keyword>" + KEYWORD_PATTERN + ")" + "|(?<paren>"
				+ PAREN_PATTERN + ")" + "|(?<brace>" + BRACE_PATTERN + ")" + "|(?<bracket>" + BRACKET_PATTERN + ")"
				+ "|(?<semicolon>" + SEMICOLON_PATTERN + ")" + "|(?<string>" + STRING_PATTERN + ")" + "|(?<comment>"
				+ COMMENT_PATTERN + ")" + "|(?<number>" + NUMBER_PATTERN + ")");
	}
}
