package com.qualcomm.ftcrobotcontroller.opmodes.ftclancers;

import com.qualcomm.ftcrobotcontroller.Keys;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * Created on 1/4/2016.
 */
public class AnalogSonar extends LinearOpMode {
    AnalogInput sonarFrontLeft, sonarFrontRight, sonarTop;
    DcMotor fl, bl, br, fr;
    double distanceToCheck;

    @Override
    public void runOpMode() throws InterruptedException {
        sonarFrontLeft = hardwareMap.analogInput.get(Keys.SONAR_FOOT);
        sonarFrontRight = hardwareMap.analogInput.get(Keys.SONAR_FOOT);
        sonarTop = hardwareMap.analogInput.get(Keys.SONAR_ABOVE_PHONE);
        fr = hardwareMap.dcMotor.get(Keys.frontRight);
        fl = hardwareMap.dcMotor.get(Keys.frontLeft);
        br = hardwareMap.dcMotor.get(Keys.backRight);
        bl = hardwareMap.dcMotor.get(Keys.backLeft);
        waitForStart();
        distanceToCheck = 24;
        //inches
        telemetry.addData("IN POSITION?", "NO");
        if (!checkSonarPosition(distanceToCheck)) {
            correctMovement(distanceToCheck);
        }
        telemetry.addData("IN POSITION?", "YES");
        telemetry.addData("MOVING TO PART 2?", "YES");
        sleep(10000);
        while(!objectInFront()) {
            setMotorPowerUniform(.4, false);
        }
        rest();
        sleep(2000);
        correctMovement(distanceToCheck);
    }

    //returns sonar values in inches
    public double readSonar(AnalogInput sonar) {
        double sValue = sonar.getValue();
        sValue = sValue/2;
        return sValue;
    }

    public boolean objectInFront () {
        double sonarFL = readSonar(sonarFrontLeft);
        double sonarFR = readSonar(sonarFrontRight);
        if (sonarFL < 10 || sonarFR < 10) {
            return true;
        }
        else {
            return false;
        }
    }

    public boolean checkSonarPosition(double distance) {
        boolean allGood;
        if(distance-3 <= readSonar(sonarFrontLeft) && readSonar(sonarFrontLeft)<= distance+3 && distance-3 <= readSonar(sonarFrontRight) && readSonar(sonarFrontRight)<= distance+3) {
            allGood = true;
        }
        else {
            allGood = false;
        }
        return allGood;
    }
    public void correctMovement(double distance) {
        while (!checkSonarPosition(distance)) {
            if (readSonar(sonarFrontRight) < distance-3 || readSonar(sonarFrontLeft) < distance-3) {
                setMotorPowerUniform(.35, false);
            } else if (readSonar(sonarFrontLeft) > distance+3 || readSonar(sonarFrontRight) > distance+3) {
                setMotorPowerUniform(.35, true);
            }
        }
        rest();
    }

    public void setMotorPowerUniform(double power, boolean backwards) {
        int direction = 1;
        if (backwards) {
            direction = -1;
        }
        power = direction * -1;
        fr.setPower(power);
        fl.setPower(power);
        bl.setPower(power);
        br.setPower(power);
    }

    public void rest() {
        fr.setPower(0);
        fl.setPower(0);
        bl.setPower(0);
        br.setPower(0);
    }
}
