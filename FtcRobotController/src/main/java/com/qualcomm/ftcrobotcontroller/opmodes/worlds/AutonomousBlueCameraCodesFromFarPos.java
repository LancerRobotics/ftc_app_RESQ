package com.qualcomm.ftcrobotcontroller.opmodes.worlds;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.util.Log;

import com.kauailabs.navx.ftc.AHRS;
import com.kauailabs.navx.ftc.navXPIDController;
import com.qualcomm.ftcrobotcontroller.Beacon;
import com.qualcomm.ftcrobotcontroller.CameraPreview;
import com.qualcomm.ftcrobotcontroller.FtcRobotControllerActivity;
import com.qualcomm.ftcrobotcontroller.Keys;
import com.qualcomm.ftcrobotcontroller.Vision;
import com.qualcomm.ftcrobotcontroller.VisionProcess;
import com.qualcomm.ftcrobotcontroller.XYCoor;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by mattquan on 2/18/2016.
 */
public class AutonomousBlueCameraCodesFromFarPos extends LinearOpMode {
    private Camera mCamera;
    public CameraPreview preview;
    public Bitmap image;


    DcMotor fr, fl, bl, br, collector;
    Servo swivel, dump, climber, hang, clampRight, clampLeft, guardLeft, guardRight;
    AnalogInput sonarAbovePhone, sonarFoot;
    boolean calibration_complete = false, pressed = false, a = false, b = false;
    //double a3,a4,a5;
    private AHRS navx_device;
    private navXPIDController yawPIDController;

    @Override
    public void runOpMode() throws InterruptedException {
        climber = hardwareMap.servo.get(Keys.climber);
        swivel = hardwareMap.servo.get(Keys.swivel);
        hang = hardwareMap.servo.get(Keys.hang);
        guardLeft = hardwareMap.servo.get(Keys.guardLeft);
        guardRight = hardwareMap.servo.get(Keys.guardRight);
        clampLeft = hardwareMap.servo.get(Keys.clampLeft);
        clampRight = hardwareMap.servo.get(Keys.clampRight);
        dump = hardwareMap.servo.get(Keys.dump);
        fr = hardwareMap.dcMotor.get(Keys.frontRight);
        fl = hardwareMap.dcMotor.get(Keys.frontLeft);
        bl = hardwareMap.dcMotor.get(Keys.backLeft);
        br = hardwareMap.dcMotor.get(Keys.backRight);
        collector = hardwareMap.dcMotor.get(Keys.collector);
        fl.setDirection(DcMotor.Direction.REVERSE);
        bl.setDirection(DcMotor.Direction.REVERSE);
        dump.setPosition(Keys.DUMP_INIT);
        swivel.setPosition(Keys.SWIVEL_CENTER);
        hang.setPosition(Keys.HANG_INIT);
        clampLeft.setPosition(Keys.CLAMP_LEFT_INIT);
        clampRight.setPosition(Keys.CLAMP_RIGHT_INIT);
        climber.setPosition(Keys.CLIMBER_INITIAL_STATE);
        guardLeft.setPosition(Keys.LEFT_GUARD_DOWN);
        guardRight.setPosition(Keys.RIGHT_GUARD_DOWN);
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
        telemetry.addData("Select the a button to not move out of the way of the incoming robot and the b button to move out of the way of the incoming robot", "");
        while (!pressed) {
            if (gamepad1.a) {
                a = true;
                pressed = true;
            } else if (gamepad1.b) {
                b = true;
                pressed = true;
            }
        }
        telemetry.addData("Start Autonomous?", "Yes");
        waitForStart();
        gyroTurn(47, false);
        moveAlteredSin(69 + 8 * Math.sqrt(2), false);
        gyroTurn(45, false);
        adjustToThisDistance(12, sonarFoot);
        telemetry.addData("sonar", readSonar(sonarFoot));
        rest();
        //telemetry.addData("sonar",readSonar(sonarAbovePhone));
        mCamera = ((FtcRobotControllerActivity) hardwareMap.appContext).mCamera;
//i need to init the camera and also get the instance of the camera        //on pic take protocol
        telemetry.addData("camera","initingcameraPreview");
        ((FtcRobotControllerActivity) hardwareMap.appContext).initCameraPreview(mCamera, this);

        //wait, because I have handler wait three seconds b4 it'll take a picture, in initCamera
        sleep(Vision.RETRIEVE_FILE_TIME);
        //now we are going to retreive the image and convert it to bitmap
        SharedPreferences prefs = hardwareMap.appContext.getApplicationContext().getSharedPreferences(
                "com.quan.companion", Context.MODE_PRIVATE);
        String path = prefs.getString(Keys.pictureImagePathSharedPrefsKeys, "No path found");
        Log.e("path",path);
        telemetry.addData("image",path);
        //debug stuff - telemetry.addData("camera", "path: " + path);
        File imgFile = new File(path);
        image = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        Log.e("image",image.toString());
        dumpClimbers();
        rest();
        sleep(500);
        //cool now u have the image file u just took the picture of
        VisionProcess mVP = new VisionProcess(image);
        returnToOrigPosAfterDumpOfClimbers();
        rest();
        Log.e("starting output","start");
        telemetry.addData("starting output","doing smart computer stuff now");
        Beacon beacon = mVP.output(hardwareMap.appContext);
        Log.e("beacon",beacon.toString());
        telemetry.addData("beacon",beacon);
        if (!beacon.error()) {
            if (beacon.oneSideUnknown()) {
                //assume this is the right side, assume left side got chopped off
                if (beacon.getRight()== Beacon.COLOR_BLUE) {
                    telemetry.addData("beacon", 1);
                    //this is what i want, since im on red team. hit right side
                    pushRightButton();
                }
                else {
                    //the other side must be red
                    //drop servo arm, then move forward
                    telemetry.addData("beacon", 2);
                    adjustAndPressLeft();
                    //park
                    //parkFromLeftSide();
                }
            }
            else {
                if (beacon.whereIsBlue().equals( Beacon.RIGHT)) {
                    pushRightButton();
                } else if (beacon.whereIsBlue().equals( Beacon.LEFT)) {
                    telemetry.addData("beacon", 4);
                    adjustAndPressLeft();
                }
            }
        }
        if(b) {
            moveStraight(24, true, .5);
            gyroTurn(30, false);
            moveStraight(29, false, .5);
        }
        else if(a) {
            rest();
        }
    }

