package edu.mit.ecspride;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @testcase_name Application-Modeling
 * @description Test if modeling correctly instantiates a single application object from the manifest
 * and passes the object correctly to calls of getApplication()
 * @dataflow source -> sink
 * @number_of_leaks 1
 * @challenges The analysis tool has to be able to resolve explicit Intent carrying tainted sensitive information
 */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @Override
    protected void onResume() {
        super.onResume();

        long start = System.currentTimeMillis();
        int rep = 100;
        while (rep > 0) {
            aliasFlowTest();
            rep--;
        }
        long end = System.currentTimeMillis();
        Log.d("GuardRail", "time elapsed: " + (end - start));

    }

    class A {
        public String b = "Y";
    }

    public class B {
        public A attr;
    }

//    // Aliasing/SimpleAliasing1
//    private void aliasFlowTest() {
//        TelephonyManager mgr = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
//        String deviceId = mgr.getDeviceId();    // source
//
//        A a = new A();
//        B b = new B();
//        B e = b;
//        A c = a;
//        A d = a;
//        b.attr = c;
//        d.b = deviceId;
//        A f = e.attr;
//
//        SmsManager sms = SmsManager.getDefault();
//        sms.sendTextMessage("+49 1234", null, f.b, null, null); // sink, leak
//    }

//    // ApplicationModeling1
//    private void aliasFlowTest() {
//        TelephonyManager mgr = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
//        String imei = mgr.getDeviceId();	//source
//
//        ((MyApplication)getApplication()).imei = imei;
//
//        Intent i = new Intent(this, AnotherActivity.class);
//        startActivity(i);
//    }

//    // DirectLeak1
//    void aliasFlowTest() {
//        TelephonyManager mgr = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
//        SmsManager sms = SmsManager.getDefault();
//        sms.sendTextMessage("+49 1234", null, mgr.getDeviceId(), null, null); //source, sink, leak
//    }

//    // Parcel1
//    void aliasFlowTest() {
//        TelephonyManager mgr = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
//
//        writeParcel(mgr.getDeviceId()); //source
//    }
//
//    public void writeParcel(String arg) {
//        final Foo orig = new Foo(arg);
//        final Parcel p1 = Parcel.obtain();
//        final Parcel p2 = Parcel.obtain();
//        final byte[] bytes;
//        final Foo result;
//
//        SmsManager sms = SmsManager.getDefault();
//
//        try {
//            p1.writeValue(orig);
//            bytes = p1.marshall();
//
//            String fromP1 = new String(bytes);
//
//
//            p2.unmarshall(bytes, 0, bytes.length);
//            p2.setDataPosition(0);
//            result = (Foo) p2.readValue(Foo.class.getClassLoader());
//
//        } finally {
//            p1.recycle();
//            p2.recycle();
//        }
//
//        sms.sendTextMessage("+49 1234", null, result.str, null, null); //sink, leak
//    }
//
//    protected static class Foo implements Parcelable {
//        public static final Parcelable.Creator<Foo> CREATOR = new Parcelable.Creator<Foo>() {
//            public Foo createFromParcel(Parcel source) {
//                final Foo f = new Foo();
//                f.str = (String) source.readValue(Foo.class.getClassLoader());
//                return f;
//            }
//
//            public Foo[] newArray(int size) {
//                throw new UnsupportedOperationException();
//            }
//
//        };
//
//        public String str;
//
//        public Foo() {
//        }
//
//        public Foo( String s ) {
//            str = s;
//        }
//
//        public int describeContents() {
//            return 0;
//        }
//
//        public void writeToParcel(Parcel dest, int ignored) {
//            dest.writeValue(str);
//        }
//    }

//    // PrivateDataLeak1
//    private User user = null;
//    void aliasFlowTest() {
//        TelephonyManager mgr = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
//
//        user = new User("a", mgr.getDeviceId());
//
//        if(user != null){
//            String password = getPassword();
//            String obfuscatedUsername = "";
//            for(char c : password.toCharArray())
//                obfuscatedUsername += c + "_";
//
//            String message = "User: " + user.getUsername() + " | Pwd: " + obfuscatedUsername;
//            SmsManager smsmanager = SmsManager.getDefault();
//            Log.i("TEST", "sendSMS"); //sink
//            smsmanager.sendTextMessage("+49 1234", null, message, null, null); //sink, leak
//        }
//
//    }
//
//    private String getPassword(){
//        if(user != null)
//            return user.getPwd().getPassword();
//        else{
//            Log.e("ERROR", "no password set");
//            return null;
//        }
//    }

