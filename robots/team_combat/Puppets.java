package my_robots;

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
	
	// Gerenciamento da munição
	private final static double BULLET_POWER = 2.5D;
	
	// Movimento
	private static int mode = -1;
	private static int movement = 600;

	public void run() {
		setAhead((movement=-movement)*(Math.random()+0.15)); // Se move aleatoriamente
	}

	public void onMessageReceived(MessageEvent m) {

		if (!(m.getMessage() instanceof Point2D)) {
			return;
		}
		
		/* Mira na direção da posição indicada pelo Master */
		
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

	/* Caso bata em algum robô que não seja aliado, atira nele. */
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