    public void smoothMoveVol2 (double inches, boolean backwards) {
        //works best for at least 1000 ticks = 11.2 inches approx
        double rotations = inches / (Keys.WHEEL_DIAMETER * Math.PI);
        double totalTicks = rotations * 1120 * 3 / 2;
        int positionBeforeMovement = fl.getCurrentPosition();
        double ticksToGo = positionBeforeMovement+totalTicks;
        //p;us one because make the first tick 1, not 0, so fxn will never be 0
        double savedPower=0;
        double savedTick=0;
        while (fl.getCurrentPosition() < ticksToGo+1) {
            telemetry.addData("front left encoder: ", fl.getCurrentPosition());
            telemetry.addData("ticksFor", totalTicks);
            collector.setPower(-1*Keys.COLLECTOR_POWER);
            //convert to radians
            int currentTick = fl.getCurrentPosition() - positionBeforeMovement +1 ;
            if (currentTick<ticksToGo/2) {
                //use an inv tan function as acceleration
                //power = ((2/pi)*.86) arctan (x/totalticks*.1)
                double power = ((2/Math.PI)*Keys.MAX_SPEED) * Math.atan(currentTick/totalTicks/2*10);
                telemetry.addData("power","accel"+power);
                if (power<Keys.MIN_SPEED_SMOOTH_MOVE) {
                    telemetry.addData("bool",power<Keys.MIN_SPEED_SMOOTH_MOVE);
                    power = Keys.MIN_SPEED_SMOOTH_MOVE;
                    telemetry.addData("power","adjusted"+power);
                }
                telemetry.addData("power",power);
                setMotorPowerUniform(power, backwards);
                savedPower=power;
                savedTick=currentTick;
            }
            else {
                //decelerate using
                double newCurrentCount = currentTick+1-savedTick;
                //current tick changes, savedTick is constant
                double horizontalStretch = totalTicks/2*.2;
                if (newCurrentCount<horizontalStretch) {
                    //becuase of domain restrictions
                    setMotorPowerUniform(savedPower,backwards);
                }
                else {
                    //in the domain

                    double power = (2/Math.PI)*savedPower*Math.asin(horizontalStretch/newCurrentCount);
                    telemetry.addData("power","decel"+power);
                    if (power<Keys.MIN_SPEED_SMOOTH_MOVE) {
                        power = Keys.MIN_SPEED_SMOOTH_MOVE;
                        telemetry.addData("power","adjusted"+power);
                    }
                    setMotorPowerUniform(power,backwards);
                }

            }

        }
        rest();
    }

    private void adjustAndPressLeft() {
        moveStraight(11, false, .6);
    }

    private void pushRightButton() {
        adjustToThisDistance(24, sonarFoot);
        gyroTurn(-10, false);
        moveStraight(30, false, .6);
    }

