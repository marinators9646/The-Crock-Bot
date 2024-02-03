// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.motorcontrol.PWMSparkMax;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.Timer;
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
  private final PWMSparkMax leftRear = new PWMSparkMax(1);
  private final PWMSparkMax leftFront = new PWMSparkMax(2);
  private final PWMSparkMax rightRear = new PWMSparkMax(3);
  private final PWMSparkMax rightFront = new PWMSparkMax(4);
  private final PWMSparkMax feedWheel = new PWMSparkMax(5);
  private final PWMSparkMax launchWheel = new PWMSparkMax(6);
  private final MotorControllerGroup leftMotorGroup = new MotorControllerGroup(leftFront,leftRear);
  private final MotorControllerGroup rightMotorGroup = new MotorControllerGroup(leftFront,leftRear);
  private final DifferentialDrive iDrive = new DifferentialDrive(leftMotorGroup, rightMotorGroup);
  private final Timer timeSecretary = new Timer();

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
  public void teleopPeriodic() {}

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
