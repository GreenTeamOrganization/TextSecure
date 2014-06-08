/**
 * Copyright (C) 2011 Whisper Systems
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.thoughtcrime.securesms.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

import org.thoughtcrime.securesms.recipients.RecipientFactory;
import org.thoughtcrime.securesms.recipients.Recipients;
import org.whispersystems.textsecure.crypto.IdentityKey;
import org.whispersystems.textsecure.crypto.InvalidKeyException;
import org.whispersystems.textsecure.crypto.MasterCipher;
import org.whispersystems.textsecure.crypto.MasterSecret;
import org.whispersystems.textsecure.util.Base64;

import java.io.IOException;

/**
 * Handles the Database for the BlackList.
 *
 * @author GreenTeam
 *
 */

public class BlackListDatabase extends Database {

    private static final Uri CHANGE_URI = Uri.parse("content://textsecure/blockednumbers");

    private static final String TABLE_NAME     = "blacklist";
    public  static final String ID             = "_id";
    public  static final String BLOCK_NUMBER   = "block_number";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" + ID + " INTEGER PRIMARY KEY, " +
            BLOCK_NUMBER + " INTEGER);";

    public BlackListDatabase(Context context, SQLiteOpenHelper databaseHelper) {
        super(context, databaseHelper);
    }

    public Cursor getBlockNumbers() {
        SQLiteDatabase database = databaseHelper.getReadableDatabase();
        Cursor cursor           = database.query(TABLE_NAME, null, null, null, null, null, null);

        if (cursor != null)
            cursor.setNotificationUri(context.getContentResolver(), CHANGE_URI);

        return cursor;
    }

    public Reader readerFor(MasterSecret masterSecret, Cursor cursor) {
        return new Reader(masterSecret, cursor);
    }

    public void saveBlockNumber(int recipientId){
        SQLiteDatabase database   = databaseHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(BLOCK_NUMBER, recipientId);

        database.replace(TABLE_NAME, null, contentValues);

        context.getContentResolver().notifyChange(CHANGE_URI, null);
    }

    public void deleteBlockNumber(long id) {
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        database.delete(TABLE_NAME, ID_WHERE, new String[] {id+""});

        context.getContentResolver().notifyChange(CHANGE_URI, null);
    }

    public class Reader {
        private final Cursor cursor;
        private final MasterCipher cipher;

        public Reader(MasterSecret masterSecret, Cursor cursor) {
            this.cursor = cursor;
            this.cipher = new MasterCipher(masterSecret);
        }

        public BlackList getCurrent() {
            long recipientId      = cursor.getLong(cursor.getColumnIndexOrThrow(BLOCK_NUMBER));
            Recipients recipients = RecipientFactory.getRecipientsForIds(context, recipientId + "", true);

            try {
                String blockNumberString = cursor.getString(cursor.getColumnIndexOrThrow(BLOCK_NUMBER));
                IdentityKey identityKey = new IdentityKey(Base64.decode(blockNumberString), 0);
                return new BlackList(recipients);
            } catch (IOException e) {
                Log.w("BlackListDatabase", e);
                return new BlackList(recipients);
            } catch (InvalidKeyException e) {
                Log.w("BlackListDatabase", e);
                return new BlackList(recipients);
            }
        }
    }

    public static class BlackList {
        private final Recipients  recipients;

        public BlackList(Recipients recipients) {
            this.recipients  = recipients;
        }

        public Recipients getRecipients() {
            return recipients;
        }

    }
}
