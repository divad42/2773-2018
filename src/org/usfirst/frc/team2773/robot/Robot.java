// Version 1.0.0
// Updated grabber code and upper bar code and added rudimentary auto and encoder functions and also climber function
// Halved the rotation speed in the drive method (not the rotation speed of the wench, wheels, 4-bar motors, etc.)

package org.usfirst.frc.team2773.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.drive.MecanumDrive;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;


/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.properties file in the
 * project.
 */
public class Robot extends TimedRobot {   
   public final double TILE_DISTANCE_RATE_COMPBOT = 330.861363636;  // in degrees per foot
   public final double COMP_DISTANCE_RATE = 1;
   public final double TILE_ROTATION_RATE_PRACTICEBOT = 1;
   public static double distRate; //= TILE or COMP rate 
   public static double degRate;
   
   public SendableChooser<Character> startLoc;
   public SendableChooser<Character> targetPos;
   public SendableChooser<Integer> objectiveChoice;

   public char startChar;
   public char targetChar;
   public int objectInt;
   public boolean isSleep;
   
   //Wheels
   public Victor FL;
   public Victor FR;
   public Victor BL;
   public Victor BR;
   public MecanumDrive drive;
  
   //Super Ultra Important Epic Awesome One-Of-A-Kind Exclusive Encoders
   static public Encoder FLE;
   static public Encoder FRE;
   static public Encoder BLE;
   static public Encoder BRE;

   //Lower Bar and Upper Bar Variables
   public Spark lowerBar;
   public Spark upperBar;
   public Encoder lowEncoder;
   public Encoder upEncoder;
   public static double maxUp;
   public static double maxBoth;
   public static double minUp;
   public static double minBoth;
   public boolean barMode;
   public boolean articulating;
   public boolean barModePressed;

   
   //Drives and Joysticks

   public Joystick gamepad;
   public Joystick stick;
         
   public double distance;
   public double grabLimit;

   //Numeral Data
   public int autoStep;
   
   //Helps control the speed of the driver method
   public double curXVel;
   public double curYVel;
   public double curRot;
   public double maxSpeed;
   public double accel;

   public Spark wench;

   //Grabber Stuff
   public Spark grab;
   public Encoder grabRot;
   public boolean isClosed;

   /**
 * Initalizes all of the needed variables to operate the robot.
 */
	@Override
	public void robotInit() {
   
      // wheels
   public Spark wench;
   
   public Timer timer;


	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
   
      distRate = TILE_DISTANCE_RATE_COMPBOT; 
      degRate = TILE_ROTATION_RATE_PRACTICEBOT;
     	
      //Wheel and Drive Objects
      FL = new Victor(3);
      FR = new Victor(0);
      BL = new Victor(1);
      BR = new Victor(2);
      distRate = TILE_DISTANCE_RATE; 
      drive = new MecanumDrive(FL, BL, FR, BR);
      
      //Encoder Declaration
      FLE = new Encoder(2,3);
      FRE = new Encoder(4,5);
      BLE = new Encoder(6,7);
      BRE = new Encoder(0,1);

      
      //Grabber and Related Data
      grab = new Spark(4);
      grab.setInverted(true);
      isClosed = true;
      grabLimit = 0;
      articulating = false;

      //4 Bar parts being declared
      lowerBar = new Spark(6);
      upperBar = new Spark(7);
      upEncoder = new Encoder(4, 5);
      upEncoder.reset();
      lowEncoder = new Encoder(6, 7);
      lowEncoder.reset();
      barMode = false;
      barModePressed = false;
      
      //Climber related data
      wench = new Spark(6);
      
      // these constants represent the limits of our 4-bar articulation
      maxUp = 360;
      minUp = -360;
      maxBoth = 360;
      minBoth = -360;

      
      // drive variables
      curXVel = 0;
      curYVel = 0;
      curRot = 0;
      accel = 0.01;
      
      //Controller objects
      gamepad = new Joystick(0);
      stick = new Joystick(1);

      //Drive Variables 
      curXVel = 0.0;
      curYVel = 0.0;
      curRot = 0.0;
      accel = 0.01;

            
      // the radio buttons for selecting our starting position
      startLoc = new SendableChooser<>();
      startLoc.addDefault("Center", new Character('C'));
      startLoc.addObject("Left", new Character('L'));
      startLoc.addObject("Right", new Character('R'));
      SmartDashboard.putData("Starting Positions", startLoc);
      
      //Target Position Radio Buttons in SmartDashboard NOT HOW WE WANT TO DECIDE THIS
      targetPos = new SendableChooser<>();
      targetPos.addDefault("Left", new Character('L'));
      targetPos.addObject("Right", new Character('R'));
      SmartDashboard.putData("Target Position", targetPos);
      
      //Selecting Objective with RadioButtons in SmartDashboard 
      objectiveChoice = new SendableChooser<>();
      objectiveChoice.addDefault("Switch", new Integer(0));
      objectiveChoice.addObject("Scale", new Integer(1));
      objectiveChoice.addObject("Baseline", new Integer(2));
      SmartDashboard.putData("Target Objective", objectiveChoice);

      timer = new Timer();

	}

