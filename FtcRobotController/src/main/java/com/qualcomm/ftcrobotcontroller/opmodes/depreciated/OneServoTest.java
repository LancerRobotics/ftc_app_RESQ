package com.qualcomm.ftcrobotcontroller.opmodes.depreciated;

import com.qualcomm.ftcrobotcontroller.Keys;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * Created by daniel on 11/13/2015.
 */
public class OneServoTest extends OpMode {
    Servo testServo;
    @Override
    public void init() {
        testServo = hardwareMap.servo.get(Keys.testServo);
    }

    @Override
    public void loop() {
        testServo.setPosition(1);
    }
}
