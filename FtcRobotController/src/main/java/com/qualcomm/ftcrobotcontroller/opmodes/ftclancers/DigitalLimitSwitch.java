package com.qualcomm.ftcrobotcontroller.opmodes.ftclancers;

import com.qualcomm.ftcrobotcontroller.Keys;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.DigitalChannelController;

/**
 * Created on 1/4/2016.
 */
public class DigitalLimitSwitch extends OpMode {
    DigitalChannel limit1;

    @Override
    public void init() {
        limit1 = hardwareMap.digitalChannel.get(Keys.LIMIT_ONE);
        limit1.setMode(DigitalChannelController.Mode.OUTPUT);
    }
    public void loop() {
        telemetry.addData("Limit switch is pressed: ", limit1.getState());
    }
}