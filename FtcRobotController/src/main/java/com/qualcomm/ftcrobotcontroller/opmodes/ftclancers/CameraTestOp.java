package com.qualcomm.ftcrobotcontroller.opmodes.ftclancers;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;

import com.qualcomm.ftcrobotcontroller.FtcRobotControllerActivity;
import com.qualcomm.ftcrobotcontroller.Keys;
import com.qualcomm.ftcrobotcontroller.Vision;
import com.qualcomm.ftcrobotcontroller.CameraPreview;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import java.io.File;

/**
 * Created by Matt on 12/27/2015.
 */
public class CameraTestOp extends LinearOpMode {
    private Camera mCamera;
    public CameraPreview preview;
    public Bitmap image;

    @Override
    public void runOpMode() throws InterruptedException {
        mCamera = ((FtcRobotControllerActivity) hardwareMap.appContext).mCamera;
        //i need to init the camera and also get the instance of the camera        //on pic take protocol

        ((FtcRobotControllerActivity) hardwareMap.appContext).initCameraPreview(mCamera, this);

        //wait 6 seconds, because I have handler wait three seconds b4 it'll take a picture, in initCamera
        sleep(6000);
        //now we are going to retreive the image and convert it to bitmap
        SharedPreferences prefs = hardwareMap.appContext.getApplicationContext().getSharedPreferences(
                "com.quan.companion", Context.MODE_PRIVATE);
        String path = prefs.getString(Keys.pictureImagePathSharedPrefsKeys, "No path found");

        telemetry.addData("camera", "path: " + path);
        File imgFile = new File(path);
        image = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        telemetry.addData("image", image.toString());
        ((FtcRobotControllerActivity) hardwareMap.appContext).initImageTakenPreview(image);
        //ok so now I have the image
        String returnedStringViaFindViaSplitImageInHalfAndSeeWhichColorIsOnWhichSide = Vision.findViaSplitImageInHalfAndSeeWhichColorIsOnWhichSide(image);
        telemetry.addData("Vision1","half split color only" +returnedStringViaFindViaSplitImageInHalfAndSeeWhichColorIsOnWhichSide);
        String returnedStringViaCutAndWhite = Vision.findViaWhiteOutNotWorthyPixelsAndThenFindANonWhiteFromLeftAndSeeColor(image,hardwareMap.appContext);
        telemetry.addData("Vision2","white out "+returnedStringViaCutAndWhite);
    }
}
