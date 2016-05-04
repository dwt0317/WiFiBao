package com.chinamobile.wifibao.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.chinamobile.wifibao.R;

/**
 * Created by apple on 2016/4/20.
 */
public class MobileServiceActivity  extends Activity {

        public void onCreate(Bundle saveInstanceState) {
            super.onCreate(saveInstanceState);
            setContentView(R.layout.mobile_service);
            ImageView goback = (ImageView)findViewById(R.id.home);
            goback.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MobileServiceActivity.this, Home2Activity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            });

        }

}