//    // PublicAPIField1
//    void aliasFlowTest() {
//        TelephonyManager mgr = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
//        String imei = mgr.getDeviceId();  //source
//        float fx = Float.valueOf(imei.substring(0, 8));
//        float fy = Float.valueOf(imei.substring(8));
//        PointF point = new PointF(fx, fy);
//
//        Log.i("DroidBench", "IMEI: " + point.x + "" + point.y);  //sink, leak
//    }

//    // PublicAPIField2
//    void aliasFlowTest() {
//        TelephonyManager mgr = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
//        String imei = mgr.getDeviceId();  //source
//
//        Intent i = new Intent();
//        i.setAction(imei);
//
//        Log.i("DroidBench", i.getAction());  //leak
//    }

//    // ArrayAccess3
//    void aliasFlowTest() {
//        String[] arrayData = new String[3];
//
//        arrayData[0] = "element 1 is tainted:";
//        arrayData[1] = ((TelephonyManager) getSystemService(TELEPHONY_SERVICE)).getDeviceId(); //source
//        //arrayData[2] is not tainted
//        arrayData[2] = "neutral text";
//
//        SmsManager sms = SmsManager.getDefault();
//
//        sms.sendTextMessage("+49 1234", null, arrayData[1], null, null);  //sink, leak
//    }

//    // ArrayCopy1
//    void aliasFlowTest() {
//        TelephonyManager mgr = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
//        String imei = mgr.getDeviceId(); //source
//        String[] array = new String[1];
//        array[0] = imei;
//        String[] arraycopy = new String[1];
//        System.arraycopy(array, 0, arraycopy, 0, 1);
//
//        Log.i("DroidBench", arraycopy[0]); //sink
//    }

//    // ArrayToString1
//    void aliasFlowTest() {
//        TelephonyManager mgr = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
//        String imei = mgr.getDeviceId();
//
//        String[] array = new String[1];
//
//        array[0] = imei;
//
//        String arrayToString = Arrays.toString(array);
//
//        Log.i("DroidBench", arrayToString);
//    }

//    // MultidimensionalArray1
//    void aliasFlowTest() {
//        TelephonyManager mgr = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
//        String imei = mgr.getDeviceId(); //source
//        String[][] array = new String[1][1];
//        array[0][0] = imei;
//
//        String[] slice = array[0];
//
//        Log.i("DroidBench", slice[0]); //sink
//    }

//    // FieldSensitivity3
//    void aliasFlowTest() {
//        Datacontainer d1 = new Datacontainer();
//        d1.setDescription("abc");
//        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
//
//        String imei = telephonyManager.getDeviceId(); //source
//        d1.setSecret(imei);
//
//        SmsManager sms = SmsManager.getDefault();
//        sms.sendTextMessage("+49 1234", null, d1.getSecret(), null, null); //sink, leak
//    }

//    // InheritedObject1
//    void aliasFlowTest() {
//        int a = 45 + 1;
//        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
//        General g;
//        if(a == 46){
//            g = new VarA();
//            g.man = telephonyManager;
//        } else{
//            g = new VarB();
//            g.man = telephonyManager;
//        }
//        SmsManager sms = SmsManager.getDefault();
//        sms.sendTextMessage("+49 1234", null, g.getInfo(), null, null);  //sink, leak
//    }

//    // Clone1
//    void aliasFlowTest() {
//        TelephonyManager mgr = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
//        String imei = mgr.getDeviceId();
//        LinkedList<String> list = new LinkedList<String>();
//        list.add(imei);
//
//        LinkedList<String> list2 = (LinkedList<String>)list.clone();
//
//        Log.i("DroidBench", list2.get(0));
//    }
//
//    // Loop1
//    void aliasFlowTest() {
//        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
//        String imei = telephonyManager.getDeviceId(); //source
//
//        String obfuscated = "";
//        for(char c : imei.toCharArray())
//            obfuscated += c + "_";
//
//        SmsManager sm = SmsManager.getDefault();
//
//        sm.sendTextMessage("+49 1234", null, obfuscated, null, null); //sink, leak
//    }

//    // Loop2
//    void aliasFlowTest() {
//        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
//        String imei = telephonyManager.getDeviceId(); //source
//
//        String obfuscated = "";
//        for(int i = 0; i < 10; i++)
//            if(i == 9)
//                for(char c : imei.toCharArray())
//                    obfuscated += c + "_";
//
//        SmsManager sm = SmsManager.getDefault();
//
//        sm.sendTextMessage("+49 1234", null, obfuscated, null, null); //sink, leak
//    }

