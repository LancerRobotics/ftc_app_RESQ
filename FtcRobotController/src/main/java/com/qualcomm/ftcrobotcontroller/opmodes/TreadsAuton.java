package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftcrobotcontroller.Keys;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CompassSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;

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

        compassTurn(90, true);
        compassTurn(90, false);
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
}


