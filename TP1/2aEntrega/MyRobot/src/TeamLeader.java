import java.awt.Color;
import java.io.IOException;
import java.util.Random;

import robocode.HitByBulletEvent;
import robocode.HitWallEvent;
import robocode.ScannedRobotEvent;
import robocode.TeamRobot;
import robocode.util.Utils;

import static robocode.util.Utils.normalRelativeAngleDegrees;

/* 
 * 
 */
public class TeamLeader extends TeamRobot{
		
	private String[] teamMates;
	
	public void run() { 
		// cores do robo
		teamMates = getTeammates(); 
		System.out.println("os meus teammates são: " + teamMates);
		setColors(Color.orange,Color.red,Color.black); 
		System.out.println("Name leader: " + this.getName());
		while(true) { 
			Random r = new Random();  
			int strategy = r.nextInt(1); 
			switch(strategy) { 
				case 1: 
					setTurnRight(270); 
					ahead(500); 
					break; 
				default: 
					setTurnLeft(270); 
					ahead(500); 
			}
		}
	} 
	
	public void onScannedRobot(ScannedRobotEvent e) { 
		if(!isTeammate(e.getName())) { 
			double e_bearing = e.getBearing() + this.getHeading(); 
			double enemy_x = getX() + e.getDistance() * Math.sin(Math.toRadians(e_bearing)); 
			double enemy_y = getY() + e.getDistance() + Math.cos(Math.toRadians(e_bearing));   
			double enemy_distance = e.getDistance();
			Coordinate c = new Coordinate(enemy_x,enemy_y);
			String enemy_name = e.getName(); 
			double enemy_energy = e.getEnergy(); 
			double enemy_vel = e.getVelocity(); 
			double enemy_bearing = e.getBearing(); 
			double enemy_heading = e.getHeading();
			RobotInfo ri = new RobotInfo(enemy_name,c,enemy_energy,enemy_vel,enemy_distance,enemy_bearing,enemy_heading);  
			System.out.println(ri.toString());
			// faz broadcast da informação scaneada do rôbo
			System.out.println("enviei a informação! " + this.getName()); 
			try {
				broadcastMessage(ri);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			// no final faz fire da sua arma 
			double theta = Math.toDegrees(Math.atan2(enemy_x,enemy_y));
			turnGunRight(normalRelativeAngleDegrees(theta - getGunHeading()));		
			fire(1);
		}
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
	
	public void onHitWall(HitWallEvent e) {
		move(getBattleFieldWidth()/2,getBattleFieldHeight()/2);
	}
	
	

	private void move(double x, double y) { 
   	 /* Calculate the difference bettwen the current position and the target position. */
       x = x - getX();
       y = y - getY();
    
       /* Calculate the angle relative to the current heading. */
       double goAngle = Utils.normalRelativeAngle(Math.atan2(x, y) - getHeadingRadians());
    
       /*
        * Apply a tangent to the turn this is a cheap way of achieving back to front turn angle as tangents period is PI.
        * The output is very close to doing it correctly under most inputs. Applying the arctan will reverse the function
        * back into a normal value, correcting the value. The arctan is not needed if code size is required, the error from
        * tangent evening out over multiple turns.
        */
       setTurnRightRadians(Math.atan(Math.tan(goAngle)));
    
       /* 
        * The cosine call reduces the amount moved more the more perpendicular it is to the desired angle of travel. The
        * hypot is a quick way of calculating the distance to move as it calculates the length of the given coordinates
        * from 0.
        */
       ahead(Math.cos(goAngle) * Math.hypot(x, y));
   }
}

