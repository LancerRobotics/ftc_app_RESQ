package com.qualcomm.ftcrobotcontroller;

/**
 * Created by Matt on 2/21/2016.
 */
public class Beacon {
    public static final int COLOR_RED = 0;
    public static final int COLOR_BLUE = 1;
    private int[] beacon;
    public static final String RIGHT = "right";
    public static final String LEFT = "left";
    public static final String UNKNOWN = "unknown";

    public Beacon(int[] beacon) {
        this.beacon = beacon;
    }
    public Beacon (Beacon dos) {
        this.beacon=dos.beacon;
    }

    public Beacon(int left, int right) {
        beacon = new int[2];
        beacon[0] = left;
        beacon[1] = right;
    }

    public Beacon() {
        beacon = new int[2];
        beacon[0] = -1;
        beacon[1] = -1;
    }

    public void setLeft(int color) {
        beacon[0] = color;
    }

    public void setRight(int color) {
        beacon[1] = color;
    }
    public int getRight() {
        return beacon[1];
    }
    public int getLeft() {
        return beacon[0];
    }

    public void setColor(int left, int right){
        beacon[0]=left;
        beacon[1]=right;
    }
    public String toString () {
        String data = "[";
        if (beacon[0]==COLOR_RED) {
            data+="red,";
        }
        else if (beacon[0]==COLOR_BLUE) {
            data +="blue,";
        }
        else if (beacon[0]==-1) {
            data+="null,";
        }
        else {
            data+="unknown,";
        }
        if (beacon[1]==COLOR_RED) {
            data+="red],";
        }
        else if (beacon[1]==COLOR_BLUE) {
            data +="blue]";
        }
        else if (beacon[0]==-1) {
            data+="null]";
        }
        else {
            data+="unknown]";
        }
        return data;
    }
    public  boolean error () {
        /*if (beacon[0]==beacon[1]) {
            return true;
            //so if returned true, then you can't use it
        }
        //implied else, then at least one of them is set, and they are both not equal
        //so if one is set but not the other, then at least it's still find-able
        return false;*/
        //simplified version of ^^
        return beacon[0]==beacon[1];
    }
    public boolean oneSideUnknown () {
        //assuming you alrady called error(), then this will be accurate
        //becuase it doesnt check for both. cuz if both unknown then false, technically.
        if (beacon[0]!=0||beacon[0]!=1) {
            return true;
        }
        if (beacon[1]!=0||beacon[1]!=1) {
            return true;
        }
        return false;
    }
    public String whereIsRed () {
        //basically finds red
       if (beacon[0]==COLOR_RED) {
           return LEFT;
       }
        else if (beacon[1]==COLOR_RED) {
           return RIGHT;
       }
        return UNKNOWN;
    }
    public String whereIsBlue () {
        //basically finds red
        if (beacon[0]==COLOR_BLUE) {
            return LEFT;
        }
        else if (beacon[1]==COLOR_BLUE) {
            return RIGHT;
        }
        return UNKNOWN;
    }

}
