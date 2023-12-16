package com.rhine1203.software.ngrokiptodiscord;

import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;

public class NgrokIpToDiscord {
    static GatewayDiscordClient client;
    static String endKey = null;
    static String failMessage;
    static Message sentMessage;
    public static void main( String[] args )
    {
        LoadProperties loader = new LoadProperties();
        endKey = loader.loadEndKey();
        failMessage = loader.loadFailMessage();
        
        client = DiscordClientBuilder.create(loader.loadToken()).build().login().block();
        System.out.println("Client connected");
        Snowflake channelId = Snowflake.of(loader.loadChannel());

        //On startup
        client.getEventDispatcher().on(ReadyEvent.class)
        .subscribe(event -> {
            SendMessage(channelId, ("IP:\n" + "```" + GetNgrokIp().get() + "```"));
        });

        client.getEventDispatcher().on(MessageCreateEvent.class).subscribe(event ->{
            Message message = event.getMessage();
            RespondByMessage(message);
        });

        client.onDisconnect()
        .block();
        
    }
    private static void RespondByMessage(Message message){
        System.out.println(message.getContent());

        if(message.getAuthor().map(user -> user.isBot()).orElse(false)){
            if(message.getContent().contains(endKey)){
                System.out.println("Logout");
                DeleteMessage(sentMessage.getChannelId(), sentMessage.getId());
                client.logout().subscribe();
            }
            return;
        }

        switch(message.getContent()){
            case "ip":
            message.getChannel().flatMap(channel -> channel.createMessage("IP:\n" + "```" + GetNgrokIp().get() + "```")).subscribe();
            break;
            default:
            return;
        }
    }
    private static void SendMessage(Snowflake channelID, String message){
        client.getChannelById(channelID)
        .ofType(MessageChannel.class)
        .flatMap(channel -> channel.createMessage(message))
        .subscribe(m -> sentMessage = m);
    }
    private static void DeleteMessage(Snowflake channelID, Snowflake messageID){
        if (messageID != null){
            client.getChannelById(channelID)
            .ofType(MessageChannel.class)
            .flatMap(channel -> channel.getMessageById(messageID))
            .flatMap(Message::delete)
            .subscribe();
            System.out.println("Deleted message: " + messageID.asString());
        }else{
            System.out.println("Message IP is null. Deletion failed.");
        }
    }
    private static Optional<String> GetNgrokIp(){
        String localUrl = "http://localhost:4040/api/tunnels";
        String ngrokIp = null;
        

        try{
            URL url = new URL(localUrl);
            String inputLine;
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            String ngrokJson = content.toString();
            ngrokIp = ngrokJson.split("\"public_url\":\"tcp://")[1].split("proto")[0].split("\",\"")[0];
            
        }catch(IOException e){
            ngrokIp = failMessage;
        }

        return Optional.ofNullable(ngrokIp);
    }
}
