package com.htc.feedback.searchprovider;

import static com.htc.feedback.searchprovider.SearchIndexablesContract.INDEXABLES_RAW_COLUMNS;                 // #columns
import static com.htc.feedback.searchprovider.SearchIndexablesContract.COLUMN_INDEX_RAW_RANK;                  // = 0
import static com.htc.feedback.searchprovider.SearchIndexablesContract.COLUMN_INDEX_RAW_TITLE;                 // = 1
import static com.htc.feedback.searchprovider.SearchIndexablesContract.COLUMN_INDEX_RAW_SUMMARY_ON;            // = 2
import static com.htc.feedback.searchprovider.SearchIndexablesContract.COLUMN_INDEX_RAW_SUMMARY_OFF;           // = 3
import static com.htc.feedback.searchprovider.SearchIndexablesContract.COLUMN_INDEX_RAW_ENTRIES;               // = 4
import static com.htc.feedback.searchprovider.SearchIndexablesContract.COLUMN_INDEX_RAW_KEYWORDS;              // = 5
import static com.htc.feedback.searchprovider.SearchIndexablesContract.COLUMN_INDEX_RAW_SCREEN_TITLE;          // = 6
import static com.htc.feedback.searchprovider.SearchIndexablesContract.COLUMN_INDEX_RAW_CLASS_NAME;            // = 7
import static com.htc.feedback.searchprovider.SearchIndexablesContract.COLUMN_INDEX_RAW_ICON_RESID;            // = 8
import static com.htc.feedback.searchprovider.SearchIndexablesContract.COLUMN_INDEX_RAW_INTENT_ACTION ;        // = 9
import static com.htc.feedback.searchprovider.SearchIndexablesContract.COLUMN_INDEX_RAW_INTENT_TARGET_PACKAGE; // = 10
import static com.htc.feedback.searchprovider.SearchIndexablesContract.COLUMN_INDEX_RAW_INTENT_TARGET_CLASS ;  // = 11
import static com.htc.feedback.searchprovider.SearchIndexablesContract.COLUMN_INDEX_RAW_KEY;                   // = 12
import static com.htc.feedback.searchprovider.SearchIndexablesContract.COLUMN_INDEX_RAW_USER_ID ;              // = 13

import android.content.Context;
import android.os.Build;
import com.htc.feedback.searchprovider.SearchIndexableData;

/**
 * <H3>Indexable raw data for Search.</H3>
 * 
 * <P>This is the raw data used by the Indexer and should match its 
 * data model.</P>
 * 
 * @author TJ Tsai
 * @since {@link Build.VERSION_CODES#KITKAT}, 2014.09.23
 * 
 * @see {@link Indexable}
 * @see {@link android.provider.SearchIndexableResource}.
 */
public class SearchIndexableRaw extends SearchIndexableData {

	/**
	 * Title's raw data.
	 */
	public String title;

	/**
	 * Summary's raw data when the data is "ON".
	 */
	public String summaryOn;

	/**
	 * Summary's raw data when the data is "OFF".
	 */
	public String summaryOff;

	/**
	 * Entries associated with the raw data (when the data can have several
	 * values).
	 */
	public String entries;

	/**
	 * Keywords' raw data.
	 */
	public String keywords;

	/**
	 * Fragment's or Activity's title associated with the raw data.
	 */
	public String screenTitle;

	public SearchIndexableRaw(Context context) {
		super(context);
	}
	
	/**
	 * Map [object fields] into [array] for cursor use. 
	 * @param instance
	 * @return
	 */
	public static Object[] toCursorData(SearchIndexableRaw instance) {
		Object [] data = new Object[INDEXABLES_RAW_COLUMNS.length];
		
		// mapping [object fields] --into--> array 
		data[COLUMN_INDEX_RAW_RANK]                  = instance.rank;
		data[COLUMN_INDEX_RAW_TITLE]                 = instance.title;
		data[COLUMN_INDEX_RAW_SUMMARY_ON]            = instance.summaryOn;
		data[COLUMN_INDEX_RAW_SUMMARY_OFF]           = instance.summaryOff;
		data[COLUMN_INDEX_RAW_ENTRIES]               = instance.entries;
		data[COLUMN_INDEX_RAW_KEYWORDS]              = instance.keywords;
		data[COLUMN_INDEX_RAW_SCREEN_TITLE]          = instance.screenTitle;
		data[COLUMN_INDEX_RAW_CLASS_NAME]            = instance.className;
		data[COLUMN_INDEX_RAW_ICON_RESID]            = instance.iconResId;
		data[COLUMN_INDEX_RAW_INTENT_ACTION]         = instance.intentAction;
		data[COLUMN_INDEX_RAW_INTENT_TARGET_PACKAGE] = instance.intentTargetPackage;
		data[COLUMN_INDEX_RAW_INTENT_TARGET_CLASS]   = instance.intentTargetClass;
		data[COLUMN_INDEX_RAW_KEY]                   = instance.key;
		data[COLUMN_INDEX_RAW_USER_ID]               = instance.userId;
		
		return data;
	}
}
