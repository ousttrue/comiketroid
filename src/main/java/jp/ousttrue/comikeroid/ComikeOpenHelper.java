package jp.ousttrue.comikeroid;

import java.io.InputStreamReader;
import android.content.Context;
//import au.com.bytecode.opencsv.CSVReader;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.content.res.AssetManager;
import android.os.Environment;
import android.os.Handler;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.app.Activity;

/*
import android.content.ContentValues;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.Message;
import android.os.Bundle;
*/


/**
 * http://www.comiket.co.jp/cd-rom/
 */
class ComikeOpenHelper extends SQLiteOpenHelper {

    private static String TAG = "ComikeOpenHelper";

    private static final String DB="comike.db";
    public static final String TABLE="rom";
    private static final String CREATE_SQL=
        "CREATE TABLE IF NOT EXISTS "+TABLE+" ("+
        "  _id integer primary key autoincrement,"+
        "  x integer,"+
        "  y integer,"+
        "  page integer,"+
        "  index integer,"+
        "  weekday text,"+
        "  area text,"+
        "  block text,"+
        "  space integer,"+
        "  jenre integer,"+
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

    public ComikeOpenHelper(Context context){
        super(context, DB, null, 1);
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

    /**
     * /sdcard/Comiketディレクトを見る
     */
    public void setup(final Activity context, final Handler onFinish)
    {
        java.io.File sdcard=Environment.getExternalStorageDirectory();
        // show selector
        java.io.File[] files=sdcard.listFiles();
        if(files.length==0){
            message(context,
                    "データがありません",
                    "カタログデータをsdcardの/Comiket/C81に配置してください");
            return;
        }
        message(context, "creat data", "create...");
    }

    /*
    Cursor fetchAll(){
        return getReadableDatabase().query(TABLE, 
                null, null, null, null, null, null);
    }

    public void setup(final Context context, final Handler onFinish)
    {
        Cursor c=fetchAll();
        try{
            if(c.moveToFirst()){
                onFinish.sendEmptyMessage(0);
                return;
            }
        }
        finally{
            c.close();
        }

        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("初期化");
        progressDialog.setMessage("データベース初期化中");
        progressDialog.setIndeterminate(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        //progressDialog.setMax(47);
        progressDialog.setCancelable(false);
        progressDialog.incrementSecondaryProgressBy(1);
        progressDialog.show();

        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String progressMessaage=(String)msg.getData().get(
                        "progressMessaage").toString();
                progressDialog.setMessage(progressMessaage);
                Log.d(TAG, "handleMessage: "+progressMessaage);
                Thread.yield();
            }
        };

        final SQLiteDatabase db=getWritableDatabase();
        final AssetManager as=context.getResources().getAssets();

        Thread t=new Thread(new Runnable(){

            @Override
            public void run(){
                try{
                    read();
                }
                finally{
                    onFinish.sendEmptyMessage(0);
                    progressDialog.dismiss();
                }
            }

            // update dialog
            private void sendMessage(String progressMessaage)
            {
                Bundle bundle = new Bundle();
                bundle.putString("progressMessaage", progressMessaage);

                Message message = new Message();
                message.setData(bundle);

                handler.sendMessage(message);
                Thread.yield();
            }

            private void read(){
                String[] list;
                try{
                    list=as.list("");
                }
                catch(java.io.IOException e){
                    e.printStackTrace();
                    return;
                }

                java.util.Arrays.sort(list);

                db.beginTransaction();
                try{
                    for(int i=0; i<list.length; ++i){
                        try{
                            String f=list[i];
                            Log.d(TAG, "open :"+f);
                            sendMessage(f);
                            readZipStream(as.open(f));
                        }
                        catch(java.io.FileNotFoundException e){
                            // skip directory
                            e.printStackTrace();
                        }
                        catch(java.io.IOException e){
                            e.printStackTrace();
                            return;
                        }
                    }
                    db.setTransactionSuccessful();
                }
                finally{
                    db.endTransaction();
                }
            }

            private void readZipEntry(CSVReader csv)
                throws java.io.IOException
            {
                String[] line;
                while ((line = csv.readNext()) != null) {
                    ContentValues cv = new ContentValues();
                    cv.put("jis", line[0]);
                    cv.put("old_zipcode", line[1]);
                    cv.put("zipcode", line[2]);
                    cv.put("prefectureKana", line[3]);
                    cv.put("cityKana", line[4]);
                    cv.put("townKana", line[5]);
                    cv.put("prefecture", line[6]);
                    cv.put("city", line[7]);
                    cv.put("town", line[8]);
                    db.insert("zipcode", "", cv);
                }
            }
        });

        t.start();
    }
    */

}

