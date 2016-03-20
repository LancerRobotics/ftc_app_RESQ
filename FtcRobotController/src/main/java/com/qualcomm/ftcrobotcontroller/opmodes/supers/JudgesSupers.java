package com.qualcomm.ftcrobotcontroller.opmodes.supers;

import com.qualcomm.ftcrobotcontroller.Keys;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * Created by mattquan on 2/23/2016.
 */
public class JudgesSupers extends LinearOpMode {
    DcMotor fr, fl, bl, br, liftLeft, liftRight, collector, winch;
    Servo swivel, dump, climber, hang, clampRight, clampLeft, triggerRight, triggerLeft;
    public void runOpMode() throws InterruptedException {
        fr = hardwareMap.dcMotor.get(Keys.frontRight);
        fl = hardwareMap.dcMotor.get(Keys.frontLeft);
        br = hardwareMap.dcMotor.get(Keys.backRight);
        bl = hardwareMap.dcMotor.get(Keys.backLeft);

        collector = hardwareMap.dcMotor.get(Keys.collector);
        liftLeft = hardwareMap.dcMotor.get(Keys.liftLeft);
        liftRight = hardwareMap.dcMotor.get(Keys.liftRight);
        winch = hardwareMap.dcMotor.get(Keys.winch);

        climber = hardwareMap.servo.get(Keys.climber);
        swivel = hardwareMap.servo.get(Keys.swivel);
        hang = hardwareMap.servo.get(Keys.hang);
        clampLeft = hardwareMap.servo.get(Keys.clampLeft);
        clampRight = hardwareMap.servo.get(Keys.clampRight);
        dump = hardwareMap.servo.get(Keys.dump);
        triggerLeft = hardwareMap.servo.get(Keys.triggerLeft);
        triggerRight = hardwareMap.servo.get(Keys.triggerRight);

        fr.setDirection(DcMotor.Direction.REVERSE);
        br.setDirection(DcMotor.Direction.REVERSE);
        liftLeft.setDirection(DcMotor.Direction.REVERSE);

        climber.setPosition(Keys.CLIMBER_INITIAL_STATE);
        dump.setPosition(Keys.DUMP_INIT);
        swivel.setPosition(Keys.SWIVEL_CENTER);
        hang.setPosition(Keys.HANG_INIT);
        clampLeft.setPosition(Keys.CLAMP_LEFT_INIT);
        clampRight.setPosition(Keys.CLAMP_RIGHT_INIT);
        triggerLeft.setPosition(Keys.LEFT_TRIGGER_INIT);
        triggerRight.setPosition(Keys.RIGHT_TRIGGER_INIT);
        waitForStart();
        int count = 2;
        while (count < 6) {
            if (gamepad1.a) {
                switch (count) {
                    case 2:
                        clampLeft.setPosition(Keys.CLAMP_LEFT_DOWN);
                        clampRight.setPosition(Keys.CLAMP_RIGHT_DOWN);
                        sleep(2000);
                        clampRight.setPosition(Keys.CLAMP_RIGHT_INIT);
                        clampLeft.setPosition(Keys.CLAMP_LEFT_INIT);
                        sleep(2000);
                        break;
                    case 3:
                        liftLeft.setPower(-.5);
                        liftRight.setPower(-.5);
                        sleep(1000);
                        liftLeft.setPower(0);
                        liftRight.setPower(0);
                        sleep(2000);
                        break;
                    case 4:
                        hang.setPosition(Keys.HANG_NOW);
                        sleep(2000);
                        break;
                    case 5:
                        liftRight.setPower(.5);
                        liftLeft.setPower(.5);
                        sleep(800);
                        liftLeft.setPower(0);
                        liftRight.setPower(0);
                        sleep(2000);
                        break;
                    }
                count++;
            }
            waitOneFullHardwareCycle();
        }
    }
}
