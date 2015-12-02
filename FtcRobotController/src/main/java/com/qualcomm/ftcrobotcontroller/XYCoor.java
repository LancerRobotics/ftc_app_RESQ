package com.qualcomm.ftcrobotcontroller;

/**
 * Created by daniel on 11/14/2015.
 */
public class XYCoor {
    private int x;
    private int y;

    public XYCoor(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    @Override
    public String toString() {
        return "XYCoor{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

}
