/*
 * The MIT License (MIT)
 * Copyright (c) 2015 Joel De La Torriente - jjdltc - http://www.jjdltc.com/
 * See a full copy of license in the root folder of the project
 */
package com.jjdltc.cordova.plugin.sftp;

import org.apache.cordova.CordovaWebView;
import com.jcraft.jsch.SftpProgressMonitor;

import android.util.Log;

public class progressMonitor implements SftpProgressMonitor{
    
    private long max                = 0;
    private long count              = 0;
    private long percent            = 0;
    private CordovaWebView actualWv = null;
    
    public progressMonitor(CordovaWebView actualWv) {
        this.actualWv   = actualWv;
    }

    public void init(int op, java.lang.String src, java.lang.String dest, long max) {
        this.max = max;
        Log.d("SFTP Plugin - JJDLTC", "Action Start, From: "+src+" <=> To: "+dest);
        Log.d("SFTP Plugin - JJDLTC", "Action Start, Size: "+max+" <=> Opt: "+op);
        this.jsEvent("SFTPActionStart", "{from:'"+src+"',to:'"+dest+"',size:'"+max+"'}");
    }

    public boolean count(long bytes){
        this.count += bytes;
        long percentNow = this.count*100/max;
        if(percentNow>this.percent){
            this.percent = percentNow;
            this.jsEvent("SFTPActionProgress", "{percent:'"+percent+"'}");
            Log.d("SFTP Plugin - JJDLTC", "Action Progress: "+this.percent+"%");
        }
        return(true);
    }

    public void end(){
        this.jsEvent("SFTPActionEnd", null);
        Log.d("SFTP Plugin - JJDLTC", "Action Finish");
    }
    
    @SuppressWarnings("deprecation")
    private void jsEvent(String evt, String data){
        String eventString = "cordova.fireDocumentEvent('"+evt+"'";
        if(data != null && !data.isEmpty()){
            eventString += ", "+data;
        }
        eventString += ");";
        Log.d("JJDLTC JS TEST", eventString);
        this.actualWv.sendJavascript(eventString);
    }
}