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
public class CameraTestOp extends LinearOpMode {
    private Camera mCamera;
    public CameraPreview preview;
    public Bitmap image;

    @Override
    public void runOpMode() throws InterruptedException {
        mCamera = ((FtcRobotControllerActivity) hardwareMap.appContext).mCamera;
        //i need to init the camera and also get the instance of the camera        //on pic take protocol

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
            Log.e("nh",nh+" h"+image.getHeight()+" w"+image.getWidth());
            image = Bitmap.createScaledBitmap(image,216,nh,true);
            //original will be same size as everything else
            //the saved version of the pic is original size, however
            Vision.savePicture(image,hardwareMap.appContext,"SHRUNKEN", false);
            telemetry.addData("bitmap shrunk","shrunk");
        }

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
        ArrayList<Object> consolidatedEdgeData = Vision.consolidateEdges(edged, totalLabel);
        //without contrast for comparison... disabling because it has already proven to be more effective
        //Vision.savePicture((Bitmap) Vision.convertGrayscaleToEdged(Vision.toGrayscaleBitmap(image)).get(1), hardwareMap.appContext, "WITHOUT CONTRAST", false);

        //debug stuff - telemetry.addData("numOfChange",consolidatedEdgeData.get(Vision.CONSOLIDATEEDGES_DATA_NUMBEROFCHANGES));
        //debug stuff - telemetry.addData("labels","old"+totalLabel+"new"+consolidatedEdgeData.get(Vision.CONSOLIDATEEDGES_DATA_TOTALLABELS));
        totalLabel = (Integer)consolidatedEdgeData.get(Vision.CONSOLIDATEEDGES_DATA_TOTALLABELS);
        Bitmap consolidatedEdge = (Bitmap) consolidatedEdgeData.get(Vision.CONSOLDIATEEDGES_DATA_BITMAP);
        telemetry.addData("consolidated Edge", Vision.savePicture(consolidatedEdge,hardwareMap.appContext,"CONSOLIDATED_EDGE", false) );

        //removing random edges
        ArrayList<Object> removedRandomnessData=Vision.getRidOfRandomEdges(consolidatedEdge,totalLabel);
        Bitmap removedRandomness = (Bitmap)removedRandomnessData.get(Vision.REMOVERANDOMNESS_DATA_BITMAP);
        telemetry.addData("removedRandomness",Vision.savePicture(removedRandomness,hardwareMap.appContext,"REMOVED_RANDOMNESS", false) );
        //debug stuff - telemetry.addData("labels","old"+totalLabel+"new"+removedRandomnessData.get(Vision.REMOVERANDOMNESS_DATA_LABELS));
        totalLabel=(Integer)removedRandomnessData.get(Vision.REMOVERANDOMNESS_DATA_LABELS);
        ArrayList <Object> returnedCirclesData = Vision.returnCircles(removedRandomness,totalLabel);

        //finding the circles
        Bitmap circles = (Bitmap)returnedCirclesData.get(Vision.RETURNCIRCLES_DATA_BITMAP);
        telemetry.addData("circles",Vision.savePicture(circles,hardwareMap.appContext,"CIRCLES", false));
        telemetry.addData("circles found",Vision.getNumberOfLabelsAssumingOrganized(circles));


    }
}