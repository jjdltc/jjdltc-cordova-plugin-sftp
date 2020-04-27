/*
 * The MIT License (MIT)
 * Copyright (c) 2015 Joel De La Torriente - jjdltc - https://github.com/jjdltc
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
    private String udid             = null;
    
    public progressMonitor(CordovaWebView actualWv, String udid) {
        this.actualWv   = actualWv;
        this.udid       = udid;
    }

    public void init(int op, java.lang.String src, java.lang.String dest, long max) {
        this.max = max;
        this.jsEvent("SFTPActionStart", "{id:'"+this.udid+"', from:'"+src+"',to:'"+dest+"',size:'"+max+"'}");
        Log.d("SFTP Plugin - JJDLTC", "Action Start, From: "+src+" <=> To: "+dest+" [ID:"+this.udid+"]");
        Log.d("SFTP Plugin - JJDLTC", "Action Start, Size: "+max+" <=> Opt: "+op+" [ID:"+this.udid+"]");
    }

    public boolean count(long bytes){
        this.count += bytes;
        long percentNow = this.count*100/max;
        if(percentNow>this.percent){
            this.percent = percentNow;
            this.jsEvent("SFTPActionProgress", "{id:'"+this.udid+"', percent:'"+percent+"'}");
            Log.d("SFTP Plugin - JJDLTC", "Action Progress: "+this.percent+"%. [ID:"+this.udid+"]");
        }
        return(true);
    }

    public void end(){
        this.jsEvent("SFTPActionEnd", "{id:'"+this.udid+"'}");
        Log.d("SFTP Plugin - JJDLTC", "Action Finish. [ID:"+this.udid+"]");
    }
    
    @SuppressWarnings("deprecation")
    private void jsEvent(String evt, String data){
        String eventString = "cordova.fireDocumentEvent('"+evt+"'";
        if(data != null && !data.isEmpty()){
            eventString += ", "+data;
        }
        eventString += ");";
//        Log.d("JJDLTC JS TEST", eventString);
        this.actualWv.sendJavascript(eventString);
    }
}