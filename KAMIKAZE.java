package KAMIZAKE2_0;
import robocode.*;
import java.awt.Color;

// API help : http://robocode.sourceforge.net/docs/robocode/robocode/Robot.html

/**
 * KAMIKAZE - a robot by (Nícolas Oliveira(nogn), Rafael de Melo(rmfm), Joao Veras (jhgv), Lucas Nunes(lns2), George H.(ghao), Natanael Souza(nss));
 */
public class KAMIKAZE extends Robot
{
	/**
	 * run: KAMIKAZE's default behavior
	 */
	public void run() {
		// Initialization of the robot should be put here

		// After trying out your robot, try uncommenting the import at the top,
		// and the next line:

		setColors(Color.red,Color.black,Color.green); // body,gun,radar
		setBulletColor(Color.red);
		// Robot main loop
		while(true) {
			// Replace the next 4 lines with any behavior you would like
			turnRadarRight(360);
		
		}
	}

	/**
	 * onScannedRobot: What to do when you see another robot
	 */
	public void onScannedRobot(ScannedRobotEvent e) {
		double ang = e.getBearing();
		turnRight(ang);

		double dist = e.getDistance(); 
		fire(500/dist);				
		ahead(dist/7);
		

	}

	/**
	 * onHitByBullet: What to do when you're hit by a bullet
	 */
	public void onHitByBullet(HitByBulletEvent e) {
		// Replace the next line with any behavior you would like

	}
	
	/**
	 * onHitWall: What to do when you hit a wall
	 */
	public void onHitWall(HitWallEvent e) {
		// Replace the next line with any behavior you would like
		back(20);
	}	
	
	public void onRobotDeath(RobotDeathEvent e){
		out.println("Alguém morreu!!!");
		
		ahead(100);
		back(100);
	}





}
