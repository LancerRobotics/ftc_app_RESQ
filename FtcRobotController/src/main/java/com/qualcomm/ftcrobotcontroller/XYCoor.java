package com.qualcomm.ftcrobotcontroller;

/**
 * Created by daniel on 11/14/2015.
 */
public class XYCoor {
    private double x;
    private double y;

    public XYCoor(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public XYCoor() {
        x = -1;
        y = -1;
    }

    public double getX() {
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

    public double getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public static double getDistance (XYCoor one, XYCoor two) {
        return Math.sqrt(Math.pow(one.getX()-two.getX(),2)+Math.pow(one.getY()-two.getY(),2));
    }

}