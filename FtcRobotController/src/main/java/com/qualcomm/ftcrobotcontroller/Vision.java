package com.qualcomm.ftcrobotcontroller;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by Matt on 12/27/2015.
 */
public class Vision {
    public static final int FIRST_LABEL = 0;
    public static final int MIN_NEEDED_TO_BE_AN_EDGE = 15;
    public static final int DIFFERENCE_IN_RADIUS_FOR_RECTANGLE_BOUNDS = 1;
    public static final double TOLERANCE_FOR_RADIUS_DIFFERENCE = .7;
    public static final int MIN_RADIUS_LENGTH = 6;
    public static final int MAX_RADIUS_LENGTH = 13;


    public static int FOCUS_TIME = 2400;
    public static int RETRIEVE_FILE_TIME = FOCUS_TIME + 2000;

    public static int EDGE_THRESHOLD = 85;
    public static double CONTRAST_ADJUSTMENT = .85;
    public static int BRIGHTNESS_ADJUSTMENT = 0;
    public static double LOWER_BOUNDS_BLUE_HUE = 172;
    public static double UPPER_BOUNDS_BLUE_HUE = 259;
    //red is right where the circle turns around
    public static double LOWER_BOUNDS_PINK_HUE = 290;
    public static double UPPER_BOUNDS_RED_HUE = 16;
    public static double UPPER_BOUNDS_BLUE_VIBRANCY = 50;
    public static double UPPER_BOUNDS_BLUE_SATURATION = 50;
    public static double UPPER_BOUNDS_RED_VIBRANCY = 80;
    public static int CONVERTGRAYSCALETOEDGED_DATA_NUMBER_OF_LABELS = 0;
    public static int CONVERTGRAYSCALETOEDGED_DATA_BITMAP = 1;
    public static int CONVERTGRAYSCALETOEDGED_DATA_EDGETHRESHOLDUSED=2;
    public static int CONSOLIDATEEDGES_DATA_NUMBEROFCHANGES = 0;
    public static int CONSOLIDATEEDGES_DATA_TOTALLABELS = 1;
    public static int CONSOLDIATEEDGES_DATA_BITMAP = 2;
    public static int REMOVERANDOMNESS_DATA_LABELS = 0;
    public static  int REMOVERANDOMNESS_DATA_BITMAP = 1 ;
    public static int RETURNCIRCLES_DATA_BITMAP = 0;
    public static int RETURNCIRCLES_DATA_XYCOORSCENTER = 1;
    public static int RETURNCIRCLES_DATA_LABELSLIST = 2;
    public static int CHECKCOLORS_DATA_RED = 0;
    public static int CHECKCOLORS_DATA_BLUE = 1;
    public static double THRESHOLD_FOR_CENTERS_OF_TWO_BUTTONS = 5;

    public static Bitmap rotate (Bitmap image) {
        Matrix matrix = new Matrix();
        matrix.postRotate(-90);
        return Bitmap.createBitmap(image, 0,0,image.getWidth(),image.getHeight(),matrix,true);
    }

    public static boolean isRedHue(double hue) {
        //returns if the color is red, based solely on hue. inaccuracies with saturataion/vibrancy variables
        return 0 < hue && hue < UPPER_BOUNDS_RED_HUE || hue > LOWER_BOUNDS_PINK_HUE;
    }

    public static boolean isBlueHue(double hue) {
        //returns if the color is blue, based solely on hue. inaccuracies with saturataion/vibrancy variables
        return LOWER_BOUNDS_BLUE_HUE < hue && hue < UPPER_BOUNDS_BLUE_HUE;
    }

    public static boolean isRed(double hue, double V) {
        //it is too dark to be red, lightness doesnt really matter for red since lighter red is pink which is basically red
        return V >= UPPER_BOUNDS_RED_VIBRANCY && (0 < hue && hue < UPPER_BOUNDS_RED_HUE || hue > LOWER_BOUNDS_PINK_HUE);
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
    public static Bitmap fastblur(Bitmap sentBitmap, int radius) {
        Bitmap bitmap = sentBitmap.copy(Bitmap.Config.ARGB_8888, true);

        if (radius < 1) {
            return (null);
        }

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] pix = new int[w * h];
        Log.e("pix", w + " " + h + " " + pix.length);
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int dv[] = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = ( 0xff000000 & pix[yi] ) | ( dv[rsum] << 16 ) | ( dv[gsum] << 8 ) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }
        Log.e("pix", w + " " + h + " " + pix.length);
        bitmap.setPixels(pix, 0, w, 0, 0, w, h);

