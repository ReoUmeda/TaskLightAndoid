package com.soepatasklight.mai.myapplication;

/**
 * Created by mai on 2017/01/08.
 */

import android.content.ContentResolver;
import android.provider.Settings;

import java.io.Serializable;

public class DimmingInformation implements Serializable{
    private int dimmingValue = -1;
    private int toningValue = -1;
    private String uuid = null;
    private static final long serialVersionUID = 3L;

    public DimmingInformation(int dv,int tv,String uuid) {
        // TODO 自動生成されたコンストラクター・スタブ
        dimmingValue = dv;
        toningValue = tv;
        this.uuid = uuid;
    }



    public int getDimmingValue() {
        return dimmingValue;
    }

    public int getToningValue() {
        return toningValue;
    }

    public String getUuid() {
        return uuid;
    }


}
