package com.example.nickgao.utils;

/**
 * Copyright (C) 2010-2013, RingCentral, Inc. 
 * All Rights Reserved.
 */

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.telephony.TelephonyManager;

import com.example.nickgao.logging.*;

import java.util.Locale;

public class NetworkUtils {
	
    private static final String TAG = "[RC] NetworkUtils";
    
    private static NetworkState previousNetworkState = NetworkState.NONE;

    public enum NetworkState {
        SERVERREQUEST, FULL, WIFI, MOBILE, NONE
    }

    public static boolean isGPRSNetwork(Context context) {
        return ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getNetworkType() == TelephonyManager.NETWORK_TYPE_GPRS;
    }

    public static int getCelluarCallState(Context context){
    	return ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getCallState();
    }
    
    private static NetworkState isNetworkStateChanged(Context context, NetworkState state) {
        if (!state.equals(previousNetworkState)) {
            previousNetworkState = state;
            context.sendBroadcast(new Intent(RCMConstants.ACTION_NETWORK_STATE_CHANGED));
        }
        return state;
    }
    
    public static boolean HasLocalDialer(Context context) {
		TelephonyManager manager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		if(manager.getPhoneType() == TelephonyManager.PHONE_TYPE_NONE) {
			return false;
		}
		return true;
	}
    

    public static String getDetailedNetState(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        String info = null;
        if (connManager != null) {
            NetworkInfo activeNet = connManager.getActiveNetworkInfo();
            if (activeNet != null) {
                info = activeNet.toString();
            }
        }
        if (info == null) {
            info = "?";
        }
        return info;
    }
    
