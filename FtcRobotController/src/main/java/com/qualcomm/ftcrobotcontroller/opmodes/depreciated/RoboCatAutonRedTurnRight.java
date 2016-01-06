package com.qualcomm.ftcrobotcontroller.opmodes.depreciated;

import com.qualcomm.ftcrobotcontroller.Keys;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
//import com.qualcomm.robotcore.hardware.Servo;


public class RoboCatAutonRedTurnRight extends LinearOpMode {
    DcMotor fr, fl, bl, br;
    double pwrLeft, pwrRight;
    DcMotor arm; //motor to swing hopper mechanism
    DcMotor liftLeft;
    DcMotor liftRight;
    //Servo filterLeft, filterRight, zipLeft, zipRight, score, climber;
    DcMotor collector;
    double coll;
    public static int[] encoderValues = new int[4];
    ElapsedTime timer = new ElapsedTime();
    Servo filterLeft, filterRight, swivel, score, climber;



    @Override
    public void runOpMode() throws InterruptedException {
        fr = hardwareMap.dcMotor.get(Keys.frontRight);
        fl = hardwareMap.dcMotor.get(Keys.frontLeft);
        br = hardwareMap.dcMotor.get(Keys.backRight);
        bl = hardwareMap.dcMotor.get(Keys.backLeft);
        fl.setDirection(DcMotor.Direction.REVERSE);
        bl.setDirection(DcMotor.Direction.REVERSE);
        //set all pos. to 0 at beginning
        fl.setTargetPosition(0);
        fr.setTargetPosition(100);
        bl.setTargetPosition(0);
        br.setTargetPosition(0);
        //This tells the app where to find the servos
        score = hardwareMap.servo.get(Keys.score);
        liftLeft = hardwareMap.dcMotor.get(Keys.liftLeft);
        liftRight = hardwareMap.dcMotor.get(Keys.liftRight);
        liftRight.setDirection(DcMotor.Direction.REVERSE);
        climber = hardwareMap.servo.get(Keys.climber);
        filterLeft = hardwareMap.servo.get(Keys.filterLeft);
        filterRight = hardwareMap.servo.get(Keys.filterRight);
        swivel = hardwareMap.servo.get(Keys.swivel);
        collector = hardwareMap.dcMotor.get(Keys.collector);

        //This reverses the direction of the servos, allowing us to move filterLeft
        // and filterRight with the same variable. This also allows for the swivel servos to go in the correct direction
        filterRight.setDirection(Servo.Direction.REVERSE);
        swivel.setDirection(Servo.Direction.REVERSE);
        climber.setPosition(Keys.CLIMBER_INITIAL_STATE); //This initializes
        filterLeft.setPosition(Keys.FILTER_UP);
        filterRight.setPosition(Keys.FILTER_UP);
        score.setPosition(Keys.SCORE_CLOSE);
        swivel.setPosition(Keys.SWIVEL_CENTER);

        //telemetry for timer
        telemetry.addData("timer: ", timer.time());


        waitForStart();
        timer.reset();
        encoderTelementery();
        telemetry.addData("timer: ", timer.time());
        telemetry.addData("place", "not forward");
        encodedForward(36);
        encodeRightTurn(30);
        encodedForward(36);
        encodeRightTurn(15);
        encodedForward(20);
        climber.setPosition(Keys.CLIMBER_HALFWAY);
        sleep(1000);
        climber.setPosition(Keys.CLIMBER_DUMP);


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

    //Moves the robot a certain amount of distance *in inches or degrees*, encoded movement
    public void encodedForward(double dist) {
        //inches
        //at speed .5, it goes over four inches
        dist = dist-4;
        double rotations = dist/(6*Math.PI);
        double addTheseTicks = rotations*1120;
        int positionBeforeMovement = fl.getCurrentPosition();
        while (fl.getCurrentPosition()<positionBeforeMovement+addTheseTicks) {
            telemetry.addData("front left encoder: ", fl.getCurrentPosition());
            telemetry.addData("ticksFor",addTheseTicks);
            forward();
        }
        rest();
    }
    public void encodedBackward(int dist) {
        //inches
        //at speed .5, it goes over four inches
        dist = dist-4;
        double rotations = dist/(6*Math.PI);
        double addTheseTicks = rotations*1120;
        int positionBeforeMovement = fl.getCurrentPosition();
        while (fl.getCurrentPosition()<positionBeforeMovement+addTheseTicks) {
            telemetry.addData("front left encoder: ", fl.getCurrentPosition());
            telemetry.addData("ticks",addTheseTicks);
            backward();
        }
        rest();
    }

    //Timed turn, when calling --> amount of time to turn and whether it is going right or not
    //TODO create a degree parameter then convert to time in the method, + degrees will be right, - degrees will be left
    public void encodeLeftTurn(double dist) {
        //inches
        //at speed .5, it goes over four inches
        dist = dist-4;
        double rotations = dist/(6*Math.PI);
        double addTheseTicks = rotations*1120;
        int positionBeforeMovement = fl.getCurrentPosition();
        while (fl.getCurrentPosition()<positionBeforeMovement+addTheseTicks) {
            telemetry.addData("front left encoder: ", fl.getCurrentPosition());
            telemetry.addData("ticksTurn",addTheseTicks);
            left();
        }
        rest();
    }

    public void encodeRightTurn(double dist) {
        //inches
        //at speed .5, it goes over four inches
        dist = dist-4;
        double rotations = dist/(6*Math.PI);
        double addTheseTicks = rotations*1120;
        int positionBeforeMovement = fr.getCurrentPosition();
        while (fr.getCurrentPosition()<positionBeforeMovement+addTheseTicks) {
            telemetry.addData("front left encoder: ", fr.getCurrentPosition());
            telemetry.addData("ticks Turn",addTheseTicks);
            right();
        }
        rest();
    }

    public void timedTurn (double time, boolean right) {
        double initialTime =timer.time();
        if (right) {
            while(timer.time()<= initialTime+time) {
                telemetry.addData("timer: ", timer.time());
                telemetry.addData("needsToBeAt", initialTime + time);
                right();

            }


        }
        else {
            while(timer.time()< initialTime+time) {

                telemetry.addData("place","not right");
                telemetry.addData("timer: ", timer.time());
                telemetry.addData("needsToBeAt", initialTime + time);
                left();

            }

            telemetry.addData("place","out of while");


        }

        telemetry.addData("place", "break b4");

        telemetry.addData("place", "before rest");
        rest();

        telemetry.addData("place","done turn");
    }

    //Gets the positions of all the encoded motors
    public int updateEncoders(int motor) {
        encoderValues[0] = fl.getCurrentPosition();
        encoderValues[1] = bl.getCurrentPosition();
        encoderValues[2] = fr.getCurrentPosition();
        encoderValues[3] = bl.getCurrentPosition();
        return encoderValues[motor];
    }
    public void encoderTelementery() {
        telemetry.addData("front left encoder: ", updateEncoders(0));
        telemetry.addData("back left encoder: ", updateEncoders(1));
        telemetry.addData("front right encoder: ", updateEncoders(2));
        telemetry.addData("back right encoder: ", updateEncoders(3));
    }

    //Simple movement methods to be used
    public void forward() {
        fr.setPower(.5);
        fl.setPower(.5);
        bl.setPower(.5);
        br.setPower(.5);
        collector.setPower(-.5);

    }
    public void backward() {
        fr.setPower(-.5);
        fl.setPower(-.5);
        bl.setPower(-.5);
        br.setPower(-.5);
        collector.setPower(-.5);

    }
    public void left() {
        fr.setPower(-.1);
        fl.setPower(.7);
        bl.setPower(.7);
        br.setPower(-.1);collector.setPower(-.5);

    }
    public void right() {
        fr.setPower(.7);
        fl.setPower(-.1);
        bl.setPower(-.1);
        collector.setPower(-.5);
        br.setPower(.7);

    }
    public void move(double frp, double flp, double brp, double blp) {
        fr.setPower(frp);
        fl.setPower(flp);
        br.setPower(brp);
        bl.setPower(blp);
    }

    //Sets the motors to 0 after they are braked, rest position
    public void rest() {
        fr.setPower(0);
        fl.setPower(0);
        bl.setPower(0);
        br.setPower(0);

    }



}


