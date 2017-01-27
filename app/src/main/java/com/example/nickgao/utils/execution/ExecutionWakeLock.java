/**
 * Copyright (C) 2010-2011, RingCentral, Inc. 
 * All Rights Reserved.
 */
package com.example.nickgao.utils.execution;

/**
 * Execution wake lock interface. 
 */
public interface ExecutionWakeLock {
    /**
     * Acquire the lock (incremental). 
     */
    void acquire();
    
    /**
     * Release the lock (decremental).
     */
    void release();
    
    /**
     * Release all.
     */
    void releaseAll();
    
    /**
     * Returns if the lock is acquired.
     * 
     * @return if the lock is acquired
     */
    boolean isHeld();
}
