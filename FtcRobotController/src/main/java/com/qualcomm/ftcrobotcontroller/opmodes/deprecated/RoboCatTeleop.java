package com.qualcomm.ftcrobotcontroller.opmodes.deprecated;

import com.qualcomm.ftcrobotcontroller.Keys;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

/**
 * Created 11/7/2015.
 */
public class RoboCatTeleop extends OpMode{

    //Declaration of our motors
    DcMotor fr, fl, bl, br;
    DcMotor liftLeft;
    DcMotor liftRight;
    DcMotor collector;
    //Variables to store the power levels that our motors will move at
    double pwrLeft, pwrRight;
    //Declaration of our servos
    Servo filterLeft, filterRight, swivel, score, climber;
    public void init() {

        //Tells the app to find our motors using the naming convention in Keys
        fr = hardwareMap.dcMotor.get(Keys.frontRight);
        fl = hardwareMap.dcMotor.get(Keys.frontLeft);
        br = hardwareMap.dcMotor.get(Keys.backRight);
        bl = hardwareMap.dcMotor.get(Keys.backLeft);

        //This reverses the direction at which the DcMotors on the left side move,
        // allowing us to use the same variables for both the left and right sides of the robot's drive train.
        fl.setDirection(DcMotor.Direction.REVERSE);
        bl.setDirection(DcMotor.Direction.REVERSE);

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

    }

