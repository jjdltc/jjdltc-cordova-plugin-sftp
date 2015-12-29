/*
 * The MIT License (MIT)
 * Copyright (c) 2015 Joel De La Torriente - jjdltc - http://www.jjdltc.com/
 * See a full copy of license in the root folder of the project
 */
package com.jjdltc.cordova.plugin.sftp;

import java.io.File;

import org.json.JSONArray;
import org.json.JSONObject;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.cordova.CordovaWebView;

public class asyncSFTPAction extends AsyncTask<Void, Integer, Boolean> {

	private JSch jsch               = null;
	private Session session         = null;
	private ChannelSftp sftpChannel = null;
	
	private JSONObject hostData     = null;
	private JSONArray actionArr     = null;
	private CordovaWebView actualWv = null;
	private String action			= "";
	private int fileListSize        = 0;
	
	public asyncSFTPAction(JSONObject hostData, JSONArray actionArr, String action, CordovaWebView actualWv) {
		this.hostData   = hostData;
		this.actionArr  = actionArr;
		this.actualWv   = actualWv;
		this.action 	= action;
	}
	
	@Override
	protected Boolean doInBackground(Void... params) {
		boolean result = true;
		try {
			this.doConnection(this.hostData);
			this.actionExecution(this.actionArr);
			this.closeConn();
		} catch (Exception e) { /*  JSchException | SftpException e */
			e.printStackTrace();
			Log.e("SFTP Plugin - JJDLTC", "There was a problem in the async execution" );
			this.closeConn();
			result = false;
		}
		return result;
	}
	
	@Override
	protected void onProgressUpdate(Integer... progress) {
		super.onProgressUpdate(progress);
		this.jsEvent("SFTPActionListProgress", "{progress:'"+progress[0]+"', total:'"+this.fileListSize+"'}");
		Log.d("SFTP Plugin - JJDLTC", "File progress: "+progress[0]+" of "+this.fileListSize+" Complete" );
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		this.jsEvent("SFTPActionListEnd", "{all:'"+result+"'}");
		Log.d("SFTP Plugin - JJDLTC", "All the files "+((result)?"were":"weren't")+" reach it" );
	}
	
	@Override
	protected void onCancelled() {
		super.onCancelled();
		this.closeConn();
		this.jsEvent("SFTPActionCancell", null);
		Log.d("SFTP Plugin - JJDLTC", "Action cancelled by the user" );
	}
	
	@SuppressWarnings("static-access")
	private void doConnection(JSONObject hostData) throws JSchException{
		this.jsch = new JSch();
		this.jsch.setConfig("StrictHostKeyChecking", "no");
		
		this.session = jsch.getSession(hostData.optString("user"), hostData.optString("host"), hostData.optInt("port"));
		this.session.setPassword(hostData.optString("pswd"));
		this.session.connect(); 
		this.sftpChannel = (ChannelSftp) session.openChannel("sftp");
		this.sftpChannel.connect();
		Log.d("SFTP Plugin - JJDLTC", "Connection Open.");
		this.jsEvent("SFTPActionConnected", null);
	}

	private void actionExecution(JSONArray actions) throws SftpException{
		this.fileListSize = actions.length();
		for (int i = 0; i < actions.length(); i++) {
			JSONObject item = (JSONObject)actions.opt(i);
			boolean isDownload				= (action == "download")?true:false;
			boolean createIfNeedIt			= item.optBoolean("create");
			boolean isValidLocalPath		= this.checkLocalPath(item.optString("local"), isDownload, createIfNeedIt);
			if(isValidLocalPath){
				if(isDownload){
					this.sftpChannel.get(item.optString("remote"), item.optString("local"), new progressMonitor(this.actualWv));            	
				}
				else{
					this.sftpChannel.put(item.optString("local"), item.optString("remote"), new progressMonitor(this.actualWv), ChannelSftp.OVERWRITE);
				}
			}
			this.publishProgress(i+1);
		}
	}

	private boolean checkLocalPath(String path, boolean isDownload, boolean create){
		String pathToSeek 	= (isDownload)?path.substring(0, path.lastIndexOf("/")+1):path;
		File seekedPath		= new File(pathToSeek);
		boolean Exists		= seekedPath.exists();
		if(!Exists && create && isDownload){
			seekedPath.mkdirs(); 
			Exists			= seekedPath.exists();
		}
		return Exists;
	}

	private void closeConn(){
		this.sftpChannel.exit();
		this.session.disconnect();
		this.jsEvent("SFTPActionDisconnected", null);
		Log.d("SFTP Plugin - JJDLTC", "Connection Close.");
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
