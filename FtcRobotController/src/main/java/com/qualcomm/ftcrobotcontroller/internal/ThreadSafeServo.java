package com.qualcomm.ftcrobotcontroller.internal;

import com.qualcomm.robotcore.hardware.*;

/**
 * ThreadSafeServo modifies the FTC-provided Servo so that it is thread-safe.
 */
public class ThreadSafeServo extends Servo
    {
    //----------------------------------------------------------------------------------------------
    // Construction
    //----------------------------------------------------------------------------------------------

    public ThreadSafeServo(ServoController controller, int portNumber, Direction direction)
        {
        super(controller, portNumber, direction);
        }

    //----------------------------------------------------------------------------------------------
    // Operations
    //----------------------------------------------------------------------------------------------

    public synchronized void setDirection(Direction direction)
        {
        // super sets direction
        super.setDirection(direction);
        }

    public synchronized void setPosition(double position)
        {
        // super uses direction, minPosition, maxPosition
        super.setPosition(position);
        }

    public synchronized double getPosition()
        {
        // super uses direction, minPosition, maxPosition
        return super.getPosition();
        }

    public synchronized void scaleRange(double min, double max) throws IllegalArgumentException
        {
        // super sets minPosition, maxPosition
        super.scaleRange(min, max);
        }

    }