    public void adjustToThisDistance(double distance, AnalogInput sonar) {
        double myPosition = readSonar(sonar);
        telemetry.addData("myPos", myPosition);
        if (readSonar(sonar) < distance - Keys.SONAR_TOLERANCE) {
            telemetry.addData("if", "readSonar<distance");
            while (readSonar(sonar) < distance - Keys.SONAR_TOLERANCE) {
                telemetry.addData("while", "looping3");
                telemetry.addData("mySonar", readSonar(sonar));
                telemetry.addData("dist", distance);
                setMotorPowerUniform(.25, true);
                telemetry.addData("bool read<dist+tol", readSonar(sonar) < distance - Keys.SONAR_TOLERANCE);
            }
        } else if (myPosition > distance + Keys.SONAR_TOLERANCE) {
            telemetry.addData("if", "readSonar<distance");
            while (readSonar(sonar) > distance + Keys.SONAR_TOLERANCE) {
                telemetry.addData("while", "looping");
                telemetry.addData("mySonar", readSonar(sonar));
                telemetry.addData("dist", distance);
                setMotorPowerUniform(.25, false);
                telemetry.addData("bool read>dist+tol", readSonar(sonar) > distance + Keys.SONAR_TOLERANCE);
            }
        }
        rest();
        telemetry.addData("sonar", "done");
        rest();
    }

    public void dumpClimbers() {
        moveStraight(8.5, false, .3);
        climber.setPosition(Keys.CLIMBER_DUMP);
    }

    public void returnToOrigPosAfterDumpOfClimbers() {
        climber.setPosition(Keys.CLIMBER_INITIAL_STATE);
        moveStraight(8.5, true, .3);
    }

    //returns sonar values in inches!!!
    public double readSonar(AnalogInput sonar) {
        double sValue = sonar.getValue();
        sValue = sValue / 2;
        return sValue;
    }

