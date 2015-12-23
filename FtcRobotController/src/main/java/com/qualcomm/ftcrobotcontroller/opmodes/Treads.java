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
        //collect = hardwareMap.dcMotor.get(Keys.collector);
        fl.setDirection(DcMotor.Direction.REVERSE);
        bl.setDirection(DcMotor.Direction.REVERSE);

    }

    @Override
    public void loop() {
        float leftY = Range.clip(gamepad1.left_stick_y, -1, 1);
        float rightY = Range.clip(gamepad1.right_stick_y, -1, 1);

        pwrLeft = Range.clip(leftY * .78, -1, 1);
        pwrRight = Range.clip(rightY * .78, -1, 1);

        telemetry.addData(Keys.telementryLeftKey, pwrLeft);
        telemetry.addData(Keys.telementryRightKey, pwrRight);
        powerSplit(pwrLeft, pwrRight);
        /*
        if(gamepad1.b) {
            collect.setPower(.85);
        }
        else if(gamepad1.a) {
            collect.setPower(-.85);
        }
        else {
            collect.setPower(0);
        }
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

    public void stop() {
    }
}


