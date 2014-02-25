package my_robots;

import java.awt.Color;

import robocode.AdvancedRobot;
import robocode.HitByBulletEvent;
import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.RobotDeathEvent;
import robocode.ScannedRobotEvent;

/*Usa movimento anti-gravidade (Anti-gravity movement) 
conforme descrito em: http://www.ibm.com/developerworks/java/library/j-antigrav/
e o algoritmo de mira descrito em: http://robowiki.net/wiki/Linear_Targeting .*/

public class CrazyDiamond extends AdvancedRobot {
	
	// Constantes
	static final double BASE_MOVEMENT = 180;
	static final double GUN_FACTOR = 500; 
	static final double BASE_TURN = Math.PI / 2;
	static final double BASE_CANNON_POWER = 20;

	// Auxiliares p/ a estratégia
	static double movement; // Distância máxima que se move
	static String lastTarget; // Último inimigo levado em consideração
	
	// Histórico do inimigo.
	static double lastHeading;
	static double lastDistance;
	
	public void run() {
		setColors(Color.white, Color.blue, Color.red);
		setAdjustGunForRobotTurn(true);
		
		movement = 240;
		
		onRobotDeath(null);
		turnRadarRight(Double.POSITIVE_INFINITY); // Gira o radar para detectar algum inimigo
	}

	/* Trata as colisões com a parede */
	public void onHitWall(HitWallEvent e) {
		if (Math.abs(movement) > BASE_MOVEMENT) {
			setAhead(movement = BASE_MOVEMENT);
		}
	}
	
	/* Caso algum robô tenha morrido, altera o histórico */
	public void onRobotDeath(RobotDeathEvent e) {
		lastDistance = Double.POSITIVE_INFINITY;
	}

	/* Trata colisões com outros robôs */
	public void onHitRobot(HitRobotEvent e) {
		if (Math.abs(movement) > BASE_MOVEMENT) {
			setAhead(movement = BASE_MOVEMENT);
		}
	}
	
	/* Trata colisões com balas */
	public void onHitByBullet(HitByBulletEvent e) {
		if (Math.abs(movement) > BASE_MOVEMENT) {
			setAhead(movement = BASE_MOVEMENT);
		}
	}
	
	public void onScannedRobot(ScannedRobotEvent e) {
		
		/* Verifica se é um aliado */
		if (e.getName().contains("CrazyDiamond")) {
			movement = (-1)*movement;
			return;
		}
		
		/* Verifica se o robô detectado está mais próximo que o inimigo levado em consideração atualmente. */
		double absoluteBearing = e.getDistance();

		if (getDistanceRemaining() == 0) {
			setAhead((movement=-movement)*(Math.random()+0.15));
			setTurnRightRadians(Math.cos(e.getBearing() - (e.getDistance() - 160.0)* (Math.abs(movement)/movement) / 240));
		}

		if (lastDistance > absoluteBearing) {
			lastDistance = absoluteBearing;
			lastTarget = e.getName();
		}
		
		/* Verifica se é o inimigo atual */
		if (lastTarget == e.getName()) {
			
			if (getGunHeat() < 1 && absoluteBearing < GUN_FACTOR) { /* Gerencia o aquecimento do canhão */
				
				if (getGunHeat() == getGunTurnRemaining()) {
					
					setFireBullet(getEnergy() * BASE_CANNON_POWER / absoluteBearing); // Gerenciamento da munição
					
					onRobotDeath(null);
				}

				setTurnRadarLeft(getRadarTurnRemaining());
			}
			
			// Mira do tipo linear
			absoluteBearing = e.getBearingRadians() + getHeadingRadians();
			setTurnGunRightRadians(Math.asin(Math.sin(absoluteBearing - getGunHeadingRadians() + (1 - e.getDistance() / 500) * Math.asin(e.getVelocity() / 11) * Math.sin(e.getHeadingRadians() - absoluteBearing))));
		}
		
	}
}
