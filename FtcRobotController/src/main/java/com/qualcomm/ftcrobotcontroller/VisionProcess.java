package com.qualcomm.ftcrobotcontroller;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Matt on 3/29/2016.
 */
public class VisionProcess {
    private Bitmap original;
    private Beacon beacon;

    public VisionProcess(Bitmap original) {
        this.original = Vision.compress(original);

    }

    public Beacon output(Context context) {
        Log.e("output","started");
        Bitmap CBG = Vision.fastblur(Vision.toGrayscaleBitmap(Vision.applyContrastBrightnessFilter(Vision.filterRedBlue(Vision.applyContrastBrightnessFilter(original, Vision.CONTRAST_ADJUSTMENT,Vision.BRIGHTNESS_ADJUSTMENT)),Vision.CONTRAST_ADJUSTMENT,Vision.BRIGHTNESS_ADJUSTMENT)),1);

        Log.e("contrast","done");
        Log.e("gray","done");
        Log.e("blur","done");
        Vision.savePicture(CBG,context,"CBG",false);
        ArrayList<Object> edgedData = Vision.convertGrayscaleToEdged(CBG,Vision.EDGE_THRESHOLD);
        int totalLabel = (Integer) edgedData.get(Vision.CONVERTGRAYSCALETOEDGED_DATA_NUMBER_OF_LABELS);

        //catching label overflows
        while (totalLabel>=255) {
            //label overflow. redo edge with a higher edge threshold
            int prevUsedThreshold = (Integer)edgedData.get(Vision.CONVERTGRAYSCALETOEDGED_DATA_EDGETHRESHOLDUSED);
            Log.e("OVERFLOW", "correcting... prevUsed=" + prevUsedThreshold + "new:" + prevUsedThreshold + 20);
            edgedData = Vision.convertGrayscaleToEdged(CBG,prevUsedThreshold+20);
            totalLabel = (Integer)edgedData.get(Vision.CONVERTGRAYSCALETOEDGED_DATA_NUMBER_OF_LABELS);
            Log.e("totalLabel","redone:"+totalLabel);
        }
        Log.e("edged","done");
        Bitmap consolidateEdge= (Bitmap) Vision.consolidateEdges((Bitmap)edgedData.get(Vision.CONVERTGRAYSCALETOEDGED_DATA_BITMAP)).get(Vision.CONSOLDIATEEDGES_DATA_BITMAP);
        Log.e("consolidated","done");
        //should not need getRidOfRandom Edges
        //finding the circles
        Vision.savePicture(consolidateEdge,context,"EDGED",false);
        ArrayList <Object> returnedCirclesData = Vision.returnCircles(consolidateEdge);
        Bitmap circles = (Bitmap)returnedCirclesData.get(Vision.RETURNCIRCLES_DATA_BITMAP);
        Log.e("filter","done");
        Log.e("circles", String.valueOf(Vision.getNumberOfLabelsNotOrganized(circles)));
        ArrayList<XYCoor> centers =  (ArrayList<XYCoor>)returnedCirclesData.get(Vision.RETURNCIRCLES_DATA_XYCOORSCENTER);
        ArrayList<Integer> labels = (ArrayList<Integer> )returnedCirclesData.get(Vision.RETURNCIRCLES_DATA_LABELSLIST);
        Vision.savePicture(circles,context,"CIRCLES",false);
        Bitmap circlesAdjusted = Vision.findAndIsolateBeaconButtons(circles, centers, labels);
        Vision.savePicture(circles,context,"ADJUSTED",false);
        int circlesFound = Vision.getNumberOfLabelsNotOrganized(circlesAdjusted);
        Log.e("circles found","circles:"+circlesFound);
        beacon = Vision.getBeacon(circlesAdjusted,original);
        return beacon;
    }

}