    public void moveStraight(double dist, boolean backwards, double power) {

        double rotations = dist / (Keys.WHEEL_DIAMETER * Math.PI);
        double totalTicks = rotations * 1120 * 3 / 2;
        int positionBeforeMovement = fl.getCurrentPosition();
        if (backwards) {
            while (fl.getCurrentPosition() > positionBeforeMovement - totalTicks) {
                setMotorPowerUniform(power, backwards);
            }
        } else {
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
            collector.setPower(-1*Keys.COLLECTOR_POWER);
            //convert to radians
            int currentTick = fl.getCurrentPosition() - positionBeforeMovement;
            //accelerate 15% of time
            //coast 25% of time
            //decelerate 60% of time
            int firstSectionTime = (int) Math.round(.05 * totalTicks);
            int secondSectionTime = (int) (Math.round((.05 + .05) * totalTicks)); //35
            int thirdSectionTime = (int) (Math.round((.5) * totalTicks)); //35
            //rest will just be 100%
            double power;
            if (currentTick < firstSectionTime) {

                power = .33;
                //first quarter (period = 2pi) of sin function is only reaching altitude

            } else if (currentTick < secondSectionTime) {
                power = .66;

            } else if (currentTick < thirdSectionTime) {
                power = .86;

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
        fr.setPower(direction * power);
        fl.setPower(direction * power);
        bl.setPower(direction * power);
        br.setPower(direction * power);
        collector.setPower(-1*Keys.COLLECTOR_POWER);

    }

    public void rest() {
        fr.setPower(0);
        fl.setPower(0);
        bl.setPower(0);
        br.setPower(0);
        collector.setPower(0);
    }

    public void turnLeft(double power) {
        fr.setPower(power);
        br.setPower(power);
    }

    public void turnRight(double power) {
        fl.setPower(power);
        bl.setPower(power);
    }

    public void gyroTurn(double degrees, boolean buttFirst) {
        //degrees=degrees*-1;

        yawPIDController = new navXPIDController(navx_device, navXPIDController.navXTimestampedDataSource.YAW);
        yawPIDController.setSetpoint(degrees);
        yawPIDController.setContinuous(true);
        yawPIDController.setOutputRange(Keys.MAX_SPEED * -1, Keys.MAX_SPEED);
        yawPIDController.setTolerance(navXPIDController.ToleranceType.ABSOLUTE, Keys.TOLERANCE_LEVEL_2);

        yawPIDController.enable(true);
        int DEVICE_TIMEOUT_MS = 500;
        navXPIDController.PIDResult yawPIDResult = new navXPIDController.PIDResult();

        telemetry.addData("Yaw", navx_device.getYaw());
        double degreesNow = navx_device.getYaw();
        double degreesToGo = degreesNow + degrees;
        //telemetry.addData("if state",navx_device.getYaw());
        //telemetry.addData("other if",degreesToGo);
        telemetry.addData("boolean", navx_device.getYaw() > degreesToGo);
        //telemetry.addData("boolean",navx_device.getYaw()<degreesToGo);
        if (navx_device.getYaw() > degreesToGo) {
            telemetry.addData("if", "getYaw>degrees");
            telemetry.addData("more boolean", !(degreesToGo - Keys.TOLERANCE_LEVEL_1 < navx_device.getYaw() && navx_device.getYaw() < degreesToGo + Keys.TOLERANCE_LEVEL_1));
            while (!(degreesToGo - Keys.TOLERANCE_LEVEL_1 < navx_device.getYaw() && navx_device.getYaw() < degreesToGo + Keys.TOLERANCE_LEVEL_1)) {
                collector.setPower(Keys.COLLECTOR_POWER*-1);
                telemetry.addData("while", "turningLeft1");
                double turnPower = .8;
                if (buttFirst) {
                    turnPower = -.8;
                    turnRight(turnPower);
                } else {
                    turnLeft(turnPower);
                }
                telemetry.addData("if", ".yaw" + navx_device.getYaw() + "toGo" + degreesToGo);
            }
            telemetry.addData("more boolean2", navx_device.getYaw() > degreesToGo + Keys.TOLERANCE_LEVEL_2);
            while (navx_device.getYaw() > degreesToGo + Keys.TOLERANCE_LEVEL_2) {
                collector.setPower(Keys.COLLECTOR_POWER*-1);
                telemetry.addData("while", "turningLeft2");
                double turnPower = .75;
                if (buttFirst) {
                    turnPower = -1 * turnPower;
                    turnRight(turnPower);
                } else {
                    turnLeft(turnPower);
                }
                telemetry.addData("if", ".yaw" + navx_device.getYaw() + "toGo" + degreesToGo);
            }
            while (!(degreesToGo - Keys.TOLERANCE_LEVEL_3 < navx_device.getYaw() && navx_device.getYaw() < degreesToGo + Keys.TOLERANCE_LEVEL_3)) {
                collector.setPower(Keys.COLLECTOR_POWER*-1);
                telemetry.addData("while", "turningLeft3");
                double turnPower = .7;
                if (buttFirst) {
                    turnPower = -1 * turnPower;
                    turnRight(turnPower);
                } else {
                    turnLeft(turnPower);
                }
                telemetry.addData("if", ".yaw" + navx_device.getYaw() + "toGo" + degreesToGo);
            }
            telemetry.addData("while", "done");
        } else if (navx_device.getYaw() < degreesToGo) {
            telemetry.addData("if", "getYaw<degrees");
            while (!(degreesToGo - Keys.TOLERANCE_LEVEL_1 < navx_device.getYaw() && navx_device.getYaw() < degreesToGo + Keys.TOLERANCE_LEVEL_1)) {
                collector.setPower(Keys.COLLECTOR_POWER*-1);
                double turnPower = .8;
                if (buttFirst) {
                    turnPower = -1 * turnPower;
                    turnLeft(turnPower);
                } else {
                    turnRight(turnPower);
                }
                telemetry.addData("if", ".yaw" + navx_device.getYaw() + "toGo" + degreesToGo);
                telemetry.addData("while", "turningRight");
            }
            while (!(degreesToGo - Keys.TOLERANCE_LEVEL_2 < navx_device.getYaw() && navx_device.getYaw() < degreesToGo + Keys.TOLERANCE_LEVEL_2)) {
                collector.setPower(Keys.COLLECTOR_POWER*-1);
                double turnPower = .75;
                if (buttFirst) {
                    turnPower = -1 * turnPower;
                    turnLeft(turnPower);
                } else {
                    turnRight(turnPower);
                }
                telemetry.addData("if", ".yaw" + navx_device.getYaw() + "toGo" + degreesToGo);
                telemetry.addData("while", "turningRight");
            }
            while (!(degreesToGo - Keys.TOLERANCE_LEVEL_3 < navx_device.getYaw() && navx_device.getYaw() < degreesToGo + Keys.TOLERANCE_LEVEL_3)) {
                collector.setPower(Keys.COLLECTOR_POWER*-1);
                double turnPower = .7;
                if (buttFirst) {
                    turnPower = -1 * turnPower;
                    turnLeft(turnPower);
                } else {
                    turnRight(turnPower);
                }

                telemetry.addData("if", ".yaw" + navx_device.getYaw() + "toGo" + degreesToGo);
                telemetry.addData("while", "turningRight");
            }
            telemetry.addData("whileD", "done");
        }
        telemetry.addData("ifD", "done");
        rest();
    }

}
