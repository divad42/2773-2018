// Version 1.0.0


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
	private static final String kDefaultAuto = "Default";
	private static final String kCustomAuto = "My Auto";
	private SendableChooser<String> m_chooser = new SendableChooser<>();
   
   public final double TILE_DISTANCE_RATE = 1;
   public final double COMP_DISTANCE_RATE = 1;
   public double distRate; //= TILE or COMP rate 
   
   //Vect- I mean Victors
   public Victor FL;
   public Victor FR;
   public Victor BL;
   public Victor BR;
   
   //Super Ultra Important Epic Awesome One-Of-A-Kind Exclusive Encoders
   static public Encoder FLE;
   static public Encoder FRE;
   static public Encoder BLE;
   static public Encoder BRE;

   //Used to control the lower bar and the upper bar
   public Spark lowerBar;
   public Spark upperBar;
   
   //Drives and Joysticks
   public MecanumDrive drive;
   public Joystick gamepad;
   public Joystick stick;
   
   //Numeral Data
   public double distance;
   public double grabLimit;
   public int autoStep;
   
   //Helps control the speed of the driver method
   public double curXVel;
   public double curYVel;
   public double curRot;
   public double maxSpeed;
   public double accel;
   
   //Variables controlling the Minimums and Maximums of the grabber
   public static double maxUp;
   public static double maxDown;
   public static double minUp;
   public static double minDown;
   
   //Grabber Stuff
   public Spark grab;
   public Encoder grabRot;
   
   //Booleans of the Grabber
   public boolean isClosed;
   public boolean barMode;
   public boolean articulating;

   /**
 * Initalizes all of the needed variables to operate the robot.
 * <p>
 * All other global variables that are not initalized are required to go here.
 */
	@Override
	public void robotInit() {
   
      distRate = COMP_DISTANCE_RATE; 
     
		m_chooser.addDefault("Default Auto", kDefaultAuto);
		m_chooser.addObject("My Auto", kCustomAuto);
		SmartDashboard.putData("Auto choices", m_chooser);
      
      // wheels
      FL = new Victor(3);
      FR = new Victor(0);
      BL = new Victor(1);
      BR = new Victor(2);
      
      drive = new MecanumDrive(FL, BL, FR, BR);
      //PORT NUMS TEMPORARY!!!
      FLE = new Encoder(2,3);
      FRE = new Encoder(4,5);
      BLE = new Encoder(6,7);
      BRE = new Encoder(0,1);
      
      // grabber
      grab = new Spark(4);
      //grabRot = new Encoder(2, 3);
      isClosed = true;
      grabLimit = 0;
      articulating = false;

      // 4 bar
      lowerBar = new Spark(6);
      upperBar = new Spark(7);
      barMode = false;
      
      // these constants represent the limits of our 4-bar articulation
      maxUp = 360;
      minUp = -360;
      maxDown = 360;
      minDown = -360;
      
      // the joysticks
      gamepad = new Joystick(0);
      stick = new Joystick(1);
      
      // drive variables
      curXVel = 0;
      curYVel = 0;
      curRot = 0;
      accel = 0.01;
            
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
 * Drives the actual robot using the given double variables.
 * <p>
 * Uses the changeSpeed method.
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
	 * Changes the requested value (curVel) by the requested value (val) and returns it
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
	 * Resets all of the encoders used in the program when teleop is initalized.
    * <p>All other encoders that need to be reset will need to go here.
	 */
   @Override
   public void teleopInit() {
	   FRE.reset();
	   FLE.reset();
	   BRE.reset();
	   BLE.reset();
   }
   
   /**
	 * Allows the teleoperator to control the robot using the joystick.
    * <p>When used, updates the SmartDashboard via the output() method
    * <p>Drives using the drive() method
    * <p>Can only be used during teleop.
	 */
	@Override
	public void teleopPeriodic() {
		
      maxSpeed = (-stick.getThrottle() + 1) / 2;
      
      drive(-stick.getY(), stick.getX(), stick.getTwist());
      fourBar();
      grabber();
      output();
	}

	/**
	 * This isn't even used. Why is it even here?
	 */
	@Override
	public void testPeriodic() {
		
		
	}
   
   public void grabber() {
	   /*if(grabRot.get() == 0) // if it's at the base position
	      {
	      if(stick.getRawButton(1) && !isClosed && !articulating)  	// trigger on joystick
	          articulating = true;							
	      else if(gamepad.getRawButton(8) && isClosed && !articulating)	// right trigger on gamepad
	    	  articulating = true;
	      }   
	   
	   	  if(articulating && isClosed)
	   		  grab.set(0.5);
	   	  else if(articulating)
	   		  grab.set(-0.5);
	   
	      if(grabRot.get() >= grabLimit)
	      {
	         grab.set(0);
	         grabRot.reset();
	         isClosed = true;
	         articulating = false;
	      }
	      
	      if(grabRot.get() <= -grabLimit)
	      {
	         grab.set(0);
	         grabRot.reset();
	         isClosed = false;
	         articulating = false;
	      }*/
   }
   

   public void fourBar(){
	   if (barMode)
		   fullBar(gamepad.getTwist());
	   else
		   topBar(gamepad.getTwist());
   }
   
   public void topBar(double val) {
	   /*if(val < 0 && upEncoder.get() < maxUp)
		   upperBar.set(0.1);
	   else if(val > 0 && upEncoder.get() > minUp)
		   upperBar.set(-0.1);
	   else
		   upperBar.set(0);*/
   }

   public void fullBar(double val) {
	   
   }
   
      /**
 * Displays the encoder values in the SmartDashboard.
 * <p>
 * Uses 4 encoders to get the encoder values needed.
 * @see Encoder
 */
   public static void displayEncoderVals(){
      double[] vals = new double[4];
      vals[0] = FRE.get();
      vals[1] = FLE.get(); 
      vals[2] = BRE.get(); 
      vals[3] = BLE.get(); 
      
      for(int i = 0; i < vals.length; i++) {
         SmartDashboard.putNumber("Value of Encoder " + i, vals[i]);
      }
 
   }
   
   /**
   * Outputs the Encoder Values and the Gamedata
   * <p>
   * Uses the displayEncoderVals() method
   */
   public void output() {
      displayEncoderVals();
	   DriverStation ds = DriverStation.getInstance();
      // display the values from the encoder to the SmartDashboard
	   SmartDashboard.putString("GameData", ds.getGameSpecificMessage());
      
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