package zerg_rush;

import java.awt.geom.Point2D;

import robocode.DeathEvent;
import robocode.Droid;
import robocode.HitByBulletEvent;
import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.MessageEvent;
import robocode.TeamRobot;
import robocode.util.Utils;

public class Puppets extends TeamRobot implements Droid {

	private final static double BULLET_POWER = 2.5D;
	
	private static int mode = -1;
	private static int movement = 600;
	
	public void run() {
		setAhead((movement=-movement)*(Math.random()+0.15));
	}

	public void onMessageReceived(MessageEvent m) {

		if (!(m.getMessage() instanceof Point2D)) {
			return;
		}
		
		Point2D p = (Point2D) m.getMessage();

		double targetX = p.getX(), targetY = p.getY();	
		double angle = Utils.normalAbsoluteAngle(Math.atan2(targetX - getX(), targetY - getY()));
		
		setTurnRightRadians(Math.cos(angle + getHeadingRadians())* (Math.abs(movement)/movement) / 240);
		setTurnGunRightRadians(Utils.normalRelativeAngle(angle - getGunHeadingRadians()));
		
		fire(BULLET_POWER);
	}

	public void onHitWall(HitWallEvent e) {
		setAhead((movement=-movement)*(Math.random()+0.15));
	}

	public void onHitByBullet(HitByBulletEvent event) {
		setAhead((movement=-movement)*(Math.random()+0.15));
	}

	public void onHitRobot(HitRobotEvent event) {
		setAhead((movement=-movement)*(Math.random()+0.15));
		if (!isTeammate(event.getName())) {
			setTurnGunRight((getHeadingRadians() + event.getBearingRadians()) - getGunHeadingRadians());
			setFire(3);
			ahead(100);
		}
	}

	public void onDeath(DeathEvent e) {
		if (getRoundNum() < 5) {
			mode = (-1)*mode;
		}
	}

}	 									
