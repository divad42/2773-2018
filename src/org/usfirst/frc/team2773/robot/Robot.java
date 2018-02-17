// Version 1.0.0
// Updated grabber code and upper bar code and added rudimentary auto and encoder functions

package org.usfirst.frc.team2773.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.command.PrintCommand;
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
   
   public Victor FL;
   public Victor FR;
   public Victor BL;
   public Victor BR;
   public MecanumDrive drive;
   
   static public Encoder FLE;
   static public Encoder FRE;
   static public Encoder BLE;
   static public Encoder BRE;

   public Spark upperBar;
   public Spark lowerBar;
   //public Encoder lowEncoder;
   //public Encoder upEncoder;
   
   
   public Joystick gamepad;
   public Joystick stick;
         
   public double distance;
   public double grabLimit;
   public int autoStep;
   
   public double curXVel;
   public double curYVel;
   public double curRot;
   public double accel;

   
   public double maxSpeed;
   public static double maxUp;
   public static double maxDown;
   public static double minUp;
   public static double minDown;
   
   public Spark grab;
   public Encoder grabRot;
   
   public boolean isClosed;
   public boolean barMode;
   public boolean articulating;

   


	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
   
      distRate = TILE_DISTANCE_RATE; 
     	
      //Wheel and Drive Objects
      FL = new Victor(3);
      FR = new Victor(0);
      BL = new Victor(1);
      BR = new Victor(2);
      drive = new MecanumDrive(FL, BL, FR, BR);
      
      //Encoder Declaration
      FLE = new Encoder(2,3);
      FRE = new Encoder(4,5);
      BLE = new Encoder(6,7);
      BRE = new Encoder(0,1);
      
      //Grabber and Related Data
      grab = new Spark(4);
      isClosed = true;
      grabLimit = 0;
      articulating = false;

      //4 Bar parts being declared
      lowerBar = new Spark(6);
      upperBar = new Spark(7);
      //upEncoder = new Encoder(4, 5);
      //lowEncoder = new Encoder(6, 7);
      barMode = false;
      
      //Constraints 
      maxUp = 360;
      minUp = -360;
      maxDown = 360;
      minDown = -360;
      
      //Controller objects
      gamepad = new Joystick(0);
      stick = new Joystick(1);
      
      //Drive Variables 
      curXVel = 0.0;
      curYVel = 0.0;
      curRot = 0.0;
      accel = 0.01;
            
      // the radio buttons for selecting our starting position
      startPos = new SendableChooser<>();
      startPos.addDefault("Center", new Character('C'));
      startPos.addObject("Left", new Character('L'));
      startPos.addObject("Right", new Character('R'));
      SmartDashboard.putData("Starting Positions", startPos);
      
      //Target Position Radio Buttons in SmartDashboard
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
		startChar = startPos.getSelected();
		targetChar = targetPos.getSelected();
		objectInt = objectiveChoice.getSelected().intValue();
		
		FRE.reset();
		FLE.reset();
		BRE.reset();
		BLE.reset();
		
	}

	/**
	 * This function is called periodically during autonomous.
	 */
	@Override
	public void autonomousPeriodic() {
		double distance;
		if(autoStep == 0){
			if(currentTime < idk)
				System.out.print("Taking A Snooze");
			else
				autoStep++;
		}
		if(autoStep == 1) {
			if(distance <= distGoal)
				moveFromCenter();
			else 
				autoStep++;
		}
		if(autoStep >= 2) {
			if(objectInt == 1)
				driveScale(startChar, DriverStation.getInstance().getGameSpecificMessage().charAt(1));
			else if(objectInt == 0) 
				driveSwitch(startChar, DriverStation.getInstance().getGameSpecificMessage().charAt(0));
			else
				autoLine();
				
		}
      
	}
   
   public void drive(double x, double y, double z) {
      
      // the robot smoothly accelerates at a rate determined by the magnitude of the
      // joystick's manipulation
      
      if(x > 0 && curXVel <= maxSpeed)
         curXVel += accel * x;
      
      if(x < 0 && curXVel >= (-1 * maxSpeed))
         curXVel += accel * x;
         
      if(y > 0 && curYVel <= maxSpeed)
         curYVel += accel * y;
      
      if(y < 0 && curYVel >= (-1 * maxSpeed))
         curYVel += accel * y;
         
      if(z > 0 && curRot <= maxSpeed)
         curRot += accel * z;
     
      if(z < 0 && curRot >= (-1 * maxSpeed))
         curRot += accel * z;
         
      // if the joystick is in the resting position, setting the motor to zero
      // should cause the robot to drift.   
      if(x > -0.1 && x < 0.1)
         curXVel = 0;
         
      if(y > -0.1 && y < 0.1)
         curYVel = 0;
         
      if(z > -0.1 && z < 0.1)
         curRot = 0;
         
      drive.driveCartesian( curYVel, curXVel, curRot );
   }

   //there be dragons here
   public double speedFromEncoder() {
      return 4;
   }
   
   public double distFromEncoders() {
	   return FLE.getDistance();
   }

   @Override
   public void teleopInit() {
	   FRE.reset();
	   FLE.reset();
	   BRE.reset();
	   BLE.reset();
   }
   
   /**
	 * This function is called periodically during operator control.
	 */
	@Override
	public void teleopPeriodic() {
		
      maxSpeed = (-stick.getThrottle() + 1) / 2;
      
      drive(-stick.getY(), stick.getX(), stick.getTwist());
      fourBar();
      grabber();
      output();
	}

	
   public void grabber() {
	   if(stick.getRawButton(1)){	// trigger on joystick
         grab.set(0.5);	
         isClosed = true;
      }
      else if(gamepad.getRawButton(8)){ 	// right trigger on gamepad
         grab.set(-0.5);
         isClosed = false;
      }
      else if(isClosed)
         grab.set(0.01);
   }
	      
      
      
   public void fourBar(){

	   if (barMode)
		   fullBar(gamepad.getTwist());
	   else
		   topBar(gamepad.getTwist());
   }
   
   //there be dragons here
   public void topBar(double val) {
	   /*if(val < 0 && upEncoder.get() < maxUp)
		   upperBar.set(0.1);
	   else if(val > 0 && upEncoder.get() > minUp)
		   upperBar.set(-0.1);
	   else
		   upperBar.set(0);*/
   }
   
   //there be dragons here

   public void fullBar(double val) {
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