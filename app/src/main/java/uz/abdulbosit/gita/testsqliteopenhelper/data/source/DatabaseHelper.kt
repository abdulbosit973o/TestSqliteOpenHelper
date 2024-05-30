package uz.abdulbosit.gita.testsqliteopenhelper.data.source

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import uz.abdulbosit.gita.testsqliteopenhelper.data.model.Contact

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "contactsManager"
        private const val TABLE_CONTACTS = "contacts"
        private const val KEY_ID = "id"
        private const val KEY_NAME = "name"
        private const val KEY_PHONE = "phone_number"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createContactsTable = ("CREATE TABLE $TABLE_CONTACTS ("
                + "$KEY_ID INTEGER PRIMARY KEY,"
                + "$KEY_NAME TEXT,"
                + "$KEY_PHONE TEXT)")
        db.execSQL(createContactsTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CONTACTS")
        onCreate(db)
    }

    fun addContact(contact: Contact) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(KEY_NAME, contact.name)
            put(KEY_PHONE, contact.phoneNumber)
        }
        db.insert(TABLE_CONTACTS, null, values)
        db.close()
    }

    fun getContact(id: Int): Contact {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_CONTACTS, arrayOf(KEY_ID, KEY_NAME, KEY_PHONE),
            "$KEY_ID=?", arrayOf(id.toString()), null, null, null, null
        )
        cursor?.moveToFirst()
        val contact = Contact(
            cursor.getInt(0),
            cursor.getString(1),
            cursor.getString(2)
        )
        cursor.close()
        return contact
    }

    fun getAllContacts(): List<Contact> {
        val contactList = ArrayList<Contact>()
        val selectQuery = "SELECT * FROM $TABLE_CONTACTS"
        val db = this.writableDatabase
        val cursor = db.rawQuery(selectQuery, null)
        if (cursor.moveToFirst()) {
            do {
                val contact = Contact(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2)
                )
                contactList.add(contact)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return contactList
    }

    fun updateContact(contact: Contact): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(KEY_NAME, contact.name)
            put(KEY_PHONE, contact.phoneNumber)
        }
        return db.update(TABLE_CONTACTS, values, "$KEY_ID = ?", arrayOf(contact.id.toString()))
    }

    fun deleteContact(contact: Contact) {
        val db = this.writableDatabase
        db.delete(TABLE_CONTACTS, "$KEY_ID = ?", arrayOf(contact.id.toString()))
        db.close()
    }

    fun getContactsCount(): Int {
        val countQuery = "SELECT * FROM $TABLE_CONTACTS"
        val db = this.readableDatabase
        val cursor = db.rawQuery(countQuery, null)
        val count = cursor.count
        cursor.close()
        return count
    }
}
