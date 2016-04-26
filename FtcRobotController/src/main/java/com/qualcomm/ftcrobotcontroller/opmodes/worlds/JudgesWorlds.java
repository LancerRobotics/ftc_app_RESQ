package com.qualcomm.ftcrobotcontroller.opmodes.worlds;

import com.kauailabs.navx.ftc.AHRS;
import com.kauailabs.navx.ftc.navXPIDController;
import com.qualcomm.ftcrobotcontroller.Keys;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * Created by spork on 4/21/2016.
 */
public class JudgesWorlds extends LinearOpMode {
    //AnalogInput limitLeft;
    AnalogInput limitRight;
    //Motors
    DcMotor fr, fl, bl, br, liftLeft, liftRight, liftMiddle, collector;
    double pwrLeft, pwrRight;

    //Servos
    Servo swivel, dump, climber, hang, clampRight, clampLeft, guardLeft, guardRight;

    //Climbers
    boolean climberButtonPressed = false;
    double[] climberPositions = {Keys.CLIMBER_INITIAL_STATE, Keys.CLIMBER_DUMP};
    int climberPos;
    int climberToggleReturnArray[] = new int[2];

    //Left Guard;
    boolean guardLeftButtonPressed = false;
    double[] guardLeftPositions = {Keys.LEFT_GUARD_DOWN, Keys.LEFT_GUARD_UP};
    int guardLeftPos;
    int guardLeftToggleReturnArray[] = new int[2];

    //Right Guard;
    boolean guardRightButtonPressed = false;
    double[] guardRightPositions = {Keys.RIGHT_GUARD_DOWN, Keys.RIGHT_GUARD_UP};
    int guardRightPos;
    int guardRightToggleReturnArray[] = new int[2];




    //Lift
    double liftPwr;

    //Pull
    double pullPwr;

    //Joystick 1 --> DRIVER
    float gamepad1LeftStickY, gamepad1RightStickY;
    //Joystick 2 --> GUNNER
    float gamepad2LeftStickY, gamepad2RightStickY;

    boolean calibration_complete;

    @Override
    public void runOpMode() throws InterruptedException {
        fr = hardwareMap.dcMotor.get(Keys.frontRight);
        fl = hardwareMap.dcMotor.get(Keys.frontLeft);
        br = hardwareMap.dcMotor.get(Keys.backRight);
        bl = hardwareMap.dcMotor.get(Keys.backLeft);

        collector = hardwareMap.dcMotor.get(Keys.collector);
        liftLeft = hardwareMap.dcMotor.get(Keys.liftLeft);
        liftMiddle=hardwareMap.dcMotor.get(Keys.liftMiddle);
        liftRight = hardwareMap.dcMotor.get(Keys.liftRight);
        climber = hardwareMap.servo.get(Keys.climber);
        swivel = hardwareMap.servo.get(Keys.swivel);
        hang = hardwareMap.servo.get(Keys.hang);
        clampLeft = hardwareMap.servo.get(Keys.clampLeft);
        clampRight = hardwareMap.servo.get(Keys.clampRight);
        dump = hardwareMap.servo.get(Keys.dump);
        guardLeft = hardwareMap.servo.get(Keys.guardLeft);
        guardRight = hardwareMap.servo.get(Keys.guardRight);
        fr.setDirection(DcMotor.Direction.REVERSE);
        br.setDirection(DcMotor.Direction.REVERSE);
        liftLeft.setDirection(DcMotor.Direction.REVERSE);
        liftMiddle.setDirection(DcMotor.Direction.REVERSE);

        climberPos = 1;
        guardLeftPos = 1;
        guardRightPos = 1;

        climber.setPosition(climberPositions[0]);
        dump.setPosition(Keys.DUMP_INIT);
        swivel.setPosition(Keys.SWIVEL_CENTER);
        hang.setPosition(Keys.HANG_INIT);
        clampLeft.setPosition(Keys.CLAMP_LEFT_INIT);
        clampRight.setPosition(Keys.CLAMP_RIGHT_INIT);
        guardLeft.setPosition(guardLeftPositions[0]);
        guardRight.setPosition(guardRightPositions[0]);
        limitRight = hardwareMap.analogInput.get(Keys.LIMIT_RIGHT);
        telemetry.addData("Start Judges Code", "YES");
        waitForStart();
        int count = 2;
        while (count < 8) {
            if (gamepad1.a) {
                switch (count) {
                    case 2:
                        telemetry.addData("Step ONE", "LIFT");
                        liftLeft.setPower(-.5);
                        liftRight.setPower(-.5);
                        liftMiddle.setPower(-.5);
                        sleep(1000);
                        liftLeft.setPower(0);
                        liftRight.setPower(0);
                        liftMiddle.setPower(0);
                        dump.setPosition(Keys.DUMP_DOWN);
                        telemetry.addData("Done?", "DONE");
                        break;
                    case 3:
                        telemetry.addData("Step TWO", "COLLECTOR");
                        collector.setPower(Keys.COLLECTOR_POWER);
                        sleep(1000);
                        collector.setPower(0);
                        telemetry.addData("Done?", "DONE");
                        break;
                    case 4:
                        telemetry.addData("Step THREE", "CLAMP");
                        clampLeft.setPosition(Keys.CLAMP_LEFT_DOWN);
                        clampRight.setPosition(Keys.CLAMP_RIGHT_DOWN);
                        sleep(3000);
                        clampRight.setPosition(Keys.CLAMP_RIGHT_INIT);
                        clampLeft.setPosition(Keys.CLAMP_LEFT_INIT);
                        telemetry.addData("Done?", "DONE");
                        break;
                    case 5:
                        telemetry.addData("Step FOUR", "HANG UP");
                        hang.setPosition(Keys.HANG_NOW);
                        while(gamepad1.a) {
                            sleep(1);
                        }
                        telemetry.addData("Done?", "DONE");
                        break;
                    case 6:
                        telemetry.addData("Step FIVE", "CLIMBER DUMP");
                        climber.setPosition(Keys.CLIMBER_DUMP);
                        while(gamepad1.a) {
                            sleep(1);
                        }
                        telemetry.addData("Done?", "DONE");
                        break;
                    case 7:
                        telemetry.addData("Step SIX", "RESTORE ALL");
                        hang.setPosition(Keys.HANG_INIT);
                        dump.setPosition(Keys.DUMP_INIT);
                        liftRight.setPower(.5);
                        liftLeft.setPower(.5);
                        sleep(800);
                        liftLeft.setPower(0);
                        liftRight.setPower(0);
                        climber.setPosition(Keys.CLIMBER_INITIAL_STATE);
                        telemetry.addData("Done?", "DONE/END OF JUDGES CODE");
                        break;
                }
                count++;
            }
            waitOneFullHardwareCycle();
        }
    }
}

