package graph.swing;

import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

import api.graph.IFileInfo;
import core.Utils;

/**
 * Một canvas cuộn để hiển thị nội dung một tập tin
 */
public class FileView extends JScrollPane implements LightTabbedPane.EqualsConstruct, ComponentListener {
	private static final long serialVersionUID = -5837930482904734094L;
	private File file;
	private JTextArea textPane;
	private static Font FONT = new Font("Consolas", Font.PLAIN, 12);

	private IFileInfo hl;

	/**
	 * Tạo một canvas cuộn từ tập tin tương ứng
	 */
	public FileView(File file) {
		this.file = file;
		setBorder(null);

		textPane = new JTextArea();
		textPane.setBorder(null);
		textPane.setBackground(Color.WHITE);
		textPane.setEditable(false);
		textPane.setFont(FONT);

		textPane.setWrapStyleWord(true);
		textPane.setLineWrap(true);

		DefaultCaret caret = (DefaultCaret) textPane.getCaret();
		caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
		this.setViewportView(textPane);

		String content;
		try {
			content = Utils.getContentFile(file);
		} catch (IOException e) {
			content = e.getMessage();
		}
		textPane.setText(content);

		textPane.addComponentListener(this);
	}

	public void setHightLight(IFileInfo info) {
		if (checkShowing())
			hightLightImidiately(info);
		else {
			hl = info;
		}
	}

	private boolean checkShowing() {
		return textPane.isShowing() && textPane.getSize().width > 0;
	}

	private void hightLightImidiately(IFileInfo loc) {
		int off = loc.getOffset(), len = loc.getLength();

		try {
			Rectangle viewRect = textPane.modelToView(off);
			int h = getViewport().getSize().height / 2;
			viewRect.y += h;

			textPane.requestFocusInWindow();
			textPane.scrollRectToVisible(viewRect);
			textPane.setCaretPosition(off);
			textPane.moveCaretPosition(off + len);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean equalsConstruct(Object... constructItem) {
		return file.equals(constructItem[0]);
	}

	@Override
	public void componentShown(ComponentEvent e) {
	}

	@Override
	public void componentResized(ComponentEvent e) {
		if (checkShowing() && hl != null) {
			hightLightImidiately(hl);
			hl = null;
		}
	}

	@Override
	public void componentMoved(ComponentEvent e) {
	}

	@Override
	public void componentHidden(ComponentEvent e) {
	}
}