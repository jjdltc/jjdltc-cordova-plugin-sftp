SFTP Plugin
===

Plugin to Download and Upload files from a SFTP server, easy to add and use.  
Contributors are welcome.

This plugin is build on top of [JSch](http://www.jcraft.com/jsch/ "In case you want to be curious") library.

Installation
---

`cordova plugin add cordova-sftp-plugin`

Easy Use  
---  
  
  
There is a base Object `JJsftp` that should be initialized with the `host`, `user` and `password`
```
var mySftp = new JJsftp("host", "user", "password");
```
###Methods###

* `download(serverPath, localPath [, createIfNotExists, successCallback, errorCallback])` Allow to download a single file.
    * `serverPath` - Path/To/File/In/Server
    * `localPath` - Path/To/File/In/Device
    * `createIfNotExists` - Create the folder path if not exist
    * `successCallback` - Function to call in plugin success
    * `errorCallback` - Function to call in plugin error
* `downloadList(list [, successCallback, errorCallback])` Allow to download a list of files
    * `list` - Object array of files, has the follow attributes:
        * `remote` - Path/To/File/In/Server
        * `local` - Local/Path/To/File
        * [`create`] - default to: `false` - Create the folder path if not exist
    * `successCallback` - Function to call in plugin success
    * `errorCallback` - Function to call in plugin error
* `upload(serverPath, localPath [, successCallback, errorCallback])` Allow to download a single file.
    * `serverPath` - Path/To/File/In/Server
    * `localPath` - Path/To/File/In/Device
    * `successCallback` - Function to call in plugin success
    * `errorCallback` - Function to call in plugin error
* `uploadList(list [, successCallback, errorCallback])` Allow to download a list of files
    * `list` - Object array of files, with `remote` and `local` attribute
    * `successCallback` - Function to call in plugin success
    * `errorCallback` - Function to call in plugin error    
* `cancel([successCallback, errorCallback])` Allow to cancel the asyn process that make the connection
    * `successCallback` - Function to call in plugin success
    * `errorCallback` - Function to call in plugin error

###Events  
There are several events that fire during the process, all of it give one arg, the cordova event with few extra attributes  

* `SFTPActionConnected`
    * __Extra event attributes__: none
    * __Desc__: Fire on sftp channel connect
* `SFTPActionStart`
    * __Extra event attributes__: `from`(string),`to`(string), `size`(long)
    * __Desc__: Fire when an action is about to start in a file
* `SFTPActionProgress`
    * __Extra event attributes__: `percent`(int)
    * __Desc__: fire to inform the % of down/up load in the ACTUAL file
* `SFTPActionEnd`
    * __Extra event attributes__: none
    * __Desc__: fire whe the ACTUAL action over a file end
* `SFTPActionListProgress`
    * __Extra event attributes__: `progress`(int) `total`(int)
    * __Desc__: Fire after an action (Up/Down) is executed over a file, giving the actual file (index in list) and total files in list
* `SFTPActionListEnd`
    * __Extra event attributes__: `all`(boolean) true if all action were made it
    * __Desc__: Fire at end of action list to inform the # of file rech it
* `SFTPActionDisconnected`
    * __Extra event attributes__: none
    * __Desc__: Fire on sftp channel disconnect
* `SFTPActionCancell`
    * __Extra event attributes__: none
    * __Desc__: Fire on cancel async action

### Use Example
To Download
```
var sftp        = new JJsftp("host", "user", "password"),
    localPath   = "String/Path/To/Place/The/Download"
    filelist    = [{
          remote    : "/Path/To/Remote/File.*"
        , local     : localPath+"file.*"
    },{
          remote    : "/Path/To/Another/Remote/File.*"
        , local     : localPath+"anotherFile.*"
    }];

    sftp.downloadList(filelist, function(data){
        /* Wow everything goes good, but just in case verify data.success */
    }, function(error){
        /* Wow something goes wrong, check the error.message */       
    });
```
To Upload is the same but calling `upload` or `uploadList` and by default will override a file if exists in server

Of course if you want, you could add any of the JJsftp events to document and listen the progress of the download by example

There is a big TODO list, but in resume  

* Bug Resolve =>
      * No Connection Break The App (Should The plugin warning the user or just don't do anything)
      * Only receive the absolute platfomr path (Without file://), should be replace in plugin don't force the user to replace it
      * Events just send a simple string object, need to be parse by user, should be the as many as need witouth the need to parse it
* Write a better documentation
* Propose new utils features (I think maybe in a fetch directory will be util) without lose the perspective of the plugin (Upload - Download via SFTP)
* Add iOS Support (Be Patient)
* Improve Uploading and Downloading feature
