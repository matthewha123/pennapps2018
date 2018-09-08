import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.apache.commons.io.FileUtils;

public class Gif {

	private ArrayList<BufferedImage> frames;
	private int k;

	// should be private --> TODO
	private Gif(ArrayList<BufferedImage> frames) {
		if (frames == null || frames.size() == 0) {
			throw new IllegalArgumentException("frames is null or 0");
		}

		this.frames = frames;
		restartGif();
	}

	// returns the number of frames in this gif
	public int getLength() {
		return frames.size();
	}

	// returns a specific frame
	public BufferedImage getFrame(int k) {
		if (k >= frames.size()) {
			throw new IllegalArgumentException("getFrame: bad frame index");
		}

		return frames.get(k);
	}

	public BufferedImage getNextFrame() {
		BufferedImage frame = frames.get(k);
		k++;
		k %= frames.size();
		return frame;
	}

	// used to restart the counter
	// for 'getNextFrame'
	public void restartGif() {
		k = 0;
	}

	public void drawNextFrame(Graphics g, int x, int y, int w, int h) {
		g.drawImage(frames.get(k), x, y, w, h, null);
		k++; k %= frames.size();
	}

	// create a gif with an array of img paths
	public static Gif createGif(ArrayList<String> paths) {
		if (paths == null || paths.size() == 0) {
			throw new IllegalArgumentException("createGif: bad paths");
		}

		ArrayList<BufferedImage> imgs = new ArrayList<>();

		try {
			for (String p : paths) {
				imgs.add(ImageIO.read(new File(p)));
			}
		} catch (IOException e) {
			System.out.println("createGif: IOException");
			System.exit(0);
		}

		return new Gif(imgs);
	}

	public static Gif createGif(URL url) {
		ArrayList<BufferedImage> frames = new ArrayList<>();

		try {
			ImageReader reader = ImageIO.getImageReadersByFormatName("gif").next();
			File input = new File("src/nerd.gif");

			//FileUtils.copyURLToFile(url, input);

			///ImageInputStream stream = ImageIO.createImageInputStream(input);
			ImageInputStream stream = ImageIO.createImageInputStream(input);
			reader.setInput(stream);

			int n = reader.getNumImages(true);
			for (int i = 0; i < n; i++) {
				frames.add(reader.read(i));
			}
		} catch (IOException ex) {
			System.out.println("IOException caught");
			System.exit(0);
		}

		return new Gif(frames);
	}
	
}
