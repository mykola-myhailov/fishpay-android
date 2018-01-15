package com.myhailov.mykola.fishpay.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Mykola Myhailov  on 15.01.18.
 */

public class DBUtils {

    private static DaoSession daoSession;
    public static DaoSession getDaoSession(Context context) {
        if (daoSession == null) {
            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, "notes-db", null);
            SQLiteDatabase db = helper.getWritableDatabase();
            DaoMaster daoMaster = new DaoMaster(db);
            daoSession = daoMaster.newSession();
        }
        return daoSession;
    }
}
