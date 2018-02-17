package org.usfirst.frc.team2773.robot;

import edu.wpi.first.wpilibj.DriverStation;

/**
 * 
 * @author Mr.Mamsy
 *
 */
public class AutonomousFrameCode{
	static DriverStation ds; 
	static String sides;
	
	static char startPos; // == L R or C //TODO also sendable value
	static boolean doSwitch = true; //TODO Change this to sendable value later on!!!!!!
	static int waitTime; //Another sendable function 

	
	static char switchSide;
	static char scaleSide;
	
	
	static void defaultExecution() 
	{
		ds.getInstance();
		sides = ds.getGameSpecificMessage();
		switchSide = sides.charAt(0);
		sides.charAt(1;)
		
		
		if(doSwitch)
		{
			if(startPos == switchSide)
				switchFrmSide(startPos);
			else if(startPos == 'C')
				switchFrmCenter(waitTime, switchSide);
		}
		
		else 
		{
			if(startPos == scaleSide)
				scaleFrmSide(startPos);
			else if(startPos == 'C')
				scaleFrmCenter(waitTime, scaleSide);
		}
		
		
	}
	
	static void switchProc(char side, char start) {
		//goForward a bit
		// turn slightly toward s
		if(start == 'L')
		{/*left side code switchFromLeft(start, side)*/}
		else if(start == 'R') 
		{/*right code turn swtichFromRight(start, side)*/}
		else
			switchFrmCenter();
		findTape();
		placeCubeSwtich();
		
	}
	
	
	static void scaleFrmSide(char s) {}
	
	//Make sure you enter the target side as S
	static void switchFrmCenter(int t, char s) {
		waitForAlliance(t, s);
		switchFrmSide(s);
	}
	
	//Make sure you enter the target side as S
	static void scaleFrmCenter(int t, char s) {
		waitForAlliance(t,s);
		scaleFrmSide(s);
	}
	
	static void findTape() {
		//locate two rectangles
		//determine distance
		//center rectangles
		//moveCloser until they're large enough portion of frame
	}
	
	static void waitForAlliance(int t, char s) {
		//freeze for t
		changeStartPosition(s);
	}
	
	static void changeStartPosition(char s) {
		//must be called from center
		//move towards S declared side, L or R
		startPos = s;
		
	}
	
	static void noJobSadRobot(int t, char s) {
		waitForAlliance(t,s);
		//move beyond the scoreLine
	}
	
	static void placeCubeSwtich() {
		//claw is presumed closed
		//lift claw
		//move forward
		//expand claw
	}
	 
	
}
