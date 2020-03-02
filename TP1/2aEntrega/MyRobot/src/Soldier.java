
import java.awt.Color;
import java.util.*;
import java.util.concurrent.*;
import robocode.*;
import robocode.util.Utils;

public class Soldier extends TeamRobot implements Droid {

	private int border = 150; 
	private boolean lider_morreu = false;

	public void run() {
		// cores do rôbo
		setColors(new Color(248, 0, 0),new Color(248, 0, 0),Color.black);  
		setBulletColor(Color.red);
		
		while(true) {
			if(lider_morreu == false) {
				if(checkCoordinates()){
					estrategiaNormal();
				}else
					go(getBattleFieldWidth()/2,getBattleFieldHeight()/2);
			// caso o lider morra começa a fazer movimentos random
			} else { 
				
				// no caso de perder o lider os droids mudam de cor para amarelo
				setColors(Color.YELLOW,Color.YELLOW,Color.black);  
				
				if(checkCoordinates()){
					estrategiaFuga();
				} else 
					go(getBattleFieldWidth()/2,getBattleFieldHeight()/2);
			}	
		}
	} 
	
	
	// verifica as coordenadas relativas do rôbo
	public boolean checkCoordinates() { 
		boolean pode = false; 
		if (getX()>this.border && getY()>this.border 
		&& getX()<getBattleFieldWidth()-this.border && getY()<getBattleFieldHeight()-this.border)
			pode = true; 
		return pode;
	}
	
	// estratégia em condições normais(lider vivo)
	public void estrategiaNormal() { 
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
				setTurnRight(180); 
				ahead(400);
		}
	}
	
	// escolha de estratégia quando o líder morre
	public void estrategiaFuga() { 
		Random r = new Random();
		int strategy = r.nextInt(1); 
		// random para os degrees que vira e para os pixeis que anda, tendo 2 estratégias que poderá escolher
		int low = 10;
		int high = 360;
		int result = r.nextInt(high-low) + low;
		int low_p = 50;
		int high_p = 350;
		int result_p = r.nextInt(high_p-low_p) + low_p;
		switch(strategy) { 
			case 1: 
				setTurnRight(result); 
				ahead(result_p);  
				setTurnLeft(result); 
				ahead(result_p-20);
				break; 
			default: 
				setTurnLeft(result); 
				ahead(result_p); 
				setTurnRight(result); 
				ahead(result_p-20); 
		}
	}

	public void onMessageReceived(MessageEvent e) {
		if (e.getMessage() instanceof RobotInfo) {
			RobotInfo en = (RobotInfo) e.getMessage();
			Coordinate cor = new Coordinate(en.getCoordinates());
			double dx = cor.getX() - this.getX();
			double dy = cor.getY() - this.getY();
			double theta = Math.toDegrees(Math.atan2(dx, dy));
			turnRight(Utils.normalRelativeAngleDegrees(theta - getGunHeading()));
			fire(3);
			ahead(100);
		} 
	}

	public void onHitByBullet(HitByBulletEvent e) {
		Random r = new Random();
		int estrategia = r.nextInt(1);
		switch(estrategia) { 
			case 1: 
				setTurnRight(60); 
				ahead(100); 
				break; 
			default: 
				setTurnLeft(60); 
				ahead(100);
		}
	}

	public void onHitWall(HitWallEvent e) {
		Random r = new Random();
		int estrategia = r.nextInt(1);
		if(e.getBearing() > -90 && e.getBearing() <= 90){
			switch(estrategia) { 
			case 1: 
				setTurnLeft(45); 
				ahead(150); 
				break; 
			default: 
				setTurnRight(45); 
				ahead(150); 
		}
		}else{
			switch(estrategia) { 
			case 1: 
				setTurnLeft(45); 
				ahead(150); 
				break; 
			default: 
				setTurnRight(45); 
				ahead(150);
			}
		}
	}

	public void onHitRobot(HitRobotEvent e) {
		if(!isTeammate(e.getName())){
			fire(3);
			ahead(5);
		}
	}
	
	
	// se o lider morreu coloca a flag a true e entra em modo de contenção
	public void onRobotDeath(RobotDeathEvent e) { 
		if(e.getName().equals("TeamLeader")) { 
			lider_morreu = true;
		}
	}
	
	
	private void go(double x, double y) {
		x = x - getX();
		y = y - getY();
		double goAngle = Utils.normalRelativeAngle(Math.atan2(x, y) - getHeadingRadians());
		setTurnRightRadians(Math.atan(Math.tan(goAngle)));
		ahead(Math.cos(goAngle) * Math.hypot(x, y));
	}
}