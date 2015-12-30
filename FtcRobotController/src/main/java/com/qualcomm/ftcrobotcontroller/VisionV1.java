package com.qualcomm.ftcrobotcontroller;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Camera;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import java.io.File;
import java.util.ArrayList;

/**
 * Created 1/5/2015.
 */
public class VisionV1 extends LinearOpMode{
    private Camera mCamera;
    public CameraPreview preview;
    private Bitmap image;
    int maxPixelWidthOfBlackDot;
    int maxPixelHeightOfBlackDot;
    double minValueAllowedForBlack;
    ArrayList<XYCoor[]> listOfBlackDots;
    int minValueForRed;
    int maxValueForRed;
    int minValueForBlue;
    int maxValueForBlue;
    int howManyToCheckForWidthOfRed;
    boolean foundRedDot;
    boolean foundBlueDot;
    XYCoor blue;
    XYCoor red;


    @Override
    public void runOpMode() throws InterruptedException {
        foundRedDot=false;
        foundBlueDot =false;
        howManyToCheckForWidthOfRed =2;
        minValueForRed=250;
        maxValueForRed=325;
        minValueForBlue=180;
        maxValueForBlue=220;
        maxPixelWidthOfBlackDot = 6;
        maxPixelHeightOfBlackDot = 6;
        minValueAllowedForBlack=.9;
        mCamera = ((FtcRobotControllerActivity)hardwareMap.appContext).mCamera;
        //i need to init the camera and also get the instance of the camera        //on pic take protocol

        ((FtcRobotControllerActivity) hardwareMap.appContext).initCameraPreview(mCamera, this);

        //wait 6 seconds, because I have handler wait three seconds b4 it'll take a picture, in initCamera
        sleep(6000);
        //now we are going to retreive the image and convert it to bitmap
        SharedPreferences prefs = hardwareMap.appContext.getApplicationContext().getSharedPreferences(
                "com.quan.companion", Context.MODE_PRIVATE);
        String path =prefs.getString(Keys.pictureImagePathSharedPrefsKeys,"No path found");
        if (!path.equals("No path found")) {
            telemetry.addData("camera","path: "+path);
            File imgFile = new File (path);
            image = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            telemetry.addData("image",image.toString());
            ((FtcRobotControllerActivity) hardwareMap.appContext).initImageTakenPreview(image);
            //ok so now I have the image
            int bitmapWidth = image.getWidth();
            int bitmapHeight = image.getHeight();
            telemetry.addData("bitmap size",bitmapHeight+"h"+" x "+bitmapWidth+"w");
            //o,o should be top left
            //width-1, 0 should be top right
            //0, height -1 should be bottom left
            //width-1, height -1 should be bottom left
            //what we're going to do is try to find the black dots that are always surrounded by lighter colors (blue or red)
            //we're going to convert hsv
            //s represents the % amoutn of gray
            //v repreents the vibrancy/brightness
            //the dot is approximately  6x6
            for (int y= bitmapHeight-1;y>=0;y--) {
                for (int x = 0; x<bitmapWidth;x++) {
                    int pixel = image.getPixel(x,y);
                    //pixel is in rgb value. let's break this into hsv values
                    // hsv[0] is Hue [0 .. 360)
                    // hsv[1] is Saturation [0...1]
                    // hsv[2] is Value [0...1]
                    float[] hsv = new float[3];
                    Color.colorToHSV(pixel,hsv);
                    //when s is approx100, then it's black.
                    //make sure s is not 100 for more than 6 pixels - if it does, then it's probably a black bar
                    if (hsv[1]>minValueAllowedForBlack) {
                        //pixel is probably black
                        determineBlackDot(x, y);
                        if (foundRedDot&& foundBlueDot) {
                            break;
                        }
                    }
                }
            }
            if (foundRedDot&&foundBlueDot) {
                //compare horizontal
                if (Math.abs(blue.getY()-red.getY())>maxPixelHeightOfBlackDot) {
                    //then there's a problem
                    telemetry.addData("error","couldn't find any black dots");
                }
                else {
                    telemetry.addData("foundBoth!","true");
                    //see whether red is right or left
                    if (blue.getX()<red.getX()) {
                        //blue is on right
                        telemetry.addData("foundBoth!","blue is on left red is on right");
                    }
                    else {
                        telemetry.addData("foundBoth!","red is on left blue is on right");
                    }
                }

            }
            else {

                telemetry.addData("error","couldn't find any black dots");
            }
        }
        else {
            telemetry.addData("camera","path not found");
        }
    }

