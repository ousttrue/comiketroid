package jp.ousttrue.comiketroid;

import java.util.Map;
import java.util.ArrayList;
import android.os.Bundle;
import android.content.Context;
import android.app.Activity;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;
import android.widget.TextView;
import android.widget.Button;
import android.content.Intent;


public class ComiketFilter extends Activity {

  private static final String TAG = "ComiketItem";
  private ComiketDef def=new ComiketDef("C81");
  private ArrayList<View> areas;

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

    // 開催日
    ViewGroup days=(ViewGroup)ViewGroup.inflate(this, R.layout.filtersub, null);
    ((TextView)days.findViewById(R.id.label)).setText("開催日");
    filter.addView(days);
    boolean isFirst=true;
    for(Map.Entry<String, String> e: def.dateMap.entrySet()) {
      addButton((ViewGroup)days.findViewById(R.id.buttons), 
          e.getKey(),
          isFirst);
      isFirst=false;
    }
    // 場所
    areas=new ArrayList<View>();
    isFirst=true;
    for(Map.Entry<String, String> e: def.areaMap.entrySet()) {
      ViewGroup area=(ViewGroup)ViewGroup.inflate(this, R.layout.filtersub, null);
      ((TextView)area.findViewById(R.id.label)).setText(e.getKey());
      filter.addView(area);
      areas.add(area.findViewById(R.id.buttons));
      String value=e.getValue();
      for(int i=0; i<value.length(); ++i){
        addButton((ViewGroup)area.findViewById(R.id.buttons), 
            value.substring(i, i+1),
            isFirst && i==0);
        isFirst=false;
      }
    }
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
              getDate(),
              getBlock(),
              getGenre()
              )),
          "text/directory");
        startActivity(intent);
      }
    });
  }

  private String getDate(){
    return "*";
  }

  private String getBlock(){
    StringBuffer buf=new StringBuffer();
    boolean isAll=true;
    for(View v: areas){
      ViewGroup g=(ViewGroup)v;
      for(int i=0; i<g.getChildCount(); ++i){
        ToggleButton b=(ToggleButton)g.getChildAt(i);
        if(b.isChecked()){
          if(buf.length()>0){
            buf.append(",");
          }
          buf.append(b.getText());
        }
        else{
          isAll=false;
        }
      }
    }
    if(isAll){
      return "*";
    }
    else{
      return buf.toString();
    }
  }

  private String getGenre(){
    return "*";
  }

  private void addButton(ViewGroup g, String text, boolean checked){
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
  }
}

