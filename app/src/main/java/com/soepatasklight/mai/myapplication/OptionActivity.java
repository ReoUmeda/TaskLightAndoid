package com.soepatasklight.mai.myapplication;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.beardedhen.androidbootstrap.BootstrapAlert;
import com.beardedhen.androidbootstrap.BootstrapEditText;

/**
 * Created by mai on 2017/01/09.
 */

public class OptionActivity extends Activity{
    BootstrapEditText dv;
    BootstrapEditText tv;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);
//        FragmentManager fragmentManager = getFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//
//        OptionFragment fragment = new OptionFragment();
//        fragmentTransaction.add(R.id.fragment_container, fragment, "tag");
//        fragmentTransaction.commit();

        dv = (BootstrapEditText)findViewById(R.id.spinnerdvb);
        tv = (BootstrapEditText)findViewById(R.id.spinnertvb);

        //調光値読み出し
        SharedPreferences prefDimmingValueRead = getSharedPreferences("pref",Context.MODE_PRIVATE);
        int dimmingValue = prefDimmingValueRead.getInt("dimmingValue",-1);

        //調色値読み出し
        SharedPreferences prefToningValueRead = getSharedPreferences("pref",Context.MODE_PRIVATE);
        int toningValue = prefToningValueRead.getInt("toningValue",-1);

        dv.setText(String.valueOf(dimmingValue));
        tv.setText(String.valueOf(toningValue));
    }

    @Override
    public void onPause(){
        super.onPause();

        //調光値
        //データ書き込み
        SharedPreferences prefdvWrite = getSharedPreferences("pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor edv = prefdvWrite.edit();
        String str = String.valueOf(dv.getText());
        String result = str.replaceAll("[^0-9]+", "");
        int tmp = Integer.parseInt(result);
//        if(tmp > 255){
//            tmp = 255;
//        }else if(tmp < 0){
//            tmp = 0;
//        }
        edv.putInt("dimmingValue", tmp);
        edv.commit();

        //調色値
        //データ書き込み
        SharedPreferences preftvWrite = getSharedPreferences("pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor etv = preftvWrite.edit();
        str = String.valueOf(tv.getText());
        result = str.replaceAll("[^0-9]+", "");
        tmp = Integer.parseInt(result);
//        if(tmp > 255){
//            tmp = 255;
//        }else if(tmp < 0){
//            tmp = 0;
//        }
        etv.putInt("toningValue", tmp);
        etv.commit();
    }
}
