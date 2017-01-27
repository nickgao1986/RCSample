/**
 * Copyright (C) 2011-2012, RingCentral, Inc. 
 * All Rights Reserved.
 */
package com.example.nickgao.utils.execution;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;

import com.example.nickgao.logging.LogSettings;
import com.example.nickgao.logging.MktLog;


/**
 * LIFO <code>Command</code> asynchronous processor. 
 */
public class CommandProcessor {
    /**
     * Synchronization primitive.
     */
    private Object _lock = new Object();

    /**
     * Execution wake lock.
     */
    private ExecutionWakeLock mWakeLock;

    /**
     * Keeps logging name tag.
     */
    private String mName;

    /**
     * Processing thread.
     */
    private HandlerThread mHandlerThread;

    /**
     * Processing handler.
     */
    private Handler mHandler;

    /**
     * Defines if the processor is active.
     */
    private boolean mActive;

    /**
     * Keeps logging tag.
     */
    private static final String LOG_TAG = "[RC]CmdProc";

    /**
     * Keeps identifier for next command.
     */
    private volatile int mNextCommandId = 1; 
    
    /**
     * Keeps number of enqueued commands.
     */
    private volatile int mNumberOfEnqueuedCommands = 0;
    
    /**
     * Keeps identifier of command that is executed.
     */
    private volatile int mCommandUnderExecutionId = 0; 
    
    /**
     * Returns next identifier for command.
     * 
     * @return identifier for command.
     */
    private int getNextCommandId() {
        synchronized (_lock) {
            if (mNextCommandId >= Short.MAX_VALUE) {
                mNextCommandId = 1;
            }
            int currentId = mNextCommandId;
            mNextCommandId++;
            return currentId;
        }
    }
    
    /**
     * Returns the number of enqueued commands (pending state), excluding current under execution.
     * @return
     */
    public int getNumberOfEnqueuedCommands() {
        synchronized (_lock) {
            return mNumberOfEnqueuedCommands;
        }
    }
    /**
     * Returns command identifier that currently under execution, otherwise 0.
     * @return
     */
    public int getCommandIdUnderExecution() {
        synchronized (_lock) {
            return mCommandUnderExecutionId;
        }
    }
    
