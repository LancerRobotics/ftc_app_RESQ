package com.qualcomm.ftcrobotcontroller.opmodes.depreciated;

/**
 * Created by AJ on 10/29/2015.
 */

import com.qualcomm.ftcrobotcontroller.Keys;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

public class PostNNTeleOp extends OpMode {
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

    public PostNNTeleOp() {

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
       // zipRight = hardwareMap.servo.get(Keys.zipRight);
        climber = hardwareMap.servo.get(Keys.climber);
        filterLeft = hardwareMap.servo.get(Keys.filterLeft);
        filterRight = hardwareMap.servo.get(Keys.filterRight);
        collector = hardwareMap.dcMotor.get(Keys.collector);

        filterRight.setDirection(Servo.Direction.REVERSE);
        collector.setDirection(DcMotor.Direction.REVERSE);

        //set to initial positions
        //TODO zipleft has no initial state because it's not working
        zipRight.setPosition(Keys.ZIP_RIGHT_INITIAL_STATE);
        climber.setPosition(Keys.CLIMBER_INITIAL_STATE);
        filterLeft.setPosition(Keys.FILTER_CLOSE);
        filterRight.setPosition(Keys.FILTER_CLOSE);
        zipLeft.setPosition(Keys.ZIP_LEFT_INITIAL_STATE);
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

        //collector going backwards on left bumper press
        if (gamepad2.left_bumper&&filterRight.getPosition()!=Keys.FILTER_UP) {

                filterLeft.setPosition(Keys.FILTER_UP);
                filterRight.setPosition(Keys.FILTER_UP);
                freezeAllOtherServosToWhereverTheyAreExceptFor(Keys.filterLeft, Keys.filterRight);
                collector.setPower(-.5);

        }
        else if (gamepad2.left_bumper&&filterRight.getPosition()==Keys.FILTER_UP) {
            //toggled
            //filter position is already up
            //toggle collector off
            //don't change flilter position
            collector.setPower(0);
        }


        //filter run forward to collect
        if (gamepad2.left_trigger > .15) {
            if (filterRight.getPosition() != Keys.FILTER_ACTIVE) {
                //set the filter to min height cuz it's not already!
                filterLeft.setPosition(Keys.FILTER_ACTIVE);
                filterRight.setPosition(Keys.FILTER_ACTIVE);
                //switch collector to forward
                collector.setPower(.5);
                //assuming positive is forward
                freezeAllOtherServosToWhereverTheyAreExceptFor(Keys.filterLeft,Keys.filterRight);
            }

        } else if (!(collector.getPower()<0&&filterLeft.getPosition()==Keys.FILTER_UP)) {
            //left trigger is not pressed
            //stop collector
            collector.setPower(0);
        }
        //implied else: collector is running backwards even though left trigger is not pressed, we don't want it to interfere with the collector running backwards


        //SCORING (open/close, no arm)
        if (gamepad1.right_trigger > 0.15 ) {
            score.setPosition(SCORE_SCORING);
            freezeAllOtherServosToWhereverTheyAreExceptFor(Keys.score);
        }
        else if (gamepad2.right_bumper) {
            score.setPosition(SCORE_CLOSE);
            freezeAllOtherServosToWhereverTheyAreExceptFor(Keys.score);
        }
        else if (gamepad2.right_trigger > 0.15 ) {
            //same as gamepad1.right_trigger
            score.setPosition(SCORE_SCORING);
            freezeAllOtherServosToWhereverTheyAreExceptFor(Keys.score);
        }

        //CLIMBERS done
        if (gamepad1.a) {
            climber.setPosition(Keys.CLIMBER_DUMP);
            freezeAllOtherServosToWhereverTheyAreExceptFor(Keys.climber);
        }
        if (gamepad1.y) {
            climber.setPosition(Keys.CLIMBER_INITIAL_STATE);
            freezeAllOtherServosToWhereverTheyAreExceptFor(Keys.climber);
        }

        //LIFT done
        if (Math.abs(gamepad2.right_stick_y) > .15) {
            //*-1 because lift was going wrong
            liftMove(gamepad2.right_stick_y * -1);
        }

        //ARM
        if (Math.abs(gamepad2.left_stick_y) > .2) {
            arm.setPower(Range.clip(gamepad2.left_stick_y * .1, -1, 1));
        }
//zipline toggles
        if(gamepad2.x) {
            if(zipLeft.getPosition() != Keys.ZIP_LEFT_DOWN) {
                zipLeft.setPosition(Keys.ZIP_LEFT_DOWN);
               // freezeAllOtherServosToWhereverTheyAreExceptFor(Keys.zipLeft);
            }
            else {
                zipLeft.setPosition(Keys.ZIP_LEFT_INITIAL_STATE);
              //  freezeAllOtherServosToWhereverTheyAreExceptFor(Keys.zipLeft);
            }
        }
        if(gamepad2.b) {
            if(zipRight.getPosition() != Keys.ZIP_RIGHT_DOWN) {
                zipRight.setPosition(Keys.ZIP_RIGHT_DOWN);
               // freezeAllOtherServosToWhereverTheyAreExceptFor(Keys.zipRight);
            }
            else {
                //it is already down
                zipRight.setPosition(Keys.ZIP_RIGHT_INITIAL_STATE);
              //  freezeAllOtherServosToWhereverTheyAreExceptFor(Keys.zipRight);
            }
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
        liftLeft.setPower(Range.clip(power*Keys.MAX_SPEED, -1, 1));
        liftRight.setPower(Range.clip(power*Keys.MAX_SPEED, -1, 1));
    }

    public void stop() {
    }
}
