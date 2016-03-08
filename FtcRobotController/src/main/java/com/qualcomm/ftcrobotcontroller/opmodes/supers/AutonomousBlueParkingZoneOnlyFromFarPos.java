package com.qualcomm.ftcrobotcontroller.opmodes.supers;

import android.graphics.Bitmap;
import android.hardware.Camera;

import com.kauailabs.navx.ftc.AHRS;
import com.kauailabs.navx.ftc.navXPIDController;
import com.qualcomm.ftcrobotcontroller.CameraPreview;
import com.qualcomm.ftcrobotcontroller.Keys;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * Created by matt quan on 2/18/2016.
 */
public class AutonomousBlueParkingZoneOnlyFromFarPos extends LinearOpMode {
    DcMotor fr, fl, bl, br, collector;
    Servo swivel, dump, hopperLeft, climber, hang, clampRight, clampLeft, hopperRight, triggerRight, triggerLeft,buttonPusher;
    AnalogInput sonarAbovePhone, sonarFoot;
    //double a3,a4,a5;
    private AHRS navx_device;
    private navXPIDController yawPIDController;
    boolean calibration_complete = false;
    @Override
    public void runOpMode() throws InterruptedException {
        buttonPusher = hardwareMap.servo.get(Keys.buttonPusher);
        climber = hardwareMap.servo.get(Keys.climber);
        swivel = hardwareMap.servo.get(Keys.swivel);
        hang = hardwareMap.servo.get(Keys.hang);
        hopperLeft = hardwareMap.servo.get(Keys.hopperLeft);
        hopperRight = hardwareMap.servo.get(Keys.hopperRight);
        clampLeft = hardwareMap.servo.get(Keys.clampLeft);
        clampRight = hardwareMap.servo.get(Keys.clampRight);
        dump = hardwareMap.servo.get(Keys.dump);
        triggerLeft = hardwareMap.servo.get(Keys.triggerLeft);
        triggerRight = hardwareMap.servo.get(Keys.triggerRight);
        fr = hardwareMap.dcMotor.get(Keys.frontRight);
        fl = hardwareMap.dcMotor.get(Keys.frontLeft);
        bl = hardwareMap.dcMotor.get(Keys.backLeft);
        br = hardwareMap.dcMotor.get(Keys.backRight);
        collector = hardwareMap.dcMotor.get(Keys.collector);
        fl.setDirection(DcMotor.Direction.REVERSE);
        bl.setDirection(DcMotor.Direction.REVERSE);
        buttonPusher.setPosition(Keys.BUTTON_PUSHER_INIT);
        dump.setPosition(Keys.DUMP_INIT);
        swivel.setPosition(Keys.SWIVEL_CENTER);
        hang.setPosition(Keys.HANG_INIT);
        hopperLeft.setPosition(Keys.HL_STORE);
        hopperRight.setPosition(Keys.HR_STORE);
        clampLeft.setPosition(Keys.CLAMP_LEFT_INIT);
        clampRight.setPosition(Keys.CLAMP_RIGHT_INIT);
        triggerLeft.setPosition(Keys.LEFT_TRIGGER_INIT);
        triggerRight.setPosition(Keys.RIGHT_TRIGGER_INIT);
        climber.setPosition(Keys.CLIMBER_INITIAL_STATE);
        collector.setDirection(DcMotor.Direction.REVERSE);
        sonarAbovePhone = hardwareMap.analogInput.get(Keys.SONAR_ABOVE_PHONE);
        sonarFoot = hardwareMap.analogInput.get(Keys.SONAR_FOOT);
        navx_device = AHRS.getInstance(hardwareMap.deviceInterfaceModule.get(Keys.advancedSensorModule), Keys.NAVX_DIM_I2C_PORT, AHRS.DeviceDataType.kProcessedData, Keys.NAVX_DEVICE_UPDATE_RATE_HZ);
        while (!calibration_complete) {
            calibration_complete = !navx_device.isCalibrating();
            if (!calibration_complete) {
                telemetry.addData("Calibration Complete?", "No");
            }
        }
        telemetry.addData("Calibration Complete?", "Yes");
        //telemetry.addData("Start Autonomous?", "Yes");
        waitForStart();
        //todo ADD NAVIGATION
        moveAlteredSin(90, false);
        telemetry.addData("Stop Autonomous Here?", "YES");
    }

