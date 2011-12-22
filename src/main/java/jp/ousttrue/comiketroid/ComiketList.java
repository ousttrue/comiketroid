package jp.ousttrue.comiketroid;

import java.util.ArrayList;
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

    Intent intent = getIntent();
    final Uri uri=intent.getData();
    Log.i(TAG, uri.toString());

    final ListActivity listActivity=this;
    getListView().setOnItemClickListener(new AdapterView.OnItemClickListener(){
      @Override
      public void onItemClick(
        AdapterView<?> _, View view, int position, long id)
      {
        Log.i(TAG, "onItemClick: "+id);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(
          Uri.parse(uri.toString()+"/"+id), 
          "text/item");
        listActivity.startActivity(intent);
      }
    });

    if(managedQuery(ComiketProvider.CONTENT_URI,
          null, null, null, null).getCount()>0)
    {
      Log.i(TAG, "already exists....");
      setListAdapter(this);
    }
    else{
      Log.i(TAG, "initialize...");

      ComiketSetupTask task = new ComiketSetupTask(this, uri.getHost());
      task.execute();
    }
  }

  static void setListAdapter(ListActivity listActivity){
    Log.d(TAG, "setAdapterHandler: ");
    Intent intent = listActivity.getIntent();
    Uri uri=intent.getData();
    Log.i(TAG, uri.toString());

    String selectParams=null;
    String[] selectArgs=null;
    if (intent.getData() != null) {
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
      if(!"*".equals(splited[1])){
        // block
        if(splited[1].indexOf(",")==-1){
          params.append(" block=?");
          args.add(splited[1]);
        }
        else{
          String[] weekdays=splited[1].split(",");
          StringBuffer weekdaysParams=new StringBuffer();
          for(int i=0; i<weekdays.length; ++i){
            if(i>0){
              weekdaysParams.append(",");
            }
            weekdaysParams.append("?");
            args.add(weekdays[i]);
          }
          params.append(" block IN ("+weekdaysParams.toString()+")");
        }
      }
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

    listActivity.setTitle(String.format("%d circles", cursor.getCount()));
  }
}

