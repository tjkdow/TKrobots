package TK;
import robocode.*;
import java.awt.Color;

// API help : http://robocode.sourceforge.net/docs/robocode/robocode/Robot.html

/**
 * EnemyBot - a robot by (your name here)
 */
public class EnemyBot {

	private double bearing;
	private double distance;
	private double energy;
	private double heading;
	private String name;
	private double velocity;
	//private EnemyBot enemy = new EnemyBot();
	private byte scanDirection = 1;
	
	public EnemyBot (){
		reset();
	}
	
	public void reset (){
		name = "";
		bearing = 0.0;
		distance = 0.0;
		energy = 0.0;
		heading = 0.0;
		velocity = 0.0;
	}
	
	public void update (ScannedRobotEvent e){
		name = e.getName();
		bearing = e.getBearing();
		distance = e.getDistance();
		energy = e.getEnergy();
		heading = e.getHeading();
		velocity = e.getVelocity();
	}
	
	public double getBearing(){
		return bearing;
	}
	
	public double getDistance(){
		return distance;
	}
	
	public double getEnergy(){
		return energy;
	}
	
	public double getHeading(){
		return heading;
	}
	
	public String getName(){
		return name;
	}
	
	public double getVelocity(){
		return velocity;
	}
		
	public boolean none (){
		boolean value;
		
		value = name.equals("");
		return value;
	}
			

	/**
	 * run: EnemyBot's default behavior
	 */
	/*
	public void run() {
		// Initialization of the robot should be put here

		// After trying out your robot, try uncommenting the import at the top,
		// and the next line:

		// setColors(Color.red,Color.blue,Color.green); // body,gun,radar

		// Robot main loop
		//setAdjustRadarForRobotTurn(true);
		enemy.reset();
		while(true) {
			double turn = getHeading() - getRadarHeading() + enemy.getBearing();
			turn += 30 * scanDirection;
			setTurnRadarRight(turn);
			scanDirection *= -1;
			execute();
		}
	}
	*/
	
	/**
	 * onScannedRobot: What to do when you see another robot
	 */
	/*
	public void onScannedRobot(ScannedRobotEvent e) {
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
	}

	public void onRobotDeath(RobotDeathEvent e) {
		// if the bot we were tracking died..
		if (e.getName().equals(enemy.getName())) {
			// clear his info, so we can track another
			enemy.reset();
		}
	}
	*/

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
	/*
	public void onHitWall(HitWallEvent e) {
		// Replace the next line with any behavior you would like
		back(20);
	}	
	*/
}
