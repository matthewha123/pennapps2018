import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

public class Gif {

	private static HashMap<String, Gif> pathMap = new HashMap<>();
	
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
		incGif();
		return frame;
	}

	// used to restart the counter
	// for 'getNextFrame'
	public void restartGif() {
		k = 0;
	}
	
	// increment the img index
	public void incGif() {
		k++;
		k %= frames.size();
	}

	// note: will not draw if off frame
	public void drawFrame(Graphics2D g, int x, int y, int w, int h) {
		if (x + w < 0 || y + h < 0 || x > GifGrid.FRAME_SIZE || y > GifGrid.FRAME_SIZE) {
			return;
		}
		
		g.drawImage(frames.get(k), x, y, w, h, null);
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

	public static Gif createGif(File input) {
		ArrayList<BufferedImage> frames = new ArrayList<>();
		
		ArrayList<String> paths = new ArrayList<>();
		paths.add("src/ball_1.png");
		paths.add("src/ball_2.png");
		paths.add("src/ball_3.png");
		paths.add("src/ball_4.png");

		int n = 0;
		try {
			ImageReader reader = ImageIO.getImageReadersByFormatName("gif").next();
			ImageInputStream stream = ImageIO.createImageInputStream(input);
			reader.setInput(stream, false);

			n = reader.getNumImages(true);
			for (int i = 0; i < Math.min(n, 10); i++) {
				frames.add(reader.read(i));
			}
		} catch (IOException ex) {
			//System.out.println("IOException caught");
			System.exit(0);
		} catch (ArrayIndexOutOfBoundsException e) {
			//System.out.println("OutOfBounds: "+ input.getPath());
		}

		if (frames.size() == 0) {
			return null;
		}
		
		return new Gif(frames);
	}
	
	public static boolean pathMapContains(String path) {
		return pathMap.containsKey(path);
	}
	
	public static Gif getGifFromMap(String path) {
		return pathMap.get(path);
	}
	
	public static void storeGifInMap(String path, Gif g) {
		pathMap.put(path, g);
	}
}
