package com.qualcomm.ftcrobotcontroller.opmodes.states;

import com.kauailabs.navx.ftc.AHRS;
import com.qualcomm.ftcrobotcontroller.Keys;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

/**
 * Created by jakew on 1/18/2016.
 */
public class ToggleWithoutBool extends OpMode{
    private AHRS navx_device;
    //AnalogInput limitLeft;
    AnalogInput limitRight;
    //Motors
    DcMotor fr, fl, bl, br, liftLeft, liftRight, collector, winch;
    double pwrLeft, pwrRight;

    //Servos
    Servo swivel, dump, hopperLeft, climber, hang, clampRight, clampLeft, hopperRight, triggerRight, triggerLeft;

    //Lift
    double liftPwr;

    //Pull
    double pullPwr;

    //Joystick 1 --> DRIVER
    float gamepad1LeftStickY, gamepad1RightStickY;
    //Joystick 2 --> GUNNER
    float gamepad2LeftStickY, gamepad2RightStickY;

    boolean dumpDown;
    boolean hanging;
    boolean leftClamped;
    boolean rightClamped;
    boolean climbers;
    boolean rightTrigger;
    boolean leftTrigger;
    boolean hopperDown = false;
    boolean swivelLeft = false;
    boolean swivelRight = false;
    boolean swivelCenter = true;

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
        hopperLeft = hardwareMap.servo.get(Keys.hopperLeft);
        hopperRight = hardwareMap.servo.get(Keys.hopperRight);
        clampLeft = hardwareMap.servo.get(Keys.clampLeft);
        clampRight = hardwareMap.servo.get(Keys.clampRight);
        dump = hardwareMap.servo.get(Keys.dump);
        triggerLeft = hardwareMap.servo.get(Keys.triggerLeft);
        triggerRight = hardwareMap.servo.get(Keys.triggerRight);

        fr.setDirection(DcMotor.Direction.REVERSE);
        br.setDirection(DcMotor.Direction.REVERSE);
        liftLeft.setDirection(DcMotor.Direction.REVERSE);

        climber.setPosition(Keys.CLIMBER_INITIAL_STATE);
        dump.setPosition(Keys.DUMP_INIT);
        swivel.setPosition(Keys.SWIVEL_CENTER);
        hang.setPosition(Keys.HANG_INIT);
        hopperLeft.setPosition(Keys.HL_STORE);
        hopperRight.setPosition(Keys.HR_STORE);
        clampLeft.setPosition(Keys.CLAMP_LEFT_INIT);
        clampRight.setPosition(Keys.CLAMP_RIGHT_INIT);
        triggerLeft.setPosition(Keys.LEFT_TRIGGER_INIT);
        triggerRight.setPosition(Keys.RIGHT_TRIGGER_INIT);

        dumpDown = false;
        hanging = false;
        leftClamped = false;
        rightClamped = false;
        climbers = false;
        rightTrigger = false;
        leftTrigger = false;

