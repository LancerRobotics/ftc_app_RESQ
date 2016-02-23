package com.qualcomm.ftcrobotcontroller.opmodes.states;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.util.Log;

import com.kauailabs.navx.ftc.AHRS;
import com.kauailabs.navx.ftc.navXPIDController;
import com.qualcomm.ftcrobotcontroller.Beacon;
import com.qualcomm.ftcrobotcontroller.CameraPreview;
import com.qualcomm.ftcrobotcontroller.FtcRobotControllerActivity;
import com.qualcomm.ftcrobotcontroller.Keys;
import com.qualcomm.ftcrobotcontroller.Vision;
import com.qualcomm.ftcrobotcontroller.XYCoor;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.DcMotor;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by daniel on 2/18/2016.
 */
public class Autonomous extends LinearOpMode {
    DcMotor fr, fl, bl, br, collector;
    AnalogInput sonarAbovePhone;
    //double a3,a4,a5;
    private AHRS navx_device;
    private navXPIDController yawPIDController;
    boolean calibration_complete = false;
    private Camera mCamera;
    public CameraPreview preview;
    public Bitmap image;
    @Override
    public void runOpMode() throws InterruptedException {
        mCamera = ((FtcRobotControllerActivity) hardwareMap.appContext).mCamera;
        fr = hardwareMap.dcMotor.get(Keys.frontRight);
        fl = hardwareMap.dcMotor.get(Keys.frontLeft);
        bl = hardwareMap.dcMotor.get(Keys.backLeft);
        br = hardwareMap.dcMotor.get(Keys.backRight);
        collector=hardwareMap.dcMotor.get(Keys.collector);
        fl.setDirection(DcMotor.Direction.REVERSE);
        bl.setDirection(DcMotor.Direction.REVERSE);
        collector.setDirection(DcMotor.Direction.REVERSE);
        sonarAbovePhone = hardwareMap.analogInput.get(Keys.SONAR_ABOVE_PHONE);
        navx_device = AHRS.getInstance(hardwareMap.deviceInterfaceModule.get(Keys.advancedSensorModule), Keys.NAVX_DIM_I2C_PORT, AHRS.DeviceDataType.kProcessedData, Keys.NAVX_DEVICE_UPDATE_RATE_HZ);
        while ( !calibration_complete ) {
            calibration_complete = !navx_device.isCalibrating();
            if (!calibration_complete) {
                telemetry.addData("Calibration Complete?", "No");
            }
        }
        telemetry.addData("Calibration Complete?", "Yes");
        //telemetry.addData("Start Autonomous?", "Yes");
        waitForStart();
        moveAlteredSin(19, false);
        gyroTurn(-31.75);
        moveAlteredSin(37, false);
        gyroTurn(-61.75);
        adjustToThisDistance(11, sonarAbovePhone);
        rest();
        telemetry.addData("sonar",readSonar(sonarAbovePhone));

        //i need to init the camera and also get the instance of the camera        //on pic take protocol
        telemetry.addData("camera","initingcameraPreview");
        ((FtcRobotControllerActivity) hardwareMap.appContext).initCameraPreview(mCamera, this);

        //wait, because I have handler wait three seconds b4 it'll take a picture, in initCamera
        sleep(Vision.RETRIEVE_FILE_TIME);
        //now we are going to retreive the image and convert it to bitmap
        SharedPreferences prefs = hardwareMap.appContext.getApplicationContext().getSharedPreferences(
                "com.quan.companion", Context.MODE_PRIVATE);
        String path = prefs.getString(Keys.pictureImagePathSharedPrefsKeys, "No path found");

        //debug stuff - telemetry.addData("camera", "path: " + path);
        File imgFile = new File(path);
        image = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        telemetry.addData("image", image.toString());
        //cool now u have the image file u just took the picture of
        //debug stuff - ((FtcRobotControllerActivity) hardwareMap.appContext).initImageTakenPreview(image);
        //ok so now I have the image

        //scale image down to 216 height if needed
        if (image.getHeight()>216||image.getWidth()>216) {
            //too large to be uploaded into a texture
            int nh = (int) ( image.getHeight() * (216.0 / image.getWidth()) );
            Log.e("nh", nh + " h" + image.getHeight() + " w" + image.getWidth());
            image = Bitmap.createScaledBitmap(image,216,nh,true);
            //original will be same size as everything else
            //the saved version of the pic is original size, however
            Vision.savePicture(image,hardwareMap.appContext,"SHRUNKEN", false);
            telemetry.addData("bitmap shrunk","shrunk");
        }
        image= Vision.rotate(image);
        Vision.savePicture(image,hardwareMap.appContext,"ROTATED",false);
        telemetry.addData("bitmap rotate","rotated");

        // deprecated - String returnedStringViaFindViaSplitImageInHalfAndSeeWhichColorIsOnWhichSide = Vision.findViaSplitImageInHalfAndSeeWhichColorIsOnWhichSide(image);
        // deprecated telemetry.addData("Vision1","half split color only" +returnedStringViaFindViaSplitImageInHalfAndSeeWhichColorIsOnWhichSide);
        // deprecated  Log.e("half split color", returnedStringViaFindViaSplitImageInHalfAndSeeWhichColorIsOnWhichSide);
        //deprecated String returnedStringViaCutAndWhite = Vision.findViaWhiteOutNotWorthyPixelsAndThenFindANonWhiteFromLeftAndSeeColor(image, hardwareMap.appContext);
        //deprecated telemetry.addData("Vision2","white out "+returnedStringViaCutAndWhite);
        //deprecated Log.e("whiteout",returnedStringViaCutAndWhite);

        //make image less contrast
        Bitmap contrastedImage = Vision.applyContrastBrightnessFilter(image, Vision.CONTRAST_ADJUSTMENT, Vision.BRIGHTNESS_ADJUSTMENT);
        telemetry.addData("contrast/brightness filter", Vision.savePicture(image, hardwareMap.appContext, "CONTRAST_BRIGHTNESS_FILTERED", false));
        //convert to grayscale/luminance
        Bitmap grayscaleBitmap = Vision.toGrayscaleBitmap(contrastedImage);
        telemetry.addData("grayscale image", Vision.savePicture(grayscaleBitmap, hardwareMap.appContext, "GRAYSCALE", false));

        //conver to edge
        ArrayList<Object> data = Vision.convertGrayscaleToEdged(grayscaleBitmap,Vision.EDGE_THRESHOLD);
        int totalLabel = (Integer) data.get(Vision.CONVERTGRAYSCALETOEDGED_DATA_NUMBER_OF_LABELS);
        telemetry.addData("totalLabel", totalLabel);
        //catching label overflows
        while (totalLabel>=255) {
            //label overflow. redo edge with a higher edge threshold
            int prevUsedThreshold = (Integer)data.get(Vision.CONVERTGRAYSCALETOEDGED_DATA_EDGETHRESHOLDUSED);
            Log.e("OVERFLOW","correcting... prevUsed="+prevUsedThreshold+"new:"+prevUsedThreshold+20);
            data = Vision.convertGrayscaleToEdged(grayscaleBitmap,prevUsedThreshold+20);
            totalLabel = (Integer)data.get(Vision.CONVERTGRAYSCALETOEDGED_DATA_NUMBER_OF_LABELS);
            Log.e("totalLabel","redone:"+totalLabel);
        }
        telemetry.addData("totalLabel","corrected:"+totalLabel);
        Bitmap edged = (Bitmap)data.get(Vision.CONVERTGRAYSCALETOEDGED_DATA_BITMAP);
        telemetry.addData("edged image", Vision.savePicture(edged, hardwareMap.appContext, "PRETTY_EDGED",false));

        //consolidating edges
        ArrayList<Object> consolidatedEdgeData = Vision.consolidateEdges(edged);
        //without contrast for comparison... disabling because it has already proven to be more effective
        //Vision.savePicture((Bitmap) Vision.convertGrayscaleToEdged(Vision.toGrayscaleBitmap(image)).get(1), hardwareMap.appContext, "WITHOUT CONTRAST", false);

        //debug stuff - telemetry.addData("numOfChange",consolidatedEdgeData.get(Vision.CONSOLIDATEEDGES_DATA_NUMBEROFCHANGES));
        //debug stuff - telemetry.addData("labels","old"+totalLabel+"new"+consolidatedEdgeData.get(Vision.CONSOLIDATEEDGES_DATA_TOTALLABELS));
        totalLabel = (Integer)consolidatedEdgeData.get(Vision.CONSOLIDATEEDGES_DATA_TOTALLABELS);
        Bitmap consolidatedEdge = (Bitmap) consolidatedEdgeData.get(Vision.CONSOLDIATEEDGES_DATA_BITMAP);
        telemetry.addData("consolidated Edge", Vision.savePicture(consolidatedEdge,hardwareMap.appContext,"CONSOLIDATED_EDGE", false) );

        //removing random edges
        ArrayList<Object> removedRandomnessData=Vision.getRidOfRandomEdges(consolidatedEdge);
        Bitmap removedRandomness = (Bitmap)removedRandomnessData.get(Vision.REMOVERANDOMNESS_DATA_BITMAP);
        telemetry.addData("removedRandomness",Vision.savePicture(removedRandomness,hardwareMap.appContext,"REMOVED_RANDOMNESS", false) );
        //debug stuff - telemetry.addData("labels","old"+totalLabel+"new"+removedRandomnessData.get(Vision.REMOVERANDOMNESS_DATA_LABELS));
        totalLabel=(Integer)removedRandomnessData.get(Vision.REMOVERANDOMNESS_DATA_LABELS);
        ArrayList <Object> returnedCirclesData = Vision.returnCircles(removedRandomness);

        //finding the circles
        Bitmap circles = (Bitmap)returnedCirclesData.get(Vision.RETURNCIRCLES_DATA_BITMAP);
        Log.e("circles", String.valueOf(Vision.getNumberOfLabelsNotOrganized(circles)));
        telemetry.addData("circles",Vision.savePicture(circles,hardwareMap.appContext,"CIRCLES", false));
        ArrayList<boolean[]> beaconColorValues;
        ArrayList<XYCoor> centers =  (ArrayList<XYCoor>)returnedCirclesData.get(Vision.RETURNCIRCLES_DATA_XYCOORSCENTER);
        ArrayList<Integer> labels = (ArrayList<Integer> )returnedCirclesData.get(Vision.RETURNCIRCLES_DATA_LABELSLIST);
        Bitmap circlesAdjusted = Vision.findAndIsolateBeaconButtons(circles,centers, labels);
        int circlesFound = Vision.getNumberOfLabelsNotOrganized(circlesAdjusted);
        telemetry.addData("circles adjusted",Vision.savePicture(circlesAdjusted,hardwareMap.appContext,"CIRCLES_ADJUSTED", false));
        telemetry.addData("circles found",circlesFound);
        Beacon beacon = Vision.getBeacon(circlesAdjusted,contrastedImage);
        telemetry.addData("beacon is",beacon);

    }


