package com.qualcomm.ftcrobotcontroller;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Matt on 12/27/2015.
 */
public class Vision {

    public static String findViaSplitImageInHalfAndSeeWhichColorIsOnWhichSide (Bitmap image) {
        //find the avg hue for right side, find avg hue for left side
        //compare
        //blue hues tend to be unvariably 180, so like 170 - 190
        //red hues tend to be pink or purple, so somehwere around 295 higher (inaccurate from our testing). but, they can also be very low numbereed hued reds so like 15
        //so basically we can say that blues will be less than reds, since the red comes out as pink
        int xMidPoint = image.getWidth()/2;
        double  avgHueLeft = 0;
        int pixelCounter = 0;
        for (int i = 0;i <xMidPoint;i++) {
            for (int j = 0; j<image.getHeight();j++) {
                int pixel = image.getPixel(i,j);
                //pixel is in rgb value. let's break this into hsv values
                // hsv[0] is Hue [0 .. 360)
                // hsv[1] is Saturation [0...1]
                // hsv[2] is Value [0...1]
                pixelCounter++;
                float[] hsv = new float[3];
                Color.colorToHSV(pixel, hsv);
                avgHueLeft+=hsv[0];
            }
        }
        //done getting left side
        avgHueLeft = avgHueLeft/pixelCounter;
        //let's do for right side
        double avgHueRight = 0;
        pixelCounter = 0;
        for (int i = image.getWidth()-1; i>xMidPoint;i--) {
            for (int j =0; j<image.getHeight();j++) {
                int pixel = image.getPixel(i,j);
                //pixel is in rgb value. let's break this into hsv values
                // hsv[0] is Hue [0 .. 360)
                // hsv[1] is Saturation [0...1]
                // hsv[2] is Value [0...1]
                pixelCounter++;
                float[] hsv = new float[3];
                Color.colorToHSV(pixel, hsv);
                avgHueRight+=hsv[0];
            }
        }
        //done getting right side
        //compare and return
        String returnThis = "avgHueLeft:"+avgHueLeft+"avgHueRight:"+avgHueRight;
        if (isRed(avgHueLeft)) {
            //left was red
            returnThis+="left=red";
            return returnThis;
        }
        else if (isBlue(avgHueLeft)) {
            returnThis+="left=blue";
            return returnThis;
        }
        else {
            returnThis+="left=unknown";
        }
        //if you're here, it means we're unknown
        //so let's check the right side
        if (isRed(avgHueRight)) {
            //right was red
            returnThis+="right=red";
            return returnThis;
        }
        else if (isBlue(avgHueRight)) {
            returnThis+="right=blue";
            return returnThis;
        }
        else {
            returnThis+="right=unknown";
            //well that's not good

        }
        return returnThis;

    }

    public static boolean isRed (double hue) {
        if (0<hue&&hue<15||hue>295) {
            //it's red
            return true;
        }
        return false;
    }
    public static boolean isBlue (double hue) {
        if (170<hue&&hue<190) {
            // it's blue
            return true;
        }
        return false;
    }

