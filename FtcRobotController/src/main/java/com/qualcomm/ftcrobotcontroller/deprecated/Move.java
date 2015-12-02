package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftcrobotcontroller.Keys;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;

/**
 * Created by AJ on 10/27/2015.
 */
public class Move extends OpMode {

        DcMotor fr, fl, bl, br;
        double pwrLeft,pwrRight;

        public Move() {}

        @Override
        public void init() {
            fr = hardwareMap.dcMotor.get(Keys.frontRight);
            fl = hardwareMap.dcMotor.get(Keys.frontLeft);
            br = hardwareMap.dcMotor.get(Keys.backRight);
            bl = hardwareMap.dcMotor.get(Keys.backLeft);

            fr.setDirection(DcMotor.Direction.REVERSE);
            br.setDirection(DcMotor.Direction.REVERSE);
        }

/*
    Mapping: (all on gamepad 1)

    Joystick Left: movement and singular wheel power - TODO singular wheel power
    Joystick Right: lift and turn - done
*/

        @Override
        public void loop() {
            //MOVEMENT CONTROL
            float gamepad1LeftStickY = Range.clip(gamepad1.left_stick_y,-1,1);
            float gamepad1RightStickY = Range.clip(gamepad1.right_stick_y,-1,1);
            //by multiplying by .78, range turns to [-.78,.78]
            pwrLeft = Range.clip(gamepad1LeftStickY*.78, -1, 1);
            pwrRight = Range.clip(gamepad1RightStickY*.78, -1,1);
            telemetry.addData(Keys.telementryLeftKey,pwrLeft);
            telemetry.addData(Keys.telementryRightKey,pwrRight);
            powerSplit(pwrLeft, pwrRight);
        }

        public void powerSplit(double left, double right){
            //swap left and right, because they're backwards in our configuration
            double tempLeft = left;
            left = right;
            right = tempLeft;
            //switch direction
            left = left*-1;
            right = right*-1;
            telemetry.addData(Keys.telementryFrontLeftPowerKey,left);
            telemetry.addData(Keys.telementryFrontRightPowerKey,right);
            telemetry.addData(Keys.telementryBackLeftPowerKey, left);
            telemetry.addData(Keys.telementryBackRightPowerKey,right);
            fl.setPower(left); fr.setPower(right);
            bl.setPower(left); br.setPower(right);
        }

        @Override
        public void stop() {}
}

