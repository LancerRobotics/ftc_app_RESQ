package com.qualcomm.ftcrobotcontroller.opmodes.ftclancers;
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
    private navXPIDController yawPIDController;
    private ElapsedTime runtime = new ElapsedTime();
    public static final double YAW_PID_P = 0.005;
    public static final double YAW_PID_I = 0.0;
    public static final double YAW_PID_D = 0.0;
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
        gyroTurn(90.0, true);
    }
    public void rest() {
        fr.setPower(0);
        fl.setPower(0);
        bl.setPower(0);
        br.setPower(0);
    }
    public void turn (double power) {
        fl.setPower(power);
        bl.setPower(power);
        fr.setPower(-power);
        br.setPower(-power);
    }
    public void gyroTurn(double degreesOfTurn, boolean right) {
        if (right) {
            degreesOfTurn = degreesOfTurn * -1;
        }

        yawPIDController = new navXPIDController(navx_device, navXPIDController.navXTimestampedDataSource.YAW);
        yawPIDController.setSetpoint(degreesOfTurn);
        yawPIDController.setContinuous(true);
        yawPIDController.setOutputRange(Keys.MAX_SPEED * -1, Keys.MAX_SPEED);
        yawPIDController.setTolerance(navXPIDController.ToleranceType.ABSOLUTE, Keys.TOLERANCE_DEGREES);
        yawPIDController.setPID(YAW_PID_P, YAW_PID_I, YAW_PID_D);
        boolean onTarget = false;
        try {
            yawPIDController.enable(true);
            int DEVICE_TIMEOUT_MS = 500;
            navXPIDController.PIDResult yawPIDResult = new navXPIDController.PIDResult();
            while (!onTarget) {
                if (yawPIDController.waitForNewUpdate(yawPIDResult, DEVICE_TIMEOUT_MS)) {
                    if (yawPIDResult.isOnTarget()) {
                        rest();
                        onTarget = true;
                    } else {
                        if (yawPIDController.getSetpoint() - 30 <= navx_device.getYaw() && navx_device.getYaw() <= yawPIDController.getSetpoint() + 30)
                            turn(yawPIDResult.getOutput() / 10);
                        else
                            turn(yawPIDResult.getOutput());
                    }
                } else {
			    /* A timeout occurred */
                    telemetry.addData("navXRotateOp", "Yaw PID waitForNewUpdate() TIMEOUT.");
                }
                telemetry.addData("Yaw", navx_device.getYaw());
                telemetry.addData("Motor Power", yawPIDResult.getOutput());
                telemetry.addData("End Point", yawPIDController.getSetpoint());
                telemetry.addData("Finished Turn?", onTarget);
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        } finally {
            navx_device.close();
        }
    }
}