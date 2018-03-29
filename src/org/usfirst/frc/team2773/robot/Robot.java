// Version 1.1.0
// Simplified all autonomous code using driveDist method, tested four bar finally

package org.usfirst.frc.team2773.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.drive.MecanumDrive;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.CameraServer;
//import org.opencv.videoio.VideoCapture;
//import org.opencv.core.Mat;


/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.properties file in the
 * project.
 */
public class Robot extends TimedRobot {
	public final double TILE_DISTANCE_RATE_COMPBOT = 330.861363636; // in degrees per foot
	public final double TILE_DISTANCE_RATE_PRACTICEBOT = 25.0 / 3;
	public final double COMP_DISTANCE_RATE = 1;
	public final double TILE_ROTATION_RATE_PRACTICEBOT = 1;
	public static double rateY; // = TILE or COMP rate
	public static double rateX;
	public static double rateRot;
	public static double degRate;

	public SendableChooser<Character> startLoc;
	public SendableChooser<Character> targetPos;
	public SendableChooser<Integer> objectiveChoice;

	public char startChar;
	public char targetChar;
	public int objectInt;
	public boolean isSleep;

	// Vect- I mean Victors
	public Victor FL;
	public Victor FR;
	public Victor BL;
	public Victor BR;
	public MecanumDrive drive;

	// Super Ultra Important Epic Awesome One-Of-A-Kind Exclusive Encoders
	static public Encoder FLE;
	static public Encoder FRE;
	static public Encoder BLE;
	static public Encoder BRE;

	public Spark upperBar;
	public Spark lowerBar;
	public Spark lowerBox;

	public boolean goDown;

	public int fakeEncoderUp;
	public int fakeEncoderDown;

	public Joystick gamepad;
	public Joystick stick;

	public double distance;
	public int autoStep;

	// Helps control the speed of the driver method
	public double curXVel;
	public double curYVel;
	public double curRot;
	public double accel;

	public double maxSpeed;
	public static double maxBoth;
	public static double maxDown;
	public static double minBoth;
	public static double minDown;

	public Spark grabL;
	public Spark grabR;

	public boolean barMode;
	public boolean barModePressed;
	public boolean articulating;
	
	public boolean cubeMode;
	public boolean cubeModePressed;
	
	public DigitalInput lowMin;
	public DigitalInput upMin;

	// public Spark wench;

	public Timer timer;
	int i;
	public CameraServer cameras;
	
	 

