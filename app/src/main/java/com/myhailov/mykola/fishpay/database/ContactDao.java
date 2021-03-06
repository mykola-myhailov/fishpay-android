package com.myhailov.mykola.fishpay.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "CONTACT".
*/
public class ContactDao extends AbstractDao<Contact, Long> {

    public static final String TABLENAME = "CONTACT";

    /**
     * Properties of entity Contact.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property DbId = new Property(0, Long.class, "dbId", true, "_id");
        public final static Property UserId = new Property(1, long.class, "userId", false, "USER_ID");
        public final static Property ContactId = new Property(2, long.class, "contactId", false, "CONTACT_ID");
        public final static Property Phone = new Property(3, String.class, "phone", false, "PHONE");
        public final static Property Name = new Property(4, String.class, "name", false, "NAME");
        public final static Property Surname = new Property(5, String.class, "surname", false, "SURNAME");
        public final static Property Photo = new Property(6, String.class, "photo", false, "PHOTO");
        public final static Property IsActiveUser = new Property(7, boolean.class, "isActiveUser", false, "IS_ACTIVE_USER");
        public final static Property IsCheck = new Property(8, boolean.class, "isCheck", false, "IS_CHECK");
        public final static Property AmountToPay = new Property(9, float.class, "amountToPay", false, "AMOUNT_TO_PAY");
    }


    public ContactDao(DaoConfig config) {
        super(config);
    }
    
    public ContactDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"CONTACT\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: dbId
                "\"USER_ID\" INTEGER NOT NULL ," + // 1: userId
                "\"CONTACT_ID\" INTEGER NOT NULL ," + // 2: contactId
                "\"PHONE\" TEXT," + // 3: phone
                "\"NAME\" TEXT," + // 4: name
                "\"SURNAME\" TEXT," + // 5: surname
                "\"PHOTO\" TEXT," + // 6: photo
                "\"IS_ACTIVE_USER\" INTEGER NOT NULL ," + // 7: isActiveUser
                "\"IS_CHECK\" INTEGER NOT NULL ," + // 8: isCheck
                "\"AMOUNT_TO_PAY\" REAL NOT NULL );"); // 9: amountToPay
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"CONTACT\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, Contact entity) {
        stmt.clearBindings();
 
        Long dbId = entity.getDbId();
        if (dbId != null) {
            stmt.bindLong(1, dbId);
        }
        stmt.bindLong(2, entity.getUserId());
        stmt.bindLong(3, entity.getContactId());
 
        String phone = entity.getPhone();
        if (phone != null) {
            stmt.bindString(4, phone);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(5, name);
        }
 
        String surname = entity.getSurname();
        if (surname != null) {
            stmt.bindString(6, surname);
        }
 
        String photo = entity.getPhoto();
        if (photo != null) {
            stmt.bindString(7, photo);
        }
        stmt.bindLong(8, entity.getIsActiveUser() ? 1L: 0L);
        stmt.bindLong(9, entity.getIsCheck() ? 1L: 0L);
        stmt.bindDouble(10, entity.getAmountToPay());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, Contact entity) {
        stmt.clearBindings();
 
        Long dbId = entity.getDbId();
        if (dbId != null) {
            stmt.bindLong(1, dbId);
        }
        stmt.bindLong(2, entity.getUserId());
        stmt.bindLong(3, entity.getContactId());
 
        String phone = entity.getPhone();
        if (phone != null) {
            stmt.bindString(4, phone);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(5, name);
        }
 
        String surname = entity.getSurname();
        if (surname != null) {
            stmt.bindString(6, surname);
        }
 
        String photo = entity.getPhoto();
        if (photo != null) {
            stmt.bindString(7, photo);
        }
        stmt.bindLong(8, entity.getIsActiveUser() ? 1L: 0L);
        stmt.bindLong(9, entity.getIsCheck() ? 1L: 0L);
        stmt.bindDouble(10, entity.getAmountToPay());
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public Contact readEntity(Cursor cursor, int offset) {
        Contact entity = new Contact( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // dbId
            cursor.getLong(offset + 1), // userId
            cursor.getLong(offset + 2), // contactId
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // phone
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // name
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // surname
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // photo
            cursor.getShort(offset + 7) != 0, // isActiveUser
            cursor.getShort(offset + 8) != 0, // isCheck
            cursor.getFloat(offset + 9) // amountToPay
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, Contact entity, int offset) {
        entity.setDbId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setUserId(cursor.getLong(offset + 1));
        entity.setContactId(cursor.getLong(offset + 2));
        entity.setPhone(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setName(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setSurname(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setPhoto(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setIsActiveUser(cursor.getShort(offset + 7) != 0);
        entity.setIsCheck(cursor.getShort(offset + 8) != 0);
        entity.setAmountToPay(cursor.getFloat(offset + 9));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(Contact entity, long rowId) {
        entity.setDbId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(Contact entity) {
        if(entity != null) {
            return entity.getDbId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(Contact entity) {
        return entity.getDbId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
