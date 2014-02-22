
package my_robots;

import java.util.ArrayList;
import java.util.Collections;

import robocode.AdvancedRobot;
import robocode.DeathEvent;
import robocode.HitByBulletEvent;
import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.RobotDeathEvent;
import robocode.ScannedRobotEvent;
import robocode.util.Utils;

/* Desvia de balas e usa um algoritmo preditivo para atacar os inimigos. Capaz de trabalhar em equipe, mas ainda é
fraco nessa categoria. Perfeito para a categoria com um líder que indique quem atacar. :)

todo:
- Robô líder.
*/

public class Metabee extends AdvancedRobot {

	// Movimento
	private final static double MOVE_DISTANCE = 240;
	private final static double NON_RAND = 0.15;
	private final static double PREFERRED_RANGE = 160.0;
	private final static double CLOSE_FCT = 450 * MOVE_DISTANCE;

	// Canhão
	private final static int DISTANCE_OFFSET = 2;
	private final static double BULLET_POWER = 2.5D;
	private final static double BULLET_SPEED = 20.0 - 3 * BULLET_POWER;
	private final static double DISTANCE_DIVISOR = PREFERRED_RANGE;
	private final static int PREDICT_TICKS = (int) (DISTANCE_OFFSET + (DISTANCE_DIVISOR / BULLET_SPEED));
	private final static int MAX_MATCH_LEN = 30;

	// Auxiliares
	private static double enemyEnergy;
	private static double moveDir = MOVE_DISTANCE;
	private static int mode = -1;
	
	// Inimigos
	