    public void adjustToThisDistance(double distance, AnalogInput sonar) {
        double myPosition  = readSonar(sonar);
        telemetry.addData("myPos",myPosition);
        if (readSonar(sonar)<distance-Keys.SONAR_TOLERANCE) {
            telemetry.addData("if","readSonar<distance");
            while(readSonar(sonar)<distance-Keys.SONAR_TOLERANCE) {
                telemetry.addData("while","looping3");
                telemetry.addData("mySonar",readSonar(sonar));
                telemetry.addData("dist",distance);
                setMotorPowerUniform(.25, true);
                telemetry.addData("bool read<dist+tol",readSonar(sonar)<distance-Keys.SONAR_TOLERANCE);
            }
        }
        else if (myPosition>distance+Keys.SONAR_TOLERANCE) {
            telemetry.addData("if","readSonar<distance");
            while (readSonar(sonar)>distance+Keys.SONAR_TOLERANCE) {
                telemetry.addData("while", "looping");
                telemetry.addData("mySonar",readSonar(sonar));
                telemetry.addData("dist",distance);
                setMotorPowerUniform(.25, false);
                telemetry.addData("bool read>dist+tol", readSonar(sonar) > distance + Keys.SONAR_TOLERANCE);
            }
        }
        rest();
        telemetry.addData("sonar","done");
        rest();
    }

    //returns sonar values in inches!!!
    public double readSonar(AnalogInput sonar) {
        double sValue = sonar.getValue();
        sValue = sValue/2;
        return sValue;
    }

    public void moveStraight (double dist, boolean backwards, double power) {

        double rotations = dist / (Keys.WHEEL_DIAMETER * Math.PI);
        double totalTicks = rotations * 1120 * 3 / 2;
        int positionBeforeMovement = fl.getCurrentPosition();
        if (backwards) {
            while (fl.getCurrentPosition()>positionBeforeMovement-totalTicks) {
                setMotorPowerUniform(power, backwards);
            }
        }
        else {
            while (fl.getCurrentPosition() < positionBeforeMovement + totalTicks) {
                setMotorPowerUniform(power, backwards);
            }
        }
        rest();
    }

    public void moveAlteredSin(double dist, boolean backwards) {
        //inches

        double rotations = dist / (Keys.WHEEL_DIAMETER * Math.PI);
        double totalTicks = rotations * 1120 * 3 / 2;
        int positionBeforeMovement = fl.getCurrentPosition();
        while (fl.getCurrentPosition() < positionBeforeMovement + totalTicks) {
            telemetry.addData("front left encoder: ", "sin" + fl.getCurrentPosition());
            telemetry.addData("ticksFor", totalTicks);
            collector.setPower(-.5);
            //convert to radians
            int currentTick = fl.getCurrentPosition() - positionBeforeMovement;
            //accelerate 15% of time
            //coast 25% of time
            //decelerate 60% of time
            int firstSectionTime = (int) Math.round(.1 * totalTicks);
            int secondSectionTime = (int) (Math.round((.1 + .25) * totalTicks));
            //rest will just be 100%
            double power;
            if (currentTick < firstSectionTime) {

                power = .3 * Math.cos((currentTick) * Math.PI / totalTicks + Math.PI) + .4;

                power += .1;
                //first quarter (period = 2pi) of sin function is only reaching altitude

            } else if (currentTick < secondSectionTime) {
                power = .8;

            } else {
                // between [40%,100%]
                //decrease time
                int ticksLeft = (int) Math.round(currentTick - (totalTicks * .35));
                //with these ticks left, set a range within cosine to decrease
                power = .4 * Math.cos((ticksLeft) * Math.PI / totalTicks) + .4;
            }

            telemetry.addData("power", power);
            setMotorPowerUniform(power, backwards);
        }
        rest();
    }

    public void setMotorPowerUniform(double power, boolean backwards) {
        int direction = 1;
        if (backwards) {
            direction = -1;
        }
        fr.setPower(direction*power);
        fl.setPower(direction*power);
        bl.setPower(direction*power);
        br.setPower(direction * power);
        //collector.setPower(-.5);

    }
    public void rest() {
        fr.setPower(0);
        fl.setPower(0);
        bl.setPower(0);
        br.setPower(0);
        collector.setPower(0);
    }

