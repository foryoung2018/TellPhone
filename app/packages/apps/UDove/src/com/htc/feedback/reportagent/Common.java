package com.htc.feedback.reportagent;

import com.htc.lib0.htcdebugflag.HtcWrapHtcDebugFlag;

public class Common {
    
        public final static String TAG = "MyReportAgent";
        public final static String WAKELOCK_TAG = "Wakelock4Data";
        public final static String WAKELOCK_S3 = "logS3Sender_133";
        public final static String WAKELOCK_S3_UB = "logS3Sender_30";
        public final static String WAKELOCK_POMELO = "logPomeloSender_133";
        public final static boolean DEBUG = HtcWrapHtcDebugFlag.Htc_DEBUG_flag;
        public final static boolean SECURITY_DEBUG = HtcWrapHtcDebugFlag.Htc_SECURITY_DEBUG_flag;
        
        public final static String APPID_REPORT_AGENT = "com.htc.reportagent";
        
        public final static String CATEGORY_INFO = "info";
       	public final static String KEY_INFO_SN = "SN";

        public final static String CATEGORY_COMMON = "common";
        public final static String KEY_COMMON_RETRY = "retry";
        
        public final static String CATEGORY_LOG = "log";
        public final static String KEY_LOG_FREQ = "freq";
        public final static String KEY_LOG_CACHE = "cache";
        
        public final static String CATEGORY_POLICY = "policy";
        public final static String KEY_POLICY_FREQ = "freq";
        
        public final static String CATEGORY_BG_POLICY = "provisioning_engine";
        public final static String KEY_BG_POLICY_ENGINE = "enable";
        
        public final static String CATEGORY_BUDGET = "budget";
        public final static String KEY_BUDGET_PERIOD = "period";
        public final static String KEY_BUDGET_PREFIX_MOBILE = "mobile_";
        public final static String KEY_BUDGET_PREFIX_OTHER = "other_";
        public final static String KEY_BUDGET_PREFIX_ALL = "all_";
        public final static String KEY_BUDGET_SUFFIX_UL = "UL";
        public final static String KEY_BUDGET_SUFFIX_DL = "DL";
        public final static String KEY_BUDGET_SUFFIX_TOTAL = "total";
        public final static String KEY_BUDGET_SUFFIX_CALC_UNIT = "calc_unit";
        
        public final static String VALUE_BUDGET_TYPE_ABS_MB = "1";
        public final static String VALUE_BUDGET_TYPE_PERCENTAGE = "2";
        
        public final static long KILOBYTE_TO_BYTES = 1024L;
        public final static long MEGABYTE_TO_KILOBYTES = 1024L;
        public final static long MEGABYTE_TO_BYTES = MEGABYTE_TO_KILOBYTES * KILOBYTE_TO_BYTES;
        
    	public final static long SECOND_TO_MILLISECONDS = 1000L; 
    	public final static long MINUTE_TO_SECONDS = 60L; 
    	public final static long HOUR_TO_MINUTES = 60L; 
    	public final static long DAY_TO_HOURS = 24L; 

    	public final static long MINUTE_TO_MILLISECONDS = MINUTE_TO_SECONDS * SECOND_TO_MILLISECONDS; 
    	public final static long HOUR_TO_MILLISECONDS = HOUR_TO_MINUTES * MINUTE_TO_MILLISECONDS; 
    	public final static long DAY_TO_MILLISECONDS = DAY_TO_HOURS * HOUR_TO_MILLISECONDS; 
    	
    	public final static boolean FAKE_POLICY_SERVER = false;
    	public final static boolean FAKE_BUDGET_PPERIOD = false;
    	public final static boolean FAKE_BUDGET_SIZE = false;
    	
    	public static final String STR_UNKNOWN = "unknown";
    	public static final String STR_DISABLED = "NA";
    	public static final String RELATIVE_LOG_FOLDER_PATH = "log_folder";
    	public static final String ZIP_FILE_ENTITY = "file_entity";
    	public static final String STR_DEFAULT_PRIVACY_VERSION = "1.0";
    	
    	public static final String STR_HEADER_APPID = "tellhtc.header";
    	public static final String STR_CATEGORY_REGION = "region";
    	public static final String STR_CATEGORY_CITY = "city";
    	public static final String STR_CATEGORY_TIMEZONE = "timezone";
    	public static final String STR_CATEGORY_MODEL_ID = "model_id"; // tell Tarcy the typo in the spec.
    	public static final String STR_CATEGORY_DEVICE_ID = "device_id";
    	public static final String STR_CATEGORY_DEVICE_SN = "device_sn";
    	public static final String STR_CATEGORY_CID = "cid";
    	public static final String STR_CATEGORY_ROM_VERSION = "rom_version";
    	public static final String STR_CATEGORY_SENSE_VERSION = "sense_version";
    	public static final String STR_CATEGORY_PRIVACY_VERSION = "privacy_statement_version";
    	
    	public static final String STR_KEY_ENABLE = "enable";
    	public static final String STR_VALUE_ENABLED = "1";
    	
    	public static final String STR_KEY_ADMIN_AREA = "AdminArea";
    	public static final String STR_KEY_SUBADMIN_AREA = "SubAdminArea";
    	public static final String STR_KEY_LOCALITY = "Locality";
    	
    	public static final String STR_DATA_PATH = "/data";
    	
    	public static final String STR_ERROR_LOG_TYPE = "ERROR";
    	public static final String STR_USAGE_LOG_TYPE = "USAGE";
		