  	// Histórico de movimentos
  	public static String enemyHistory = ""
    + (char) 0 + (char) 0 + (char) 0 + (char) 0
    + (char) 0 + (char) 0 + (char) 0 + (char) 0
    + (char) 0 + (char) 0 + (char) 0 + (char) 0
    + (char) 0 + (char) 0 + (char) 0 + (char) 0
    + (char) 0 + (char) 0 + (char) 0 + (char) 0
    + (char) 1 + (char) 2 + (char) 3 + (char) 4
    + (char) 5 + (char) 6 + (char) 7 + (char) 8
    + (char) 8 + (char) 8 + (char) 8 + (char) 8
    + (char) 8 + (char) 8 + (char) 8 + (char) 8
    + (char) 8 + (char) 8 + (char) 8 + (char) 8
    + (char) 8 + (char) 8 + (char) 8 + (char) 8
    + (char) 6 + (char) 4 + (char) 2 + (char) 0
    + (char)-1 + (char)-2 + (char)-3 + (char)-4
    + (char)-5 + (char)-6 + (char)-7 + (char)-8
    + (char)-8 + (char)-8 + (char)-8 + (char)-8
    + (char)-8 + (char)-8 + (char)-8 + (char)-8
    + (char)-8 + (char)-8 + (char)-8 + (char)-8
    + (char)-8 + (char)-8 + (char)-8 + (char)-8
    + (char)-8 + (char)-8 + (char)-8 + (char)-8
    + (char)-8 + (char)-8 + (char)-8 + (char)-8
    + (char)-8 + (char)-8 + (char)-8 + (char)-8
    + (char)-8 + (char)-8 + (char)-8 + (char)-8
    + (char)-8 + (char)-8 + (char)-8 + (char)-8
    + (char)-6 + (char)-4 + (char)-2 + (char) 0
    + (char) 0 + (char) 0 + (char) 0 + (char) 0
    + (char) 0 + (char) 0 + (char) 0 + (char) 0
    + (char) 0 + (char) 0 + (char) 0 + (char) 0
    + (char) 0 + (char) 0 + (char) 0 + (char) 0
    + (char) 0 + (char) 0 + (char) 0 + (char) 0
    + (char)-1 + (char)-2 + (char)-3 + (char)-4
    + (char)-5 + (char)-6 + (char)-7 + (char)-8
    + (char)-8 + (char)-8 + (char)-8 + (char)-8
    + (char)-8 + (char)-8 + (char)-8 + (char)-8
    + (char)-8 + (char)-8 + (char)-8 + (char)-8
    + (char)-8 + (char)-8 + (char)-8 + (char)-8
    + (char)-6 + (char)-4 + (char)-2 + (char) 0
    + (char) 0 + (char) 0 + (char) 0 + (char) 0
    + (char) 0 + (char) 0 + (char) 0 + (char) 0
    + (char) 0 + (char) 0 + (char) 0 + (char) 0
    + (char) 0 + (char) 0 + (char) 0 + (char) 0
    + (char) 0 + (char) 0 + (char) 0 + (char) 0
    + (char) 0 + (char) 0 + (char) 0 + (char) 0
    + (char) 0 + (char) 0 + (char) 0 + (char) 0
    + (char) 0 + (char) 0 + (char) 0 + (char) 0
    + (char) 0 + (char) 0 + (char) 0 + (char) 0
    + (char) 0 + (char) 0 + (char) 0 + (char) 0
    + (char) 0 + (char) 0 + (char) 0 + (char) 0
    + (char) 0 + (char) 0 + (char) 0 + (char) 0
    + (char) 0 + (char) 0 + (char) 0 + (char) 0
    + (char) 0 + (char) 0 + (char) 0 + (char) 0
    + (char) 0 + (char) 0 + (char) 0 + (char) 0
    + (char) 0 + (char) 0 + (char) 0 + (char) 0
    + (char) 0 + (char) 0 + (char) 0 + (char) 0
    + (char) 0 + (char) 0 + (char) 0 + (char) 0
    + (char) 0 + (char) 0 + (char) 0 + (char) 0
    + (char) 0 + (char) 0 + (char) 0 + (char) 0
    + (char) 0 + (char) 0 + (char) 0 + (char) 0
    + (char) 0 + (char) 0 + (char) 0 + (char) 0
    + (char) 0 + (char) 0 + (char) 0 + (char) 0
    + (char) 0 + (char) 0 + (char) 0 + (char) 0
    + (char) 0 + (char) 0 + (char) 0 + (char) 0
    + (char) 0 + (char) 0 + (char) 0 + (char) 0
    + (char) 0 + (char) 0 + (char) 0 + (char) 0
    + (char) 0 + (char) 0 + (char) 0 + (char) 0
    + (char) 0 + (char) 0 + (char) 0 + (char) 0
    + (char) 0 + (char) 0 + (char) 0 + (char) 0
    + (char) 0 + (char) 0 + (char) 0 + (char) 0
    + (char) 0 + (char) 0 + (char) 0 + (char) 0
    + (char) 0 + (char) 0 + (char) 0 + (char) 0
    + (char) 0 + (char) 0 + (char) 0 + (char) 0
    + (char) 0 + (char) 0 + (char) 0 + (char) 0
    + (char) 0 + (char) 0 + (char) 0 + (char) 0
    + (char) 0 + (char) 0 + (char) 0 + (char) 0
    + (char) 0 + (char) 0 + (char) 0 + (char) 0
    + (char) 0 + (char) 0 + (char) 0 + (char) 0
    + (char) 0 + (char) 0 + (char) 0 + (char) 0
    + (char) 0 + (char) 0 + (char) 0 + (char) 0
    + (char) 0 + (char) 0 + (char) 0 + (char) 0
    + (char) 0 + (char) 0 + (char) 0 + (char) 0
    + (char) 0 + (char) 0 + (char) 0 + (char) 0
    + (char) 0 + (char) 0 + (char) 0 + (char) 0
    + (char) 0 + (char) 0 + (char) 0 + (char) 0
    + (char) 0 + (char) 0 + (char) 0 + (char) 0
    + (char) 0 + (char) 0 + (char) 0 + (char) 0
    + (char) 0 + (char) 0 + (char) 0 + (char) 0
    + (char) 0 + (char) 0 + (char) 0 + (char) 0
    + (char) 0 + (char) 0 + (char) 0 + (char) 0
    + (char) 0 + (char) 0 + (char) 0 + (char) 0
    + (char) 0 + (char) 0 + (char) 0 + (char) 0
    + (char) 0 + (char) 0 + (char) 0 + (char) 0
    + (char) 0 + (char) 0 + (char) 0 + (char) 0
    + (char) 0 + (char) 0 + (char) 0 + (char) 0
    + (char) 0 + (char) 0 + (char) 0 + (char) 0
    + (char) 0 + (char) 0 + (char) 0 + (char) 0
    + (char) 0 + (char) 0 + (char) 0 + (char) 0
    + (char) 0 + (char) 0 + (char) 0 + (char) 0
    + (char) 0 + (char) 0 + (char) 0 + (char) 0
    + (char) 0 + (char) 0 + (char) 0 + (char) 0
    + (char) 0 + (char) 0 + (char) 0 + (char) 0
    + (char) 0 + (char) 0 + (char) 0 + (char) 0
    + (char) 0 + (char) 0 + (char) 0 + (char) 0
    + (char) 0 + (char) 0 + (char) 0 + (char) 0
    + (char) 0 + (char) 0 + (char) 0 + (char) 0
    + (char) 0 + (char) 0 + (char) 0 + (char) 0
    + (char) 0 + (char) 0 + (char) 0 + (char) 0
    + (char) 0 + (char) 0 + (char) 0 + (char) 0
    + (char) 0 + (char) 0 + (char) 0 + (char) 0;
  	
