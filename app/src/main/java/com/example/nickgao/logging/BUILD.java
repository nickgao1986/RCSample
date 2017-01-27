package com.example.nickgao.logging;


public class BUILD {

	 public static final int BRAND = Brands.RC_BRAND;
	    public static final String VERSION_NAME = "6.4.0";
	    
	    /** Market package (for rating) **/
	    public static final String PACKAGE_NAME = "com.ringcentral.android";
	    public static final String SVN_REVISION = "239473";
	    public static final int CFG_REVISION = 3;
	    public static final String APP_FOLDER_NAME_ON_SDCARD = "rc";
	    
	    /**
	     * VoIP section
	     */
	    public static final boolean VOIP_ENABLED = true;
	    public static final boolean VOIP_ENABLED_BY_DEFAULT = true;
	    public static final boolean VOIP_MOBILE_3G_4G_ENABLED = true;
	    public static final boolean VOIP_MOBILE_3G_4G_ENABLED_BY_DEFAULT = false;
	    public static final boolean VOIP_INCOMING_ENABLED = true;
	    public static final boolean VOIP_INCOMING_ENABLED_BY_DEFAULT = true;
	    
	    /**
	     * Defines User-Agent header for SIP packets.
	     */
	    public static final String VOIP_SIP_USER_AGENT ="RC_AND_3.9.8_DEBUG";
	    
	    public static final boolean TEST_MODE = false;
	    
	    /**
	     * Outbound link will have next view URI.SIGNUP?ol={ENCRYPTED_DATA} - all about encrypted data you can find at: http://jira.dins.ru/browse/RC-34972
	     * bellow is flag which rules inclusion of transfering data
	     * and parameter data 
	     */
	    public static final boolean isOutboundLinkWithEncryptedDataTurnedOn = true;
	    /**
	     * Time interval to reminder user to proceed express setup,
	     */
	    public static final long SETUP_WIZARD_REMINDER_INTERVAL = (24 * 60 * 60 * 1000); // once in 24 hours
	    
	    /**
	     * Time interval to reminder user about expiration
	     */
	    public static final long ACCOUNT_EXPIRATION_REMINDER_INTERVAL = (24 * 60 * 60 * 1000); // once in 24 hours
	    /**
	     * Tune interval in days for starting notification about account expirations 7 days by default
	     */
	    public static final int ACCOUNT_EXPIRE_REMINDER_START_DAYS = 7;
	    /**
	     * Android:scheme/appUrlScheme value for mobile web url
	     */
	    public static final String APP_URL_SCHEME = "rcmobile";
		/**
		 * TOS EULA
		 */ 
	    public static final boolean isTosEULAEnabled = true;
	    public static final int TOS_VERSION = 1;
		/**
		 * TOS 911
		 */ 
	    public static final boolean isTos911Enabled = true;
	    /**
	     * Login Screen: Sign-Up button ("I'm a new user")
	     */ 
	    public static final boolean LOGIN_SCREEN_SIGNUP_ENABLED = true;
	    /**
	     * Settings: Company ('Phone System') section
	     */ 
	    public static final boolean SETTINGS_COMPANY_ENABLED = true;
	    /**
	     * Settings: 'My Incoming' section
	     */ 
	    public static final boolean SETTINGS_INCOMING_ENABLED = true;
	    /**
	     * Settings: Billing section
	     */ 
	    public static final boolean SETTINGS_BILLING_ENABLED = true;
	    /**
	     * Settings: Tell A Friend: "new" (Amarosa) Settings screen
	     */ 
	    public static final boolean SETTINGS_TELLFRIEND_ENABLED = true;    
	    /**
	     * Settings: Tell A Friend in the About Screen: "old" (pre-Amarosa)Settings screen
	     */ 
	    public static final boolean SETTINGS_LEGACY_ABOUT_TELLFRIEND_ENABLED = true;    
	    /**
	     * Login Screen: show/hide password recovery link
	     */ 
	    public static final boolean PASSWORD_RECOVERY_ENABLED=true;
	    /**
	     * Enable/Disable splash screen on startup
	     */
	    public static final boolean SPLASH_SCREEN_ENABLED = true;
	    /**
	     * Splash screen lifetime in millis
	     */
	    public static final long SPLASH_SCREEN_TIMEOUT = 2000l;
	    
