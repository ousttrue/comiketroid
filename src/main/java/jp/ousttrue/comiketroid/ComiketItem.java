package jp.ousttrue.comiketroid;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.net.Uri;
import android.content.Intent;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.AdapterView;
import android.widget.TextView;
import android.util.Log;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;


public class ComiketItem extends Activity {

  private static String TAG = "ComiketItem";

  private static String[] projection={
    "x", // 0
    "y",
    "page",
    "cut",
    "weekday",
    "area", // 5
    "block",
    "space",
    "jenre",
    "name",
    "kana", // 10
    "author",
    "publish",
    "url",
    "email",
    "comment", // 15
  };

  /**
   * Called when the activity is first created.
   * @param savedInstanceState If the activity is being re-initialized after
   * previously being shut down then this Bundle contains the data it most
   * recently supplied in onSaveInstanceState(Bundle). <b>Note: Otherwise it is null.</b>
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.i(TAG, "onCreate");

    setContentView(R.layout.item);

    Intent intent = getIntent();
    String selectParams="_id=?";
    String[] selectArgs=new String[]{ 
      ((Uri)intent.getData()).getPath().substring(1) };
    Cursor cursor=managedQuery(ComiketProvider.CONTENT_URI,
        projection, selectParams, selectArgs, null);
    if(!cursor.moveToNext()){
      return;
    }

    ((TextView)findViewById(R.id.weekday)).setText(cursor.getString(4));
    ((TextView)findViewById(R.id.area)).setText(cursor.getString(5));
    ((TextView)findViewById(R.id.block)).setText(cursor.getString(6));
    ((TextView)findViewById(R.id.space)).setText(cursor.getString(7));
    ((TextView)findViewById(R.id.name)).setText(cursor.getString(9));
    ((TextView)findViewById(R.id.kana)).setText(cursor.getString(10));
    ((TextView)findViewById(R.id.author)).setText(cursor.getString(11));
    ((TextView)findViewById(R.id.publish)).setText(cursor.getString(12));
    ((TextView)findViewById(R.id.url)).setText(cursor.getString(13));
    ((TextView)findViewById(R.id.email)).setText(cursor.getString(14));
    ((TextView)findViewById(R.id.comment)).setText(cursor.getString(15));
  }
}

