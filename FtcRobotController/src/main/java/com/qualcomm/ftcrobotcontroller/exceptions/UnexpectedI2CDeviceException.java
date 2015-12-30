package com.qualcomm.ftcrobotcontroller.exceptions;

/**
 * 
 */
public class UnexpectedI2CDeviceException extends RuntimeException
    {
    public UnexpectedI2CDeviceException(int id)
        {
        super(String.format("unexpected i2c device id: 0x%02x", id));
        }
    }
