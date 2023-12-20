package com.rhine1203.software.ipable;

import java.io.InputStream;
import org.apache.commons.io.IOUtils;

public class LoadProperties {
    String[] content = new String[4];
    public LoadProperties(){
        try{
            InputStream inputStream = getClass().getResourceAsStream("/app.properties");
            content = IOUtils.readLines(inputStream, "UTF-8").toArray(content);
        }catch(Exception e){
            System.out.println("Exception occured while loading properties.");
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

        try {
            channelID = Long.parseLong(content[1].split("channelID=")[1]);
        } catch (Exception e) {
            System.out.println("Exception occured while parsing Channel ID.");
            e.printStackTrace();
        }
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
