package com.qualcomm.ftcrobotcontroller.opmodes.ftclancers;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoController;

/**
 * Created by spork on 2/8/2016.
 */


//THIS IS FOR LATER USAGE, DO NOT TOUCH!!!!

public class ServoTest extends OpMode {
    ServoController servoTest;
    @Override
    public void init() {
        servoTest = hardwareMap.servoController.get("servo_controller");
    }

    @Override
    public void loop() {
        setServoPosition(1, 255);
    }
    public void stop() {

    }
    public void setServoPosition(int portNumber, int position) {
        if(position > 205) {
            servoTest.setServoPosition(portNumber, 1);
            servoTest.pwmDisable();
            servoTest.pwmEnable();
            position = 255 - position;
            position = position/255;
            servoTest.setServoPosition(portNumber, position);
        }
        else if (position < 50) {
            servoTest.setServoPosition(portNumber, 0);
            servoTest.pwmDisable();
            servoTest.pwmEnable();
            position = 50 - position;
            position = position/255;
            servoTest.setServoPosition(portNumber, position);
        }
        else {
            servoTest.setServoPosition(portNumber, position/255);
        }
    }
}
