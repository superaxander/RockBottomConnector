package alexanders.mods.rbb.connectors;

import alexanders.mods.rbb.RBB;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.event.EventResult;
import de.ellpeck.rockbottom.api.event.IEventListener;
import de.ellpeck.rockbottom.api.event.impl.ChatMessageEvent;
import de.ellpeck.rockbottom.api.net.chat.IChatLog;
import de.ellpeck.rockbottom.api.net.chat.ICommandSender;
import de.ellpeck.rockbottom.api.net.chat.component.ChatComponent;
import de.ellpeck.rockbottom.api.util.Util;
import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.PircBot;
import org.newdawn.slick.Color;

import java.io.IOException;
import java.util.UUID;

import static alexanders.mods.rbb.connectors.DiscordChatter.escape;

public class IRCChatter extends PircBot implements ICommandSender, IEventListener<ChatMessageEvent> {
    public static IRCChatter instance;

    public IRCChatter() {
        super();
        instance = this;
        this.setVerbose(true);
        this.setName(RBB.irc_username);
        this.setLogin(RBB.irc_username);
        try {
            if (RBB.irc_password.isEmpty())
                this.connect(RBB.irc_server, RBB.irc_port);
            else
                this.connect(RBB.irc_server, RBB.irc_port, RBB.irc_password);
            this.joinChannel(RBB.irc_channel);
        } catch (IOException | IrcException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public EventResult listen(EventResult result, ChatMessageEvent event) {
        if (event.sender != this)
            this.sendMessage(RBB.irc_channel, event.sender.getName() + ": " + event.message);
        return result;
    }

    @Override
    public int getCommandLevel() {
        return 0;
    }

    @Override
    public UUID getUniqueId() {
        return UUID.randomUUID();
    }

    @Override
    public String getChatColorFormat() {
        return Util.colorToFormattingCode(Color.yellow);
    }

    @Override
    public void sendMessageTo(IChatLog chat, ChatComponent message) {

    }

    @Override
    protected void onMessage(String channel, String sender, String login, String hostname, String message) {
        if (channel.equals(RBB.irc_channel) && !sender.equals(getName()))
            RockBottomAPI.getGame().getChatLog().sendCommandSenderMessage(escape(sender) + ": " + escape(message), this);
    }
}
