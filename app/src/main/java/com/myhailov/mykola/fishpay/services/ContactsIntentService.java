package com.myhailov.mykola.fishpay.services;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;

import com.myhailov.mykola.fishpay.database.Contact;
import com.myhailov.mykola.fishpay.database.ContactDao;
import com.myhailov.mykola.fishpay.database.DBUtils;

import java.util.ArrayList;

/** Created by Mykola Myhailov  on 15.01.18. */

public class ContactsIntentService extends IntentService {

    private ArrayList<Contact> contacts;

    public ContactsIntentService() {
        super("ContactsIntentService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        contacts = getDeviceContactsInfo();
        saveContacts();
        uploadContactsRequest();
    }

    private void saveContacts() {
        ContactDao contactsTable = DBUtils.getDaoSession(this).getContactDao();
        contactsTable.deleteAll();
        for (Contact contactInfo: contacts) {
            Contact entity = new Contact();
            entity.setName(contactInfo.getName());
            entity.setPhone(contactInfo.getPhone());
            entity.setPhoto(contactInfo.getPhoto());
            contactsTable.insert(entity);
        }
    }


    private void uploadContactsRequest() {

    }


    //extracting contacts info from device. It take a few seconds, so must execute in background
    //each contact has one id, one photo, one name and can has many phone numbers or emails;

    private ArrayList<Contact> getDeviceContactsInfo() {
        ArrayList<Contact> contacts = new ArrayList<>();
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                if (hasPhoneNumber(cursor)) {
                    String contactId = getContactId(cursor);
                    String name = getContactName(cursor);
                    Uri photoUri = getPhotoUri(contactId);
                    Cursor cursorInfo = getCursorInfo(contentResolver, contactId);
                    if (cursorInfo != null) {
                        while (cursorInfo.moveToNext()){
                            String phone = getContactPhone(cursorInfo);
                            contacts.add(createContactInfo(phone, name, photoUri));
                        }
                        cursorInfo.close();
                    }
                }
            }
            cursor.close();
        }
        return contacts;
    }

    @NonNull
    private Contact createContactInfo(String phone, String name, Uri photoUri) {
        Contact contact = new Contact();
        contact.setName(name);
        contact.setPhone(phone);
        contact.setPhoto(photoUri.toString());
        contact.setUserId(0);
        return contact;
    }

    private Cursor getCursorInfo(ContentResolver contentResolver, String contactId) {
        return contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{contactId}, null);
    }

    private String getContactId(Cursor cursor) {
        return cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
    }

    private boolean hasPhoneNumber(Cursor cursor) {
        return Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0;
    }

    private Uri getPhotoUri(String id) {
        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.parseLong(id));
        return Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
    }

    private String getContactName(Cursor cursor) {
        return cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
    }

    private String getContactPhone(Cursor cursor) {
        return cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
    }


}
