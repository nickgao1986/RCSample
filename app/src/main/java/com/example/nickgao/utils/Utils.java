package com.example.nickgao.utils;

import java.io.*;
import java.util.UUID;

import com.example.nickgao.logging.MktLog;
import com.example.nickgao.rcproject.RingCentralApp;


public class Utils {
	
    private static final String rgch = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+*";
    private static final int bBad = 0xff;

    private static final String TAG = "[RC] Utils";
    private static String sID = null;
    private static final String INSTALLATION = "INSTALLATION";
	
    public synchronized static String getUid() {
        if (sID == null) {
            File installation = new File(RingCentralApp.getContextRC().getFilesDir(), INSTALLATION);
            try {
                if (!installation.exists())
                    writeInstallationFile(installation);
                sID = readInstallationFile(installation);
            } catch (Exception e) {
                MktLog.e(TAG,"getUid error e="+e);
            }
        }
        return sID;
    }
    
	  public static void clearUid() {
	        sID = null;
	        File installation = new File(RingCentralApp.getContextRC().getFilesDir(), INSTALLATION);
	        try {
	            if (installation.exists()) {
	                installation.delete();
	            }
	        } catch (Exception e) {
	            MktLog.e(TAG,"clearUid error e="+e);
	        }

	    }

	    private static String readInstallationFile(File installation) throws IOException {
	        RandomAccessFile f = new RandomAccessFile(installation, "r");
	        byte[] bytes = new byte[(int) f.length()];
	        f.readFully(bytes);
	        f.close();
	        return new String(bytes);
	    }

	    private static void writeInstallationFile(File installation) throws IOException {
	        FileOutputStream out = new FileOutputStream(installation);
	        String id = UUID.randomUUID().toString();
	        out.write(id.getBytes());
	        out.close();
	    }
}