	/**
	 * This function is run when the robot is first started up and should be used
	 * for any initialization code.
	 */
	@Override
	public void robotInit() {

		rateY = TILE_DISTANCE_RATE_PRACTICEBOT;
		rateRot = TILE_ROTATION_RATE_PRACTICEBOT;
		rateX = 2;

		// Wheel and Drive Objects
		FL = new Victor(3);
		FR = new Victor(2);
		BL = new Victor(1);
		BR = new Victor(0);
		drive = new MecanumDrive(FL, BL, FR, BR);

		// Encoder Declaration
		FLE = new Encoder(2, 3);
		FRE = new Encoder(0, 1);
		BLE = new Encoder(6, 7);
		BRE = new Encoder(4, 5);

		BLE.setReverseDirection(true);
		FLE.setReverseDirection(true);

		// Grabber and Related Data
		grabR = new Spark(7);
		grabL = new Spark(5);
		// grabL.setInverted(true);

		// 4 Bar parts being declared
		lowerBar = new Spark(6);
		lowerBar.setInverted(false);

		upperBar = new Spark(8);
		upperBar.setInverted(true);
		
		goDown = false;

		fakeEncoderUp = 0;
		fakeEncoderDown = 0;

		barMode = false;
		barModePressed = false;

		cubeMode = true;
		cubeModePressed = false;
		
		// Climber related data
		// wench = new Spark(4);

		// Constraints
		maxBoth = 10000;
		minBoth = -10000;
		maxDown = 10000;
		minDown = -10000;

		// Controller objects
		gamepad = new Joystick(0);
		stick = new Joystick(1);

		// Drive Variables
		curXVel = 0.0;
		curYVel = 0.0;
		curRot = 0.0;
		accel = 0.03;
		
		// Limit Switches
		lowMin = new DigitalInput(8);
		upMin = new DigitalInput(9);

		// the radio buttons for selecting our starting position
		startLoc = new SendableChooser<>();
		startLoc.addDefault("Center", new Character('C'));
		startLoc.addObject("Left", new Character('L'));
		startLoc.addObject("Right", new Character('R'));
		SmartDashboard.putData("Starting Positions", startLoc);

		autoStep = 0;

		/*
		 * THERE SHOULD BE NO Target Position Radio Buttons in SmartDashboard targetPos
		 * = new SendableChooser<>(); targetPos.addDefault("Left", new Character('L'));
		 * targetPos.addObject("Right", new Character('R'));
		 * SmartDashboard.putData("Target Position", targetPos);
		 */

		// Selecting Objective with RadioButtons in SmartDashboard
		objectiveChoice = new SendableChooser<>();
		objectiveChoice.addDefault("Switch", new Integer(0));
		objectiveChoice.addObject("Scale", new Integer(1));
		objectiveChoice.addObject("Baseline", new Integer(2));
		SmartDashboard.putData("Target Objective", objectiveChoice);

		timer = new Timer();

		// Declares the cameras and sets their resolution to their respective maxima
		// "startAutomaticCapture" creates a "UsbCamera" object, on which we can call
		// the "setResolution" method.
		cameras = CameraServer.getInstance();
		cameras.startAutomaticCapture(0).setResolution(1280, 720);
		cameras.startAutomaticCapture(1).setResolution(1280, 720);
		
		
		// vision
		
		/*GripPipeline pipe = new GripPipeline();
		Mat m  = new Mat();
		cameras.getVideo().grabFrame(m);*/
		//VideoCapture cap = new VideoCapture();
		
		
		/*pipe.process(m);
		System.out.println("VISION TEST VAR: PREPARE FOR LONG OUTPUT \n" + pipe.filterContoursOutput());*/
		
	}

	/**
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable chooser
	 * code works with the Java SmartDashboard. If you prefer the LabVIEW Dashboard,
	 * remove all of the chooser code and uncomment the getString line to get the
	 * auto name from the text box below the Gyro
	 *
	 * <p>
	 * You can add additional auto modes by adding additional comparisons to the
	 * switch structure below with additional strings. If using the SendableChooser
	 * make sure to add them to the chooser code above as well.
	 */
	@Override
	public void autonomousInit() {
		startChar = startLoc.getSelected(); // the starting position
		objectInt = objectiveChoice.getSelected().intValue(); // whether we want the switch or scale
		targetChar = DriverStation.getInstance().getGameSpecificMessage().charAt(objectInt);

		maxSpeed = 0.4; // how did we not do this before

		timer.start();

		fakeEncoderUp = 0;
		fakeEncoderDown = 0;

		autoStep = 0;

		FRE.reset();
		FLE.reset();
		BRE.reset();
		BLE.reset();
		
		i = 0;
	}

	/**
	 * This function is called periodically during autonomous.
	 */
	@Override
	public void autonomousPeriodic() {

		output();

		if (autoStep == 0) {
			/*
			 * if(timer.get() < 2 && startChar == 'C')
			 * System.out.println("tAkInG a snOoZE"); else {
			 */
			autoStep++;
			timer.stop();
			timer.reset();
			// }
		}
		if (autoStep == 1 && objectInt != 0) {
			moveFromCenter();
		}
		
		if (autoStep >= 2) {
			if(objectInt == 1)
				driveScale(startChar, targetChar);

			else if(objectInt == 0)
				driveSwitchShort(startChar, targetChar);
			
			else if (autoStep == 2)
				driveDist(0, true, 10, 0.5);
			
			else {
				drive(0, 0, 0);
				System.out.println("fish are bad");
			}

		}

	}