    public void adjustToThisDistance(double distance, AnalogInput sonar) {
        double myPosition  = readSonar(sonar);
        telemetry.addData("myPos",myPosition);
        if (readSonar(sonar)<distance-Keys.SONAR_TOLERANCE) {
            telemetry.addData("if","readSonar<distance");
            while(readSonar(sonar)<distance-Keys.SONAR_TOLERANCE) {
                telemetry.addData("while","looping3");
                telemetry.addData("mySonar",readSonar(sonar));
                telemetry.addData("dist",distance);
                setMotorPowerUniform(.15, true);
                telemetry.addData("bool read<dist+tol",readSonar(sonar)<distance-Keys.SONAR_TOLERANCE);
            }
        }
        else if (myPosition>distance+Keys.SONAR_TOLERANCE) {
            telemetry.addData("if","readSonar<distance");
            while (readSonar(sonar)>distance+Keys.SONAR_TOLERANCE) {
                telemetry.addData("while", "looping");
                telemetry.addData("mySonar",readSonar(sonar));
                telemetry.addData("dist",distance);
                setMotorPowerUniform(.15, false);
                telemetry.addData("bool read>dist+tol", readSonar(sonar) > distance + Keys.SONAR_TOLERANCE);
            }
        }

        rest();
        telemetry.addData("sonar","done");
    }
    //returns sonar values in inches!!!
    public double readSonar(AnalogInput sonar) {
        double sValue = sonar.getValue();
        sValue = sValue/2;
        return sValue;
    }


