package TK;
import robocode.*;
import java.awt.Color;

// API help : http://robocode.sourceforge.net/docs/robocode/robocode/Robot.html

/**
 * TonyBot - a robot by (Tony Kim)
 */
public class TonyBot extends AdvancedRobot
{
	private byte scanDirection = 1;
	private EnemyBot enemy = new EnemyBot();

	public void run() {
		setColors(Color.red,Color.blue,Color.green); // body,gun,radar
		setAdjustRadarForRobotTurn(true);
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
			enemy.update(e);
		}
	}

	void doScanner() {
		double turn = getHeading() - getRadarHeading() + enemy.getBearing();
		turn += 30 * scanDirection;
		setTurnRadarRight(turn);
		scanDirection *= -1;
	}

	void doGun() {
		// don't fire if there's no enemy
		if (enemy.none()) return;
		// convenience variable
		double max = Math.max(getBattleFieldHeight(), getBattleFieldWidth());
		// only shoot if we're (close to) pointing at our enemy
		if (Math.abs(getTurnRemaining()) < 10) {
			if (enemy.getDistance() < max / 3) {
				// fire hard when close
				setFire(3);
			} else {
				// otherwise, just plink him
				setFire(1);
			}
		}
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
