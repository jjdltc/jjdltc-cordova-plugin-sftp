/*
 * The MIT License (MIT)
 * Copyright (c) 2015 Joel De La Torriente - jjdltc - https://github.com/jjdltc
 * See a full copy of license in the root folder of the project
 */

#import <Cordova/CDVPlugin.h>
#import <NMSSH/NMSSH.h>

@interface JJsftp : CDVPlugin

@property NMSSHSession *session;
@property NSDictionary *hostData;
@property NSMutableArray *actionArr;
@property NMSFTP *sftpInstance;

- (void)download:(CDVInvokedUrlCommand*)command;

- (void)upload:(CDVInvokedUrlCommand*)command;

- (void)cancel:(CDVInvokedUrlCommand*)command;

@end
