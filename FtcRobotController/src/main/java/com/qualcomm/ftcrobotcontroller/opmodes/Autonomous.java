package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftcrobotcontroller.Keys;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * Created on 11/25/2015.
 */
public class Autonomous extends LinearOpMode {
    DcMotor fr, fl, bl, br;
    @Override
    public void runOpMode() throws InterruptedException {
        fr = hardwareMap.dcMotor.get(Keys.frontRight);
        fl = hardwareMap.dcMotor.get(Keys.frontLeft);
        br = hardwareMap.dcMotor.get(Keys.backRight);
        bl = hardwareMap.dcMotor.get(Keys.backLeft);
        fl.setDirection(DcMotor.Direction.REVERSE);
        bl.setDirection(DcMotor.Direction.REVERSE);
        waitForStart();
        //moveStraight(12, false);
        //sleep(1500);
        //moveStraight(12, true);

        //telemetry.addData("place", "afterStraight");
        moveAlteredSin(36, false);

        telemetry.addData("place", "afterSinForward");
        //moveSin(12,true);

        //telemetry.addData("place","here");



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
    public void moveAlteredSin (double dist, boolean backwards) {
        //inches
        telemetry.addData("place","moveSin");
        double rotations = dist / (6 * Math.PI);
        double totalTicks = rotations * 1120;
        int positionBeforeMovement = fl.getCurrentPosition();
        while (fl.getCurrentPosition() < positionBeforeMovement + totalTicks) {
            telemetry.addData("front left encoder: ", "sin"+fl.getCurrentPosition());
            telemetry.addData("ticksFor", totalTicks);
            telemetry.addData("place","moveSinWhile");
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

                telemetry.addData("place","1");
                power +=.1;
                //first quarter (period = 2pi) of sin function is only reaching altitude

            }
            else if (currentTick<secondSectionTime) {
                power = .8;

                telemetry.addData("place","2");
            }
            else {
                // between [40%,100%]
                //decrease time
                int ticksLeft=(int) Math.round(currentTick-(totalTicks*.35));
                telemetry.addData("place", "three");
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


    public void rest() {
        fr.setPower(0);
        fl.setPower(0);
        bl.setPower(0);
        br.setPower(0);

    }
}