	public void moveFromCenter() {
		if (targetChar == 'R')
			driveDist(1, true, 7.5, 1);
		else if (targetChar == 'L')
			driveDist(1, true, 7.5, -1);
		else
			autoStep++;

		System.out.println("moving from center");
	}

	public void drive(double x, double y, double z) {

		// the robot smoothly accelerates at a rate determined by the magnitude of the
		// joystick's manipulation
		// if the joystick is in the resting position, setting the motor to zero
		// should cause the robot to drift.

		if (Math.abs(x) > 0.15 && Math.abs(curXVel) <= maxSpeed)
			curXVel += accel * x * 0.5;
		else if (Math.abs(x) <= 0.15)
			curXVel = 0;

		if (Math.abs(y) > 0.15 && Math.abs(curYVel) <= maxSpeed)
			curYVel += accel * y * 0.5;
		else if (Math.abs(y) <= 0.15)
			curYVel = 0;

		if (Math.abs(z) > 0.3)
			curRot = z;
		else
			curRot = 0;

		drive.driveCartesian(curYVel, curXVel, curRot * 0.3);

		balanceMotors(1);
	}

	/**
	 * balanceMotors Attempts to corrects any motors going too fast based on encoder
	 * values
	 * 
	 * @param percentError
	 *            how far you will allow the encoder value can stray from average of
	 *            the 4 encoders (eg 95% means it can be within 5% of average)
	 * @return void
	 */
	public void balanceMotors(double percentError) {
		if (curYVel == 0 ^ curXVel == 0 ^ curRot == 0) {

			boolean changed = false; // Controls whether the specific motor has been modified
			double[] velocity = new double[4]; // stores absolute value rate of all 4 encoders

			Encoder[] encoders = new Encoder[4]; // all 4 encoders
			encoders[0] = FRE;
			encoders[1] = FLE;
			encoders[2] = BRE;
			encoders[3] = BLE;

			Victor[] victors = new Victor[4]; // all 4 motors
			victors[0] = FR;
			victors[1] = FL;
			victors[2] = BR;
			victors[3] = BL;

			double[] rateAdjustments = { 1, 1, 1, 1 }; // base adjustment rate

			while (changed == false) {
				changed = false;
				velocity[0] = Math.abs(FRE.getRate());
				velocity[1] = Math.abs(FLE.getRate());
				velocity[2] = Math.abs(BRE.getRate());
				velocity[3] = Math.abs(BLE.getRate());

				double avg = 0;
				for (double d : velocity) // calculates average velocity
					avg += d;
				avg = avg / velocity.length;

				for (int x = 0; x < 3; x++) {
					// checks if rate is within accepted percent error
					if (velocity[x] >= (avg * (1 + (1 - percentError)))) {
						rateAdjustments[x] *= percentError;
						changed = true;
					}
				}

				for (int x = 0; x < victors.length; x++) // performs adjustments
					victors[x].set(victors[x].get() * rateAdjustments[x]);

				for (double d : rateAdjustments)
					if (d <= .7)
						changed = false;

			}
		}
	}

	// I did not decide this
	public double distFromEncoders() {
		 int count = 0;
		 double val = 0.0; //DOUBLE DOUBLE DOUBLE THIS IS A DOUBLE
		 if(FRE.getDistance() != 0){
			count++;
		 	val += FRE.getDistance();
		 }
		 if(FLE.getDistance() != 0){
			count++;
		 	val += FLE.getDistance();
		 }
		 if(BRE.getDistance() != 0){
			count++;
			val += BRE.getDistance();
		 }
		 if(BLE.getDistance() != 0){
			count++;
			val += BLE.getDistance();
		 }
		 if(val == 0)
			return val;
		 return val/count;
	}

	

