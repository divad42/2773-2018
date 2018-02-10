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
	private static final String kDefaultAuto = "Default";
	private static final String kCustomAuto = "My Auto";
	//private String m_autoSelected;
	private SendableChooser<String> m_chooser = new SendableChooser<>();
   
   public final double TILE_DISTANCE_RATE = 1;
   public final double COMP_DISTANCE_RATE = 1;
   public double distRate; //= TILE or COMP rate 
   
   public Victor FL;
   public Victor FR;
   public Victor BL;
   public Victor BR;
   
   static public Encoder FLE;
   static public Encoder FRE;
   static public Encoder BLE;
   static public Encoder BRE;

   public Spark lowerBar;
   public Spark upperBar;
   public Encoder lowEncoder;
   public Encoder upEncoder;
   
   public MecanumDrive drive;
   public Joystick gamepad;
   public Joystick stick;
   
   public Encoder testEncoder;
   
   public PrintCommand printer;
   
   public double distance;
   public double grabLimit;
   public int autoStep;
   
   public double curXVel;
   public double curYVel;
   public double curRot;
   public double maxSpeed;
   public double accel;
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
      FLE = new Encoder(4,5);
      FRE = new Encoder(2,3);
      //BLE = new Encoder(6,7);
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
      //upEncoder = new Encoder(4, 5);
      //lowEncoder = new Encoder(6, 7);
      barMode = false;
      
      // these constants represent the limits of our 4-bar articulation
      maxUp = 360;
      minUp = -360;
      maxDown = 360;
      minDown = -360;
      
      // the joysticks
      gamepad = new Joystick(0);
      stick = new Joystick(1);
      
      // test encoder
      //testEncoder = new Encoder(0, 1);
      
      // drive variables
      curXVel = 0;
      curYVel = 0;
      curRot = 0;
      accel = 0.01;
            
      // this is necessary to print to the console
      printer = new PrintCommand("abcderfjkdjs");
      printer.start();
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
		/*m_autoSelected = m_chooser.getSelected();
		// m_autoSelected = SmartDashboard.getString("Auto Selector",
		// 		kDefaultAuto);
		System.out.println("Auto selected: " + m_autoSelected);*/
	  
      distance = 0;
      autoStep = 0;
	}

	/**
	 * This function is called periodically during autonomous.
	 */
	@Override
	public void autonomousPeriodic() {
		/*switch (m_autoSelected) {
			case kCustomAuto:
				// Put custom auto code here
				break;
			case kDefaultAuto:
			default:
				// Put default auto code here
				break;
		}*/
      
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
   
   
   public double speedFromEncoder() {
      return 4;
   }

   @Override
   public void teleopInit() {
	   testEncoder.reset();
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

	/**
	 * This function is called periodically during test mode.
	 */
	@Override
	public void testPeriodic() {
		
		
	}
   
   public void grabber() {
	   if(grabRot.get() == 0) // if it's at the base position
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
	      }
   }
   

   public void fourBar(){
	   if (barMode)
		   fullBar(gamepad.getTwist());
	   else
		   topBar(gamepad.getTwist());
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
	   
   }
   public static void displayEncoderRates(){
      double[] rates = new double[3];
      rates[0] = FRE.getRate();
      rates[1] = FLE.getRate(); 
      rates[2] = BRE.getRate(); 
      //rates[3] = BLE.getRate(); 
      
      for(int i = 0; i < rates.length; i
    		  							++) {
         SmartDashboard.putNumber("Rate of Encoder " + i, rates[i]);
      }
 
   }
   public void output() {
      displayEncoderRates();
	   DriverStation ds= DriverStation.getInstance();
      // display the values from the encoder to the SmartDashboard
	   SmartDashboard.putString("GameData", ds.getGameSpecificMessage());
	   SmartDashboard.putNumber("distance", testEncoder.getDistance());
		SmartDashboard.putBoolean("direction", testEncoder.getDirection());
		SmartDashboard.putNumber("rate", testEncoder.getRate());
      
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