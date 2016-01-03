package com.qualcomm.ftcrobotcontroller.opmodes.depreciated;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.UltrasonicSensor;

/**
 * Created by AJ on 12/3/2015.
 */
public class SonarTest extends OpMode {
    UltrasonicSensor sonar;

        public void init() {
            sonar = hardwareMap.ultrasonicSensor.get("sonar");
        }

        public void loop() {
            telemetry.addData("Sonar Value", sonar.getUltrasonicLevel());
            telemetry.addData("Sonar Status", sonar.status());
        }
}
