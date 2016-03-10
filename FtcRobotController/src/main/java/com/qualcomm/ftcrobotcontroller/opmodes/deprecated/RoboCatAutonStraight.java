package com.qualcomm.ftcrobotcontroller.opmodes.deprecated;

import com.qualcomm.ftcrobotcontroller.Keys;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * Created by daniel on 11/14/2015.
 */
public class RoboCatAutonStraight extends LinearOpMode {
    DcMotor fr, fl, bl, br;

    DcMotor collector;
    Servo filterLeft, filterRight, swivel, score, climber;

    @Override
    public void runOpMode() throws InterruptedException {
        fr = hardwareMap.dcMotor.get(Keys.frontRight);
        fl = hardwareMap.dcMotor.get(Keys.frontLeft);
        br = hardwareMap.dcMotor.get(Keys.backRight);
        bl = hardwareMap.dcMotor.get(Keys.backLeft);
        fl.setDirection(DcMotor.Direction.REVERSE);
        bl.setDirection(DcMotor.Direction.REVERSE);
        filterLeft = hardwareMap.servo.get(Keys.filterLeft);
        filterRight = hardwareMap.servo.get(Keys.filterRight);

        score = hardwareMap.servo.get(Keys.score);

        climber = hardwareMap.servo.get(Keys.climber);
        filterLeft = hardwareMap.servo.get(Keys.filterLeft);
        filterRight = hardwareMap.servo.get(Keys.filterRight);
        swivel = hardwareMap.servo.get(Keys.swivel);
        collector = hardwareMap.dcMotor.get(Keys.collector);

        filterRight.setDirection(Servo.Direction.REVERSE);
        swivel.setDirection(Servo.Direction.REVERSE);
        climber.setPosition(Keys.CLIMBER_INITIAL_STATE); //This initializes
        filterLeft.setPosition(Keys.FILTER_UP);
        filterRight.setPosition(Keys.FILTER_UP);
        score.setPosition(Keys.SCORE_CLOSE);collector.setPower(0);
        swivel.setPosition(Keys.SWIVEL_CENTER);

        waitForStart();
        encodedForward(100);
    }
    public  void freezeAllOtherServosToWhereverTheyAreExceptFor (String...arrayOfServosToExclude) {
        //just use config names
        //check if
        // 0 = "filter_left";
        //1= "filter_right";
        //2= "score";
        //3="climber";
        //4 = "swivel"
        String [] mArray = {Keys.filterLeft, Keys.filterRight, Keys.score, Keys.climber, Keys.swivel};
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
            climber.setPosition(climber.getPosition());
        }
        if (!mArray[4].equals("exclude")) {
            swivel.setPosition(swivel.getPosition());
        }

    }
    public void encodedForward(double dist) {
        //inches
        //at speed .5, it goes over four inches
        dist = dist - 4;
        double rotations = dist / (6 * Math.PI);
        double addTheseTicks = rotations * 1120;
        int positionBeforeMovement = fl.getCurrentPosition();
        while (fl.getCurrentPosition() < positionBeforeMovement + addTheseTicks) {
            telemetry.addData("front left encoder: ", fl.getCurrentPosition());
            telemetry.addData("ticksFor", addTheseTicks);
            forward();
        }
        rest();
    }

    public void forward() {
        fr.setPower(.5);
        fl.setPower(.5);
        bl.setPower(.5);
        br.setPower(.5);
        collector.setPower(-.5);

    }

    public void rest() {
        fr.setPower(0);
        fl.setPower(0);
        bl.setPower(0);
        br.setPower(0);
        collector.setPower(0);

    }
}