	@Override
	public void teleopInit() {
		FRE.reset();
		FLE.reset();
		BRE.reset();
		BLE.reset();
		barMode = false;
	}

	double yFromButtons() {
		double val = 0;
		if (stick.getRawButton(8))
			val = -1;
		else if (stick.getRawButton(9))
			val = 1;
		return val;
	}

	/**
	 * telopPeriodic This function is called periodically during operator control.
	 */
	@Override
	public void teleopPeriodic() {

		double x = (-stick.getRawAxis(2) + 1) / 2;
		
		maxSpeed = gamepad.getRawButton(3) ? 0.2 : x;

		
		drive(-stick.getY(), stick.getX(), stick.getZ());
		fourBar();
		grabber();
		// climb();
		output();
	}

	public void grabber() {

		if (stick.getRawButton(1)) { // trigger on joystick
			grabL.set(-0.2);
			grabR.set(0.8);
		} else if (gamepad.getRawButton(8)) { // right trigger on gamepad
			grabL.set(-1);
			grabR.set(-1);
		} else if (stick.getRawButton(2)) {
			grabL.set(0.5);
			grabR.set(0.5);
		} else {
			grabL.set(0);
			grabR.set(0);
		}

	}
	
	// code for controlling fourbar operations during teleop
	public void fourBar() {

		// old code based on barMode
		/*changeBarMode();
		
		if (barMode && !articulating)
			fullBar( - gamepad.getRawAxis(3) );
		else
			topBar( - gamepad.getRawAxis(3) );*/
		
		lowBar( - gamepad.getRawAxis(1));
		topBar( - gamepad.getRawAxis(3));
		
		if(gamepad.getPOV() == 0)
			fullBar(1);
		else if(gamepad.getPOV() == 180)
			fullBar(-1);
		
	}

	void changeBarMode() {
		if (gamepad.getRawButton(10) && !barModePressed) {
			barMode = !barMode;
			barModePressed = true;
			/*if(barMode)
			   articulating = true;*/
		} else if (!gamepad.getRawButton(10)) {
			barModePressed = false;
		}

		
		/*if(articulating) {
			if(fakeEncoderDown < fakeEncoderUp - 1) {
				lowerBar(0.1);
				fakeEncoderDown++;
			} else if(fakeEncoderDown > fakeEncoderUp + 1) {
				lowerBar(-0.1);
				fakeEncoderDown--;
			} else
				articulating = false;
		}*/
		 
	}
	void changeCubeMode() {
		if (gamepad.getRawButton(9) && !cubeModePressed) { //eventually make limit switch?
			cubeMode = !cubeMode;
			cubeModePressed = true;
		} else if (!gamepad.getRawButton(9)) {
			cubeModePressed = false;
		}
	}
	
	void topBar(double val) {
		
		lowerBar.set(0.15);
		
		if (val < -0.15) { // POSITIVE IS UP
			if(cubeMode)
				upperBar.set(0.60);
			else
				upperBar.set(0.55);
			
			fakeEncoderUp++;
			System.out.println("upper bar positive");
			
			
		}
		
		else if (val > 0.15 && upMin.get()) { // NEGATIVE IS DOWN
			upperBar.set(-0.225);
			fakeEncoderUp--;
			System.out.println("upper bar negative");
			
		}
		
		else
			upperBar.set(0);
	}

	public void fullBar(double val) {
		topBar(val);
		lowBar(val);
	}
	public void lowBar(double val) {
		//if (!articulating) {
			
			if (val > 0.15) { // POSITIVE IS UP
				 
				lowerBar.set(1);
				fakeEncoderDown--;
				
				System.out.println("lower bar positive");
			} 
			else if (val < -0.15 && lowMin.get()) { // NEGATIVE IS DOWN
				
				lowerBar.set(-0.4);
				fakeEncoderDown++;
					
				System.out.println("lower bar negative");
			} 
			else {
				upperBar.set(0);
				lowerBar.set(0);
			}
		//}
	}

