package com.qualcomm.ftcrobotcontroller.deprecated;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.CompassSensor;

/**
 * Created 11/30/2015.
 */
public class CompassLooping extends OpMode {

    /*
    Implementations of a compass sensor in autonomous:
    - autonomous turning
    - for mecanum --> perspective drive
     */

    CompassSensor compass;
    String compassStatus = "success";

    public CompassLooping() {}

    @Override
    public void init() {
        compass = hardwareMap.compassSensor.get("compass");
        //Calibration
        compass.setMode(CompassSensor.CompassMode.CALIBRATION_MODE);
        if (compass.calibrationFailed()) compassStatus = "failed";
        telemetry.addData("Compass calibration ", compassStatus);
        telemetry.addData("Status: ", compass.status());
        compass.setMode(CompassSensor.CompassMode.MEASUREMENT_MODE);
    }

    @Override
    public void loop() {
        telemetry.addData("Compass rotation: ", compass.getDirection());
    }
}

