package com.qualcomm.ftcrobotcontroller.opmodes.deprecated;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Camera;
import android.provider.MediaStore;
import android.util.Log;

import com.qualcomm.ftcrobotcontroller.CameraPreview;
import com.qualcomm.ftcrobotcontroller.FtcRobotControllerActivity;
import com.qualcomm.ftcrobotcontroller.Keys;
import com.qualcomm.ftcrobotcontroller.Vision;
import com.qualcomm.ftcrobotcontroller.XYCoor;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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

        //TODO just in case((FtcRobotControllerActivity) hardwareMap.appContext).initCameraPreview(mCamera, this);

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
    //DEPRECATED WE DO NOT USE THIS METHOD ANyMORE
    //these values are all based off of the color wheel
    public static String findViaSplitImageInHalfAndSeeWhichColorIsOnWhichSide(Bitmap circies) {
        //find the avg hue for right side, find avg hue for left side
        //compare
        //blue hues tend to be unvariably 180, so like 170 - 190
        //red hues tend to be pink or purple, so somehwere around 295 higher (inaccurate from our testing). but, they can also be very low numbereed hued reds so like 15
        //so basically we can say that blues will be less than reds, since the red comes out as pink
        int xMidPoint = circies.getWidth() / 2;
        double avgHueLeft = 0;
        int pixelCounter = 0;
        for (int i = 0; i < xMidPoint; i++) {
            for (int j = 0; j < circies.getHeight(); j++) {
                int pixel = circies.getPixel(i, j);
                //pixel is in rgb value. let's break this into hsv values
                // hsv[0] is Hue [0 .. 360)
                // hsv[1] is Saturation [0...1]
                // hsv[2] is Value [0...1]
                pixelCounter++;
                float[] hsv = new float[3];
                Color.RGBToHSV(Color.red(pixel),Color.green(pixel),Color.blue(pixel),hsv);
                avgHueLeft += hsv[0];
            }
        }
        //done getting left side
        avgHueLeft = avgHueLeft / pixelCounter;
        //let's do for right side
        double avgHueRight = 0;
        pixelCounter = 0;
        for (int i = circies.getWidth() - 1; i > xMidPoint; i--) {
            for (int j = 0; j < circies.getHeight(); j++) {
                int pixel = circies.getPixel(i, j);
                //pixel is in rgb value. let's break this into hsv values
                // hsv[0] is Hue [0 .. 360)
                // hsv[1] is Saturation [0...1]
                // hsv[2] is Value [0...1]
                pixelCounter++;
                float[] hsv = new float[3];
                Color.colorToHSV(pixel, hsv);
                avgHueRight += hsv[0];
            }
        }
        //done getting right side
        //compare and return
        String returnThis = "avgHueLeft:" + avgHueLeft + "avgHueRight:" + avgHueRight;
        if (Vision.isRedHue(avgHueLeft)) {
            //left was red
            returnThis += "left=red";
            return returnThis;
        } else if (Vision.isBlueHue(avgHueLeft)) {
            returnThis += "left=blue";
            return returnThis;
        } else {
            returnThis += "left=unknown";
        }
        //if you're here, it means we're unknown
        //so let's check the right side
        if (Vision.isRedHue(avgHueRight)) {
            //right was red
            returnThis += "right=red";
            return returnThis;
        } else if (Vision.isBlueHue(avgHueRight)) {
            returnThis += "right=blue";
            return returnThis;
        } else {
            returnThis += "right=unknown";
            //well that's not good

        }
        return returnThis;

    }
    //DEPRECATED WE DO NOT USE THIS METHOD ANyMORE
    public static String findViaWhiteOutNotWorthyPixelsAndThenFindANonWhiteFromLeftAndSeeColor(Bitmap circies, Context context) {
        // a drop of 150 in all rgb (or at least two) means that relatively, the pixel is black, relative to all the other pixels
        //scan horizontally downward
        //here's how we'll represnt a dot
        //represent it via an arraylist of pixels
        //keep on esarching vertically down until you don't hit a black dot anymore
        //then go from oringial found. go left and down until no more black
        //then go from origninal and go right and down
        //done
        //here's how we'll decide if the surrounding is black: using luminance formula
        //0.2126*R + 0.7152*G + 0.0722*B
        //where 0 is black and 255 is white
        //we'll do less than 90 on luminance scale is considered a black
        //we also need an array list with pixels we already defined as black, so as to not create duplicate black dots with different original dot points
        //ArrayList checkedBlackPixels = new ArrayList<XYCoor>();
        //TODO edit description because it's kinda different now
        //change image to mutable
        circies = circies.copy(Bitmap.Config.ARGB_8888, true);
        String returnThis = "";
        for (int i = 0; i < circies.getHeight(); i++) {
            int numberOfPixelsThatAreNoteWorthy = 0;
            //noteworthy pixels are those within the hue range of blue and pink
            //so around 165 - 190 for blues, and from 0-15 for reds and 295 up for reds
            for (int j = 0; j < circies.getWidth(); j++) {
                //where j is x and i is y, 0,0 is top left corner
                int pixel = circies.getPixel(j, i);
                //pixel is in rgb value. let's break this into hsv values
                // hsv[0] is Hue [0 .. 360)
                // hsv[1] is Saturation [0...1]
                // hsv[2] is Value [0...1]
                float[] hsv = new float[3];
                Color.colorToHSV(pixel, hsv);
                //check hue to see if noteworthy
                if ((1 <= hsv[0] && hsv[0] <= 15) || hsv[0] > 295 || (170 < hsv[0] && hsv[0] < 190)) {
                    //then it is either red or blue, and is noteworthy
                    //note a complete white pixel has hue values of 0, hence the greater than or equal to 1
                    numberOfPixelsThatAreNoteWorthy++;
                }
            }
            //from our testing, about 25% of the row is taken up by the beacon
            //so noteworthiness must cross the 25% threshold
            double percentNoteworthy = (double) (numberOfPixelsThatAreNoteWorthy) / (double) (circies.getWidth());
            Log.e("numPixNote", String.valueOf(numberOfPixelsThatAreNoteWorthy));
            Log.e("total", String.valueOf(circies.getWidth()));
            Log.e("percentNoteWorthy", String.valueOf(percentNoteworthy));
            if (percentNoteworthy < .25) {
                //delete this row
                Log.e("delete!", "Deleting...");
                /*for (int x = 0; x<image.getWidth();x++) {
                    if (x>=image.getWidth()) {
                        x=image.getWidth()-1;
                    }
                    image.setPixel(x,i,Color.argb(255,255,255,255));
                    //set the pixel to white
                    //note a complete white pixel has hue value of 0
                }*/
            }
        }
        //ok so now you have a corrected image.
        //save the image for logging and debugging
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + " EditedWhiteRows";
        File pictureFile = Vision.getOutputMediaFile(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE, timeStamp, context, false);
        if (pictureFile == null) {
            Log.d("ERROR", "Error creating media file, check storage permissions: "
            );
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            circies.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            fos.write(byteArray);
            fos.close();


        } catch (FileNotFoundException e) {
            Log.d("ERROR", "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d("ERROR", "Error accessing file: " + e.getMessage());
        }
        //ok saved
        returnThis = "pathRows:" + "IMG_" + timeStamp + "EditedWhiteRows" + ".jpg";

        //so now all we have left is the bacon and a bunch of white lines, horizontally
        //do the same vertically now, to eliminate all of the excess stuff
        for (int i = 0; i < circies.getWidth(); i++) {
            int numberOfWorthy = 0;
            for (int j = 0; j < circies.getHeight(); j++) {
                int pixel = circies.getPixel(i, j);
                float[] hsv = new float[3];
                Color.colorToHSV(pixel, hsv);
                if ((1 <= hsv[0] && hsv[0] <= 15) || hsv[0] > 295 || (170 < hsv[0] && hsv[0] < 190)) {
                    //then it is either red or blue, and is noteworthy
                    //note a complete white pixel has hue values of 0, hence the greater than or equal to 1
                    numberOfWorthy++;
                }
            }
            double percentNoteworthy = numberOfWorthy / circies.getWidth();
            if (percentNoteworthy < .25) {
                //delete this row
                for (int x = 0; x < circies.getHeight(); x++) {
                    circies.setPixel(i, x, Color.argb(255, 255, 255, 255));
                    //set the pixel to white
                    //note a complete white pixel has hue value of 0
                }
            }
        }
        String timeStamp2 = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + " EditedWhiteAll";
        File pictureFile2 = Vision.getOutputMediaFile(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE, timeStamp, context, false);
        if (pictureFile == null) {
            Log.d("ERROR", "Error creating media file, check storage permissions: "
            );
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            circies.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            fos.write(byteArray);
            fos.close();


        } catch (FileNotFoundException e) {
            Log.d("ERROR", "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d("ERROR", "Error accessing file: " + e.getMessage());
        }
        returnThis = "pathAll:" + "IMG_" + timeStamp + "EditedWhiteAll" + ".jpg";

        //now all we have to do is see what the first hue value from the left is
        for (int i = 0; i < circies.getWidth(); i++) {
            for (int j = 0; j < circies.getHeight(); j++) {
                //go down and move to the right (start from left) until you hit a non white
                int pixel = circies.getPixel(i, j);
                float[] hsv = new float[3];
                Color.colorToHSV(pixel, hsv);
                if (hsv[0] != 0) {
                    //because r=255,g=255,b=255, means hue = 0; (which is white)
                    if (Vision.isBlue(hsv[0], hsv[1], hsv[2])) {
                        returnThis += "firstColorFound:blue XYCoor:" + i + "," + j;
                        return returnThis;
                    } else if (Vision.isRed(hsv[0], hsv[2])) {
                        returnThis += "firstColorFound:red XYCoor:" + i + "," + j;
                        return returnThis;
                    } else {
                        //uhoh, something was wrong
                        returnThis += "error!";
                    }
                }
            }
        }

        return returnThis;
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