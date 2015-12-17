package com.qualcomm.ftcrobotcontroller.opmodes;

import android.util.Log;

import com.kauailabs.navx.ftc.AHRS;
import com.kauailabs.navx.ftc.navXPIDController;
import com.qualcomm.ftcrobotcontroller.Keys;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import java.text.DecimalFormat;

/**
 * Created on 11/25/2015.
 */
public class GyroAutonomous extends LinearOpMode {
    DcMotor fr, fl, bl, br;
    double a3,a4,a5;
    private AHRS navx_device;
    private navXPIDController yawPIDController;
    private ElapsedTime runtime = new ElapsedTime();
    @Override
    public void runOpMode() throws InterruptedException {
        fr = hardwareMap.dcMotor.get(Keys.frontRight);
        fl = hardwareMap.dcMotor.get(Keys.frontLeft);
        br = hardwareMap.dcMotor.get(Keys.backRight);
        bl = hardwareMap.dcMotor.get(Keys.backLeft);
        fl.setDirection(DcMotor.Direction.REVERSE);
        bl.setDirection(DcMotor.Direction.REVERSE);
        navx_device = AHRS.getInstance(hardwareMap.deviceInterfaceModule.get(Keys.advancedSensorModule), Keys.NAVX_DIM_I2C_PORT, AHRS.DeviceDataType.kProcessedData, Keys.NAVX_DEVICE_UPDATE_RATE_HZ);
        yawPIDController = new navXPIDController( navx_device, navXPIDController.navXTimestampedDataSource.YAW);
        yawPIDController.setSetpoint(Keys.ORIGINAL_TARGET_ANGLE);
        yawPIDController.setContinuous(true);
        yawPIDController.setOutputRange(Keys.MIN_SPEED_SMOOTH_MOVE * -1, Keys.MAX_SPEED_SMOOTH_MOVE);
        yawPIDController.setTolerance(navXPIDController.ToleranceType.ABSOLUTE, Keys.TOLERANCE_DEGREES);
        yawPIDController.setPID(Keys.YAW_PID_P, Keys.YAW_PID_I, Keys.YAW_PID_D);
        waitForStart();
        gyroTurn(90.0);
        sleep(60000);
        gyroTurn(-90.0);
    }

