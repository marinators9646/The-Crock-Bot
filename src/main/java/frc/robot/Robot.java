// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.XboxController;
// CAN troubleshooting
import edu.wpi.first.wpilibj.motorcontrol.MotorController;
import com.revrobotics.CANSparkLowLevel.MotorType;
import com.revrobotics.CANSparkLowLevel;
import com.revrobotics.CANSparkMax;
// Problematic Imports
/* import edu.wpi.first.wpilibj.motorcontrol.PWMSparkMax;
import static frc.robot.Constants.DrivetrainConstants.*;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.subsystems.CANDrivetrain;
import frc.robot.subsystems.CANLauncher; */
/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  private static final String kDefaultAuto = "Default"; // go across line only
  private static final String kSpeakerMiddle = "Speaker Middle and Backup";
  private static final String kRedLongAuto = "Red Long Speaker and Backup";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();
  private final CANSparkMax leftRear = new CANSparkMax(0, MotorType.kBrushed);
  private final CANSparkMax leftFront = new CANSparkMax(1, MotorType.kBrushed);
  private final CANSparkMax rightRear = new CANSparkMax(2, MotorType.kBrushed);
  private final CANSparkMax rightFront = new CANSparkMax(3, MotorType.kBrushed);
  private final CANSparkMax feedWheel = new CANSparkMax(4, MotorType.kBrushed);
  private final CANSparkMax launchWheel = new CANSparkMax(5, MotorType.kBrushed);
  private final MotorControllerGroup leftMotorGroup = new MotorControllerGroup(leftFront,leftRear);
  private final MotorControllerGroup rightMotorGroup = new MotorControllerGroup(rightFront,rightRear);
  private final DifferentialDrive iDrive = new DifferentialDrive(leftMotorGroup, rightMotorGroup);
  private final Timer timeSecretary = new Timer();
  private final XboxController driverController = new XboxController(0);
  private final XboxController operatorController = new XboxController(1);
  double driveLimit = 1;
  double launchPower = 0;
  double feedPower = 0;

  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  @Override
  public void robotInit() {
    m_chooser.setDefaultOption("Default", kDefaultAuto);
    m_chooser.addOption("Speaker Middle and Backup", kSpeakerMiddle);
    m_chooser.addOption("Red Long Speaker and Backup", kRedLongAuto);
    SmartDashboard.putData("Auto Choices", m_chooser);
    leftMotorGroup.setInverted(true);
    rightMotorGroup.setInverted(false); // intentional redundancy
    feedWheel.setInverted(true);
    launchWheel.setInverted(true);
    timeSecretary.start();
  }

  /**
   * This function is called every 20 ms, no matter the mode. Use this for items like diagnostics
   * that you want ran during disabled, autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before LiveWindow and
   * SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {}

  /**
   * This autonomous (along with the chooser code above) shows how to select between different
   * autonomous modes using the dashboard. The sendable chooser code works with the Java
   * SmartDashboard. If you prefer the LabVIEW Dashboard, remove all of the chooser code and
   * uncomment the getString line to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional comparisons to the switch structure
   * below with additional strings. If using the SendableChooser make sure to add them to the
   * chooser code above as well.
   */
  @Override
  public void autonomousInit() {
    m_autoSelected = m_chooser.getSelected();
    // m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
    System.out.println("Auto selected: " + m_autoSelected);
    timeSecretary.reset();
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {
    switch (m_autoSelected) {
      case kRedLongAuto:
        double redLongMovement1End = 3;
        double redLongMovement2End = redLongMovement1End + 2;
        double redLongMovement3End = redLongMovement2End + 1.5;
        double redLongMovement4End = redLongMovement3End + 1;
        double redLongMovement5End = redLongMovement4End + 2;
        if (timeSecretary.get() < redLongMovement1End) { // spool up the launch wheel
          iDrive.tankDrive(0,0); // intentional redundancy
          launchWheel.set(1);
          feedWheel.set(0);
        } else if (timeSecretary.get() < redLongMovement2End) { // turn on feed wheel to launch the note
          launchWheel.set(1);
          feedWheel.set(1);
        } else if (timeSecretary.get() < redLongMovement3End) {
          launchWheel.set(0);
          feedWheel.set(0);
          iDrive.tankDrive(-.5, -.5);
        } else if (timeSecretary.get() < redLongMovement4End) {
          launchWheel.set(0);
          feedWheel.set(0);
          iDrive.tankDrive(.4, -.4);
        } else if (timeSecretary.get() < redLongMovement5End) {
          iDrive.tankDrive(-.5, -.5);
        } else { // turn off all motors
          launchWheel.set(0);
          feedWheel.set(0);
          iDrive.tankDrive(0,0);
        }
        break;
      case kSpeakerMiddle: // start middle speaker launch then backup
        double speakerMiddleMovement1End = 3;
        double speakerMiddleMovement2End = speakerMiddleMovement1End + 2;
        double speakerMiddleMovement3End = speakerMiddleMovement2End + 1.5;
        /* The above variables are temporary.
        Basically, every time you want something to continue for a set amount of time,
        you need to sum up the time it took for all previous actions to occur,
        and then add the time you want for the current action to occur.
        An Array or ArrayList will eventually replace these variables
        (and every other "movementEnd" variable for that matter)
        so that there can just be one method
        that takes the action number as an input
        and returns the sum of all the elements in the array until reaching the desired action time. */
        if (timeSecretary.get() < speakerMiddleMovement1End) { // spool up the launch wheel
          iDrive.tankDrive(0,0); // intentional redundancy
          launchWheel.set(1);
        } else if (timeSecretary.get() < speakerMiddleMovement2End) { // turn on feed wheel to launch the note
          launchWheel.set(1);
          feedWheel.set(1);
        } else if (timeSecretary.get() < speakerMiddleMovement3End) {
          launchWheel.set(0);
          feedWheel.set(0);
          iDrive.tankDrive(-.5, -.5);
        } else { // turn off all motors
          launchWheel.set(0);
          feedWheel.set(0);
          iDrive.tankDrive(0,0);
        }
        break;
      case kDefaultAuto:
      default: // just crossing the line for points
        double defaultMovement1End = 1.5;
        if (timeSecretary.get() < defaultMovement1End) {
          iDrive.tankDrive(-.5, -.5);
        } else {
          iDrive.tankDrive(0,0);
        }
        break;
    }
  }

  /** This function is called once when teleop is enabled. */
  @Override
  public void teleopInit() {}

  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {
    if (driverController.getLeftBumper()) {
      // if (driveLimit > .1) {driveLimit -= .1;}
      driveLimit = .5;
    } else if (driverController.getRightBumper()) {
      // if (driveLimit < 1) {driveLimit += .1;}
      driveLimit = 1;
    }
    // iDrive.tankDrive(-driveLimit*driverController.getLeftY(), -driveLimit*driverController.getRightY());
    iDrive.arcadeDrive(-driveLimit*driverController.getLeftY(), -driveLimit*driverController.getRightX());
    
    // BORDER BETWEEN DRIVER CODE AND OPERATOR CODE

    if (operatorController.getLeftBumper()) {
      launchPower = -1;
      feedPower = -.2;
    } else {
      if (operatorController.getAButtonPressed()) { // at the beginnng of the period, make sure no note is in the barel
        timeSecretary.reset();
      }
      if (timeSecretary.get() < 1.0) {
        launchPower = 1;
        feedPower = 0; // intentional redundancy
      } else if (timeSecretary.get() < 2.0) {
        launchPower = 1;
        feedPower = 1;
      } else {
        launchPower = 0;
        feedPower = 0;
      }
    }
    launchWheel.set(launchPower);
    feedWheel.set(feedPower);
  }

  /** This function is called once when the robot is disabled. */
  @Override
  public void disabledInit() {}

  /** This function is called periodically when disabled. */
  @Override
  public void disabledPeriodic() {}

  /** This function is called once when test mode is enabled. */
  @Override
  public void testInit() {}

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {}

  /** This function is called once when the robot is first started up. */
  @Override
  public void simulationInit() {}

  /** This function is called periodically whilst in simulation. */
  @Override
  public void simulationPeriodic() {}
}
