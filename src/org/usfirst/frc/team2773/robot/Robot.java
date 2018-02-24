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


/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.properties file in the
 * project.
 */
public class Robot extends TimedRobot {   
   public final double TILE_DISTANCE_RATE = 330.861363636;  // in degrees per foot
   public final double COMP_DISTANCE_RATE = 1;
   public static double distRate; //= TILE or COMP rate 
   
   public SendableChooser<Character> startPos;
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
            
      // the radio buttons for selecting our starting position
      startPos = new SendableChooser<>();
      startPos.addDefault("Center", new Character('C'));
      startPos.addObject("Left", new Character('L'));
      startPos.addObject("Right", new Character('R'));
      SmartDashboard.putData("Starting Positions", startPos);
      
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
		
      resetEncoders();

	}

	/**
	 * This function is called periodically during autonomous.
	 */
	@Override
	public void autonomousPeriodic() {

      // In the first (zeroth?) step, the robot moves 12 feet
		
		output();
		
      if(autoStep == 0 && distance < 12) {
         drive(0, 1, 0);
      } else if(autoStep == 0) {
         drive(0, 0, 0);
         autoStep ++;
      } else
         drive(0, 0, 0);
      
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
         
      drive.driveCartesian( curYVel, curXVel, curRot );
   }
   
     /**
	 * Changes the requested value (curVel) by the requested value (val) and returns it for a smoother acceleration
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
   }


       /**
	 * Returns the distance from the encoders
    * @return The distance from the encoders
	 */
   public double distFromEncoders() {
	   return FLE.getDistance();
   }

    /**
	 * Resets all of the encoders used in the program when teleop is initalized.
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
   
   public void resetEncoders(){
	   FRE.reset();
	   FLE.reset();
	   BRE.reset();
	   BLE.reset();   
   }
	
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
   public void fourBar(){
	   changeBarMode();
	   
	   if (barMode && !articulating)
		   fullBar(gamepad.getTwist());
	   else
		   topBar(gamepad.getTwist());
   }
   
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

   public void topBar(double val) {
	   if(val < 0 && upEncoder.get() < maxUp)
		   upperBar.set(0.1);
	   else if(val > 0 && upEncoder.get() > minUp)
		   upperBar.set(-0.1);
	   else
		   upperBar.set(0);
   }

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
    * <p>
    * Uses 4 encoders to get the encoder values needed.
    * @see Encoder
    */
   public void output() {
      // display the values from the encoder to the SmartDashboard
	  displayEncoderVals();

	  SmartDashboard.putString("GameData", DriverStation.getInstance().getGameSpecificMessage());	   
	  SmartDashboard.putString("startPos", startPos.getSelected().toString());
      
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