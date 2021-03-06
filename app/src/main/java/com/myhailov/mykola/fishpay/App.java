package com.myhailov.mykola.fishpay;


import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.myhailov.mykola.fishpay.database.DaoMaster;

import io.fabric.sdk.android.Fabric;
import org.greenrobot.greendao.database.Database;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/** Created by Mykola Myhailov  on 16.11.17.*/

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
     /*   FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        Fabric.with(this, new Crashlytics());

*/
        Database db = new DaoMaster.DevOpenHelper(this, "notes-db").getWritableDb();
        DaoMaster.dropAllTables(db, true);
        DaoMaster.createAllTables(db, true);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/NewFiraSansExtraCondensed-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }

/*    private Activity mCurrentActivity = null;
    public Activity getCurrentActivity(){
        return mCurrentActivity;
    }
    public void setCurrentActivity(Activity mCurrentActivity){
        this.mCurrentActivity = mCurrentActivity;
    }*/
}
