package com.qualcomm.ftcrobotcontroller.opmodes.ftclancers;

import com.qualcomm.ftcrobotcontroller.Keys;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.AnalogInput;

/**
 * Created on 1/4/2016.
 */
public class AnalogSonar extends OpMode {
    AnalogInput sonar1;

    @Override
    public void init() {
        sonar1 = hardwareMap.analogInput.get(Keys.SONAR_ONE);
    }
    public void loop() {
        double s1 = readSonar(sonar1);
        telemetry.addData("Sonar report (in inches yippy): ", s1);
    }

    public void stop() {
        sonar1.close();
    }

    //returns sonar values in inches!!!
    public static double readSonar(AnalogInput sonar) {
        double sValue = sonar.getValue();
        sValue = sValue/2;
        return sValue;
    }
}