    public void moveStraight (double dist, boolean backwards) {
        //inches
        //at speed .5, it goes over four inches
        //dist = dist - 4;
        double rotations = dist / (6 * Math.PI);
        double addTheseTicks = rotations * 1120;
        int positionBeforeMovement = fl.getCurrentPosition();
        while (fl.getCurrentPosition() < positionBeforeMovement + addTheseTicks) {
            //telemetry.addData("front left encoder: ", fl.getCurrentPosition());
            telemetry.addData("power",.5);
            //telemetry.addData("ticksFor", addTheseTicks);
            setMotorPowerUniform(.1,backwards);
        }
        rest();
    }
    public void moveSmooth (double dist, boolean backwards) {
        double rotations = dist / (6 * Math.PI);
        double totalTicksNeeded = rotations * 1120;
        telemetry.addData("ticksneed",totalTicksNeeded);
        //based off of totalTicksNeeded, you can find the correct function to minimize jerk
        //v(ticksNeed) = 0
        //v(ticksNeed-1) = low power, at the second to last tick, have it at the lowest power possible
        //v(ticksNeed/2) = max power, at the half way point, you should be at max
        //v(t) = 3a3t^2 - 4a4t^3 + 5a5t^4 --> this function will generate a velocity/function that minimizes jerk
        //based on the top three conditions, solve for a3, a4, and a5 of the function. Use Cramer's rule
        double D =
                3*Math.pow(totalTicksNeeded,2)*-4*Math.pow(totalTicksNeeded-1,3)*5*Math.pow(totalTicksNeeded/2,4)
                        +-4*Math.pow(totalTicksNeeded,3)*5*Math.pow(totalTicksNeeded-1,4)*3*Math.pow(totalTicksNeeded/2,2)
                        +5*Math.pow(totalTicksNeeded,4)*3*Math.pow(totalTicksNeeded-1,2)*-4*Math.pow(totalTicksNeeded/2,3)
                        -(
                        3*Math.pow(totalTicksNeeded/2,2)*-4*Math.pow(totalTicksNeeded-1,3)*5*Math.pow(totalTicksNeeded,4)
                                +-4*Math.pow(totalTicksNeeded/2,3)*5*Math.pow(totalTicksNeeded-1,4)*3*Math.pow(totalTicksNeeded,2)
                                +5*Math.pow(totalTicksNeeded/2,4)*3*Math.pow(totalTicksNeeded-1,2)*-4*Math.pow(totalTicksNeeded,3)
                );


        double Da3 =
                0
                        +-4*Math.pow(totalTicksNeeded,3)*5*Math.pow(totalTicksNeeded-1,4)*Keys.MAX_SPEED_SMOOTH_MOVE
                        +5*Math.pow(totalTicksNeeded,4)*Keys.MIN_SPEED_SMOOTH_MOVE*-4*Math.pow(totalTicksNeeded/2,3)
                        - (
                        Keys.MAX_SPEED_SMOOTH_MOVE*-4*Math.pow(totalTicksNeeded-1,3)*5*Math.pow(totalTicksNeeded,4)
                                +0
                                +5*Math.pow(totalTicksNeeded/2,4)*Keys.MIN_SPEED_SMOOTH_MOVE*-4*Math.pow(totalTicksNeeded,3)
                );
        double Da4 =
                3*Math.pow(totalTicksNeeded,2)*Keys.MIN_SPEED_SMOOTH_MOVE*5*Math.pow(totalTicksNeeded/2,4)
                        +0
                        +5*Math.pow(totalTicksNeeded,4)*3*Math.pow(totalTicksNeeded-1,2)*Keys.MAX_SPEED_SMOOTH_MOVE
                        - (
                        3*Math.pow(totalTicksNeeded/2,2)*Keys.MIN_SPEED_SMOOTH_MOVE*5*Math.pow(totalTicksNeeded,4)
                                +Keys.MAX_SPEED_SMOOTH_MOVE*5*Math.pow(totalTicksNeeded-1,4)*3*Math.pow(totalTicksNeeded,2)
                                +0

                );
        double Da5 =
                3*Math.pow(totalTicksNeeded,2)*-4*Math.pow(totalTicksNeeded-1,3)*Keys.MAX_SPEED_SMOOTH_MOVE
                        +-4*Math.pow(totalTicksNeeded,3)*Keys.MIN_SPEED_SMOOTH_MOVE*3*Math.pow(totalTicksNeeded/2,2)
                        +0
                        - (
                        0
                                +-4*Math.pow(totalTicksNeeded/2,3)*Keys.MIN_SPEED_SMOOTH_MOVE*3*Math.pow(totalTicksNeeded,2)
                                +Keys.MAX_SPEED_SMOOTH_MOVE*3*Math.pow(totalTicksNeeded-1,2)*-4*Math.pow(totalTicksNeeded,3)

                );

        a3 = Da3/D;
        a4 = Da4/D;
        a5 = Da5/D;
        telemetry.addData("Math","D: "+D+", Da3: "+Da3+", Da4: "+Da4+", Da5: "+Da5+", a3: "+a3+", a4: "+a4+", a5: "+a5);

        //ok so now you know the coefficients of the v(t), formualted so that encoder is time, and eevrything is scaled in terms of motor power
        //because when tick is 0, power will be set to 0, and then tick can't change, we need to manually change it
        boolean tick0Done = false;
        int positionBeforeMovement = fl.getCurrentPosition();
        while (fl.getCurrentPosition() < positionBeforeMovement + totalTicksNeeded) {
            int currentTick = fl.getCurrentPosition()-positionBeforeMovement;
            telemetry.addData("power",functionThisAndReturnPowerBasedOnEncodedTime(currentTick));
            telemetry.addData("time/ticks", currentTick);
            setMotorPowerUniform(functionThisAndReturnPowerBasedOnEncodedTime(currentTick), backwards);
            if (tick0Done==false) {
                tick0Done=true;
                //manually increase tick and do the next tick manually
                currentTick++;
                telemetry.addData("power","manual: "+functionThisAndReturnPowerBasedOnEncodedTime(currentTick));
                telemetry.addData("time/ticks",currentTick);
                setMotorPowerUniform(functionThisAndReturnPowerBasedOnEncodedTime(currentTick),backwards);
                //when this ends, it will have moved one power and one tick only
                //now when it goes back into the loop, fl.getCurrentPos will be changed, but it should resemble the correct change and now be at tick 2. everything else is same and currentTick will equal two
            }
            //implied else, it was already true so you don't need to do anything
        }
        //rest at end
        rest();
    }

