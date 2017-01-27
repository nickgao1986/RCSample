package com.example.nickgao.database;

import android.database.sqlite.SQLiteDatabase;


abstract class RCMDbTable {

    private static final String TAG = "[RC]RCMDbTable";
    
    
    /**
     * @return the DB table name
     */
    abstract String getName();
    
    
    /**
     * Creates the DB table according to the DB scheme
     * 
     * @param db
     */
    abstract void onCreate(SQLiteDatabase db);

    
    /**
     * Upgrades DB table to the new scheme.
     * The data in the matching columns is preserved.
     * Supported operations:
     * 1. Add columns.
     *    New columns are filled with the default column values according to the table scheme,
     *    or with NULL if no default value is specified. 
     * 2. Remove columns.
     * If any additional operations need to be performed during table upgrade
     * (e.g. new column contents should be calculated based on other columns,
     * or existing columns shall be filled with the default values instead of copying from the old table, etc.), 
     * the method should be overridden for the specific table.
     * 
     * @param db
     * @param oldVersion
     * @param newVersion
     * @param tempName - unique temporary name which may be securely used during table upgrade.
     */
    void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion, String tempName) {
        
        //Rename old table to temporary name
        RCMDbUtils.renameTable(db, getName(), tempName);
        //Create clear table according to the new scheme
        onCreate(db);
        //Copy content of the matching columns from the old table to the new one
        joinColumns(db, tempName, getName());
        
        //Delete old table
        RCMDbUtils.dropTable(db, tempName);
        
        initTableContent(db);
    }
    
    void initTableContent(SQLiteDatabase db) {
    }
   
    void joinColumns(SQLiteDatabase db, String tempName, String tableName) {
    	RCMDbUtils.joinColumns(db, tempName, tableName);
    }


}
