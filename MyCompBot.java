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
	//public EnemyBot enemy = new EnemyBot;
	/**
	 * run: CompBot's default behavior
	 */

	public void run() {

		setColors(Color.red,Color.blue,Color.green); // body,gun,radar
		
		setAdjustRadarForRobotTurn(true);


		while(true) {


			//turnGunRight(99999);
			turnRadarRight(360);
		}
	}

	/**
	 * onScannedRobot: What to do when you see another robot
	 */
	public void onScannedRobot(ScannedRobotEvent e) {
		setTurnRight(e.getBearing()+90-30*toggleDirection);
		scanDirection *= -1; // change value from 1 to -1
		setTurnRadarRight(360 * scanDirection);
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
		setTurnGunRight(getHeading() - getGunHeading() + e.getBearing());
	    //
	    // Fire directly at target
	    fire ( 2 ) ;
		/*
		if (event.getDistance() < 100) {
	    	fire(3);
	    } else {
	        fire(1);
	    }
		*/
	     
	    // Track the energy level
	    oldEnergyLevel = e.getEnergy();
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
}
