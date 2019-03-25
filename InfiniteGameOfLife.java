import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class InfiniteGameOfLife {
	/* 
	 * Infinite Game of Life Engine
	 * 
	 * My implementation of this is pretty simple. I use a hashmap to 
	 * store all alive cells. It maps a coordinate (separate class I made) 
	 * to an Integer that represents state. Obviously the only possible state is ALIVE (1).
	 * If I access the hashmap for a coordinate that is alive, it returns 1. If I access it
	 * for a coordinate that's dead, it returns null because the coordinate is not in it.
	 * 
	 * Many of the methods are the same as in GameOfLife, however, there is one major difference
	 * in the way I use countLivingNeighbors(). 
	 * In my nextGen(), I loop through each of my alive cells. However, I can't simply just count
	 * neighbors for each alive cell. I also have to check if the dead neighbors can
	 * be revived. This is crucial because I am significantly reducing the amount of dead cells I check versus
	 * just looping through all of the dead cells. This also ties the complexity of my program to 
	 * the number of alive cells rather than the dimensions, which can get very big and full of empty space. 
	 * For each of the dead cells I then use the original countLivingNeighbors().
	 * 
	 * I also have willBeAlive() broken up into two, aliveToDead() and deadToAlive() since I am now checking the
	 * dead and alive cells separately.
	 * 
	 * 
	 **/

	private HashMap<Coordinate, Integer> board;
	private int ALIVE = 1;
	private int gen;
	private static final int LONELINESS_UPPER_THRESHOLD = 1;
	private static final int BIRTH_VALUE = 3;
	private static final int OVERCROWDING_LOWER_THRESHOLD = 4;
	private static final int[][] NEIGHBORS = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}, {-1, -1}, {1, 1}, {1, -1}, {-1, 1}};

	
	public String toString() {
		/* 
		 * Returns a string representation of the board
		 *  
		 **/

		int[] bounds = getBounds();
		String toReturn = "";
		for (int y = bounds[2]; y <= bounds[3]; y++) {
			for (int x = bounds[0]; x <= bounds[1]; x++) {
				if (board.get(new Coordinate(x, y)) != null) {
					toReturn += "1 ";
				}
				else {
					toReturn += "0 ";
				}
			}
			toReturn += "\n";
		}
		return toReturn;
	}
	
	public InfiniteGameOfLife() {
		/* 
		 *
		 * Constructs a new Infinite Game of Life
		 * 
		 **/
		
		board = new HashMap<Coordinate, Integer>();	
		gen = 0;
	}
	
	public void addAliveCell(int x, int y) {
		/* 
		 * Adds a new Alive Cell at given coordinates
		 * 
		 **/

		board.put(new Coordinate(x, y), ALIVE);
	}
	
	public void clear() {
		/* 
		 *
		 * Clears the board
		 * 
		 **/

		board = new HashMap<Coordinate, Integer>();
		this.gen = 0;
	}

	private int[] getBounds() {
		/* 		 
		 * Returns bounds for the smallest window that shows all alive cells
		 * Used in toString()
		 * 
		 **/

		int largestY = 0;
		int largestX = 0;
		int smallestY = 0;
		int smallestX = 0;
		for (Coordinate c : board.keySet()) {
			if (c.getY() > largestY) {
				largestY = c.getY();
			}
			if (c.getX() > largestX) {
				largestX = c.getX();
			}
			if (c.getY() < smallestY) {
				smallestY = c.getY();
			}
			if (c.getX() < smallestX) {
				smallestX = c.getX();
			}
		}
		
		return new int[] {smallestX, largestX, smallestY, largestY};
	}
		
	
	public void removeAliveCell(int x, int y) {
		/* 
		 * Removes alive cell at given location (if already dead does nothing)
		 * 
		 **/

		board.remove(new Coordinate(x, y));
	}
	
	public boolean isAlive(int x, int y) {
		/* 
		 * Returns true if cell is alive at given location
		 * 
		 **/
		return (board.get(new Coordinate(x, y)) != null);
	}
	
	public void randomSetup(int[] bounds) {
		/* 
		 * Randomizes a section of the board defined by inputed bounds
		 * 
		 **/

		int xLow = bounds[0];
		int xHigh = bounds[1];
		int yLow = bounds[2];
		int yHigh = bounds[3];
		for (int i = yLow; i <= yHigh; i++) {
			for (int j = xLow; j <= xHigh; j++) {
				int randomState = (int)(Math.random() * 2);
				if (randomState == 1) {
					addAliveCell(j, i);						
				}
				else if (randomState == 0) {
					removeAliveCell(j, i);
				}
			}
		}
		this.gen = 0;
	}
	
	public void changeState(int x, int y) {
		/* 
		 * Toggles the opposite state (ALIVE or DEAD) at the given location
		 * 
		 **/

		if (board.get(new Coordinate(x, y)) != null) {
			removeAliveCell(x, y);
		}
		else {
			addAliveCell(x, y);
		}
	}
	
	public int countLivingNeighbors(int x, int y) {
		/* 
		 * Count alive neighbors of cell at given location
		 * 
		 **/

		int neighbors = 0;
		for (int[] shift : NEIGHBORS) {
			if (board.get(new Coordinate(x + shift[0], y + shift[1])) != null) {
				neighbors++;
			}
		}
		return neighbors;
	}
	

	private boolean deadToAlive(Coordinate key) {
		/* 
		 * Returns true if dead cell at given coordinate will be alive in the next generation
		 *  
		 **/

		int neighbors = this.countLivingNeighbors(key.getX(), key.getY());
		
		if (neighbors != BIRTH_VALUE) {
			return false;
		}
		return true;
	}

	
	private boolean aliveToDead(int neighbors) {
		/* 
		 * Returns true if alive cell at given coordinate will be dead in the next generation
		 *  
		 **/

		if (neighbors >= OVERCROWDING_LOWER_THRESHOLD || neighbors <= LONELINESS_UPPER_THRESHOLD) {
			return true;
		}
		return false;
		
	}
	

	public void nextGen() {
		/* 
		 * Updates board to the next generation
		 * See comments at the top of the class for more details
		 *  
		 **/

    	ArrayList<Coordinate> toKill = new ArrayList<Coordinate>();
    	ArrayList<Coordinate> toRevive = new ArrayList<Coordinate>();
		for (Coordinate key : new ArrayList<Coordinate>(board.keySet())) {
	    	int neighbors = 0;
			for (int[] shift : NEIGHBORS) {
				if (board.get(new Coordinate(key.getX() + shift[0], key.getY() + shift[1])) != null) {
					neighbors++;
				}
				else if (deadToAlive(new Coordinate(key.getX() + shift[0], key.getY() + shift[1]))) {
					toRevive.add(new Coordinate(key.getX() + shift[0], key.getY() + shift[1]));
				}
			}
			if (aliveToDead(neighbors)) {
				toKill.add(key);
			}
		}
	    
		
	    for (Coordinate birthCell : toRevive) {
	    	if(board.get(birthCell) == null) {
	    		addAliveCell(birthCell.getX(), birthCell.getY());
	    	}
	    }
	    for (Coordinate dyingCell : toKill) {
	    	removeAliveCell(dyingCell.getX(), dyingCell.getY());
	    }
	    
	    this.gen++;
	}
	
	public int getGen() {
		/* 
		 * Returns current generation
		 *  
		 **/

		return this.gen;
	}
}
