package com.qualcomm.ftcrobotcontroller.opmodes.worlds;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.util.Log;

import com.qualcomm.ftcrobotcontroller.Beacon;
import com.qualcomm.ftcrobotcontroller.CameraPreview;
import com.qualcomm.ftcrobotcontroller.FtcRobotControllerActivity;
import com.qualcomm.ftcrobotcontroller.Keys;
import com.qualcomm.ftcrobotcontroller.Vision;
import com.qualcomm.ftcrobotcontroller.VisionProcess;
import com.qualcomm.ftcrobotcontroller.XYCoor;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Matt on 3/29/2016.
 */
public class CameraTestOpWorlds extends LinearOpMode {
    private Camera mCamera;
    public CameraPreview preview;
    public Bitmap image;

    @Override
    public void runOpMode() throws InterruptedException {
        mCamera = ((FtcRobotControllerActivity) hardwareMap.appContext).mCamera;
        //i need to init the camera and also get the instance of the camera        //on pic take protocol
        telemetry.addData("camera","initingcameraPreview");
        ((FtcRobotControllerActivity) hardwareMap.appContext).initCameraPreview(mCamera, this);

        //wait, because I have handler wait three seconds b4 it'll take a picture, in initCamera
        sleep(Vision.RETRIEVE_FILE_TIME);
        //now we are going to retreive the image and convert it to bitmap
        SharedPreferences prefs = hardwareMap.appContext.getApplicationContext().getSharedPreferences(
                "com.quan.companion", Context.MODE_PRIVATE);
        String path = prefs.getString(Keys.pictureImagePathSharedPrefsKeys, "No path found");
        Log.e("path",path);
        telemetry.addData("image",path);
        //debug stuff - telemetry.addData("camera", "path: " + path);
        File imgFile = new File(path);
        image = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        Log.e("image",image.toString());
        //cool now u have the image file u just took the picture of
        VisionProcess mVP = new VisionProcess(image);
        Log.e("starting output","start");
        telemetry.addData("starting output","doing smart computer stuff now");
        Beacon beacon = mVP.output(hardwareMap.appContext);
        Log.e("beacon",beacon.toString());
        telemetry.addData("beacon",beacon);


    }
}