    /**
     * Construct a LIFO command processor.
     * 
     * @param name
     *            the short name for logging
     * @param threadPriority
     *            the linux priority thread to be set for
     *            <code>CommandProcessor</code>, @see {@link android.os.Process}
     * @param wakeLock
     *            the execution wake lock, can be <code>null</code>
     */
    public CommandProcessor(String name, int threadPriority, ExecutionWakeLock wakeLock) {
        if (name == null || name.trim().length() == 0) {
            throw new IllegalArgumentException(getTraceName() + " constructor:invalid name parameter");
        }
        mWakeLock = wakeLock;
        mName = name;
        mHandlerThread = new HandlerThread("CmdProc:" + name + "[ET:" + SystemClock.elapsedRealtime() + "]", threadPriority) {
            protected void onLooperPrepared() {
                Looper looper = mHandlerThread.getLooper();
                if (looper == null) {
                    mActive = false;
                    
                    if (LogSettings.MARKET) {
                        MktLog.w(LOG_TAG, getTraceName() + " Looper is NULL");
                    }
                   
                    try {
                        mHandlerThread.getLooper().quit();
                    } catch (Throwable th) {
                        if (LogSettings.MARKET) {
                            MktLog.w(LOG_TAG, getTraceName() + " onLooperPrepared:looper:quit:exception:" + th.toString());
                        }
                    } finally {
                        mHandlerThread = null;
                    }
                    return;
                    
                }
                mHandler = new Handler(mHandlerThread.getLooper()) {
                    public void handleMessage(Message msg) {
                        if (msg != null) {
                            if (msg.what == DESTROY_MSG_ID) {
                                try {
                                    mHandlerThread.getLooper().quit();
                                } catch (Throwable th) {
                                    if (LogSettings.MARKET) {
                                        MktLog.w(LOG_TAG, getTraceName() + " handleMessage:looper:quit:exception:" + th.toString());
                                    }
                                } finally {
                                    mHandlerThread = null;
                                }
                                try {
                                    if (mWakeLock != null) {
                                        mWakeLock.releaseAll();
                                    }
                                } catch (Throwable th) {
                                    if (LogSettings.MARKET) {
                                        MktLog.w(LOG_TAG, getTraceName() + " handleMessage:lock:releaseAll:exception:" + th.toString());
                                    }
                                } finally {
                                    mWakeLock = null;
                                }
                                mHandler = null;

                                if (LogSettings.MARKET) {
                                    MktLog.w(LOG_TAG, getTraceName() + " Destroyed.");
                                }
                                return;
                            }

                            if (msg.what != COMMAND_MSG_ID) {
                                if (LogSettings.MARKET) {
                                    MktLog.w(LOG_TAG, getTraceName() + " handleMessage:invalid message:");
                                }
                                return;
                            }

                            if (msg.obj == null) {
                                if (LogSettings.MARKET) {
                                	MktLog.w(LOG_TAG, getTraceName() + " handleMessage:CommandEnum is null");
                                }
                                return;
                            }

                            if (!(msg.obj instanceof Command)) {
                                if (LogSettings.MARKET) {
                                	MktLog.w(LOG_TAG, getTraceName() + " handleMessage:Invalid command");
                                }
                                return;
                            }

                            Command command = (Command) msg.obj;
                            int currentId = command.commandId;
                            boolean hasMessages = mHandler.hasMessages(COMMAND_MSG_ID);
                            synchronized (_lock) {
                                mNumberOfEnqueuedCommands--;
                                if (!hasMessages || mNumberOfEnqueuedCommands < 0) {
                                    mNumberOfEnqueuedCommands = 0;
                                }
                                if (!mActive) {
                                    if (LogSettings.MARKET) {
                                    	MktLog.w(LOG_TAG, getTraceName() + " handleMessage:IllegalState");
                                    }

                                    return;
                                }
                                mCommandUnderExecutionId = currentId;
                            }

                            command.mStartTime = SystemClock.elapsedRealtime();
                            
//                            try {
//                                if (mWakeLock != null) {
//                                    if (!mWakeLock.isHeld()) {
//                                        if (LogSettings.MARKET) {
//                                            MktLog.e(LOG_TAG, getTraceName() + " !mWakeLock.isHeld():acquire");
//                                        }
//                                        mWakeLock.acquire();
//                                    }
//                                }
//                            } catch (Throwable thIn) {
//                            }
                            
                            if (LogSettings.MARKET) {
                            	MktLog.d(LOG_TAG, getTraceName(command) + "(" + command.commandId + ") " +
                            			"Started (after waiting " + (command.mStartTime - command.mEnqueueTime)
                                        + "ms)");
                            }

                            try {
                                command.run();
                            } catch (Throwable th) {
                                if (LogSettings.MARKET) {
                                    MktLog.e(LOG_TAG, getTraceName(command) + " Execution exception:" + th.toString());
                                }
                            } finally {
                                command.mFinishTime = SystemClock.elapsedRealtime();
                                long totalTime = command.mFinishTime - command.mEnqueueTime;
                                long waitingTime = command.mStartTime - command.mEnqueueTime;
                                long executionTime = command.mFinishTime - command.mStartTime;
                                synchronized (_lock) {
                                    mCommandUnderExecutionId = 0;
                                }
                                if (LogSettings.MARKET) {
                                    StringBuffer sb = new StringBuffer(getTraceName(command) + "(" + command.commandId + 
                                            ") Finished (Time: " + (totalTime) + "ms [waited: "
                                            + (waitingTime) + "ms; executed: " + (executionTime) + "ms])(queue=" + mNumberOfEnqueuedCommands + ")");

                                    boolean warning = false;
                                    if (command.mTimeExecutionLimit != 0 && (executionTime > command.mTimeExecutionLimit)) {
                                        warning = true;
                                        sb.append(" Warning: Execution time exceed limit " + command.mTimeExecutionLimit);
                                    }
                                    if (command.mTotalTimeExecutionLimit != 0 && (totalTime > command.mTotalTimeExecutionLimit)) {
                                        if (warning) {
                                            sb.append("; Total time exceed limit " + command.mTotalTimeExecutionLimit);
                                        } else {
                                            sb.append(" Warning: Total time exceed limit " + command.mTotalTimeExecutionLimit);
                                        }
                                        warning = true;
                                    }

                                    if (warning) {
                                        MktLog.w(LOG_TAG, sb.toString());
                                    } else {
                                        MktLog.d(LOG_TAG, sb.toString());
                                    }
                                }
                                
                                try {
                                    if (mWakeLock != null) {
                                        mWakeLock.release();
                                    }
                                } catch (Throwable thIn) {
                                    if (LogSettings.MARKET) {
                                        MktLog.w(LOG_TAG, getTraceName() + " handleMessage:lock:release:exception:" + thIn.toString());
                                    }
                                }
                            }
                        }
                    }
                };
                mActive = true;
                if (LogSettings.MARKET) {
                    MktLog.d(LOG_TAG, getTraceName() + " Started.");
                }
            }
        };
        mHandlerThread.start();
        if (LogSettings.MARKET) {
            MktLog.d(LOG_TAG, getTraceName() + " Created.");
        }
    }

