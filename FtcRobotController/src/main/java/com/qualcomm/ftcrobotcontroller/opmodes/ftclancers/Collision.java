package com.qualcomm.ftcrobotcontroller.opmodes.ftclancers;

import com.kauailabs.navx.ftc.AHRS;
import com.qualcomm.ftcrobotcontroller.Keys;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * Created by spork on 3/2/2016.
 */
public class Collision extends LinearOpMode {
    AnalogInput sonarAbovePhone, sonarFoot;
    private AHRS navx_device;
    DcMotor fl, bl, br, fr;
    double distanceToCheck;
    boolean calibration_complete = false;

    @Override
    public void runOpMode() throws InterruptedException {
        fr = hardwareMap.dcMotor.get(Keys.frontRight);
        fl = hardwareMap.dcMotor.get(Keys.frontLeft);
        br = hardwareMap.dcMotor.get(Keys.backRight);
        bl = hardwareMap.dcMotor.get(Keys.backLeft);
        sonarAbovePhone = hardwareMap.analogInput.get(Keys.SONAR_ABOVE_PHONE);
        sonarFoot = hardwareMap.analogInput.get(Keys.SONAR_FOOT);
        navx_device = AHRS.getInstance(hardwareMap.deviceInterfaceModule.get(Keys.advancedSensorModule), Keys.NAVX_DIM_I2C_PORT, AHRS.DeviceDataType.kProcessedData, Keys.NAVX_DEVICE_UPDATE_RATE_HZ);
        while (!calibration_complete) {
            calibration_complete = !navx_device.isCalibrating();
            if (!calibration_complete) {
                telemetry.addData("Calibration Complete?", "No");
            }
        }
        telemetry.addData("Calibration Complete?", "Yes");
        waitForStart();
        while(!gamepad1.a) {
            sleep(1);
        }
        setMotorPowerUniform(Keys.MAX_SPEED, false);
        while(!objectInFront()) {
            sleep(1);
        }
        rest();
        while(!gamepad1.a) {
            sleep(1);
        }
        setMotorPowerUniform(.66, false);
        telemetry.addData("hitBeaconX", hitBeaconX());
        telemetry.addData("hitBeaconZ", hitBeaconZ());
        telemetry.addData("hitBeaconY", hitBeaconY());

    }
    public void setMotorPowerUniform(double power, boolean backwards) {
        int direction = 1;
        if (backwards) {
            direction = -1;
        }
        fr.setPower(direction * power);
        fl.setPower(direction * power);
        bl.setPower(direction * power);
        br.setPower(direction * power);
    }
    public void rest() {
        fr.setPower(0);
        fl.setPower(0);
        bl.setPower(0);
        br.setPower(0);
    }
    public double readSonar(AnalogInput sonar) {
        double sValue = sonar.getValue();
        sValue = sValue/2;
        return sValue;
    }

    public boolean objectInFront () {
        double sonarFootValue = readSonar(sonarFoot);
        double sonarAbovePhoneValue = readSonar(sonarAbovePhone);
        if (sonarFootValue < 7 || sonarAbovePhoneValue < 7) {
            return true;
        }
        else {
            return false;
        }
    }

    public boolean hitBeaconX () {
        double speed = navx_device.getWorldLinearAccelX();
        if(speed < 2) {
            return true;
        }
        else {
            return false;
        }
    }

    public boolean hitBeaconZ () {
        double speed = navx_device.getWorldLinearAccelZ();
        if(speed < 2) {
            return true;
        }
        else {
            return false;
        }
    }

    public boolean hitBeaconY () {
        double speed = navx_device.getWorldLinearAccelY();
        if(speed < 2) {
            return true;
        }
        else {
            return false;
        }
    }
}
