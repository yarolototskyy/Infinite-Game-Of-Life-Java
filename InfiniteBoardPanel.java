import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;

/*
 * A class that extends the JPanel class, adding the functionality
 * of painting the current generation of a Game of Life.
 */
public class InfiniteBoardPanel extends JPanel{
	/*
	 *Infinite Board Panel that only shows a part of the game at a time,
	 *based on a set of bounds. Responds to zoom and pan events.
	 *
	 *Main thing to note is that the stored bounds are not necessarily the bounds
	 *of the window. Showing just the bounds would cause the panel size to jump around too
	 *much when zooming in or out. To counter-act this I just add some extra cells
	 *to each side so that the board panel is always filled to the edges.
	 *
	 */
	
	private InfiniteGameOfLife game;
	private int[] bounds;
	private boolean canZoomOut = true;
	
	public InfiniteBoardPanel(InfiniteGameOfLife g){
		game = g;
		
		//Default bounds upon launch
		bounds = new int[] {-20, 20, -20, 20};
	}
	
	public void setBoundaries(int[] bounds) {
		/* 
		 * Updates boundaries to display
		 * 
		 **/
		this.bounds = bounds;
	}

	public int[] getBoundaries() {
		/* 
		 * Returns current boundaries (without the filler cells)
		 * 
		 **/
		return bounds;
	}
	
	public int[] getSeenBoundaries() {
		/* 
		 * Returns current seen window (with the filler cells)
		 * 
		 **/
		
		int numSquaresX = bounds[1] - bounds[0] + 1;
		int numSquaresY = bounds[3] - bounds[2] + 1;
		int panelWidth = getWidth();
		int panelHeight = getHeight();
		int squareSideX = panelWidth / numSquaresX;
		int squareSideY = panelHeight / numSquaresY;
		int BoundedWindowWidth = numSquaresX * squareSideX; 
		int BoundedWindowHeight = numSquaresY * squareSideY; 
		int numSquaresLeftOverX = (panelWidth - BoundedWindowWidth) / squareSideX;
		int numSquaresLeftOverY = (panelHeight - BoundedWindowHeight) / squareSideY;
		return new int[] {bounds[0] - numSquaresLeftOverX / 2, bounds[1] + numSquaresLeftOverX / 2, bounds[2] - numSquaresLeftOverY / 2, bounds[3] + numSquaresLeftOverY / 2};
	}
	
	public boolean canZoomOut() {
		/* 
		 * Returns false if dimensions of each cell are too small to zoom out
		 * 
		 **/

		return canZoomOut;
	}
	/**
	 * Paints the current state of the Game of Life board onto
	 * this panel. This method is invoked for you each time you
	 * call repaint() on either this object or on the JFrame upon
	 * which this panel is placed.
	 */
	public void paintComponent(Graphics g){
		Graphics2D g2 = (Graphics2D)g;
		// TODO this is where you need to draw the current state of the board
		int numSquaresX = bounds[1] - bounds[0] + 1;
		int numSquaresY = bounds[3] - bounds[2] + 1;
		int panelWidth = getWidth();
		int panelHeight = getHeight();
		int squareSideX = panelWidth / numSquaresX;
		int squareSideY = panelHeight / numSquaresY;
		if (canZoomOut == false && squareSideX > 2 && squareSideY > 2) {
			canZoomOut = true;
		}
		if (squareSideX < 2) {
			squareSideX = 2;
			canZoomOut = false;
		}
		if (squareSideY < 2) {
			squareSideY = 2;
			canZoomOut = false;
		}
		int BoundedWindowWidth = numSquaresX * squareSideX; 
		int BoundedWindowHeight = numSquaresY * squareSideY; 
		int numSquaresLeftOverX = (panelWidth - BoundedWindowWidth) / squareSideX;
		int numSquaresLeftOverY = (panelHeight - BoundedWindowHeight) / squareSideY;
		for (int i = bounds[2] - numSquaresLeftOverY / 2; i < bounds[3] + numSquaresLeftOverY / 2; i++) {
			for (int j = bounds[0] - numSquaresLeftOverX / 2; j < bounds[1]  + numSquaresLeftOverX / 2; j++) {
				if (game.isAlive(j, i)) {
					g2.setColor(Color.WHITE);
				}
				else {
					g2.setColor(Color.BLACK);
				}
				g2.fillRect((j - bounds[0] + numSquaresLeftOverX / 2) * squareSideX, (i - bounds[2] + numSquaresLeftOverY / 2) * squareSideY, squareSideX, squareSideY);
				g2.setColor(Color.BLACK);
				g2.drawRect((j - bounds[0] + numSquaresLeftOverX / 2) * squareSideX, (i - bounds[2] + numSquaresLeftOverY / 2) * squareSideY, squareSideX, squareSideY);
				
			}
		}
	}
}
