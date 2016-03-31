package com.chinamobile.wifibao.activity;

import android.app.Activity;
import android.os.Bundle;

import com.chinamobile.wifibao.R;

/**
 * Created by cdd on 2016/3/24.
 */
public class BalanceShareActivity extends Activity {
    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.balance_share);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.alpha_in, R.anim.translate_out);
    }
}
