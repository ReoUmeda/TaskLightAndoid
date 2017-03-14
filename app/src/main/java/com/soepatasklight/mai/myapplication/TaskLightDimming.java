package com.soepatasklight.mai.myapplication;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by mai on 2017/01/08.
 */

public class TaskLightDimming implements Runnable{
    private final static int port = 54931;
    private final static String IP = "172.20.11.93";
    private Socket sc = null;
    private PrintWriter pw;
    private BufferedReader br;
    private DimmingInformation di;
    private Handler presenceHandler;

    public TaskLightDimming(DimmingInformation di,Handler handler){
        this.di = di;
        presenceHandler = handler;
    }


    public boolean socketConnect(){
        boolean ret = true;
        try {
            Socket sc = new Socket(IP,port);
            this.sc = sc;
            pw = new PrintWriter(new PrintStream((sc.getOutputStream())));
            br = new BufferedReader(new InputStreamReader(sc.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
            ret = false;
        }

        return ret;
    }

    public void close(){
        try {
            sc.close();
            pw.close();
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {

        long start = System.currentTimeMillis();

        if (socketConnect()) {
//            Log.e("td","td");
            try {
                pw.println(di.getDimmingValue());
                pw.println(di.getToningValue());
                pw.println(di.getUuid());
                pw.flush();
                if (br.readLine() == "false") {

                }


            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                close();
            }
        }else{
            Message msg = new Message();
            msg.what = 0x50732;
            msg.obj = new TaskLightServerErrorMessage("エラー","サーバーが起動しているか確認し，再度調光してください");
            presenceHandler.sendMessage(msg);
        }

    }
}
