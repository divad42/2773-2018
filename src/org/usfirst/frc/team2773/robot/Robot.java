// Version 1.0.2
// Added a debug message, made various specific changes, bug fixes, and feature removals


package org.usfirst.frc.team2773.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.drive.MecanumDrive;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.CameraServer;




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
   public Spark lowerBox;
   
   public DigitalOutput lowestBar;
   public boolean goDown;
   
   public int fakeEncoderUp;
   public int fakeEncoderDown;
   
   public Joystick gamepad;
   public Joystick stick;
         
   public double distance;
   public int autoStep;
   
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

   //public Spark wench;
   
   public Timer timer;
   
   public CameraServer cameras;


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
      drive = new MecanumDrive(FL, BL, FR, BR);
      
      //Encoder Declaration
      FLE = new Encoder(2,3);
      FRE = new Encoder(4,5);
      BLE = new Encoder(6,7);
      BRE = new Encoder(0,1);
      
      BLE.setReverseDirection(true);
      FLE.setReverseDirection(true);
      
      //Grabber and Related Data
      grabR = new Spark(7);
      grabL = new Spark(5);
      //grabL.setInverted(true);

      //4 Bar parts being declared
      lowerBar = new Spark(6);
      lowerBar.setInverted(false);
      
      upperBar = new Spark(8);
      lowestBar = new DigitalOutput(8);
      goDown = false;
      
      fakeEncoderUp = 0;
      fakeEncoderDown = 0;
      barMode = false;
      barModePressed = false;
      
      //Climber related data
      //wench = new Spark(4);
      
      //Constraints 
      maxBoth = 10000;
      minBoth = -10000;
      maxDown = 10000;
      minDown = -10000;
      
      //Controller objects
      gamepad = new Joystick(0);
      stick = new Joystick(1);
      
      //Drive Variables 
      curXVel = 0.0;
      curYVel = 0.0;
      curRot = 0.0;
      accel = 0.03;
      
      // the radio buttons for selecting our starting position
      startLoc = new SendableChooser<>();
      startLoc.addDefault("Center", new Character('C'));
      startLoc.addObject("Left", new Character('L'));
      startLoc.addObject("Right", new Character('R'));
      SmartDashboard.putData("Starting Positions", startLoc);
      
      autoStep = 0;
      
      /*THERE SHOULD BE NO Target Position Radio Buttons in SmartDashboard
      targetPos = new SendableChooser<>();
      targetPos.addDefault("Left", new Character('L'));
      targetPos.addObject("Right", new Character('R'));
      SmartDashboard.putData("Target Position", targetPos);*/
      
      //Selecting Objective with RadioButtons in SmartDashboard 
      objectiveChoice = new SendableChooser<>();
      objectiveChoice.addDefault("Switch", new Integer(0));
      objectiveChoice.addObject("Scale", new Integer(1));
      objectiveChoice.addObject("Baseline", new Integer(2));
      SmartDashboard.putData("Target Objective", objectiveChoice);
      
      timer = new Timer();
      
      // Declares the cameras and sets their resolution to their respective maxima
      // "startAutomaticCapture" creates a "UsbCamera" object, on which we can call the "setResolution" method.
      cameras = CameraServer.getInstance();
      cameras.startAutomaticCapture(0).setResolution(1280, 720);
      //cameras.startAutomaticCapture(1).setResolution(1280, 720);
      
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
		startChar = startLoc.getSelected();     // the starting position
		objectInt = objectiveChoice.getSelected().intValue();		// whether we want the switch or scale
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
	}

	/**
	 * This function is called periodically during autonomous.
	 */
	@Override
	public void autonomousPeriodic() {
		
		output();
		
		if(autoStep == 0){
			if(timer.get() < 2 && startChar == 'C')
				System.out.println("tAkInG a snOoZE");
			else {
				autoStep++;
				timer.stop();
				timer.reset();
			}
		}
		if(autoStep == 1) {
			if(Math.abs(distFromEncoders()) <= 7.5 * distRate && startChar == 'C')
				moveFromCenter();
			else {
				autoStep++;
				startChar = targetChar;
				resetEncoders();
			}
		}
		if(autoStep >= 2) {
			/*if(objectInt == 1)
				driveScale(startChar, targetChar);
			else if(objectInt == 0) 
				driveSwitch(startChar, targetChar); */
			//else
					if(autoStep == 2)
				autoLine();
				
		}
      
	}
	
	public void moveFromCenter() {
		if(targetChar == 'R')
			drive(0, 1, 0);
		else
			drive(0, -1, 0);
		
		System.out.println("moving from center");
	}
	
   
   public void drive(double x, double y, double z) {
      
      // the robot smoothly accelerates at a rate determined by the magnitude of the
      // joystick's manipulation
      
      if(x > 0.3 && curXVel <= maxSpeed)
         curXVel += accel * x * 0.5;
      
      if(x < -0.3 && curXVel >= (-1 * maxSpeed))
         curXVel += accel * x * 0.5;
         
      if(y > 0.3 && curYVel <= maxSpeed)
         curYVel += accel * y * 0.5;
      
      if(y < -0.3 && curYVel >= (-1 * maxSpeed))
         curYVel += accel * y * 0.5;
      
      if(z > 0.3 || z < -0.3)
    	  curRot = z; 
      
      // if the joystick is in the resting position, setting the motor to zero
      // should cause the robot to drift.   
      if(x > -0.3 && x < 0.1)
         curXVel = 0;
         
      if(y > -0.1 && y < 0.1)
         curYVel = 0;
      
      if(z > -0.1 && z < 0.1)
         curRot = 0;
      
      drive.driveCartesian( curYVel, curXVel, curRot * 0.3);
      
      balanceMotors(.95);
   }
   
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
   
   
   // I did not decide this
   public double distFromEncoders() {
	   return FRE.getDistance();
   }
   
   public double distFromEncoder() {
      return FRE.get();
   }


   @Override
   public void teleopInit() {
	   FRE.reset();
	   FLE.reset();
	   BRE.reset();
	   BLE.reset();
	   barMode = false;
   }
   
   /**
	 *telopPeriodic    This function is called periodically during operator control.
	 */
	@Override
	public void teleopPeriodic() {
		
      maxSpeed = (-stick.getThrottle() + 1) / 2;
      
      drive(-stick.getY(), stick.getX(), stick.getTwist());
      fourBar();
      grabber();
      //climb();
       output();
	}

	
   public void grabber() {
	   
	  if(stick.getRawButton(1)){	// trigger on joystick
         grabL.set(-0.2);
         grabR.set(0.8);
      }
      else if(gamepad.getRawButton(8)){ 	// right trigger on gamepad
         grabL.set(-1);
         grabR.set(-1);
      }
      else if(stick.getRawButton(2)) {
    	  grabL.set(0.5);
    	  grabR.set(0.5);
      }
      else {
		   grabL.set(0);
		   grabR.set(0); 
      }
      
   }
	      
