package my_robots;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

import robocode.DeathEvent;
import robocode.HitByBulletEvent;
import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.RobotDeathEvent;
import robocode.Rules;
import robocode.ScannedRobotEvent;
import robocode.TeamRobot;
import robocode.util.Utils;

public class Master extends TeamRobot {

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
	ArrayList<String> enemyNames = new ArrayList<String>();
	
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

	/* Indica se o robô detectado já foi visto. */
	public boolean visto(String name) {
		boolean r = false;

		for (int i = 0; i < enemyNames.size(); i++) {
			if (enemyNames.get(i).equals(name)) {
				return true;
			}
		}

		return r;
	}

	public void run() {
		setBodyColor(Color.black);
		setGunColor(Color.black);
		setRadarColor(Color.black);
		setScanColor(Color.black);
		setBulletColor(Color.white);

		setAdjustRadarForGunTurn(true);

		while (true) {
			if (getRadarTurnRemaining() == 0.0)
				setTurnRadarRightRadians(Double.POSITIVE_INFINITY);
			execute();
		}
	}

	public void onScannedRobot(ScannedRobotEvent e) {
	
		/*Verifica se é um aliado*/
		if (isTeammate(e.getName())) {
			return;
		} else if (!visto(e.getName())) { //Se não foi visto anteriormente, adiciona em ordem reversa à lista de inimigos
			enemyNames.add(e.getName());
			Collections.sort(enemyNames);
			Collections.reverse(enemyNames);
		}

		if (!e.getName().equals(enemyNames.get(0))) {
			return;
		}
		
		// Comportamento próprio do Master
		/*-------------------------------------------------------------------------------------------------------*/
		int ri = MAX_MATCH_LEN; /*Usado pra detectar os padrões de movimento*/
		double rd = e.getBearingRadians();
		int matchPos;

		setTurnRadarLeftRadians(getRadarTurnRemaining());
	
		/*Verifica se o inimigo está mais fraco e se aproxima dele em caso positivo*/
		if (enemyEnergy > (enemyEnergy = e.getEnergy())) {
			setAhead((moveDir *= mode) * (Math.random() + NON_RAND));
		}
		setFire(BULLET_POWER);

		// turn perpendicular with range control
		setTurnRightRadians(Math.cos(rd - (e.getDistance() - PREFERRED_RANGE)* moveDir / CLOSE_FCT));

		// rd has enemy relative bearing
		rd += getHeadingRadians();

		/*Adiciona o movimento mais recente do inimigo ao histórico*/
		enemyHistory = String.valueOf((char) (e.getVelocity() * (Math.sin(e.getHeadingRadians() - rd)))).concat(enemyHistory);

		/*Procura por um padrão*/
		while ((matchPos = enemyHistory.indexOf(enemyHistory.substring(0, ri--), PREDICT_TICKS)) < 0);

		/*Calcula a variação que deve ser feita na mira, i.e., prevê os próximos movimentos do inimigo.*/
		ri = PREDICT_TICKS;
		do {
			rd += ((short) enemyHistory.charAt(--matchPos))/(DISTANCE_DIVISOR);
		} while (--ri > 0);

		setTurnGunRightRadians(Utils.normalRelativeAngle(rd - getGunHeadingRadians()));
		/*-------------------------------------------------------------------------------------------------------*/
		
		// Complementar: calcula a posição do inimigo para enviar para os aliados.
		/*-------------------------------------------------------------------------------------------------------*/
		double angleToEnemy = getHeadingRadians() + e.getBearingRadians();
		double radarTurn = Utils.normalRelativeAngle(angleToEnemy - getRadarHeadingRadians());
		double extraTurn = Math.min(Math.atan(5.0 / e.getDistance()), Rules.RADAR_TURN_RATE_RADIANS);

		radarTurn += (radarTurn < 0 ? -extraTurn : extraTurn);

		setTurnRadarRightRadians(radarTurn);
		setTurnGunRightRadians(getRadarHeadingRadians()
				- getGunHeadingRadians());

		double bulletPower = Math.min(3.0, getEnergy());
		double myX = getX();
		double myY = getY();
		double absoluteBearing = getHeadingRadians() + e.getBearingRadians();
		double enemyX = myX + e.getDistance() * Math.sin(absoluteBearing);
		double enemyY = myY + e.getDistance() * Math.cos(absoluteBearing);
		double enemyHeading = e.getHeadingRadians();
		double enemyVelocity = e.getVelocity();

		double deltaTime = 0;
		double battleFieldHeight = getBattleFieldHeight(), battleFieldWidth = getBattleFieldWidth();
		double predictedX = enemyX, predictedY = enemyY;
		
		while ((++deltaTime) * (20.0 - 3.0 * bulletPower) < Point2D.Double.distance(myX, myY, predictedX, predictedY)) {
			
			predictedX += Math.sin(enemyHeading) * enemyVelocity;
			predictedY += Math.cos(enemyHeading) * enemyVelocity;
			
			if (predictedX < 18.0 || predictedY < 18.0 || predictedX > battleFieldWidth - 18.0 || predictedY > battleFieldHeight - 18.0) {
				
				predictedX = Math.min(Math.max(18.0, predictedX), battleFieldWidth - 18.0);
				predictedY = Math.min(Math.max(18.0, predictedY), battleFieldHeight - 18.0);
				break;
			}
		
		}

		double theta = Utils.normalAbsoluteAngle(Math.atan2(predictedX - getX(), predictedY - getY()));
		
		setTurnRadarRightRadians(Utils.normalRelativeAngle(absoluteBearing - getRadarHeadingRadians()));
		setTurnGunRightRadians(Utils.normalRelativeAngle(theta - getGunHeadingRadians()));
		fire(bulletPower);
		/*-------------------------------------------------------------------------------------------------------*/
		
		try {
			Point2D lugar = new Point2D.Double(predictedX, predictedY);
			broadcastMessage((Serializable) lugar);
		} catch (IOException ignored) {}
		
	}

	/* Se algum inimigo morreu, o remove da lista */
	public void onRobotDeath(RobotDeathEvent e) {
		enemyNames.remove(enemyNames.indexOf(e.getName()));
	}
	
	/* Trata os choques com a parede */
	public void onHitWall(HitWallEvent e) {
		setAhead(moveDir = -moveDir);
	}
	
	/* Trata os choques com balas */
	public void onHitByBullet(HitByBulletEvent event) {
		setAhead(moveDir = -moveDir);
	}
	
	/* Trata os choques com outros robôs */
	public void onHitRobot(HitRobotEvent event) {
		setAhead(moveDir = -moveDir);
	}
	
	/* Muda estratégia caso tenha morrido muito na primeira metade da competição */
	public void onDeath(DeathEvent e) {
		if (getRoundNum() < 5) {
			mode = (-1)*mode;
		}
	}

}	 									