	    /**
	     * Defines if traces to logcat enabled.
	     */
	    public static boolean TRACES_TO_LOGCAT_ENABLED = true;
	    
	    public static final boolean PERFORMANCE_MEASUREMENT_ENABLED = false;
	    
	    public static final boolean WHATISNEW_DIALOG_ENABLED = true;

	    public static final boolean INTRO_DIALOG_ENABLED = true;
	    
	    public static final boolean CRASH_REPORT_ENABLED = false;
	    
	    /**
	     * REST APP keys.
	     */
	    public static final String REST_APPID     = "9002";
	    public static final String REST_APPKEY = "1007B157FD38ecc5fd63b5ea4753D98C404D9BA9193d3CE526dfEAC2CE8CA517";
	    public static final String REST_APPSECRET = "56D332332D98db9a985D229AC651052730BF93609ee7b1318b0fDBFA15d4DFEC";
	    
	    /**
	     * Phone Number Utils
	     */
	    public static final String INTERNATIONAL_PREFIX 	= "011";
	    public static final String COUNTRY_NUMBER			= "1";
	    public static final String AREA_CODE				= "650";
	    
	    public static final boolean IS_SPECIAL_NEED_AREA_CODE	= false;
	    public static final boolean REST_CALL_LOG_ENABLE		= true;
	    public static final boolean EXTENSION_INFO_ENABLE		= true;
	    public static final boolean FCC_ZOOM_HARD_CODE			= false;
	    public static final boolean RATE_APP_ENABLE				= true;
	    
	    /** Zoom package name **/
	    public static final String ZOOM_PACKAGE_NAME 	= "com.ringcentral.meetings";
	    
	    /**
	     * Enable internal configuration screen for URL's
	     */    
	    public static class CONFIG_URI {
	        public static final String LAUNCH_KEY = "*3328433284#";
	    }
	    public static class FLURRY {
	        public static final boolean ENABLE = true;
	        public static final String APP_KEY = "QDY3MVK8JF9DKZPG227S";
	    }
	    
		public static class CloudStorage {
	        public static class Dropbox {
	            public static final boolean ENABLE = true;
	            public static final boolean MULTIPLE_ACCOUNTS_SUPPORT = false;
	            public static final int DISPLAY_PRIORITY = 30;
	            
	            /* www.dropbox.com, rcfaxout.dev@gmail.com, application : RingCentral CloudFax Dev */
	            public static final String APP_KEY = "tcmb7wpmjxcaf66";
	            public static final String APP_SECRET = "8lbuf5ewnn685r4";
	        }
	        public static class Box {
	            public static final boolean ENABLE = true;
	            public static final int DISPLAY_PRIORITY = 20;
	            
	            /* www.box.com, rcfaxout.dev@gmail.com, application : RingCentral CloudFax Dev */
	            public static final String APP_KEY = "gaxwb82ur2ut5ug8976xcadx7z3xjt4x";
				public static final String CLIENT_ID = "4x2fqyjo9kijtslu0dfvb4kyqoi5q72v";
	            public static final String CLIENT_SECRET = "dwM5DCgbMnHWAI3IWQN0CmQDT2XycPyx";
	        }
		    public static class GoogleDrive {
				public static final boolean ENABLE = false;
				public static final int DISPLAY_PRIORITY = 10;
				public static final boolean MULTIPLE_ACCOUNTS_SUPPORT = false;
			}
			
			public static final boolean ENABLE = GoogleDrive.ENABLE || Box.ENABLE || Dropbox.ENABLE;
			public static boolean TEST_MODE = false;
		}
	    
