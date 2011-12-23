package jp.ousttrue.comiketroid;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
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
import android.net.Uri;


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
    "  _id integer primary key autoincrement,"+ // 0
    "  x integer,"+
    "  y integer,"+
    "  page integer,"+
    "  cut integer,"+
    "  weekday text,"+ // 5
    "  area text,"+
    "  block text,"+
    "  space integer,"+
    "  genre integer,"+
    "  name text,"+ // 10
    "  kana text,"+
    "  author text,"+
    "  publish text,"+
    "  url text,"+
    "  email text,"+ // 15
    "  comment text"+
    ")"
    ;
  private static final String DROP_SQL=
    "DROP TABLE IF EXISTS "+TABLE
    ;
  private static final String[] projection={
    "_id", // 0
    "x",
    "y",
    "page",
    "cut",
    "weekday", // 5
    "area",
    "block",
    "space",
    "genre",
    "name", // 10
    "kana",
    "author",
    "publish",
    "url",
    "email", // 15
    "comment",
  };
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

  static Cursor query(Activity activity){
    return uriQuery(activity, activity.getIntent().getData());
  }

  /**
   * Uri
   * comiket://${Cxx}/${date}/${block}/${genre}/id
   */
  static Cursor uriQuery(Activity activity, Uri uri){
    Log.i(TAG, "uriQuery: "+uri.toString());
    String selectParams=null;
    String[] selectArgs=null;
    if (uri != null) {
      // build request 
      String[] splited=uri.getPath().substring(1).split("/");
      StringBuffer params=new StringBuffer();
      ArrayList<String> args=new ArrayList<String>();
      if(!"*".equals(splited[0])){
        // weekday
        if(splited[0].indexOf(",")==-1){
          params.append(" weekday=?");
          args.add(splited[0]);
        }
        else{
          String[] weekdays=splited[0].split(",");
          StringBuffer weekdaysParams=new StringBuffer();
          for(int i=0; i<weekdays.length; ++i){
            if(i>0){
              weekdaysParams.append(",");
            }
            weekdaysParams.append("?");
            args.add(weekdays[i]);
          }
          params.append(" weekdays IN ("+weekdaysParams.toString()+")");
        }
      }
      parseParam(splited[0], "weekday", params, args);
      parseParam(splited[1], "block", params, args);
      parseParam(splited[2], "genre", params, args);
      if(!args.isEmpty()){
        Log.d(TAG, "args: "+args.size());
        for(int i=0; i<args.size(); ++i){
          Log.d(TAG, args.get(i));
        }
        Log.d(TAG, params.toString());

        selectParams=params.toString();
        selectArgs=new String[args.size()];
        selectArgs=args.toArray(selectArgs);
      }
    }

    return activity.managedQuery(
        ComiketProvider.CONTENT_URI,
        projection, 
        selectParams, 
        selectArgs, null);
  }

  static private void parseParam(String src, String field, 
      StringBuffer params, List<String> args)
  {
    if("*".equals(src)){
      return;
    }
    if(params.length()>0){
      params.append(" AND");
    }
    if(src.indexOf(",")==-1){
      params.append(" "+field+"=?");
      args.add(src);
    }
    else{
      String[] weekdays=src.split(",");
      StringBuffer weekdaysParams=new StringBuffer();
      for(int i=0; i<weekdays.length; ++i){
        if(i>0){
          weekdaysParams.append(",");
        }
        weekdaysParams.append("?");
        args.add(weekdays[i]);
      }
      params.append(" "+field+" IN ("+weekdaysParams.toString()+")");
    }
  }
}

