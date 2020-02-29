import java.io.Serializable;

public class Coordinate implements Serializable{
		
	private double x; 
	private double y; 
	
	public Coordinate() { 
		this.x = 0; 
		this.y = 0; 
	}
	
	public Coordinate(double xx, double yy) { 
		this.x = xx; 
		this.y = yy; 
	}

	public Coordinate(Coordinate cord) {
		this.x = cord.getX();
		this.y = cord.getY();
	}
	
	public double getX() {return this.x;} 
	public double getY() {return this.y;} 
	public void setX(double xx) {this.x = xx;} 
	public void setY(double yy) {this.y =yy;}

	public double distance (Coordinate c){
		return Math.sqrt((c.getY() - this.y) * (c.getY() - this.y) + (c.getX() - this.x) * (c.getX() - this.x));
	}
}