    private double functionThisAndReturnPowerBasedOnEncodedTime(int currentTick) {
        //v(t) = 3a3t^2 - 4a4t^3 + 5a5t^4
        return 3*a3*Math.pow(currentTick,2)-4*a4*Math.pow(currentTick,3)+5*a5*Math.pow(currentTick,4);
    }

    public void moveAlteredSin (double dist, boolean backwards) {
        //inches

        double rotations = dist / (6 * Math.PI);
        double totalTicks = rotations * 1120;
        int positionBeforeMovement = fl.getCurrentPosition();
        while (fl.getCurrentPosition() < positionBeforeMovement + totalTicks) {
            telemetry.addData("front left encoder: ", "sin"+fl.getCurrentPosition());
            telemetry.addData("ticksFor", totalTicks);
            //convert to radians
            int currentTick = fl.getCurrentPosition()-positionBeforeMovement;
            //accelerate 15% of time
            //coast 25% of time
            //decelerate 60% of time
            int firstSectionTime = (int)Math.round(.1*totalTicks);
            int secondSectionTime = (int)(Math.round( (.1+.25)*totalTicks));
            //rest will just be 100%
            double power;
            if (currentTick<firstSectionTime) {

                power = .3*Math.cos((currentTick)*Math.PI/totalTicks+Math.PI)+.4;

                power +=.1;
                //first quarter (period = 2pi) of sin function is only reaching altitude

            }
            else if (currentTick<secondSectionTime) {
                power = .8;

            }
            else {
                // between [40%,100%]
                //decrease time
                int ticksLeft=(int) Math.round(currentTick-(totalTicks*.35));
                //with these ticks left, set a range within cosine to decrease
                power = .4*Math.cos((ticksLeft)*Math.PI/totalTicks)+.4;
            }

            telemetry.addData("power",power);
            setMotorPowerUniform(power, backwards);
        }
        rest();
    }

    public void setMotorPowerUniform(double power, boolean backwards) {
        int direction = 1;
        if (backwards) {
            direction =-1;
        }
        fr.setPower(power);
        fl.setPower(power);
        bl.setPower(power);
        br.setPower(power);
        //collector.setPower(-.5);

    }

    public void turn(double power, boolean right) {
        double rightPower;
        double leftPower;

        if(right) {
            rightPower = power * -1;
            leftPower = power;
        }
        else {
            rightPower = power;
            leftPower = power * -1;
        }
        fr.setPower(rightPower);
        br.setPower(rightPower);
        fl.setPower(leftPower);
        bl.setPower(leftPower);
    }

    public void gyroTurn(double degreesOfTurn) {
        yawPIDController.setSetpoint(degreesOfTurn);
        try {
            yawPIDController.enable(true);

        /* Wait for new Yaw PID output values, then update the motors
           with the new PID value with each new output value.
         */

            final double TOTAL_RUN_TIME_SECONDS = 30.0;
            int DEVICE_TIMEOUT_MS = 500;
            navXPIDController.PIDResult yawPIDResult = new navXPIDController.PIDResult();
            boolean moveOn = false;
            DecimalFormat df = new DecimalFormat("#.##");

            while ( (runtime.time() < TOTAL_RUN_TIME_SECONDS) || !Thread.currentThread().isInterrupted() || !moveOn) {
                if (yawPIDController.waitForNewUpdate(yawPIDResult, DEVICE_TIMEOUT_MS)) {
                    if (yawPIDResult.isOnTarget()) {
                        rest();
                        moveOn = true;
                        telemetry.addData("PIDOutput", df.format(0.00));
                    }
                    else if (!(yawPIDResult.output/-yawPIDResult.output == -1) && yawPIDResult.getOutput() > degreesOfTurn) {
                        double output = yawPIDResult.getOutput();
                        turn(output, true);
                        telemetry.addData("PIDOutput", df.format(output) + ", " + df.format(-output));
                    }
                    else if ((yawPIDResult.output/-yawPIDResult.output == 1) && yawPIDResult.getOutput() < degreesOfTurn) {
                        double output = yawPIDResult.getOutput();
                        turn(output, false);
                        telemetry.addData("PIDOutput", df.format(-output) + ", " + df.format(output));
                    }
                } else {
			    /* A timeout occurred */
                    Log.w("navXRotateOp", "Yaw PID waitForNewUpdate() TIMEOUT.");
                }
                telemetry.addData("Yaw", df.format(navx_device.getYaw()));
            }
        }
        catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        finally {
            navx_device.close();
        }
    }


    public void rest() {
        fr.setPower(0);
        fl.setPower(0);
        bl.setPower(0);
        br.setPower(0);

    }
}