    public void moveAlteredSin(double dist, boolean backwards) {
        //inches

        double rotations = dist / (6 * Math.PI);
        double totalTicks = rotations * 1120 * 3 / 2;
        int positionBeforeMovement = fl.getCurrentPosition();
        while (fl.getCurrentPosition() < positionBeforeMovement + totalTicks) {
            telemetry.addData("front left encoder: ", "sin" + fl.getCurrentPosition());
            telemetry.addData("ticksFor", totalTicks);
            collector.setPower(-.5);
            //convert to radians
            int currentTick = fl.getCurrentPosition() - positionBeforeMovement;
            //accelerate 15% of time
            //coast 25% of time
            //decelerate 60% of time
            int firstSectionTime = (int) Math.round(.1 * totalTicks);
            int secondSectionTime = (int) (Math.round((.1 + .25) * totalTicks));
            //rest will just be 100%
            double power;
            if (currentTick < firstSectionTime) {

                power = .3 * Math.cos((currentTick) * Math.PI / totalTicks + Math.PI) + .4;

                power += .1;
                //first quarter (period = 2pi) of sin function is only reaching altitude

            } else if (currentTick < secondSectionTime) {
                power = .8;

            } else {
                // between [40%,100%]
                //decrease time
                int ticksLeft = (int) Math.round(currentTick - (totalTicks * .35));
                //with these ticks left, set a range within cosine to decrease
                power = .4 * Math.cos((ticksLeft) * Math.PI / totalTicks) + .4;
            }

            telemetry.addData("power", power);
            setMotorPowerUniform(power, backwards);
        }
        rest();
    }

