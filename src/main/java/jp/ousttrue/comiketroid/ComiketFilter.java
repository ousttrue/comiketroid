package jp.ousttrue.comiketroid;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import android.os.Bundle;
import android.content.Context;
import android.app.Activity;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;
import android.widget.Checkable;
import android.widget.TextView;
import android.widget.ListView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SimpleCursorAdapter;
import android.widget.AdapterView;
import android.content.Intent;
import android.database.MatrixCursor;


public class ComiketFilter extends Activity {

  private static final String TAG = "ComiketItem";
  private ComiketDef def=new ComiketDef("C81");
  private final ArrayList<String> dates=new ArrayList<String>();
  private ArrayList<String> blocks=new ArrayList<String>();
  private ArrayList<String> genres=new ArrayList<String>();

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

    setContentView(R.layout.filter);

    ViewGroup filter = (ViewGroup) findViewById(R.id.filter);

    filter.addView(createDates());
    filter.addView(createGenre());
    addBlocks(filter);
    // OK
    Button button = (Button) findViewById(R.id.go);
    button.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Log.i(TAG, "onClick");
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(
          Uri.parse(String.format("comiket://%s/%s/%s/%s",
              def.getName(),
              listToParam(dates),
              listToParam(blocks),
              listToParam(genres)
              )),
          "text/directory");
        startActivity(intent);
      }
    });
  }

  private View createDates(){
    ViewGroup days=(ViewGroup)ViewGroup.inflate(this, R.layout.filtersub, null);
    ((TextView)days.findViewById(R.id.label)).setText("開催日");
    boolean isFirst=false;
    for(final Map.Entry<String, String> e: def.dateMap.entrySet()) {
      ToggleButton button=addButton((ViewGroup)days.findViewById(R.id.buttons), 
          e.getKey(),
          isFirst);
      if(isFirst){
        dates.add(e.getKey());
      }
      button.setOnClickListener(new OnClickListener() {
        public void onClick(View v) {
          if (((Checkable) v).isChecked()) {
            dates.add(e.getKey());
          } 
          else {
            dates.remove(dates.indexOf(e.getKey()));
          }
        }
      });
      isFirst=false;
    }
    return days;
  }

  private View createGenre(){
    ViewGroup genre=(ViewGroup)ViewGroup.inflate(this, R.layout.filtergenre, null);
    ViewGroup list=(ViewGroup)genre.findViewById(R.id.genrelist);
    for(final Map.Entry<String, String> e: def.genreMap.entrySet()) {
      CheckBox row=(CheckBox)ViewGroup.inflate(
          this, R.layout.filtergenrerow, null);
      row.setText(e.getKey()+" "+e.getValue());
      row.setOnClickListener(new OnClickListener() {
        public void onClick(View v) {
          if (((Checkable) v).isChecked()) {
            genres.add(e.getKey());
          } 
          else {
            genres.remove(genres.indexOf(e.getKey()));
          }
        }
      });
      list.addView(row);
    }
    return genre;
  }

  private void addBlocks(ViewGroup filter){
    boolean isFirst=false;
    for(Map.Entry<String, String> e: def.areaMap.entrySet()) {
      ViewGroup area=(ViewGroup)ViewGroup.inflate(this, R.layout.filtersub, null);
      ((TextView)area.findViewById(R.id.label)).setText(e.getKey());
      filter.addView(area);
      String value=e.getValue();
      for(int i=0; i<value.length(); ++i){
        final String block=value.substring(i, i+1);
        ToggleButton button=addButton((ViewGroup)area.findViewById(R.id.buttons), 
            block,
            isFirst);
        if(isFirst){
          blocks.add(block);
        }
        button.setOnClickListener(new OnClickListener() {
          public void onClick(View v) {
            if (((Checkable) v).isChecked()) {
              blocks.add(block);
            } 
            else {
              blocks.remove(blocks.indexOf(block));
            }
          }
        });
        isFirst=false;
      }
    }
  }

  private String listToParam(List<String> list){
    if(list.isEmpty()){
      return "*";
    }
    StringBuffer buf=new StringBuffer();
    for(String e: list){
      if(buf.length()>0){
        buf.append(",");
      }
      buf.append(e);
    }
    return buf.toString();
  }

  private ToggleButton addButton(ViewGroup g, String text, boolean checked){
    ToggleButton button=new ToggleButton(this);
    button.setText(text);
    button.setTextOn(text);
    button.setTextOff(text);
    button.setChecked(checked);
    RelativeLayout.LayoutParams prm = new RelativeLayout.LayoutParams(
        LinearLayout.LayoutParams.WRAP_CONTENT,
        LinearLayout.LayoutParams.WRAP_CONTENT);
    prm.setMargins(0, 0, 0, 0);
    g.addView(button);
    return button;
  }
}

