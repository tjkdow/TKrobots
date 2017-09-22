package TK;
import robocode.*;
import java.awt.Color;
import java.awt.geom.Point2D;

// API help : http://robocode.sourceforge.net/docs/robocode/robocode/Robot.html

/**
 * TonyBot - a robot by (Tony Kim)
 */
public class TonyBot extends AdvancedRobot
{
	private byte scanDirection = 1;
	private AdvancedEnemyBot enemy = new AdvancedEnemyBot();

	public void run() {
		setColors(Color.red,Color.blue,Color.green); // body,gun,radar
		setAdjustRadarForRobotTurn(true);
		setAdjustGunForRobotTurn(true);
		enemy.reset();
		while (true) {
			setTurnRadarRight(360);
			doScanner();
			doGun();
			doMovement();
			execute();
		}
	}


	public void onScannedRobot(ScannedRobotEvent e) {
		scanDirection *= -1; // changes value from 1 to -1
		setTurnRadarRight(360 * scanDirection);
		
		if (
			// we have no enemy, or..
			enemy.none() ||
			// the one we just spotted is closer, or..
			e.getDistance() < enemy.getDistance() - 70 ||
			// we found the one we've been tracking..
			e.getName().equals(enemy.getName())
			) {
			// track him!
			enemy.update(e, this);
		}	

	}

	void doScanner() {
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

	void doGun() {
		// calculate firepower based on distance
		double firePower = Math.min(500 / enemy.getDistance(), 3);
		// calculate speed of bullet
		double bulletSpeed = 20 - firePower * 3;
		// distance = rate * time, solved for time
		long time = (long)(enemy.getDistance() / bulletSpeed);
		//  calculate gun turn toward enemy
		//double turn = getHeading() - getGunHeading() + enemy.getBearing();			// normalize the turn to take the shortest path there
		// calculate gun turn to predicted x,y location
		double futureX = enemy.getFutureX(time);
		double futureY = enemy.getFutureY(time);
		double absDeg = absoluteBearing(getX(), getY(), futureX, futureY);
		// turn the gun to the predicted x,y location
		setTurnGunRight(normalizeBearing(absDeg - getGunHeading()));
		// if the gun is cool and we're pointed at the target, shoot!
		if (getGunHeat() == 0 && Math.abs(getGunTurnRemaining()) < 10)
			setFire(firePower);
		}
	
	void doMovement() {
		setTurnRight(enemy.getBearing());
		// move a little closer
		if (enemy.getDistance() > 200)
			setAhead(enemy.getDistance() / 2);
		// but not too close
		if (enemy.getDistance() < 100)
			setBack(enemy.getDistance());
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

	public void onHitByBullet(HitByBulletEvent e) {

		back(10);
	}
	

	public void onHitWall(HitWallEvent e) {

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
