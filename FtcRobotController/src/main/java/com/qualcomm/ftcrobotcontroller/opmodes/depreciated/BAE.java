package com.qualcomm.ftcrobotcontroller.opmodes.depreciated;

import com.qualcomm.ftcrobotcontroller.Keys;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

/**
 * Created by daniel on 11/7/2015.
 */
public class BAE extends OpMode{

        DcMotor fr, fl, bl, br;
        double pwrLeft, pwrRight;
        //DcMotor arm; //motor to swing hopper mechanism
        DcMotor liftLeft;
        DcMotor liftRight;

        Servo filterLeft, filterRight, swivel, score, climber;
        DcMotor collector;


        public void init() {
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

            filterRight.setDirection(Servo.Direction.REVERSE);

            //set to initial positions
            //TODO climber set it to iNitial state
            climber.setPosition(.5);
            filterLeft.setPosition(Keys.FILTER_UP);
            filterRight.setPosition(Keys.FILTER_UP);
             //score.setPosition(Keys.SCORE_CLOSE);
            //swivel.setDirection(Servo.Direction.REVERSE);
            //swivel.setPosition(Keys.SWIVEL_LEFT);
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
            telemetry.addData("swivel",swivel.getPosition());
            powerSplit(pwrLeft, pwrRight);

            if (gamepad2.left_bumper&&collector.getPower()>=0) {
                //TODO filter conditions
                filterLeft.setPosition(Keys.FILTER_UP);
                filterRight.setPosition(Keys.FILTER_UP);
                freezeAllOtherServosToWhereverTheyAreExceptFor(Keys.filterLeft, Keys.filterRight);
                collector.setPower(-.5);

            }
            else if (gamepad2.left_bumper&&collector.getPower()< 0){
                //TODO filter conditions
                //toggled
                //filter position is already up
                //toggle collector off
                //don't change flilter position
                collector.setPower(0);
            }


            //filter run forward to collect
            if (gamepad2.left_trigger > .15) {
                //TODO filter conditions
                filterLeft.setPosition(Keys.FILTER_ACTIVE);
                filterRight.setPosition(Keys.FILTER_ACTIVE);
                collector.setPower(.5);
                //assuming positive is forward


            } else if (collector.getPower()>0) {
                //TODO filter conditions
                //left trigger is not pressed
                //stop collector
                collector.setPower(0);
            }
            //implied else: collector is running backwards even though left trigger is not pressed, we don't want it to interfere with the collector running backwards



            //SCORING (open/close)
            if (gamepad1.right_trigger > 0.15 ) {
                score.setPosition(Keys.SCORE_SCORING);
                freezeAllOtherServosToWhereverTheyAreExceptFor(Keys.score);
            }
            else if (gamepad2.right_bumper) {
                score.setPosition(Keys.SCORE_CLOSE);
                freezeAllOtherServosToWhereverTheyAreExceptFor(Keys.score);
            }
            else if (gamepad2.right_trigger > 0.15 ) {
                //same as gamepad1.right_trigger
                score.setPosition(Keys.SCORE_SCORING);
                freezeAllOtherServosToWhereverTheyAreExceptFor(Keys.score);
            }

            //CLIMBERS
            if (gamepad1.a) {
                climber.setPosition(Keys.CLIMBER_DUMP);
                freezeAllOtherServosToWhereverTheyAreExceptFor(Keys.climber);
            }
            if (gamepad1.y) {
                climber.setPosition(Keys.CLIMBER_INITIAL_STATE);
                freezeAllOtherServosToWhereverTheyAreExceptFor(Keys.climber);
            }

            //LIFT
            if (Math.abs(gamepad2.left_stick_y) > .35) {
                //*-1 because lift was going wrong
                liftMove(gamepad2.left_stick_y * -1);
            }
            else if (Math.abs(gamepad2.left_stick_y) < .35) {
                liftMove(0);
            }

            //swivel
            double adjustedPosition;
            if (swivel.getPosition()>.4&&swivel.getPosition()<.6) {
                adjustedPosition = Keys.SWIVEL_CENTER;
            } else if (swivel.getPosition()>.7&&swivel.getPosition()<.8) {
                adjustedPosition= Keys.SWIVEL_LEFT;
            } else if (swivel.getPosition()>.2&&swivel.getPosition()<.3) {
                adjustedPosition = Keys.SWIVEL_RIGHT;
            } else {
                adjustedPosition = Keys.SWIVEL_CENTER;
            }
            //TODO change the == to a range, becasue the swivel could be in motion when Olivia wants to change swivel motions. So, withotu ranges
            //TODO the swivel has to be exactly in that position or else it won't do anything
            if (gamepad2.right_stick_x<.3) {
                //less than 0 is negative, negative is that the stick was moved to the left

                if (adjustedPosition== Keys.SWIVEL_CENTER||adjustedPosition== Keys.SWIVEL_RIGHT) {
                    //we are going to multiply everything by 100 because we are doing all position values to two decimal places
                    //conevrt to int because switch case only works with ints
                    //we could use if/else but swtich case makes more sense to me
                    //int pos = (int)(swivel.getPosition()*100);
                    telemetry.addData(Keys.SWIVEL_TELEMETRY,swivel.getPosition());
                    switch ((int)(adjustedPosition*100)) {
                        case (int)(Keys.SWIVEL_CENTER*100):
                            swivel.setPosition(Keys.SWIVEL_LEFT);
                            freezeAllOtherServosToWhereverTheyAreExceptFor(Keys.swivel);
                            break;
                        case (int) (Keys.SWIVEL_RIGHT*100):
                            swivel.setPosition(Keys.SWIVEL_CENTER);
                            freezeAllOtherServosToWhereverTheyAreExceptFor(Keys.swivel);
                            break;
                    }

                }
                //implied else, else the swivel was in position in left, in which case, we don't want it to move because it's in left most position
            }
            else if (gamepad2.right_stick_x>.3) {
                //means that the right stick was moved to the right
                if (adjustedPosition== Keys.SWIVEL_CENTER||adjustedPosition== Keys.SWIVEL_LEFT) {
                    //int pos = (int)(swivel.getPosition()*100);
                    telemetry.addData(Keys.SWIVEL_TELEMETRY,swivel.getPosition());
                    switch ((int)(adjustedPosition*100)) {
                        case (int)(Keys.SWIVEL_CENTER*100):
                            swivel.setPosition(Keys.SWIVEL_RIGHT);
                            break;
                        case (int)(Keys.SWIVEL_LEFT*100):
                            swivel.setPosition(Keys.SWIVEL_CENTER);
                            break;
                    }
                }
            }


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
            liftLeft.setPower(Range.clip(power* Keys.MAX_SPEED, -1, 1));
            liftRight.setPower(Range.clip(power* Keys.MAX_SPEED, -1, 1));
        }

        public void stop() {
        }
    }