    @Override
    public void loop() {
        //MOVEMENT CONTROL
        //This gets and clips the values from the gamepad
        float gamepad1LeftStickY = Range.clip(gamepad1.left_stick_y, -1, 1);
        float gamepad1RightStickY = Range.clip(gamepad1.right_stick_y, -1, 1);
        //The following two statements clip the gamePad values even further, by multiplying them by .78
        pwrLeft = Range.clip(gamepad1LeftStickY * .78, -1, 1);
        pwrRight = Range.clip(gamepad1RightStickY * .78, -1, 1);

        //TELEMENTRY
        //The telemetry tells us at what power our motors are moving and what position the swivel is in
        telemetry.addData(Keys.telementryLeftKey, pwrLeft);
        telemetry.addData(Keys.telementryRightKey, pwrRight);
        telemetry.addData("swivel",swivel.getPosition());

        //The below method makes our robot move and is included further down the code
        powerSplit(pwrLeft, pwrRight);

        //COLLECTOR_POWER AND FILTER CONTROLS
        //The following code makes the left bumper control the collector moving backwards, if it is moving forwards or not moving at
        // all. This enables us to remove blocakges of blocks from the collection system.
        // The filters also move in order to get a ball out of the system, if one was to go into the collection system.
        if (gamepad2.left_bumper&&collector.getPower()>=0) {
            filterLeft.setPosition(Keys.FILTER_UP);
            filterRight.setPosition(Keys.FILTER_UP);
            freezeAllOtherServosToWhereverTheyAreExceptFor(Keys.filterLeft, Keys.filterRight);
            collector.setPower(-.5);

        }

        //The following code makes the left bumper stop the collector if it is moving backwards, making the backwards movement of
        //the collector a toggle system.
        else if (gamepad2.left_bumper&&collector.getPower()< 0){
            collector.setPower(0);
        }

        //The following code makes the left trigger control the collection of balls. When the trigger is pressed,
        //the filters move to the position that will prevent the taking in of balls, but allow blocks to come in,
        //while spinning the collector to scoop up the balls.
        if (gamepad2.left_trigger > .15) {
            filterLeft.setPosition(Keys.FILTER_ACTIVE);
            filterRight.setPosition(Keys.FILTER_ACTIVE);
            collector.setPower(.5);
        }

        //The following code will stop the collector if the left trigger nor left bumper on the Gamepad 2 is not pressed
        else if (collector.getPower()>0) {
            //TODO filter conditions
            //left trigger is not pressed
            //stop collector
            collector.setPower(0);
        }

        //SCORING SERVO CONTROLS

        //The first gamepad's right trigger will put the scoring servo into the scoring position while preventing any other servo
        //from moving, courtesy of the "freezeAllOtherServosToWhereverTheyAreExceptFor()" method
        if (gamepad1.right_trigger > 0.15 ) {
            score.setPosition(Keys.SCORE_SCORING);
            freezeAllOtherServosToWhereverTheyAreExceptFor(Keys.score);
        }

        //The second gamepad's right bumper will close the scoring servo and prevent the moving of any other servo
        else if (gamepad2.right_bumper) {
            score.setPosition(Keys.SCORE_CLOSE);
            freezeAllOtherServosToWhereverTheyAreExceptFor(Keys.score);
        }

        //Just in case the first gamepad user is to slow and dealing with something else, the second gamepad can also score the blocks
        else if (gamepad2.right_trigger > 0.15 ) {
            //same as gamepad1.right_trigger
            score.setPosition(Keys.SCORE_SCORING);
            freezeAllOtherServosToWhereverTheyAreExceptFor(Keys.score);
        }


        //CLIMBER ARM CONTROLS
        //If the first gamepad's a button is pressed, the climber servo will dump the climbers into the bin while freezing the
        //other servos
        if (gamepad1.a) {
            climber.setPosition(Keys.CLIMBER_DUMP);
            freezeAllOtherServosToWhereverTheyAreExceptFor(Keys.climber);
        }

        //If the first gamepad's y button is pressed, the climber servo will open and return to its original position while freezing
        //the other servos
        if (gamepad1.y) {
            climber.setPosition(Keys.CLIMBER_INITIAL_STATE);
            freezeAllOtherServosToWhereverTheyAreExceptFor(Keys.climber);
        }

        if (gamepad1.b) {
            climber.setPosition(Keys.CLIMBER_HALFWAY);
            freezeAllOtherServosToWhereverTheyAreExceptFor(Keys.climber);
        }

        //LIFT CONTROLS
        //The following code makes the left stick of gamepad 2 control the lift. The deadzones, however, are huge in order to ensure
        //the speedy rise of the lift. The liftMove method is declared later in the code.
        if (Math.abs(gamepad2.left_stick_y) > .35) {
            //*-1 because lift was going wrong
            liftMove(gamepad2.left_stick_y * -1);
        }

        //The following code brakes the lift to ensure it stops immediately
        else if (Math.abs(gamepad2.left_stick_y) <= .35) {
            liftMove(0);
        }

        //SWIVEL Controls

        //The y, x, and b buttons mirror what the swivel positions will look like. This makes the driver able to determine which
        //button puts the swivel servos into the corresponding swivel positions in order to turn the arm into the correct position for
        //scoring
        if (gamepad2.x) {
            swivel.setPosition(Keys.SWIVEL_LEFT);
        }
        else if (gamepad2.y) {
            swivel.setPosition(Keys.SWIVEL_CENTER);
        }
        else if (gamepad2.b) {
            swivel.setPosition(Keys.SWIVEL_RIGHT);

        }
    }




    //The following method takes in an array of the names of the servos which are moving. Each servo has a corresponding number.
    //0 is filter_left, 1 is filter_right, 2 is score, 3 is climber, and 4 is swivel. A string is created using the names of the servos.
    //A loop then checks which servo numbers match the servo names. If they match, they are excluded from being frozen in the next step.
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



    //The following method takes in the motor powers for the left and right motors and makes left right and right left due to our
    //robot's configuration. Also due to the construction of our robot, the new left and right values must be multiplied by -1 to
    //avoid the robot from moving backwards. Then, we log the power being given to each motor so we can confirm they are driving right.
    //Finally, we put the finalized variables as the power that the motors will move in.
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

    //The following method clips the power that the lift motors can recieve and sets them to this clipped power.
    public void liftMove(double power) {
        liftLeft.setPower(Range.clip(power* Keys.MAX_SPEED, -1, 1));
        liftRight.setPower(Range.clip(power* Keys.MAX_SPEED, -1, 1));
    }

    public void stop() {
    }
}


