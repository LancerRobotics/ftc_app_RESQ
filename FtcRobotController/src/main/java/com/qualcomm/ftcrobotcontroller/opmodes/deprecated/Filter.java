package com.qualcomm.ftcrobotcontroller.opmodes.deprecated;

import com.qualcomm.ftcrobotcontroller.Keys;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * Created by Matt on 10/29/2015.
 */
public class Filter extends OpMode {
    Servo filterLeft, filterRight, zipLeft,zipRight,score,climber;
    DcMotor collector;

    @Override
    public void init() {
        filterLeft = hardwareMap.servo.get(Keys.filterLeft);
        filterRight = hardwareMap.servo.get(Keys.filterRight);
        //TODO make sure that servos are servos and motors are motors
        collector = hardwareMap.dcMotor.get(Keys.collector);
        //asuming left filter starts at 0 and the right filter starts at 1
        //let's flip the filter right position so programming wise, they're the same
        filterRight.setDirection(Servo.Direction.REVERSE);
        collector.setDirection(DcMotor.Direction.REVERSE);
        //reverse collector so positive runs forward
        //zipLeft= hardwareMap.servo.get(Keys.zipLeft);
        //zipRight = hardwareMap.servo.get(Keys.zipRight);
        score = hardwareMap.servo.get(Keys.score);
        climber = hardwareMap.servo.get(Keys.climber);
    }

    @Override
    public void loop() {
        //FILTER
        if (gamepad2.left_bumper) {
            if (filterRight.getPosition() != Keys.FILTER_UP) {
                //let's not go full power
                //assume neg value goes backwards
                //assuming the ranges are from 0 to 1
                filterLeft.setPosition(Keys.FILTER_UP);
                filterRight.setPosition(Keys.FILTER_UP);
                freezeAllOtherServosToWhereverTheyAreExceptFor(Keys.filterLeft, Keys.filterRight);
                collector.setPower(.5);
            }
            if(filterRight.getPosition() == Keys.FILTER_UP) {
                collector.setPower(0);
            }
        }

        //COLLECTOR_POWER almost done
        /*
        we could just use this, easier @mattquan
        if (gamepad2.y) {collector.setPower(-.5);}
        else if (gamepad2.a) {collector.setPower(.5);}
        else {collector.setPower(0);}
         */
        /*
        if (gamepad2.y) {
            collector.setPower(-.5);
        }
        else if (gamepad2.y&&collector.getPower()!=-.5)
            collector.setPower(-.5);
        else {
            collector.setPower(0);
        }
        */

        //WTF MOAR FILTER?
        else if (gamepad2.left_trigger > .15) {
            if (filterRight.getPosition() != Keys.FILTER_ACTIVE) {
                //set the filter to min height cuz it's not already!
                filterLeft.setPosition(Keys.FILTER_ACTIVE);
                filterRight.setPosition(Keys.FILTER_ACTIVE);
                //switch collector to forward
                collector.setPower(-.5);
                //assuming positive is forward
                freezeAllOtherServosToWhereverTheyAreExceptFor(Keys.filterLeft, Keys.filterRight);
            }
            //else if it's still true, but filter is alraedy in position, dont do anything!
        } else {
            //left trigger is not pressed
            //stop collector
            collector.setPower(0);
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
        String [] mArray = {Keys.filterLeft, Keys.filterRight, Keys.score, Keys.climber};
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

}
