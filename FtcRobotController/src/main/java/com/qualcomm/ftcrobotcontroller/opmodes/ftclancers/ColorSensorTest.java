package com.qualcomm.ftcrobotcontroller.opmodes.ftclancers;

import android.graphics.Color;

import com.qualcomm.ftcrobotcontroller.Keys;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;


/**
 * Created by spork on 2/2/2016.
 */
public class ColorSensorTest extends LinearOpMode {
    ColorSensor colorFR;
    public void runOpMode() throws InterruptedException {
        hardwareMap.logDevices();
        colorFR = hardwareMap.colorSensor.get(Keys.COLOR_FRONT_RIGHT);
        waitOneFullHardwareCycle();
        waitForStart();
        while(true) {
            telemetry.addData("RED", (colorFR.red() * 255) / 800);
            telemetry.addData("BLUE", (colorFR.blue() * 255) / 800);
            telemetry.addData("GREEN", (colorFR.green() * 255) / 800);
        }
    }
}
