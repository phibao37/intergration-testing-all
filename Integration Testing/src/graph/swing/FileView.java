package graph.swing;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.IOException;

import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.DefaultCaret;

import core.Utils;

/**
 * Một canvas cuộn để hiển thị nội dung một tập tin
 */
public class FileView extends JScrollPane implements LightTabbedPane.EqualsConstruct {
	private static final long serialVersionUID = -5837930482904734094L;
	private File file;
	private JTextPane textPane;
	private static Font FONT = new Font("Consolas", Font.PLAIN, 12);
	
	/**
	 * Tạo một canvas cuộn từ tập tin tương ứng 
	 */
	public FileView(File file){
		this.file = file;
		setBorder(null);
		
		textPane = new JTextPane();
		textPane.setBorder(null);
		textPane.setBackground(Color.WHITE);
		textPane.setEditable(false);
		textPane.setFont(FONT);
		DefaultCaret caret = (DefaultCaret)textPane.getCaret();
		caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
		this.setViewportView(textPane);
		
		String content;
		try {
			content = Utils.getContentFile(file);
		} catch (IOException e) {
			content = e.getMessage();
		}
		textPane.setText(content);
	}

	public boolean equalsConstruct(Object... constructItem) {
		return file.equals(constructItem[0]);
	}
}