    /**
     * Defines timeout in ms when warn if time between command injection and enqueue exceeded  
     */
    private static final long ENQUEUE_TIMEOUT_WARNING_MS = 500;
    
    /**
     * Enqueue command for processing.
     * 
     * @param command
     *            the command to be put for execution.
     */
    public void execute(Command command) {
    	long startTime =  SystemClock.elapsedRealtime();
        if (LogSettings.QA) {
        	MktLog.d(LOG_TAG, getTraceName(command) + " execute is starting");
        }

    	if (command == null) {
            String prompt = getTraceName() + " execute: command is null";
            if (LogSettings.MARKET) {
            	MktLog.w(LOG_TAG, prompt); 
            }
            throw new IllegalStateException(prompt);
        }
    	boolean hasMessages = mHandler.hasMessages(COMMAND_MSG_ID);
        synchronized (_lock) {
            if (!hasMessages || mNumberOfEnqueuedCommands < 0) {
                mNumberOfEnqueuedCommands = 0;
            }
            if (!mActive) {
                if (LogSettings.MARKET) {
                    MktLog.w(LOG_TAG, getTraceName() + " execute:IllegalState");
                }
                return;
            }
            mNumberOfEnqueuedCommands++;
        }

        command.commandId = getNextCommandId();
        
        try {
            if (mWakeLock != null) {
                mWakeLock.acquire();
            }
        } catch (Throwable th) {
            if (LogSettings.MARKET) {
                MktLog.w(LOG_TAG, getTraceName() + " execute:lock:acquire:exception:" + th.toString());
            }
        }

        try {
            command.mEnqueueTime = SystemClock.elapsedRealtime();

            if (LogSettings.MARKET) {
                long delta = command.mEnqueueTime - startTime;
                if (delta > ENQUEUE_TIMEOUT_WARNING_MS) {
                    MktLog.e(LOG_TAG, getTraceName(command) + "(" + command.commandId + ") Enqueued after " + delta + "ms (queue=" + mNumberOfEnqueuedCommands + ")");
                } else {
                    MktLog.d(LOG_TAG, getTraceName(command) + "(" + command.commandId + ") Enqueued (queue=" + mNumberOfEnqueuedCommands + ")");
                }
            }
            
            mHandler.obtainMessage(COMMAND_MSG_ID, command).sendToTarget();
        } catch (Throwable th) {
            if (LogSettings.MARKET) {
                MktLog.w(LOG_TAG, getTraceName() + " execute:enqueue:exception:" + th.toString());
            }
            synchronized (_lock) {
                mNumberOfEnqueuedCommands--;
            }

            try {
                if (mWakeLock != null) {
                    mWakeLock.release();
                }
            } catch (Throwable thIn) {
                if (LogSettings.MARKET) {
                    MktLog.w(LOG_TAG, getTraceName() + " execute:lock:release:exception:" + thIn.toString());
                }
            }
        }
    }

