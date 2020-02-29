import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.*;

import robocode.HitByBulletEvent;
import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.RobotDeathEvent;
import robocode.ScannedRobotEvent;
import robocode.TeamRobot;
import robocode.util.Utils;

import static robocode.util.Utils.normalRelativeAngleDegrees;

/*
 *
 */
public class TeamLeader extends TeamRobot{

	private int border=150;
	private HashMap<String,Coordinate> last_coordinate_robot = new HashMap<>();
	private String currentTarget;
	private RobotInfo target;
	private Coordinate nextDestination;
	static Coordinate lastPosition;
	static Coordinate myPos;
	static double myEnergy;

	static Hashtable enemies = new Hashtable();


	public void run() {
		// cores do rôbo
		setColors(new Color(136, 0, 0),new Color(136, 0, 0),Color.black); 
		setBulletColor(Color.red);
		setAdjustGunForRobotTurn(true);
		setAdjustRadarForGunTurn(true);

		setTurnRadarRightRadians(Double.POSITIVE_INFINITY);

		nextDestination = lastPosition = myPos = new Coordinate(getX(), getY());
		target = null;/*new RobotInfo();*/

		/*while(true) {
			//setTurnRadarRight(Double.POSITIVE_INFINITY);
			if(getX()>this.border && getY()>this.border && getX()<getBattleFieldWidth()-this.border && getY()<getBattleFieldHeight()-this.border){
				Random r = new Random();
				int strategy = r.nextInt(1);
				if(strategy==1){
					setTurnRight(270);
					ahead(500);
				}else{
					setTurnLeft(180);
					ahead(400);
					setTurnRight(180);
					ahead(400);
				}
			}else
				go(getBattleFieldWidth()/2,getBattleFieldHeight()/2);
		}*/
		
		do {

			myPos = new Coordinate(getX(),getY());
			myEnergy = getEnergy();

			// wait until you have scanned all other bots. this should take around 7 to 9 ticks.
			if(target != null && getTime()>9) {
				doMovementAndGun();
			}

			execute();

		} while(true);
		
	}



	//- stuff -----------------------------------------------------------------------------------------------------------------------------------
	public void doMovementAndGun() {

		double distanceToTarget = myPos.distance(target.getCoordinates());

		//**** gun ******************//
		// HeadOnTargeting there's nothing I can say about this
		if(getGunTurnRemaining() == 0 && myEnergy > 1) {
			setFire( Math.min(Math.min(myEnergy/6d, 1300d/distanceToTarget), target.getEnergy()/3d) );
		}

		setTurnGunRightRadians(Utils.normalRelativeAngle(calcAngle(target.getCoordinates(), myPos) - getGunHeadingRadians()));

		//**** move *****************//
		double distanceToNextDestination = myPos.distance(nextDestination);

		//search a new destination if I reached this one
		if(distanceToNextDestination < 15) {

			// there should be better formulas then this one but it is basically here to increase OneOnOne performance. with more bots
			// addLast will mostly be 1
			double addLast = 1 - Math.rint(Math.pow(Math.random(), getOthers()));

			//Rectangle2D.Double battleField = new Rectangle2D.Double(30, 30, getBattleFieldWidth() - 60, getBattleFieldHeight() - 60);
			Coordinate testPoint;
			int i=0;

			do {
				//	calculate the testPoint somewhere around the current position. 100 + 200*Math.random() proved to be good if there are
				//	around 10 bots in a 1000x1000 field. but this needs to be limited this to distanceToTarget*0.8. this way the bot wont
				//	run into the target (should mostly be the closest bot)
				testPoint = calcPoint(myPos, Math.min(distanceToTarget*0.8, 100 + 200*Math.random()), 2*Math.PI*Math.random());
				if(30 < testPoint.getX() && testPoint.getX() < getBattleFieldWidth() - 60 && 30 < testPoint.getY() && testPoint.getY() < getBattleFieldHeight() - 60 && evaluate(testPoint, addLast) < evaluate(nextDestination, addLast)) {
					nextDestination = testPoint;
				}
			} while(i++ < 200);

			lastPosition = myPos;

		} else {

			// just the normal goTo stuff
			double angle = calcAngle(nextDestination, myPos) - getHeadingRadians();
			double direction = 1;

			if(Math.cos(angle) < 0) {
				angle += Math.PI;
				direction = -1;
			}

			setAhead(distanceToNextDestination * direction);
			setTurnRightRadians(angle = Utils.normalRelativeAngle(angle));
			// hitting walls isn't a good idea, but HawkOnFire still does it pretty often
			setMaxVelocity(Math.abs(angle) > 1 ? 0 : 8d);

		}
	}



