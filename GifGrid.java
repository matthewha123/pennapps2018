import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Collections;
import java.util.LinkedList;

public class GifGrid {

	public static int GIF_SIZE; // n x n grid
	public static int FRAME_SIZE;
	public static int zoomTime = 5;
	
	private static int n;
	private int vn;

	private Gif[][] grid;
	private int[][][] avgRGB;
	public BufferedImage img;

	ListMap[][] listmaps;
	
	public int xOff, yOff;
	public int dxOff, dyOff;
	
	public LinkedList<Boolean> ne_count, sw_count;

	public GifGrid(BufferedImage img) {
		if (img == null) {
			throw new IllegalArgumentException("GifGrid: image null");
		}

		vn = n;
		xOff = 0;
		yOff = 0;
		ne_count = new LinkedList<>();
		sw_count = new LinkedList<>();
		
		this.img = getScaledImage(img, FRAME_SIZE, FRAME_SIZE);
		grid = new Gif[n][n];
		calculateAvgRGB();
		generateGifGrid();
	}
	
	public void offsetGrid(boolean ne, boolean sw) {
		ne_count.add(0, ne);
		sw_count.add(0, sw);
		
		int tx = 0;
		for (int i = 0; i < ne_count.size(); i++) {
			tx += ne_count.get(i) ? (FRAME_SIZE * Math.pow(2, i)) : 0;
		}
		
		int ty = 0;
		for (int i = 0; i < sw_count.size(); i++) {
			ty += sw_count.get(i) ? (FRAME_SIZE * Math.pow(2, i)) : 0;
		}
		
		dxOff = (tx - xOff) / zoomTime;
		dyOff = (ty - yOff) / zoomTime;
	}
	
	public void incZoom() {
		xOff += dxOff;
		yOff += dyOff;
	}

	// calculates the average RGB value for each tile
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

	// generates the gif grid with requests to the server
	private void generateGifGrid() {
		if (avgRGB == null) {
			calculateAvgRGB();
		}

		listmaps = new ListMap[n][n];
		String response = null;
		int r, g, b;
		
		for (int j = 0; j < n; j++) {
			for (int i = 0; i < n; i++) {
				r = avgRGB[j][i][0];
				g = avgRGB[j][i][1];
				b = avgRGB[j][i][2];
				
				if (MosiacComp.local) {
					response = HttpRequest.get("http://localhost:4000/api/nearest_gifs?rgb="+r+","+g+","+b+"&numgifs=10").body();
				} else {
					response = HttpRequest.get("http://35.229.42.2/api/nearest_gifs?rgb="+r+","+g+","+b+"&numgifs=10").body();
				}
				
				listmaps[j][i] = JsonArrayParser.parseBody(response);
			}
		}
		
		System.out.println("\nphase 1 complete\n");

		int x;
		String path;
		ListMap l;
		Gif gif;

		for (int j = 0; j < n; j++) {
			for (int i = 0; i < n; i++) {
				l = listmaps[j][i];
				x = (int) (Math.random() * l.list.size());
				path = l.map.get(l.list.get(x));
				Collections.sort(l.list);
				//path = l.map.get(l.list.get(0));
				//path = "src/repaircatgif.gif";
				
				if (Gif.pathMapContains(path)) {
					gif = Gif.getGifFromMap(path);
				} else {
					gif = Gif.createGif(new File("gifs/pennapps2018/"+path));	
					//gif = Gif.createGif(new File(path));
					Gif.storeGifInMap(path, gif);
				}

				grid[j][i] = gif;
			}
		}
	}

	// draws gif grid
	public void drawGifs(Graphics g) {
		BufferedImage b;
		for (int j = 0; j < n; j++) {
			for (int i = 0; i < n; i++) {
				if (grid[j][i] == null) {
					g.setColor(new Color(
							avgRGB[j][i][0],
							avgRGB[j][i][1],
							avgRGB[j][i][2]));
					g.fillRect(i*GIF_SIZE - xOff, j*GIF_SIZE - yOff, GIF_SIZE, GIF_SIZE);
				} else {
					b = grid[j][i].getNextFrame();
					//System.out.println("i - iOFF * GIFSIZE is " + (i-iOff)*GIF_SIZE + " j - jOFF * GIFSIZE is " + (j-jOff)*GIF_SIZE);
					g.drawImage(b, i*GIF_SIZE - xOff, j*GIF_SIZE - yOff, GIF_SIZE, GIF_SIZE, null);
				}
			}
		}
	}
	
	public void cycleAllGifs() {
		for (int j = 0; j < n; j++) {
			for (int i = 0; i < n; i++) {
				if (grid[j][i] != null) {
					grid[j][i].incGif();
				}
			}
		}
	}
	
	//scales the gif
	public void scale(Graphics g, double multiplier, int seconds) {
		BufferedImage b;
		for (int j = 0; j < n; j++) {
			for (int i = 0; i < n; i++) {
				if (grid[j][i] == null) {
					g.setColor(new Color(
							avgRGB[j][i][0],
							avgRGB[j][i][1],
							avgRGB[j][i][2]));
					g.fillRect(i*GIF_SIZE, j*GIF_SIZE, GIF_SIZE, GIF_SIZE);
				} else {
					b = grid[j][i].getNextFrame();
					g.drawImage(b, i*GIF_SIZE, j*GIF_SIZE, (int) (GIF_SIZE * Math.pow(multiplier, seconds)), (int) (GIF_SIZE * Math.pow(multiplier, seconds)), null);
				}
			}
		}
	}

	// draws the base image
	public void drawImg(Graphics g) {
		g.drawImage(img, 0, 0, null);
	}

	// draws the tiles
	public void drawAvgRGBGrid(Graphics g) { 
		for (int j = 0; j < n; j++) {
			for (int i = 0; i < n; i++) {
				int[] rgb = avgRGB[j][i];
				g.setColor(new Color(rgb[0], rgb[1], rgb[2]));
				g.fillRect(i*GIF_SIZE, j*GIF_SIZE, GIF_SIZE, GIF_SIZE);
			}
		}
	}

	// forces the img into a square
	private BufferedImage getScaledImage(Image srcImg, int w, int h){
		BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TRANSLUCENT);
		Graphics2D g2 = resizedImg.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2.drawImage(srcImg, 0, 0, w, h, null);
		g2.dispose();
		return resizedImg;
	}

	// sets the GifGrid's frame size and gif_size
	public static void setFrameRes(int frame_size, int gif_size) {
		if (frame_size % gif_size != 0) {
			throw new IllegalArgumentException("invalid gif size");
		}

		GifGrid.FRAME_SIZE = frame_size;
		GifGrid.GIF_SIZE = gif_size;
		GifGrid.n = frame_size / gif_size;
	}

	// returns the gif clicked
	// TODO: return an ordered pair of indices
	public Gif getGifClicked(int mouseX, int mouseY) {
		int i = mouseX / GIF_SIZE;
		int j = mouseY / GIF_SIZE;
		return grid[j][i];
	}

}
