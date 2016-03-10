package com.qualcomm.ftcrobotcontroller.opmodes.deprecated;

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
            double y = Range.clip(gamepad1.left_stick_x,-1,1);
            double x = Range.clip(gamepad1.left_stick_y,-1,1);
            double z = Range.clip(gamepad1.right_stick_x,-1,1);

            //DEADZONES
            if (Math.abs(x)<.15) x=0;
            if (Math.abs(y)<.15) y=0;
            if (Math.abs(z)<.15) z=0;
            //TODO think about this Jake - what if the joystick was moved diagonally? So forward and strafe left/right
            //TODO would this cause it to move foward and strafe at the same time? Let me know. Idk which is why I'm asking
            powerSplit(x, y, z);
        }

        public void powerSplit(double x, double y, double z){
            telemetry.addData(Keys.telementryFrontLeftPowerKey,x+y-z);
            telemetry.addData(Keys.telementryFrontRightPowerKey,x-y-z);
            telemetry.addData(Keys.telementryBackLeftPowerKey, -x+y-z);
            telemetry.addData(Keys.telementryBackRightPowerKey,-x-y-z);
            fl.setPower(Range.clip((-x-y-z)*.78, -1, 1)); fr.setPower(Range.clip((-x-y+z) * .78, -1, 1));
            bl.setPower(Range.clip((-x+y-z)*.78, -1, 1)); br.setPower(Range.clip((-x+y+z) * .78, -1, 1));
        }


        @Override
        public void stop() {}
}

