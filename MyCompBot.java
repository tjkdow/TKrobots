package TK;
import robocode.*;
import java.awt.Color;

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
	private EnemyBot enemy = new EnemyBot();
	/**
	 * run: CompBot's default behavior
	 */

	public void run() {

		setColors(Color.red,Color.blue,Color.green); // body,gun,radar
		
		
		setAdjustRadarForRobotTurn(true);
		enemy.reset();
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
		// if we have no enemy or we found the one we're tracking..
		
		if (
			// we have no enemy, or..
			enemy.none() ||
			// the one we just spotted is closer, or..
			e.getDistance() < enemy.getDistance() - 70 ||
			// we found the one we've been tracking..
			e.getName().equals(enemy.getName())
			) {
			// track him!
			enemy.update(e);
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
		double gunTurn = getHeading() - getGunHeading() + e.getBearing();
		// normalize the turn to take the shortest path there
		setTurnGunRight(normalizeBearing(gunTurn));
	    //
		setFire(Math.min(400 / enemy.getDistance(), 3));
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
