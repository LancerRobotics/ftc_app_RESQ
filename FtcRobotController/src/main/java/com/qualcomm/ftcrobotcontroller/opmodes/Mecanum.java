package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftcrobotcontroller.Keys;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;

/**
 * Created 11/30/2015.
 */
public class Mecanum extends OpMode {

        DcMotor fr, fl, bl, br;
        double pwrLeft,pwrRight;

        public Mecanum() {}

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
    Left joystick:
        x = stafe left and right
        y = forward and backward
    Right joystick:
        x: turn left and right
*/

        @Override
        public void loop() {
            //MOVEMENT CONTROL
            //by multiplying by .78, range turns to [-.78,.78]
            double x = Range.clip(gamepad1.left_stick_x*.78,-1,1);
            double y = Range.clip(gamepad1.left_stick_y*.78,-1,1);
            double z = Range.clip(gamepad1.right_stick_x*.78,-1,1);

            //DEADZONES
            if (Math.abs(x)<.15) x=0;
            if (Math.abs(y)<.15) y=0;
            if (Math.abs(z)<.15) z=0;
            //TODO think about this Jake - what if the joystick was moved diagonally? So forward and strafe left/right
            //TODO would this cause it to move foward and strafe at the same time? Let me know. Idk which is why I'm asking
            //Forward and backward
            powerSplit(y, y, y, y);
            //Strafing left and right
            powerSplit(x, -x, -x, x);
            //Turning
            powerSplit(-z, -z, z, z);

        }

        public void powerSplit(double frontl, double backl, double frontr, double backr){
            telemetry.addData(Keys.telementryFrontLeftPowerKey,frontl);
            telemetry.addData(Keys.telementryFrontRightPowerKey,frontr);
            telemetry.addData(Keys.telementryBackLeftPowerKey, backl);
            telemetry.addData(Keys.telementryBackRightPowerKey,backr);
            fl.setPower(frontl); fr.setPower(frontr);
            bl.setPower(backl); br.setPower(backr);
        }


        @Override
        public void stop() {}
}

