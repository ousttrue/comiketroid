package jp.ousttrue.comiketroid;

import android.widget.RelativeLayout;
import android.util.Log;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;


public class FloatLayout extends RelativeLayout {
  private static final String TAG = "RelativeLayout";

  public FloatLayout(Context context) {
    this(context, null);
  }
  
  public FloatLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override
  protected void onMeasure(int widthMeasureSpec,int heightMeasureSpec){
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
  
    int childCount = getChildCount();
    Log.d(TAG, "layout: "+childCount);
    if (childCount == 0) {
      return;
    }

    int max=getMeasuredWidth();
    // first
    View pline=getChildAt(0);
    int x=0;
    int b=pline.getMeasuredHeight();
    for (int i = 1; i < childCount; i++) {
      View button=getChildAt(i);
      RelativeLayout.LayoutParams prm = 
        (RelativeLayout.LayoutParams)button.getLayoutParams();
      int w = button.getMeasuredWidth() + 
        button.getPaddingLeft()+button.getPaddingRight();
      if (x + w > max) {
        prm.addRule(RelativeLayout.BELOW, pline.getId());
        pline = button;
        x=0;
        b+=button.getMeasuredHeight();
      }
      else{
        prm.addRule(RelativeLayout.ALIGN_TOP, i);
        prm.addRule(RelativeLayout.RIGHT_OF, i);
      }
      x+=w;
    }

    Log.d(TAG, String.format("%d-%d", max, b));
    setMeasuredDimension(max, b);
  }

  @Override
  protected void onLayout (boolean changed, int l, int t, int r, int b)
  {
    super.onLayout(changed, l, t, r, b);
    int x=l;
    int y=t;
    for (int i=0; i<getChildCount(); i++) {
      View button=getChildAt(i);
      int w=button.getWidth()+button.getPaddingLeft()+button.getPaddingRight();
      if(x+w>r){
        x=l;
        y+=button.getHeight();
      }
      button.layout(x, y, x+button.getWidth(), y+button.getHeight());
      x+=w;
    }
  }
}

