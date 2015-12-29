package com.qualcomm.ftcrobotcontroller.opmodes;

import android.util.Log;

import com.kauailabs.navx.ftc.AHRS;
import com.kauailabs.navx.ftc.navXPIDController;
import com.qualcomm.ftcrobotcontroller.Keys;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * Created on 11/25/2015.
 */
public class GyroAuton extends LinearOpMode {
    DcMotor fr, fl;
    //double a3,a4,a5;
    private AHRS navx_device;
    private navXPIDController yawPIDController;
    private ElapsedTime runtime = new ElapsedTime();
    @Override
    public void runOpMode() throws InterruptedException {
        fr = hardwareMap.dcMotor.get(Keys.frontRight);
        fl = hardwareMap.dcMotor.get(Keys.frontLeft);
        //br = hardwareMap.dcMotor.get(Keys.backRight);
        //bl = hardwareMap.dcMotor.get(Keys.backLeft);
        fr.setDirection(DcMotor.Direction.REVERSE);
        //bl.setDirection(DcMotor.Direction.REVERSE);
        navx_device = AHRS.getInstance(hardwareMap.deviceInterfaceModule.get(Keys.advancedSensorModule), Keys.NAVX_DIM_I2C_PORT, AHRS.DeviceDataType.kProcessedData, Keys.NAVX_DEVICE_UPDATE_RATE_HZ);
        yawPIDController = new navXPIDController( navx_device, navXPIDController.navXTimestampedDataSource.YAW);
        yawPIDController.setContinuous(true);
        yawPIDController.setOutputRange(Keys.MIN_SPEED_SMOOTH_MOVE * -1, Keys.MAX_SPEED_SMOOTH_MOVE);
        yawPIDController.setTolerance(navXPIDController.ToleranceType.ABSOLUTE, Keys.TOLERANCE_DEGREES);
        yawPIDController.setPID(Keys.YAW_PID_P, Keys.YAW_PID_I, Keys.YAW_PID_D);
        waitForStart();
        gyroTurn(90.0, true);
    }

    public void turn (double power) {
        fl.setPower(power);
        fr.setPower(-power);
    }

    public void gyroTurn(double degreesOfTurn, boolean right) {
        if(!right) {
            degreesOfTurn = degreesOfTurn * -1;
        }
        yawPIDController.setSetpoint(degreesOfTurn);
        try {
            yawPIDController.enable(true);
            int DEVICE_TIMEOUT_MS = 500;
            navXPIDController.PIDResult yawPIDResult = new navXPIDController.PIDResult();
            while (!yawPIDResult.isOnTarget()) {
                if (yawPIDController.waitForNewUpdate(yawPIDResult, DEVICE_TIMEOUT_MS)) {
                    double motorPower = yawPIDResult.getOutput();
                    turn(motorPower);
                } else {
			    /* A timeout occurred */
                    Log.w("navXRotateOp", "Yaw PID waitForNewUpdate() TIMEOUT.");
                }
                telemetry.addData("Yaw", navx_device.getYaw());
                telemetry.addData("Motor Power", yawPIDResult.getOutput());
                telemetry.addData("End Point", yawPIDController.getSetpoint());
            }
            rest();
        }
        catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        finally {
            navx_device.close();
        }
    }

    public void rest() {
        fr.setPower(0);
        fl.setPower(0);
    }

}