    private boolean determineBlackDot(int x, int y) {
        //with this pixel coordinate, look to the right of this pixel. If more than six pixels to the right still returns black, it may not be a pixel
        int orgx = x;
        int orgy=y;
        int pixel = image.getPixel(x,y);
        //pixel is in rgb value. let's break this into hsv values
        // hsv[0] is Hue [0 .. 360)
        // hsv[1] is Saturation [0...1]
        // hsv[2] is Value [0...1]
        int height =-1;
        //negative one becausei it'll always be true for one
        float[] hsv = new float[3];
        Color.colorToHSV(pixel, hsv);


        //red is weird, after black, black to right, but ontop, it's not very black...
        //so check the hue, and if it's between red, then call check red
        if (hsv[0]>minValueForRed&&hsv[0]<maxValueForRed) {
            if (checkIfDotIsRed(x,y)) {
                foundRedDot=true;
                red = new XYCoor (orgx,orgy);
                telemetry.addData("foundRedDot","true"+x+"x, "+y+"y");
                return true;
            }
            else {
                telemetry.addData("foundRedDot","called, but false");
                return false;
            }
        }
        while (hsv[1]>minValueAllowedForBlack) {
            //move up until it's not black.
            //we're hoping the first black color hits around the middle, this is most ilkely because circle
            //so move up. once it's not, the height is found
            height++;
            if (height>maxPixelHeightOfBlackDot+1) {
                telemetry.addData("foundBlueDot","false, too high");
                return false;
                //if it's too big, it can't be the dot get out.
                //give it a one pixel give/take
            }
            y++;
            pixel = image.getPixel(x,y);
            Color.colorToHSV(pixel,hsv);
        }
        //now we have the height of the dot
        if (height<maxPixelHeightOfBlackDot/2-1) {
            //too small
            telemetry.addData("foundBlueDot","false, height is too small");
            return false;
        }
        else {
            //check for blue hue
            float hue = hsv[0];
            if (hue>minValueForBlue&&hue<maxValueForBlue) {
                //it is blue

                foundBlueDot=true;
                blue = new XYCoor(orgx,orgy);
                telemetry.addData("foundBlueDot","true"+x+"x, "+y+"y");
                return true;
            }
            else {
                //it wasn't blue

                telemetry.addData("foundBlueDot","false");
                return false;
            }
        }

    }

    private boolean checkIfDotIsRed(int x, int y) {
        int pixel = image.getPixel(x,y);
        //pixel is in rgb value. let's break this into hsv values
        // hsv[0] is Hue [0 .. 360)
        // hsv[1] is Saturation [0...1]
        // hsv[2] is Value [0...1]
        int height =-1;
        //negative one becausei it'll always be true for one
        float[] hsv = new float[3];
        Color.colorToHSV(pixel, hsv);
        boolean isRedDot;
        for (int i =1; i<howManyToCheckForWidthOfRed;i++) {
            pixel = image.getPixel(x+i,y);
            hsv = new float[3];
            Color.colorToHSV(pixel, hsv);
            if (hsv[1]>minValueAllowedForBlack) {
                return false;
                //all of these pixels should be black and red
            }
        }
        //well these have red backgrounds, and the blackness is eh
        //must be black dot of red

        return true;
    }


}