    public void setMotorPowerUniform(double power, boolean backwards) {
        int direction = 1;
        if (backwards) {
            direction = -1;
        }
        fr.setPower(direction*power);
        fl.setPower(direction*power);
        bl.setPower(direction*power);
        br.setPower(direction*power);
        //collector.setPower(-.5);

    }
    public void rest() {
        fr.setPower(0);
        fl.setPower(0);
        bl.setPower(0);
        br.setPower(0);
        collector.setPower(0);
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
        //degrees=degrees*-1;
        yawPIDController = new navXPIDController(navx_device, navXPIDController.navXTimestampedDataSource.YAW);
        yawPIDController.setSetpoint(degrees);
        yawPIDController.setContinuous(true);
        yawPIDController.setOutputRange(Keys.MAX_SPEED * -1, Keys.MAX_SPEED);
        yawPIDController.setTolerance(navXPIDController.ToleranceType.ABSOLUTE,Keys.TOLERANCE_LEVEL_2);

        yawPIDController.enable(true);
        int DEVICE_TIMEOUT_MS = 500;
        navXPIDController.PIDResult yawPIDResult = new navXPIDController.PIDResult();

        telemetry.addData("Yaw", navx_device.getYaw());
        double degreesNow = navx_device.getYaw();
        double degreesToGo = degreesNow+degrees;
        //telemetry.addData("if state",navx_device.getYaw());
        //telemetry.addData("other if",degreesToGo);
        telemetry.addData("boolean",navx_device.getYaw()>degreesToGo);
        //telemetry.addData("boolean",navx_device.getYaw()<degreesToGo);
        if (navx_device.getYaw()>degreesToGo) {
            telemetry.addData("if","getYaw>degrees");
            telemetry.addData("more boolean",!(degreesToGo-Keys.TOLERANCE_LEVEL_1<navx_device.getYaw()&&navx_device.getYaw()<degreesToGo+Keys.TOLERANCE_LEVEL_1));
            while (!(degreesToGo-Keys.TOLERANCE_LEVEL_1<navx_device.getYaw()&&navx_device.getYaw()<degreesToGo+Keys.TOLERANCE_LEVEL_1))
            {
                collector.setPower(-.5);
                telemetry.addData("while","turningLeft1");
                turnLeft(.8);
                telemetry.addData("if",".yaw"+navx_device.getYaw()+"toGo"+degreesToGo);
            }
            telemetry.addData("more boolean2",navx_device.getYaw()>degreesToGo+Keys.TOLERANCE_LEVEL_2);
            while (navx_device.getYaw()>degreesToGo+Keys.TOLERANCE_LEVEL_2)
            {
                collector.setPower(-.5);
                telemetry.addData("while","turningLeft2");
                turnLeft(.65);
                telemetry.addData("if",".yaw"+navx_device.getYaw()+"toGo"+degreesToGo);
            }
            while (!(degreesToGo-Keys.TOLERANCE_LEVEL_3<navx_device.getYaw()&&navx_device.getYaw()<degreesToGo+Keys.TOLERANCE_LEVEL_3))
            {
                collector.setPower(-.5);
                telemetry.addData("while","turningLeft3");
                turnLeft(.4);
                telemetry.addData("if",".yaw"+navx_device.getYaw()+"toGo"+degreesToGo);
            }
            telemetry.addData("while","done");
        }
        else if (navx_device.getYaw()<degreesToGo) {
            telemetry.addData("if","getYaw<degrees");
            while (!(degreesToGo-Keys.TOLERANCE_LEVEL_1<navx_device.getYaw()&&navx_device.getYaw()<degreesToGo+Keys.TOLERANCE_LEVEL_1)) {
                collector.setPower(-.5);
                turnRight(.8);
                telemetry.addData("if",".yaw"+navx_device.getYaw()+"toGo"+degreesToGo);
                telemetry.addData("while","turningRight");
            }
            while (!(degreesToGo-Keys.TOLERANCE_LEVEL_2<navx_device.getYaw()&&navx_device.getYaw()<degreesToGo+Keys.TOLERANCE_LEVEL_2)) {
                collector.setPower(-.5);
                turnRight(.65);
                telemetry.addData("if", ".yaw" + navx_device.getYaw() + "toGo" + degreesToGo);
                telemetry.addData("while","turningRight");
            }
            while (!(degreesToGo-Keys.TOLERANCE_LEVEL_3<navx_device.getYaw()&&navx_device.getYaw()<degreesToGo+Keys.TOLERANCE_LEVEL_3)) {
                collector.setPower(-.5);
                turnRight(.4);
                telemetry.addData("if",".yaw"+navx_device.getYaw()+"toGo"+degreesToGo);
                telemetry.addData("while","turningRight");
            }

            telemetry.addData("whileD","done");
        }
        telemetry.addData("ifD","done");
        rest();



    }
}
