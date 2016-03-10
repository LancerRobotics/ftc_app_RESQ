package com.qualcomm.ftcrobotcontroller.opmodes.deprecated;

import com.qualcomm.ftcrobotcontroller.Keys;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;

/**
 * Created 11/12/2015.
 */
public class Judging extends LinearOpMode {
    DcMotor fr, fl, bl, br;
    DcMotor liftLeft;
    DcMotor liftRight;
    Servo filterLeft, filterRight, swivel, score, climber;
    DcMotor collector;
    TouchSensor touch;

    @Override
    public void runOpMode() throws InterruptedException {
        fr = hardwareMap.dcMotor.get(Keys.frontRight);
        fl = hardwareMap.dcMotor.get(Keys.frontLeft);
        br = hardwareMap.dcMotor.get(Keys.backRight);
        bl = hardwareMap.dcMotor.get(Keys.backLeft);
        fl.setDirection(DcMotor.Direction.REVERSE);
        bl.setDirection(DcMotor.Direction.REVERSE);
        score = hardwareMap.servo.get(Keys.score);
        liftLeft = hardwareMap.dcMotor.get(Keys.liftLeft);
        liftRight = hardwareMap.dcMotor.get(Keys.liftRight);
        liftRight.setDirection(DcMotor.Direction.REVERSE);
        climber = hardwareMap.servo.get(Keys.climber);
        filterLeft = hardwareMap.servo.get(Keys.filterLeft);
        filterRight = hardwareMap.servo.get(Keys.filterRight);
        swivel = hardwareMap.servo.get(Keys.swivel);
        collector = hardwareMap.dcMotor.get(Keys.collector);
        touch = hardwareMap.touchSensor.get(Keys.touch);
        filterRight.setDirection(Servo.Direction.REVERSE);
        climber.setPosition(Keys.CLIMBER_INITIAL_STATE);
        filterLeft.setPosition(Keys.FILTER_UP);
        filterRight.setPosition(Keys.FILTER_UP);
        score.setPosition(Keys.SCORE_CLOSE);
        swivel.setPosition(Keys.SWIVEL_CENTER);
        waitForStart();
        int count = 1;


        while (count < 8) {
            if (touch.isPressed()) {
                switch (count) {
                    case 1:
                        collector.setPower(.5);
                        filterLeft.setPosition(Keys.FILTER_ACTIVE);
                        filterRight.setPosition(Keys.FILTER_ACTIVE);
                        sleep(3000);
                        collector.setPower(0);
                        break;

                    case 2:
                        liftLeft.setPower(.5);
                        liftRight.setPower(.5);
                        sleep(1500);
                        liftLeft.setPower(0);
                        liftRight.setPower(0);
                        break;

                    case 3:
                        swivel.setPosition(Keys.SWIVEL_LEFT);
                        sleep(1000);
                        break;

                    case 4:
                        score.setPosition(Keys.SCORE_SCORING);
                        sleep(1000);
                        break;

                    case 5:
                        score.setPosition(Keys.SCORE_CLOSE);
                        swivel.setPosition(Keys.SWIVEL_CENTER);
                        liftLeft.setPower(-.5);
                        liftRight.setPower(-.5);
                        sleep(1000);
                        liftLeft.setPower(0);
                        liftRight.setPower(0);
                        break;

                    case 6:
                        climber.setPosition(Keys.CLIMBER_DUMP);
                        sleep(2000);
                        climber.setPosition(Keys.CLIMBER_INITIAL_STATE);
                        sleep(2000);
                        break;

                    case 7:
                        collector.setPower(-.5);
                        filterLeft.setPosition(Keys.FILTER_UP);
                        filterRight.setPosition(Keys.FILTER_UP);
                        sleep(3000);
                        collector.setPower(0);
                        break;
                }
                count++;
            }
            waitOneFullHardwareCycle();
        }


    }
}