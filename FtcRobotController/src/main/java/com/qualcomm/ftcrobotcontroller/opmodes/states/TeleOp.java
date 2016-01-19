package com.qualcomm.ftcrobotcontroller.opmodes.states;

import com.qualcomm.ftcrobotcontroller.Keys;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

/**
 * Created by jakew on 1/18/2016.
 */
public class TeleOp extends OpMode{
    //Motors
    DcMotor fr, fl, bl, br, liftLeft, liftRight, collector;
    double pwrLeft, pwrRight;

    //Servos
    Servo swivel, dump1, dump2, score, climber, hang, clamp1, clamp2;

    //Lift
    double liftPwr;

    //Joystick 1 --> DRIVER
    float gamepad1LeftStickY, gamepad1RightStickY;
    //Joystick 2 --> GUNNER
    float gamepad2LeftStickY;
    boolean gamepad2LeftTrigger;
    boolean gamepad2LeftBumper;

    boolean dumpDown;
    boolean hanging;
    boolean clamped;
    boolean climbers;

    public void init() {
        fr = hardwareMap.dcMotor.get(Keys.frontRight);
        fl = hardwareMap.dcMotor.get(Keys.frontLeft);
        br = hardwareMap.dcMotor.get(Keys.backRight);
        bl = hardwareMap.dcMotor.get(Keys.backLeft);

        collector = hardwareMap.dcMotor.get(Keys.collector);
        liftLeft = hardwareMap.dcMotor.get(Keys.liftLeft);
        liftRight = hardwareMap.dcMotor.get(Keys.liftRight);

        score = hardwareMap.servo.get(Keys.score);
        climber = hardwareMap.servo.get(Keys.climber);
        swivel = hardwareMap.servo.get(Keys.swivel);
        hang = hardwareMap.servo.get(Keys.hang);
        dump1 = hardwareMap.servo.get(Keys.dump2);
        dump2 = hardwareMap.servo.get(Keys.dump1);
        clamp1 = hardwareMap.servo.get(Keys.clamp1);
        clamp2 = hardwareMap.servo.get(Keys.clamp2);

        fl.setDirection(DcMotor.Direction.REVERSE);
        bl.setDirection(DcMotor.Direction.REVERSE);
        liftRight.setDirection(DcMotor.Direction.REVERSE);
        clamp2.setDirection(Servo.Direction.REVERSE);
        dump2.setDirection(Servo.Direction.REVERSE);

        climber.setPosition(Keys.CLIMBER_INITIAL_STATE);
        score.setPosition(Keys.SCORE_CLOSE);
        swivel.setPosition(Keys.SWIVEL_CENTER);
        hang.setPosition(Keys.HANG_INIT);
        dump1.setPosition(Keys.DUMP_INIT);
        dump2.setPosition(Keys.DUMP_INIT);
        clamp1.setPosition(Keys.CL_INIT);
        clamp2.setPosition(Keys.CR_INIT);

        dumpDown = false;
        hanging = false;
        clamped = false;
        climbers = false;
    }

    public void loop() {
        //TODO deadzones and test values
        /*
        DRIVER:
         - movement DONE TODO NEEDS DEADZONES
         - clamps DONE
         - climbers DONE

        GUNNER:
         - lift DONE TODO NEEDS DEADZONES
         - swivel
         - dump DONE
         - collector DONE
         */

        gamepad1LeftStickY = Range.clip(gamepad1.left_stick_y, -1, 1);
        gamepad1RightStickY = Range.clip(gamepad1.right_stick_y, -1, 1);
        gamepad2LeftStickY = Range.clip(gamepad2.left_stick_y, -1, 1);
        gamepad2LeftTrigger = gamepad2.left_trigger > .15;
        gamepad2LeftBumper = gamepad2.left_bumper;

        pwrLeft = Range.clip(gamepad1LeftStickY * .78, -1, 1);
        pwrRight = Range.clip(gamepad1RightStickY * .78, -1, 1);
        liftPwr = Range.clip(gamepad2LeftStickY * .78, -1, 1);

        //Movement
        powerSplit(pwrLeft, pwrRight);

        //Lift
        liftMove(liftPwr);

        //Collector
        if (gamepad2LeftBumper)
            collectorMovement(false);
        else if (gamepad2LeftTrigger)
            collectorMovement(true);

        //Dump
        if (gamepad2.a && !dumpDown) {
            dump1.setPosition(Keys.DUMP_DOWN);
            dump2.setPosition(-Keys.DUMP_DOWN);
            dumpDown = true;
        } else if (gamepad2.a && dumpDown){
            dump1.setPosition(Keys.DUMP_INIT);
            dump2.setPosition(-Keys.DUMP_INIT);
            dumpDown = true;
        }

        //Hang
        if (gamepad2.b && !hanging) {
            hang.setPosition(Keys.HANG_NOW);
            hanging = true;
        } else if (gamepad2.a && hanging){
            hang.setPosition(Keys.HANG_INIT);
            hanging = false;
        }

        //Clamps (for ramp)
        if (gamepad1.a && !clamped) {
            clamp1.setPosition(Keys.CL_DOWN);
            clamp2.setPosition(-Keys.CR_DOWN);
            clamped = true;
        } else if (gamepad1.a && clamped) {
            clamp1.setPosition(Keys.CL_INIT);
            clamp2.setPosition(-Keys.CR_INIT);
            clamped = false;
        }

        //Climbers
        if (gamepad1.x && !climbers) {
            clamp1.setPosition(Keys.CLIMBER_DUMP);
            clamp2.setPosition(-Keys.CLIMBER_DUMP);
            clamped = true;
        } else if (gamepad1.x && climbers) {
            clamp1.setPosition(Keys.CL_INIT);
            clamp2.setPosition(-Keys.CL_INIT);
            clamped = false;
        }
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
        liftLeft.setPower(Range.clip(power * Keys.MAX_SPEED, -1, 1));
        liftRight.setPower(Range.clip(power* Keys.MAX_SPEED, -1, 1));
    }

    //Method for collection system
    public void collectorMovement(boolean backward) {
        if (backward)
            collector.setPower(-Keys.COLLECTOR);
        else
            collector.setPower((Keys.COLLECTOR));
    }
}