//    // Exception1
//    void aliasFlowTest() {
//        String imei = "";
//        try {
//            TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
//            imei = telephonyManager.getDeviceId(); //source
//            throw new RuntimeException();
//        }
//        catch (RuntimeException ex) {
//            SmsManager sm = SmsManager.getDefault();
//            sm.sendTextMessage("+49 1234", null, imei, null, null); //sink, leak
//        }
//    }

//    // Exception2
//    void aliasFlowTest() {
//        String imei = "";
//        try {
//            TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
//            imei = telephonyManager.getDeviceId(); //source
//            int[] arr = new int[(int) Math.sqrt(49)];
//            if (arr[32] > 0)
//                imei = "";
//        }
//        catch (RuntimeException ex) {
//            SmsManager sm = SmsManager.getDefault();
//            sm.sendTextMessage("+49 1234", null, imei, null, null); //sink, leak
//        }
//    }

//    // Exception4
//    void aliasFlowTest() {
//        try {
//            TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
//            String imei = telephonyManager.getDeviceId(); //source
//            throw new RuntimeException(imei);
//        }
//        catch (RuntimeException ex) {
//            SmsManager sm = SmsManager.getDefault();
//            sm.sendTextMessage("+49 1234", null, ex.getMessage(), null, null); //sink, leak
//        }
//    }

//    // Exception6
//    String imei;
//    void aliasFlowTest() {
//        try {
//            callMe();
//        }
//        catch (RuntimeException ex) {
//            SmsManager sm = SmsManager.getDefault();
//            sm.sendTextMessage("+49 1234", null, imei, null, null); //sink, leak
//        }
//    }
//
//    private void callMe() {
//        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
//        imei = telephonyManager.getDeviceId(); //source
//        String[] arr = new String[42];
//        System.out.println(arr[1337]);
//    }

//    // SourceCodeSpecific1
//    void aliasFlowTest() {
//        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
//
//
//        Set<String> phoneNumbers = new HashSet<String>();
//        phoneNumbers.add("+49 123456");
//        phoneNumbers.add("+49 654321");
//        phoneNumbers.add("+49 111111");
//        phoneNumbers.add("+49 222222");
//        phoneNumbers.add("+49 333333");
//
//        int a = 22 + 11;
//        int b = 22 * 2 - 1 + a;
//
//        String message = (a == b) ? "no taint" : telephonyManager.getDeviceId(); //source
//
//        sendSMS(phoneNumbers, message);
//    }
//
//    private void sendSMS(Set<String> numbers, String message){
//        SmsManager sm = SmsManager.getDefault();
//
//        for(String number : numbers){
//            sm.sendTextMessage(number, null, message, null, null); //sink
//        }
//    }

//    // StartProcessWithSecret1
//    void aliasFlowTest() {
//        TelephonyManager mgr = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
//        String imei = mgr.getDeviceId();  //source
//
//        try {
//            ProcessBuilder pb = new ProcessBuilder();
//            pb.command("cmd", imei);
//            pb.start();  //leak (assuming cmd is a leak)
//        } catch (Exception e) {
//
//        }
//    }

//    // StaticInitialization1
//    public static String im;
//    void aliasFlowTest() {
//        im = ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId(); //source
//        StaticInitClass1 s1 = new StaticInitClass1();
//    }
//
//    public static class StaticInitClass1{
//        static{
//            SmsManager sms = SmsManager.getDefault();
//            sms.sendTextMessage("+49 1234", null, im, null, null);   //sink, leak
//        }
//
//    }

//    // StringPatternMatching1
//    void aliasFlowTest() {
//        TelephonyManager mgr = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
//        String imei = mgr.getDeviceId();
//
//        Pattern p = Pattern.compile("(.*)");
//        Matcher matcher = p.matcher(imei);
//
//        if (matcher.matches()) {
//            String match = matcher.group(1);
//            Log.i("DroidBench", match);
//        }
//    }

//    // StringToCharArray1
//    void aliasFlowTest() {
//        TelephonyManager mgr = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
//        String imei = mgr.getDeviceId();  //source
//        char[] chars = new char[imei.length()];
//
//        imei.getChars(0, imei.length(), chars, 0);
//
//        String builtImei = "";
//        for (int i = 0; i < chars.length; i++)
//            builtImei += chars[i];
//
//        Log.i("DroidBench", builtImei);  //sink, leak
//    }

//    // StringToOutputStream1
//    void aliasFlowTest() {
//        TelephonyManager mgr = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
//        String imei = mgr.getDeviceId();
//        byte[] bytes = imei.getBytes();
//
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        out.write(bytes, 0, bytes.length);
//
//        String outString = out.toString();
//
//        Log.i("DroidBench", outString);
//    }

