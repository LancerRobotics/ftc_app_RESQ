package com.qualcomm.ftcrobotcontroller.opmodes.ftclancers;

import com.qualcomm.ftcrobotcontroller.Keys;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.AnalogInput;

/**
 * Created on 1/4/2016.
 */
public class AnalogLimitSwitch extends OpMode {
    AnalogInput limit1;

    @Override
    public void init() {
        limit1 = hardwareMap.analogInput.get(Keys.LIMIT_LEFT);
    }
    public void loop() {
        telemetry.addData("Limit switch is pressed: ", getState());
    }
    public boolean getState() {
        if(limit1.getValue() > 0) {
            return true;
        }
        else {
            return false;
        }
    }
}
