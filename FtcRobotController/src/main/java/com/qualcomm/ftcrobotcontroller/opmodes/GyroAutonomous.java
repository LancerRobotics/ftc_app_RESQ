/*package com.qualcomm.ftcrobotcontroller.opmodes;

import com.kauailabs.navx.ftc.AHRS;
import com.kauailabs.navx.ftc.navXPIDController;
import com.qualcomm.ftcrobotcontroller.Keys;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

public class GyroAutonomous extends LinearOpMode {
    DcMotor fr, fl;
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

    public void turn(double power, boolean right) {
        double rightPower;
        double leftPower;

        if(right) {
            rightPower = power * -1;
            leftPower = power;
        }
        else {
            rightPower = power;
            leftPower = power * -1;
        }
        fr.setPower(rightPower);
        //br.setPower(rightPower);
        fl.setPower(leftPower);
        //bl.setPower(leftPower);
    }

    public void gyroTurn(double degreesOfTurn, boolean right) {
        if(!right) {
            degreesOfTurn = degreesOfTurn * -1;
        }
        yawPIDController.setSetpoint(degreesOfTurn);
        try {
            yawPIDController.enable(true);

        }
        catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        finally {
            navx_device.close();
            rest();
        }
    }


    public void rest() {
        fr.setPower(0);
        fl.setPower(0);
        //bl.setPower(0);
        //br.setPower(0);

    }
}
*/