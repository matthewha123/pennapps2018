import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

public class GifGrid {

	private static int GIF_SIZE; // n x n grid
	private static int FRAME_SIZE;
	private static int n;

	private Gif[][] grid;
	private int[][][] avgRGB;
	private BufferedImage img;

	public GifGrid(BufferedImage img) {
		if (img == null) {
			throw new IllegalArgumentException("GifGrid: image null");
		}

		this.img = getScaledImage(img, FRAME_SIZE, FRAME_SIZE);
		grid = new Gif[n][n];
		calculateAvgRGB();
	}

	private BufferedImage getScaledImage(Image srcImg, int w, int h){
		BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TRANSLUCENT);
		Graphics2D g2 = resizedImg.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2.drawImage(srcImg, 0, 0, w, h, null);
		g2.dispose();
		return resizedImg;
	}

	public void calculateAvgRGB() {
		avgRGB = new int[n][n][3];

		for (int j = 0; j < n; j++) {
			for (int i = 0; i < n; i++) {

				int RGB = 0;
				int r = 0;
				int g = 0;
				int b = 0;

				for (int y = 0; y < GIF_SIZE; y++) {
					for (int x = 0; x < GIF_SIZE; x++) {
						RGB = img.getRGB(i*GIF_SIZE + x, j*GIF_SIZE + y);		
						r += (RGB >> 16) & 0xFF;
						g += (RGB >> 8) & 0xFF;
						b += (RGB & 0xFF);
					}
				}

				avgRGB[j][i][0] = r / (GIF_SIZE * GIF_SIZE);
				avgRGB[j][i][1] = g / (GIF_SIZE * GIF_SIZE);
				avgRGB[j][i][2] = b / (GIF_SIZE * GIF_SIZE);
			}
		}
	}

	/*
	 * 
	 * int rgb = red;
rgb = (rgb << 8) + green;
rgb = (rgb << 8) + blue;
Also, I believe you can get the individual values using:*/
	
	public void drawImg(Graphics g) {
		g.drawImage(img, 0, 0, null);
	}
	
	public void drawAvgRGBGrid(Graphics g) {
		for (int j = 0; j < n; j++) {
			for (int i = 0; i < n; i++) {
				int[] rgb = avgRGB[j][i];
				g.setColor(new Color(rgb[0], rgb[1], rgb[2]));
				g.fillRect(i*GIF_SIZE, j*GIF_SIZE, GIF_SIZE, GIF_SIZE);
			}
		}
	}

	public static void setFrameRes(int frame_size, int gif_size) {
		if (frame_size % gif_size != 0) {
			throw new IllegalArgumentException("invalid gif size");
		}

		GifGrid.FRAME_SIZE = frame_size;
		GifGrid.GIF_SIZE = gif_size;
		GifGrid.n = frame_size / gif_size;
	}
	
	public Gif determineGif(int mouseX, int mouseY) {
		int i = mouseX / GIF_SIZE;
		int j = mouseY / GIF_SIZE;
		return grid[j][i];
	}
	

	/*
	 * take 1st frame
	 * create gifgrid
	 * split up into grid
	 * average rgb of each sq
	 * retrieve closest gif for each sq
	 * draw
	 * 
	 */

}
