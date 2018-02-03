// Version 0.0.2
// Added primitive autonomous code

package org.usfirst.frc.team2773.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.command.PrintCommand;
import edu.wpi.first.wpilibj.drive.MecanumDrive;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Encoder;


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
	private String m_autoSelected;
	private SendableChooser<String> m_chooser = new SendableChooser<>();
   
   public Victor FL;
   public Victor FR;
   public Victor BL;
   public Victor BR;
   
   public Victor grabL;
   public Victor grabR;
   
   public MecanumDrive drive;
   public Joystick gamepad;
   public Joystick stick;
   
   public Encoder testEncoder;
   
   public PrintCommand printer;
   
   public double dist;
   
   public double curXVel;
   public double curYVel;
   public double curRot;
   
   public double spdMult;
   public boolean driving;
   
   public int autoStep;

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		m_chooser.addDefault("Default Auto", kDefaultAuto);
		m_chooser.addObject("My Auto", kCustomAuto);
		SmartDashboard.putData("Auto choices", m_chooser);
      
      // wheels
      FL = new Victor(0);
      FR = new Victor(1);
      BL = new Victor(2);
      BR = new Victor(3);
      
      drive = new MecanumDrive(FL, BL, FR, BR);
      
      // grabber
      grabL = new Victor(4);
      grabR = new Victor(5);
      
      gamepad = new Joystick(0);
      stick = new Joystick(1);
      
      testEncoder = new Encoder(0, 1);
      
      printer = new PrintCommand("abcderfjkdjs");
      printer.start();
      
      curXVel = 0;
      curYVel = 0;
      curRot = 0;
      
      multiplier = 0;
      driving = false;
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
		System.out.println("Auto selected: " + m_autoSelected);*/'
      
      setDist(0);
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
      
      if(autoStep == 0 && dist < 12)
         drive.driveCartesian(0, 1, 0);
      else if(autoStep == 0) {
         drive.driveCartesian(0, 0, 0);
         autoStep ++;
      }
      
	}

	/**
	 * This function is called periodically during operator control.
	 */
	@Override
	public void teleopPeriodic() {
      /*drive.driveCartesian(gamepad.getRawAxis(1), gamepad.getRawAxis(0), gamepad.getRawAxis(2));
      grabber();*/
      
      output();
	}

	/**
	 * This function is called periodically during test mode.
	 */
	@Override
	public void testPeriodic() {
		
		
	}
   
   public void setGrabbers(double val) {
      grabL.set(val);
      grabR.set(val);
   }
   
   public void grabber() {
      if(stick.getRawButton(1))  // trigger is used to eject the cube
         setGrabbers(0.5);
      else if(stick.getRawButton(2))   // side button is used to take in the cube
         setGrabbers(-0.5);
   }
   
   public void output() {
	   SmartDashboard.putNumber("distance", testEncoder.getDistance());
		SmartDashboard.putBoolean("direction", testEncoder.getDirection());
		SmartDashboard.putNumber("rate", testEncoder.getRate());
   }
   
   public void setDist(double val) {
      dist = val;
   }
   
   public double getDist() {
      return dist;
   }
   
   //Max Set speed = 1
   public void drive(double xVel, double yVel, double rotation){
      addVel(xVel, curXVel);
      addVel(yVel, curYVel);
      addVel(rotation, curRot);      
      drive.driveCartesian(curXVel, curYVel, curRot);      
   }
   
   public void addVel(double val, double addTo){
        if (vel != 0){
             addTo += val / 50;  
        }else{
            addTo = 0;
        }         
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