// code for controlling fourbar operations during teleop
   public void fourBar(){
	   
	   //changeBarMode();
	   
	   if (barMode )//&& !articulating)
		   fullBar(gamepad.getRawAxis(3));
	   else
		   topBar((gamepad.getRawAxis(3)));
	   
	   }
   
   
   void changeBarMode() {
	   if(gamepad.getRawButton(10) && !barModePressed) {
		   barMode = !barMode;
		   barModePressed = true;
		   //if(barMode)
			   //articulating = true;
	   } else if(!gamepad.getRawButton(10)) {
		   barModePressed = false;
	   }
	   
	   
	   /*if(articulating) {
		   if(fakeEncoderDown < fakeEncoderUp - 1) {
			   lowerBar(0.1);
			   fakeEncoderDown++;
		   }
		   else if(fakeEncoderDown > fakeEncoderUp + 1) {
			   lowerBar(-0.1);
			   fakeEncoderDown--;
		   }
		   else
			   articulating = false;
	   } */
   }

   public void topBar(double val) {
	   if(val > 0.15) {
		   upperBar.set(0.48);
		   fakeEncoderUp++;
	   }
	   else if(val < -0.15) {
		   upperBar.set(-0.48);
		   fakeEncoderUp--;
	   }
	   else
		   upperBar.set(0);
   }

   public void fullBar(double val) {
	   if(!articulating) {
		   if(val > 0.15) {
			   /*upperBar.set(0.5);
			   lowerBar(0.4);
			   fakeEncoderUp++;
			   fakeEncoderDown++;*/
			   System.out.println("positive");
		   }
		   else if(val < -0.15) {
			   /*upperBar.set(-0.25);
			   lowerBar(-1.00);
			   fakeEncoderUp--;
			   fakeEncoderDown--;*/
			   System.out.println("negative");
		   }
		   else {
			   upperBar.set(0);
			   lowerBar(0);
		   }
	   }
   }
   
   // articulates both lower bar motors
   void lowerBar(double val) {
	   lowerBar.set(val);			// CIM motor is about 5 times as powerful as the Bosch motor
   }
   
 /*  
   public void climb() {
	   if(stick.getRawButton(5))
		   wench.set(1);
	   else if(stick.getRawButton(6))
		   wench.set(-1);
	   else
		   wench.set(0);
   }
  */

   public void driveSwitch(char startPos, char switchSide) {
      if(autoStep == 2) { //moves the robot forwards to the middle of either side of the switch
         if(distFromEncoders() <= 11.59 * distRate) //drive it forward regardless of the side
            drive(1, 0, 0);
         else { //increments autoStep
            autoStep++;
            resetEncoders();
            drive(0, 0, 0);
         }
      }
      if(autoStep == 3) { //moves the robot past the switch if necessary.
         if(startPos == switchSide) 
            autoStep++;
         else {
            if(distFromEncoders() <= 5.75 * distRate)
               drive(1, 0, 0);
            else {
               autoStep++;
               resetEncoders();
               drive(0, 0, 0);
            }
         }
      }
      if(autoStep == 4) { //moving across the switch horizontally
         if(startPos == switchSide)
            autoStep++;
         else {
            if(switchSide == 'R') {
               if(distFromEncoders() >= -15.34 * distRate)
                  drive(0, -1, 0); //don't know how negatives work when swapping x and y values
               else {
                  autoStep++;
                  resetEncoders();
                  drive(0, 0, 0);
               }
            }
            else {
               if(distFromEncoders() <= 15.34 * distRate)
                  drive(0, 1, 0);
               else {
                  autoStep++;
                  resetEncoders();
                  drive(0, 0, 0);
               }
            }
         }         
      }
      if(autoStep == 5) {  //moves backwards to the desired location in the middle of each side.
         if(startPos == switchSide)  //increments autoStep because it is in position to rotate.
            autoStep++;
         else {
            if(distFromEncoders() >= -5.75 * distRate)
               drive(-1, 0, 0);
            else {
               autoStep++;
               resetEncoders();
               drive(0, 0, 0);
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
               drive(0, 0, 0);
            }
         else {
            if(distFromEncoders() >= -90 * degRate)
               drive(0, 0, -1);   
            else {
               autoStep++;
               resetEncoders();
               drive(0, 0, 0);
            }
         }
      }
      if(autoStep == 7) { //moves the fourbar up
         if(fakeEncoderUp <= maxBoth)
            topBar(0.5);
         else {
            autoStep++;
            //fakeEncoderUp = 0; //why???
            timer.reset();
            timer.start();
            topBar(0);
         }
         drive(0, 0, 0);
      }
      if(autoStep == 8) { //expels the cube from the grabber.
         if(timer.get() < 0.25) {
            grabL.set(-0.5);//do this for a few seconds
            grabR.set(-0.5);
         }
         else {
            autoStep++;
            grabL.set(0);
            grabR.set(0);
            timer.stop();
         }
         drive(0, 0, 0);
      }
      if(autoStep == 9) { //moves the fourbar back down.
         if(fakeEncoderUp >= minBoth)
            topBar(-0.5);
         else {
            topBar(0);
        	 autoStep++;
            fakeEncoderUp = 0; //why???
         } autoStep++;
         drive(0, 0, 0);
      }
      if(autoStep > 9)
         drive(0, 0, 0);       
   }
   
   public void autoLine() {
	      if (Math.abs(distFromEncoder()) < 2.5 * distRate) { //move from alliance wall to the scale
	         drive(1, 0, 0);
	         System.out.println("autoLine");
	      } else {
	         autoStep++;
	         resetEncoders();
	         drive(0, 0, 0);
	      }
	   }
   
   public void driveScale(char pos, char side) {
	      if(autoStep == 2) { //10 ft forward
	         autoLine();
	      }
	      if(autoStep == 3) {
	        if(pos == 'L') {
	           if(side == 'L') {
	              if (distFromEncoder() < 1.86 * distRate)   
	                  //moving right to align with scale plate
	                  drive(0,1,0);
	              else {
	                  drive(0,0,0);
	                  autoStep++;
	                  resetEncoders();
	              }
	           }
	           else {
	              if (distFromEncoder() < 15 * distRate)
	                  drive(0,1,0);
	              else {
	                  drive(0,0,0);
	                  autoStep++;
	                  resetEncoders();
	              }
	           }
	        }
	        else {
	           if(side == 'L') {
	              if (distFromEncoder() > -15 * distRate) {
	                  drive(0,-1,0);
	              }
	              else {
	                  drive(0,0,0);
	                  autoStep++;
	                  resetEncoders();
	              }
	           }
	           else {
	             if (distFromEncoder() < 1.86 * distRate){  
	                  //move left to align with right scale plate
	                  drive(0,-1,0);
	             }
	             else{
	                  drive(0,0,0);
	                  autoStep++;
	                  resetEncoders();
	             }

	           }
	        }
	      }
	      if(autoStep == 4) { 
	         if(fakeEncoderDown < maxBoth)
	            fullBar(1);
	         else
	            autoStep++;
	         drive(0, 0, 0);
	      }
	      if(autoStep == 5) {
	         if (distFromEncoder() < 1.333 * distRate) {
	            drive(0.5,0,0);
	         }
	         else {
	            drive(0,0,0);
	            autoStep++;
	            resetEncoders();
	            timer.reset();
	            timer.start();
	         }
	      }
	      if(autoStep == 6) {
	         //let go of block
	         if(timer.get() < 1) {
	            grabR.set(-0.5);
	            grabL.set(-0.5);
	         }
	         else {
	            //reset distance from Encoder
		        grabR.set(0);
		        grabL.set(0);
	        	 autoStep++;
	            timer.stop();
	         }
	         drive(0, 0, 0);            
	      }

	   } 
   
   
   public void resetEncoders()
   {
   	   FRE.reset();
	   FLE.reset();
	   BRE.reset();
	   BLE.reset();
   }

   
   public void displayEncoderVals(){ 
	
      SmartDashboard.putNumber("Value of Encoder FRE", FRE.get());
      SmartDashboard.putNumber("Value of Encoder FLE", FLE.get());
      SmartDashboard.putNumber("Value of Encoder BRE", BRE.get());
      SmartDashboard.putNumber("Value of Encoder BLE", BLE.get());

   }
   
   public void output() {
      // display the values from the encoder to the SmartDashboard
	  displayEncoderVals();

	  SmartDashboard.putString("GameData", DriverStation.getInstance().getGameSpecificMessage());	   
	  SmartDashboard.putString("startPos", startLoc.getSelected().toString());
      SmartDashboard.putNumber("Value of fake top bar encoder", fakeEncoderUp);
      SmartDashboard.putNumber("Value of fake bottom bar encoder", fakeEncoderDown);
      SmartDashboard.putBoolean("Is lower limit pressed", lowestBar.get());
      SmartDashboard.putBoolean("fullbar mode", barMode);
      
      
      SmartDashboard.putData("Starting Positions", startLoc);
      SmartDashboard.putData("Target Objective", objectiveChoice);
      
      SmartDashboard.putNumber("Step", autoStep);
      
      SmartDashboard.putNumber("Timer:", timer.get());
      
      SmartDashboard.putNumber("Max Speed", maxSpeed);
   }
   
   
   // this method prints values when the robot is disabled
   @Override
   public void disabledInit() {
	   System.out.println("Front Right Encoder: " + FRE.get());
	   System.out.println("Front Left Encoder: " + FLE.get());
	   System.out.println("Back Right Encoder: " + BRE.get());
	   System.out.println("Back Left Encoder: " + BLE.get());
	   
	   System.out.println("Fake Upper Encoder: " + fakeEncoderUp);
	   System.out.println("Fake Lower Encoder: " + fakeEncoderDown);
	   
	   System.out.println("Autonomous Step: " + autoStep);
	   
	   System.out.println("X Velocity: " + curXVel);
	   System.out.println("Y Velocity: " + curYVel);
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