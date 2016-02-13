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
import java.util.ArrayList;

/**
 * Created by Matt on 12/27/2015.
 */
public class Jarvis extends LinearOpMode {
    public Bitmap image;

    @Override
    public void runOpMode() throws InterruptedException {


        //sleep(Vision.RETRIEVE_FILE_TIME);
        //now we are going to retreive the image and convert it to bitmap
        String path = "/storage/emulated/0/Pictures/Testing/input.png";

        telemetry.addData("camera", "path: " + path);
        File imgFile = new File(path);
        image = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        telemetry.addData("image", image.toString());
        Vision.savePictureOuput((Bitmap)(Vision.consolidateEdges(image,Vision.getNumberOfLabelsAssumingOrganized(image)).get(2)),hardwareMap.appContext);
        Log.e("done","done");
        //ok so now I have the image
        //storage/emulated/0/Pictures/Matt Quan is a boss/IMG_20160211_200555Original.jpg

    }
}
