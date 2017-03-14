package com.soepatasklight.mai.myapplication;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.LongSparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapAlert;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapCircleThumbnail;
import com.beardedhen.androidbootstrap.BootstrapText;
import com.beardedhen.androidbootstrap.api.view.BootstrapTextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private DimmingInformation di;
    private  DimmingInformation di2;
    private Handler dvtvHandler;
    private Handler presenceHandler;
    private Handler leaveSeatHandler;
    private Handler SeatpresenceHandler;
    private ImageButton seat;
    private boolean seatSitFlage = false;
    private BootstrapButton seatPresence;
    private BootstrapButton seatLeaveSeat;
    private BootstrapTextView dvlx;
    private BootstrapTextView tvk;
    private Spinner spinnerdv;
    private String spinnerdvItems[] = {"600lx", "1200lx"};
    private Spinner spinnertv;
    private String spinnertvItems[] = {"3000K", "4600K"};
    private BootstrapButton seatDimming;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private Context activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setActivity(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        final WifiManager wifi = (WifiManager) getSystemService(WIFI_SERVICE);
        if(!wifi.isWifiEnabled()){
            // 確認ダイアログの生成
            AlertDialog.Builder alertDlg = new AlertDialog.Builder(this);
            alertDlg.setTitle("WI-FI確認");
            alertDlg.setMessage("WI-FIがOFFになっています\nWI-FIをONにしますか?");
            alertDlg.setPositiveButton(
                    "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // OK ボタンクリック処理
                            wifi.setWifiEnabled(true);
                        }
                    });
            alertDlg.setNegativeButton(
                    "Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Cancel ボタンクリック処理
                        }
                    });


            // 表示
            alertDlg.create().show();
        }

//        fragmentManager = getFragmentManager();
//        fragmentTransaction = fragmentManager.beginTransaction();

        //uuid読み出し
        SharedPreferences prefuuidRead = getSharedPreferences("pref",Context.MODE_PRIVATE);
        String uuid = prefuuidRead.getString("uuid",null);
        if(uuid == null){

            //uuid
            UUID uuid1 = UUID.randomUUID();
            //uuid
            UUID uuid2 = UUID.randomUUID();

            //データ書き込み
            SharedPreferences prefuuidWrite = getSharedPreferences("pref", Context.MODE_PRIVATE);
            SharedPreferences.Editor e = prefuuidWrite.edit();
            e.putString("uuid",uuid1+"-"+uuid2);
            e.commit();
            uuid = uuid1+"-"+uuid2;
        }

        //調光値読み出し
        SharedPreferences prefDimmingValueRead = getSharedPreferences("pref",Context.MODE_PRIVATE);
        int dimmingValue = prefDimmingValueRead.getInt("dimmingValue",-1);

        //調色値読み出し
        SharedPreferences prefToningValueRead = getSharedPreferences("pref",Context.MODE_PRIVATE);
        int toningValue = prefToningValueRead.getInt("toningValue",-1);

        di = new DimmingInformation(dimmingValue,toningValue,uuid);
        di2 = new DimmingInformation(-1,-1,uuid);
        //di = null;
//        di = new DimmingInformation(1400,6900,uuid);
        dvtvHandler = new Handler(){
            //調光値,調色値の書き換え用
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 0x628765){
                    DimmingInformation tmp = (DimmingInformation)msg.obj;
                    if(seatSitFlage){
                        setDimmingValueToningValue(tmp.getDimmingValue(),tmp.getToningValue());
                    }
//                    Log.d(String.valueOf(tmp.getDimmingValue()),String.valueOf(tmp.getToningValue()));
                }
            }
        };

        presenceHandler = new Handler(){
            //サーバーと通信できなかった時のflag書き換え用
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 0x50732){
                    chageSeatSitFlag();
                    servernotDialog(((TaskLightServerErrorMessage)msg.obj).getTitle(),((TaskLightServerErrorMessage)msg.obj).getMessege());
                }
            }
        };
        leaveSeatHandler = new Handler(){
            //サーバーと通信できなかった時のflag書き換え用

            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 0x474432){
                    chageSeatSitFlag();
                    servernotDialog(((TaskLightServerErrorMessage)msg.obj).getTitle(),((TaskLightServerErrorMessage)msg.obj).getMessege());
                }
            }
        };


        //現在のタスクライトの調光，調色確認用(サーバープッシュ)
        new Thread(new TaskLightDimmingState(dvtvHandler)).start();

        //在離席ボタン(画像)
        seat = (ImageButton)findViewById(R.id.seat);
        seat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                changeSeatStandSit();
            }
        });

        //在席ボタン(ボタン)
        seatPresence = (BootstrapButton)findViewById(R.id.presence);
        seatPresence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!seatSitFlage){
                    changeSeatStandSit();
                }
            }
        });

        //離席ボタン(ボタン)
        seatLeaveSeat = (BootstrapButton)findViewById(R.id.leaveseat);
        seatLeaveSeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(seatSitFlage){
                    changeSeatStandSit();
                }
            }
        });

        //調光調色用ボタン(ボタン)
        seatDimming = (BootstrapButton)findViewById(R.id.dimming);
        seatDimming.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(seatSitFlage){
                    dimming(di2);
                }
            }
        });

        //調光値表示用
        dvlx = (BootstrapTextView)findViewById(R.id.dvlx);
        //調色値表示用
        tvk = (BootstrapTextView)findViewById(R.id.tvk);


        spinnerdv = (Spinner)findViewById(R.id.spinnerdv);
        // ArrayAdapter
        ArrayAdapter<String> adapterdv
                = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerdvItems);
        adapterdv.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // spinner に adapter をセット
        spinnerdv.setAdapter(adapterdv);

        // リスナーを登録
        spinnerdv.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            //　アイテムが選択された時
            public void onItemSelected(AdapterView<?> parent, View viw, int arg2, long arg3) {
                Spinner spinner = (Spinner) parent;
                String item = (String) spinner.getSelectedItem();

                di2 = new DimmingInformation(Integer.parseInt(removeNumbersOther(item)),di2.getToningValue(),di2.getUuid());



            }

            //　アイテムが選択されなかった
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinnertv = (Spinner)findViewById(R.id.spinnertv);
        // ArrayAdapter
        ArrayAdapter<String> adaptertv
                = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnertvItems);
        adapterdv.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // spinner に adapter をセット
        spinnertv.setAdapter(adaptertv);

        // リスナーを登録
        spinnertv.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            //　アイテムが選択された時
            public void onItemSelected(AdapterView<?> parent, View viw, int arg2, long arg3) {
                Spinner spinner = (Spinner) parent;
                String item = (String) spinner.getSelectedItem();

                di2 = new DimmingInformation(di2.getDimmingValue(),Integer.parseInt(removeNumbersOther(item)),di2.getUuid());

            }

            //　アイテムが選択されなかった
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

