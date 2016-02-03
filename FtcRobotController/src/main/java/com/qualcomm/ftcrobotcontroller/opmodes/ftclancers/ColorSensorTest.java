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
        colorFR = hardwareMap.colorSensor.get(Keys.COLOR_FRONT_RIGHT);
        waitForStart();
        while(opModeIsActive()) {
            telemetry.addData("RED", colorFR.red());
            telemetry.addData("BLUE", colorFR.blue());
            telemetry.addData("GREEN", colorFR.green());
        }
    }
}
