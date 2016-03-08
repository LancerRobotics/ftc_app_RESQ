package com.qualcomm.ftcrobotcontroller.opmodes.supers;

import com.kauailabs.navx.ftc.AHRS;
import com.qualcomm.ftcrobotcontroller.Keys;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

/**
 * Created by AJ on 1/18/2016.
 */
public class TeleOpSupers extends OpMode{
    private AHRS navx_device;
    //AnalogInput limitLeft;
    AnalogInput limitRight;
    //Motors
    DcMotor fr, fl, bl, br, liftLeft, liftRight, collector, winch;
    double pwrLeft, pwrRight;

    //Servos
    Servo jam, swivel, dump, climber, hang, clampRight, clampLeft, triggerRight, triggerLeft;

    //Climbers
    boolean climberButtonPressed = false;
    double[] climberPositions = {Keys.CLIMBER_INITIAL_STATE, Keys.CLIMBER_DUMP};
    int climberPos;
    int climberToggleReturnArray[] = new int[2];

    //Jam
    boolean jamButtonPressed = false;
    double[] jamPositions = {Keys.JAM_INIT, Keys.JAM_JAM};
    int jamPos;
    int jamToggleReturnArray[] = new int[2];

    //Lift
    double liftPwr;

    //Pull
    double pullPwr;

    //Joystick 1 --> DRIVER
    float gamepad1LeftStickY, gamepad1RightStickY;
    //Joystick 2 --> GUNNER
    float gamepad2LeftStickY, gamepad2RightStickY;

    boolean calibration_complete;

    public void init() {
        fr = hardwareMap.dcMotor.get(Keys.frontRight);
        fl = hardwareMap.dcMotor.get(Keys.frontLeft);
        br = hardwareMap.dcMotor.get(Keys.backRight);
        bl = hardwareMap.dcMotor.get(Keys.backLeft);

        collector = hardwareMap.dcMotor.get(Keys.collector);
        liftLeft = hardwareMap.dcMotor.get(Keys.liftLeft);
        liftRight = hardwareMap.dcMotor.get(Keys.liftRight);
        winch = hardwareMap.dcMotor.get(Keys.winch);
        climber = hardwareMap.servo.get(Keys.climber);
        swivel = hardwareMap.servo.get(Keys.swivel);
        hang = hardwareMap.servo.get(Keys.hang);
        clampLeft = hardwareMap.servo.get(Keys.clampLeft);
        clampRight = hardwareMap.servo.get(Keys.clampRight);
        dump = hardwareMap.servo.get(Keys.dump);
        triggerLeft = hardwareMap.servo.get(Keys.triggerLeft);
        triggerRight = hardwareMap.servo.get(Keys.triggerRight);
        jam = hardwareMap.servo.get(Keys.jam);

        fr.setDirection(DcMotor.Direction.REVERSE);
        br.setDirection(DcMotor.Direction.REVERSE);
        liftLeft.setDirection(DcMotor.Direction.REVERSE);

        climberPos = 1;
        climber.setPosition(climberPositions[0]);

        jamPos = 1;
        jam.setPosition(jamPositions[0]);
        dump.setPosition(Keys.DUMP_INIT);
        swivel.setPosition(Keys.SWIVEL_CENTER);
        hang.setPosition(Keys.HANG_INIT);
        clampLeft.setPosition(Keys.CLAMP_LEFT_INIT);
        clampRight.setPosition(Keys.CLAMP_RIGHT_INIT);
        triggerLeft.setPosition(Keys.LEFT_TRIGGER_INIT);
        triggerRight.setPosition(Keys.RIGHT_TRIGGER_INIT);
        navx_device = AHRS.getInstance(hardwareMap.deviceInterfaceModule.get(Keys.advancedSensorModule), Keys.NAVX_DIM_I2C_PORT, AHRS.DeviceDataType.kProcessedData, Keys.NAVX_DEVICE_UPDATE_RATE_HZ);
        //limitLeft = hardwareMap.analogInput.get(Keys.LIMIT_LEFT);
        limitRight = hardwareMap.analogInput.get(Keys.LIMIT_RIGHT);
        while ( !calibration_complete ) {
            calibration_complete = !navx_device.isCalibrating();
            if (!calibration_complete) {
                telemetry.addData("Start Teleop?", "No");
            }
        }
        telemetry.addData("Start Teleop?", "Yes");
    }

