package com.qualcomm.ftcrobotcontroller.deprecated;

import com.qualcomm.ftcrobotcontroller.Keys;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CompassSensor;
import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * Created on 11/7/2015.
 */
public class TreadsAuton extends LinearOpMode{

    DcMotor fr, fl, bl, br;
    double pwrLeft, pwrRight;

    //Compass stuff
    CompassSensor compass;
    String compassStatus;

    @Override
    public void runOpMode() {
        fr = hardwareMap.dcMotor.get(Keys.frontRight);
        fl = hardwareMap.dcMotor.get(Keys.frontLeft);
        br = hardwareMap.dcMotor.get(Keys.backRight);
        bl = hardwareMap.dcMotor.get(Keys.backLeft);

        fl.setDirection(DcMotor.Direction.REVERSE);
        bl.setDirection(DcMotor.Direction.REVERSE);

        //More compass stuff DO NOT EDIT
        compass = hardwareMap.compassSensor.get("compass");
        //Calibration
        compass.setMode(CompassSensor.CompassMode.CALIBRATION_MODE);
        if (compass.calibrationFailed()) compassStatus = "failed";
        telemetry.addData("Compass calibration ", compassStatus);
        telemetry.addData("Status: ", compass.status());
        compass.setMode(CompassSensor.CompassMode.MEASUREMENT_MODE);
        telemetry.addData("Initial position: ", compass.getDirection());


        telemetry.addData(Keys.telementryLeftKey, pwrLeft);
        telemetry.addData(Keys.telementryRightKey, pwrRight);
        moveAlteredSin(60, false);
        compassTurn(90, true);
        /*
        moveAlteredSin(24, false);
        compassTurn(90, false);
        moveAlteredSin(24, false);
        compassTurn(90, true);
        moveAlteredSin(24, false);
        //insert camera code here
        moveAlteredSin(33, true);
        compassTurn(45, false);
        moveAlteredSin(72, false);
        */
    }

    public void powerSplit(double left, double right) {
        //swap left and right, because they're backwards in our configuration
        double tempLeft = left;
        left = right;
        right = tempLeft;
        //switch direction
        left = left * -1;
        right = right * -1;
        telemetry.addData(Keys.telementryFrontLeftPowerKey, left);
        telemetry.addData(Keys.telementryFrontRightPowerKey, right);
        telemetry.addData(Keys.telementryBackLeftPowerKey, left);
        telemetry.addData(Keys.telementryBackRightPowerKey, right);
        fl.setPower(left);
        fr.setPower(right);
        bl.setPower(left);
        br.setPower(right);
    }
    public void compassTurn(double degrees, boolean right) {
        telemetry.addData("Compass value: ", compass.getDirection());
        telemetry.addData("To turn ", degrees + " degrees");
        double pos;
        boolean overUnder = false;
        //turning right
        if (right) {
            pos = compass.getDirection()+degrees;
            if (pos>360){
                pos = pos-compass.getDirection();
                overUnder = true;
            }
            if (overUnder) {
                while(compass.getDirection()<pos)
                    powerSplit(-.2, .2);
            }
            else {
                while(compass.getDirection()>pos)
                    powerSplit(-.2, .2);
            }
        }
        //turning left
        else {
            pos = compass.getDirection()-degrees;
            if(pos<0) {
                pos = 360-Math.abs(pos);
                overUnder = true;
            }
            if (overUnder) {
                while(compass.getDirection()>pos)
                    powerSplit(.2, -.2);
            }
            else {
                while(compass.getDirection()<pos)
                    powerSplit(.2, -.2);
            }
        }
        telemetry.addData("Compass value: ", compass.getDirection());
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
            if(backwards) {
                power = power * 1;
            }
            powerSplit(power, power);
        }
        rest();
    }


    public void rest() {
        fr.setPower(0);
        fl.setPower(0);
        bl.setPower(0);
        br.setPower(0);

    }
}


