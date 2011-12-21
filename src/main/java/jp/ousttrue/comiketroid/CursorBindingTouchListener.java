package jp.ousttrue.comiketroid;

import java.io.File;
import android.util.Log;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.MotionEvent;
import android.view.animation.AnimationUtils;
import android.view.View;
import android.widget.ViewFlipper;
import android.widget.TextView;
import android.widget.ImageView;
import android.os.Environment;
import android.app.Activity;


/**
 * DBのカーソルと現在表示中のものをマッピングする
 */
class CursorBindingTouchListener implements View.OnTouchListener {
  private static final String TAG = "CursorBindingTouchListener";
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
  String getWeekday(){ return cursor.getString(5); }
  String getArea(){ return cursor.getString(6); }
  String getBlock(){ return cursor.getString(7); }
  int getSpace(){ return cursor.getInt(8); }

  Activity activity;
  ViewFlipper viewFlipper;
  float firstTouch;
  boolean isFlipping;
  int id;
  Cursor cursor;
  View[] views={null, null, null};
  ComiketDef def;

  CursorBindingTouchListener(Activity activity, ViewFlipper viewFlipper, int id){
    this.activity=activity;
    this.viewFlipper=viewFlipper;
    this.id=id;
    this.firstTouch=0.0f;
    this.isFlipping=false;
    this.def=new ComiketDef();

    // create views
    for(int i=0; i<this.views.length; ++i){
      View view=ViewFlipper.inflate(activity, R.layout.item, null);
      view.setOnTouchListener(this);
      viewFlipper.addView(view);
      views[i]=view;
    }

    // find cursor position
    this.cursor=activity.managedQuery(ComiketProvider.CONTENT_URI,
        projection, null, null, null);
    if(this.cursor.getCount()==0){
      return;
    }
    boolean found=false;
    for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
      if(cursor.getInt(0)==id){
        found=true;
        break;
      }
    }
    if(!found){
      Log.w(TAG, "not found for id: "+id);
      return;
    }

    // binding
    if(!cursor.isFirst()){
      cursor.moveToPrevious();
      bindView(views[2]);
      cursor.moveToNext();
    }
    updateTitle();
    bindView(views[0]);
    if(!cursor.isLast()){
      cursor.moveToNext();
      bindView(views[1]);
      cursor.moveToPrevious();
    }
  }

  private void bindView(View view) {
    ((TextView)view.findViewById(R.id.genre)).setText(
    cursor.getString(9)+":"+def.getGenre(cursor.getInt(9)));
    ((TextView)view.findViewById(R.id.name)).setText(cursor.getString(10));
    ((TextView)view.findViewById(R.id.kana)).setText(cursor.getString(11));
    ((TextView)view.findViewById(R.id.author)).setText(cursor.getString(12));
    ((TextView)view.findViewById(R.id.publish)).setText(cursor.getString(13));
    ((TextView)view.findViewById(R.id.url)).setText(cursor.getString(14));
    ((TextView)view.findViewById(R.id.comment)).setText(cursor.getString(16));

    File sdcard=Environment.getExternalStorageDirectory();
    File dir =new File(sdcard, "Comiket/C81/PDATA");
    File file=new File(dir,
        String.format("%04d.PNG", cursor.getInt(3)));
    Log.i(TAG, file.getPath());
    Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());

    if(bitmap != null){
      // crop
      // 178 253 8 64  0 0
      // X6
      int index=cursor.getInt(4)-1;
      int y=index/6;
      int x=index%6;
      int w=178;
      int h=253;
      Log.i(TAG, "setImageBitmap("+index+"): "+x+":"+y);
      ((ImageView)view.findViewById(R.id.cut)).setImageBitmap(
      Bitmap.createBitmap(bitmap, 8+w*x, 64+h*y, w, h));
    }
  }

  private View getView(int index){
    while(index<0){
      index+=3;
    }
    return views[index%3];
  }

  private View getRelativeView(int d){
    return getView(viewFlipper.getDisplayedChild()+d);
  }

  private View getNextView(){
    return getView(viewFlipper.getDisplayedChild()+1);
  }

  private View getPreviousView(){
    return getView(viewFlipper.getDisplayedChild()-1);
  }

  private void updateTitle(){
    activity.setTitle(String.format("%s %s%s%d(%d/%d)", 
          def.getDate(getWeekday()),
          def.getAreaName(getBlock()),
          getBlock(),
          getSpace(),
          cursor.getPosition()+1, 
          cursor.getCount()
          ));
  }

  @Override
  public boolean onTouch(View v, MotionEvent event) {
    int x = (int)event.getRawX();
    switch(event.getAction()) {
      case MotionEvent.ACTION_DOWN:
        firstTouch = event.getRawX();
        return true;

      case MotionEvent.ACTION_MOVE:
        if(!isFlipping){
          if(x - firstTouch > 50 && !cursor.isFirst()) {
            isFlipping=true;
            viewFlipper.setInAnimation(
                AnimationUtils.loadAnimation(activity, R.anim.move_right_incoming));
            viewFlipper.setOutAnimation(
                AnimationUtils.loadAnimation(activity, R.anim.move_right_outgoing));

            cursor.moveToPrevious();
            updateTitle();
            viewFlipper.showPrevious();
            // 先読み
            if(!cursor.isFirst()){
              cursor.moveToPrevious();
              bindView(getPreviousView());
              cursor.moveToNext();
            }
          }
          else if(x - firstTouch < -50 && !cursor.isLast()) {
            isFlipping=true;
            viewFlipper.setInAnimation(
                AnimationUtils.loadAnimation(activity, R.anim.move_left_incoming));
            viewFlipper.setOutAnimation(
                AnimationUtils.loadAnimation(activity, R.anim.move_left_outgoing));

            cursor.moveToNext();
            updateTitle();
            viewFlipper.showNext();
            // 先読み
            if(!cursor.isLast()){
              cursor.moveToNext();
              bindView(getNextView());
              cursor.moveToPrevious();
            }
          }
        }
        return false;

      case MotionEvent.ACTION_UP:
        isFlipping=false;
        return false;

      default:
        return false;
    }
  }
}

