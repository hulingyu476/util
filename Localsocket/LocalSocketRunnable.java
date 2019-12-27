package com.hht.crestron;

import android.content.Context;
import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.os.Handler;

import com.hht.crestron.utils.DefaultLogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

/**
 * @author Realmo
 * @version 1.0.0
 * @name LocalSocketRunnable
 * @email momo.weiye@gmail.com
 * @time 2019/4/9 19:36
 * @describe
 */
public class LocalSocketRunnable implements Runnable{

    private static final String ADDRESS ="sys_order";
    private int timeout=30000;
    private LocalSocket client;
    private PrintWriter os;
    private BufferedReader is;



    public LocalSocketRunnable(Handler handler, Context context){

    }

    public LocalSocketRunnable(Context context){

    }

    //发数据
    public void send(String data){
        if (os!=null) {
            os.println(data);
            os.flush();
        }
    }

    @Override
    public void run() {
        client=new LocalSocket();
        try {
            client.connect(new LocalSocketAddress(ADDRESS, LocalSocketAddress.Namespace.RESERVED));
            DefaultLogger.verbose("Connect succes");
            client.setSoTimeout(timeout);
            os=new PrintWriter(client.getOutputStream());
            is=new BufferedReader(new InputStreamReader(client.getInputStream()));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        String result="";
        while(true){
            try {
                result=is.readLine();
                DefaultLogger.verbose("from server raw data:"+ result);


            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    public void close(){
        try {
            if (os!=null) {
                os.close();
            }
            if (is!=null) {
                is.close();
            }
            if(client!=null){
                client.close();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }




}