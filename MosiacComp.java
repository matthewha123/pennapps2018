import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.Timer;

@SuppressWarnings("serial")
public class MosiacComp extends JComponent {
	
	final static boolean local = true;

	final static int FRAME_SIZE = 640;
	final int GIF_SIZE_init = 10;

	final int GIF_INTERVAL = 250;
	final int ZOOM_INTERVAL = 80;

	int GIF_SIZE;
	
	GifGrid grid;
	State state;
	Quad quad;
	
	Timer zoomTimer, gifTimer;

	int xt, yt;
	int zoomTime = 5;
	int z;
	
	public MosiacComp(BufferedImage img) {
		if (img == null) {
			throw new IllegalArgumentException("img is null");
		}

		setup(img);		

		zoomTimer = new Timer(ZOOM_INTERVAL, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				zoomTick();
			}
		});

		gifTimer = new Timer(GIF_INTERVAL, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				gifTick();
			}
		});

		gifTimer.start();

		addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (grid == null || state == State.ZOOM) {
					return;
				}
				
				switch(e.getKeyCode()) {
				case KeyEvent.VK_Q:
					zoomToQuadrant(Quad.NW);
					break;
				case KeyEvent.VK_W:
					zoomToQuadrant(Quad.NE);
					break;
				case KeyEvent.VK_A:
					zoomToQuadrant(Quad.SW);
					break;
				case KeyEvent.VK_S:
					zoomToQuadrant(Quad.SE);
					break;
				case KeyEvent.VK_R: // reset
					GIF_SIZE = GIF_SIZE_init;
					GifGrid.GIF_SIZE = GIF_SIZE;
					grid.xOff = 0;
					grid.yOff = 0;
					System.out.println("GRID RESET");
					break;
				case KeyEvent.VK_ESCAPE:
					System.exit(0);
				} 
			}					
		});

		addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (grid == null || state == State.ZOOM) {
					return;
				}
				
				System.out.println("~ click ~");
				System.out.println("iOff = " + grid.xOff + " jOff" + grid.yOff + " Gif size is " + GIF_SIZE);
				System.out.println(findQuadrant(e.getX(), e.getY()));
				zoomToQuadrant(findQuadrant(e.getX(), e.getY()));
			}
		});

		requestFocusInWindow();
	}

	public void setup(BufferedImage img) {
		if (img == null) {
			throw new IllegalStateException("setup: img is null");
		}
		
		GIF_SIZE = GIF_SIZE_init;
		GifGrid.setFrameRes(FRAME_SIZE, GIF_SIZE);
		grid = new GifGrid(img);
		
		state = State.WAIT;
		quad = Quad.NONE;
	}

	public void gifTick() {
		grid.cycleAllGifs();
		repaint();
	}

	public void zoomTick() {
		if (state != State.ZOOM || z == 0) {
			state = State.WAIT;
			quad = Quad.NONE;
			GIF_SIZE = GifGrid.GIF_SIZE;
			return;
		}
		
		z--;
		GifGrid.GIF_SIZE += GIF_SIZE / zoomTime;
		grid.incZoom();
		repaint();
	}

	public void zoomToQuadrant(Quad q) {
		if (GifGrid.GIF_SIZE >= FRAME_SIZE) {
			Gif g = grid.getGifClicked(grid.xOff+1, grid.yOff+1);
			
			if (g == null) {
				setup(grid.img);
			} else {
				setup(g.getFrame(0));
			}
			
			return;
		}
		
		state = State.ZOOM;
		quad = q;
		z = zoomTime;
		
		switch(quad) {
		case NE:
			grid.offsetGrid(true, false);
			break;
		case NW:
			grid.offsetGrid(false, false);
			break;
		case SE:
			grid.offsetGrid(true, true);
			break;
		case SW:
			grid.offsetGrid(false, true);
			break;
		}
		
		zoomTimer.start();
	}
	
	public Quad findQuadrant(int x, int y) {
		Quad q = null;
		
		if(x > (FRAME_SIZE / 2) && y > (FRAME_SIZE / 2)) {
			q = Quad.SE;
		} else if(x < (FRAME_SIZE / 2) && y > (FRAME_SIZE / 2)) {
			q = Quad.SW;
		} else if(x > (FRAME_SIZE / 2) && y < (FRAME_SIZE / 2)) {
			q = Quad.NE;
		} else if(x < (FRAME_SIZE / 2) && y < (FRAME_SIZE / 2)) {
			q = Quad.NW;
		} else {
			System.out.println("Couldn't find quad");
		}
		
		return q;
	}

	@Override
	public void paintComponent(Graphics g) {
		//super.paintComponent(g);
		grid.drawGifs(g);
		requestFocusInWindow();
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(FRAME_SIZE, FRAME_SIZE);
	}
}

enum State {
	WAIT, ZOOM;
}

enum Quad {
	NW, NE, SW, SE, NONE;
}

/*

public enum Quad{NW, NE, SW, SE};

    public Quad findQuad(int x, int y) {
    	Quad q = null;
    	if(x > (n / 2) && y > (n / 2)) {
    		q = Quad.SE;
    	}else if(x < (n / 2) && y > (n / 2)) {
    		q = Quad.SW;
    	}else if(x > (n / 2) && y < (n / 2)) {
    		q = Quad.NE;
    	}else if(x < (n / 2) && y < (n / 2)) {
    		q = Quad.NW;
    	}else {
    		System.out.println("Couldn't find quad");
    	}
    	return q;
    }

    public int[] moveGraphicCtx(Quad q) {
    	//returns the cell of the top left giph in the quad
    	int[] coor = new int[2];
    	//x coord is the first cell
    	switch(q) {
    	case SE: coor[0] = n / 2; coor[1] = n / 2; 
    		break;
		case NE: coor[0] = 0; coor[1] = n / 2;
			break;
		case NW: coor[0] = 0; coor[1] = 0;
			break;
		case SW: coor[0] = n / 2; coor[1] = 0;
			break;
		default:
			break;
    	}
    		return coor;
    } 



 */



