package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftcrobotcontroller.Keys;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;

/**
 * Created on 11/7/2015.
 */
public class Treads extends OpMode {

    DcMotor fr, fl, bl, br;
    double pwrLeft, pwrRight;

    //Compass stuff
    //CompassSensor compass;
    //String compassStatus = "success";
    //UltrasonicSensor sonar;

    public void init() {
        fr = hardwareMap.dcMotor.get(Keys.frontRight);
        fl = hardwareMap.dcMotor.get(Keys.frontLeft);
        br = hardwareMap.dcMotor.get(Keys.backRight);
        bl = hardwareMap.dcMotor.get(Keys.backLeft);

        fl.setDirection(DcMotor.Direction.REVERSE);
        bl.setDirection(DcMotor.Direction.REVERSE);
        //sonar = hardwareMap.ultrasonicSensor.get("sonar");


        //More compass stuff DO NOT EDIT
        //compass = hardwareMap.compassSensor.get("compass");
        //Calibration
        //compass.setMode(CompassSensor.CompassMode.CALIBRATION_MODE);
        //if (compass.calibrationFailed()) compassStatus = "failed";
        //telemetry.addData("Compass calibration ", compassStatus);
        //telemetry.addData("Status: ", compass.status());
        //compass.setMode(CompassSensor.CompassMode.MEASUREMENT_MODE);
        //telemetry.addData("Initial position: ", compass.getDirection());
        //telemetry.addData("Sonar Status: ", sonar.status());
    }

    @Override
    public void loop() {
        float leftY = Range.clip(gamepad1.left_stick_y, -1, 1);
        float rightY = Range.clip(gamepad1.right_stick_y, -1, 1);

        pwrLeft = Range.clip(leftY * .78, -1, 1);
        pwrRight = Range.clip(rightY * .78, -1, 1);

        telemetry.addData(Keys.telementryLeftKey, pwrLeft);
        telemetry.addData(Keys.telementryRightKey, pwrRight);
        //telemetry.addData("Compass value: ", compass.getDirection());
        //telemetry.addData("Sonar Value", sonar.getUltrasonicLevel());
        //telemetry.addData("Sonar Status", sonar.status());
        powerSplit(pwrLeft, pwrRight);
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

    public void stop() {
    }
}


