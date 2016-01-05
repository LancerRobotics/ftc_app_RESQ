package com.qualcomm.ftcrobotcontroller.opmodes.ftclancers;

import com.qualcomm.ftcrobotcontroller.Keys;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.DigitalChannelController;

/**
 * Created on 1/4/2016.
 */
public class DigitalLimitSwitch extends OpMode {
    DcMotor fr, fl;
    DigitalChannel limit1;

    @Override
    public void init() {
        fr = hardwareMap.dcMotor.get(Keys.frontRight);
        fl = hardwareMap.dcMotor.get(Keys.frontLeft);
        fr.setDirection(DcMotor.Direction.REVERSE);
        limit1 = hardwareMap.digitalChannel.get(Keys.LIMIT_ONE);
        limit1.setMode(DigitalChannelController.Mode.INPUT);
    }
    public void loop() {
        telemetry.addData("Limit switch is pressed: ", limit1.getState());
    }


    public void turn (double power) {
        fl.setPower(power);
        fr.setPower(-power);
    }
    public void rest() {
        fr.setPower(0);
        fl.setPower(0);
    }
}