    public void turnLeft (double power) {
        fr.setPower(power);
        br.setPower(power);
    }
    public void turnRight (double power) {
        fl.setPower(power);
        bl.setPower(power);
    }
    public void gyroTurn (double degrees, boolean buttFirst) {
        //degrees=degrees*-1;

        yawPIDController = new navXPIDController(navx_device, navXPIDController.navXTimestampedDataSource.YAW);
        yawPIDController.setSetpoint(degrees);
        yawPIDController.setContinuous(true);
        yawPIDController.setOutputRange(Keys.MAX_SPEED * -1, Keys.MAX_SPEED);
        yawPIDController.setTolerance(navXPIDController.ToleranceType.ABSOLUTE,Keys.TOLERANCE_LEVEL_2);

        yawPIDController.enable(true);
        int DEVICE_TIMEOUT_MS = 500;
        navXPIDController.PIDResult yawPIDResult = new navXPIDController.PIDResult();

        telemetry.addData("Yaw", navx_device.getYaw());
        double degreesNow = navx_device.getYaw();
        double degreesToGo = degreesNow+degrees;
        //telemetry.addData("if state",navx_device.getYaw());
        //telemetry.addData("other if",degreesToGo);
        telemetry.addData("boolean",navx_device.getYaw()>degreesToGo);
        //telemetry.addData("boolean",navx_device.getYaw()<degreesToGo);
        if (navx_device.getYaw()>degreesToGo) {
            telemetry.addData("if","getYaw>degrees");
            telemetry.addData("more boolean",!(degreesToGo-Keys.TOLERANCE_LEVEL_1<navx_device.getYaw()&&navx_device.getYaw()<degreesToGo+Keys.TOLERANCE_LEVEL_1));
            while (!(degreesToGo-Keys.TOLERANCE_LEVEL_1<navx_device.getYaw()&&navx_device.getYaw()<degreesToGo+Keys.TOLERANCE_LEVEL_1))
            {
                collector.setPower(-.5);
                telemetry.addData("while","turningLeft1");
                double turnPower = .8;
                if (buttFirst) {
                    turnPower=-.8;
                    turnRight(turnPower);
                }
                else {
                    turnLeft(turnPower);
                }
                telemetry.addData("if",".yaw"+navx_device.getYaw()+"toGo"+degreesToGo);
            }
            telemetry.addData("more boolean2",navx_device.getYaw()>degreesToGo+Keys.TOLERANCE_LEVEL_2);
            while (navx_device.getYaw()>degreesToGo+Keys.TOLERANCE_LEVEL_2)
            {
                collector.setPower(-.5);
                telemetry.addData("while","turningLeft2");
                double turnPower = .75;
                if (buttFirst) {
                    turnPower=-1*turnPower;
                    turnRight(turnPower);
                }
                else {
                    turnLeft(turnPower);
                }
                telemetry.addData("if",".yaw"+navx_device.getYaw()+"toGo"+degreesToGo);
            }
            while (!(degreesToGo-Keys.TOLERANCE_LEVEL_3<navx_device.getYaw()&&navx_device.getYaw()<degreesToGo+Keys.TOLERANCE_LEVEL_3))
            {
                collector.setPower(-.5);
                telemetry.addData("while", "turningLeft3");
                double turnPower = .7;
                if (buttFirst) {
                    turnPower=-1*turnPower;
                    turnRight(turnPower);
                } else {
                    turnLeft(turnPower);
                }
                telemetry.addData("if",".yaw"+navx_device.getYaw()+"toGo"+degreesToGo);
            }
            telemetry.addData("while", "done");
        }
        else if (navx_device.getYaw()<degreesToGo) {
            telemetry.addData("if","getYaw<degrees");
            while (!(degreesToGo-Keys.TOLERANCE_LEVEL_1<navx_device.getYaw()&&navx_device.getYaw()<degreesToGo+Keys.TOLERANCE_LEVEL_1)) {
                collector.setPower(-.5);
                double turnPower = .8;
                if (buttFirst) {
                    turnPower=-1*turnPower;
                    turnLeft(turnPower);
                }
                else {
                    turnRight(turnPower);
                }
                telemetry.addData("if",".yaw"+navx_device.getYaw()+"toGo"+degreesToGo);
                telemetry.addData("while","turningRight");
            }
            while (!(degreesToGo-Keys.TOLERANCE_LEVEL_2<navx_device.getYaw()&&navx_device.getYaw()<degreesToGo+Keys.TOLERANCE_LEVEL_2)) {
                collector.setPower(-.5);
                double turnPower = .75;
                if (buttFirst) {
                    turnPower=-1*turnPower;
                    turnLeft(turnPower);
                }
                else {
                    turnRight(turnPower);
                }
                telemetry.addData("if", ".yaw" + navx_device.getYaw() + "toGo" + degreesToGo);
                telemetry.addData("while","turningRight");
            }
            while (!(degreesToGo-Keys.TOLERANCE_LEVEL_3<navx_device.getYaw()&&navx_device.getYaw()<degreesToGo+Keys.TOLERANCE_LEVEL_3)) {
                collector.setPower(-.5);
                double turnPower= .7;
                if (buttFirst) {
                    turnPower=-1*turnPower;
                    turnLeft(turnPower);
                } else {
                    turnRight(turnPower);
                }

                telemetry.addData("if",".yaw"+navx_device.getYaw()+"toGo"+degreesToGo);
                telemetry.addData("while","turningRight");
            }

            telemetry.addData("whileD", "done");
        }
        telemetry.addData("ifD","done");
        rest();



    }
}