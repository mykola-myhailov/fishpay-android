package com.myhailov.mykola.fishpay.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mykola Myhailov  on 15.01.18.
 */

public class DBUtils {

    public static DaoSession daoSession;
    public static DaoSession getDaoSession(Context context) {
        if (daoSession == null) {
            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, "notes-db", null);
            SQLiteDatabase db = helper.getWritableDatabase();
            DaoMaster daoMaster = new DaoMaster(db);
            daoSession = daoMaster.newSession();
        }
        return daoSession;
    }

    public static void saveAppContacts(Context context, ArrayList<Contact> appContacts) {
        ContactDao contactsTable = DBUtils.getDaoSession(context).getContactDao();
        List<Contact> deviceContacts = contactsTable.loadAll();
        contactsTable.deleteAll();
        ArrayList<String> appContactsPhones = new ArrayList<>();
        for (Contact appContact : appContacts) {
            if (appContact.isActiveUser()){
                String phone = appContact.getPhone();
                if (!appContactsPhones.contains(phone)) appContactsPhones.add(phone);
                contactsTable.insert(makeContactWithUniqueId(appContact));
            }
        }
        for (Contact deviceContact : deviceContacts){
            String phone = deviceContact.getPhone();
            if (!appContactsPhones.contains(phone))
                contactsTable.insert(makeContactWithUniqueId(deviceContact));
        }
    }

    private static Contact makeContactWithUniqueId(Contact appContact) {
        String phone = appContact.getPhone();
        long userId = appContact.getUserId();
        String name =  appContact.getName();
        String suname = appContact.getSurname();
        String photo = appContact.getPhoto();
        Contact newContact = new Contact();
        newContact.setPhone(phone);
        newContact.setUserId(userId);
        newContact.setName(name);
        newContact.setSurname(suname);
        newContact.setPhoto(photo);
        return newContact;
    }
}
