package com.qualcomm.ftcrobotcontroller.opmodes.states;

import com.qualcomm.ftcrobotcontroller.Keys;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * Created by spork on 2/21/2016.
 */
public class ToggleWithButtonMethod extends OpMode {
    Servo climber;
    static boolean pressed;
    double[] climberPositions = {Keys.CLIMBER_INITIAL_STATE, Keys.CLIMBER_DUMP};
    int climberPos;
    public void init() {
        climber = hardwareMap.servo.get(Keys.climber);
        climber.setPosition(climberPositions[0]);
        climberPos = 1;
    }
    public void loop() {
        climberPos = toggle(gamepad2.a, climber, climberPositions, climberPos);
    }
    public void stop() {}
    public int toggle(boolean button, Servo servo, double[] positions, int currentPos) {
        int servoPositions = positions.length;
        if(button) {
            pressed = true;
        }
        if(pressed) {
            if(servoPositions == 2) {
                if(currentPos == 1) {
                    servo.setPosition(positions[1]);
                    if(!button) {
                        pressed = false;
                        currentPos = 2;
                    }
                }
                else if(currentPos == 2) {
                    servo.setPosition(positions[0]);
                    if(!button) {
                        pressed = false;
                        currentPos = 1;
                    }
                }
            }
        }
        return currentPos;
    }
}