//    // Reflection1
//    void aliasFlowTest() {
//        BaseClass bc = null;
//        try {
//            bc = (BaseClass) Class.forName("edu.mit.ecspride.ConcreteClass").newInstance();
//        } catch (InstantiationException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//        bc.imei = telephonyManager.getDeviceId(); //source
//
//        SmsManager sms = SmsManager.getDefault();
//        sms.sendTextMessage("+49 1234", null, bc.imei, null, null);   //sink, leak
//    }

//    // Reflection2
//    void aliasFlowTest() {
//        try {
//            BaseClass bc = (BaseClass) Class.forName("edu.mit.ecspride.ConcreteClass").newInstance();
//            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//            bc.imei = telephonyManager.getDeviceId(); //source
//
//            SmsManager sms = SmsManager.getDefault();
//            sms.sendTextMessage("+49 1234", null, bc.foo(), null, null);   //sink, leak
//        } catch (InstantiationException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//    }

//    // Reflection3
//    void aliasFlowTest() {
//        try {
//            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//            String imei = telephonyManager.getDeviceId(); //source
//
//            Class c = Class.forName("edu.mit.ecspride.ReflectiveClass");
//            Object o = c.newInstance();
//            Method m = c.getMethod("setIme" + "i", String.class);
//            m.invoke(o, imei);
//
//            Method m2 = c.getMethod("getImei");
//            String s = (String) m2.invoke(o);
//
//            SmsManager sms = SmsManager.getDefault();
//            sms.sendTextMessage("+49 1234", null, s, null, null);   //sink, leak
//        } catch (InstantiationException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (NoSuchMethodException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (IllegalArgumentException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//    }

//    // Reflection4
//    void aliasFlowTest() {
//        try {
//            BaseClass bc = (BaseClass) Class.forName("edu.mit.ecspride.ConcreteClass").newInstance();
//            String s = bc.foo(this);
//            bc.bar(s);
//        } catch (InstantiationException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//    }

//    // Reflection5
//    void aliasFlowTest() {
//        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//        String source = telephonyManager.getDeviceId(); // source
//
//        try {
//            Method method = getClass().getDeclaredMethod("log", String.class);
//            method.invoke(this, source);
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//        } catch (IllegalArgumentException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
//        }
//    }
//    public void log(String msg) {
//        Log.i("DroidBench", msg); // sink
//    }

//    // Reflection6
//    void aliasFlowTest() {
//        try {
//            Method method = getClass().getMethod("foo");
//            String deviceId = (String) method.invoke(this);
//            Log.i("DroidBench", deviceId); // sink
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//        } catch (IllegalArgumentException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
//        }
//    }
//    public String foo() {
//        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//        return telephonyManager.getDeviceId(); // source
//    }

//    // Reflection8
//    void aliasFlowTest() {
//        try {
//            Class<?> clz = Class.forName("edu.wayne.cs.ConcreteClass");
//            BaseClass bc = (BaseClass) clz.newInstance();
//
//            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//            bc.imei = telephonyManager.getDeviceId(); // source
//
//            Method method = clz.getMethod("foo", String.class);
//            method.invoke(bc, bc.imei);
//        } catch (InstantiationException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        } catch (IllegalArgumentException e) {
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//        }
//    }

//    // AsyncTask1
//    void aliasFlowTest() {
//        TelephonyManager telephonyManager = (TelephonyManager)
//                getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
//        MyAsyncTask async = new MyAsyncTask();
//        async.execute(telephonyManager.getDeviceId());
//    }
//
//    private class MyAsyncTask extends AsyncTask<String, String, String> {
//
//        @Override
//        protected String doInBackground(String... params) {
//            Log.i("DroidBench", params[0]);
//            return "Done";
//        }
//    }

//    // Executor1
//    void aliasFlowTest() {
//        TelephonyManager telephonyManager = (TelephonyManager)
//                getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
//        Executors.newCachedThreadPool().execute(new MyRunnable(telephonyManager.getDeviceId()));
//    }
//
//    private class MyRunnable implements Runnable {
//
//        private final String deviceId;
//
//        public MyRunnable(String deviceId) {
//            this.deviceId = deviceId;
//        }
//
//        @Override
//        public void run() {
//            Log.i("DroidBench", deviceId);
//        }
//
//    }

    // Looper1
    void aliasFlowTest() {
        LooperThread lpt = new LooperThread();
        lpt.start();

        while (!lpt.ready)
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        TelephonyManager telephonyManager = (TelephonyManager)
                getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = telephonyManager.getDeviceId();

        Message msg = new Message();
        msg.obj = deviceId;
        LooperThread.handler.dispatchMessage(msg);
    }
}
