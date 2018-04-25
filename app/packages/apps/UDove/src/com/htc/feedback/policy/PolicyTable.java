package com.htc.feedback.policy;

import com.htc.feedback.reportagent.util.Log;

/**
 * Date: 2014-04-09
 * Changes: Remove all UP policies except error report, configuration, neutral and second-switch policies.
 *          UP policies reply on black list. Otherwise, use white list. 
 *          a. error report policy still reply on white list
 *          b. neutral policy means not belong to error report or usage log. Ex. Tell HTC header policies
 *          c. second switches still reply on white list 
 */
public class PolicyTable {
    
	// Default white list for non-shipping ROM before L
//	private static final String [][] DEBUG_POLICY=  
//    {
//        // begin-engineering-S3-policies: used in S3 uploader only
//        {"com.htc.feedback",       "HTC_UB",                  "enable", "1"}, // nc-PPMC (non-provisioning server) has defined this item and we don't change it to com.htc.feedback.debug (2014-04-09)
//        {"com.htc.feedback.debug", "HTC_UP",                  "enable", "1"},
//        {"com.htc.feedback.debug", "TEST",                    "enable", "1"},
//        {"com.htc.feedback.debug", "HTC_APP_CRASH",           "enable", "1"},
//        {"com.htc.feedback.debug", "HTC_APP_ANR",             "enable", "1"},
//        {"com.htc.feedback.debug", "SYSTEM_CRASH",            "enable", "1"},
//        {"com.htc.feedback.debug", "HTC_HOME_RESTART",        "enable", "1"},
//        {"com.htc.feedback.debug", "HTC_APP_NATIVE_CRASH",    "enable", "1"},
//        {"com.htc.feedback.debug", "SYSTEM_WTF",              "enable", "1"},
//        {"com.htc.feedback.debug", "HTC_APP_WTF",             "enable", "1"},
//        {"com.htc.feedback.debug", "SYSTEM_ANR",              "enable", "1"}, // sy_wang, 20140407, report system anr
//        {"com.htc.feedback.debug", "LASTKMSG",                "enable", "1"},
//        {"com.htc.feedback.debug", "HTC_POWER_EXPERT",        "enable", "1"}, // This flag should be removed after HTC_PWR_EXPERT is applied in all ROM. (2014-04-09)
//        {"com.htc.feedback.debug", "HTC_PWR_EXPERT",          "enable", "1"}, // sy_wang, 20140328, New tag of power expert log
//        {"com.htc.feedback.debug", "HTC_DEVICE_OVERHEATING",  "enable", "1"},
//        {"com.htc.feedback.debug", "HTC_DEVICE_NO_CHARGING",  "enable", "1"},
//        {"com.htc.feedback.debug", "HTC_MODEM_RESET",         "enable", "1"},
//        {"com.htc.feedback.debug", "HTC_HW_RST",              "enable", "1"},
//        {"com.htc.feedback.debug", "HTC_MODEM_ABNORMAL",      "enable", "1"}
//        // end-engineering-S3-policies
//    };
	