	/**
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable
	 * chooser code works with the Java SmartDashboard. If you prefer the
	 * LabVIEW Dashboard, remove all of the chooser code and uncomment the
	 * getString line to get the auto name from the text box below the Gyro
	 *
	 * <p>You can add additional auto modes by adding additional comparisons to
	 * the switch structure below with additional strings. If using the
	 * SendableChooser make sure to add them to the chooser code above as well.
	 */
	@Override
	public void autonomousInit() {

      distance = 0;
      autoStep = 0;

		startChar = startPos.getSelected();
		targetChar = targetPos.getSelected();
		objectInt = objectiveChoice.getSelected().intValue();
		startChar = startLoc.getSelected();     // the starting position
		objectInt = objectiveChoice.getSelected().intValue();		// whether we want the switch or scale
		targetChar = DriverStation.getInstance().getGameSpecificMessage().charAt(objectInt);
		
      resetEncoders();

	}

	/**
	 * This function is called periodically during autonomous.
	 */
	@Override
	public void autonomousPeriodic() {
		if(autoStep == 0){
			if(Timer.getMatchTime() < 3)
				System.out.print("Taking A Snooze");
			else
				autoStep++;
		}
		if(autoStep == 1) {
			if(Math.abs(distFromEncoders()) <= 7.5 * distRate)
				moveFromCenter();
			else 
				autoStep++;
		}
		if(autoStep >= 2) {
			if(objectInt == 1)
				driveScale(startChar, targetChar);
			else if(objectInt == 0) 
				driveSwitch(startChar, targetChar);
			else
				autoLine();
				
		}
      
	}
	
	public void moveFromCenter() {
		if(targetChar == 'R')
			drive(0, 1, 0);
		else
			drive(0, -1, 0);
	}
   
   /**
 * Drives the actual robot using the given double variables using the changeSpeed method.
 * <p>
 * @param x Value to drive the robot on the x axis. The x-axis controls left and right
 * @param y Value to drive the robot on the y axis. The y-axis controls forward and backward
 * @param z Value to rotate the robot by degrees. The robot will turn clockwise when the number is positive, and counterclockwise when the number is negative
 */
   public void drive(double x, double y, double z) {

      curXVel = changeSpeed(x, curXVel);
      curYVel = changeSpeed(y, curYVel);
      curRot = changeSpeed(z, curRot);

      // if the joystick is in the resting position, setting the motor to zero
      // should cause the robot to drift.   
      if(x > -0.1 && x < 0.1)
         curXVel = 0;
         
      if(y > -0.1 && y < 0.1)
         curYVel = 0;
         
      if(z > -0.1 && z < 0.1)
         curRot = 0;

      drive.driveCartesian( curYVel, curXVel, curRot );
      
      balanceMotors(.95);
   }