        return (bitmap);
    }
    public static boolean isBlue(double hue, double S, double V) {
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

    public static double toLum(int r, int g, int b) {
        //luminace returns between 0, 255, 0 being black and 255 being white
        //formula from https://github.com/rayning0/Princeton-Algorithms-Java/blob/master/introcs/Luminance.java
        return .299 * r + .587 * g + .114 * b;
    }

    public static Bitmap toGrayscaleBitmap(Bitmap original) {

        Bitmap palet = original.copy(Bitmap.Config.ARGB_8888, true);
        for (int i = 0; i < palet.getWidth(); i++) {
            for (int j = 0; j < palet.getHeight(); j++) {
                //getPixel returns a color int of the pixel
                int red = Color.red(palet.getPixel(i, j));
                int green = Color.green(palet.getPixel(i, j));
                int blue = Color.blue(palet.getPixel(i, j));
                int lum = (int) (Math.round(toLum(red, green, blue)));
                palet.setPixel(i, j, Color.argb(255, lum, lum, lum));
            }
        }
        return palet;

    }

    public static Bitmap filterRedBlue (Bitmap original) {
        original = original.copy(Bitmap.Config.ARGB_8888,true);
        for (int i =0; i <original.getWidth();i++) {
            for (int j =0; j<original.getHeight();j++) {
                int pixel = original.getPixel(i,j);
                float[] hsv = new float[3];
                Color.RGBToHSV(Color.red(pixel), Color.green(pixel), Color.blue(pixel), hsv);
                float hue = hsv[0];

                if (!isRedHue(hue)&&!isBlueHue(hue)) {
                    original.setPixel(i,j,Color.argb(255,255,255,255));
                }

            }
        }
        return original;
    }

    public static Beacon getBeacon (Bitmap circles, Bitmap original) {
        //precondition that it is either one circle or two
        //get a list of the edges
        if (getNumberOfLabelsNotOrganized(circles)==0) {
            return new Beacon();
        }
        ArrayList<Integer> listOfLabelsInPicture = new ArrayList<Integer>();
        for (int i =0; i <circles.getWidth();i++) {
            for (int j= 0 ;j <circles.getHeight();j++) {
                int label = Color.red(circles.getPixel(i,j));
                if (label!=255) {
                    //check if this label is in the list
                    if (!listOfLabelsInPicture.contains(label)) {
                        listOfLabelsInPicture.add(label);
                    }
                }
            }
        }

        if (listOfLabelsInPicture.size()==1) {
            //grab a pixel with that label
            XYCoor picked = new XYCoor(-1, -1);
            outerloop:
            for (int x = 0; x < circles.getWidth(); x++) {
                for (int y = 0; y < circles.getHeight(); y++) {
                    if (Color.red(circles.getPixel(x, y)) == listOfLabelsInPicture.get(0)) {
                        //cool i foud it
                        picked = new XYCoor(x, y);
                        break outerloop;
                    }
                }
            }
            //retrieve pixel value. check around for average hue
            float averageHue = 0;
            for (int a = -1; a <= 1; a++) {
                for (int b = -1; b <= 1; b++) {
                    //get average from original picture
                    int pixel = original.getPixel((int) picked.getX() + a, (int) picked.getY() + b);
                    float[] hsv = new float[3];
                    Color.RGBToHSV(Color.red(pixel), Color.green(pixel), Color.blue(pixel), hsv);
                    float hue = hsv[0];
                    //becuase hue of red can be 0, and then an average of 360 and 0 creates  blue color, we have to translate anything that is on the left half of red to the right side by adding 360
                    if (hsv[0] < UPPER_BOUNDS_RED_HUE) {
                        hue += 360;
                    }
                    averageHue += hue;
                }

            }
            averageHue = averageHue / 9;
            //now get this color and set it equal to the right beacon
            Beacon beacon = new Beacon();
            if (isRedHue(averageHue)) {
                //assumption that the left one must've gotten cu toff
                beacon.setRight(Beacon.COLOR_RED);
            }
            else if (isBlueHue(averageHue)) {
                beacon.setRight(Beacon.COLOR_BLUE);
            }
            return beacon;

        }

        Log.e("listOfLabelsInPicture",listOfLabelsInPicture.toString());
        XYCoor[] myGuienaPigs = new XYCoor[2];
        for (int i = 0; i <listOfLabelsInPicture.size();i++) {
            //get my label
            int label = listOfLabelsInPicture.get(i);
            outerloop:
            for (int x= 0; x<circles.getWidth();x++) {
                for (int y = 0; y<circles.getHeight();y++) {
                    if (Color.red(circles.getPixel(x,y))==label) {
                        //cool i foud it
                        myGuienaPigs[i]= new XYCoor(x,y);
                        break outerloop;
                    }
                }
            }
        }

        //sort array with x axis left being in hole 0
        if (myGuienaPigs[0].getX()>myGuienaPigs[1].getX()) {
            //swap
            XYCoor holder = myGuienaPigs[0];
            myGuienaPigs[0]=myGuienaPigs[1];
            myGuienaPigs[1]=holder;
            Log.e("myGuienaPigs","swapped");
        }
        Log.e("myGuienaPigs", Arrays.toString(myGuienaPigs));
        double averageHues[] = new double[2];
        for (int i=0;i<myGuienaPigs.length;i++) {
            //retrieve pixel value. check around for average hue
            for (int a = -1; a <= 1; a++) {
                for (int b = -1; b <= 1; b++) {
                    //get average from original picture
                    int pixel = original.getPixel((int) myGuienaPigs[i].getX() + a, (int) myGuienaPigs[i].getY() + b);
                    float[] hsv = new float[3];
                    Color.RGBToHSV(Color.red(pixel),Color.green(pixel),Color.blue(pixel),hsv);
                    float hue = hsv[0];
                    //becuase hue of red can be 0, and then an average of 360 and 0 creates  blue color, we have to translate anything that is on the left half of red to the right side by adding 360
                    if (hsv[0]<UPPER_BOUNDS_RED_HUE) {
                        hue+=360;
                    }
                    averageHues[i] +=hue;
                }
            }
            averageHues[i]=averageHues[i]/9.0;
        }
        Log.e("averageHues", Arrays.toString(averageHues));
        Beacon beacon = new Beacon();
        if (isRedHue(averageHues[0])) {
            Log.e("colored circles","left is red");
            beacon.setLeft(Beacon.COLOR_RED);
        }
        else if (isBlueHue(averageHues[0])) {
            Log.e("colored circles","left is blue");
            beacon.setLeft(Beacon.COLOR_BLUE);
        }
        else {
            Log.e("colored circles","left is unknown");

        }
        if (isRedHue(averageHues[1])) {
            Log.e("colored circles","right is red");
            beacon.setRight(Beacon.COLOR_RED);
        }
        else if(isBlueHue(averageHues[1])) {
            Log.e("colored circles","right is blue");
            beacon.setRight(Beacon.COLOR_BLUE);
        }
        else {
            Log.e("colored circles","right is unknown");
        }

        return beacon;
    }
    public static ArrayList<Object> convertGrayscaleToEdged(Bitmap grayscale, int edgeThresholdUsed) {
        //sort through image matrix pixxel by pixel
        //for each pixel, analyze each of the 8 pixels surrounding it

        //record the value of the darkest pixel, and the lightest pixel

        // if (darkest_pixel_value - lightest_pixel_value) > threshold)
        //then rewrite that pixel as 1;
        //else rewrite that pixel as 0;
        //255 = white
        //0 = dark;
        //so we need to find max and min
        Bitmap clean = Bitmap.createBitmap(grayscale.getWidth(), grayscale.getHeight(), Bitmap.Config.ARGB_8888);
        //make entire bitmap white
        for (int i = 0; i < clean.getWidth(); i++) {
            for (int j = 0; j < grayscale.getHeight(); j++) {
                clean.setPixel(i, j, Color.WHITE);
            }
        }
        //Log.e("Done with clean", "DONE");
        int label = Vision.FIRST_LABEL;
        for (int i = 0; i < grayscale.getWidth(); i++) {
            for (int j = 0; j < grayscale.getHeight(); j++) {
                int max = 0;
                int min = 255;
                for (int a = -1; a <= 1; a++) {
                    for (int b = -1; b <= 1; b++) {
                        int x = i + a;
                        int y = j + b;
                        //check bottom left first, then moves down, then next column
                        //greyscale = all rgb values are equal to luminace value
                        if (!(x < 0 || y < 0 || x >= grayscale.getWidth() || y >= grayscale.getHeight() || (a == 0 && b == 0))) {
                            //if not any of these conditions, then it's a real pixel
                            //get the lum and see if it's a max or min
                            int pixel = grayscale.getPixel(x, y);
                            int lum = Color.red(pixel);
                            if (lum > max) {
                                max = lum;
                            }
                            if (lum < min) {
                                min = lum;
                            }
                        }
                    }
                }
                //ok so now u set max/min for this group of surrounding pixels
                //if darkest - lightest > threshold, then it's an edge
                if (max - min > edgeThresholdUsed) {
                    //his is an edge
                    //mark i,j as an edge

                    //if neighbours are unlabled, label pixel with label. increment label by one.
                    //else label the neighbor label
                    boolean neighborHasLabel = false;
                    for (int a = -1; a <= 1; a++) {
                        for (int b = -1; b <= 1; b++) {
                            int x = i + a;
                            int y = j + b;
                            if (!(x < 0 || y < 0 || x >= grayscale.getWidth() || y >= grayscale.getHeight() || (a == 0 && b == 0))) {
                                int pixel = clean.getPixel(x, y);
                                if (Color.red(pixel) != 255) {
                                    //if neighbor pixel is not 0, then it has a label
                                    //if it finds itself, it should still be equal to 0 so no problems there
                                    clean.setPixel(i, j, Color.argb(255, Color.red(pixel), Color.red(pixel), Color.red(pixel)));
                                    neighborHasLabel = true;
                                }
                            }
                        }
                    }
                    //if after checking all labels, neighborHasLabel is still fallse, then sit it to label and icrement label
                    if (neighborHasLabel == false) {
                        clean.setPixel(i, j, Color.argb(255, label, label, label));
                        label++;
                    }
                }


            }
        }
        ArrayList<Object> data = new ArrayList();
        //let the first bitmap be raw, second be made pretty
        data.add(CONVERTGRAYSCALETOEDGED_DATA_NUMBER_OF_LABELS, label);
        //add in a raw edge with no shading
        //ok so now you have a graph with a bunch of labeled pixels.
        if (label > 255) {
            //we have a problem because then we cant scale values to different shades of grade
            Log.e("ERROR", "label overflow" + label);
        } else if (label == 50) {
            Log.e("LOL", "got fifty shades of gray");
        } /*else {
            //shade the gray
            //shade it to 255/label
            //ok so now you've found edge, and successfully labeled it
            for (int i = 0; i < grayscale.getWidth(); i++) {
                for (int j = 0; j < grayscale.getHeight(); j++) {
                    if (Color.red(clean.getPixel(i, j)) != 255) {
                        //if it's not white, dont do this
                        int shade = (255 / (label + 1)) * Color.red(clean.getPixel(i, j));
                        clean.setPixel(i, j, Color.argb(255, shade, shade, shade));
                    }
                }
            }

        }*/
        //beautiful. now you've got a shaded grayed image. EDITED: WE DONT WANT BEAUTY ANYMORE, it was screwing around. plus black = 0 so whatever
        data.add(CONVERTGRAYSCALETOEDGED_DATA_BITMAP, clean);
        data.add(CONVERTGRAYSCALETOEDGED_DATA_EDGETHRESHOLDUSED, edgeThresholdUsed);
        return data;   //not clean anymore doe
    }

    public static Bitmap applyContrastBrightnessFilter(Bitmap coloredImage, double contrastFactor, int brightnessFactor) {
        //f(x) = ax + B, where a>1 means more contrast and 0<a<1 means less contrast
        //b = brightness
        //for easier separation of "brightness" and contrast" do
        //f(x) = a(x-128) +128+b, where x = rgb value
        // contrast correction factor = 259(C+255)/ 255(259-c)
        //where C = the desried level of contrast, negative = less contrast
        //formula from http://math.stackexchange.com/questions/906240/algorithms-to-increase-or-decrease-the-contrast-of-an-image
        //and http://www.dfstudios.co.uk/articles/programming/image-programming-algorithms/image-processing-algorithms-part-5-contrast-adjustment/
        Bitmap mutableEdited = coloredImage.copy(Bitmap.Config.ARGB_8888, true);
        for (int i = 0; i < mutableEdited.getWidth(); i++) {
            for (int j = 0; j < mutableEdited.getHeight(); j++) {
                int red = Color.red(coloredImage.getPixel(i, j));
                int green = Color.green(coloredImage.getPixel(i, j));
                int blue = Color.blue(coloredImage.getPixel(i, j));

                int improvedRed = (int) (contrastFactor * (red - 128) + 128 + brightnessFactor);
                int improvedBlue = (int) (contrastFactor * (blue - 128) + 128 + brightnessFactor);
                int improvedGreen = (int) (contrastFactor * (green - 128) + 128 + brightnessFactor);
                mutableEdited.setPixel(i, j, Color.argb(255, improvedRed, improvedGreen, improvedBlue));

            }
        }
        return mutableEdited;
    }

    public static ArrayList<Object> consolidateEdges(Bitmap clumpy) {
        clumpy = clumpy.copy(Bitmap.Config.ARGB_8888,true);
        boolean didIMakeAnEdit;
        int numberOfChanges = 0;
        do {
            //before, reset didImake an edit. don't know if I made an edit yet.
            didIMakeAnEdit=false;
            for (int i = 0; i < clumpy.getWidth(); i++) {
                for (int j = 0; j < clumpy.getHeight(); j++) {
                    if (Color.red(clumpy.getPixel(i, j)) != 255) {
                        //if the pixel color is not white, meaning that it IS an edge
                        //check neighboring pixels and set it to lowest "label" value
                        int minLabel = Color.red(clumpy.getPixel(i, j));
                        for (int a = -1; a <= 1; a++) {
                            for (int b = -1; b <= 1; b++) {
                                int x = i + a;
                                int y = j + b;
                                if (!(x < 0 || y < 0 || x >= clumpy.getWidth() || y >= clumpy.getHeight() || (a == 0 && b == 0))) {
                                    //make sure no errors thrown
                                    int neighborsLabel = Color.red(clumpy.getPixel(x, y));
                                    //Log.e("data","neighbor"+neighborsLabel+"min"+minLabel);
                                    if (neighborsLabel < minLabel) {
                                        minLabel = neighborsLabel;
                                        //i made a change
                                        didIMakeAnEdit = true;
                                    }
                                }
                            }
                        }
                        //great now we've got smallest label surrounding
                        if (didIMakeAnEdit) {
                            clumpy.setPixel(i, j, Color.argb(255, minLabel, minLabel, minLabel));
                            numberOfChanges++;
                        }
                    }

                }
            }
            //Log.e("didMakEdit",String.valueOf(didIMakeAnEdit));

        } while (didIMakeAnEdit == true);
        //so here, labels are not organized... or are they? no they aren't
        ArrayList<Object>data = new ArrayList<Object>();
        data.add(CONSOLIDATEEDGES_DATA_NUMBEROFCHANGES,numberOfChanges);
        data.add(CONSOLIDATEEDGES_DATA_TOTALLABELS,getNumberOfLabelsNotOrganized(clumpy));
        data.add(CONSOLDIATEEDGES_DATA_BITMAP,clumpy);
        return data;  //although not clumpy anymore
    }

    public static int getNumberOfLabelsAssumingOrganized(Bitmap circies) {
        int label = 0;
        for (int i =0; i< circies.getWidth();i++) {
            for (int j =0; j< circies.getHeight();j++) {
                if (Color.red(circies.getPixel(i,j))!=255) {
                    int myLabel = Color.red(circies.getPixel(i,j));
                    if (myLabel>label) {
                        label=myLabel;
                    }
                }
            }
        }
        return label;
    }

    public static ArrayList<Object> getRidOfRandomEdges (Bitmap dirty) {
        //image= image.copy(Bitmap.Config.ALPHA_8,true);
        //that jusut makes sure it's mutable
        int labels = getNumberOfLabelsAssumingOrganized(dirty);
        //this will give max label
        int [] labelCount = new int[labels+1];
        for (int i =0; i <dirty.getWidth();i++) {
            for (int j =0; j<dirty.getHeight();j++) {
                if (Color.red(dirty.getPixel(i,j))!=255) {
                    //if it's not white
                    labelCount[Color.red(dirty.getPixel(i,j))]++;
                    //increase the counter within the slot of labels
                }
            }
        }
        //let's log this out
        Log.e("labelCount",Arrays.toString(labelCount));

        for (int label = Vision.FIRST_LABEL;label<labelCount.length;label++) {
            if (labelCount[label]<=Vision.MIN_NEEDED_TO_BE_AN_EDGE) {
                dirty = removeLabel(dirty,label);
            }

            //implicit else: then it has enough pixels to be an edge, leave it
        }
        //now check any edges that are touching the borders of the bitmap
        for (int i =0; i <dirty.getWidth();i++) {
            for (int j =0;j <dirty.getHeight();j++) {
                if (i==dirty.getWidth()-1||j==0||j==dirty.getHeight()-1) {
                    //then you're on an edge of the bitmap
                    if (Color.red(dirty.getPixel(i,j))!=255) {
                        //if it's a label, or it's not white, those two statements are equal
                        dirty = removeLabel(dirty,Color.red(dirty.getPixel(i,j)));
                        Log.e("removed label","touching the edge");
                    }
                }
            }
        }


        ArrayList<Object> data = new ArrayList();
        data.add(REMOVERANDOMNESS_DATA_LABELS,getNumberOfLabelsNotOrganized(dirty));
        data.add(REMOVERANDOMNESS_DATA_BITMAP, dirty);
        return data;

    }

    public static int getNumberOfLabelsNotOrganized (Bitmap circies) {
        ArrayList<Integer> listOfLabelsInPicture = new ArrayList<Integer>();
        for (int i =0; i <circies.getWidth();i++) {
            for (int j= 0 ;j <circies.getHeight();j++) {
                int label = Color.red(circies.getPixel(i,j));
                if (label!=255) {
                    //check if this label is in the list
                    if (!listOfLabelsInPicture.contains(label)) {
                        listOfLabelsInPicture.add(label);
                    }
                }
            }
        }
        Log.e("listOfLabels", "not organized" + listOfLabelsInPicture.toString());
        if (listOfLabelsInPicture.isEmpty()) {
            return 0;
        }
        return listOfLabelsInPicture.size();
    }

    public static Bitmap compress (Bitmap image) {
        if (image.getHeight()>160||image.getWidth()>160) {
            //too large to be uploaded into a texture
            int nh = (int) ( image.getHeight() * (160.0 / image.getWidth()) );
            Log.e("nh", nh + " h" + image.getHeight() + " w" + image.getWidth());
            image = Bitmap.createScaledBitmap(image,160,nh,true);
            //original will be same size as everything else
            //the saved version of the pic is original size, however

        }

        return image;
    }

    public static ArrayList<Object> returnCircles (Bitmap circles) {
        Log.e("starting","circle");
        //for each label, determine if it's a circle
        //find a bounding rectangle to find the center and radius of the circle.
        //calculate distance from edge to center
        //if equal to radius or almost equal to radius, for ALL of them, then it's a circle.
        //will have to check for tolerances
        //also elminate radius equal to one
        ArrayList<XYCoor> centers = new ArrayList<XYCoor>();
        ArrayList<Integer> labels = new ArrayList<Integer>();
        //k so for any given label, numOfLabels will only be greater, not less, so while some labels will have 0 edges, u wont miss any
        ArrayList<Integer> listOfLabelsInPicture = new ArrayList<Integer>();
        for (int i =0; i <circles.getWidth();i++) {
            for (int j= 0 ;j <circles.getHeight();j++) {
                int label = Color.red(circles.getPixel(i,j));
                if (label!=255) {
                    //check if this label is in the list
                    if (!listOfLabelsInPicture.contains(label)) {
                        listOfLabelsInPicture.add(label);
                    }
                }
            }
        }
        for (int counter = 0; counter <listOfLabelsInPicture.size();counter++) {
            int label = listOfLabelsInPicture.get(counter);
            //so for each label, determine if it's a circle
            //comb thru the image vertically first to determine left most pixel
            circles = circles.copy(Bitmap.Config.ARGB_8888, true);
            //make image mutable
            XYCoor leftMost = new XYCoor();
            //so the additional condition is my built in break statemtn
            for (int i = 0; i < circles.getWidth() && leftMost.getX() == -1; i++) {
                for (int j = 0; j < circles.getHeight() && leftMost.getX() == -1; j++) {
                    //if leftMost.getX is set, exit out
                    if (Color.red(circles.getPixel(i, j)) == label) {
                        //cool u found the label
                        //Log.e("found",leftMost.toString());
                        leftMost = new XYCoor(i, j);
                    }

                }
            }
            //now do for right most. so this, you will satrt from right side going to 0
            XYCoor rightMost = new XYCoor();

            for (int i = circles.getWidth() - 1; i >= 0 && rightMost.getX() == -1; i--) {
                for (int j = 0; j < circles.getHeight() && rightMost.getX() == -1; j++) {
                    //if rightMost.getX is set, exit out
                    if (Color.red(circles.getPixel(i, j)) == label) {
                        //cool u found the label
                        rightMost = new XYCoor(i, j);
                    }
                }
            }



            XYCoor topMost = new XYCoor();

            for (int i = 0; i < circles.getHeight() && topMost.getX() == -1; i++) {
                for (int j = 0; j < circles.getWidth() && topMost.getX() == -1; j++) {
                    //if topMost.getX is set, exit out
                    if (Color.red(circles.getPixel(j, i)) == label) {
                        //cool u found the label
                        topMost = new XYCoor(j, i);
                    }
                }
            }
            XYCoor bottomMost = new XYCoor();

            for (int i = circles.getHeight() - 1; i >= 0 && bottomMost.getX() == -1; i--) {
                for (int j = 0; j < circles.getWidth() && bottomMost.getX() == -1; j++) {
                    //if bottom.getX is set, exit out
                    if (Color.red(circles.getPixel(j, i)) == label) {
                        //cool u found the label
                        bottomMost = new XYCoor(j, i);
                    }
                }
            }
            //ok so now u have all the corners set.

            //first check. if top and bottom is not equal to right and left radius, then it's not a circle

            double leftRightRadius = Math.abs(rightMost.getX() - leftMost.getX());
            double topBottomRadius = Math.abs(bottomMost.getY() - topMost.getY());
            Log.e("radius",label+"leftR"+leftRightRadius+"tb"+topBottomRadius);
            if (Math.abs(leftRightRadius - topBottomRadius) > Vision.DIFFERENCE_IN_RADIUS_FOR_RECTANGLE_BOUNDS) {
                //then it isn't a cirlce
                //so we'll white out and move on
                circles = removeLabel(circles, label);
                Log.e("not a circle", label + " radius = not square - delete");

            } else if (leftRightRadius <= MIN_RADIUS_LENGTH || topBottomRadius <= MIN_RADIUS_LENGTH) {
                //not a circle because it's too small... we don't want small circles
                Log.e("not a circle", label + "radius = too small");
                circles = removeLabel(circles, label);
            }
            else if (checkIfClosedShape(circles, label, leftMost,rightMost,topMost,bottomMost)==false) {
               circles = removeLabel(circles,label);

            }
            else {

                Log.e("almost there", "checking def of circle");
                //ok so if u made it thus far, u should still check the distance to the center
                //midpoint of right/left
                //midpoint of top/bottom
                double centerX = (leftMost.getX() + rightMost.getX()) / 2.0;
                double centerY = (topMost.getY() + bottomMost.getY()) / 2.0;
                XYCoor center = new XYCoor(centerX, centerY);
                double radius = (leftRightRadius + topBottomRadius) / 2.0;
                Log.e("coordinates", "right" + rightMost.toString() + "left" + leftMost.toString() + "top" + topMost.toString() + "bottom" + bottomMost.toString() + "center" + center.toString() + "radius" + radius);
                //we need to find all the edges
                ArrayList<XYCoor> edgesOfShape = getCoordinatesOfEdges(circles, label);
                boolean litmustTest = true;
                //if litmusTest comes out false, meaning we broke out of statement becuase one was out of ordinairy, then it's not a circle
                for (int i = 0; i < edgesOfShape.size(); i++) {
                    //retrieve each coordinate and test
                    double distanceBetweenEdgeAndCenter = XYCoor.getDistance(center, edgesOfShape.get(i));
                    if (distanceBetweenEdgeAndCenter - radius > Vision.TOLERANCE_FOR_RADIUS_DIFFERENCE) {
                        //then ur not a circle
                        litmustTest = false;
                    }
                    if (edgesOfShape.get(i).getX() == 0 || edgesOfShape.get(i).getY() == 0) {
                        //if any parto f the image is on the edge, it's most likely not a beacon... they are usually in the middlee
                        //so this will eliminate any edge stuff (which we found during testing)
                        litmustTest = false;
                    }
                    //implied else, ur good. check next pixel
                }
                if (litmustTest == false) {
                    //failed that litmus test, one was out of the circle
                    //not a circle
                    circles = removeLabel(circles, label);
                    Log.e("not a circle", label + "def. of circle/litmus test fail");
                } else {
                    //implied else, ur a circle, let's keep you
                    //also type in the center
                    centers.add(center);
                    Log.e("you're a circle", label + center.toString());
                    labels.add(label);
                }
            }
        }
        //turn it into organized
        ArrayList <Object> data = new ArrayList<Object>();
        data.add(RETURNCIRCLES_DATA_BITMAP, circles);
        Log.e("centers", centers.toString());
        data.add(RETURNCIRCLES_DATA_XYCOORSCENTER, centers);
        data.add(RETURNCIRCLES_DATA_LABELSLIST, labels);
        return data;
    }

    private static boolean checkIfClosedShape(Bitmap circles, int label, XYCoor leftMost, XYCoor rightMost, XYCoor topMost, XYCoor bottomMost) {
        for (int i =((int)leftMost.getX()); i<=((int)rightMost.getX());i++) {
            //so for all these columns, make sure there are two labels
            int columnCounter = 0;
            for (int y =((int)topMost.getY());y<=((int)bottomMost.getY());y++) {
                if (Color.red(circles.getPixel(i,y))==label) {
                    columnCounter++;
                }
            }
            if (columnCounter<2) {
                Log.e("not a circle", "not a closed shape at column "+i+" and counter was "+columnCounter);
                return false;
            }
            //implied else, do nothing, move on

        }
        //now check the horizontal line test
        for (int i =((int)topMost.getY());i<=((int)bottomMost.getY());i++) {
            int rowCounter = 0;
            for (int x = (int)(leftMost.getX());x<=(int)(rightMost.getX());x++) {
                if (Color.red(circles.getPixel(x,i))==label) {
                    rowCounter++;
                }
            }
            if (rowCounter!=2) {
                Log.e("not a circle","not a closed shape at row "+i+"counter was "+rowCounter);
            return false;
            }
        }
        //else you passed all these test without failing
        return true;
    }

    private static ArrayList<XYCoor> getCoordinatesOfEdges(Bitmap circies, int label) {
        ArrayList <XYCoor> data = new ArrayList<XYCoor>();
        for (int i =0; i<circies.getWidth();i++) {
            for (int j = 0; j <circies.getHeight();j++) {
                if (Color.red(circies.getPixel(i,j))==label) {
                    data.add(new XYCoor(i, j));
                }
            }
        }
        return data;
    }

    private static Bitmap removeLabel(Bitmap circies, int label) {
        circies = circies.copy(Bitmap.Config.ARGB_8888,true);
        for (int i =0; i<circies.getWidth();i++) {
            for (int j = 0; j <circies.getHeight();j++) {
                if (Color.red(circies.getPixel(i,j))==label) {
                    //white it out
                    circies.setPixel(i, j, Color.argb(255, 255, 255, 255));
                }
            }
        }
        return circies;
    }
    public static Bitmap findAndIsolateBeaconButtonsOuput (Bitmap circles, ArrayList<XYCoor> centersOfCircles, ArrayList<Integer> labelsInSameOrderAsCircles, Context context) {
        Log.e("centerOfCircles",centersOfCircles.toString());
        if (centersOfCircles.size()<2) {
            Log.e("less than two","making educated guess");
            //assuming the right beacon button was captured
            return circles;
        }
        if (centersOfCircles.size()==2) {
            //check to make sure the two x values are equal
            Log.e("centers = 2",Math.abs(centersOfCircles.get(0).getY()-centersOfCircles.get(1).getY())+"");
            if (Math.abs(centersOfCircles.get(0).getY()-centersOfCircles.get(1).getY())<= THRESHOLD_FOR_CENTERS_OF_TWO_BUTTONS) {
                //check y's. the x's will have a huge diff. but the y placement should be about the same
                Log.e("returning original","yay");
                return circles;
                //you already had the two
            }
            else {
                Log.e("returning null","centers didnt match");
                Bitmap empty= Bitmap.createBitmap(circles.getWidth(), circles.getHeight(), Bitmap.Config.ARGB_8888);
                empty = makeEmpty(empty);
                return empty;
                //else those two werent the buttons, so send empty
            }
        }
        else {
            Log.e("more than 2","running center check");
            //more than 2. check to see if any two y's are equal
            int[][] numberOfMatchesPerCircle = new int[centersOfCircles.size()][centersOfCircles.size()];
            ArrayList<XYCoor> pairsOfCircles = new ArrayList<XYCoor>();
            //we can use XYCoor to represnt the pair
            for (int i =0; i <centersOfCircles.size()-1;i++) {
                for (int j =i+1;j<centersOfCircles.size();j++) {
                    //compare i and j y values. note the number of matches
                    if (Math.abs(centersOfCircles.get(i).getY()-centersOfCircles.get(j).getY())<=THRESHOLD_FOR_CENTERS_OF_TWO_BUTTONS) {
                        numberOfMatchesPerCircle[i][j]++;
                    }
                }
            }
            //k so now we've got that half triangle adjacency matrix
            int[] sumOfRows = new int[centersOfCircles.size()];
            for (int i =0; i <centersOfCircles.size();i++) {
                int sumOfI = 0;
                for (int j =0; j<centersOfCircles.size();j++) {
                    sumOfI+=numberOfMatchesPerCircle[i][j];
                }
                sumOfRows[i]=sumOfI;
            }
            //now check to see which ones have a sum of 1. then check the one
            for (int i =0; i<centersOfCircles.size();i++) {

                if (sumOfRows[i]==1) {
                    //check the sum of Column associated with that match
                    int matchOfI = findPair (numberOfMatchesPerCircle,i);
                    //now check for sum
                    int sumOfMatchPair = sumOfPairColumn(matchOfI, numberOfMatchesPerCircle);
                    if (sumOfMatchPair==1) {
                        //then we found the pair
                        Log.e("found pair!","circle one label: "+labelsInSameOrderAsCircles.get(i)+"circle two label:"+labelsInSameOrderAsCircles.get(matchOfI));
                        pairsOfCircles.add(new XYCoor(labelsInSameOrderAsCircles.get(i),labelsInSameOrderAsCircles.get(matchOfI)));
                        Log.e("pair of cirlces",pairsOfCircles.toString());
                    }
                    //implied else, didn't work out, don't save
                }
            }
            //remove everything from the picture but the paired circles
            for (int i =0; i <centersOfCircles.size();i++) {
                int label = labelsInSameOrderAsCircles.get(i);
                if (!ArrayListOfXYCoorsIncludes(pairsOfCircles,label)) {
                    Log.e("removed a label","not a pair: "+label);
                    circles = removeLabel(circles, label);
                }
            }
            if (pairsOfCircles.size()==1) {
                //awesomeee my job is easy
                Log.e("only one pair left","good! returning now!");
                return circles;
            }
            else {
                //TODO find the best fit ie. what is the usual button size, radius size
                //now u have to rule out circles :/

                return circles;
            }
        }
    }
    public static Bitmap findAndIsolateBeaconButtons (Bitmap circles, ArrayList<XYCoor> centersOfCircles, ArrayList<Integer> labelsInSameOrderAsCircles) {
        Log.e("centerOfCircles",centersOfCircles.toString());
        if (centersOfCircles.size()<2) {
            Log.e("less than two","making educated guess");
            //assuming the right beacon button was captured
           return circles;
        }
        if (centersOfCircles.size()==2) {
            //check to make sure the two x values are equal
            Log.e("centers = 2",Math.abs(centersOfCircles.get(0).getY()-centersOfCircles.get(1).getY())+"");
            if (Math.abs(centersOfCircles.get(0).getY()-centersOfCircles.get(1).getY())<= THRESHOLD_FOR_CENTERS_OF_TWO_BUTTONS) {
                //check y's. the x's will have a huge diff. but the y placement should be about the same
                Log.e("returning original","yay");
                return circles;
                //you already had the two
            }
            else {
                Log.e("returning null","centers didnt match");
                Bitmap empty= Bitmap.createBitmap(circles.getWidth(), circles.getHeight(), Bitmap.Config.ARGB_8888);
                empty = makeEmpty(empty);
                return empty;
                //else those two werent the buttons, so send empty
            }
        }
        else {
            Log.e("more than 2","running center check");
            //more than 2. check to see if any two y's are equal
            int[][] numberOfMatchesPerCircle = new int[centersOfCircles.size()][centersOfCircles.size()];
            ArrayList<XYCoor> pairsOfCircles = new ArrayList<XYCoor>();
            //we can use XYCoor to represnt the pair
            for (int i =0; i <centersOfCircles.size()-1;i++) {
                for (int j =i+1;j<centersOfCircles.size();j++) {
                    //compare i and j y values. note the number of matches
                    if (Math.abs(centersOfCircles.get(i).getY()-centersOfCircles.get(j).getY())<=THRESHOLD_FOR_CENTERS_OF_TWO_BUTTONS) {
                        numberOfMatchesPerCircle[i][j]++;
                    }
                }
            }
            //k so now we've got that half triangle adjacency matrix
            int[] sumOfRows = new int[centersOfCircles.size()];
            for (int i =0; i <centersOfCircles.size();i++) {
                int sumOfI = 0;
                for (int j =0; j<centersOfCircles.size();j++) {
                    sumOfI+=numberOfMatchesPerCircle[i][j];
                }
                sumOfRows[i]=sumOfI;
            }
            //now check to see which ones have a sum of 1. then check the one
            for (int i =0; i<centersOfCircles.size();i++) {

                if (sumOfRows[i]==1) {
                    //check the sum of Column associated with that match
                    int matchOfI = findPair (numberOfMatchesPerCircle,i);
                    //now check for sum
                    int sumOfMatchPair = sumOfPairColumn(matchOfI, numberOfMatchesPerCircle);
                    if (sumOfMatchPair==1) {
                        //then we found the pair
                        Log.e("found pair!","circle one label: "+labelsInSameOrderAsCircles.get(i)+"circle two label:"+labelsInSameOrderAsCircles.get(matchOfI));
                        pairsOfCircles.add(new XYCoor(labelsInSameOrderAsCircles.get(i),labelsInSameOrderAsCircles.get(matchOfI)));
                        Log.e("pair of cirlces",pairsOfCircles.toString());
                    }
                    //implied else, didn't work out, don't save
                }
            }
            //remove everything from the picture but the paired circles
            for (int i =0; i <centersOfCircles.size();i++) {
                int label = labelsInSameOrderAsCircles.get(i);
                if (!ArrayListOfXYCoorsIncludes(pairsOfCircles,label)) {
                    Log.e("removed a label","not a pair: "+label);
                    circles = removeLabel(circles, label);
                }
            }
            if (pairsOfCircles.size()==1) {
                //awesomeee my job is easy
                Log.e("only one pair left","good! returning now!");
                //TODO HERE
                return circles;
            }
            else {
                //TODO find the best fit ie. what is the usual button size, radius size
                //now u have to rule out circles :/

                return circles;
            }
        }
    }

    private static Bitmap makeEmpty(Bitmap empty) {
        Bitmap mutable = empty.copy(Bitmap.Config.ARGB_8888, true);
        for (int i =0; i <empty.getWidth();i++) {
            for (int j =0; j<empty.getHeight();j++) {
                mutable.setPixel(i,j,Color.argb(255,255,255,255));
            }
        }
        return mutable;
    }

    private static boolean ArrayListOfXYCoorsIncludes(ArrayList<XYCoor> pairsOfCircles, int label) {
        for (int i =0; i <pairsOfCircles.size();i++) {
            if (pairsOfCircles.get(i).getX()==label||pairsOfCircles.get(i).getY()==label) {
                return true;
            }
        }
        return false;
    }

    private static int sumOfPairColumn(int matchOfI, int[][] fullAdjacencyMatrix) {
        int sum = 0;
        for (int i =0; i <fullAdjacencyMatrix.length;i++) {
            sum+=fullAdjacencyMatrix[i][matchOfI];
        }
        return sum;
    }

    private static int findPair(int[][] fullAdjacencyMatrix, int rowThatHasOneMatch) {
        for (int i = 0; i< fullAdjacencyMatrix.length;i++) {
            //go to that row and search until it's not 0
            if (fullAdjacencyMatrix[rowThatHasOneMatch][i]==1) {
                return i;
            }
        }
        //this should never happen...
        return -1;
    }
    

    public static String savePictureOuput (Bitmap bitmap, Context context) {
        return savePicture(bitmap, context, "OUTPUT", true);
    }
    public static String savePicture(Bitmap bitmap, Context context, String tag, boolean debug) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + tag;
        File pictureFile = getOutputMediaFile(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE, timeStamp, context, debug);
        if (pictureFile == null) {
            Log.d("ERROR", "Error creating media file, check storage permissions: "
            );
            return "ERROR";
        }
        try {

            FileOutputStream fos = new FileOutputStream(pictureFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);

            fos.close();


        } catch (FileNotFoundException e) {
            Log.d("ERROR", "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d("ERROR", "Error accessing file: " + e.getMessage());
        }
        return pictureFile.getName();
    }

    public static File getOutputMediaFile(int type, String timeStamp, Context context, boolean debug) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Matt Quan is a boss");
        if (debug) {
            mediaStorageDir =  new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), "Testing");

        }
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
        if (type == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE) {
            String path = mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".png";
            Log.e("savedPath", path);
            SharedPreferences prefs = context.getApplicationContext().getSharedPreferences(
                    "com.quan.companion", Context.MODE_PRIVATE);
            prefs.edit().putString(Keys.pictureImagePathSharedPrefsKeys, path).apply();
            Log.e("saved path", "saved path in shared prefs");
            mediaFile = new File(path);

        } else {
            return null;
        }

        return mediaFile;
    }
}