    private static final String [][] BASIC_POLICY=
    {
        // begin-uploader-configuration
        {"com.htc.reportagent", "common", "retry",            "1"},
        {"com.htc.reportagent", "policy", "freq",             "168"}, // 7 days = 168 hours
        {"com.htc.reportagent", "log",    "freq",             "24"}, // 24 hours
        {"com.htc.reportagent", "log",    "cache",            "2"}, // 2 MB
        {"com.htc.reportagent", "budget", "period",           "720"}, // 1 month = 720 hours
        {"com.htc.reportagent", "budget", "mobile_calc_unit", "1"}, // 1: absolute MB
        {"com.htc.reportagent", "budget", "mobile_UL",        "50"}, // 50MB
        {"com.htc.reportagent", "budget", "mobile_DL",        "50"}, // 50MB
        {"com.htc.reportagent", "budget", "mobile_total",     "100"}, // 100MB
        {"com.htc.reportagent", "budget", "other_calc_unit",  "1"}, // 1: absolute MB
        {"com.htc.reportagent", "budget", "other_UL",         "-1"}, // unlimited
        {"com.htc.reportagent", "budget", "other_DL",         "-1"}, // unlimited
        {"com.htc.reportagent", "budget", "other_total",      "-1"}, // unlimited
        {"com.htc.reportagent", "budget", "all_calc_unit",    "1"}, // 1: absolute MB
        {"com.htc.reportagent", "budget", "all_UL",           "-1"}, // unlimited
        {"com.htc.reportagent", "budget", "all_DL",           "-1"}, // unlimited
        {"com.htc.reportagent", "budget", "all_total",        "-1"}, // unlimited
        // end-uploader-configuration
        
        // Since we change to black list from L, we should set default black list for shipping ROM
        //{"com.htc.feedback",    "HTC_UB",                     "enable", "0"}, // nc-PPMC (non-provisioning server) has defined this item and we don't change it to com.htc.feedback.debug (2014-04-09)
        {"com.htc.feedback",    "HTC_UP",                     "enable", "0"},
        {"com.htc.feedback",    "TEST",                       "enable", "0"},
	    {"com.htc.feedback",    "SYSTEM_WTF",                 "enable", "0"},
	    {"com.htc.feedback",    "HTC_APP_WTF",                "enable", "0"},
	    {"com.htc.feedback",    "HTC_POWER_EXPERT",           "enable", "0"}, // This flag should be removed after HTC_PWR_EXPERT is applied in all ROM. (2014-04-09)
	    {"com.htc.feedback",    "HTC_PWR_EXPERT",             "enable", "0"}, // sy_wang, 20140328, New tag of power expert log
	    {"com.htc.feedback",    "HTC_DEVICE_OVERHEATING",     "enable", "0"},
	    {"com.htc.feedback",    "HTC_DEVICE_NO_CHARGING",     "enable", "0"},
	    {"com.htc.feedback",    "HTC_MODEM_RESET",            "enable", "0"},
	    {"com.htc.feedback",    "HTC_HW_RST",                 "enable", "0"},
	    {"com.htc.feedback",    "HTC_MODEM_ABNORMAL",         "enable", "0"},
        // Default white list for shipping ROM before L
//        // begin-pomelo-error-report-policies
//        {"com.htc.feedback", "HTC_APP_CRASH",        "enable",    "1"}, // (ON:1, OFF:0)
//        {"com.htc.feedback", "SYSTEM_CRASH",         "enable",    "1"}, // (ON:1, OFF:0)
//        {"com.htc.feedback", "LASTKMSG",             "enable",    "1"}, // (ON:1, OFF:0)
//        {"com.htc.feedback", "HTC_APP_ANR",          "enable",    "1"}, // (ON:1, OFF:0)
//        {"com.htc.feedback", "extra",                "bugreport", "0"},
//        {"com.htc.feedback", "HTC_HOME_RESTART"    , "enable",    "1"}, // (ON:1, OFF:0), 2013/07/10, launcher java crash and native crash will be reported in shipping ROM
//        {"com.htc.feedback", "HTC_APP_NATIVE_CRASH", "enable",    "1"}, // sense 5.0MR, 2013/07/01, Android start to send native crash error report from JB43
//        {"com.htc.feedback", "SYSTEM_ANR",           "enable",    "1"}, // (ON:1, OFF:0), sy_wang, 20140407, report system anr
//        // end-pomelo-error-report-policies
        
        // begin-TellHTC-transmission-headers : used in pomelo uploader only
        {"tellhtc.header",      "region",                    "enable",   "1"}, // Sense 3.5
        {"tellhtc.header",      "city",                      "enable",   "1"}, // Sense 3.5
        {"tellhtc.header",      "timezone",                  "enable",   "1"}, // Sense 3.5
        {"tellhtc.header",      "model_id",                  "enable",   "1"}, // Sense 3.5
        {"tellhtc.header",      "device_id",                 "enable",   "1"}, // Sense 3.5
        {"tellhtc.header",      "device_sn",                 "enable",   "1"}, // Sense 3.5
        {"tellhtc.header",      "cid",                       "enable",   "1"}, // Sense 3.5
        {"tellhtc.header",      "rom_version",               "enable",   "1"}, // Sense 3.5
        {"tellhtc.header",      "sense_version",             "enable",   "1"}, // Sense 3.5
        {"tellhtc.header",      "privacy_statement_version", "enable",   "1"}, // Sense 3.5
        // begin-TellHTC-transmission-headers
        
        // begin-TellHTC-second-switch
        {"tellhtc_client",       "error_report",             "enable",   "1"}, // Sense 4.0
        // end-TellHTC-second-switch
    };
    
    public static String [][] getBasicPolicy() {
        Log.d("[getBasicPolicy] number: " + BASIC_POLICY.length);
        return BASIC_POLICY;
    }

}
