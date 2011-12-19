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

            Intent intent = getIntent();
            String selectParams;
            String[] selectArgs;
            if (intent.getData() == null) {
              selectParams=null;
              selectArgs=null;
            } else {
              selectParams="_id=?";
              selectArgs=new String[]{ 
                ((Uri)intent.getData()).getPath().substring(1) };
            }

            final SimpleCursorAdapter adapter = 
              new SimpleCursorAdapter(getApplicationContext(), R.layout.row,
                managedQuery(ComiketProvider.CONTENT_URI,
                  null, selectParams, selectArgs, null),
                new String[]{
                  "weekday", 
                  "area", 
                  "block", 
                  "space",
                  "name"
                },
                new int[]{
                  R.id.weekday, 
                  R.id.area,
                  R.id.block,
                  R.id.space,
                  R.id.name,
                });
            setListAdapter(adapter);

            getListView().setOnItemClickListener(
                new AdapterView.OnItemClickListener(){
                  @Override
                  public void onItemClick(
                    AdapterView<?> _, View view, int position, long id)
                  {
                    Log.i(TAG, "onItemClick: "+id);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("comiket://81/" + id));
                    startActivity(intent);
                  }
            });
          }
        };

        ComiketOpenHelper helper=new ComiketOpenHelper(getApplicationContext());
        helper.setup(this, handler);
    }
}