     /**
	 * Changes the requested value at a lower rate defined by an input and a global constant for smoother acceleration
    * @param val The value to change the curVel by
    * @param curVel The value being changed by the val parameter
    * @return The changed number
	 */
   public double changeSpeed (double val, double curVel){
      if(val > 0 && curVel <= maxSpeed)
         return (curVel += accel * val);
      
      if(val < 0 && curVel >= (-1 * maxSpeed))
         return (curVel += accel * val);
         
      if(val > -0.1 && val < 0.1)
         return 0;
   }
   
   
    /**
	 * Returns the speed of the robot from the encoder
    * @return The speed of the robot from the encoder
	 */
   public double speedFromEncoder() {
      return 4;

   /**
    * balanceMotors     Attempts to corrects any motors going too fast based on encoder values 
    * @param percentError     how far you will allow the encoder value can stray from average of the 4 encoders (eg 95% means it can be within 5% of average)
    * @return void
    */
   public void balanceMotors(double percentError) {
	   if(curYVel == 0 ^ curXVel == 0 ^ curRot == 0) {
		   boolean changed = false; //Controls whether the specific motor has been modified
		   double[] velocity = new double[4]; //stores absolute value rate of all 4 encoders
		   Encoder[] encoders = new Encoder[4]; // all 4 encoders
		    encoders[0] = FRE;
		   	encoders[1] = FLE;
		   	encoders[2] = BRE;
		   	encoders[3] = BLE;
		   Victor[] victors = new Victor[4]; //all 4 motors
		    victors[0] = FR;
		    victors[1] = FL;
		    victors[2] = BR;
		    victors[3] = BL;
		   double[] rateAdjustments = {1,1,1,1}; //base adjustment rate
		   while(changed == false) {
			   changed = false;
			   velocity[0] = Math.abs(FRE.getRate());
			   velocity[1] = Math.abs(FLE.getRate());
			   velocity[2] = Math.abs(BRE.getRate());
			   velocity[3] = Math.abs(BLE.getRate());
		   
			   double avg = 0;
			   for(double d: velocity) //calculates average velocity
				   avg += d;
			   avg = avg/velocity.length; 
		   
			   for(int x = 0; x < 3; x++) {
				   //checks if rate is within accepted percent error
				   if(velocity[x] >= (avg * (1 + (1 - percentError)))) {
					   rateAdjustments[x] *= percentError; 
					   changed = true;
				   }
			   }
			   
			   for(int x = 0; x < victors.length; x++) //performs adjustments
				   victors[x].set(victors[x].get() * rateAdjustments[x]);
			   
			   for(double d: rateAdjustments)
				   if(d <= .7)
					   changed = false;
				   
		   }
	   }

   }


       /**
	 * Returns the distance from the encoders
    * @return The distance from the encoders
	 */
   public double distFromEncoders() {
	   return FLE.getDistance();
   }
   
   public double distFromEncoder() {
      return FLE.get();
   }

    /**
	 * Utilizes the resetEncoders() method to reset the encoders when teleop is initalized
    * <p>All other encoders that need to be reset will need to go here.
	 */
   @Override
   public void teleopInit() {
      resetEncoders();
   }
   
   /**
	 * Allows the teleoperator to control the robot using the joystick.
    * <p>When used, updates the SmartDashboard via the output() method
    * <p>Drives using the drive() method
	 * This function is called periodically during operator control.
	 *telopPeriodic    This function is called periodically during operator control.
	 */
	@Override
	public void teleopPeriodic() {
		
      maxSpeed = (-stick.getThrottle() + 1) / 2;      // sets the maximum speed of the robot using the slider
      
      drive(-stick.getY(), stick.getX(), stick.getTwist());
      fourBar();
      grabber();
      output();
	}

	/**
	 * This will be used during test mode if we ever actually use it
	 */
	@Override
	public void testPeriodic() {
   
	}
   
   /**
   * Resets the wheel Encoders.
   */
   public void resetEncoders(){
	   FRE.reset();
	   FLE.reset();
	   BRE.reset();
	   BLE.reset();   
   }
	
   /**
   *Operates the Grabbers
   *<p>
   *Actions are based off of the Joystick and Gamepad
   */
   public void grabber() {
	   if(isClosed)
	         grab.set(0.25);
	   else
		   grab.set(0);
	   
	  if(stick.getRawButton(1)){	// trigger on joystick
         grab.set(0.5);	
         isClosed = true;
      }
      else if(gamepad.getRawButton(8)){ 	// right trigger on gamepad
         grab.set(-0.5);
         isClosed = false;
      }
      
   }
	      
      
// code is commented out when and only when the encoders are not plugged in.

   /**
   *Controls the Four Bar. changeBarMode determines what bar(s) to change.
   */
   public void fourBar(){

	   changeBarMode();
	   
	   if (barMode && !articulating)
		   fullBar(gamepad.getTwist());
	   else
		   topBar(gamepad.getTwist());
   }
   
   /**
   *Determines whether the upper bar or both bars are controlled
   */
   void changeBarMode() {
	   if(gamepad.getRawButton(10) && !barModePressed) {
		   barMode = !barMode;
		   barModePressed = true;
		   if(barMode)
			   articulating = true;
	   }
	   else
		   barModePressed = false;
	   
	   if(articulating) {
		   if(lowEncoder.get() < upEncoder.get() - 1)
			   lowerBar.set(0.1);
		   else if(lowEncoder.get() > upEncoder.get() + 1)
			   lowerBar.set(-0.1);
		   else
			   articulating = false;
	   }
   }

   /**
   *Operates the upper Bar and changes it by the requested value
   * @param val Changes the upper bar by the requested value
   */

   public void topBar(double val) {
	   if(val < 0 && upEncoder.get() < maxUp)
		   upperBar.set(0.1);
	   else if(val > 0 && upEncoder.get() > minUp)
		   upperBar.set(-0.1);
	   else
		   upperBar.set(0);
   }

   /**
   * Operates both the Upper and Lower bar, and changes them by the requested value
   * @param val Changes both bars by the requested value
   */
   public void fullBar(double val) {
	   if(!articulating) {
		   if(val < 0 && upEncoder.get() < maxUp) {
			   upperBar.set(0.1);
			   lowerBar.set(0.1);
		   }
		   else if(val > 0 && upEncoder.get() > minUp) {
			   upperBar.set(-0.1);
			   lowerBar.set(-0.1);
		   }
		   else {
			   upperBar.set(0);
			   lowerBar.set(0);
		   }
	   }
   }
   
   /**
   *Allows the robot to climb the rope
   */
   public void climb() {
	   if(stick.getRawButton(2))
		   wench.set(1);
	   else if(stick.getRawButton(5))
		   wench.set(-1);
	   else
		   wench.set(0);
	   
   }

      /**
   * Outputs the Encoder Values and the Gamedata
   * <p>
   * Uses the displayEncoderVals() method
   */

   public void driveSwitch(char startPos, char switchSide) {
      if(autoStep == 2) { //moves the robot forwards to the middle of either side of the switch
         if(distFromEncoders() <= 12.5 * distRate) //drive it forward regardless of the side
            drive(0, 1, 0);
         else { //increments autoStep
            autoStep++;
            resetEncoders();
         }
      }
      if(autoStep == 3) { //moves the robot past the switch if necessary.
         if(startPos == switchSide) 
            autoStep++;
         else {
            if(distFromEncoders() <= 7 * distRate)
               drive(1, 0, 0);
            else {
               autoStep++;
               resetEncoders();
            }
         }
      }
      if(autoStep == 4) { //moving across the switch horizontally
         if(startPos == switchSide)
            autoStep++;
         else {
            if(switchSide == 'R') {
               if(distFromEncoders() >= -19 * distRate)
                  drive(0, -1, 0);
               else {
                  autoStep++;
                  resetEncoders();
               }
            }
            else {
               if(distFromEncoders() <= 19 * distRate)
                  drive(0, 1, 0);
               else {
                  autoStep++;
                  resetEncoders();
               }
            }
         }         
      }
      if(autoStep == 5) {  //moves backwards to the desired location in the middle of each side.
         if(startPos == switchSide)  //increments autoStep because it is in position to rotate.
            autoStep++;
         else {
            if(distFromEncoders() >= 7 * distRate)
               drive(-1, 0, 0);
            else {
               autoStep++;
               resetEncoders();
            }
         }
      }    
      if(autoStep == 6) { //rotates the robot 90 degrees in the appropriate direciton
         if(switchSide == 'L') 
            if(distFromEncoders() <= 90 * degRate)
               drive(0, 0, 1);
            else {
               autoStep++;
               resetEncoders();
            }
         else {
            if(distFromEncoders() >= -90 * degRate)
               drive(0, 0, -1);   
            else {
               autoStep++;
               resetEncoders();
            }
         }
      }
      if(autoStep == 7) { //moves the fourbar up
         if(upEncoder.get() <= maxUp)
            topBar(0.5);
         else {
            autoStep++;
            upEncoder.reset();
         }
         drive(0, 0, 0);
      }
      if(autoStep == 8) { //expels the cube from the grabber.
         int i = 0;
         if(i <= 2000)
            grab.set(-0.5);//do this for a few seconds
         else {
            autoStep++;
            grab.set(0);
            isClosed = false;
         }
         drive(0, 0, 0);
      }
      if(autoStep == 9) { //moves the fourbar back down.
         if(upEncoder.get() >= minUp)
            topBar(-0.5);
         else {
            autoStep++;
            upEncoder.reset();
         }
         drive(0, 0, 0);
      }
      if(autoStep > 9)
         drive(0, 0, 0);       
   }
   
   public void resetEncoders()
   {
   	FRE.reset();
	   FLE.reset();
	   BRE.reset();
	   BLE.reset();
   }

   public static void displayEncoderVals(){
      double[] vals = new double[4];
      vals[0] = FRE.get();
      vals[1] = FLE.get(); 
      vals[2] = BRE.get(); 
      vals[3] = BLE.get(); 
      
      for(int i = 0; i < vals.length; i
    		  							++) {
         SmartDashboard.putNumber("Value of Encoder " + i, vals[i]);
      }
 
   }
   
    /**
    * Displays the encoder values in the SmartDashboard.
    * @see Encoder
    */
   public void output() {
      // display the values from the encoder to the SmartDashboard
	  displayEncoderVals();

	  SmartDashboard.putString("GameData", DriverStation.getInstance().getGameSpecificMessage());	   
	  SmartDashboard.putString("startPos", startLoc.getSelected().toString());
      
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
      -odmmmmmmmjaredmmmmmmmmh`                  .dmmmmmmmmmmmmmmdh               
     .-`.+hmmmmmmismmmmmmmmmmms                  hmmmmmmmmmdhddy+-`               
    `odmmdhshmmmmmthemmmmmmmmmd/     `+shys/      odmmmmdy+-` ``                   
   `ymmmmmkhaimmmmmrealmmmmm:.      `hhailmmy      `-omm-                          
   smmmmmmmismmmmmmmmvpmmmmm        :mmthemmm`       -mm:                          
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
`:::::::.   Mr. Meckling is Max's dad.  `:::::::*/