    /**
     * Request to destroy the processor after completion of latest enqueued
     * command.
     * 
     * @param cleanUpPendingCommands
     *            if <code>true</code> all pending commands will be cleaned and
     *            the processor will be destroyed after completion of current
     *            command execution, otherwise all commands will be processed
     *            before clean up
     */
    public void destroy(boolean cleanUpPendingCommands) {
        if (LogSettings.MARKET) {
            MktLog.w(LOG_TAG, getTraceName() + " destroy...");
        }
        synchronized (_lock) {
            if (!mActive) {
                return;
            }
            mActive = false;
        }

        if (cleanUpPendingCommands) {
            try {
                mHandler.removeMessages(COMMAND_MSG_ID);
            } catch (Throwable th) {
                if (LogSettings.MARKET) {
                    MktLog.e(LOG_TAG, getTraceName() + " destroy:removeMessages:exception:" + th.toString());
                }
            }
        }

        try {
            mHandler.sendEmptyMessage(DESTROY_MSG_ID);
        } catch (Throwable th) {
            if (LogSettings.MARKET) {
                MktLog.e(LOG_TAG, getTraceName() + " destroy:enqueue:exception:" + th.toString());
            }
        }
    }

    /**
     * Defines COMMAND message identifier.
     */
    private static final int COMMAND_MSG_ID = 0;

    /**
     * Defines DESTROY message identifier.
     */
    private static final int DESTROY_MSG_ID = 1;

    /**
     * Keeps trace prefix.
     */
    private String mTraceName;

    /**
     * Returns processor name for traces.
     * 
     * @return processor name
     */
    private String getTraceName() {
        if (mTraceName == null) {
            mTraceName = "CMD[" + mName + "]";
        }
        return mTraceName;
    }

    /**
     * Returns command name for traces.
     * 
     * @return command name
     */
    private String getTraceName(Command command) {
        if (command.mTraceName == null) {
            command.mTraceName = "CMD[" + mName + "/" + command.mName + "]";
        }
        return command.mTraceName;
    }
    
    /**
     * A command to be executed.
     */
    public static abstract class Command implements Runnable {
        private long mTotalTimeExecutionLimit;
        private long mTimeExecutionLimit;
        private long mEnqueueTime;
        private long mStartTime;
        private long mFinishTime;
        private String mName;
        private String mTraceName;
        private int commandId = 0;
        
        /**
         * Returns enqueuing time.
         * @return
         */
        public long getEnqueueTime() {
            return mEnqueueTime;
        }
        
        /**
         * Returns start time (<code>run()</code> execution)
         * @return
         */
        public long getStartTime() {
            return mStartTime;
        }
        
        /**
         * Returns finish of (<code>run()</code>) execution.
         * 
         * @return
         */
        public long getFinishTime() {
            return mFinishTime;
        }
        
        /**
         * Returns label for tracing.
         * 
         * @return label for tracing.
         */
        public String getTraceName() {
            return mTraceName;
        }
        
        /**
         * Construct a command.
         * 
         * @param name
         *            short name of the command for logging
         */
        public Command(String name) {
            if (name == null || name.trim().length() == 0) {
                throw new IllegalArgumentException("Commnad<init>:invalid name parameter");
            }
            mName = name;
            mTotalTimeExecutionLimit = 0;
            mTimeExecutionLimit = 0;
        }
        
        /**
         * Construct a command.
         * 
         * @param name
         *            short name of the command for logging
         * @param timeExecutionLimit
         *            defines limit for command execution (run method
         *            execution), if the value will exceed a warning will be
         *            logged, 0 is magic number means do not check the limitation
         * @param totalTimeExecutionLimit
         *            defines limit for total command execution (pending and run
         *            execution), if the value will exceed a warning will be
         *            logged, 0 is magic number means do not check the limitation
         */
        public Command(String name, long timeExecutionLimit, long totalTimeExecutionLimit) {
            if (name == null || name.trim().length() == 0) {
                throw new IllegalArgumentException("Commnad<init>:invalid name parameter");
            }
            mName = name;
            mTotalTimeExecutionLimit = totalTimeExecutionLimit;
            mTimeExecutionLimit      = timeExecutionLimit;
        }
    }
}
