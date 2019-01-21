SFTP Upload/Download Plugin
===

Cordova plugin to __Download__ and __Upload__ files from a __SFTP__ server.

This plugin is build on top of: 
  - [JSch](http://www.jcraft.com/jsch/ "In case you want to be curious") library for __Android__.
  - [NMSSH](http://cocoadocs.org/docsets/NMSSH/2.2.7/index.html "In case you want to be curious") framework for __iOS__.

__Contributors are welcome.__

Platforms supported

* Android
* iOS (Partial)

Installation
---

`cordova plugin add cordova-sftp-plugin`

Easy Use  
---  
    
There is a base Object `JJsftp`  expose in `window` that should be initialized with the `host`, `user` and `password` for the server
```
var mySftp = new JJsftp("host", "user", "password");
```
### Methods

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
    * `list` - Object array of files, has the follow attributes 
      * `remote` - Path/To/File/In/Server
      * `local` - Local/Path/To/Fil
    * `successCallback` - Function to call in plugin success
    * `errorCallback` - Function to call in plugin error    
* `cancel([successCallback, errorCallback])` Allow to cancel the asyn process that make the connection
    * `successCallback` - Function to call in plugin success
    * `errorCallback` - Function to call in plugin error

##### Important
Every callback (success or error) will response for the action of add the elements downloads/upload list, if you wanna know when a file or all the files are donwloaded, you should listen to the events fires over document.

### Events  
There are several events that fire during the process, all of it give one arg, the cordova event with few extra attributes  

* `SFTPActionConnected`
    * __Desc__: Fire on sftp channel connect
    * __Extra event attributes__: none
* `SFTPActionStart`
    * __Desc__: Fire when an action is about to start in a file
    * __Extra event attributes__: 
      * `from`- string : Path of the source
      * `to` - string : Path of the target
      * `size` - long : Size of the element
* `SFTPActionProgress`
    * __Desc__: fire to inform the % of down/up load in the ACTUAL file
    * __Extra event attributes__: 
      * `percent` - int : % of actual file progress
* `SFTPActionEnd`
    * __Desc__: fire whe the ACTUAL action over a file end
    * __Extra event attributes__: none
* `SFTPActionListProgress`
    * __Desc__: Fire after an action (Up/Down) is executed over a file, giving the actual file (index in list) and total files in list
    * __Extra event attributes__: 
      * `progress` - int : Actual element index in list
      * `total` - int : Total count of elements in list
* `SFTPActionListEnd`
    * __Desc__: Fire at end of action list to inform the # of file rech it
    * __Extra event attributes__: 
      * `all` - boolean : true if all action were made it
* `SFTPActionDisconnected`
    * __Desc__: Fire on sftp channel disconnect
    * __Extra event attributes__: none
* `SFTPActionCancell`
    * __Desc__: Fire on cancel async action
    * __Extra event attributes__: none

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

#### Know Issues
- `upload` does not work on __iOS__ (Yet)
- `cancel` does not work on __iOS__ (Yet), therefore `SFTPActionCancell` is never trigger.

There is a big TODO list, but in resume  

* Bug Resolve =>
  * No Connection Break The App (Should The plugin warning the user or just don't do anything)
  * ~~Only receive the absolute platfomr path (Without file://), should be replace in plugin don't force the user to replace it~~
  * Events just send a simple string object, need to be parse by user, should be the as many as need witouth the need to parse it
* Write a better documentation
* Propose new utils features (I think maybe in a fetch directory will be util) without lose the perspective of the plugin (Upload - Download via SFTP)
* _Add iOS Support (Be Patient)_ (Partial done, `download` now available)
* Improve Uploading and Downloading feature