	public static double evaluate(Coordinate p, double addLast) {
		// this is basically here that the bot uses more space on the battlefield. In melee it is dangerous to stay somewhere too long.
		double eval = addLast*0.08/((p.distance(lastPosition)) * (p.distance(lastPosition)));

		Enumeration _enum = enemies.elements();
		while (_enum.hasMoreElements()) {
			RobotInfo en = (RobotInfo) _enum.nextElement();
			// this is the heart of HawkOnFire. So I try to explain what I wanted to do:
			// -	Math.min(en.energy/myEnergy,2) is multiplied because en.energy/myEnergy is an indicator how dangerous an enemy is
			// -	Math.abs(Math.cos(calcAngle(myPos, p) - calcAngle(en.pos, p))) is bigger if the moving direction isn't good in relation
			//		to a certain bot. it would be more natural to use Math.abs(Math.cos(calcAngle(p, myPos) - calcAngle(en.pos, myPos)))
			//		but this wasn't going to give me good results
			// -	1 / p.distanceSq(en.pos) is just the normal anti gravity thing
			if(en != null) {
				eval += Math.min(en.getEnergy()/myEnergy,2) *
						(1 + Math.abs(Math.cos(calcAngle(myPos, p) - calcAngle(en.getCoordinates(), p)))) / ((p.distance(en.getCoordinates())) * (p.distance(en.getCoordinates())));
			}
		}
		return eval;
	}




	
	public void onScannedRobot(ScannedRobotEvent e) {

		if(!isTeammate(e.getName())){

			RobotInfo en = (RobotInfo) enemies.get(e.getName());

			if(en == null){
				en = new RobotInfo();
				enemies.put(e.getName(), en);
			}

			double enemyBearing = this.getHeading() + e.getBearing();
			double enemyX = getX() + e.getDistance() * Math.sin(Math.toRadians(enemyBearing));
			double enemyY = getY() + e.getDistance() * Math.cos(Math.toRadians(enemyBearing));

			Coordinate c = new Coordinate(enemyX, enemyY);
			last_coordinate_robot.put(e.getName(), c);

			en.setEnergy(e.getEnergy());
			en.setCoordinates(c);

			if(currentTarget != null){
				try {
					broadcastMessage(new RobotInfo(currentTarget, last_coordinate_robot.get(currentTarget), 0.0));
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}

			if (currentTarget == null){
				currentTarget = e.getName();	
				try {
					broadcastMessage(new RobotInfo(e.getName(), last_coordinate_robot.get(currentTarget), 0.0));
				} catch (IOException ex) {
					ex.printStackTrace(out);
				}
			}

			// normal target selection: the one closer to you is the most dangerous so attack him
			if(target == null || e.getDistance() < myPos.distance(target.getCoordinates())) {
				target = en;
			}

			// locks the radar if there is only one opponent left
			if(getOthers()==1)	setTurnRadarLeftRadians(getRadarTurnRemainingRadians());

		}


	}

	private static double calcAngle(Coordinate p2,Coordinate p1){
		return Math.atan2(p2.getX() - p1.getX(), p2.getY() - p1.getY());
	}

	private static Coordinate calcPoint(Coordinate p, double dist, double ang) {
		return new Coordinate(p.getX() + dist*Math.sin(ang), p.getY() + dist*Math.cos(ang));
	}
	
	public void onHitByBullet(HitByBulletEvent e) {

		
		if(!isTeammate(e.getName())) {
			currentTarget = e.getName();
		}
		
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
		go(getBattleFieldWidth()/2,getBattleFieldHeight()/2);
	}

	public void onHitRobot(HitRobotEvent e) {
		
		Random r = new Random();
		int strategy = r.nextInt(1);
		if(e.getBearing() > -90 && e.getBearing() <= 90){
			if(strategy==1){
				setTurnRight(45);
				back(200);
			}else{
				setTurnLeft(45);
				back(200);
			}
		}else{
			if(strategy==1){
				setTurnRight(45);
				ahead(200);
			}else{
				setTurnLeft(45);
				ahead(200);
			}
		}
	}
	
	// remove o currenttarget da hashmap e passa o currenttarget a null
	public void onRobotDeath(RobotDeathEvent e) { 
		if(!isTeammate(e.getName())) { 
			last_coordinate_robot.remove(e.getName()); 
			if(e.getName().equals(currentTarget))
				currentTarget = null;
			if(e.getName().equals(target.getName()))
				target = null;
		}
	}
	
	private void go(double x, double y) {
		x = x - getX(); y = y - getY();
		double goAngle = Utils.normalRelativeAngle(Math.atan2(x, y) - getHeadingRadians());
		setTurnRightRadians(Math.atan(Math.tan(goAngle)));
		ahead(Math.cos(goAngle) * Math.hypot(x, y));
	}

}



