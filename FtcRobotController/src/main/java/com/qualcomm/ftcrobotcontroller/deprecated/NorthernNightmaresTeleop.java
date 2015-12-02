package com.qualcomm.ftcrobotcontroller.opmodes;

/**
 * Created by AJ on 10/29/2015.
 */

import com.qualcomm.ftcrobotcontroller.Keys;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

import java.util.ArrayList;

public class NorthernNightmaresTeleop extends OpMode {
    DcMotor fr, fl, bl, br;
    double pwrLeft, pwrRight;
    DcMotor arm; //motor to swing hopper mechanism
    DcMotor liftLeft;
    DcMotor liftRight;
    public final double SCORE_CLOSE = .1;
    public final double SCORE_SCORING = .6;
    Servo filterLeft, filterRight, zipLeft, zipRight, score, climber;
    DcMotor collector;
    double coll;

    public NorthernNightmaresTeleop() {

    }

    public void init() {
        fr = hardwareMap.dcMotor.get(Keys.frontRight);
        fl = hardwareMap.dcMotor.get(Keys.frontLeft);
        br = hardwareMap.dcMotor.get(Keys.backRight);
        bl = hardwareMap.dcMotor.get(Keys.backLeft);
        fr.setDirection(DcMotor.Direction.REVERSE);
        br.setDirection(DcMotor.Direction.REVERSE);
        score = hardwareMap.servo.get(Keys.score);
        //arm = hardwareMap.dcMotor.get(Keys.arm);
        liftLeft = hardwareMap.dcMotor.get(Keys.liftLeft);
        liftRight = hardwareMap.dcMotor.get(Keys.liftRight);
        liftRight.setDirection(DcMotor.Direction.REVERSE);
       // zipLeft = hardwareMap.servo.get(Keys.zipLeft);
        //zipRight = hardwareMap.servo.get(Keys.zipRight);
        climber = hardwareMap.servo.get(Keys.climber);
        filterLeft = hardwareMap.servo.get(Keys.filterLeft);
        filterRight = hardwareMap.servo.get(Keys.filterRight);
        collector = hardwareMap.dcMotor.get(Keys.collector);

        filterRight.setDirection(Servo.Direction.REVERSE);
        collector.setDirection(DcMotor.Direction.REVERSE);

        //set to initial psotions
        //TODO zipleft has no initial state because it's not working
        zipRight.setPosition(Keys.ZIP_RIGHT_INITIAL_STATE);
        climber.setPosition(Keys.CLIMBER_INITIAL_STATE);
        filterLeft.setPosition(Keys.FILTER_CLOSE);
        filterRight.setPosition(Keys.FILTER_CLOSE);
    }

