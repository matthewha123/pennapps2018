import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class MosiacMain implements Runnable {

	public static void main(String[] args) {
		
		SwingUtilities.invokeLater(new MosiacMain());
	}

	@Override
	public void run() {
		
		BufferedImage img = null;
		
		try {
			img = ImageIO.read(new File("src/charles.jpg"));
		} catch (IOException e) { }
		
		JFrame frame = new JFrame();
		MosiacComp comp = new MosiacComp(img);
		frame.add(comp);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

}
