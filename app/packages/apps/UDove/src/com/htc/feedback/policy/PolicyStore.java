package com.htc.feedback.policy;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.htc.feedback.reportagent.Common;
import com.htc.feedback.reportagent.policy.PolicyPreference;
import com.htc.feedback.reportagent.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class PolicyStore {
    private static final String TAG = "PolicyStore";
    private static final String APPID_OF_SECOND_SWITCH = "tellhtc_client";
    private final static String CATEGORY_OF_SECOND_SWITCH_OF_ERROR_REPORT = "error_report";
    private static final String KEY_ENABLE = "enable";
    static final String KEY_DUE_DATE = "due_date";
    static final String KEY_DEFAULT_VALUE="default_value";
    private static PolicyStore sInstance;
    public synchronized static PolicyStore getInstance(Context context){
        if (sInstance == null)
            sInstance = new PolicyStore(context);
        return sInstance;
    }
        
    private Context mContext;
    private DatabaseHelper mDatabaseHelper;
    private Bundle mPolicyBundle;
    private PolicyStore(Context context) {
        mContext = context;
        mDatabaseHelper = new DatabaseHelper(mContext, null);
        init();
    }
    
    public Bundle getPolicy() {
        return mPolicyBundle;
    }
    
    public DatabaseHelper getDatabase() {
        return mDatabaseHelper;
    }
    
    private void init() {
        Log.d(TAG, "[Init] Get cached policy bundle");

        if (!PolicyPreference.isDefaultPolicyAlreadyLoad(mContext)) {
            boolean success = setDefaultPolicy();
            if (success) {
                PolicyPreference.setDefaultPolicyAlreadyLoad(mContext);
                Log.d(TAG, "[Init] Success set default policies");
            }
        }

        renewPolicyBundle();
    }

    private boolean setDefaultPolicy() {
        // Load directly from our hard coding.
        // TODO: setDefaultPolicy() may be called from UI thread, so we should still handle the rare case that DB I/O block UI thread
        DefaultPolicyLoader loader = new DefaultPolicyLoader(mDatabaseHelper);
        return loader.loadDefaultPoliciesWithoutBundle();
    }

    // Download new policy to apply (provision) will call this function
    public boolean setPolicy(Bundle bundle) {
    	boolean result = false;
        DefaultPolicyLoader loader = new DefaultPolicyLoader(mDatabaseHelper);
        // Will not initiate policyLoader and directly insert 1 by 1
        if(bundle != null){
            if(loader.loadProvisionPolicy(bundle)) {
                renewPolicyBundle();
                result = true;
                Log.d(TAG, "[setPolicy] setPolicyInternal() succeeds");
            }
            else  //This is an abnormal case, since if calling provision, it must have bundle (new policies for provision)
                Log.d(TAG, "[setPolicy] ABNORMAL setPolicyInternal() called. But nothing gets updated. ");
        }
        
        return result;
    }
    
    /**
     * Get policies from database, and cache policies in memory
     */
    private void renewPolicyBundle() {
        
        try{
            mPolicyBundle = mDatabaseHelper.getPolicy();
        } catch(Exception e) {
        	Log.e(TAG, "Exception when get policies from database", e);
        }
            
        // Put the second switch of error report in the first level of policy bundle. It can speed up searching in the bundle in UPolicy
		String valueOfLogErrorReportBySecondSwitch = getPolicyValue(mPolicyBundle, APPID_OF_SECOND_SWITCH, CATEGORY_OF_SECOND_SWITCH_OF_ERROR_REPORT, KEY_ENABLE);
		if(!TextUtils.isEmpty(valueOfLogErrorReportBySecondSwitch))
			mPolicyBundle.putBoolean(UPolicy.KEY_SECOND_SWITCH_OF_ERROR_REPORT, "1".equals(valueOfLogErrorReportBySecondSwitch));
		else
			if(Common.DEBUG) Log.d("value: "+valueOfLogErrorReportBySecondSwitch+", appid: "+APPID_OF_SECOND_SWITCH+", category: "+CATEGORY_OF_SECOND_SWITCH_OF_ERROR_REPORT+", key: "+KEY_ENABLE);
    }
    
    private static String getPolicyValue(Bundle allPolicy, String appid, String category, String key) {

        if(allPolicy != null && !TextUtils.isEmpty(appid) && !TextUtils.isEmpty(category) && !TextUtils.isEmpty(key)) {
            if(allPolicy != null) {
                Bundle appBundle = allPolicy.getBundle(appid);
                if(appBundle != null) {
                    Bundle cateBundle = appBundle.getBundle(category);
                    if(cateBundle != null) {
                        Bundle keyBundle = cateBundle.getBundle(key);
                        if(keyBundle != null) {
                            String value = keyBundle.getString(key);
                            
                            // When the policy is expired, use the default value if it has
                            long dueDate = keyBundle.getLong(KEY_DUE_DATE);
                            String defaultValue = null;
                            if(dueDate != -1 && dueDate < System.currentTimeMillis()) { //Expired policy,reset to default
                                if((defaultValue = keyBundle.getString(KEY_DEFAULT_VALUE)) != null)
                                    value = defaultValue;
                            }
                            if(Common.DEBUG) Log.d(TAG, "appid: "+ appid+", category: "+ category +", key: "+key+", value: "+value+", due date: "+dueDate
                                    +", current time: "+System.currentTimeMillis()+", default value: "+defaultValue);
                            
                            // If the policy is expired without default value, then return its original set value, else return null
                            return value == null ? "" : value;
                        }
                    }
                }
            }
        }

        return "";
    }

    private static class DefaultPolicyLoader{
        
        private DatabaseHelper mDBHelper;
        private HashMap<String,Object> prePolicy=null;
        
        public DefaultPolicyLoader(DatabaseHelper dbHelper) {
            mDBHelper = dbHelper;
        }

        public boolean loadProvisionPolicy(Bundle policy){
            Log.d(TAG,"[loadProvisionPolicy]Provisioning, incrementally insert policies.");
            return setPolicyInternal(policy);
        }

        private boolean loadDefaultPoliciesWithoutBundle(){
            if(!hasPoliciesInDB()){
                // Because no bundle inside, load default hard-coded policies only    
                //force to set provisioning frequency as 2 days,added by Ricky 2012.08.07
                //setEngPolicyFreq(48);
                setBasicPolicy(false);
                //setUserProfilingPolicy(false);   
                //setDebuggingRomPolicyInNeed(false);
                Log.d(TAG,"[loadDefaultPoliciesWithoutBundle]No policies in DB,batch insert policies.");
                boolean result = insertAllDefaultPolicy();
                Log.d(TAG, "[loadDefaultPoliciesWithoutBundle] insertAllDefaultPolicy="+result);
                return result;
//                return insertAllDefaultPolicy(); //because it 'MUST' insert data, so if <0 means failed
            } else {
                setBasicPolicy(true);
                //setUserProfilingPolicy(true);    
                //setDebuggingRomPolicyInNeed(true);
                Log.d(TAG,"[loadDefaultPoliciesWithoutBundle]has policies in DB,incrementally insert policies.");
                return true;
            }
        }
        
        private boolean hasPoliciesInDB(){
            int count=mDBHelper.getPolicyCount();
            Log.d(TAG, "The policy count in policy table="+count);
            if(count>0) return true;
            else return false;
        }
        
        private boolean setPolicyInternal(Bundle policy) {
            int nChanged = 0;
            if(mDBHelper != null) {
                String _appid="", _category="", _key="", _value="";
                Bundle _appBundle=null, _cateBundle=null, _keyBundle=null;
                Set<String> _categorySet = null, _keySet = null;
                Iterator<String> _categoryIter = null, _keyIter = null;
                long _endTime = -1;
                Set<String> _set = policy.keySet();
                if(_set != null) {
                    Iterator<String> iter = _set.iterator();
                    while(iter != null && iter.hasNext()) {
                        _appid = iter.next();
                        if(!TextUtils.isEmpty(_appid)) {
                            _appBundle = policy.getBundle(_appid);
                            if(_appBundle != null) {
                                _categorySet = _appBundle.keySet();
                                if(_categorySet != null) {
                                    _categoryIter = _categorySet.iterator();
                                    while(_categoryIter != null && _categoryIter.hasNext()) {
                                        _category = _categoryIter.next();
                                        if(!TextUtils.isEmpty(_category)) {
                                            _cateBundle = _appBundle.getBundle(_category);
                                            if(_cateBundle != null) {
                                                _keySet = _cateBundle.keySet();
                                                if(_keySet != null) {
                                                    _keyIter = _keySet.iterator();
                                                    while(_keyIter != null && _keyIter.hasNext()) {
                                                        _key = _keyIter.next();
                                                        if(!TextUtils.isEmpty(_key)) {
                                                            _keyBundle = _cateBundle.getBundle(_key);
                                                            if(_keyBundle != null) {
                                                                _value = _keyBundle.getString(_key);
                                                                if(!TextUtils.isEmpty(_value)) {
                                                                    _endTime = _keyBundle.getLong(KEY_DUE_DATE);
                                                                    nChanged += mDBHelper.InsertPolicy(_appid, _category, _key, _value, _endTime) ? 1 : 0;
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return nChanged > 0;
        }
        
        private void setBasicPolicy(boolean hasDataInDB) {
            String [][] BasicPolicy = PolicyTable.getBasicPolicy();
            for(int i=0; i<BasicPolicy.length; i++) {
                if(hasDataInDB)
                    mDBHelper.InsertPolicy(BasicPolicy[i][0], BasicPolicy[i][1], BasicPolicy[i][2], BasicPolicy[i][3]);
                else
                    addIntoPolicyVTable(BasicPolicy[i][0], BasicPolicy[i][1],BasicPolicy[i][2], BasicPolicy[i][3]);
            }
        }
        
       // This method is for preload policy from SIE and hard-code, 2011/04/18
        private int addIntoPolicyVTable(String appid,String category,String key, String value){
            if(prePolicy==null){ // First policy in SIE
                Log.d(TAG,"New prePolicy(policy cache)");
                prePolicy=new HashMap();
                HashMap app_table = new HashMap();
                HashMap cat_table= new HashMap();
                cat_table.put(key, value);
                app_table.put(category, cat_table);
                prePolicy.put(appid, app_table);
                Log.d(TAG,"Insert one row in virtual table, appid="+appid+",category="+category+",key="+key+",value="+value);
                return 1;
            }
            else{
                HashMap app_table=(HashMap)prePolicy.get(appid);
                if(app_table!=null){
                    HashMap cat_table=(HashMap)app_table.get(category);
                    if(cat_table!=null){
                        String value_old = (String)cat_table.get(key);
                        if(value_old!=null){
                            Log.d(TAG,"The key value set is already set, Appid="+appid+", Category="+category+", Key="+key+". Already set value="+value_old+" wanna to set value="+value+".");
                            return 1;
                        }
                        else{ // No value for this key, put it into cat_table
                            cat_table.put(key, value);
                            app_table.put(category, cat_table);
                            Log.d(TAG,"Insert one row in virtual table, appid="+appid+",category="+category+",key="+key+",value="+value);
                            return 1;
                        }
                    }
                    else {
                        cat_table=new HashMap();
                        Log.d(TAG,"Add new Category, category="+category);
                        cat_table.put(key, value);
                        app_table.put(category, cat_table);
                        Log.d(TAG,"Insert one row in virtual table, appid="+appid+",category="+category+",key="+key+",value="+value);
                        return 1;
                    }
                }
                else {
                    app_table = new HashMap();
                    HashMap cat_table= new HashMap();
                    cat_table.put(key, value);
                    Log.d(TAG,"Add new Category, category="+category);
                    app_table.put(category, cat_table);
                    Log.d(TAG,"Add new app, app="+appid);
                    prePolicy.put(appid, app_table);
                    Log.d(TAG,"Insert one row in virtual table, appid="+appid+",category="+category+",key="+key+",value="+value);
                    return 1;
                }
            }
        }
        
        // Batch insert all policy after OOBE
        private boolean insertAllDefaultPolicy(){
            int count = 0 ;
            if(prePolicy==null) {
                Log.d(TAG, "No policy in policy virtual table");
                return false;
            }
            ArrayList<String[]> p_al=new ArrayList();
            Iterator iterator_1 = prePolicy.keySet().iterator();
             while(iterator_1!=null&&iterator_1.hasNext()) {
                 String appid = (String)iterator_1.next();
                 if(prePolicy.get(appid) != null) { // sy, Fix CQG issue, 20130429
                     Iterator iterator_cat=((HashMap)prePolicy.get(appid)).keySet().iterator();
                     while(iterator_cat!=null&&iterator_cat.hasNext()){
                         String category = (String)iterator_cat.next();
                         if(((HashMap)prePolicy.get(appid)).get(category) != null) { // sy, Fix CQG issue, 20130429
                             HashMap key_value = (HashMap)((HashMap)prePolicy.get(appid)).get(category);
                             if(key_value != null) { // sy, Fix CQG issue, 20130429
                                 Iterator iterator_key=key_value.keySet().iterator();
                                 while(iterator_key!=null&&iterator_key.hasNext()){
                                     String key=(String)iterator_key.next();
                                     String value=(String)key_value.get(key);
                                     String[] parameters={appid,category,key,value,"-1"};
                                     Log.d(TAG," Put parameters for insert appid="+appid+",category="+category+",key="+key+",value="+value+".");
                                     p_al.add(parameters);
                                     count++;
                                 } //End of key-value iterator which inside a category
                             }
                             else
                                 Log.d(TAG, "key_value is null, count=" + count);
                         }
                         else
                             Log.d(TAG, "((HashMap)prePolicy.get(appid)).get(category) is null, count=" + count);
                     } // End of category iterator inside an appid 
                 }
                 else
                     Log.d(TAG, "prePolicy.get(appid) is null, count=" + count);
             } // End of appid iterator
             Log.d(TAG,"The count for policy insert, "+(count+1));
             int suc= mDBHelper.batchInsertPolicy(p_al);
             if(suc>0) return true;
             else return false;
        }
    
    }    
}
