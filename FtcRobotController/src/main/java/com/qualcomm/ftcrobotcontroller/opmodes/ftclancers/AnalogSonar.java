package com.qualcomm.ftcrobotcontroller.opmodes.ftclancers;

import com.qualcomm.ftcrobotcontroller.Keys;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.AnalogInput;

/**
 * Created on 1/4/2016.
 */
public class AnalogSonar extends OpMode {
    static AnalogInput sonarFrontLeft, sonarFrontRight, sonarBackRight, sonarBackLeft;

    @Override
    public void init() {
        sonarFrontLeft = hardwareMap.analogInput.get(Keys.SONAR_FRONT_LEFT);
        sonarFrontRight = hardwareMap.analogInput.get(Keys.SONAR_FRONT_RIGHT);
        sonarBackLeft = hardwareMap.analogInput.get(Keys.SONAR_BACK_LEFT);
        sonarBackRight = hardwareMap.analogInput.get(Keys.SONAR_BACK_RIGHT);
    }

    public void loop() {
        telemetry.addData("Object Behind? ", objectBehind());
        telemetry.addData("Object In Front? ", objectInFront());
    }

    public void stop() {
        sonarFrontLeft.close();
        sonarFrontRight.close();
        sonarBackLeft.close();
        sonarBackRight.close();
    }

    //returns sonar values in inches!!!
    public static double readSonar(AnalogInput sonar) {
        double sValue = sonar.getValue();
        sValue = sValue/2;
        return sValue;
    }

    public static boolean objectBehind () {
        double sonarBL = readSonar(sonarBackLeft);
        double sonarBR = readSonar(sonarBackRight);
        if (sonarBL < 10 || sonarBR < 10) {
            return true;
        }
        else {
            return false;
        }
    }

    public static boolean objectInFront () {
        double sonarFL = readSonar(sonarFrontLeft);
        double sonarFR = readSonar(sonarFrontRight);
        if (sonarFL < 10 || sonarFR < 10) {
            return true;
        }
        else {
            return false;
        }
    }
}
