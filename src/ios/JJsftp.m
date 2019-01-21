/*
 * The MIT License (MIT)
 * Copyright (c) 2015 Joel De La Torriente - jjdltc - https://github.com/jjdltc
 * See a full copy of license in the root folder of the project
 */

#import "JJsftp.h"
#import <Cordova/CDV.h>
#import <NMSSH/NMSSH.h>

@implementation JJsftp

- (void)download:(CDVInvokedUrlCommand*)command {
    
    [self set_hostData:[command argumentAtIndex:0]];
    [self set_actionArr:[command argumentAtIndex:1]];
    NSNumber *success = @1;
    NSString *message = @"Download is added to to list";
    
    if(self.hostData != nil && self.actionArr != nil && [self.actionArr count]>0){
        SEL downloadAtBackground = @selector(downloadAtBackground);
        [self performSelectorInBackground:downloadAtBackground withObject:nil];
    }
    else{
        message = @"Some parameters were missed - hostData or actionArr not found";
        success = @0;
    }
    
    NSDictionary *responseObj = @{
                                  @"success" : success,
                                  @"message" : message
                                  };
    
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:responseObj];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)upload:(CDVInvokedUrlCommand*)command {
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"Hi upload"];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)cancel:(CDVInvokedUrlCommand*)command {
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"Hi cancel"];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)downloadAtBackground{
    [self doConnection];
    
    NSInteger fileListSize = [self.actionArr count];
    NSInteger actualIdx = 0;
    __block long successAll = 1;
    
    for(NSDictionary *item in self.actionArr){
        actualIdx++;
        
        NSError *error = nil;
        NSString *local = [item valueForKey:@"local"];
        NSString *remote = [item valueForKey:@"remote"];
        
        local = [local stringByReplacingOccurrencesOfString:@"file://" withString:@""];
        remote = [@"" stringByAppendingFormat:@".%@", remote];
        
        NSInteger lastIndexSlash = [local rangeOfString:@"/" options:NSBackwardsSearch].location;
        NSString *folderLocal = [local substringWithRange:NSMakeRange(0, lastIndexSlash+1)];
        
        //        Boolean fileExist = [self.session.sftp fileExistsAtPath:remote];
        
        __block long downloaded = 0;
        __block BOOL isEventAlreadyTrigger = NO;
        
        NSData *file = [self.sftpInstance contentsAtPath:remote progress:^BOOL(NSUInteger got, NSUInteger totalBytes) {
            if(!isEventAlreadyTrigger){
                NSLog(@"SFTP Plugin - JJDLTC => Action Start, From: %@ <=> To: %@ -> Size: %lu", remote, local, (unsigned long)totalBytes);
                [self jsEvent:@"SFTPActionStart":[self dictionaryToJSONString:@{
                                                                                @"from" : remote,
                                                                                @"to" : local,
                                                                                @"size" : [NSNumber numberWithInteger:totalBytes]
                                                                                }]];
                
                isEventAlreadyTrigger = YES;
            }
            
            long tempDownloaded = got * 100 / totalBytes;
            
            if (tempDownloaded != downloaded) {
                downloaded = tempDownloaded;
            }
            
            NSLog(@"SFTP Plugin - JJDLTC => Action Progress: %ld %@", tempDownloaded, @"%");
            [self jsEvent:@"SFTPActionProgress":[self dictionaryToJSONString:@{
                                                                               @"percent" : [NSNumber numberWithInteger:tempDownloaded]
                                                                               }]];
            return TRUE;
        }];
        
        if(file != nil){
            BOOL doesPathExist = [[NSFileManager defaultManager] fileExistsAtPath:folderLocal];
            
            if(!doesPathExist){
                [[NSFileManager defaultManager] createDirectoryAtURL:[NSURL fileURLWithPath:folderLocal] withIntermediateDirectories:YES attributes:nil error:nil];
            }
            
            [file writeToFile:local options:NSDataWritingAtomic error:&error];
            if(error != nil){
                NSLog(@"SFTP Plugin - JJDLTC => Write returned error: %@", [error localizedDescription]);
                successAll = 0;
            }
        }
        else{
            successAll = 0;
        }
        
        NSLog(@"SFTP Plugin - JJDLTC => SFTPActionEnd");
        [self jsEvent:@"SFTPActionEnd":nil];
        NSLog(@"SFTP Plugin - JJDLTC => File progress: %ld of %ld Complete", (long)actualIdx, (long)fileListSize);
        [self jsEvent:@"SFTPActionListProgress":[self dictionaryToJSONString:@{
                                                                               @"progress" : [NSNumber numberWithInteger:actualIdx],
                                                                               @"total" : [NSNumber numberWithInteger:fileListSize]
                                                                               }]];
        
        if(actualIdx == fileListSize){
            NSString *successString = (successAll==1)
                ? @"were"
                : @"weren't";
            NSLog(@"SFTP Plugin - JJDLTC => SFTPActionListEnd All the files %@ reach it", successString);
            [self jsEvent:@"SFTPActionListEnd":[self dictionaryToJSONString:@{
                                                                              @"all" : [NSNumber numberWithLong:successAll]
                                                                              }]];
        }
    }
    
    [self closeConn];
}

