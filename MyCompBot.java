package TK;
import robocode.*;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.io.*;
import java.text.NumberFormat;

// API help : http://robocode.sourceforge.net/docs/robocode/robocode/Robot.html

/**
 * CompBot - a robot by (your name here)
 */
public class MyCompBot extends AdvancedRobot
{
	double oldEnergyLevel = 100;
	int toggleDirection = 1;
	int gunDirection = 1;
	private byte scanDirection = 1;
	private AdvancedEnemyBot enemy = new AdvancedEnemyBot();
	private PrintStream ps;
	private NumberFormat f;
	/**
	 * run: CompBot's default behavior
	 */

	public void run() {

		setColors(Color.red,Color.blue,Color.green); // body,gun,radar
		
		
		setAdjustRadarForRobotTurn(true);
		enemy.reset();
		File file;
		try {
			file = getDataFile("output.dat");
			RobocodeFileOutputStream roboOutput = new RobocodeFileOutputStream(file);
			ps = new PrintStream(roboOutput);
	    }
		catch (IOException ex) {
	      //System.out.println("There was a problem creating/writing to the temp file");
	      ex.printStackTrace();
	    }
		f = NumberFormat.getNumberInstance();

		f.setMaximumFractionDigits(2);


		while (true) {
			/*
			double turn = getHeading() - getRadarHeading() + enemy.getBearing();
			turn += 30 * scanDirection;
			setTurnRadarRight(turn);
			scanDirection *= -1;
			execute();
			*/
			if (enemy.none()) {
				// look around
				setTurnRadarRight(36000);
			}	 else {
				// keep him inside a cone
				double turn = getHeading() - getRadarHeading() + enemy.getBearing();
				turn += 30 * scanDirection;
				setTurnRadarRight(turn);
				scanDirection *= -1;
			}
			execute();
			
		}
		

		/*
		while(true) {


			//turnGunRight(99999);
			turnRadarRight(360);
		}
		*/
		/*
		setAdjustRadarForRobotTurn(true);
		enemy.reset();
		while (true) {
			setTurnRadarRight(360);
			
			execute();
		}
		*/
	}
	
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

	/**
	 * onScannedRobot: What to do when you see another robot
	 */
	public void onScannedRobot(ScannedRobotEvent e) {
		setTurnRight(e.getBearing()+90-30*toggleDirection);
		//scanDirection *= -1; // change value from 1 to -1
		//setTurnRadarRight(360 * scanDirection);
		//setTurnRadarRight(getHeading() - getRadarHeading() + e.getBearing());
		//change later - how to sense if enemy bot is facing gun towards me?
		/*
		if(e.getBearing()>0){
			setTurnGunRight(e.getBearing());
		}
		else{
			setTurnGunLeft(e.getBearing());
		}
		*/
		
		// track if we have no enemy, the one we found is significantly
		// closer, or we scanned the one we've been tracking.
		if ( enemy.none() || e.getDistance() < enemy.getDistance() - 70 ||
				e.getName().equals(enemy.getName())) {
	
			// track him using the NEW update method
			enemy.update(e, this);
		}
		

	    double energyChange = oldEnergyLevel-e.getEnergy();
	    if (energyChange>0 && energyChange<=3) {
	         // dodge mechanism
	         toggleDirection = -toggleDirection;
	         setAhead((e.getDistance()/4+25)*toggleDirection);
	     }
	    // When a bot is spotted,
	    // sweep the gun and radar
	    //gunDirection = -gunDirection;
	    //setTurnGunRight(99999*gunDirection);
		//setTurnGunRight(getHeading() - getGunHeading() + e.getBearing());
		//  calculate gun turn toward enemy
		
		// calculate firepower based on distance
		double firePower = Math.min(500 / enemy.getDistance(), 3);
		// calculate speed of bullet
		double bulletSpeed = 20 - firePower * 3;
		// distance = rate * time, solved for time
		long time = (long)(enemy.getDistance() / bulletSpeed);

		// calculate gun turn to predicted x,y location
		double futureX = enemy.getFutureX(time);
		double futureY = enemy.getFutureY(time);
		double absDeg = absoluteBearing(getX(), getY(), futureX, futureY);
		// turn the gun to the predicted x,y location
		setTurnGunRight(normalizeBearing(absDeg - getGunHeading()));
	    //
		
		ps.println("Tracking: " + enemy.getName() + " at (x,y) (" + 
		f.format(enemy.getX()) + ", " + f.format(enemy.getY()) + ")");
		
		// if the gun is cool and we're pointed at the target, shoot!
		if (getGunHeat() == 0 && Math.abs(getGunTurnRemaining()) < 10) {
			setFire(firePower);
		}
	    // Fire directly at target
	    //fire ( 2 ) ;
		/*
		if (event.getDistance() < 100) {
	    	fire(3);
	    } else {
	        fire(1);
	    }
		*/
	     
	    // Track the energy level
	    oldEnergyLevel = e.getEnergy();
		//execute();
		if (ps != null) ps.close();
	}

	/**
	 * onHitByBullet: What to do when you're hit by a bullet
	 */
	public void onHitByBullet(HitByBulletEvent e) {
		// Replace the next line with any behavior you would like
		back(10);
	}
	
	/**
	 * onHitWall: What to do when you hit a wall
	 */
	public void onHitWall(HitWallEvent e) {
		// Replace the next line with any behavior you would like
		back(20);
	}	
	
	public void onRobotDeath(RobotDeathEvent e) {
	// if the bot we were tracking died..
	
		if (e.getName().equals(enemy.getName())) {
			// clear his info, so we can track another
			enemy.reset();
		}
	
}
}
