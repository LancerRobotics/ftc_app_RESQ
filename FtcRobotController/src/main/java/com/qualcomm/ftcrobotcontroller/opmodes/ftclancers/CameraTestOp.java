package com.qualcomm.ftcrobotcontroller.opmodes.ftclancers;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.util.Log;

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
        //scale image down to 216 height if needed
        if (image.getHeight()>216||image.getWidth()>216) {
            //too large to be uploaded into a texture
            int nh = (int) ( image.getHeight() * (216.0 / image.getWidth()) );
            Log.e("nh",nh+" h"+image.getHeight()+" w"+image.getWidth());
            image = Bitmap.createScaledBitmap(image,216,nh,true);
            //original will be same size as everything else
            //the saved version of the pic is original size, however
            Vision.savePicture(image,hardwareMap.appContext,"SHRUNKEN");
            telemetry.addData("bitmap shrunk","shrunk");
        }
        String returnedStringViaFindViaSplitImageInHalfAndSeeWhichColorIsOnWhichSide = Vision.findViaSplitImageInHalfAndSeeWhichColorIsOnWhichSide(image);
        telemetry.addData("Vision1","half split color only" +returnedStringViaFindViaSplitImageInHalfAndSeeWhichColorIsOnWhichSide);
        Log.e("half split color", returnedStringViaFindViaSplitImageInHalfAndSeeWhichColorIsOnWhichSide);
<<<<<<< HEAD
        //String returnedStringViaCutAndWhite = Vision.findViaWhiteOutNotWorthyPixelsAndThenFindANonWhiteFromLeftAndSeeColor(image, hardwareMap.appContext);
        //telemetry.addData("Vision2","white out "+returnedStringViaCutAndWhite);
        //Log.e("whiteout",returnedStringViaCutAndWhite);
=======
        String returnedStringViaCutAndWhite = Vision.findViaWhiteOutNotWorthyPixelsAndThenFindANonWhiteFromLeftAndSeeColor(image, hardwareMap.appContext);
        telemetry.addData("Vision2","white out "+returnedStringViaCutAndWhite);
        Log.e("whiteout", returnedStringViaCutAndWhite);
>>>>>>> aa3de7cf5d8cd60bcab62dd7a19e8321d987b23c
        Bitmap grayscaleBitmap = Vision.toGrayscaleBitmap(image);
        telemetry.addData("grayscale image", Vision.savePicture(grayscaleBitmap, hardwareMap.appContext,"GRAYSCALE"));
        Bitmap edged = Vision.convertGrayscaleToEdged(grayscaleBitmap);
        telemetry.addData("edged image",Vision.savePicture(edged,hardwareMap.appContext,"EDGED"));
        Bitmap filtered = Vision.filter(edged);
        telemetry.addData("filtered image",Vision.savePicture(filtered,hardwareMap.appContext,"FILTERED"));
    }
}
