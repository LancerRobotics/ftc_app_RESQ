package com.qualcomm.ftcrobotcontroller.opmodes.depreciated;

import com.qualcomm.ftcrobotcontroller.Keys;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

/**
 * Created by AJ on 10/28/2015.
 */
public class Score extends OpMode {

    Servo score; //door to release blocks

    Servo filterLeft,filterRight,zipLeft,zipRight,climber;
    DcMotor arm; //motor to swing hopper mechanism
    DcMotor liftLeft;
    DcMotor liftRight;
    public final double SCORE_CLOSE = .1;
    public final double SCORE_SCORING = .6;
    public final double CLIMBER_DUMP = .1;
    public final double CLIMBER_NO_DUMP = 1;

    public Score() {

    }

    public void init() {
        score = hardwareMap.servo.get(Keys.score);
        //arm = hardwareMap.dcMotor.get(Keys.arm);
        liftLeft = hardwareMap.dcMotor.get(Keys.liftLeft);
        liftRight = hardwareMap.dcMotor.get(Keys.liftRight);
        liftRight.setDirection(DcMotor.Direction.REVERSE);
        //zipLeft= hardwareMap.servo.get(Keys.zipLeft);
        //zipRight = hardwareMap.servo.get(Keys.zipRight);
        climber = hardwareMap.servo.get(Keys.climber);
        filterLeft = hardwareMap.servo.get(Keys.filterLeft);
        filterRight = hardwareMap.servo.get(Keys.filterRight);
    }

    public void loop() {

        if(gamepad1.right_trigger > 0.3 && score.getPosition() != SCORE_SCORING) {
            score.setPosition(SCORE_SCORING);
            filterLeft.setPosition(Keys.FILTER_CLOSE);
            filterRight.setPosition(Keys.FILTER_CLOSE);
            zipRight.setPosition(Keys.ZIP_RIGHT_INITIAL_STATE);
            climber.setPosition(Keys.CLIMBER_INITIAL_STATE);



        }
        if (gamepad1.a) {
            climber.setPosition(CLIMBER_NO_DUMP);
        }
        if(gamepad1.y) {
            climber.setPosition(CLIMBER_DUMP);
        }

        if(gamepad2.right_trigger > 0.3 && score.getPosition() != SCORE_CLOSE) {
            score.setPosition(SCORE_CLOSE);
            filterLeft.setPosition(Keys.FILTER_CLOSE);
            filterRight.setPosition(Keys.FILTER_CLOSE);
            zipRight.setPosition(Keys.ZIP_RIGHT_INITIAL_STATE);
            climber.setPosition(Keys.CLIMBER_INITIAL_STATE);
        }

        if(Math.abs(gamepad2.right_stick_y) > .15) {
            //Deadzone, check to see if this is needed
            //*-1 becasue lift was going opposite direction
            liftMove(gamepad2.right_stick_y*-1);
        }
        else if(Math.abs(gamepad2.right_stick_y) <= .15) {
            liftMove(0);
        }

        if(gamepad2.right_bumper && score.getPosition() != SCORE_SCORING) {
        score.setPosition(SCORE_SCORING);
        filterLeft.setPosition(Keys.FILTER_CLOSE);
        filterRight.setPosition(Keys.FILTER_CLOSE);
        zipRight.setPosition(Keys.ZIP_RIGHT_INITIAL_STATE);
        climber.setPosition(Keys.CLIMBER_INITIAL_STATE);
        }
        if(Math.abs(gamepad2.left_stick_y) > .15) {
            arm.setPower(Range.clip(gamepad2.left_stick_y*.25,-1,1));
            //Discuss with AJ - When Olivia moves backwards, the motor needs to move backwards too
            //so arm should be set to negative values for backwards, and pos. values for fowards
            //just use left stick values but clip the ranges
            //Math.abs(.15) is just .15
        }


    }

    public void liftMove(double power) {
        liftLeft.setPower(Range.clip(power, -1, 1));
        liftRight.setPower(Range.clip(power, -1, 1));
    }
}
