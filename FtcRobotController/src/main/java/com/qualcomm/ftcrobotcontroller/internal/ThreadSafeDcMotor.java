package com.qualcomm.ftcrobotcontroller.internal;

import com.qualcomm.robotcore.hardware.*;

/**
 * ThreadSafeServo modifies the FTC-provided DCMotor so that it is thread-safe.
 */
public class ThreadSafeDcMotor extends DcMotor
    {
    //----------------------------------------------------------------------------------------------
    // Construction
    //----------------------------------------------------------------------------------------------

    public ThreadSafeDcMotor(DcMotorController controller, int portNumber, Direction direction)
        {
        super(controller, portNumber, direction);
        }

    //----------------------------------------------------------------------------------------------
    // Operations
    //----------------------------------------------------------------------------------------------

    public synchronized void setDirection(Direction direction)
        {
        // super writes direction
        super.setDirection(direction);
        }

    public synchronized void setPower(double power)
        {
        // super reads direction, mode
        super.setPower(power);
        }

    public synchronized double getPower()
        {
        // super reads direction
        return super.getPower();
        }

    @Deprecated
    public synchronized void setChannelMode(DcMotorController.RunMode mode)
        {
        // super writes mode
        super.setChannelMode(mode);
        }

    public synchronized void setMode(DcMotorController.RunMode mode)
        {
        // super writes mode
        super.setMode(mode);
        }

    @Deprecated
    public DcMotorController.RunMode getChannelMode()
        {
        return super.getChannelMode();
        }

    public DcMotorController.RunMode getMode()
        {
        return super.getMode();
        }
    }
