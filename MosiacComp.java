import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.Timer;

@SuppressWarnings("serial")
public class MosiacComp extends JComponent {

	final int FRAME_SIZE = 600;
	final int INTERVAL = 5;
	final int GIF_SIZE = 60;
	
	GifGrid grid;
	
	public MosiacComp() {
		setup();		
		
		Timer tickTimer = new Timer(INTERVAL, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tick();
			}
		});
		tickTimer.start();
		
		addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					System.exit(0);
				} 
			}					
		});
		
		addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				System.out.println("~ click ~");
				grid.determineGif(e.getX(), e.getY());
				// e.getX(); e.getY();
			}
		});
		
		requestFocusInWindow();
	}
	
	public void setup() {
		GifGrid.setFrameRes(FRAME_SIZE, GIF_SIZE);
		BufferedImage img = null;
		
		try {
			img = ImageIO.read(new File("src/charles.jpg"));
		} catch (IOException e) { }
		
		grid = new GifGrid(img);
	}
	
	public void tick() {
		repaint();
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		grid.drawImg(g);
		grid.drawAvgRGBGrid(g);
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(FRAME_SIZE, FRAME_SIZE);
	}
	
}
