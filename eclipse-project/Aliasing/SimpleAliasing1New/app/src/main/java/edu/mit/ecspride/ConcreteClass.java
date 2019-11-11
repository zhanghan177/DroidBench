package edu.mit.ecspride;

import android.content.Context;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;

public class ConcreteClass extends BaseClass {

    @Override
    public String foo(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId(); //source
    }

    public void foo(String msg) {
        Log.i("DroidBench", msg); // sink
    }

    @Override
    public void bar(String s) {
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage("+49 1234", null, s, null, null);   //sink, leak
    }
}
