package com.sparkzi.pannicedittext;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

public class PanningEditText extends EditText {

  public PanningEditText(Context context, AttributeSet attrSet) {
    super(context, attrSet);
  }

  @Override
  public boolean onKeyPreIme(int keyCode, KeyEvent keyEvent)
  {
    if(keyCode == KeyEvent.KEYCODE_BACK)
    {
      clearFocus();
    }
    return super.onKeyPreIme(keyCode, keyEvent);
  }
}
