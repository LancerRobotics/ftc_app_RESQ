package com.qualcomm.ftcrobotcontroller.opmodes.ftclancers;

import android.graphics.Color;

import com.qualcomm.ftcrobotcontroller.Keys;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;

public class ColorSensorTest extends LinearOpMode {
    ColorSensor colorFR;
    public void runOpMode() throws InterruptedException {

        hardwareMap.logDevices();
        colorFR = hardwareMap.colorSensor.get(Keys.COLOR_FRONT_RIGHT);

        waitOneFullHardwareCycle();
        waitForStart();

        while (opModeIsActive()) {
            telemetry.addData("Hue", altColorSensor());
        }
    }

    public int altColorSensor() {
        return colorFR.argb();
    }

    //pretty sure this doesn't work
    public float colorSensorValue(float[] values) {
        Color.RGBToHSV((colorFR.red() * 255) / 800, (colorFR.green() * 255) / 800, (colorFR.blue() * 255) / 800, values);
        return values[0];
    }
}