	    public static class ADMOB {
	        public static final boolean ENABLE = false;
	        public static final String APP_KEY = "";
	    }
	    
	    public static class GETJAR {
	        public static final boolean ENABLE = false;
	    }
	    
	    public static class GANALYT {
	    	public static final boolean ENABLE = false;
	    	public static final String APP_KEY = "UA-31119-4";
	    }
	    
	    /**
	     * For Deprecated fields, use getters from RCMConfig instead of direct use from BUILD.java
	     */
	    public static class URI {
	    	
	        /** JEDI **/
	    	public static final String JEDI = "https://ws.ringcentral.com/ringcentral/API/";
//	    	public static final String JEDI = "http://ws-up.lab.rcch.ringcentral.com/ringcentral/API/";
//	    	public static final String JEDI = "http://ws-tm2dev.lab.rcch.ringcentral.com/ringcentral/API/";
	    	
	    	/** SOAP **/
	        public static final String JEDI_SOAP_XNLNS = "http://service.ringcentral.com/";
	        
	        /** Messaging **/
	    	public static final String MSGSYNC = "https://sync.ringcentral.com/localstorage/msgmanager.dll";
//	    	public static final String MSGSYNC = "http://sync-up.lab.rcch.ringcentral.com/localstorage/msgmanager.dll";
//	    	public static final String MSGSYNC = "http://sync-tm2dev.lab.rcch.ringcentral.com/localstorage/msgmanager.dll";
	        
	    	/** I'm a new user **/
	        public static final String SIGNUP = "http://m.ringcentral.com/";
	        
	    	/** Setup URL Overridden by server**/
	    	public static final boolean SETUP_URL_OVERRIDING_ENABLE = true;    	
	    	
	        /** Setup **/
	        public static final String SETUP = "https://secure.ringcentral.com/rc-setup/mobile/";
	        
	        /** SignUp/Setup settings **/
	        public static final String SETUP_SIGNUP_SETTINGS = "https://secure.ringcentral.com/rc-setup/mobile/";

	        /** Base web settings URL **/
	        public static final String WEB_SETTINGS_BASE = "https://service.ringcentral.com/mobileweb/app.html";
//	        public static final String WEB_SETTINGS_BASE = "http://service-tm2dev.lab.rcch.ringcentral.com/mobileweb/app.html";
	    	
	    	/** Base web settings URL -  Enhance **/
	    	public static final String WEB_SETTINGS_BASE_ENHANCE = "https://service.ringcentral.com";
//	        public static final String WEB_SETTINGS_BASE_ENHANCE = "http://service-tm2dev.lab.rcch.ringcentral.com";
	    	
	        /** Password recovery URL **/
	    	public static final String PASSWORD_RECOVERY_URL = "https://service.ringcentral.com/login/password.asp";
	    	
	        /** HTTP Registration URL **/
	    	public static final String HTTPREG = "http://agentconnect.ringcentral.com/httpreg/rchttpreg.dll?Register";
//	    	public static final String HTTPREG = "http://agentconnect-up.lab.rcch.ringcentral.com/httpreg/rchttpreg.dll?Register";
//	    	public static final String HTTPREG = "http://agentconnect-tm2dev.lab.rcch.ringcentral.com/httpreg/rchttpreg.dll?Register";

	    	/** SIP Provider URL **/
	        public static final String URI_SIP_PROVIDER = "sip.ringcentral.com";
	        
	        /** SIP Outbound Proxy URL **/
	        public static final String URI_SIP_OUTBOUND_PROXY = "sip1.ringcentral.com:5090";
	        
	        /** Platform REST API server URL (PROTOCOL + HOST) **/
	        public static final String REST_SERVER_URL = "https://api.ringcentral.com";
//	        public static final String REST_SERVER_URL = "http://api-up.lab.rcch.ringcentral.com";
//	      public static final String REST_SERVER_URL = "http://api-tm2dev.lab.rcch.ringcentral.com";
	      
	    }
}
