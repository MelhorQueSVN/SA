import java.io.Serializable;

import robocode.ScannedRobotEvent;

public class RobotInfo implements Serializable{
	
	// nome do rôbo
	private String name; 
	// coordenadas do robo
	private Coordinate cord;
	// energia do robot
	private double energy;
	
	public RobotInfo() { 
		this.name = ""; 
		this.cord = new Coordinate();
		this.energy = 0.0;
	}
	
	public RobotInfo(String n,Coordinate c, double e) {
		this.name = n; 
		this.cord = c;
		this.energy = e;
	}

	// getters
	public String getName() {return this.name;} 
	public Coordinate getCoordinates() {return new Coordinate(this.cord);}
	public double getEnergy() {return this.energy;}
	/*public double getVelocity() {return this.vel;}
	public double getDistance() {return this.dist;} 
	public double getBearing() {return this.bearing;} 
	public double getHeading() {return this.heading;}*/
	
	// setters
	public void setName(String n) {this.name = n;} 
	public void setCoordinates(Coordinate c) {this.cord = c;} 
	public void setEnergy(double e) {this.energy = e;}
	/*public void setVelocity(double v) {this.vel = v;}
	public void setDistance(double d) {this.dist = d;} 
	public void setBearing(double b) {this.bearing = b;} 
	public void setHeading(double h) {this.heading = h;}*/

}
