package TK;
import robocode.*;
import java.awt.geom.Point2D;
import java.awt.Color;

public class PartsBot2 extends AdvancedRobot {
	private int wallMargin = 100;
	private int tooCloseToWall = 0;
	private AdvancedEnemyBot enemy = new AdvancedEnemyBot();

	RobotPart[] parts = new RobotPart[3]; // make three parts
	final static int RADAR = 0;
	final static int GUN = 1;
	final static int TANK = 2;

	public void run() {
		
		
		parts[RADAR] = new Radar();
		parts[GUN] = new Gun();
		if (getOthers() > 10) {
			// a large group calls for fluid movement
			parts[TANK] = new CirclingTank();
		} else if (getOthers() > 1) {
			// dodging is the best small-group tactic
			// this is really just STRAFING, NOT DODGING
			parts[TANK] = new DodgingTank();
		} else if (getOthers() == 1) {
			// if there's only one bot left, hunt him down
			parts[TANK] = new SeekAndDestroy();
		}
		
		
		// Don't get too close to the walls
		addCustomEvent(new Condition("too_close_to_walls") {
				public boolean test() {
					return (
						// we're too close to the left wall
						(getX() <= wallMargin ||
						 // or we're too close to the right wall
						 getX() >= getBattleFieldWidth() - wallMargin ||
						 // or we're too close to the bottom wall
						 getY() <= wallMargin ||
						 // or we're too close to the top wall
						 getY() >= getBattleFieldHeight() - wallMargin)
						);
					}
				});

		// initialize each part
		for (int i = 0; i < parts.length; i++) {
			parts[i].init();
		}

		// iterate through each part, moving them as we go
		for (int i = 0;true; i = (i + 1) % parts.length) {
			parts[i].move();
			if (i == 0) execute();
		}
	   /*
	   // Create the condition for our custom event
	   Condition triggerHitCondition = new Condition("triggerhit") {
	       public boolean test() {
	           return (getEnergy() <= trigger);
	       }
	   }
	
	   // Add our custom event based on our condition
	   addCustomEvent(triggerHitCondition);
	   */
		/*
		// Don't get too close to the walls
		Condition tooCloseToWallsCondition = new Condition("too_close_to_walls") {
			public boolean test() {
				return (
					// we're too close to the left wall
					(getX() <= wallMargin ||
					 // or we're too close to the right wall
					 getX() >= getBattleFieldWidth() - wallMargin ||
					 // or we're too close to the bottom wall
					 getY() <= wallMargin ||
					 // or we're too close to the top wall
					 getY() >= getBattleFieldHeight() - wallMargin)
					);
			}
		}
		
		addCustomEvent(tooCloseToWallsCondition);
		*/
		
		
	}

	
	public void onScannedRobot(ScannedRobotEvent e) {
		Radar radar = (Radar)parts[RADAR];
		if (radar.shouldTrack(e)) enemy.update(e, this);
	}

	public void onRobotDeath(RobotDeathEvent e) {
		Radar radar = (Radar)parts[RADAR];
		if (radar.wasTracking(e)) enemy.reset();
		if (getOthers() > 10) {
			// a large group calls for fluid movement
			parts[TANK] = new CirclingTank();
		} else if (getOthers() > 1) {
			// dodging is the best small-group tactic
			// this is really just STRAFING, NOT DODGING
			parts[TANK] = new DodgingTank();
		} else if (getOthers() == 1) {
			// if there's only one bot left, hunt him down
			parts[TANK] = new SeekAndDestroy();
		}
		
	}
	
	public void onCustomEvent(CustomEvent e) {
		if (e.getCondition().getName().equals("too_close_to_walls"))
		{
			if (tooCloseToWall <= 0) {
				// if we weren't already dealing with the walls,
				// we are now
				tooCloseToWall += wallMargin;
				setMaxVelocity(0); // stop!!!
			}
		}
	}
	
	public void onHitWall(HitWallEvent e) { out.println("OUCH! I hit a wall anyway!"); }

	public void onHitRobot(HitRobotEvent e) { tooCloseToWall = 0; }

	
	

	// ... put normalizeBearing and absoluteBearing methods here
		// normalizes a bearing to between +180 and -180
	public double normalizeBearing(double angle) {
		while (angle >  180) angle -= 360;
		while (angle < -180) angle += 360;
		return angle;
	}

	// computes the absolute bearing between two points
	public double absoluteBearing(double x1, double y1, double x2, double y2) {
		double xo = x2-x1;
		double yo = y2-y1;
		double hyp = Point2D.distance(x1, y1, x2, y2);
		double arcSin = Math.toDegrees(Math.asin(xo / hyp));
		double bearing = 0;
	
		if (xo > 0 && yo > 0) { // both pos: lower-Left
			bearing = arcSin;
		} else if (xo < 0 && yo > 0) { // x neg, y pos: lower-right
			bearing = 360 + arcSin; // arcsin is negative here, actually 360 - ang
		} else if (xo > 0 && yo < 0) { // x pos, y neg: upper-left
			bearing = 180 - arcSin;
		} else if (xo < 0 && yo < 0) { // both neg: upper-right
			bearing = 180 - arcSin; // arcsin is negative here, actually 180 + ang
		}
	
		return bearing;
	}

	// ...  declare the RobotPart interface and classes that implement it here
	// They will be _inner_ classes.
	
	public interface RobotPart {
		public void init();
		public void move();
	}
	
	public class Radar implements RobotPart {
		private byte scanDirection = 1;
		public void init(){
			//super.init();
			setAdjustRadarForGunTurn(true);
		}
		public void move(){
			//super.move();
			//execute();
			if (enemy.none()) {
				// look around
				setTurnRadarRight(360);
			}
			else {
				double turn = getHeading() - getRadarHeading() + enemy.getBearing();
				turn += 30 * scanDirection;
				setTurnRadarRight(turn);
				scanDirection *= -1;
			}
		}
		