	// articulates both lower bar motors
	void lowerBar(double val) {
		lowerBar.set(val); // CIM motor is about 5 times as powerful as the Bosch motor
	}

	/*
	 * public void climb() { if(stick.getRawButton(5)) wench.set(1); else
	 * if(stick.getRawButton(6)) wench.set(-1); else wench.set(0); }
	 */

	public void driveSwitch(char startPos, char switchSide) {
		if (autoStep == 2) { // moves the robot forwards to the middle of either side of the switch
			driveDist(0, true, 11.59, 1);
		}
		if (autoStep == 3) { // moves the robot past the switch if necessary.
			if (startPos == switchSide)
				autoStep++;
			else {
				driveDist(0, true, 5.75, 1);
			}

		}
		if (autoStep == 4) { // moving across the switch horizontally
			if (startPos == switchSide)
				autoStep++;
			else {
				if (switchSide == 'R') {
					driveDist(1, true, 15.34, -1);
				} else {
					driveDist(1, true, 15.34, 1);
				}
			}
		}
		if (autoStep == 5) { // moves backwards to the desired location in the middle of each side.
			if (startPos == switchSide) // increments autoStep because it is in position to rotate.
				autoStep++;
			else {
				driveDist(0, true, 5.75, -1);
			}
		}
		if (autoStep == 6) { // rotates the robot 90 degrees in the appropriate direciton
			if (switchSide == 'L')
				driveDist(2, true, 90, 1);
			else {
				driveDist(2, true, 90, -1);
			}
		}
		if (autoStep == 7) { // moves the fourbar up
			if (fakeEncoderUp <= maxBoth)
				topBar(0.5);
			else {
				autoStep++;
				// fakeEncoderUp = 0; //why???
				timer.reset();
				timer.start();
				topBar(0);
			}
			drive(0, 0, 0);
		}
		if (autoStep == 8) { // expels the cube from the grabber.
			if (timer.get() < 0.25) {
				grabL.set(-0.5);// do this for a few seconds
				grabR.set(-0.5);
			} else {
				autoStep++;
				grabL.set(0);
				grabR.set(0);
				timer.stop();
			}
			drive(0, 0, 0);
		}
		if (autoStep == 9) { // moves the fourbar back down.
			if (fakeEncoderUp >= minBoth)
				topBar(-0.5);
			else {
				topBar(0);
				autoStep++;
				fakeEncoderUp = 0; // why???
			}
			autoStep++;
			drive(0, 0, 0);
		}
		if (autoStep > 9)
			drive(0, 0, 0);
	}
	
	public void driveSwitchShort(char startPos, char switchSide) { //shortened version of the driveSwitchMethod. DIFFERENT FUNCITON!
		if(autoStep == 2) { //Moves the robot horizontally to the appropriate position.
			if(startPos == 'C') {
				if(switchSide == 'R') {
					driveDist(1, true, 5.84, 0.75);
				}
				else {
					driveDist(1, true, 5.84, -1);
				}
			}
			else {
				if(startPos == 'R') {
					if(switchSide == startPos) {
						driveDist(1, true, 5.5, -1);
					}
					else {
							driveDist(1, true, 16.5, -1);
					}
				}
				else {
					if(switchSide == startPos) {
						driveDist(1, true, 5.5, 1);	
					}
					else {
						driveDist(1, true, 16.5, 1);
					}
						
				}
				
			}
			
		}
		if(autoStep == 3) { //drives the robot forwards to meet the switch
			driveDist(0, true, 9.5, 1);
		}
		if(autoStep == 4) { //drops the cube
			if (i <= 2000) {
				grab(-0.5);// do this for a few seconds
				i++;
			}
			else {
				autoStep++;
				grab(0);
			}
		drive(0, 0, 0);
		}
	}
	
	private void grab(double val) {
		grabL.set(val);
		grabR.set(val);
	}
	