        navx_device = AHRS.getInstance(hardwareMap.deviceInterfaceModule.get(Keys.advancedSensorModule), Keys.NAVX_DIM_I2C_PORT, AHRS.DeviceDataType.kProcessedData, Keys.NAVX_DEVICE_UPDATE_RATE_HZ);
        //limitLeft = hardwareMap.analogInput.get(Keys.LIMIT_LEFT);
        limitRight = hardwareMap.analogInput.get(Keys.LIMIT_RIGHT);
    }

    public void loop() {
        //TODO test values
        /*
        DRIVER:
         - movement DONE
         - clamps DONE
         - climbers DONE

        GUNNER:
         - lift DONE
         - swivel
         - dump DONE
         - collector DONE
         */

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
        if (gamepad2.left_bumper)
            collectorMovement(false, false);
        if (gamepad2.left_bumper)
            collectorMovement(true, false);
        else
            collectorMovement(true, true);


        //Hopper
        if (gamepad2.a && !hopperDown) {
            hopperLeft.setPosition(Keys.HL_DUMP);
            hopperRight.setPosition(Keys.HR_DUMP);
            hopperDown = true;
        } else if (gamepad2.a && hopperDown){
            hopperLeft.setPosition(Keys.HL_STORE);
            hopperRight.setPosition(Keys.HR_STORE);
            hopperDown = false;
        }

        //Dump
        if(gamepad2.y && (dump.getPosition() != Keys.DUMP_DOWN)) {
            dump.setPosition(Keys.DUMP_DOWN);
            telemetry.addData("The dump is now dowwwwwwn", dump.getPosition() != Keys.DUMP_DOWN);
        }
        else if (gamepad2.y && (dump.getPosition() != Keys.DUMP_INIT)) {
            dump.setPosition(Keys.DUMP_INIT);
            telemetry.addData("The dump shud be going upppppp", dump.getPosition() != Keys.DUMP_INIT);
        }

        //Hang
        if (gamepad2.x && !hanging) {
            hang.setPosition(Keys.HANG_NOW);
            hanging = true;
        } else if (gamepad2.x && hanging){
            hang.setPosition(Keys.HANG_INIT);
            hanging = false;
        }

        //Clamps (for ramp)
        if (gamepad1.right_bumper && !rightClamped) {
            clampRight.setPosition(Keys.CLAMP_RIGHT_DOWN);
            rightClamped = true;
        } else if (gamepad1.right_bumper && rightClamped) {
            clampRight.setPosition(Keys.CLAMP_RIGHT_INIT);
            rightClamped = false;
        }
        if(gamepad1.left_bumper && !leftClamped) {
            clampLeft.setPosition(Keys.CLAMP_LEFT_DOWN);
            leftClamped = true;
        } else if (gamepad1.left_bumper && leftClamped) {
            clampLeft.setPosition(Keys.CLAMP_LEFT_INIT);
            leftClamped = false;
        }

        //Climbers
        if (gamepad1.y && !climbers) {
            climber.setPosition(Keys.CLIMBER_DUMP);
            climbers = true;
        } else if (gamepad1.y && climbers) {
            climber.setPosition(Keys.CLIMBER_INITIAL_STATE);
            climbers = false;
        }

        //Triggers
        if(gamepad2.right_trigger > .15 && !rightTrigger) {
            triggerRight.setPosition(Keys.RIGHT_TRIGGER_TRIGGER);
            rightTrigger = true;
        }
        else if(gamepad2.right_trigger > .15 && rightTrigger) {
            triggerRight.setPosition(Keys.RIGHT_TRIGGER_INIT);
            rightTrigger = false;
        }
        if(gamepad2.left_trigger > .15 && !leftTrigger) {
            triggerLeft.setPosition(Keys.LEFT_TRIGGER_TRIGGER);
            leftTrigger = true;
        }
        else if(gamepad2.left_trigger > .15 && leftTrigger) {
            triggerLeft.setPosition(Keys.LEFT_TRIGGER_INIT);
            leftTrigger = false;
        }

        //Swivels
        if(gamepad1.x && !swivelLeft) {
            swivel.setPosition(Keys.SWIVEL_LEFT);
            swivelLeft = true;
            swivelCenter = false;
            swivelRight = false;
        }
        else if(gamepad1.b && !swivelRight) {
            swivel.setPosition(Keys.SWIVEL_RIGHT);
            swivelLeft = false;
            swivelCenter = false;
            swivelRight = true;
        }
        else if(gamepad1.a && !swivelCenter) {
            swivel.setPosition(Keys.SWIVEL_CENTER);
            swivelLeft = false;
            swivelCenter = true;
            swivelRight = false;
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
        winch.setPower(power);
    }

    public boolean getState(AnalogInput limitSwitch) {
        if(limitSwitch.getValue() > 100) {
            return true;
        }
        else {
            return false;
        }
    }
}