		boolean shouldTrack(ScannedRobotEvent e) {
			// track if we have no enemy, the one we found is significantly
			// closer, or we scanned the one we've been tracking.
			return (enemy.none() || e.getDistance() < enemy.getDistance() - 70 ||
					e.getName().equals(enemy.getName()));
		}
		
		boolean wasTracking(RobotDeathEvent e) {
			return (e.getName().equals(enemy.getName()));
		}
	}
	
	
	
	public class Gun implements RobotPart {
		public void init(){
			//super.init();
			setAdjustGunForRobotTurn(true);
		}
		public void move(){
			//super.move();
			// calculate firepower based on distance
			double firePower = Math.min(500 / enemy.getDistance(), 3);
			// calculate speed of bullet
			double bulletSpeed = 20 - firePower * 3;
			// distance = rate * time, solved for time
			long time = (long)(enemy.getDistance() / bulletSpeed);
			//  calculate gun turn toward enemy
			//double turn = getHeading() - getGunHeading() + enemy.getBearing();
			// normalize the turn to take the shortest path there
			// calculate gun turn to predicted x,y location
			double futureX = enemy.getFutureX(time);
			double futureY = enemy.getFutureY(time);
			double absDeg = absoluteBearing(getX(), getY(), futureX, futureY);
			// turn the gun to the predicted x,y location
			setTurnGunRight(normalizeBearing(absDeg - getGunHeading()));
			// if the gun is cool and we're pointed at the target, shoot!
			if (getGunHeat() == 0 && Math.abs(getGunTurnRemaining()) < 10) {
				setFire(firePower);
			}
		}
	}
	
	/*
	public class Tank implements RobotPart {
			
		private byte moveDirection = 1;
		public void init(){
			//super.init();
			setColors(Color.red,Color.blue,Color.green); // body,gun,radar
		}

		public void move(){
			//super.move();
			// always square off against our enemy
			setTurnRight(normalizeBearing(enemy.getBearing() + 90 - (30 * moveDirection)));
		
			// strafe by changing direction every 20 ticks
			if (getTime() % 20 == 0) {
				moveDirection *= -1;
				setAhead(150 * moveDirection);
			}
			
			// if we're close to the wall, eventually, we'll move away
			if (tooCloseToWall > 0) tooCloseToWall--;
			
			// move a little closer
			//if (enemy.getDistance() > 200) {
			//	setAhead(enemy.getDistance() / 2);
			//}
			// but not too close
			//if (enemy.getDistance() < 100) {
			//	setBack(enemy.getDistance());
			//}
			
			//if (getVelocity() == 0){
	    	//	moveDirection *= -1;
			//}
			
			if (getVelocity() == 0) {
				setMaxVelocity(8);
				moveDirection *= -1;
				setAhead(10000 * moveDirection);
			}

		}

	}
	*/
	

	public class CirclingTank implements RobotPart {
		private byte moveDirection = 1;
		public void init(){
			setColors(Color.white,Color.black,Color.green); // body,gun,radar
		}
		
		public void move(){
			out.println("CirclingBot");
			setTurnRight(normalizeBearing(enemy.getBearing() + 90));
			/*
			// switch directions if we've stopped
			if (getVelocity() == 0) {
				moveDirection *= -1;
			}
			*/
			/*
			// always square off against our enemy
			setTurnRight(normalizeBearing(enemy.getBearing() + 90));
			*/
			// circle our enemy
			setAhead(1000 * moveDirection);

			// if we're close to the wall, eventually, we'll move away
			if (tooCloseToWall > 0) tooCloseToWall--;

			/*
			if (getVelocity() == 0) {
				setMaxVelocity(8);
				moveDirection *= -1;
				setAhead(10000 * moveDirection);
			}
			*/

		}
		
	}
	
	public class DodgingTank implements RobotPart {
		private byte moveDirection = 1;
		public void init(){
			setColors(Color.white,Color.black,Color.green); // body,gun,radar
		}
		
		public void move(){
			out.println("DodgingBot");
			setTurnRight(normalizeBearing(enemy.getBearing() + 90));
			
			// strafe by changing direction every 20 ticks
			if (getTime() % 20 == 0) {
				moveDirection *= -1;
				setAhead(150 * moveDirection);
				out.println("Strafing");
			}
			

			// if we're close to the wall, eventually, we'll move away
			if (tooCloseToWall > 0) tooCloseToWall--;

			
			if (getVelocity() == 0) {
				setMaxVelocity(8);
				moveDirection *= -1;
				setAhead(10000 * moveDirection);
			}
			

		}
	}
	
	public class SeekAndDestroy implements RobotPart {
		private byte moveDirection = 1;
		public void init(){
			setColors(Color.white,Color.black,Color.green); // body,gun,radar
		}
		
		public void move(){
			out.println("SeekAndDestroy Bot");
			setTurnRight(normalizeBearing(enemy.getBearing() + 90 - (30 * moveDirection)));
			/*
			// strafe by changing direction every 20 ticks
			if (getTime() % 20 == 0) {
				moveDirection *= -1;
				setAhead(150 * moveDirection);
			}
			*/

			// if we're close to the wall, eventually, we'll move away
			if (tooCloseToWall > 0) tooCloseToWall--;

			
			if (getVelocity() == 0) {
				setMaxVelocity(8);
				moveDirection *= -1;
				setAhead(150 * moveDirection);
			}
			

		}
	}
	

	
	
	
}



