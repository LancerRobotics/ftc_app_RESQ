package com.qualcomm.ftcrobotcontroller.opmodes.states;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * Created by jakew on 2/26/2016.
 */
public class Timer extends LinearOpMode{
    ElapsedTime timer;

    public void runOpMode() throws InterruptedException {
        //initalize stuff here
        timer = new ElapsedTime();
        //waiting for start
        waitForStart();
        timer.reset(); //resets the timer when the start button is pushed
        while(opModeIsActive()) {
            telemetry.addData("Time Runnint", timer.time());
        }
        telemetry.addData("OpMode Stopped At", timer.time());
    }
}
