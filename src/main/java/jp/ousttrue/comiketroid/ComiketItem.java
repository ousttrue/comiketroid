package jp.ousttrue.comiketroid;

import android.os.Bundle;
import android.app.Activity;
import android.net.Uri;
import android.util.Log;
import android.widget.ViewFlipper;


public class ComiketItem extends Activity {

  private static final String TAG = "ComiketItem";

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

    setContentView(R.layout.flipper);

    ViewFlipper viewFlipper = (ViewFlipper) findViewById(R.id.flipper);
    viewFlipper.setAutoStart(false);
    CursorBindingTouchListener listener=
      new CursorBindingTouchListener(this, viewFlipper);
  }
}