    /*
     * 
     */
    public static NetworkState getActiveNetworkState(Context context){
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivity.getActiveNetworkInfo();
    	if( activeNetwork != null ){
    		int type = activeNetwork.getType();
    		if (type == ConnectivityManager.TYPE_WIFI ) return NetworkState.WIFI;
    		else if(type == ConnectivityManager.TYPE_MOBILE ) return NetworkState.MOBILE;
    	}
    	
    	return NetworkState.NONE;
    }
    /*
     * 
     */
    public static NetworkState getNetworkState(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobile = connectivity.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        boolean wifiEnabled = (wifi != null) && wifi.isAvailable() && wifi.isConnected();
        boolean mobileEnabled = (mobile != null) && mobile.isAvailable() && mobile.isConnected();
        
        NetworkInfo activeNetwork = connectivity.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isAvailable() && activeNetwork.isConnectedOrConnecting()) {
            int type = activeNetwork.getType();
            
            if (type == ConnectivityManager.TYPE_WIFI || type == ConnectivityManager.TYPE_MOBILE) {
                if (wifiEnabled) {
                    if (mobileEnabled) {
                        return isNetworkStateChanged(context, NetworkState.FULL);
                    } else {
                        return isNetworkStateChanged(context, NetworkState.WIFI);
                    }
                } else {
                    return isNetworkStateChanged(context, NetworkState.MOBILE);
                }
            }
            return isNetworkStateChanged(context, NetworkState.FULL);
        } else if ( wifiEnabled && mobileEnabled) {
            return isNetworkStateChanged(context, NetworkState.FULL);
        } else if (wifiEnabled) {
            return isNetworkStateChanged(context, NetworkState.WIFI);
        } else if (mobileEnabled) {
            return isNetworkStateChanged(context, NetworkState.MOBILE);
        } else {
            return isNetworkStateChanged(context, NetworkState.NONE);
        }
    }

    public static boolean hasMobileModule(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobile = connectivity.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        
        if(mobile == null) {
        	return false;
        }
    	return true;
    }

  
    public static boolean isAirplaneMode(Context context) {
//        int mode = Settings.System.getInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0);
//        return (mode != 0);
    	return false;
    }
    
    
    /**
     * detect current network availability based on phone state
     *
     * @param context
     * @return boolean as network accessibility
     */
    public static boolean isRCAvaliable(Context context) {
    	
    	try {
    		NetworkState network_state = getNetworkState(context); 
    		boolean result = network_state != NetworkState.NONE;
    		return result;
    	} catch (Exception e) {
    		return false;
    	}
    }
    
    
    public static String getNetworkInfoStateLabel(NetworkInfo.State state) {
        if (state == NetworkInfo.State.CONNECTED) {
            return "CONNECTED";
        }

        if (state == NetworkInfo.State.CONNECTING) {
            return "CONNECTING";
        }
        
        if (state == NetworkInfo.State.DISCONNECTED) {
            return "DISCONNECTED";
        }
        
        if (state == NetworkInfo.State.DISCONNECTING) {
            return "DISCONNECTING";
        }
        
        if (state == NetworkInfo.State.SUSPENDED) {
            return "SUSPENDED";
        }
        
        return "UNKNOWN";
    }
    
    public static String getNetworkTypeLabel(int type) {
        switch (type) {
        case 0: return "MOBILE";
        case 1: return "WIFI";
        case 2: return "MOBILE_MMS";
        case 3: return "MOBILE_SUPL";
        case 4: return "MOBILE_DUN";
        case 5: return "MOBILE_HIPRI";
        case 6: return "WIMAX";
        case 7: return "BLUETOOTH";
        case 8: return "DUMMY";
        case 9: return "ETHERNET";
        }
        return "UNKNOWN";
    }
    
    public static String getRadioNetworkTypeLabel(int type) {
        switch (type) {
        case 0:
            return "NETWORK_TYPE_UNKNOWN";
        case 1:
            return "NETWORK_TYPE_GPRS";
        case 2:
            return "NETWORK_TYPE_EDGE";
        case 3:
            return "NETWORK_TYPE_UMTS";
        case 4:
            return "NETWORK_TYPE_CDMA";
        case 5:
            return "NETWORK_TYPE_EVDO_0";
        case 6:
            return "NETWORK_TYPE_EVDO_A";
        case 7:
            return "NETWORK_TYPE_1xRTT";
        case 8:
            return "NETWORK_TYPE_HSDPA";
        case 9:
            return "NETWORK_TYPE_HSUPA";
        case 10:
            return "NETWORK_TYPE_HSPA";
        case 11:
            return "NETWORK_TYPE_IDEN";
        case 12:
            return "NETWORK_TYPE_EVDO_B";
        case 13:
            return "NETWORK_TYPE_LTE";
        case 14:
            return "NETWORK_TYPE_EHRPD";
        case 15:
            return "NETWORK_TYPE_HSPAP";
        }
        return "UNKNOWN";
    }
    
    /**
     * Returns current RC/Network state as string for logging.
     * 
     * @param ctx
     *            the execution context
     * 
     * @return current RC/Network state as string for logging
     */
    public static String getNetworkStatusAsString(Context ctx) {
        if (ctx != null) {
            try {
                StringBuffer sb = new StringBuffer("NET [RC:");
                sb.append(getNetworkState(ctx));
                sb.append("; ACTIVE:");

                String activeNetwork = null;
                int activeNetworkType = -1;
                int activeNetworkSubType = -1;
                int radioNetworkType = TelephonyManager.NETWORK_TYPE_UNKNOWN;
                ConnectivityManager connManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
                TelephonyManager telephonyMng = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
                if (connManager != null) {
                    NetworkInfo activeNet = connManager.getActiveNetworkInfo();
                    if (activeNet != null) {
                        activeNetworkType = activeNet.getType();
                        activeNetworkSubType = activeNet.getSubtype();
                        activeNetwork = activeNet.toString();
                    }
                }
                if (telephonyMng != null) {
                    radioNetworkType = telephonyMng.getNetworkType();
                }

                sb.append(getNetworkTypeLabel(activeNetworkType));
                sb.append('(');
                sb.append(activeNetworkSubType);
                sb.append(')');
                sb.append(" RADIO:");
                sb.append(getRadioNetworkTypeLabel(radioNetworkType));
                sb.append(" {");
                if (activeNetwork != null) {
                    sb.append(activeNetwork);
                } else {
                    sb.append("NULL");
                }
                sb.append("}]");
                return sb.toString();
            } catch (Throwable th) {

            }
        }
        return "";
    }
    
    private static class RcNetInfo {
        boolean valid = false;
        int networkType = 0;
        String extra = null;
        boolean isRoaming = false;
        String brand = null;
        String operatorName = null;
        String simOperatorName = null;
    }
    
    private static final boolean USE_FORCED_RE_RIGESTRATION = false;
    private static final boolean USE_FORCED_RE_RIGESTRATION_FOR_VERIZON_LTE = USE_FORCED_RE_RIGESTRATION && false;
    
    private static final boolean USE_FORCED_SIP_PORT = true;
    private static final boolean USE_FORCED_SIP_PORT_FOR_VERIZON_LTE = USE_FORCED_SIP_PORT && true;

    private static final long MIN_TIMEOUT_UNDER_NAT_IN_SEC = 30;
    private static final int FORCED_SIP_TRANSPORT_PORT_FOR_VERIZON_LTE = 5077;
    private static final String VERIZON = "verizon";
    
    /**
     * Returns if needed forced re-registration: if return value is 0 - not
     * required, otherwise even server returns expiration value for next
     * registration, the returned value shall be used instead
     * 
     * @param ctx the execution context
     * @return 0 or minimal registration timeout required in seconds
     */
    public static final long getForcedExpirationIfRecuired(Context ctx) {
        long value = 0;
        if (USE_FORCED_RE_RIGESTRATION) {
            if (ctx == null) {
                return value;
            }
            RcNetInfo info = getRcNetInfo(ctx);

            if (!info.valid) {
                return value;
            }
            if (USE_FORCED_RE_RIGESTRATION_FOR_VERIZON_LTE) {
                if (isVerizonLTEWorkaroundRequired(info)) {
                    return MIN_TIMEOUT_UNDER_NAT_IN_SEC;
                }
            }
        }
        return value;
    }
    
    /**
     * Returns if needed local port for SIP transport: if return value is 0 - not
     * required, otherwise the port
     * 
     * @param ctx the execution context
     * @return 0 or port
     */
    public static final int getDedicatedSIPTransportLocalPortIfrequired(Context ctx) {
        int value = 0;
        if (USE_FORCED_SIP_PORT) {
            if (ctx == null) {
                return value;
            }
            RcNetInfo info = getRcNetInfo(ctx);

            if (!info.valid) {
                return value;
            }
            if (USE_FORCED_SIP_PORT_FOR_VERIZON_LTE) {
                if (isVerizonLTEWorkaroundRequired(info)) {
                    return FORCED_SIP_TRANSPORT_PORT_FOR_VERIZON_LTE;
                }
            }
        }
        return value;
    }

    private static final boolean isVerizonLTEWorkaroundRequired(RcNetInfo info) {
        if (info == null || !info.valid) {
            return false;
        }
        
        if (info.networkType == ConnectivityManager.TYPE_WIFI ||
                info.networkType == 6) { //WiMax
            return false;
        }
        
        if (matched(info.operatorName, VERIZON)) {
            if (LogSettings.MARKET) {
                MktLog.i(TAG, "Verizon LTE Workaround : P1 detected");
            }
            return true;
        }

        if (matched(info.simOperatorName, VERIZON) && !info.isRoaming ) {
            if (LogSettings.MARKET) {
                MktLog.i(TAG, "Verizon LTE Workaround : P2 detected");
            }
            return true;
        }

        if (matched(info.extra, "VZWINTERNET")) {
            if (LogSettings.MARKET) {
                MktLog.i(TAG, "Verizon LTE Workaround : P3 detected");
            }
            return true;
        }
        

        if (matched(info.brand, VERIZON) && !info.isRoaming ) {
            if (LogSettings.MARKET) {
                MktLog.i(TAG, "Verizon LTE Workaround : P4 detected");
            }
            return true;
        }
        
        if (matched(info.extra, "VZWAPP")) {
            if (LogSettings.MARKET) {
                MktLog.i(TAG, "Verizon LTE Workaround : P5 detected");
            }
            return true;
        }

        if (matched(info.simOperatorName, "VZW")  && !info.isRoaming) {
            if (LogSettings.MARKET) {
                MktLog.i(TAG, "Verizon LTE Workaround : P6 detected");
            }
            return true;
        }

        return false;
    }
    
    private static final boolean matched(String value, String key) {
        if (key == null || value == null) {
            return false;
        }
        
        try {
            String v = value.toLowerCase(Locale.US);
            String k = key.toLowerCase(Locale.US);
            return v.contains(k);
        } catch (Throwable th) {
        }
        return false;
    }
    
    /**
     * Returns current net state
     * 
     * @param ctx
     *            the execution context
     * @return current net state
     */
    private static final RcNetInfo getRcNetInfo(Context ctx) {
        RcNetInfo info = new RcNetInfo();
        ConnectivityManager connManager = null;
        try {
            connManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        } catch (Throwable th) {
        }
        
        if (connManager == null) {
            return info;
        }
        
        TelephonyManager telephonyMng = null;
        try {
            telephonyMng = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        } catch (Throwable th) {
        }
        
        NetworkInfo activeNet = connManager.getActiveNetworkInfo();
        if (activeNet != null) {
            info.valid = true;
            info.networkType = activeNet.getType();
            info.isRoaming = activeNet.isRoaming();
            try {
                info.extra = activeNet.getExtraInfo();
            } catch (Throwable th) {
            }
        } else {
            return info;
        }
        
        if (telephonyMng != null) {
            try {
                info.operatorName = telephonyMng.getNetworkOperatorName();
            } catch (Throwable th) {
            }
            try {
                info.simOperatorName = telephonyMng.getSimOperatorName();
            } catch (Throwable th) {
            }
            try {
                info.isRoaming = telephonyMng.isNetworkRoaming();
            } catch (Throwable th) {
            }
        }
        
        info.brand = Build.BRAND;
        
        if (LogSettings.MARKET) {
            StringBuffer sb = new StringBuffer();
            sb.append("NetInfo: type=");
            sb.append(info.networkType);
            sb.append("; roam=");
            sb.append(info.isRoaming);
            sb.append("; op=[");
            sb.append(getNormalizedString(info.operatorName));
            sb.append("]; sim=[");
            sb.append(getNormalizedString(info.simOperatorName));
            sb.append("]; extra=[");
            sb.append(getNormalizedString(info.extra));
            sb.append("]; brand=[");
            sb.append(getNormalizedString(info.brand));
            sb.append("]");
            MktLog.d(TAG, sb.toString());
        }
        return info;
    }
    
    private static final String getNormalizedString(String str) {
        return ((str == null) ? "null" : str);
    }
    
    public static String getNetworkClass(Context context) {
        TelephonyManager mTelephonyManager = (TelephonyManager)
                context.getSystemService(Context.TELEPHONY_SERVICE);
        int networkType = mTelephonyManager.getNetworkType();
        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return "2G";
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return "3G";
            case TelephonyManager.NETWORK_TYPE_LTE:
                return "4G";
            default:
                return "Unknown";
        }
    }
}
