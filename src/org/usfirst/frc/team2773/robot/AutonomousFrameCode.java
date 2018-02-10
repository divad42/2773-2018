package org.usfirst.frc.team2773.robot;
/**
 * 
 * @author Mr.Mamsy
 *
 */
public class AutonomousFrameCode{
	
	static String sides; // == from GSM
	
	static char startPos; // == L R or C //TODO also sendable value
	static boolean doSwitch = true; //TODO Change this to sendable value later on!!!!!!
	static int waitTime; //Another sendable function 

	
	static char switchSide = sides.charAt(0);
	static char scaleSide = sides.charAt(1);
	
	
	static void defaultExecution() 
	{
		
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
	
	static void switchFrmSide(char s) {
		//goForward a bit
		// turn slightly toward s
		if(s == 'L')
		{}
		else 
		{/*right code turn*/}
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
