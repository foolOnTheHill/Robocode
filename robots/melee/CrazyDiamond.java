package melee;

import java.awt.Color;

import robocode.AdvancedRobot;
import robocode.HitByBulletEvent;
import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.RobotDeathEvent;
import robocode.ScannedRobotEvent;

/*Usa movimento anti-gravidade (Anti-gravity movement) 
conforme descrito em: http://www.ibm.com/developerworks/java/library/j-antigrav/

Perfeito para a categoria melee, i.e., 5vs5, do projeto. :)

todo:

- Adicionar movimento que evite colisões com as paredes.*/

public class CrazyDiamond extends AdvancedRobot {
	
	static final double BASE_MOVEMENT = 180;
	static final double GUN_FACTOR = 500; 
	static final double BASE_TURN = Math.PI / 2;
	static final double BASE_CANNON_POWER = 20;

	static double movement;
	static double lastHeading;
	static String lastTarget;
	static double lastDistance;

	public void run() {
		setColors(Color.white, Color.blue, Color.red);
		setAdjustGunForRobotTurn(true);

		movement = 240;

		onRobotDeath(null);

		turnRadarRight(Double.POSITIVE_INFINITY);
	}

	public void onHitWall(HitWallEvent e) {
		if (Math.abs(movement) > BASE_MOVEMENT) {
			setAhead(movement = BASE_MOVEMENT);
		}
	}

	public void onRobotDeath(RobotDeathEvent e) {
		lastDistance = Double.POSITIVE_INFINITY;
	}

	public void onHitRobot(HitRobotEvent e) {
		if (Math.abs(movement) > BASE_MOVEMENT) {
			setAhead(movement = BASE_MOVEMENT);
		}
	}
	
	public void onHitByBullet(HitByBulletEvent e) {
		if (Math.abs(movement) > BASE_MOVEMENT) {
			setAhead(movement = BASE_MOVEMENT);
		}
	}
	
	public void onScannedRobot(ScannedRobotEvent e) {
		
		if (e.getName().contains("CrazyDiamond")) {
			movement = (-1)*movement;
			return;
		}
		
		double absoluteBearing = e.getDistance();

		if (getDistanceRemaining() == 0) {
			setAhead((movement=-movement)*(Math.random()+0.15));
			setTurnRightRadians(Math.cos(e.getBearing() - (e.getDistance() - 160.0)* (Math.abs(movement)/movement) / 240));
		}

		if (lastDistance > absoluteBearing) {
			lastDistance = absoluteBearing;
			lastTarget = e.getName();
		}

		if (lastTarget == e.getName()) {
			
			if (getGunHeat() < 1 && absoluteBearing < GUN_FACTOR) {
				
				if (getGunHeat() == getGunTurnRemaining()) {
					
					setFireBullet(getEnergy() * BASE_CANNON_POWER / absoluteBearing);
					
					onRobotDeath(null);
				}

				setTurnRadarLeft(getRadarTurnRemaining());
			}

			absoluteBearing = e.getBearingRadians() + getHeadingRadians();

			setTurnGunRightRadians(Math.asin(Math.sin(absoluteBearing - getGunHeadingRadians() + (1 - e.getDistance() / 500) * Math.asin(e.getVelocity() / 11) * Math.sin(e.getHeadingRadians() - absoluteBearing))));
		}
		
	}
}