	public void autoReset() {
		drive(0, 0, 0);
		resetEncoders();
		autoStep++;
	}

	/**
	 * @param axis: y is 0, x is 1, and rotation is 2
	 */
	public void driveDist(int axis, boolean step, double dist, double pow) {
		
		if(dist < 0)
			pow *= -1;
		
		double rate;
		
		if (axis == 0) {
			rate = rateY;
		} 
		else if (axis == 1) {
			rate = rateX;
		} 
		else {
			rate = rateRot;	
		}
		
		if (Math.abs(distFromEncoders()) < Math.abs(dist) * rate)	{
			if (axis == 0) {
				drive(pow, 0, 0);
			} else if (axis == 1) {
				drive(0, pow, 0);
			} else {
				drive(0, 0, pow);
			}
		} 
		
		else {
			drive(0, 0, 0);
			if(step) {
				autoStep ++;
				resetEncoders();
			}
		}
	}

	public void driveScale(char pos, char side) {
		if (autoStep == 2) { // 10 ft forward
			driveDist(0, true, 10, 0.5);
		}
		if (autoStep == 3) {
			if (pos == 'L') {
				if (side == 'L') {
					driveDist(1, true, 1.86, 1);
				} else {
					driveDist(1, true, 15, 1);
				}
			} else {
				if (side == 'L') {
					driveDist(1, true, 15, -1);
				} else {
					driveDist(1, true, 1.86, -1);
				}
			}
		}
		if (autoStep == 4) {
			if (fakeEncoderDown < maxBoth)
				fullBar(1);
			else
				autoStep++;
			drive(0, 0, 0);
		}
		if (autoStep == 5) {
			driveDist(0, true, 1.333, -0.5);
			if(autoStep == 6) {
				timer.reset();
				timer.start();
			}
		}
		if (autoStep == 6) {
			// let go of block
			if (timer.get() < 1) {
				grabR.set(-0.5);
				grabL.set(-0.5);
			} else {
				// reset distance from Encoder
				grabR.set(0);
				grabL.set(0);
				autoStep++;
				timer.stop();
			}
			drive(0, 0, 0);
		}

	}

	public void resetEncoders() {
		FRE.reset();
		FLE.reset();
		BRE.reset();
		BLE.reset();
	}

	public void displayEncoderVals() {

		SmartDashboard.putNumber("Value of Encoder FRE", FRE.get());
		SmartDashboard.putNumber("Value of Encoder FLE", FLE.get());
		SmartDashboard.putNumber("Value of Encoder BRE", BRE.get());
		SmartDashboard.putNumber("Value of Encoder BLE", BLE.get());

	}

	public void displayEncoderRates() {

		SmartDashboard.putNumber("Rate of Encoder FRE", FRE.getRate());
		SmartDashboard.putNumber("Rate of Encoder FLE", FLE.getRate());
		SmartDashboard.putNumber("Rate of Encoder BRE", BRE.getRate());
		SmartDashboard.putNumber("Rate of Encoder BLE", BLE.getRate());

	}
	
	public void output() {
		// display the values from the encoder to the SmartDashboard
		displayEncoderVals();
		displayEncoderRates();

		SmartDashboard.putString("GameData", DriverStation.getInstance().getGameSpecificMessage());
		SmartDashboard.putString("startPos", startLoc.getSelected().toString());
		SmartDashboard.putNumber("Value of fake top bar encoder", fakeEncoderUp);
		SmartDashboard.putNumber("Value of fake bottom bar encoder", fakeEncoderDown);
		SmartDashboard.putBoolean("Is lower limit pressed", !lowMin.get());
		SmartDashboard.putBoolean("Is upper limit pressed", !upMin.get());
		SmartDashboard.putBoolean("fullbar mode", barMode);

		SmartDashboard.putData("Starting Positions", startLoc);
		SmartDashboard.putData("Target Objective", objectiveChoice);

		SmartDashboard.putNumber("Average of Drive Encoders", distFromEncoders());
		
		SmartDashboard.putNumber("Step", autoStep);

		SmartDashboard.putNumber("Timer", timer.get());

		SmartDashboard.putNumber("Max Speed", maxSpeed);
	}
	
