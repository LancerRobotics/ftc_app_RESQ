package com.qualcomm.ftcrobotcontroller.opmodes.ftclancers;
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
    DcMotor fr, fl, bl, br;
    //double a3,a4,a5;
    private AHRS navx_device;
    private navXPIDController leftYawPIDController, rightYawPIDController;
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
        leftYawPIDController = new navXPIDController(navx_device, navXPIDController.navXTimestampedDataSource.YAW);
        leftYawPIDController.setContinuous(true);
        leftYawPIDController.setOutputRange(Keys.MAX_SPEED * -1, Keys.MAX_SPEED);
        leftYawPIDController.setTolerance(navXPIDController.ToleranceType.ABSOLUTE, Keys.TOLERANCE_DEGREES);
        leftYawPIDController.setPID(YAW_PID_P, YAW_PID_I, YAW_PID_D);
        rightYawPIDController = new navXPIDController(navx_device, navXPIDController.navXTimestampedDataSource.YAW);
        rightYawPIDController.setContinuous(true);
        rightYawPIDController.setOutputRange(Keys.MAX_SPEED * -1, Keys.MAX_SPEED);
        rightYawPIDController.setTolerance(navXPIDController.ToleranceType.ABSOLUTE, Keys.TOLERANCE_DEGREES);
        rightYawPIDController.setPID(YAW_PID_P, YAW_PID_I, YAW_PID_D);
        while ( !calibration_complete ) {
            calibration_complete = !navx_device.isCalibrating();
            if (!calibration_complete) {
                telemetry.addData("Start Autonomous?", "No");
            }
        }
        telemetry.addData("Start Autonomous?", "Yes");
        waitForStart();
        telemetry.clearData();
        gyroTurn(90, true);
        telemetry.addData("Done", "HALFWAY");
        sleep(10000);
        gyroTurn(90, false);
        telemetry.addData("Done", "DONE");
        navx_device.close();
    }
    public void rest() {
        fr.setPower(0);
        fl.setPower(0);
        bl.setPower(0);
        br.setPower(0);
    }
    public void turn (double power, boolean right) {
        if(right) {
            fl.setPower(power);
            bl.setPower(power);
        }
        else {
            fr.setPower(power);
            br.setPower(power);
        }
    }
    public void gyroTurn(double degrees, boolean right) {
        rest();
        navx_device.zeroYaw();
        boolean onTarget = false;
        try {
            if (right) {
                rightYawPIDController.setSetpoint(-1 * degrees);
                rightYawPIDController.enable(true);
            }
            else {
                leftYawPIDController.setSetpoint(degrees);
                leftYawPIDController.enable(true);
            }
            int DEVICE_TIMEOUT_MS = 500;
            navXPIDController.PIDResult yawPIDResult = new navXPIDController.PIDResult();
            while (!onTarget) {
                if (leftYawPIDController.waitForNewUpdate(yawPIDResult, DEVICE_TIMEOUT_MS) || rightYawPIDController.waitForNewUpdate(yawPIDResult, DEVICE_TIMEOUT_MS)) {
                    if (leftYawPIDController.isOnTarget() || rightYawPIDController.isOnTarget()) {
                        onTarget = true;
                    }
                    else {
                        turn(yawPIDResult.getOutput(), right);
                    }
                } else {
			    /* A timeout occurred */
                    Log.w("navXRotateOp", "Yaw PID waitForNewUpdate() TIMEOUT.");
                }
                telemetry.addData("Yaw", navx_device.getYaw());
                if(leftYawPIDController.isEnabled())
                    telemetry.addData("Setpoint", leftYawPIDController.getSetpoint());
                else if(rightYawPIDController.isEnabled())
                    telemetry.addData("Setpoint", rightYawPIDController.getSetpoint());
                telemetry.addData("Motor Power", yawPIDResult.getOutput());
                telemetry.addData("Finished Turn?", onTarget);
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        } finally {
            rest();
            if (right) {
                rightYawPIDController.enable(false);
            }
            else {
                leftYawPIDController.enable(false);
            }
        }
    }
}