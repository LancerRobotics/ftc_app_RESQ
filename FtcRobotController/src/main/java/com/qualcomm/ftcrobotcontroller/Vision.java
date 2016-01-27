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
    public static double EDGE_THRESHOLD = 125;
    public static double LOWER_BOUNDS_BLUE_HUE = 172;
    public static double UPPER_BOUNDS_BLUE_HUE = 240;
    //red is right where the circle turns around
    public static double LOWER_BOUNDS_PINK_HUE = 290;
    public static double UPPER_BOUNDS_RED_HUE = 15;
    public static double UPPER_BOUNDS_BLUE_VIBRANCY = 50;
    public static double UPPER_BOUNDS_BLUE_SATURATION = 50;
    public static double UPPER_BOUNDS_RED_VIBRANCY = 80;
    //these values are all based off of the color wheel
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
        if (isRedHue(avgHueLeft)) {
            //left was red
            returnThis+="left=red";
            return returnThis;
        }
        else if (isBlueHue(avgHueLeft)) {
            returnThis+="left=blue";
            return returnThis;
        }
        else {
            returnThis+="left=unknown";
        }
        //if you're here, it means we're unknown
        //so let's check the right side
        if (isRedHue(avgHueRight)) {
            //right was red
            returnThis+="right=red";
            return returnThis;
        }
        else if (isBlueHue(avgHueRight)) {
            returnThis+="right=blue";
            return returnThis;
        }
        else {
            returnThis+="right=unknown";
            //well that's not good

        }
        return returnThis;

    }
    public static boolean isRedHue (double hue) {
        //returns if the color is red, based solely on hue. inaccuracies with saturataion/vibrancy variables
        return 0 < hue && hue < UPPER_BOUNDS_RED_HUE || hue > LOWER_BOUNDS_PINK_HUE;
    }
    public static boolean isBlueHue (double hue) {
        //returns if the color is blue, based solely on hue. inaccuracies with saturataion/vibrancy variables
        return LOWER_BOUNDS_BLUE_HUE < hue && hue < UPPER_BOUNDS_BLUE_HUE;
    }

    public static boolean isRed (double hue, double V) {
        //it is too dark to be red, lightness doesnt really matter for red since lighter red is pink which is basically red
        return V >= UPPER_BOUNDS_BLUE_VIBRANCY && (0 < hue && hue < UPPER_BOUNDS_RED_HUE || hue > LOWER_BOUNDS_PINK_HUE);
        /* UNSIMPLIFIED CODE cuz this makes more sense then the random stuff up there
        if (V<UPPER_BOUNDS_BLUE_VIBRANCY) {
            //it is too dark to be red, lightness doesnt really matter for red since lighter red is pink which is basically red
            return false;
        }
        if (0<hue&&hue<UPPER_BOUNDS_RED_HUE||hue>LOWER_BOUNDS_PINK_HUE) {
            //it's red
            return true;
        }
        return false;
         */
    }
    public static boolean isBlue (double hue, double S, double V) {
        //becuase blue can be less vibrant and still be blue, as well as lighter and still be blue, there's more lenienacy for "blue"
        return !(V < UPPER_BOUNDS_BLUE_VIBRANCY && S < UPPER_BOUNDS_BLUE_SATURATION) && LOWER_BOUNDS_BLUE_HUE < hue && hue < UPPER_BOUNDS_BLUE_HUE;
        /* UNSIMPLIFIED CODE (because this makes more sense then the random stuff up there.
        if (V<UPPER_BOUNDS_BLUE_VIBRANCY&& S<UPPER_BOUNDS_BLUE_SATURATION) {

            return false;
            //becuase blue can be less vibrant and still be blue, as well as lighter and still be blue, there's more lenienacy for "blue"

        }
        if (LOWER_BOUNDS_BLUE_HUE<hue&&hue<UPPER_BOUNDS_BLUE_HUE) {
            // it's blue
            return true;
        }
        return false;
        */
    }

    public static double toLum (int r, int g, int b) {
        //luminace returns between 0, 255, 0 being black and 255 being white
        //formula from https://github.com/rayning0/Princeton-Algorithms-Java/blob/master/introcs/Luminance.java
        return .299*r + .587*g + .114*b;
    }

    public static Bitmap toGrayscaleBitmap (Bitmap original) {

        Bitmap palet = original.copy(Bitmap.Config.ARGB_8888,true);
        for (int i =0; i <palet.getWidth();i++ ) {
            for (int j = 0;j <palet.getHeight();j++) {
                //getPixel returns a color int of the pixel
                int red = Color.red(palet.getPixel(i,j));
                int green = Color.green(palet.getPixel(i,j));
                int blue = Color.blue(palet.getPixel(i,j));
                int lum = (int)(Math.round(toLum(red,green,blue)));
                palet.setPixel(i,j,Color.argb(255,lum,lum,lum));
            }
        }
        return palet;

    }

    public static Bitmap convertGrayscaleToEdged (Bitmap grayscale) {
        //sort through image matrix pixxel by pixel
        //for each pixel, analyze each of the 8 pixels surrounding it

        //record the value of the darkest pixel, and the lightest pixel

        // if (darkest_pixel_value - lightest_pixel_value) > threshold)
        //then rewrite that pixel as 1;
        //else rewrite that pixel as 0;
        //255 = white
        //0 = dark;
        //so we need to find max and min
        Bitmap clean = Bitmap.createBitmap(grayscale.getWidth(),grayscale.getHeight(), Bitmap.Config.ARGB_8888);
        //make entire bitmap white
        for (int i = 0; i<clean.getWidth();i++) {
            for (int j  = 0 ; j <grayscale.getHeight();j++) {
                clean.setPixel(i,j,Color.WHITE);
            }
        }
        Log.e("Done with clean","DONE");
       int label = 1;
        for (int i = 0; i <grayscale.getWidth();i++) {
            for (int j  = 0 ; j <grayscale.getHeight();j++) {
                int max = 0;
                int min = 255;
                for (int a = -1; a<=1;a++) {
                    for (int b =-1;b<=1;b++) {
                        int x = i+a;
                        int y = j+b;
                        //check bottom left first, then moves down, then next column
                        //greyscale = all rgb values are equal to luminace value
                        if (!(x<0||y<0||x>=grayscale.getWidth()||y>=grayscale.getHeight()||(a==0&&b==0))) {
                            //if not any of these conditions, then it's a real pixel
                            //get the lum and see if it's a max or min
                            int pixel = grayscale.getPixel(x,y);
                            int lum = Color.red(pixel);
                            if (lum>max) {
                                max = lum;
                            }
                            if (lum<min) {
                                min = lum;
                            }
                        }
                    }
                }
                //ok so now u set max/min for this group of surrounding pixels
                //if darkest - lightest > threshold, then it's an edge
                if (max - min > EDGE_THRESHOLD) {
                    //his is an edge
                    Log.e("Edge found!",""+i+","+j);
                    //mark i,j as an edge

                    //if neighbours are unlabled, label pixel with label. increment label by one.
                    //else label the neighbor label
                    boolean neighborHasLabel = false;
                    for (int a = -1; a<=1;a++) {
                        for (int b = -1; b <= 1; b++) {
                            int x = i+a;
                            int y = j+b;
                            if (!(x<0||y<0||x>=grayscale.getWidth()||y>=grayscale.getHeight()||(a==0&&b==0))) {
                                int pixel = clean.getPixel(x, y);
                                if (Color.red(pixel) != 255) {
                                    //if neighbor pixel is not 0, then it has a label
                                    //if it finds itself, it should still be equal to 0 so no problems there
                                    clean.setPixel(i, j, Color.argb(255,Color.red(pixel),Color.red(pixel),Color.red(pixel)) );
                                    Log.e("label", "neighbor had a label: " + Color.red(pixel));
                                    neighborHasLabel = true;
                                }
                            }
                        }
                    }
                    //if after checking all labels, neighborHasLabel is still fallse, then sit it to label and icrement label
                    if (neighborHasLabel==false) {
                        clean.setPixel(i,j,Color.argb(255,label,label,label));
                        label++;
                    }
                }


            }
        }
        //ok so now you have a graph with a bunch of labeled pixels.
        if (label>255) {
            //we have a problem because then we cant scale values to different shades of grade
            Log.e("ERROR","label overflow"+label);
        }
        else if (label==50) {
            Log.e("LOL","got fifty shades of gray");
        }
        else {
            //shade the gray
            //shade it to 255/label
            //ok so now you've found edge, and successfully labeled it
            for (int i = 0; i <grayscale.getWidth();i++) {
                for (int j = 0; j < grayscale.getHeight(); j++) {
                    if (Color.red(clean.getPixel(i,j))!=255) {
                        //if it's not white, dont do this
                        Log.e("pixel", "red" + Color.red(clean.getPixel(i, j)) + "blue" + Color.blue(clean.getPixel(i, j)) + "colorint" + clean.getPixel(i, j));
                        int shade = (255 / (label + 1)) * Color.red(clean.getPixel(i, j));
                        Log.e("shade", shade + "" + "label"+label+" redCol"+Color.red(clean.getPixel(i,j)));
                        clean.setPixel(i, j, Color.argb(255, shade, shade, shade));
                        Log.e("clean shade", Color.red(clean.getPixel(i, j)) + "first" + Color.blue(clean.getPixel(i, j)) + "second");
                    }
                }
            }

        }
        //beautiful. now you've got a shaded grayed image.
        return clean;   //not clean anymore doe
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
            double percentNoteworthy = (double)(numberOfPixelsThatAreNoteWorthy)/(double)(image.getWidth());
            Log.e("numPixNote",String.valueOf(numberOfPixelsThatAreNoteWorthy));
            Log.e("total",String.valueOf(image.getWidth()));
            Log.e("percentNoteWorthy",String.valueOf(percentNoteworthy));
            if (percentNoteworthy<.25) {
                //delete this row
                Log.e("delete!","Deleting...");
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
                int pixel = image.getPixel(i,j);
                float[] hsv = new float[3];
                Color.colorToHSV(pixel, hsv);
                if (hsv[0]!=0) {
                    //because r=255,g=255,b=255, means hue = 0; (which is white)
                    if (isBlue(hsv[0], hsv[1], hsv[2])) {
                        returnThis+="firstColorFound:blue XYCoor:"+i+","+j;
                        return returnThis;
                    }
                    else if (isRed(hsv[0],hsv[2])) {
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

    public static String savePicture (Bitmap bitmap, Context context, String tag) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date())+tag;
        File pictureFile = getOutputMediaFile(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE,timeStamp, context);
        if (pictureFile == null){
            Log.d("ERROR", "Error creating media file, check storage permissions: "
            );
            return "ERROR";
        }
        try {

            FileOutputStream fos = new FileOutputStream(pictureFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,fos);
            fos.close();


        } catch (FileNotFoundException e) {
            Log.d("ERROR", "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d("ERROR", "Error accessing file: " + e.getMessage());
        }
        return pictureFile.getName();
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

    public static Bitmap filter(Bitmap edge) {
        int white = 255;
        for (int r=0; r<edge.getWidth(); r++) {
            for(int c=0; c<edge.getHeight(); c++) {
                for (int a = -1; a<=1;a++) {
                    for (int b = -1; b <= 1; b++) {
                        int x = r + a;
                        int y = c + b;
                        if (!(x < 0 || y < 0 || x >= edge.getWidth() || y >= edge.getHeight() || (a == 0 && b == 0))) {
                            //check for neighboring edges
                            //if there are less than 3 then delete
                            //idk how to do this so I just put the for loops to get through the pixels
                            //MAAAAATTTTT
                        }
                    }
                }
            }
        }
        return edge;
    }

}