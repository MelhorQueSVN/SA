import robocode.ScannedRobotEvent;

public class RobotInfo {
	
	// nome do rôbo
	private String name; 
	// coordenadas do robo
	private Coordinate cord; 
	// energia restante do robo
	private double energy; 
	// velocidade do robo 
	private double vel; 
	// distancia do robo 
	private double dist;  
	// bearing do robo 
	private double bearing; 
	// heading do robo 
	private double heading;
	
	public RobotInfo() { 
		this.name = ""; 
		this.cord = new Coordinate(); 
		this.energy = 0; 
		this.vel = 0; 
		this.dist = 0; 
		this.bearing = 0;	 
		this.heading = 0;
	}
	
	public RobotInfo(String n, double e, double v, double d, double b, double h) { 
		this.name = n;
		this.energy = e; 
		this.vel = v; 
		this.dist = d; 
		this.bearing = b; 
		this.heading = h; 
		
		double angulo = Math.toRadians((heading+bearing)%360); 
		this.cord.setX( this.cord.getX() + Math.sin(angulo)*d); 
		this.cord.setY( this.cord.getY() + Math.cos(angulo)*d);
	}

	// Recebe o evento corresponde a ter encontrado um robô no scanner
	public RobotInfo(ScannedRobotEvent sre) { 
		this.name = sre.getName();
		this.energy = sre.getEnergy(); 
		this.vel = sre.getVelocity(); 
		this.dist = sre.getDistance(); 
		this.bearing = sre.getBearing(); 
		this.heading = sre.getHeading(); 
		
		double angulo = Math.toRadians((heading+bearing)%360); 
		this.cord.setX( this.cord.getX() + Math.sin(angulo)*dist); 
		this.cord.setY( this.cord.getY() + Math.cos(angulo)*dist);
	}
	
	// getters
	public String getName() {return this.name;} 
	public Coordinate getCoordinates() {return this.cord;} 
	public double getEnergy() {return this.energy;} 
	public double getVelocity() {return this.vel;} 
	public double getDistance() {return this.dist;} 
	public double getBearing() {return this.bearing;} 
	public double getHeading() {return this.heading;}
	
	// setters
	public void setName(String n) {this.name = n;} 
	public void setCoordinates(Coordinate c) {this.cord = c;} 
	public void setEnergy(double e) {this.energy = e;}
	public void setVelocity(double v) {this.vel = v;} 
	public void setDistance(double d) {this.dist = d;} 
	public void setBearing(double b) {this.bearing = b;} 
	public void setHeading(double h) {this.heading = h;}

}
