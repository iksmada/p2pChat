package br.ufscar.p2pchat.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import br.ufscar.p2pchat.objects.Contact;
import br.ufscar.p2pchat.objects.Message;

import static android.provider.BaseColumns._ID;
import static br.ufscar.p2pchat.db.P2pChatContract.FeedEntry.CONTACT_IP_COLUMN;
import static br.ufscar.p2pchat.db.P2pChatContract.FeedEntry.CONTACT_NAME_COLUMN;
import static br.ufscar.p2pchat.db.P2pChatContract.FeedEntry.CONTACT_TABLE;
import static br.ufscar.p2pchat.db.P2pChatContract.FeedEntry.MESSAGE_CONTENT_COLUMN;
import static br.ufscar.p2pchat.db.P2pChatContract.FeedEntry.MESSAGE_IP_COLUMN;
import static br.ufscar.p2pchat.db.P2pChatContract.FeedEntry.MESSAGE_TABLE;

/**
 * Created by rapha on 18/07/2017.
 */

public class P2pChatDbHelper extends SQLiteOpenHelper {

    private static final String SQL_CREATE_CONTACTS =
            "CREATE TABLE IF NOT EXISTS " + CONTACT_TABLE + " (" +
                    _ID + " INTEGER PRIMARY KEY," +
                    CONTACT_NAME_COLUMN + " TEXT," +
                    CONTACT_IP_COLUMN + " TEXT)";

    private static final String SQL_CREATE_MESSAGES =
            "CREATE TABLE IF NOT EXISTS " + P2pChatContract.FeedEntry.MESSAGE_TABLE + " (" +
                    P2pChatContract.FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    P2pChatContract.FeedEntry.MESSAGE_CONTENT_COLUMN + " TEXT," +
                    P2pChatContract.FeedEntry.MESSAGE_IP_COLUMN + " TEXT)";

    private static final String SQL_DELETE_CONTACTS =
            "DROP TABLE IF EXISTS " + CONTACT_TABLE;

    private static final String SQL_DELETE_MESSAGES =
            "DROP TABLE IF EXISTS " + P2pChatContract.FeedEntry.MESSAGE_TABLE;

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "FeedReader.db";

    public P2pChatDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_CONTACTS);
        db.execSQL(SQL_CREATE_MESSAGES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_CONTACTS);
        db.execSQL(SQL_DELETE_MESSAGES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void addContact(Contact contact){
        //for logging
        Log.d("addContact ", contact.toString());

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(CONTACT_NAME_COLUMN, contact.getName()); // get title
        values.put(CONTACT_IP_COLUMN, contact.getIP()); // get author

        // 3. insert
        db.insert(CONTACT_TABLE, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // 4. close
        db.close();
    }

    public Contact getContact(int id) {

        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();
        final String[] COLUMNS = {_ID, CONTACT_NAME_COLUMN, CONTACT_IP_COLUMN};

        // 2. build query
        Cursor cursor =
                db.query(CONTACT_TABLE, // a. table
                        COLUMNS, // b. column names
                        " id = ?", // c. selections
                        new String[]{String.valueOf(id)}, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit
        Contact contact = null;
        // 3. if we got results get the first one
        if (cursor != null) {
            cursor.moveToFirst();

            // 4. build book object
            contact = new Contact(cursor.getString(1), cursor.getString(2));

            //log
            Log.d("getContact(" + id + ")", contact.toString());

            cursor.close();
        }
        // 5. return book
        return contact;
    }

    public ArrayList<Contact> getAllContacts() {
        ArrayList<Contact> contacts = new ArrayList<Contact>();

        // 1. build the query
        String query = "SELECT  * FROM " + CONTACT_TABLE;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        // 3. go over each row, build book and add it to list
        Contact contact = null;
        if (cursor.moveToFirst()) {
            do {
                contact = new Contact(cursor.getString(1),cursor.getString(2));

                // Add book to books
                contacts.add(contact);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        db.close();

        Log.d("getAllContacts()", contacts.toString());
        // return books
        return contacts;
    }

    public Cursor getAllContactsCursor() {
        ArrayList<Contact> contacts = new ArrayList<Contact>();

        // 1. build the query
        String query = "SELECT  * FROM " + CONTACT_TABLE;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery(query, null);
    }

    public void deleteContact(int id) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. delete
        db.delete(CONTACT_TABLE, //table name
                _ID+" = ?",  // selections
                new String[] { String.valueOf(id) }); //selections args
        // 3. close
        db.close();
        //log
        Log.d("deleteContact", String.valueOf(id) );
    }

    public void deleteContact(String name) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. delete
        db.delete(CONTACT_TABLE, //table name
                CONTACT_NAME_COLUMN+" = ?",  // selections
                new String[] { name }); //selections args
        // 3. close
        db.close();
        //log
        Log.d("deleteContact", name );
    }

    public void addMessage(Message message){
        //for logging
        Log.d("addMessage ", message.toString());

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(MESSAGE_CONTENT_COLUMN, message.getContent()); // get title
        values.put(MESSAGE_IP_COLUMN, message.getIP()); // get author

        // 3. insert
        db.insert(MESSAGE_TABLE, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // 4. close
        db.close();
    }

    public Message getMessage(int id){

        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();
        final String[] COLUMNS = {_ID,MESSAGE_CONTENT_COLUMN,MESSAGE_IP_COLUMN};
        Message message = null;

        // 2. build query
        Cursor cursor =
                db.query(MESSAGE_TABLE, // a. table
                        COLUMNS, // b. column names
                        " id = ?", // c. selections
                        new String[] { String.valueOf(id) }, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        // 3. if we got results get the first one
        if (cursor != null) {
            cursor.moveToFirst();

            // 4. build book object
            message = new Message(cursor.getString(1), cursor.getString(2));

            //log
            Log.d("getMessage(" + id + ")", message.toString());
            cursor.close();
        }
        // 5. return book
        return message;
    }

    public ArrayList<Message> getAllMessages() {
        ArrayList<Message> messages = new ArrayList<Message>();

        // 1. build the query
        String query = "SELECT  * FROM " + MESSAGE_TABLE;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        // 3. go over each row, build book and add it to list
        Message message = null;
        if (cursor.moveToFirst()) {
            do {
                message = new Message(cursor.getString(1),cursor.getString(2));

                // Add book to books
                messages.add(message);
            } while (cursor.moveToNext());
            cursor.close();
        }

        Log.d("getAllMessages()", messages.toString());

        // return books
        return messages;
    }

    public Cursor getAllMessagesCursor() {
        ArrayList<Message> messages = new ArrayList<Message>();

        // 1. build the query
        String query = "SELECT  * FROM " + MESSAGE_TABLE;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery(query, null);
    }

    public void deleteMessage(int id) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. delete
        db.delete(MESSAGE_TABLE, //table name
                _ID+" = ?",  // selections
                new String[] { String.valueOf(id) }); //selections args

        // 3. close
        db.close();

        //log
        Log.d("deleteMessage", String.valueOf(id) );

    }

}
