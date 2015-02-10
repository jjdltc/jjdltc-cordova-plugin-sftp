/*
 *
 * License TODO
 *
*/

var argscheck   = require('cordova/argscheck'),
    utils       = require('cordova/utils'),
    exec        = require('cordova/exec'),
    cordova     = require('cordova');

/**
 * Desc
 * 
 * @constructor
 * @param {String} Server url
 * @param {String} Username
 * @param {String} Password that match with the user name credentials
 */
function SFTP(host, usr, pwr) {
    argscheck.checkArgs('sSS', 'SFTP', arguments);
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
 * Desc
 *
 * @param {String} Path to the file in the server (Remote)
 * @param {String} Path to the file in the device (Local)
 * @param {Function} successCallback The function to call when the heading data is available
 * @param {Function} errorCallback The function to call when there is an error getting the heading data. (OPTIONAL)
 */
SFTP.prototype.download = function(serverPath, localPath, successCallback, errorCallback) {
    argscheck.checkArgs('sSFF', 'SFTP.download', arguments);
    var actionInfo      = [{
          remote        : serverPath
        , local         : localPath
    }]
    exec(successCallback, errorCallback, "JJsftp", "download", [this.hostInfo, actionInfo]);
};

/**
 * Desc
 *
 * @param {Array} List of Objects with the path of the files, has the follow structure 
 * [{
 *   remote : Path/To/Server/File,
 *   local  : Path/Where/Save/File
 * },{...},...]
 * @param {Function} successCallback The function to call when the heading data is available
 * @param {Function} errorCallback The function to call when there is an error getting the heading data. (OPTIONAL)
 */
SFTP.prototype.downloadList = function(list, successCallback, errorCallback) {
    argscheck.checkArgs('aFF', 'SFTP.downloadList', arguments);
    exec(successCallback, errorCallback, "JJsftp", "download", [this.hostInfo, list]);
};

/**
 * Desc
 *
 * @param {Array} List of Objects with the path of the files, has the follow structure 
 * [{
 *   remote : Path/To/Server/File,
 *   local  : Path/Where/Save/File
 * },{...},...]
 * @param {Function} successCallback The function to call when the heading data is available
 * @param {Function} errorCallback The function to call when there is an error getting the heading data. (OPTIONAL)
 */
SFTP.prototype.cancel = function(successCallback, errorCallback) {
    argscheck.checkArgs('fF', 'SFTP.cancel', arguments);
    exec(successCallback, errorCallback, "JJsftp", "cancel", [this.hostInfo]);
};

module.exports = SFTP;

/*
 * Events
 * => SFTPActionListProgress    - {progress:'int / processed action', total:'int / action list count'} - fire on file notice to action execute
 * => SFTPActionListEnd         - {all:'boolean - true if all action were made it'} - fire at end of action list to inform the # of file rech it
 * => SFTPActionCancell         - null - fire un cancell async
 * => SFTPActionConnected       - null - fire un sftp channel connect
 * => SFTPActionDisconnected    - null - fire un sftp channel disconnect
 * ---
 * => SFTPActionStart           - {from:'string',to:'string',size:'long'} - fire when an action is about to start in a file
 * => SFTPActionProgress        - {percent:'int'} - fire to inform the % of down/up load in the ACTUAL file
 * => SFTPActionEnd             - null - fire whe the actual action over a file end
 */