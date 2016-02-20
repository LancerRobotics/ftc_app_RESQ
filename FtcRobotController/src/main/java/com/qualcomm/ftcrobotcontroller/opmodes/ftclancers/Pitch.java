package com.qualcomm.ftcrobotcontroller.opmodes.ftclancers;

import com.kauailabs.navx.ftc.AHRS;
import com.kauailabs.navx.ftc.navXPIDController;
import com.qualcomm.ftcrobotcontroller.Keys;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
public class Pitch extends LinearOpMode {
    private AHRS navx_device;
    private navXPIDController yawPIDController;
    private ElapsedTime runtime = new ElapsedTime();
    public static boolean calibration_complete = false;
    @Override
    public void runOpMode() throws InterruptedException {
        navx_device = AHRS.getInstance(hardwareMap.deviceInterfaceModule.get(Keys.advancedSensorModule), Keys.NAVX_DIM_I2C_PORT, AHRS.DeviceDataType.kProcessedData, Keys.NAVX_DEVICE_UPDATE_RATE_HZ);
        while ( !calibration_complete ) {
            calibration_complete = !navx_device.isCalibrating();
            if (!calibration_complete) {
                telemetry.addData("Calibration Complete?", "No");
            }
        }
        waitForStart();
        while (opModeIsActive()) {
            telemetry.addData("Navx Pitch(Z-Axis)", getPitch());
        }
    }
    public float getPitch () {
        yawPIDController = new navXPIDController(navx_device, navXPIDController.navXTimestampedDataSource.YAW);
        yawPIDController.setContinuous(true);
        yawPIDController.setOutputRange(Keys.MAX_SPEED * -1, Keys.MAX_SPEED);
        yawPIDController.setTolerance(navXPIDController.ToleranceType.ABSOLUTE,Keys.TOLERANCE_LEVEL_2);
        yawPIDController.enable(true);
        return navx_device.getPitch();
    }



}