    public static String findViaWhiteOutNotWorthyPixelsAndThenFindANonWhiteFromLeftAndSeeColor (Bitmap image, Context context) {
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
        image = image.copy(Bitmap.Config.ARGB_8888,true);
        String returnThis ="";
        for (int i = 0; i<image.getHeight(); i++) {
            int numberOfPixelsThatAreNoteWorthy = 0;
            //noteworthy pixels are those within the hue range of blue and pink
            //so around 165 - 190 for blues, and from 0-15 for reds and 295 up for reds
            for (int j = 0; j<image.getWidth();j++) {
                //where j is x and i is y, 0,0 is top left corner
                int pixel = image.getPixel(j,i);
                //pixel is in rgb value. let's break this into hsv values
                // hsv[0] is Hue [0 .. 360)
                // hsv[1] is Saturation [0...1]
                // hsv[2] is Value [0...1]
                float[] hsv = new float[3];
                Color.colorToHSV(pixel, hsv);
                //check hue to see if noteworthy
                if ((1<=hsv[0]&&hsv[0]<=15)||hsv[0]>295||(170<hsv[0]&&hsv[0]<190)) {
                    //then it is either red or blue, and is noteworthy
                    //note a complete white pixel has hue values of 0, hence the greater than or equal to 1
                    numberOfPixelsThatAreNoteWorthy++;
                }
            }
            //from our testing, about 25% of the row is taken up by the beacon
            //so noteworthiness must cross the 25% threshold
            double percentNoteworthy = numberOfPixelsThatAreNoteWorthy/image.getWidth();
            if (percentNoteworthy<.25) {
                //delete this row
                for (int x = 0; x<image.getWidth();x++) {
                    if (x>=image.getWidth()) {
                        x=image.getWidth()-1;
                    }
                    image.setPixel(x,i,Color.argb(255,255,255,255));
                    //set the pixel to white
                    //note a complete white pixel has hue value of 0
                }
            }
        }
        //ok so now you have a corrected image.
        //save the image for logging and debugging
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date())+" EditedWhiteRows";
        File pictureFile = getOutputMediaFile(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE,timeStamp, context);
        if (pictureFile == null){
            Log.d("ERROR", "Error creating media file, check storage permissions: "
            );
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            fos.write(byteArray);
            fos.close();


        } catch (FileNotFoundException e) {
            Log.d("ERROR", "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d("ERROR", "Error accessing file: " + e.getMessage());
        }
        //ok saved
        returnThis = "pathRows:"+"IMG_" + timeStamp+ "EditedWhiteRows"+".jpg";

        //so now all we have left is the bacon and a bunch of white lines, horizontally
        //do the same vertically now, to eliminate all of the excess stuff
        for (int i =0;i<image.getWidth();i++) {
            int numberOfWorthy = 0;
            for (int j = 0;j<image.getHeight();j++) {
                int pixel = image.getPixel(i,j);
                float[] hsv = new float[3];
                Color.colorToHSV(pixel, hsv);
                if ((1<=hsv[0]&&hsv[0]<=15)||hsv[0]>295||(170<hsv[0]&&hsv[0]<190)) {
                    //then it is either red or blue, and is noteworthy
                    //note a complete white pixel has hue values of 0, hence the greater than or equal to 1
                    numberOfWorthy++;
                }
            }
            double percentNoteworthy = numberOfWorthy/image.getWidth();
            if (percentNoteworthy<.25) {
                //delete this row
                for (int x = 0; x<image.getHeight();x++) {
                    image.setPixel(i,x,Color.argb(255,255,255,255));
                    //set the pixel to white
                    //note a complete white pixel has hue value of 0
                }
            }
        }
        String timeStamp2 = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date())+" EditedWhiteAll";
        File pictureFile2 = getOutputMediaFile(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE,timeStamp, context);
        if (pictureFile == null){
            Log.d("ERROR", "Error creating media file, check storage permissions: "
            );
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            fos.write(byteArray);
            fos.close();


        } catch (FileNotFoundException e) {
            Log.d("ERROR", "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d("ERROR", "Error accessing file: " + e.getMessage());
        }
        returnThis = "pathAll:"+"IMG_" + timeStamp+ "EditedWhiteAll"+".jpg";

        //now all we have to do is see what the first hue value from the left is
        for (int i =0; i<image.getWidth();i++) {
            for (int j = 0; j<image.getHeight();j++) {
                //go down and move to the right (start from left) until you hit a non white
                int pixel = image.getPixel(j,i);
                float[] hsv = new float[3];
                Color.colorToHSV(pixel, hsv);
                if (hsv[0]!=0) {
                    //because r=255,g=255,b=255, means hue = 0; (which is white)
                    if (isBlue(hsv[0])) {
                        returnThis+="firstColorFound:blue XYCoor:"+i+","+j;
                        return returnThis;
                    }
                    else if (isRed(hsv[0])) {
                        returnThis+= "firstColorFound:red XYCoor:"+i+","+j;
                        return returnThis;
                    }
                    else {
                        //uhoh, something was wrong
                        returnThis+="error!";
                    }
                }
            }
        }

        return returnThis;
    }
    private static File getOutputMediaFile(int type, String timeStamp, Context context) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Matt Quan is a boss");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.e("MyCameraApp", "failed to create directory");
                return null;
            }
        }
        // Create a media file name

        File mediaFile;
        if (type == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE){
            String path = mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp+".jpg";
            Log.e("savedPath",path);
            SharedPreferences prefs = context.getApplicationContext().getSharedPreferences(
                    "com.quan.companion", Context.MODE_PRIVATE);
            prefs.edit().putString(Keys.pictureImagePathSharedPrefsKeys,path).apply();
            Log.e("saved path","saved path in shared prefs");
            mediaFile = new File(path);

        } else {
            return null;
        }

        return mediaFile;
    }
}