package jp.ousttrue.comiketroid;

import java.io.File;
import android.content.Context;
import android.util.Log;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.app.AlertDialog;
import android.app.Activity;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;

/**
 * http://www.comiket.co.jp/cd-rom/
 */
class ComiketOpenHelper extends SQLiteOpenHelper {

    private static String TAG = "ComiketOpenHelper";

    private String DB;
    private final String name;
    public static final String TABLE="rom";
    private static final String CREATE_SQL=
        "CREATE TABLE IF NOT EXISTS "+TABLE+" ("+
        "  _id integer primary key autoincrement,"+
        "  x integer,"+
        "  y integer,"+
        "  page integer,"+
        "  cut integer,"+
        "  weekday text,"+
        "  area text,"+
        "  block text,"+
        "  space integer,"+
        "  genre integer,"+
        "  name text,"+
        "  kana text,"+
        "  author text,"+
        "  publish text,"+
        "  url text,"+
        "  email text,"+
        "  comment text"+
        ")"
        ;
    private static final String DROP_SQL=
        "DROP TABLE IF EXISTS "+TABLE
        ;

    public ComiketOpenHelper(Context context, String name){
        super(context, String.format("%s.db", name), null, 1);
        this.DB=String.format("%s.db", name);
        this.name=name;
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        Log.w(TAG, "create "+DB);
        db.execSQL(CREATE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        Log.w(TAG, "drop "+DB);
        db.execSQL(DROP_SQL);
    }

    private void message(final Activity context, 
            final String title, final String message)
    {
        AlertDialog.Builder alertDialog=new 
            AlertDialog.Builder(context);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,int whichButton) {
                        context.setResult(Activity.RESULT_OK);
                    }
                });
        alertDialog.create();
        alertDialog.show();
    }

    Cursor fetchAll(){
        return getReadableDatabase().query(TABLE, 
                null, null, null, null, null, null);
    }

}