    @Override
    public void loop() {
        //MOVEMENT CONTROL
        float gamepad1LeftStickY = Range.clip(gamepad1.left_stick_y, -1, 1);
        float gamepad1RightStickY = Range.clip(gamepad1.right_stick_y, -1, 1);
        //by multiplying by .78, range turns to [-.78,.78]
        pwrLeft = Range.clip(gamepad1LeftStickY * .78, -1, 1);
        pwrRight = Range.clip(gamepad1RightStickY * .78, -1, 1);
        telemetry.addData(Keys.telementryLeftKey, pwrLeft);
        telemetry.addData(Keys.telementryRightKey, pwrRight);
        powerSplit(pwrLeft, pwrRight);
        if (gamepad2.left_bumper) {
            if (filterRight.getPosition() != Keys.FILTER_UP) {
                //let's not go full power
                //assume neg value goes backwards
                //assuming the ranges are from 0 to 1
                filterLeft.setPosition(Keys.FILTER_UP);
                filterRight.setPosition(Keys.FILTER_UP);
                freezeAllOtherServosToWhereverTheyAreExceptFor(Keys.filterLeft,Keys.filterRight);
            } else {


            }
            //if presed down and already everything is set, even better
            //but the moment it's released and not presesd (below else)
        } else {
            //left bumper isn't pressed down
            //so stop everything
            //i think that's what she wants TODO

            //leave the filters there because you dont need them to change
        }
        telemetry.addData("y",gamepad2.y);
        telemetry.addData("collectorPower",collector.getPower());
        if (gamepad2.y) {
            collector.setPower(-.5);
        }
        else {
            collector.setPower(0);
        }

        if (gamepad2.y&&collector.getPower()!=-.5)
            collector.setPower(-.5);
        else
            collector.setPower(0);

        if (gamepad2.left_trigger > .1) {
            if (filterRight.getPosition() != Keys.FILTER_ACTIVE) {
                //set the filter to min height cuz it's not already!
                filterLeft.setPosition(Keys.FILTER_ACTIVE);
                filterRight.setPosition(Keys.FILTER_ACTIVE);
                //switch collector to forward
                collector.setPower(.5);
                //assuming positive is forward
                freezeAllOtherServosToWhereverTheyAreExceptFor(Keys.filterLeft,Keys.filterRight);
            }
            //else if it's still true, but filter is alraedy in position, dont do anything!
        } else {
            //left trigger is not pressed
            //stop collector
            collector.setPower(0);
        }
        if (gamepad1.right_trigger > 0.3 && score.getPosition() != SCORE_SCORING) {
            score.setPosition(SCORE_SCORING);
            freezeAllOtherServosToWhereverTheyAreExceptFor(Keys.score);
        }
        if (gamepad1.a) {
            climber.setPosition(Keys.CLIMBER_DUMP);
            // freeze other
        }
        if (gamepad1.y) {
            climber.setPosition(Keys.CLIMBER_INITIAL_STATE);
            // freeze other
        }

        if (gamepad2.right_trigger > 0.3 && score.getPosition() != SCORE_CLOSE) {
            score.setPosition(SCORE_CLOSE);
            freezeAllOtherServosToWhereverTheyAreExceptFor(Keys.score);
        }

        if (Math.abs(gamepad2.right_stick_y) > .15) {
            liftMove(gamepad2.right_stick_y * -1);
        } else if (Math.abs(gamepad2.right_stick_y) <= .15) {
            //TODO pretty sure this is not necessary
            liftMove(0);
        }

        if (gamepad2.right_bumper && score.getPosition() != SCORE_SCORING) {
            score.setPosition(SCORE_SCORING);
            freezeAllOtherServosToWhereverTheyAreExceptFor(Keys.score);
        }

        if (Math.abs(gamepad2.left_stick_y) > .15) {
            arm.setPower(Range.clip(gamepad2.left_stick_y * .25, -1, 1));

        }

    }

    public  void freezeAllOtherServosToWhereverTheyAreExceptFor (String...arrayOfServosToExclude) {
        //just use config names
        //check if
      // 0 = "filter_left";
       //1= "filter_right";
      //2= "score";
       //3= "zip_left";
      //4 = "zip_right";
       //5= "climber";
        String [] mArray = {Keys.filterLeft,Keys.filterRight,Keys.score,Keys.climber};
        for (int j = 0; j<mArray.length;j++) {
            for (int i = 0; i < arrayOfServosToExclude.length; i++) {
                if (arrayOfServosToExclude[i].equals(mArray[j])) {
                    mArray[j] = "exclude";
                }
            }
        }
        if (!mArray[0].equals("exclude")) {
            filterLeft.setPosition(filterLeft.getPosition());
        }
        if (!mArray[1].equals("exclude")) {
            filterRight.setPosition(filterRight.getPosition());
        }
        if (!mArray[2].equals("exclude")) {
            score.setPosition(score.getPosition());
        }
        if (!mArray[3].equals("exclude")) {
            zipLeft.setPosition(zipLeft.getPosition());
        }
        if (!mArray[4].equals("exclude")) {
            zipRight.setPosition(zipRight.getPosition());
        }
        if (!mArray[5].equals("exclude")) {
            climber.setPosition(climber.getPosition());
        }


    }

    public void powerSplit(double left, double right) {
        //swap left and right, because they're backwards in our configuration
        double tempLeft = left;
        left = right;
        right = tempLeft;
        //switch direction
        left = left * -1;
        right = right * -1;
        telemetry.addData(Keys.telementryFrontLeftPowerKey, left);
        telemetry.addData(Keys.telementryFrontRightPowerKey, right);
        telemetry.addData(Keys.telementryBackLeftPowerKey, left);
        telemetry.addData(Keys.telementryBackRightPowerKey, right);
        fl.setPower(left);
        fr.setPower(right);
        bl.setPower(left);
        br.setPower(right);
    }

    public void liftMove(double power) {
        liftLeft.setPower(Range.clip(power, -1, 1));
        liftRight.setPower(Range.clip(power, -1, 1));
    }

    public void stop() {
    }
}
