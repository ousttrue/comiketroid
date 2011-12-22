package jp.ousttrue.comiketroid;

import java.io.InputStreamReader;
import java.io.File;
import java.io.FileInputStream;
import au.com.bytecode.opencsv.CSVReader;
import android.util.Log;
import android.database.sqlite.SQLiteDatabase;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.os.Bundle;
import android.os.Message;
import android.os.Handler;
import android.app.Activity;


class Logic implements Runnable {

  private static String TAG = "Logic";

  final SQLiteDatabase db;
  final Handler onFinish;
  final ProgressDialog progressDialog;
  final Handler handler;
  final ComiketOpenHelper helper;
  File dir;

  Logic(Activity context, ComiketOpenHelper helper, Handler onFinish, File dir){
    this.helper=helper;
    this.db=helper.getWritableDatabase();
    this.onFinish=onFinish;
    this.progressDialog=createProgressDialog(context);
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

  private ProgressDialog createProgressDialog(Activity context)
  {
    final ProgressDialog progressDialog = new ProgressDialog(context);
    progressDialog.setTitle("初期化");
    progressDialog.setMessage("データベース初期化中");
    //progressDialog.setIndeterminate(true);
    //progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
    progressDialog.setCancelable(false);
    progressDialog.show();
    return progressDialog;
  }

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
        //break;
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
          //break;
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



