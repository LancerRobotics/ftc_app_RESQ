package com.qualcomm.ftcrobotcontroller.opmodes.ftclancers;

import android.graphics.Color;
import android.util.Log;

import com.kauailabs.navx.ftc.AHRS;
import com.kauailabs.navx.ftc.navXPIDController;
import com.qualcomm.ftcrobotcontroller.Keys;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DeviceInterfaceModule;
import com.qualcomm.robotcore.hardware.I2cDevice;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * Created on 11/25/2015.
 */
public class Gyro extends LinearOpMode {
    DcMotor fr, fl, bl, br, collector;
    //AnalogInput sonarFrontLeft, sonarFrontRight, sonarBackRight, sonarBackLeft;
    Servo climber, clampRight, clampLeft;
    double a3, a4, a5, distanceToCheck;
    float hsvValues[] = {0F,0F,0F};
    private AHRS navx_device;
    private navXPIDController leftYawPIDController;
    private navXPIDController rightYawPIDController;
    public static final double YAW_PID_P = 0.005;
    public static final double YAW_PID_I = 0.0;
    public static final double YAW_PID_D = 0.0;
    boolean calibration_complete = false;
    //ColorSensor colorFR, colorFL;
    DeviceInterfaceModule cdim;
    int movementCounter = 0;

    @Override
    public void runOpMode() throws InterruptedException {
        fr = hardwareMap.dcMotor.get(Keys.frontRight);
        fl = hardwareMap.dcMotor.get(Keys.frontLeft);
        br = hardwareMap.dcMotor.get(Keys.backRight);
        bl = hardwareMap.dcMotor.get(Keys.backLeft);
        collector = hardwareMap.dcMotor.get(Keys.collector);
        climber = hardwareMap.servo.get(Keys.climber);
        clampLeft = hardwareMap.servo.get(Keys.clampLeft);
        clampRight = hardwareMap.servo.get(Keys.clampRight);
        fl.setDirection(DcMotor.Direction.REVERSE);
        bl.setDirection(DcMotor.Direction.REVERSE);
        //colorFR = hardwareMap.colorSensor.get(Keys.COLOR_FRONT_RIGHT);
        //colorFL = hardwareMap.colorSensor.get(Keys.COLOR_FRONT_LEFT);
        //sonarFrontLeft = hardwareMap.analogInput.get(Keys.SONAR_FRONT_LEFT);
        //sonarBackLeft = hardwareMap.analogInput.get(Keys.SONAR_BACK_LEFT);
        //sonarBackRight = hardwareMap.analogInput.get(Keys.SONAR_BACK_RIGHT);
        //sonarFrontRight = hardwareMap.analogInput.get(Keys.SONAR_FRONT_RIGHT);
        cdim = hardwareMap.deviceInterfaceModule.get(Keys.advancedSensorModule);
        navx_device = AHRS.getInstance(hardwareMap.deviceInterfaceModule.get(Keys.advancedSensorModule), Keys.NAVX_DIM_I2C_PORT, AHRS.DeviceDataType.kProcessedData, Keys.NAVX_DEVICE_UPDATE_RATE_HZ);
        while ( !calibration_complete ) {
            calibration_complete = !navx_device.isCalibrating();
            if (!calibration_complete) {
                telemetry.addData("Calibration done?", "No");
            }
        }
        telemetry.addData("Calibration done?", "Yes");
        waitForStart();
        gyroTurn (90, true, true);
        telemetry.addData("Done", "HALFWAY");
        sleep(30000);
        gyroTurn(90, false, true);
        telemetry.addData("Done", "DONE");
    }

    public void moveStraight(double dist, boolean backwards) {
        //inches
        //at speed .5, it goes over four inches
        //dist = dist - 4;
        double rotations = dist / (6 * Math.PI);
        double addTheseTicks = rotations * 1120;
        int positionBeforeMovement = fl.getCurrentPosition();
        while (fl.getCurrentPosition() < positionBeforeMovement + addTheseTicks) {
            //telemetry.addData("front left encoder: ", fl.getCurrentPosition());
            telemetry.addData("power", .5);
            //telemetry.addData("ticksFor", addTheseTicks);
            setMotorPowerUniform(.1, backwards);
        }
        rest();
    }

