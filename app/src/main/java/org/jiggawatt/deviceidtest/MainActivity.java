package org.jiggawatt.deviceidtest;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private final static int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 123579;

    private final static int MSG_UPDATE_LABELS = 246;

    private String mDefaultImei = "";
    private int mNumSimSlots;
    private int mRun = 0;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHandler = new Handler() {
            public void handleMessage(Message msg) {
                final int what = msg.what;
                switch(what) {
                    case MSG_UPDATE_LABELS:
                        updateLabels(true);
                        break;
                    default:
                        break;
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        mRun++;
        getImei();
    }

    private void getImei() {
        updateLabels(false);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_PHONE_STATE},
                    MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
        } else {
            onRequestPermissionsResult(MY_PERMISSIONS_REQUEST_READ_PHONE_STATE,
                    new String[]{Manifest.permission.READ_PHONE_STATE},
                    new int[]{PackageManager.PERMISSION_GRANTED});
        }
    }

    private void updateLabels(final boolean haveNewInfo) {
        if (haveNewInfo) {
            ((TextView) findViewById(R.id.textView)).setText("Run " + mRun);
            ((TextView) findViewById(R.id.textView2)).setText("API level: " + Build.VERSION.SDK_INT);
            ((TextView) findViewById(R.id.textView3)).setText("# SIM slots: " + ((mNumSimSlots == -1) ? "?" : mNumSimSlots));
            ((TextView) findViewById(R.id.textView4)).setText("Default IMEI: " + mDefaultImei);
        } else {
            ((TextView) findViewById(R.id.textView)).setText("Gathering info..");
            ((TextView) findViewById(R.id.textView2)).setText("");
            ((TextView) findViewById(R.id.textView3)).setText("");
            ((TextView) findViewById(R.id.textView4)).setText("");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_PHONE_STATE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    final TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    mDefaultImei = tm.getDeviceId();
                } else {
                    mDefaultImei = "No access";
                }
                break;
            }
        }
        if (Build.VERSION.SDK_INT >= 22) {
            SubscriptionManager sm = SubscriptionManager.from(this);
            mNumSimSlots = sm.getActiveSubscriptionInfoCountMax();
        } else {
            mNumSimSlots = -1;
        }
        mHandler.sendEmptyMessage(MSG_UPDATE_LABELS);
    }
}
