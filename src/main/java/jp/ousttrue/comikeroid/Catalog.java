package jp.ousttrue.comikeroid;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.os.Handler;
import android.os.Message;
import android.net.Uri;
import android.content.Intent;


public class Catalog extends ListActivity {

    private static String TAG = "Catalog";

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

        Handler handler=new Handler() {
          @Override
          // should call on setup is finished
          public void handleMessage(Message msg) {
            Log.d(TAG, "setAdapterHandler: ");

            String[] selectParams;
            Intent intent = getIntent();
            selectParams=new String[]{ "" };
            /*
            if (intent.getData() == null) {
              selectParams=new String[]{ "" };
            } else {
              selectParams=new String[]{ ((Uri)intent.getData()).getHost() };
            }
            */

            SimpleCursorAdapter adapter = 
              new SimpleCursorAdapter(getApplicationContext(), R.layout.row,
                managedQuery(ComikeProvider.CONTENT_URI,
                  //null, "prefecture=?", selectParams, null),
                  null, "weekday=?", selectParams, null),
                new String[]{
                  "weekday", 
                  "area", 
                  "block", 
                  "space"},
                new int[]{
                  R.id.weekday, 
                  R.id.area,
                  R.id.block,
                  R.id.space});
            setListAdapter(adapter);
          }
        };

        ComikeOpenHelper helper=new ComikeOpenHelper(getApplicationContext());
        helper.setup(this, handler);
    }
}