    public void loop() {

        gamepad1LeftStickY = Range.clip(gamepad1.left_stick_y, -1, 1);
        if(Math.abs(gamepad1LeftStickY) < .15) {
            gamepad1LeftStickY = 0;
        }
        gamepad1RightStickY = Range.clip(gamepad1.right_stick_y, -1, 1);
        if(Math.abs(gamepad1RightStickY) < .15) {
            gamepad1RightStickY = 0;
        }
        gamepad2LeftStickY = Range.clip(gamepad2.left_stick_y, -1, 1);
        if(Math.abs(gamepad2LeftStickY) < .15) {
            gamepad2LeftStickY = 0;
        }
        gamepad2RightStickY = Range.clip(gamepad2.right_stick_y, -1, 1);
        if(Math.abs(gamepad2RightStickY) < .15) {
            gamepad2RightStickY = 0;
        }

        pwrLeft = Range.clip(gamepad1LeftStickY * .86, -1, 1);
        pwrRight = Range.clip(gamepad1RightStickY * .86, -1, 1);
        liftPwr = Range.clip(gamepad2LeftStickY * .86, -1, 1);
        pullPwr = Range.clip(gamepad2RightStickY * .86, -1, 1);

        //Movement
        powerSplit(pwrLeft, pwrRight);

        //Lift
        liftMove(liftPwr);

        //Pull Up
        pullUp(pullPwr);

        //Collector
        if(gamepad2.left_trigger > .15)
            collectorMovement(false, false);
        else if (gamepad2.left_bumper)
            collectorMovement(true, false);
        else
            collectorMovement(true, true);

        //Dump
        if(gamepad2.right_trigger > .15) {
            dump.setPosition(Keys.DUMP_DOWN);
        }
        else if (gamepad2.right_bumper) {
            dump.setPosition(Keys.DUMP_INIT);
        }

        //Hang
        if (gamepad2.dpad_left || gamepad2.dpad_right) {
            hang.setPosition(Keys.HANG_HALFWAY);
        } else if (gamepad2.dpad_up){
            hang.setPosition(Keys.HANG_NOW);
        } else if (gamepad2.dpad_down) {
            hang.setPosition(Keys.HANG_INIT);
        }

        //Clamps (for ramp)
        if (gamepad1.right_trigger > .15) {
            clampRight.setPosition(Keys.CLAMP_RIGHT_DOWN);
        } else if (gamepad1.right_bumper) {
            clampRight.setPosition(Keys.CLAMP_RIGHT_INIT);
        } else if (gamepad1.left_trigger > .15) {
            clampLeft.setPosition(Keys.CLAMP_LEFT_DOWN);
        } else if (gamepad1.left_bumper) {
            clampLeft.setPosition(Keys.CLAMP_LEFT_INIT);
        }

        //Climbers
        climberToggleReturnArray = toggle(gamepad1.y, climber, climberPositions, climberPos, climberButtonPressed);
        climberPos = climberToggleReturnArray[0];
        if(climberToggleReturnArray[1] == 1) {
            climberButtonPressed = true;
        }
        else {
            climberButtonPressed = false;
        }

        //Jam
        jamToggleReturnArray = toggle(gamepad2.a, jam, jamPositions, jamPos, jamButtonPressed);
        jamPos = jamToggleReturnArray[0];
        if(jamToggleReturnArray[1] == 1) {
            jamButtonPressed = true;
        }
        else {
            jamButtonPressed = false;
        }

        //Triggers
        if(gamepad2.x) {
            triggerRight.setPosition(Keys.RIGHT_TRIGGER_TRIGGER);
        }
        else if(gamepad2.b) {
            triggerLeft.setPosition(Keys.LEFT_TRIGGER_TRIGGER);
        }
        else if(gamepad2.y) {
            triggerRight.setPosition(Keys.RIGHT_TRIGGER_INIT);
            triggerLeft.setPosition(Keys.LEFT_TRIGGER_INIT);
        }


        //Auto-Trigger
        /*if(!gamepad2.x && !gamepad2.b && !gamepad2.a && !rightTrigger && !leftTrigger) {
            if (navx_device.getPitch() > Keys.PITCH_OF_MOUNTAIN) {
                triggerLeft.setPosition(Keys.LEFT_TRIGGER_TRIGGER);
                triggerRight.setPosition(Keys.RIGHT_TRIGGER_TRIGGER);
            }
            else {
                triggerLeft.setPosition(Keys.LEFT_TRIGGER_INIT);
                triggerRight.setPosition(Keys.RIGHT_TRIGGER_INIT);
                //else do nothing
            }
        }*/

        //Swivels
        if(gamepad1.x) {
            swivel.setPosition(Keys.SWIVEL_LEFT);
        }
        else if(gamepad1.b) {
            swivel.setPosition(Keys.SWIVEL_RIGHT);
        }
        else if(gamepad1.a) {
            swivel.setPosition(Keys.SWIVEL_CENTER);
        }

        telemetry.addData("Gamepad 2 Left Bumper Pressed?", gamepad2.left_bumper);
        telemetry.addData("Gamepad 2 Right Bumper Pressed?", gamepad2.right_bumper);
    }

