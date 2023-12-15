package com.rhine1203.software.ngrokiptodiscord;

import java.io.InputStream;
import java.util.Scanner;

public class LoadProperties {
    String[] content = new String[4];
    public LoadProperties(){
        try{
            InputStream inputStream = getClass().getResourceAsStream("/app.properties");
            Scanner scanner = new Scanner(inputStream).useDelimiter("\n");
            for (int i = 0; i < content.length; i++) {
                content[i] = scanner.next();
            }
            scanner.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public String loadToken(){
        String token = null;

        token = content[0].split("token=")[1];
        System.out.println("Loaded token: " + token);
        return token;
    }
    public Long loadChannel(){
        long channelID = 0;

        channelID = Long.parseLong(content[1].split("channelID=")[1]);
        System.out.println("Loaded channel ID: " + channelID);        
        return channelID;
    }
    public String loadEndKey(){
        String endKey = null;

        endKey = content[2].split("endKey=")[1];
        System.out.println("Loaded endKey: " + endKey);
        return endKey;
    }
    public String loadFailMessage(){
        String failMessage = null;
        failMessage = content[3].split("failMessage=")[1];
        System.out.println("Loaded fail message: " + failMessage);
        return failMessage;
    }
}
