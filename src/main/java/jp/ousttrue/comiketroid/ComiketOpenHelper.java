package jp.ousttrue.comiketroid;

import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.File;
import android.content.Context;
import au.com.bytecode.opencsv.CSVReader;
import android.util.Log;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.app.AlertDialog;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.ContentValues;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;


/**
 * http://www.comiket.co.jp/cd-rom/
 */
class ComiketOpenHelper extends SQLiteOpenHelper {

    private static String TAG = "ComiketOpenHelper";

    private static final String DB="comiket.db";
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

    File dir;

    void setDir(File dir)
    {
        this.dir=dir;
    }

    File getDir()
    {
        return dir;
    }

    File getCDATA()
    {
        return new File(dir, "CDATA");
    }

    public ComiketOpenHelper(Context context){
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

    Cursor fetchAll(){
        return getReadableDatabase().query(TABLE, 
                null, null, null, null, null, null);
    }

    public void setup(final Activity context, final Handler onFinish)
    {
        final ComiketOpenHelper helper=this;

        Cursor c=fetchAll();
        try{
            if(c.moveToFirst()){
                onFinish.sendEmptyMessage(0);
                return;
            }
        }
        finally{
            c.close();
            close();
            Log.i(TAG, "closed");
        }

        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("初期化");
        progressDialog.setMessage("データベース初期化中");
        //progressDialog.setIndeterminate(true);
        //progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);
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

        File sdcard=Environment.getExternalStorageDirectory();
        // show selector
        // /sdcard/Comiket決めうちにする
        final File[] files=(new File(sdcard, "Comiket")).listFiles();
        if(files.length==0){
            message(context,
                    "データがありません",
                    "カタログデータをsdcardの/Comiket/C81に配置してください");
            return;
        }

        // 読み込み処理
        final Thread t=new Thread(new Runnable(){

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
                db.beginTransaction();
                try{
                    for(int i=1; i<=3; ++i){
                        try{
                            File f=new File(
                                    getCDATA(), getDir().getName()+"ROM"+i+".TXT");
                            Log.d(TAG, "open :"+f);
                            CSVReader csv=new CSVReader(
                                    new InputStreamReader(
                                        new FileInputStream(f), "SJIS"), '\t');
                            readCSV(csv);
                        }
                        catch(java.io.FileNotFoundException e){
                            // skip directory
                            e.printStackTrace();
                        }
                        catch(java.io.IOException e){
                            e.printStackTrace();
                            return;
                        }
                        // debug...
                        break;
                    }
                    db.setTransactionSuccessful();
                    db.endTransaction();
                }
                finally{
                    helper.close();
                    Log.i(TAG, "closed");
                }
            }

            private void readCSV(CSVReader csv)
                throws java.io.IOException
            {
                String[] line;
                ContentValues cv = new ContentValues();
                String lastBlock="";
                while ((line = csv.readNext()) != null) {
                    if(!lastBlock.equals(line[6])){
                      sendMessage("("+line[4]+")"+line[5]+line[6]);
                      if(!"".equals(lastBlock)){
                        // debug...
                        break;
                      }
                      lastBlock=line[6];
                    }
                    cv.clear();
                    cv.put("x", line[0]);
                    cv.put("y", line[1]);
                    cv.put("page", line[2]);
                    cv.put("cut", line[3]);
                    cv.put("weekday", line[4]);
                    cv.put("area", line[5]);
                    cv.put("block", line[6]);
                    cv.put("space", line[7]);
                    cv.put("jenre", line[8]);
                    cv.put("name", line[9]);
                    cv.put("kana", line[10]);
                    cv.put("author", line[11]);
                    cv.put("publish", line[12]);
                    cv.put("url", line[13]);
                    cv.put("email", line[14]);
                    cv.put("comment", line[15]);
                    db.insert(TABLE, "", cv);
                }
            }
        });

        // build menu...
        AlertDialog.Builder builder=new 
            AlertDialog.Builder(context);
        builder.setTitle("データを読み込むディレクトリを選択してください");
        String[] dirs=new String[files.length];
        for(int i=0; i<files.length; ++i){
            dirs[i]=files[i].getName();
        }
        builder.setItems(dirs, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog,int whichButton) {
                Log.i(TAG, "selected: "+files[whichButton]);
                helper.setDir(files[whichButton]);
                t.start();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.create();
        builder.show();
    }

    /*
    public void setup(final Context context, final Handler onFinish)
    {
    */

}