		public static final String STR_COM_HTC_FEEDBACK = "com.htc.feedback";
		public static final String STR_HTC_APP_CRASH = "HTC_APP_CRASH";
		public static final String STR_HTC_APP_ANR = "HTC_APP_ANR";
		public static final String STR_SYSTEM_CRASH = "SYSTEM_CRASH";
		public static final String STR_LASTKMSG = "LASTKMSG";
		public static final String STR_UPLOADED_COUNT = "uploaded_count";
		
		public static final String STR_HTC_UP = "HTC_UP";
		public static final String STR_HTC_UB = "HTC_UB";
		public static final String STR_USER_TRIAL_FEEDBACK = "USER_TRIAL_FEEDBACK";
		public static final String STR_ROSIE_DIED = "ROSIE_DIED";
		public static final String STR_HTC_ROSIE_DIED = "HTC_ROSIE_DIED";
		public static final String STR_HTC_HOME_RESTART = "HTC_HOME_RESTART";
		public static final String STR_APP_FEEDBACK = "APP_FEEDBACK";
		public static final String STR_HTC_APP_NATIVE_CRASH = "HTC_APP_NATIVE_CRASH";
		public static final String STR_TEST = "TEST";
		
		public static final String STR_HTC_POWER_EXPERT = "HTC_POWER_EXPERT"; // sy_wang, 20140328, Old tag of power expert log, will be dropped
		public static final String STR_HTC_PWR_EXPERT = "HTC_PWR_EXPERT"; // sy_wang, 20140328, New tag of power expert log
		public static final String STR_HTC_DEVICE_OVERHEATING = "HTC_DEVICE_OVERHEATING";
		public static final String STR_HTC_DEVICE_NO_CHARGING = "HTC_DEVICE_NO_CHARGING";
		public static final String STR_HTC_MODEM_RESET = "HTC_MODEM_RESET";
		public static final String STR_HTC_HW_RST = "HTC_HW_RST";
		public static final String STR_HTC_MODEM_ABNORMAL = "HTC_MODEM_ABNORMAL";
		
		public static final String STR_SYSTEM_WTF = "SYSTEM_WTF";
		public static final String STR_HTC_APP_WTF = "HTC_APP_WTF";
		public static final String STR_HTC_POMELO_USAGE_LOG = "HTC_ULOGDATA";
		
		public static final String STR_DEFAULT_SENSE_ID = "HSV_0000";
		public static final String STR_SENSE_ID_PREFIX = "HSV_";
		
		// sy_wang, Move from frameworks/base/core/java/android/provider/Settings$HtcISecure.java, begin
		// [BEGIN] HTC feedback client
		/**
		 * Flag for allowing ActivityManagerService to send ACTION_APP_ERROR intents
		 * on application crashes and ANRs. If this is disabled, the crash/ANR dialog
		 * will never display the "Report" button.
		 * Type: int ( 0 = disallow, 1 = allow )
		 * @exthide
		 */
		public static final String SEND_HTC_ERROR_REPORT = "send_htc_error_report";

		/**
		 * Flag for allowing user to turn on/off HTC feedback client UI.
		 * Type: int ( 0 = disallow, 1 = allow )
		 * @exthide
		 */
		public static final String HTC_ERROR_REPORT_SETTING = "htc_error_report_setting";
		
		/**
		 * Tell HTC privacy statement version
		 * Type: string
		 * @exthide
		 */
		public static final String TELL_HTC_PRIVACY_VERSION = "tell_htc_privacy_version";
		/**
		 * User Profile privacy statement version
		 * Type: string
		 * @exthide
		 */
		public static final String USER_PROFILE_PRIVACY_VERSION = "user_profile_privacy_version";
		/**
		 * Error Report privacy statement version
		 * Type: string
		 * @exthide
		 */
		public static final String ERROR_REPORT_PRIVACY_VERSION = "error_report_privacy_version";

		/**
		 * Setting for user to select prefer network to send error report.
		 * Type: int ( 0 = 3G or Wi-Fi, 1 = Wi-Fi only )
		 * @exthide
		 */
		public static final String HTC_ERROR_REPORT_PREFER_NETWORK = "htc_error_report_prefer_network";

		/**
		 * Allow user to send report automatically when error occurs
		 * Type: int ( 0 = manually send, 1 = automatically send )
		 * @exthide
		 */
		public static final String HTC_ERROR_REPORT_AUTO_SEND = "htc_error_report_auto_send";

		/**
		 * Allow user to send application log
		 * Type: int ( 0 = disallow, 1 = allow )
		 * @exthide
		 */
		public static final String SEND_HTC_APPLICATION_LOG = "send_htc_application_log";
		// [END] HTC feedback client

		// begin-tellhtc-enable-sense.com-logs, pitt_wu, 2011/06/30
		/**
		 * [sense 3.5] sense.com log is the second kind of usage log in Tell Htc. The value 1 
		 *			 means sense.com logs are allowed to send to Tell Htc server. Otherwise, 
		 *			 value 0.
		 * Type	 : int
		 * value : 0 = disable sense.com logs, 1 = enable sense.com logs
		 * 
		 * @exthide
		 */
		public static final String TELLHTC_ENABLE_SENSE_DOT_COM_LOG = "tellhtc_enable_sense_dot_com_log";
		// end-tellhtc-enable-sense.com-logs
		// sy_wang, Move from frameworks/base/core/java/android/provider/Settings$HtcISecure.java, end
		
		// Flag to show usage report preference in TellHTC's setting page
		public static final String SHOW_HTC_APPLICATION_LOG = "show_htc_application_log";
		
		// Flag to know device network is WiFi only or both WiFi and mobile data
		public static final String BOTH_NETWORK_OPTION = "both_network_option";
}
