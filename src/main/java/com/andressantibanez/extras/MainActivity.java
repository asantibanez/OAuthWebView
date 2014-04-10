package com.andressantibanez.extras;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

import com.andressantibanez.oauthwebview.R;

public class MainActivity extends Activity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_main);
    }
}
