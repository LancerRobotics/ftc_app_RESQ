package com.qualcomm.ftcrobotcontroller.opmodes.ftclancers;

import android.util.Log;

import com.kauailabs.navx.ftc.AHRS;
import com.kauailabs.navx.ftc.navXPIDController;
import com.qualcomm.ftcrobotcontroller.Keys;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * Created by spork on 12/17/2015.
 */
public class GyroAuton extends LinearOpMode {
    DcMotor fr, fl, bl, br;
    //double a3,a4,a5;
    private AHRS navx_device;
    private navXPIDController yawPIDController;
    private ElapsedTime runtime = new ElapsedTime();
    public static final double YAW_PID_P = 0.005;
    public static final double YAW_PID_I = 0.0;
    public static final double YAW_PID_D = 0.0;
    public static boolean calibration_complete = false;
    @Override
    public void runOpMode() throws InterruptedException {
        fr = hardwareMap.dcMotor.get(Keys.frontRight);
        fl = hardwareMap.dcMotor.get(Keys.frontLeft);
        bl = hardwareMap.dcMotor.get(Keys.backLeft);
        br = hardwareMap.dcMotor.get(Keys.backRight);
        fr.setDirection(DcMotor.Direction.REVERSE);
        br.setDirection(DcMotor.Direction.REVERSE);
        navx_device = AHRS.getInstance(hardwareMap.deviceInterfaceModule.get(Keys.advancedSensorModule), Keys.NAVX_DIM_I2C_PORT, AHRS.DeviceDataType.kProcessedData, Keys.NAVX_DEVICE_UPDATE_RATE_HZ);
        waitForStart();
        gyroTurn(90);
    }
    public void rest() {
        fr.setPower(0);
        fl.setPower(0);
        bl.setPower(0);
        br.setPower(0);
    }
    public void turnLeft (double power) {
        fr.setPower(power);
        br.setPower(power);
    }
    public void turnRight (double power) {
        fl.setPower(power);
        bl.setPower(power);
    }
    public void gyroTurn (double degrees) {
        degrees=degrees*-1;
        yawPIDController = new navXPIDController(navx_device, navXPIDController.navXTimestampedDataSource.YAW);
        yawPIDController.setSetpoint(degrees);
        yawPIDController.setContinuous(true);
        yawPIDController.setOutputRange(Keys.MAX_SPEED * -1, Keys.MAX_SPEED);
        yawPIDController.setTolerance(navXPIDController.ToleranceType.ABSOLUTE,Keys.TOLERANCE_DEGREES);

        yawPIDController.enable(true);
        int DEVICE_TIMEOUT_MS = 500;
        navXPIDController.PIDResult yawPIDResult = new navXPIDController.PIDResult();

        telemetry.addData("Yaw", navx_device.getYaw());
        double degreesNow = navx_device.getYaw();
        double degreesToGo = degreesNow+degrees;
        telemetry.addData("if state",navx_device.getYaw());
        telemetry.addData("other if",degreesToGo);
        telemetry.addData("boolean",navx_device.getYaw()>degreesToGo);
        telemetry.addData("boolean",navx_device.getYaw()<degreesToGo);
        if (navx_device.getYaw()>degreesToGo) {
            telemetry.addData("if","getYaw>degrees");
            while (!(degreesToGo-Keys.TOLERANCE_DEGREES<navx_device.getYaw()&&navx_device.getYaw()<degreesToGo+Keys.TOLERANCE_DEGREES));
            {
                telemetry.addData("while","turningLeft");
                turnLeft(.6);
                telemetry.addData("if",".yaw"+navx_device.getYaw()+"toGo"+degreesToGo);
            }
            telemetry.addData("while","done");
        }
        else if (navx_device.getYaw()<degreesToGo) {
            telemetry.addData("if","getYaw<degrees");
            while (!(degreesToGo-Keys.TOLERANCE_DEGREES<navx_device.getYaw()&&navx_device.getYaw()<degreesToGo+Keys.TOLERANCE_DEGREES)) {
                turnRight(.6);
                telemetry.addData("while","turningRight");
                telemetry.addData("if",".yaw"+navx_device.getYaw()+"toGo"+degreesToGo);
            }
            telemetry.addData("whileD","done");
        }
        telemetry.addData("ifD","done");
        rest();



    }



}
