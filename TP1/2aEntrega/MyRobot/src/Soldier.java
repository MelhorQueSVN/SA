import java.awt.Color;
import java.util.Random;

import robocode.Droid;
import robocode.HitByBulletEvent;
import robocode.MessageEvent;
import robocode.TeamRobot;
import robocode.util.Utils; 

import static robocode.util.Utils.normalRelativeAngleDegrees;

/* 
 * 	
 */
public class Soldier extends TeamRobot implements Droid{

	public void run() { 
		// Cores do rôbo
		setColors(Color.blue,Color.red,Color.black); 
		System.out.println("Meu nome é: " + this.getName());
		while(true) { 
			Random r = new Random();  
			int strategy = r.nextInt(1); 
			switch(strategy) { 
				case 1: 
					setTurnRight(270); 
					ahead(500);  
					break; 
				default: 
					setTurnLeft(180);
					ahead(400);
			}
		}
	}	
	
	public void onMessageReceived(MessageEvent e) { 
		// se recebeu uma mensagem com a info do robo scaneado  
		System.out.println("Não estou a receber\n"); 
		System.out.println(e.getMessage().toString());
		if (e.getMessage() instanceof RobotInfo) { 
			System.out.println("recebi a informação!\n");
			RobotInfo ri = (RobotInfo) e.getMessage(); 
			String name = ri.getName(); 
			Coordinate cord = ri.getCoordinates(); 
			double energy = ri.getEnergy(); 
			double vel = ri.getVelocity(); 
			double dist = ri.getDistance();  
			double bearing = ri.getBearing(); 
			double heading = ri.getHeading(); 
			System.out.println("Nome recebido: " + name); 
			// Usa o intercept para disparar 
			Intercept i = new Intercept(); 
			i.calculate(this.getX(),this.getY(),cord.getX(), cord.getY(), heading, vel, 3, 0);
			if ((i.impactPoint.getX() > 0) && (i.impactPoint.getX() < getBattleFieldWidth()) && (i.impactPoint.getY() > 0) && (i.impactPoint.getY() < getBattleFieldHeight())) {
    			orderFire(i.impactPoint.getX(),i.impactPoint.getY());
			}
		}
	}
	
	public void orderFire(double x, double y){
		double dx = x - this.getX();
		double dy = y - this.getY();
	
		double theta = Math.toDegrees(Math.atan2(dx, dy));
		turnGunRight(normalRelativeAngleDegrees(theta - getGunHeading()));
		fire(3); 
		ahead(10);
	}
	
	public void onHitByBullet(HitByBulletEvent e) {
		Random r = new Random();
		int strategy = r.nextInt(1);
		if(strategy==1){
			setTurnRight(60);
			ahead(100);
		}else{
			setTurnLeft(60);
			ahead(100);
		}
	}
}
