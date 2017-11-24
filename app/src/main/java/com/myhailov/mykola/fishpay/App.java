package com.myhailov.mykola.fishpay;


import android.app.Activity;
import android.app.Application;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/** Created by Mykola Myhailov  on 16.11.17.*/

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
     /*   FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        Fabric.with(this, new Crashlytics());
*/
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
