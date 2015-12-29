/*
 * The MIT License (MIT)
 * Copyright (c) 2015 Joel De La Torriente - jjdltc - http://www.jjdltc.com/
 * See a full copy of license in the root folder of the project
 */
package com.jjdltc.cordova.plugin.sftp;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

public class JJsftp extends CordovaPlugin {
    
    private AsyncTask<Void, Integer, Boolean> staticAsync = null;
    private enum ACTIONS {
          download
        , upload
        , cancel
    };
    
    /**
     * Executes the request and returns PluginResult.
     *
     * @param action            The action to execute.
     * @param args              JSONArry of arguments for the plugin.
     * @param callbackContext   The callback id used when calling back into JavaScript.
     * @return                  True if the action was valid, false if not.
     */
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        boolean result = true;
        
        JSONObject hostData = this.setHostData(args);
        JSONArray actionArr = this.setActionArr(args);
        
        if((hostData==null || action == null) && action!="cancel"){
            this.processResponse(callbackContext, false, "Some parameters were missed - hostData or actionArr not found-");
            return false;
        }
        
        switch (ACTIONS.valueOf(action)) {
            case download:
                this.download(hostData, actionArr);
                this.processResponse(callbackContext, true, "Download is added to to list");
            break;
            case upload:
            	this.upload(hostData, actionArr);
                this.processResponse(callbackContext, true, "upload is added to to list");
            break;
            case cancel:
                boolean cancellSuccess = this.cancelStaticAsync();
                String msg = (cancellSuccess)?"Cancellation request sent":"Cancellation request sent but we are unable to execute (may not be such a process or is already cancelled)";
                this.processResponse(callbackContext, cancellSuccess, msg);
            break;
            default:
                this.processResponse(callbackContext, false, "Some parameters were missed - action not found -");
                result = false;
            break;
        }
        
        return result;
    }
    
    /**
     * 
     * @param ctx               The plugin CallbackContext
     * @param success           Boolean that define if the JS plugin should fire the success or error function
     * @param msg               The String msg to by sended
     * @throws JSONException
     */
    private void processResponse(CallbackContext ctx, boolean success, String msg) throws JSONException{
        JSONObject response = new JSONObject();
        response.put("success", success);
        response.put("message", msg);
        if(success){
            ctx.success(response);
        }
        else{
            ctx.error(response);
        }
    }
    
    /**
     * Use an custom AsyncTask 'asyncSFTPAction' to execute the download
     * 
     * @param hostData          JSONObject with the host data to connect (processed by 'setHostData' function)
     * @param actionArr         JSONArray with the action list to execute (processed by 'setActionArr' function)
     */
    private void download(JSONObject hostData, JSONArray actionArr){
        this.staticAsync = new asyncSFTPAction(hostData, actionArr, "download", this.webView);
        this.staticAsync.execute();
    }
    
    private void upload(JSONObject hostData, JSONArray actionArr){
        this.staticAsync = new asyncSFTPAction(hostData, actionArr, "upload", this.webView);
        this.staticAsync.execute();
    }
    
    /**
     * Validate if the options sent by user are not null or empty and also if accomplish the base structure
     * 
     * @param arguments         The arguments passed by user with the JSONObject that has the host options
     * @return                  A valid 'hostData' JSONObject with its inner host options to connect
     * @throws JSONException
     */
    private JSONObject setHostData(JSONArray arguments) throws JSONException{
        JSONObject hostData = arguments.optJSONObject(0);
        boolean validArgs   = true;
        String[] keys       = new String[]{
              "host"
            , "user"
            , "pswd"
        };

        if(hostData==null){
            return null;            
        }
        if(hostData.opt("port")==null){
            hostData.put("port", 22);
        }
        
        for (int i = 0; i < keys.length; i++) {
            if(hostData.opt(keys[i])==null){
                validArgs = false;
                break;
            }
        }

        return (validArgs)?hostData:null;
    }
    
    /**
     * Validate if the options sent by user are not null or empty and also if accomplish the base structure
     * 
     * @param arguments         The arguments passed by user with the JSONArray of JSONObject with the local and remote path of the files
     * @return                  A valid 'actionArr' JSONArray with its inner JSONObject paths
     */
    private JSONArray setActionArr(JSONArray arguments){
        JSONArray actionArr = arguments.optJSONArray(1);
        boolean validArr    = true;
        
        if(actionArr==null){
            return null;            
        }
        
        for (int i = 0; i < actionArr.length(); i++) {
            JSONObject tempActionObj = actionArr.optJSONObject(i);
            if(tempActionObj==null){
                validArr = false;
            }
            else{
                validArr = (tempActionObj.opt("remote")==null || tempActionObj.opt("local")==null)?false:validArr;
            }
            if(!validArr){
                break;
            }
        }
        
        return (validArr)?actionArr:null;
    }
    
    private boolean cancelStaticAsync(){
        return (this.staticAsync!=null)?this.staticAsync.cancel(true):false;
    }
}
