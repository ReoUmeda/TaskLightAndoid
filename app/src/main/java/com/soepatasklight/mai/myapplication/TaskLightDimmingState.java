package com.soepatasklight.mai.myapplication;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

/**
 * Created by mai on 2017/01/08.
 */

public class TaskLightDimmingState implements Runnable{
    private int port = 54932;
    private String IP = "172.20.11.93";
    private Socket sc = null;
    private ObjectInputStream ois;
    private boolean connectFlag = true;
    private Handler dvtvHandler;

    public TaskLightDimmingState(Handler handler){
        dvtvHandler = handler;

    }

    public boolean socketConnect(){
        boolean ret = false;
        try {
            Socket sc = new Socket(IP,port);
            this.sc = sc;
            ois = new ObjectInputStream(sc.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            ret = true;
        }
//        Log.e("main","11");
        return ret;
    }

    public void setConnectFlag(boolean flag){
        connectFlag = flag;
    }

    public void close(){
        try {
            sc.close();
            ois.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        while (socketConnect()){
//            Log.e("tds","tds");
        }
//        Log.e("main","12");
        while (connectFlag){
//            Log.e("main","13");
            try {
//                Log.e("main","14");
                DimmingInformation di = (DimmingInformation)ois.readObject();
//                Log.e("main","15");
                Message msg = new Message();
                msg.what = 0x628765;
                msg.obj = di;
                dvtvHandler.sendMessage(msg);
            } catch(EOFException e){
//                Log.e("main","1e3");
                e.printStackTrace();
                close();
                socketConnect();
            } catch (IOException e) {
                e.printStackTrace();
                close();
                socketConnect();
//                Log.e("main","1e1");
            } catch (ClassNotFoundException e) {
//               Log.e("main","1e2");
                e.printStackTrace();
                close();
                socketConnect();
            }
        }

    }
}