    public void moveAlteredSin(double dist, boolean backwards) {
        //inches

        double rotations = dist / (6 * Math.PI);
        double totalTicks = rotations * 1120;
        int positionBeforeMovement = fl.getCurrentPosition();
        while (fl.getCurrentPosition() < positionBeforeMovement + totalTicks) {
            telemetry.addData("front left encoder: ", "sin" + fl.getCurrentPosition());
            telemetry.addData("ticksFor", totalTicks);
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
            /*if((objectInFront() && !backwards) || (objectBehind() && backwards)) {
                rest();
            }
            else {
                setMotorPowerUniform(power, backwards);
            }
            */
        }
        rest();
    }

    public void setMotorPowerUniform(double power, boolean backwards) {
        int direction = 1;
        if (backwards) {
            direction = -1;
        }
        power = direction * -1;
        fr.setPower(power);
        fl.setPower(power);
        bl.setPower(power);
        br.setPower(power);
    }

    public void rest() {
        fr.setPower(0);
        fl.setPower(0);
        bl.setPower(0);
        br.setPower(0);
    }
    public void turn (double power, boolean right, boolean motorPowerForwards) {
        if(motorPowerForwards) {
            power = power * -1;
        }
        if(right) {
            fl.setPower(power);
            bl.setPower(power);
        }
        else {
            fr.setPower(power);
            br.setPower(power);
        }
    }
    public void gyroTurn(double degrees, boolean right, boolean motorPowerForwards) {
        rest();
        navx_device.zeroYaw();
        boolean onTarget = false;
        if (right) {
            rightYawPIDController = new navXPIDController(navx_device, navXPIDController.navXTimestampedDataSource.YAW);
            rightYawPIDController.setContinuous(true);
            rightYawPIDController.setOutputRange(Keys.MAX_SPEED * -1, Keys.MAX_SPEED);
            rightYawPIDController.setTolerance(navXPIDController.ToleranceType.ABSOLUTE, Keys.TOLERANCE_DEGREES);
            rightYawPIDController.setPID(YAW_PID_P, YAW_PID_I, YAW_PID_D);
            rightYawPIDController.enable(true);
            rightYawPIDController.setSetpoint(-1 * degrees);
        }
        else {
            leftYawPIDController = new navXPIDController(navx_device, navXPIDController.navXTimestampedDataSource.YAW);
            leftYawPIDController.setContinuous(true);
            leftYawPIDController.setOutputRange(Keys.MAX_SPEED * -1, Keys.MAX_SPEED);
            leftYawPIDController.setTolerance(navXPIDController.ToleranceType.ABSOLUTE, Keys.TOLERANCE_DEGREES);
            leftYawPIDController.setPID(YAW_PID_P, YAW_PID_I, YAW_PID_D);
            leftYawPIDController.enable(true);
            leftYawPIDController.setSetpoint(degrees);
        }
        try {
            int DEVICE_TIMEOUT_MS = 500;
            navXPIDController.PIDResult yawPIDResult = new navXPIDController.PIDResult();
            while (!onTarget) {
                if(right) {
                    if (rightYawPIDController.waitForNewUpdate(yawPIDResult, DEVICE_TIMEOUT_MS)) {
                        if (rightYawPIDController.isOnTarget()) {
                            onTarget = true;
                        } else {
                            double motorPower = yawPIDResult.getOutput();
                            if(yawPIDResult.getOutput() > .7 && rightYawPIDController.getSetpoint() - navx_device.getYaw() < -10) {
                                motorPower = .7;
                            }
                            else if (yawPIDResult .getOutput() < -.7 && rightYawPIDController.getSetpoint() - navx_device.getYaw() < -10) {
                                motorPower = -.7;
                            }
                            turn(motorPower, right, motorPowerForwards);
                        }
                    }
                }
                else if (!right) {
                    if (leftYawPIDController.waitForNewUpdate(yawPIDResult, DEVICE_TIMEOUT_MS)) {
                        if (leftYawPIDController.isOnTarget()) {
                            onTarget = true;
                        } else {
                            double motorPower = yawPIDResult.getOutput();
                            if(yawPIDResult.getOutput() > .7 && leftYawPIDController.getSetpoint() - navx_device.getYaw() < 10) {
                                motorPower = .7;
                            }
                            else if (yawPIDResult .getOutput() < -.7 && leftYawPIDController.getSetpoint() - navx_device.getYaw() < 10) {
                                motorPower = -.7;
                            }
                            turn(motorPower, right, motorPowerForwards);
                        }
                    }
                } else {
			    /* A timeout occurred */
                    Log.w("navXRotateOp", "Yaw PID waitForNewUpdate() TIMEOUT.");
                    telemetry.addData("TIMEOUT OCCUR", "YES");
                }
                telemetry.addData("Yaw", navx_device.getYaw());
                if(!right)
                    telemetry.addData("Setpoint", leftYawPIDController.getSetpoint());
                else if(right)
                    telemetry.addData("Setpoint", rightYawPIDController.getSetpoint());
                telemetry.addData("Motor Power", yawPIDResult.getOutput());
                telemetry.addData("Finished Turn?", onTarget);
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            if(right) {
                double startPos = fl.getCurrentPosition();
                double ticksNeeded = startPos + ((1680/90) * degrees);
                while(fl.getCurrentPosition() < ticksNeeded) {
                    while (ticksNeeded - fl.getCurrentPosition() > 1120) {
                        turn(.86, right, motorPowerForwards);
                    }
                    while(ticksNeeded - fl.getCurrentPosition() > 500) {
                        turn(.75, right, motorPowerForwards);
                    }
                    while(ticksNeeded - fl.getCurrentPosition() > 200) {
                        turn(.7, right, motorPowerForwards);
                    }
                }
            }
            else {
                double startPos = br.getCurrentPosition();
                double ticksNeeded = startPos + ((1400/90) * degrees);
                while(br.getCurrentPosition() < ticksNeeded) {
                    while (ticksNeeded - br.getCurrentPosition() > 1120) {
                        turn(.86, right, motorPowerForwards);
                    }
                    while(ticksNeeded - br.getCurrentPosition() > 500) {
                        turn(.75, right, motorPowerForwards);
                    }
                    while(ticksNeeded - br.getCurrentPosition() > 200) {
                        turn(.7, right, motorPowerForwards);
                    }
                }
            }
            rest();
        } finally {
            rest();
            if (right) {
                rightYawPIDController.enable(false);
            }
            else {
                leftYawPIDController.enable(false);
            }
        }
    }


    //return a float the just contains the *hue* which is the color value we want
    /*public float colorSensorValue(float[] values) {
        Color.RGBToHSV((colorFR.red() * 255) / 800, (colorFR.green() * 255) / 800, (colorFR.blue() * 255) / 800, values);
        return values[0];
    }
    */
    //alternative color sensor method to try as well


    public double readSonar(AnalogInput sonar) {
        double sValue = sonar.getValue();
        sValue = sValue/2;
        return sValue;
    }
/*
    public boolean objectBehind () {
        double sonarBL = readSonar(sonarBackLeft);
        double sonarBR = readSonar(sonarBackRight);
        if (sonarBL < 10 || sonarBR < 10) {
            return true;
        }
        else {
            return false;
        }
    }

    public boolean objectInFront () {
        double sonarFL = readSonar(sonarFrontLeft);
        double sonarFR = readSonar(sonarFrontRight);
        if (sonarFL < 10 || sonarFR < 10) {
            return true;
        }
        else {
            return false;
        }
    }

    public boolean checkSonarPosition(double distance) {
        boolean allGood;
        if(distance-4 <= readSonar(sonarFrontLeft) && readSonar(sonarFrontLeft)<= distance+4 && distance-4 <= readSonar(sonarFrontRight) && readSonar(sonarFrontRight)<= distance+4) {
            allGood = true;
        }
        else {
            allGood = false;
        }
        return allGood;
    }
    public void correctMovement(double distance) {
        while (!checkSonarPosition(distance)) {
            if (readSonar(sonarFrontRight) < distance-4 || readSonar(sonarFrontLeft) < distance-4) {
                setMotorPowerUniform(.1, false);
            } else if (readSonar(sonarFrontLeft) > distance+4 || readSonar(sonarFrontRight) > distance+4) {
                setMotorPowerUniform(.1, true);
            }
        }
        rest();
    }
    */
}