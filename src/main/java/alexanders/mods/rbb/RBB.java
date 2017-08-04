package alexanders.mods.rbb;

import alexanders.mods.rbb.connectors.DiscordChatter;
import alexanders.mods.rbb.connectors.IRCChatter;
import de.ellpeck.rockbottom.api.IApiHandler;
import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.event.IEventHandler;
import de.ellpeck.rockbottom.api.event.impl.ChatMessageEvent;
import de.ellpeck.rockbottom.api.mod.IMod;

import java.io.*;
import java.util.Properties;

import static de.ellpeck.rockbottom.api.RockBottomAPI.createRes;
import static de.ellpeck.rockbottom.api.RockBottomAPI.getGame;

public class RBB implements IMod {

    private static final Properties DEFAULT_SETTINGS = new Properties() {
        
        {
            setProperty("discord_channel", "PLEASE_SPECIFY");
            setProperty("irc_channel", "PLEASE_SPECIFY");
            setProperty("irc_server", "PLEASE_SPECIFY");
            setProperty("discord", "false");
            setProperty("discord_token", "PLEASE_SPECIFY");
            setProperty("irc", "false");
            setProperty("irc_port", "6667");
            setProperty("irc_username", "PLEASE_SPECIFY");
            setProperty("irc_password", "PLEASE_SPECIFY");
        }
    };

    
    public static String discord_channel;
    public static String discord_token;
    public static String irc_channel;
    public static String irc_server;
    public static String irc_username;
    public static String irc_password;
    public static boolean discord;
    public static boolean irc;
    public static int irc_port;
    //public static String invite = "https://discordapp.com/oauth2/authorize?client_id=342766811831599106&scope=bot&permissions=3072";

    @Override
    public String getDisplayName() {
        return "RockBottomConnector";
    }

    @Override
    public String getId() {
        return "rbb";
    }

    @Override
    public String getVersion() {
        return "@VERSION@";
    }

    @Override
    public String getResourceLocation() {
        return "/assets/" + getId();
    }

    @Override
    public String getDescription() {
        return getGame().getAssetManager().localize(createRes(this, "desc.mod"));
    }

    @Override
    public void init(IGameInstance game, IApiHandler apiHandler, IEventHandler eventHandler) {
        Properties properties = DEFAULT_SETTINGS;
        try {
            properties.load(new FileInputStream("./rockbottom/rbb.properties"));
        } catch (IOException e) {
            try {
                File file = new File("./rockbottom/rbb.properties");
                file.createNewFile();
                properties.store(new FileOutputStream(file), "");
            } catch (IOException e1) {
                throw new UncheckedIOException(e1);
            }
        }
        discord_channel = properties.getProperty("discord_channel");
        discord_token = properties.getProperty("discord_token");
        irc_channel = properties.getProperty("irc_channel");
        irc_server = properties.getProperty("irc_server");
        irc_port = Integer.parseInt(properties.getProperty("irc_port"));
        irc_username = properties.getProperty("irc_username");
        irc_password = properties.getProperty("irc_password");
        discord = Boolean.valueOf(properties.getProperty("discord"));
        irc = Boolean.valueOf(properties.getProperty("irc"));
        if(discord) {
            Thread t = new Thread(DiscordChatter::new);
            t.setDaemon(true);
            t.start();
            while (DiscordChatter.instance == null)
                Thread.yield();
            eventHandler.registerListener(ChatMessageEvent.class, DiscordChatter.instance);
        }
        if(irc){
            Thread t = new Thread(IRCChatter::new);
            t.setDaemon(true);
            t.start();
            while(IRCChatter.instance ==null)
                Thread.yield();
            eventHandler.registerListener(ChatMessageEvent.class, IRCChatter.instance);
        }
    }
}