- (BOOL)downloadAction{
    
    return false;
}

- (void)doConnection{
    self.session = [NMSSHSession
                    connectToHost:[self.hostData valueForKey:@"host"]
                    withUsername:[self.hostData valueForKey:@"user"]];
    
    if (self.session.isConnected) {
        [self.session authenticateByPassword:[self.hostData valueForKey:@"pswd"]];
        
        if (self.session.isAuthorized) {
            self.sftpInstance = [NMSFTP connectWithSession:self.session];
//            [self.session.sftp connect];
            NSLog(@"SFTP Plugin - JJDLTC => Connection Open.");
            [self jsEvent:@"SFTPActionConnected":nil];
        }
    }
}

- (void)closeConn{
    [self.sftpInstance disconnect];
//    [self.session disconnect];
    NSLog(@"SFTP Plugin - JJDLTC => Connection Close.");
    [self jsEvent:@"SFTPActionDisconnected":nil];
}

- (void)set_hostData:(NSDictionary*)data {
    NSString *user = [data valueForKey:@"user"];
    NSString *host = [data valueForKey:@"host"];
    NSString *pswd = [data valueForKey:@"pswd"];
    NSNumber *port = ([data valueForKey:@"port"] == nil)
    ?@22
    :[[[NSNumberFormatter alloc]init] numberFromString:[data valueForKey:@"port"]];
    NSDictionary *result = nil;
    
    if(user != nil || host != nil || pswd != nil){
        result = @{
                   @"user" : user,
                   @"host" : host,
                   @"pswd" : pswd,
                   @"port" : port
                   };
    }
    
    self.hostData = result;
}

- (void)set_actionArr:(NSArray*)actionArr {
    NSMutableArray *resultArr = [NSMutableArray array];
    Boolean isValid = true;
    
    if([actionArr count]>0){
        for(NSDictionary *actionItem in actionArr){
            NSString *local = [actionItem valueForKey:@"local"];
            NSString *remote = [actionItem valueForKey:@"remote"];
            NSNumber *create = ([actionItem valueForKey:@"create"] == @YES)
            ?@1
            :@0;
            
            if(local == nil || remote == nil){
                isValid = false;
                break;
            }
            
            if(isValid){
                NSDictionary *toAdd = @{
                                        @"local" : local,
                                        @"remote": remote,
                                        @"create": create
                                        };
                [resultArr addObject:toAdd];
            }
        }
    }
    
    if(isValid == false || [resultArr count]==0){
        resultArr = nil;
    }
    
    self.actionArr = resultArr;
}

- (void)jsEvent:(NSString*)event:(NSString*)data{
    NSString *eventStrig = [NSString stringWithFormat:@"cordova.fireDocumentEvent('%@'", event];
    // NSString *eventStrig = [NSString stringWithFormat:@"console.log('%@'", event];
    
    if(data != nil){
        eventStrig = [NSString stringWithFormat:@"%@,%@", eventStrig, data];
    }
    
    eventStrig = [eventStrig stringByAppendingString:@");"];
    
    [self.commandDelegate evalJs:eventStrig];
}

- (NSString*) dictionaryToJSONString:(NSDictionary*)toCast{
    NSError *error;
    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:toCast options:NSJSONWritingPrettyPrinted error:&error];
    if(!jsonData){
        return nil;
    }
    else{
        return [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
    }
}

@end
