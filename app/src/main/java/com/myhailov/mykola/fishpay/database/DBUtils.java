package com.myhailov.mykola.fishpay.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        ArrayList<Contact> allContacts = new ArrayList<>();

        Map<String, Contact> appContactsPhones = new HashMap<>();
        for (Contact appContact : appContacts) {
            if (appContact.isActiveUser()){
                String phone = appContact.getPhone();
                if (!appContactsPhones.containsKey(phone)) appContactsPhones.put(phone,  appContact);
            }
        }

        for (Contact deviceContact : deviceContacts){
            String phone = deviceContact.getPhone();
            if (appContactsPhones.containsKey(phone)) {
                Contact contact = appContactsPhones.get(phone);
                if (contact.getPhoto() == null && deviceContact.getPhoto() != null)
                    contact.setPhoto(deviceContact.getPhoto());
            } else allContacts.add(makeContactWithUniqueId(deviceContact));
        }


        for (Map.Entry<String, Contact> mapEntry : appContactsPhones.entrySet())
            allContacts.add(makeContactWithUniqueId(mapEntry.getValue()));


        contactsTable.deleteAll();
        contactsTable.insertInTx(allContacts);
    }

    private static Contact makeContactWithUniqueId(Contact appContact) {
        String phone = appContact.getPhone();
        long userId = appContact.getUserId();
        String name =  appContact.getName();
        String surname = appContact.getSurname();
        String photo = appContact.getPhoto();
        Contact newContact = new Contact();
        newContact.setPhone(phone);
        newContact.setUserId(userId);
        newContact.setName(name);
        newContact.setSurname(surname);
        newContact.setPhoto(photo);
        return newContact;
    }
}
