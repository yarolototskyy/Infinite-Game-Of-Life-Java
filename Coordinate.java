
public class Coordinate {

	/* 
	 * Coordinate class that I just realized is the same as the Point class.
	 * Oh well.
	 *  
	 **/

	private int x, y;
	
	public Coordinate(int x, int y) {
		/* 
		 * Constructs a new Coordinate
		 *  
		 **/
		
		this.x = x;
		this.y = y;
	}
	
	public String toString() {
		/* 
		 * Returns String representation of coordinate
		 *  
		 **/

		return "(" + this.x + ", " + this.y + ")";
	}

	public int getX() {
		/* 
		 * Returns x-value of Coordinate
		 *  
		 **/

		return this.x;
	}
	
	public int getY() {
		/* 
		 * Returns y-value of Coordinate
		 *  
		 **/

		return this.y;
	}
	
    public boolean equals(Object other) {
		/* 
		 * Returns true if coordinate is equal to another given Coordinate
		 *  
		 **/

        if (this == other)
          return true;

        if (!(other instanceof Coordinate))
          return false;

        Coordinate otherPoint = (Coordinate) other;
        return otherPoint.x == x && otherPoint.y == y;
    }
    
    public int hashCode() {
		/* 
		 * Returns hashCode for Coordinate
		 *  
		 **/

    	int result = x;
        result = 31 * result + y;
        return result;
    }
    
}
