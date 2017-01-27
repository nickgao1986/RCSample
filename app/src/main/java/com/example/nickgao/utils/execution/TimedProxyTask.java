/**
 * Copyright (C) 2012, RingCentral, Inc. 
 * All Rights Reserved.
 */
package com.example.nickgao.utils.execution;

import android.os.SystemClock;

import com.example.nickgao.logging.LogSettings;
import com.example.nickgao.logging.MktLog;

/**
 * <code>TimedSIProxyTask</code> allows to make asynchronous execution of
 * <code>doTask</code> method, indicating timeout when called thread will be
 * waked-up if <code>doTask</code> has not been completed in
 * <code>timeout</code>. See {@link #execute(long, boolean)}.
 */
public abstract class TimedProxyTask extends Thread {
    /**
     * Defines logging tag.
     */
    private static final String TAG = "[RC]TimedProxyTask";
    
    /**
     * Defines initial state of the execution.
     */
    public final static int PENDING    = 0;
    
    /**
     * Defines state when <code>doTask</code> was completed.See {@link #execute(long, boolean)}.
     */
    public final static int TIMEOUT    = 1;
    
    /**
     * Defines state when timeout occurred. See {@link #execute(long, boolean)} and
     * See {@link #wasCompletedWithError()}
     */
    public final static int COMPLETED  = 2;
    
    /**
     * Keeps synchronization primitive.
     */
    private volatile Object mLock = new Object();
    
    /**
     * Keeps execution state.
     */
    private volatile int mState = PENDING;
    
    /**
     * Defines task(thread) name.
     */
    private volatile String mName = null;
    
    /**
     * Defines if <code>doTask</code> was completed with errors.
     */
    private volatile boolean mCompletedWithError = false;
    
    /**
     * Defines if <code>doTask</code> shall be completed even timeout happened.
     */
    private volatile boolean mDoTaskEvenTimeoutOccured = false;

    /**
     * Default constructor.
     * 
     * @param name the task name for logging and analysis
     */
    public TimedProxyTask(String name) {
        super((name == null ? "NONAME" : name));
        mName = (name == null ? "NONAME" : name);
    }
    
    /**
     * Returns task name.
     * 
     * @return  task name
     */
    public String getTaskName() {
        return mName;   
    }
    
    /**
     * Returns if <code>doTask</code> has been completed with errors (exception
     * when doTask was executed)
     * 
     * @return <code>true</code> if <code>doTask</code> has been completed with
     *         errors (exception when doTask was executed), otherwise
     *         <code>false</code>
     */
    public boolean wasCompletedWithError() {
        synchronized (mLock) {
            return mCompletedWithError;
        }
    }
    
    /**
     * Keeps execution start time.
     */
    private volatile long mStartTime = 0; 
    
    /**
     * Defines timeout when warn about long execution.
     */
    private static final long WARN_TIMEOUT = 2000; 
    
    
    /**
     * Executes <code>doTask</code>.
     * 
     * @param timeout
     *            timeout when caller will be notified if <code>doTask</code>
     *            has not been finished in that time
     * @param doTaskEvenTimeoutOccured
     *            defines if need complete <code>doTask</code> even timeout
     *            occured
     * 
     * @return status: {@link #TIMEOUT} if <code>doTask</code> has not been
     *         finished in <code>timeout</code> time; {@link #COMPLETED} if the
     *         <code>doTask</code> has been completed in <code>timeout</code>
     */
    public int execute(final long timeout, final boolean doTaskEvenTimeoutOccured) {
        if (timeout < 0) {
            throw new IllegalArgumentException(TAG + ':' + mName + ':' + "execute:negative timeout.");
        }
        synchronized (mLock) {
            mStartTime = SystemClock.elapsedRealtime();
            mDoTaskEvenTimeoutOccured = doTaskEvenTimeoutOccured;
            if (mState != PENDING) {
                throw new IllegalStateException(TAG + ':' + mName + ':' + "execute:invalid state.");
            }
            start();
            try {
                mLock.wait(timeout);
            } catch (Throwable th) {
            }
            if (mState == PENDING) {
                if (LogSettings.MARKET) {
                    MktLog.w(TAG, (mName + ":execute:timeout expired"));
                }
                mState = TIMEOUT;
            }
            return mState;
        }
    }

    @Override
    public void run() {
        synchronized (mLock) {
            if (mState == TIMEOUT) {
                if (!mDoTaskEvenTimeoutOccured) {
                    if (LogSettings.MARKET) {
                        MktLog.i(TAG, (mName + ":run:ignore execution as expired"));
                    }
                    mLock.notifyAll();
                    return;
                }
            } else if (mState != PENDING) {
                if (LogSettings.MARKET) {
                    MktLog.w(TAG, (mName + ":run:ignore execution due to invalid state"));
                }
                mLock.notifyAll();
                return;
            }
        }
        boolean error = false;
        try {
            doTask();
        } catch (Throwable th) {
            if (LogSettings.MARKET) {
                MktLog.e(TAG, (mName + ":error:"));
            }
            error = true;
        }
        long totalTime = SystemClock.elapsedRealtime() - mStartTime;
        
        if (totalTime > WARN_TIMEOUT) {
            if (LogSettings.MARKET) {
                MktLog.w(TAG, (mName + ":executed " + totalTime + " ms"));
            }
        } else {
            if (LogSettings.MARKET) {
                MktLog.d(TAG, (mName + ":executed " + totalTime + " ms"));
            }
        }
        
        synchronized (mLock) {
            mCompletedWithError = error;
            mState = COMPLETED;
            mLock.notifyAll();
            return;
        }
    }
    
    /**
     * The task will be executed by <code>TimedProxyTask</code>
     */
    public abstract void doTask();
}
