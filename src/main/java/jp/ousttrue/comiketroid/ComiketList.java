package jp.ousttrue.comiketroid;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.ListActivity;
import android.net.Uri;
import android.content.Intent;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.AdapterView;
import android.util.Log;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;


public class ComiketList extends ListActivity {

  private static String TAG = "ComiketList";

  static final String[] fields={
    "weekday", 
    "area", 
    "block", 
    "space",
    "name"
  };
  static final int[] views={
    R.id.lweekday, 
    R.id.larea,
    R.id.lblock,
    R.id.lspace,
    R.id.lname,
  };

  /**
   * Called when the activity is first created.
   * @param savedInstanceState If the activity is being re-initialized after
   * previously being shut down then this Bundle contains the data it most
   * recently supplied in onSaveInstanceState(Bundle). 
   * <b>Note: Otherwise it is null.</b>
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.i(TAG, "onCreate");

    final ListActivity listActivity=this;
    getListView().setOnItemClickListener(new AdapterView.OnItemClickListener(){
      @Override
      public void onItemClick(
        AdapterView<?> _, View view, int position, long id)
      {
        Log.i(TAG, "onItemClick: "+id);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(
          Uri.parse("comiket://81/" + id),
          "text/item");
        listActivity.startActivity(intent);
      }
    });

    ComiketOpenHelper helper=new ComiketOpenHelper(
        getApplicationContext(), "C81");
    if(helper.fetchAll().getCount()==0){
      Log.i(TAG, "closed");
      helper.close();
      ComiketList.setListAdapter(this);
      return;
    }

    helper.setup(this, new Handler(){
      @Override
      public void handleMessage(Message msg) {
        ComiketList.setListAdapter(listActivity);
      }
    });
  }

  static void setListAdapter(ListActivity listActivity){
    Log.d(TAG, "setAdapterHandler: ");
    Intent intent = listActivity.getIntent();
    Uri uri=intent.getData();
    Log.i(TAG, uri.toString());

    String selectParams;
    String[] selectArgs;
    //if (intent.getData() == null) {
      selectParams=null;
      selectArgs=null;
    /*
    } else {
      selectParams="_id=?";
      selectArgs=new String[]{ 
        ((Uri)intent.getData()).getPath().substring(1) };
    }
    */

    Cursor cursor=listActivity.managedQuery(
            ComiketProvider.CONTENT_URI,
            null, 
            selectParams, 
            selectArgs, null);
    SimpleCursorAdapter adapter=new SimpleCursorAdapter(listActivity, 
        R.layout.row,
        cursor,
        fields,
        views
        );
    listActivity.setListAdapter(adapter);
  }
}