//        //テスト
//        di = new DimmingInformation(255,255,uuid);
//        Log.e("main","1");
//        new Thread(new TaskLightDimmingState(dvtvHandler)).start();
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        Log.e("main","2");
//        new Thread(new TaskLightDimming(di)).start();
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        Log.e("main","3");
//        new Thread(new TaskLightLeave(uuid)).start();


    }

    //離席在席の画像を変更(トグル)
    private void changeSeatImage(){
        if(seatSitFlage){
            seat.setImageResource(R.drawable.leaveseat);
        }else{
            seat.setImageResource(R.drawable.presence);
        }
    }
    //離席在席を変更(トグル)
    //処理の順番は他の場所にも影響する(dvtvHander,changeSeatImage())
    private void changeSeatStandSit(){
        changeSeatImage();
        if(seatSitFlage){
            seatSitFlage = false;
            seatLeave();
        }else{
            seatSitFlage = true;


            //調光値読み出し
            SharedPreferences prefDimmingValueRead = getSharedPreferences("pref",Context.MODE_PRIVATE);
            int dimmingValue = prefDimmingValueRead.getInt("dimmingValue",-1);

            //調色値読み出し
            SharedPreferences prefToningValueRead = getSharedPreferences("pref",Context.MODE_PRIVATE);
            int toningValue = prefToningValueRead.getInt("toningValue",-1);
            //uuid
            SharedPreferences prefuuidRead = getSharedPreferences("pref",Context.MODE_PRIVATE);
            String uuid = prefuuidRead.getString("uuid",null);

            di = new DimmingInformation(dimmingValue,toningValue,uuid);

            dimming(di);
        }
    }

    //在席離席フラグ変更
    private void chageSeatSitFlag(){
        if(seatSitFlage){
            changeSeatImage();
            seatSitFlage = false;
        }else{
            changeSeatImage();
            seatSitFlage = true;
        }
    }

    //在席
    private void dimming(DimmingInformation di){
        Thread t = new Thread(new TaskLightDimming(di,presenceHandler));
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
    //離席
    private void seatLeave(){
        Thread t = new Thread(new TaskLightLeave(di.getUuid(),leaveSeatHandler));
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        setDimmingValueToningValue(0,0);
    }

    //調光値調色値変更
    private void setDimmingValueToningValue(int dv,int tv){
        String tmpdv = "";
        String tmptv = "";

        if(tv < 10){
            tmptv = "      ";
        }else if(tv < 100){
            tmptv = "    ";
        }else if(tv < 1000){
            tmptv = "  ";
        }

        if(dv < 10){
            tmpdv = "      ";
        }else if(dv < 100){
            tmpdv = "    ";
        }else if(dv < 1000){
            tmpdv = "  ";
        }

        dvlx.setMarkdownText(tmpdv+String.valueOf(dv)+"lx");
        tvk.setMarkdownText(tmptv+String.valueOf(tv)+"K");

    }

    //サーバーが落ちていて通信できない時ダイアログ
    private void servernotDialog(String title,String message){
        new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    private String removeNumbersOther(String str) {
        String result = str.replaceAll("[^0-9]+", "");
        return result;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId()){
            case R.id.menu_settings:

//                OptionFragment oFragment = new OptionFragment();
//                fragmentTransaction.add(R.id.fragment_container, oFragment);
//                fragmentTransaction.addToBackStack(null);
//                fragmentTransaction.commit();
                Intent intent = new Intent(getApplication(), OptionActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }

    public Context getActivity() {
        return activity;
    }
    public void setActivity(Context activity){
        this.activity = activity;
    }
}
