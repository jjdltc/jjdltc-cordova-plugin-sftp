/*
 * The MIT License (MIT)
 * Copyright (c) 2015 Joel De La Torriente - jjdltc - http://www.jjdltc.com/
 * See a full copy of license in the root folder of the project
 */

var argscheck   = require('cordova/argscheck'),
	exec        = require('cordova/exec'),
	cordova     = require('cordova');

/**
 * @TODO Desc
 * 
 * @constructor
 * @param {String} Server url
 * @param {String} Username
 * @param {String} Password that match with the user name credentials
 */
function JJsftp(host, usr, pwr) {
	argscheck.checkArgs('sss', 'JJsftp', arguments);
	this.host       = host || false;
	this.usr        = usr || false;
	this.pwr        = pwr || false;
	this.hostInfo   = {
		  host      : this.host
		, user      : this.usr
		, pswd      : this.pwr
	};
}

/**
 * Download and Upload feature, that allow interact with the sftp server
 *
 * @param {String} Path to the file in the server (Remote)
 * @param {String} Path to the file in the device (Local)
 * @param {Boolean} Flag to allow the creation of folder path if not exist (Default to false) 
 * @param {Function} successCallback The function to call when the heading data is available
 * @param {Function} errorCallback The function to call when there is an error getting the heading data. (OPTIONAL)
 */
JJsftp.prototype.download = function(serverPath, localPath, create, successCallback, errorCallback) {
	argscheck.checkArgs('ss*FF', 'JJsftp.download', arguments);
	var actionInfo      = [{
		  remote        : serverPath
		, local         : localPath
		, create        : create || false
	}]
	exec(successCallback, errorCallback, "JJsftp", "download", [this.hostInfo, actionInfo]);
};

JJsftp.prototype.upload = function(serverPath, localPath, successCallback, errorCallback) {
	argscheck.checkArgs('ssFF', 'JJsftp.upload', arguments);
	var actionInfo      = [{
		  remote        : serverPath
		, local         : localPath
	}]
	exec(successCallback, errorCallback, "JJsftp", "upload", [this.hostInfo, actionInfo]);
};

/**
 * Download and Upload feature with a list of file paths, that allow interact with the sftp server
 *
 * @param {Array} List of Objects with the path of the files, has the follow structure 
 * [{
 *   remote : Path/To/Server/File,
 *   local  : Path/Where/Save/File
 *   create	: true || false (Only for download for now)
 * },{...},...]
 * @param {Function} successCallback The function to call when the heading data is available
 * @param {Function} errorCallback The function to call when there is an error getting the heading data. (OPTIONAL)
 */
JJsftp.prototype.downloadList = function(list, successCallback, errorCallback) {
	argscheck.checkArgs('aFF', 'JJsftp.downloadList', arguments);
	exec(successCallback, errorCallback, "JJsftp", "download", [this.hostInfo, list]);
};

JJsftp.prototype.uploadList = function(list, successCallback, errorCallback) {
	argscheck.checkArgs('aFF', 'JJsftp.uploadList', arguments);
	exec(successCallback, errorCallback, "JJsftp", "upload", [this.hostInfo, list]);
};


/**
 * 
 * Cancel the actual process with the sftp if exists.
 *
 * @param {Function} successCallback The function to call when the heading data is available
 * @param {Function} errorCallback The function to call when there is an error getting the heading data. (OPTIONAL)
 */
JJsftp.prototype.cancel = function(successCallback, errorCallback) {
	argscheck.checkArgs('FF', 'JJsftp.cancel', arguments);
	exec(successCallback, errorCallback, "JJsftp", "cancel", [this.hostInfo]);
};

module.exports = JJsftp;