    //Motor power for the wheels, for movement
    public void powerSplit(double left, double right) {
        telemetry.addData(Keys.telementryFrontLeftPowerKey, left);
        telemetry.addData(Keys.telementryFrontRightPowerKey, right);
        telemetry.addData(Keys.telementryBackLeftPowerKey, left);
        telemetry.addData(Keys.telementryBackRightPowerKey, right);
        fl.setPower(left);
        fr.setPower(right);
        bl.setPower(left);
        br.setPower(right);
    }

    //Clips the power that the lift motors can recieve and sets them to this clipped power.
    public void liftMove(double power) {
        if (power >= 0) {
            liftLeft.setPower(Range.clip(power * Keys.MAX_SPEED, -1, 1));
            liftRight.setPower(Range.clip(power * Keys.MAX_SPEED, -1, 1));
        }
        else {
            if(!getState(limitRight)) {
                liftLeft.setPower(Range.clip(power * Keys.MAX_SPEED, -1, 1));
                liftRight.setPower(Range.clip(power * Keys.MAX_SPEED, -1, 1));
            }
            else {
                liftLeft.setPower(0);
                liftRight.setPower(0);
            }
        }
    }

    //Method for collection system
    public void collectorMovement(boolean backward, boolean stop) {
        if(!stop) {
            if (backward)
                collector.setPower(-Keys.COLLECTOR);
            else
                collector.setPower((Keys.COLLECTOR));
        }
        else {
            collector.setPower(0);
        }
    }

    //Method for pulling bot up mountain when hanging
    public void pullUp(double power) {
        winch.setPower(-power);
    }

    public boolean getState(AnalogInput limitSwitch) {
        if(limitSwitch.getValue() > 100) {
            return true;
        }
        else {
            return false;
        }
    }

    public int[] toggle(boolean button, Servo servo, double[] positions, int currentPos, boolean pressed) {
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
            else if(servoPositions == 3) {
                if(currentPos == 1) {
                    servo.setPosition(positions[1]);
                    if(!button) {
                        pressed = false;
                        currentPos = 2;
                    }
                }
                else if(currentPos == 2) {
                    servo.setPosition(positions[2]);
                    if(!button) {
                        pressed = false;
                        currentPos = 3;
                    }
                }
                else if(currentPos == 3) {
                    servo.setPosition(positions[0]);
                    if(!button) {
                        pressed = false;
                        currentPos = 1;
                    }
                }
            }
        }
        int bool;
        if (pressed) {
            bool = 1;
        }
        else {
            bool = 0;
        }
        int returnArray[] = new int[2];
        returnArray[0] = currentPos;
        returnArray[1] = bool;
        return returnArray;
    }
}
