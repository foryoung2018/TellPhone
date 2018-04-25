package com.htc.feedback.policy;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.text.TextUtils;

import com.htc.feedback.reportagent.Common;
import com.htc.feedback.reportagent.util.Log;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    private static final String DATABASE_NAME = "errorreport.db";
    private static final int DATABASE_VERSION = 1;
    private static final boolean IS_DEBUG = Common.DEBUG;
    
    // policy table begin
    private static final String POLICY_TABLE_NAME = "policy";
    private static final String POLICY_ID = "_id";
    private static final String POLICY_APPID = "appid";
    private static final String POLICY_CATEGORY = "category";
    private static final String POLICY_KEY = "key";
    private static final String POLICY_VALUE = "value";
    private static final String POLICY_DUE_DATE = "due_date";
    private static final String POLICY_DEFAULT_VALUE="default_value";
    // policy table : column index should align its name above
    private static final int    POLICY_ID_IDX = 0;
    private static final int    POLICY_APPID_IDX = 1;
    private static final int    POLICY_CATEGORY_IDX = 2;
    private static final int    POLICY_KEY_IDX = 3;
    private static final int    POLICY_VALUE_IDX = 4;
    private static final int    POLICY_DUE_DATE_IDX = 5;
    private static final int    POLICY_DEFAULT_VALUE_IDX = 6;
    // policy table end
    
    public DatabaseHelper(Context context, String dbName) {
        super(context, TextUtils.isEmpty(dbName) ? DATABASE_NAME : dbName, null, DATABASE_VERSION);
        Log.d(TAG, "[DatabaseHelper constructor]");
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "[onCreate]");
        updateDatabase(db, 0, DATABASE_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "[onUpgrade]");
        updateDatabase(db, 0, DATABASE_VERSION);
    }

    //[2016.08.30 Eric Lu] Override onDowngrade to prevent default RuntimeException, and pass Lint code analysis.
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.e(TAG, "[onDowngrade] Shouldn't have this case.");
    }

    private void updateDatabase(SQLiteDatabase db, int oldVersion, int newVersion){
        Log.d(TAG,"version: from "+oldVersion+" to "+newVersion);
        try {
            if (oldVersion < 1) {
                db.execSQL("CREATE TABLE " + POLICY_TABLE_NAME + " ( "
                        + POLICY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + POLICY_APPID + " TEXT, "
                        + POLICY_CATEGORY + " TEXT, "
                        + POLICY_KEY + " TEXT, "
                        + POLICY_VALUE + " TEXT, " 
                        + POLICY_DUE_DATE + " INTEGER DEFAULT -1, "
                        + POLICY_DEFAULT_VALUE + " TEXT " + ")"
                );
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to create policy table", e);
        }
    }
    

    public boolean InsertPolicy(String appid, String category, String key, String value) {
        return InsertPolicy(appid, category, key, value, -1);
    }
    
    /**
     * Insert new policy or update old policy with new value, 
     * policy ( _id INTEGER PRIMARY KEY AUTOINCREMENT, appid TEXT, 
     * category TEXT, key TEXT, value TEXT, due_date INTEGER DEFAULT -1,
     * by_default INTEGER DEFAULT 1,default_value text)
     */
    public boolean InsertPolicy(String appid, String category, String key, String value, long dueDate) {
    	if(IS_DEBUG) Log.d(TAG,"[InsertPolicy] appid: "+appid+", category : "+category+", key: "+key+", value: "+value+", due date:"+dueDate);
        boolean bChanged = false;
        if(!TextUtils.isEmpty(appid) && !TextUtils.isEmpty(category) 
                && !TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)){
            
            final String whereClause = POLICY_APPID + "=?" + " AND " + POLICY_CATEGORY + "=?"
                    + " AND " + POLICY_KEY + "=?";
            final String[] whereArgs = new String[]{appid, category, key};
            
            SQLiteDatabase db = null;
            Cursor c = null;
            try {
                db = getWritableDatabase();
                c = db.query(POLICY_TABLE_NAME, 
                        new String []{POLICY_ID, POLICY_VALUE, POLICY_DUE_DATE}, 
                        whereClause, 
                        whereArgs, 
                        null, null, null);
                // for existed policy, update the value and due date, therefore no need to update default value
                if(c != null && c.getCount() > 0 && c.moveToFirst()) {
                	if(IS_DEBUG) Log.d("cursor[0]: "+c.getInt(0)+", cursor[1]: "+c.getString(1)+", cursor[2]: "+c.getLong(2));
                    //Has the previous SIE setting, no need to update
                    if(dueDate==-1) bChanged=false;
                    else if(!value.equals(c.getString(1)) || dueDate != c.getLong(2)) {
                        int id = c.getInt(0);
                        ContentValues values = new ContentValues();
                        values.put(POLICY_VALUE, value);
                        values.put(POLICY_DUE_DATE, dueDate);
                        db.update(POLICY_TABLE_NAME, values, POLICY_ID+"=?", new String []{Integer.toString(id)});
                        bChanged = true;
                    }
                }
                else {
                    // New policies,including those are default
                    ContentValues values = new ContentValues();
                    values.put(POLICY_APPID, appid);
                    values.put(POLICY_CATEGORY, category);
                    values.put(POLICY_KEY, key);
                    values.put(POLICY_VALUE, value);
                    values.put(POLICY_DUE_DATE, dueDate);
                    // Default : values.put(POLICY_IS_DEFAULT,1)
                    // Add one more column for default value
                    if(dueDate==-1)  //This is the default policy
                    values.put(POLICY_DEFAULT_VALUE,value);
                    db.insert(POLICY_TABLE_NAME, "garbge", values);
                    bChanged = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try{
                    if(c != null){
                        c.close();
                        c = null;
                    }
                    if(db != null) {
                        db.close();
                        db = null;
                    }
                } catch(Exception e) {}
            }
        }
        return bChanged;
    }
    
    public Bundle getPolicy() {
        String appid=null, category=null, key=null, value=null;
        long dueDate=-1;
        Bundle policyBundle = new Bundle();
        SQLiteDatabase db = getReadableDatabase();
        if(db != null) {
            Cursor c = null;
            try {
                c = db.query(POLICY_TABLE_NAME, null, null, null, null, null, null);
                if(c != null) {
                    c.moveToFirst();
                    while(!c.isAfterLast()){
                        appid = c.getString(POLICY_APPID_IDX);
                        category = c.getString(POLICY_CATEGORY_IDX);
                        key = c.getString(POLICY_KEY_IDX);
                        value = c.getString(POLICY_VALUE_IDX);
                        dueDate = c.getLong(POLICY_DUE_DATE_IDX);
                        if(!c.isNull(POLICY_DEFAULT_VALUE_IDX)){
                            String default_value= c.getString(POLICY_DEFAULT_VALUE_IDX);
                            PolicyUtils.putPolicy(policyBundle, appid, category, key, value, dueDate,default_value);
                        }
                        else
                            PolicyUtils.putPolicy(policyBundle, appid, category, key, value, dueDate);
                        c.moveToNext();
                    }
                }
            } catch(Exception e) {
                e.printStackTrace();
            } finally {
                if(c != null) {
                    try {
                        c.close();
                    } catch(Exception e) {}
                }
            }
        }
        
        return policyBundle;
        //return policyBundle.size() > 0 ? policyBundle : null;
    }
    
    public Cursor queryPolicyFromSystem() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor result = null;
        if (db != null) {
            try {
                result = db.query(POLICY_TABLE_NAME, null, null, null, null, null, null);
                if (result != null)
                    result.moveToFirst();
            } catch (Exception e) {
                Log.e(TAG, "Failed to query policy from system!", e);
            }
        }
        return result;
    }
    
    // The function is only for insert policy checking
    public int getPolicyCount(){
        int rowCount=0;
        Cursor c = null;
        SQLiteDatabase db = null;
        try{
            db=getReadableDatabase();
            c = db.rawQuery("select count(*) from "+POLICY_TABLE_NAME,null);
            if(c!=null){
                c.moveToFirst();
                rowCount=c.getInt(0);  //Get the count from policy table
            }
            else 
                Log.d(TAG,"[Select from Policy Table]"+" The policy table doens't exist");
            }catch (Exception e) {
                Log.d(TAG,"[get readableDB fails] method: "+"getPolicyCount");
                e.printStackTrace();
            } finally {
            try { // sy, Fix CQG issue, 20130429
                if(c != null)
                    c.close();
            } catch(Exception e) {
                Log.e(TAG,"[getPolicyCount]Closing Cursor Exception,Method: "+"batchInsertPolicy "+" Error msg: "+e.getMessage());
            }
            try { // sy, Fix CQG issue, 20130429
                if(db != null)
                    db.close();
            } catch(Exception e) {
                Log.e(TAG,"[getPolicyCount]Clocing DB Exception,Method: "+"batchInsertPolicy "+" Error msg: "+e.getMessage());
            }
        }
        return rowCount;
        
    }
    
    // Add new method for batch insert policy when OOBE is just completed (or after factory reset),
    // No need to check if Policy exists or not since it should be empty
    // The SqlStatement is String array which consists 5 parameters used to insert statement : [appid,category,key,value,due_date]
    public int batchInsertPolicy(ArrayList<String[]> sqlParameters){
        int rowInsertCount=0;  // Return the total insert policies 
        SQLiteDatabase db = null;
        SQLiteStatement insert=null;
        //Make sure the policy table is empty and exist
        if(getPolicyCount()!=0) {
            return -1; // return -1 means there are polcies inside Policy table, so please insert 1 by 1 to prevent replacing
        }
        else{
            try{
            db=getWritableDatabase();
            db.beginTransaction();
            String sql = "insert into "+POLICY_TABLE_NAME+" ("+POLICY_APPID+","+POLICY_CATEGORY+","+POLICY_KEY+","+POLICY_VALUE+","+POLICY_DUE_DATE+","+ POLICY_DEFAULT_VALUE+ ") values(?,?,?,?,?,?)";
            insert = db.compileStatement(sql);
            
            if(insert != null) { // sy, Fix CQG issue, 20130429
                for(int i =0;i<sqlParameters.size();i++){
                    String[] parameters=sqlParameters.get(i);
                    insert.bindString(1, parameters[0]);
                    insert.bindString(2, parameters[1]);
                    insert.bindString(3, parameters[2]);
                    insert.bindString(4, parameters[3]);
                    insert.bindString(5, parameters[4]);
                    if(new Integer(parameters[4]).intValue()==-1) // The default policy, should set the default value
                        insert.bindString(6, parameters[3]);
                    else
                        insert.bindString(6, "");
                    
                    insert.executeInsert();
                    rowInsertCount++;  //Execute 1 insert and increase row count
                }
                db.setTransactionSuccessful();
                if(IS_DEBUG) Log.d(TAG,"[BatchInsert] done");
                db.endTransaction();
            }
            else { // sy, Fix CQG issue, 20130429
                if(IS_DEBUG) Log.d(TAG,"[BatchInsert] fail, insert is null");
                db.endTransaction();
            }
            }catch(Exception e){
                Log.e(TAG,"Method: "+"batchInsertPolicy "+" Error msg: "+e.getMessage());
            }
            finally{
                try{ // sy, Fix CQG issue, 20130429
                    if(insert != null)
                        insert.close();
                }catch(Exception e) {
                    Log.e(TAG,"[batchInsertPolicy]Closing SQLiteStatement Exception,Method: "+"batchInsertPolicy "+" Error msg: "+e.getMessage());
                }
                try{ // sy, Fix CQG issue, 20130429
                    if(db != null)
                        db.close();
                }catch(Exception e) {
                    Log.e(TAG,"[batchInsertPolicy]Clocing DB Exception,Method: "+"batchInsertPolicy "+" Error msg: "+e.getMessage());
                }
            }
        }
        return rowInsertCount;
    }

}
