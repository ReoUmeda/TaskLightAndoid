package com.soepatasklight.mai.myapplication;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PipedWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by mai on 2017/01/08.
 */

public class TaskLightLeave implements Runnable{
    private int port = 54933;
    private String IP = "172.20.11.93";
    private Socket sc = null;
    private PrintWriter pw;
    private BufferedReader br;
    private String uuid;
    private Handler leaveSeatHandler;

    public TaskLightLeave(String uuid,Handler handler){
        this.uuid = uuid;
        leaveSeatHandler = handler;

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
        if (socketConnect()){
//            Log.e("tdl","tdl");

            try {
                pw.println(uuid);
                pw.flush();

                if(br.readLine() == "false"){

                }

            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                close();
            }
        }else{
            Message msg = new Message();
            msg.what = 0x474432;
            msg.obj = new TaskLightServerErrorMessage("エラー","サーバーが起動しているか確認し，再度離席してください");
            leaveSeatHandler.sendMessage(msg);
        }

    }
}