  	String enemiesHistory[] = {enemyHistory, enemyHistory, enemyHistory, enemyHistory, enemyHistory}; // Novo
  	ArrayList<String> enemyNames = new ArrayList<String>(); // Novo
  	int next = 0;
  	
  	public boolean visto(String name) {
  		boolean r = false;
  		
  		for (int i = 0; i < next; i++) {
  			if (enemyNames.get(i).equals(name)) {
  				return true;
  			}
  		}
  		
  		return r;
  	}
  	
	public void run() {
		setAdjustGunForRobotTurn(true);
		turnRadarRightRadians(Double.POSITIVE_INFINITY);
	}

	public void onScannedRobot(ScannedRobotEvent e) {

		if (e.getName().contains("Metabee")) {
			mode = (-1)*mode;
			return;
		} else if (!visto(e.getName())) {
			enemyNames.add(e.getName());
			next++;
			Collections.sort(enemyNames);
			Collections.reverse(enemyNames);
		}

		if (!e.getName().equals(enemyNames.get(0))) {
			return;
		}
		
		int ri = MAX_MATCH_LEN;
		double rd = e.getBearingRadians();

		int matchPos;

		setTurnRadarLeftRadians(getRadarTurnRemaining());

		if (enemyEnergy > (enemyEnergy = e.getEnergy())) {
			setAhead((moveDir *= mode) * (Math.random() + NON_RAND));
		}

		setFire(BULLET_POWER);

		// turn perpendicular with range control
		setTurnRightRadians(Math.cos(rd - (e.getDistance() - PREFERRED_RANGE)* moveDir / CLOSE_FCT));

		// rd has enemy relative bearing
		rd += getHeadingRadians();

		// pattern gun from WeekendOnsession by Eric Simonton
		// add last enemy move to the pattern
		enemyHistory = String.valueOf((char) (e.getVelocity() * (Math.sin(e.getHeadingRadians() - rd)))).concat(enemyHistory);

		// search for a match
		while ((matchPos = enemyHistory.indexOf(enemyHistory.substring(0, ri--), PREDICT_TICKS)) < 0);

		// calculate aim offset
		ri = PREDICT_TICKS;
		do {
			rd += ((short) enemyHistory.charAt(--matchPos))/(DISTANCE_DIVISOR);
		} while (--ri > 0);

		// turn gun
		setTurnGunRightRadians(Utils.normalRelativeAngle(rd - getGunHeadingRadians()));
	}

	public void onHitWall(HitWallEvent e) {
		setAhead(moveDir = -moveDir);
	}

	public void onHitByBullet(HitByBulletEvent event) {
		mode = -mode;
	}
	
	public void onRobotDeath(RobotDeathEvent e) {
		enemyNames.remove(enemyNames.indexOf(e.getName()));
	}
	
	public void onHitRobot(HitRobotEvent event) {
		setAhead(moveDir = -moveDir);
	}
	
	public void onDeath(DeathEvent e) {
		if (getRoundNum() < 5) {
			mode = (-1)*mode;
		}
	}
	
}	 									
