/*
 * The MIT License (MIT)
 * Copyright (c) 2015 Joel De La Torriente - jjdltc - http://www.jjdltc.com/
 * See a full copy of license in the root folder of the project
 */

var argscheck   = require('cordova/argscheck'),
    utils       = require('cordova/utils'),
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
 * @TODO Desc
 *
 * @param {String} Path to the file in the server (Remote)
 * @param {String} Path to the file in the device (Local)
 * @param {Function} successCallback The function to call when the heading data is available
 * @param {Function} errorCallback The function to call when there is an error getting the heading data. (OPTIONAL)
 */
JJsftp.prototype.download = function(serverPath, localPath, successCallback, errorCallback) {
    argscheck.checkArgs('ssFF', 'JJsftp.download', arguments);
    var actionInfo      = [{
          remote        : serverPath
        , local         : localPath
    }]
    exec(successCallback, errorCallback, "JJsftp", "download", [this.hostInfo, actionInfo]);
};

/**
 * @TODO Desc
 *
 * @param {Array} List of Objects with the path of the files, has the follow structure 
 * [{
 *   remote : Path/To/Server/File,
 *   local  : Path/Where/Save/File
 * },{...},...]
 * @param {Function} successCallback The function to call when the heading data is available
 * @param {Function} errorCallback The function to call when there is an error getting the heading data. (OPTIONAL)
 */
JJsftp.prototype.downloadList = function(list, successCallback, errorCallback) {
    argscheck.checkArgs('aFF', 'JJsftp.downloadList', arguments);
    exec(successCallback, errorCallback, "JJsftp", "download", [this.hostInfo, list]);
};

/**
 * @TODO Desc
 *
 * @param {Array} List of Objects with the path of the files, has the follow structure 
 * [{
 *   remote : Path/To/Server/File,
 *   local  : Path/Where/Save/File
 * },{...},...]
 * @param {Function} successCallback The function to call when the heading data is available
 * @param {Function} errorCallback The function to call when there is an error getting the heading data. (OPTIONAL)
 */
JJsftp.prototype.cancel = function(successCallback, errorCallback) {
    argscheck.checkArgs('FF', 'JJsftp.cancel', arguments);
    exec(successCallback, errorCallback, "JJsftp", "cancel", [this.hostInfo]);
};

module.exports = JJsftp;
