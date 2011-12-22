package jp.ousttrue.comiketroid;

import java.io.InputStreamReader;
import java.io.File;
import java.io.FileInputStream;
import au.com.bytecode.opencsv.CSVReader;
import android.util.Log;
import android.os.Bundle;
import android.os.Message;
import android.os.Handler;
import android.os.AsyncTask;
import android.os.Environment;
import android.app.ProgressDialog;
import android.app.ListActivity;
import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.content.res.AssetManager;
import android.content.ContentValues;
import android.content.Context;


class ComiketSetupTask extends AsyncTask<Object, String, Object>
{
  private static String TAG = "ComiketSetupTask";
  final ListActivity activity;
  ProgressDialog progressDialog;
  SQLiteDatabase db;
  Handler handler;
  File dir;
  final String name;

  public ComiketSetupTask(ListActivity activity, String name) {
    this.activity=activity;
    this.name=name;
    // /sdcard/Comiket/C81に決めうちにする
    File sdcard=Environment.getExternalStorageDirectory();
    this.dir=new File(sdcard, String.format("Comiket/"+name));
  }

  File getDir()
  {
      return dir;
  }

  File getCDATA()
  {
      return new File(dir, "CDATA");
  }

  @Override
  protected void onPreExecute()
  {
    ComiketOpenHelper helper=new ComiketOpenHelper(activity, name);
    this.db=helper.getWritableDatabase();

    this.progressDialog = new ProgressDialog(activity);
    progressDialog.setTitle("初期化");
    progressDialog.setMessage("データベース初期化中");
    progressDialog.setCancelable(false);
    progressDialog.show();

    this.handler = new Handler() {
      @Override
      public void handleMessage(Message msg) {
        String progressMessaage=(String)msg.getData().get(
            "progressMessaage").toString();
        progressDialog.setMessage(progressMessaage);
        Log.d(TAG, "handleMessage: "+progressMessaage);
        Thread.yield();
      }
    };
  }

  @Override
  protected void onProgressUpdate(String... values) {
    progressDialog.incrementProgressBy(1);
    progressDialog.incrementSecondaryProgressBy(1);

    Bundle bundle = new Bundle();
    bundle.putString("progressMessaage", values[0]);
    Message message = new Message();
    message.setData(bundle);
    handler.sendMessage(message);
  }

  @Override
  protected void onPostExecute(Object result) {
    db.close();
    ComiketList.setListAdapter(activity);
    progressDialog.dismiss();
  }

  @Override
  protected String doInBackground(Object... params)
  {
    db.beginTransaction();
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
        return null;
      }
      // debug
      break;
    }
    db.setTransactionSuccessful();
    db.endTransaction();
    return null;
  }

  private void readCSV(CSVReader csv)
    throws java.io.IOException
  {
    String[] line;
    ContentValues cv = new ContentValues();
    String lastBlock="";
    while ((line = csv.readNext()) != null) {
      if(!lastBlock.equals(line[6])){
        onProgressUpdate("("+line[4]+")"+line[5]+line[6]);
        if(!"".equals(lastBlock)){
          // debug
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
      cv.put("genre", line[8]);
      cv.put("name", line[9]);
      cv.put("kana", line[10]);
      cv.put("author", line[11]);
      cv.put("publish", line[12]);
      cv.put("url", line[13]);
      cv.put("email", line[14]);
      cv.put("comment", line[15]);
      db.insert(ComiketOpenHelper.TABLE, "", cv);
    }
  }
}

