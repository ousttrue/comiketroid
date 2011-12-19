package jp.ousttrue.comikeroid;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;


public class ComikeProvider extends ContentProvider {

    public static final Uri CONTENT_URI=Uri.parse(
        "content://jp.ousttrue.comikeroid.comikeprovider");

    ComikeOpenHelper databaseHelper;

    @Override
    public boolean onCreate() {
        databaseHelper = new ComikeOpenHelper(getContext());
        return true;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(ComikeOpenHelper.TABLE);
        Cursor c = qb.query(
            db, projection, selection, selectionArgs, null, null, null);
        return c;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
            String[] selectionArgs) {
        return 0;
    }
}


