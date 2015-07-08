package core.graph;

import java.awt.AlphaComposite;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.Timer;

/**
 * Lớp đồ họa giúp hiển thị các nhãn có độ trong suốt
 * @author ducvu
 *
 */
public class LightLabel extends JLabel implements ActionListener  {
	private static final long serialVersionUID = -415417527452755579L;
	
	private static final int DELAY = 25;
	private static final int minOpacity = 100;
	private static final int maxOpacity = 500;
	private static final int baseStep = 25;
	public static final float fraction = 0.001f;
	
	private Timer timer;
	private int opacity;
	private int step;
	
	/**
	 * Tạo một nhãn mới với độ trong suốt tối thiểu
	 */
	public LightLabel() {
		super();
		setOpaque(false);
		timer = new Timer(DELAY, this);
		timer.setInitialDelay(0);
		
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				step = baseStep;
				if (!timer.isRunning())
					timer.start();
			}
			@Override
			public void mouseExited(MouseEvent e) {
				step = -baseStep;
				if (!timer.isRunning())
					timer.start();
			}
		});
		
		setOpacity(minOpacity);
	}
	
	/**
	 * Thiết đặt độ trong suốt cho nhãn
	 * @param opacity độ trong suốt, nằm trong khoảng 
	 * {@link #minOpacity} và {@link #maxOpacity}
	 */
	public void setOpacity(int opacity){
		this.opacity = opacity;
		this.repaint();
	}
	
	@Override
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setComposite(AlphaComposite.getInstance(
				AlphaComposite.SRC_OVER, opacity*fraction));
		super.paint(g2);
		g2.dispose();
	}
	
	public void actionPerformed(ActionEvent e) {
		int tmp = opacity + step;

		if (tmp < minOpacity || tmp > maxOpacity){
			timer.stop();
			return;
		}
		this.setOpacity(tmp);
	}
}
