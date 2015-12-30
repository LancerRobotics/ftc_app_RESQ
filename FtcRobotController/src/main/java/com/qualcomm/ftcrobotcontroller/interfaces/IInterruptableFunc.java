package com.qualcomm.ftcrobotcontroller.interfaces;

/**
 * A variation of IFunc that permits the throwing of an InterruptedException
 * @see IFunc
 */
public interface IInterruptableFunc<TResult>
    {
    TResult value() throws InterruptedException;
    }