	@Override
	public void testInit() {
		resetEncoders();
		maxSpeed = 1;
		autoStep = 0;
	}
	
	@Override
	public void testPeriodic() {
		
		driveDist(0, false, 10, 0.25);
		output();
		displayEncoderVals();
		
		// for vision
		
	}

	// this method prints values when the robot is disabled
	@Override
	public void disabledInit() {
		System.out.println("Front Right Encoder: " + FRE.get());
		System.out.println("Front Left Encoder: " + FLE.get());
		System.out.println("Back Right Encoder: " + BRE.get());
		System.out.println("Back Left Encoder: " + BLE.get());

		System.out.println("Rate of Front Right Encoder: " + FRE.getRate());
		System.out.println("Rate of Front Left Encoder: " + FLE.getRate());
		System.out.println("Rate of Back Right Encoder: " + BRE.getRate());
		System.out.println("Rate of Back Left Encoder: " + BLE.getRate());

		System.out.println("Fake Upper Encoder: " + fakeEncoderUp);
		System.out.println("Fake Lower Encoder: " + fakeEncoderDown);

		System.out.println("Autonomous Step: " + autoStep);

		System.out.println("X Velocity: " + curXVel);
		System.out.println("Y Velocity: " + curYVel);
		System.out.println("Rotation Rate: " + curRot);

		System.out.println("Stick X: " + stick.getX());
		System.out.println("Stick Y: " + stick.getY());
	}

}

                                                  /*:-                          
                                                 /hdms                          
                                                `ommmh.                          
                                               `+mmmd/os+-`                      
                                             `:ohmmmmdmmmmmhs/                    
                                    -+oooo/+sdmmmmbmanmmmmmmmd``                  
             ```...--..```        `+dmyyyhmmmmmmmmmneedsmmmmmmhyy+                
      ``-/+syhhdmmmmmmmdhyso+++++sydms    hmmmmdmmmmtommmmmmmmmmmd`               
     /ydmmmmmmmmmmmmmmmmmmmmmmy:./oo/`    .+so/./hmmmstopmmquichem+               
      -odmmmmmmmjaredmmmmmmmmh`                  .dmmmmmmeatermmmdh               
     .-`.+hmmmmmmismmmmmmmmmmms                  hmmmmmmmmmdhddy+-`               
    `odmmdhshmmmmmthemmmmmmmmmd/     `+shys/      odmmmmdy+-` ``                   
   `ymmmmmkhaimmmmmrealmmmmm:.      `hhailmmy      `-omm-                          
   smsocksmismmmmmmmmvpmmmmm        :mmthemmm`       -mm:                          
  `mmmmmmmmmbadmmmmmmmmmmmmmyo/`     ommorbd/     `/oymd.                          
  -mmmsquidmmmmmmmmmmmmyommmmmms      `:+/:`     `dmds+.                           
  `dmmmtastesmmmmmmmd/`hmmmmmd:                  smm/                             
   ommmmmmgoodmmmmmdo` `oydmmmh-   ``        ``   /mmo                             
   :malecmmmmmmmmdo.     ``-+syhsohddh-    +hddy+hmd+`                             
   :mmisn'tmmmmd+.            ``.-:+hmy-.-:md+oyyy+`                               
   ommmrealmmh+`                    /mmddddm/  ``                                  
   `dmmmmmmmy/`                      -josephd                                       
   +mmmmmds-`                         ydunlap                                       
  .dmmmh+.                            .herwin`                                      
 `ymmd/`                               .hmmmmo.                                     
ommmmh/`                               .smmmmdy/.                                  
`:::::::.             BEES_             `:::::::*/