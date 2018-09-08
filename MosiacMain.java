import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class MosiacMain implements Runnable {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new MosiacMain());
	}

	@Override
	public void run() {
		JFrame frame = new JFrame();
		MosiacComp comp = new MosiacComp();
		frame.add(comp);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

}
