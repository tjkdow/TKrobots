package TK;
import robocode.*;
//import java.awt.Color;

// API help : http://robocode.sourceforge.net/docs/robocode/robocode/Robot.html

/**
 * MyBearingBot - a robot by (TonyKim)
 */
public class MyBearingBot extends Robot
{
	/**
	 * run: MyBearingBot's default behavior
	 */
	public void run() {
		// Initialization of the robot should be put here

		// After trying out your robot, try uncommenting the import at the top,
		// and the next line:

		// setColors(Color.red,Color.blue,Color.green); // body,gun,radar

		// Robot main loop
		while(true) {
			// Replace the next 4 lines with any behavior you would like
			/* default moving
			ahead(100);
			turnGunRight(360);
			back(100);
			turnGunRight(360);
			*/
			turnRadarRight(360);
		}
	}

	/**
	 * onScannedRobot: What to do when you see another robot
	 */
	public void onScannedRobot(ScannedRobotEvent e) {
		// Replace the next line with any behavior you would like
		if(e.getBearing()>=0){
			turnRight(e.getBearing());
		}
		else if(e.getBearing()<0){
			turnLeft(e.getBearing());
		}
		
		fire(1);
		ahead(e.getDistance());
	}

	/**
	 * onHitByBullet: What to do when you're hit by a bullet
	 */
	/*
	public void onHitByBullet(HitByBulletEvent e) {
		// Replace the next line with any behavior you would like
		back(10);
	}
	*/
	/**
	 * onHitWall: What to do when you hit a wall
	 */
	public void onHitWall(HitWallEvent e) {
		// Replace the next line with any behavior you would like
		back(20);
	}	
}
