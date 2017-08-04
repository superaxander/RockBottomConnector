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
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.EventListener;
import org.newdawn.slick.Color;

import javax.security.auth.login.LoginException;
import java.util.UUID;


public class DiscordChatter implements ICommandSender, EventListener, IEventListener<ChatMessageEvent> {
    public static DiscordChatter instance;
    private final JDA jda;

    public DiscordChatter() {
        instance = this;
        try {
            jda = new JDABuilder(AccountType.BOT).setToken(RBB.discord_token).buildBlocking();
            jda.addEventListener(this);
        } catch (LoginException | InterruptedException | RateLimitedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        new DiscordChatter();
    }

    @Override
    public int getCommandLevel() {
        return 0;
    }

    @Override
    public String getName() {
        return "Discord";
    }

    @Override
    public UUID getUniqueId() {
        return UUID.randomUUID();
    }

    @Override
    public String getChatColorFormat() {
        return Util.colorToFormattingCode(Color.blue);
    }

    @Override
    public void sendMessageTo(IChatLog chat, ChatComponent message) {

    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof MessageReceivedEvent) {
            if (((MessageReceivedEvent) event).getTextChannel().getId().equals(RBB.discord_channel)) {
                Message message = ((MessageReceivedEvent) event).getMessage();
                if (!message.getAuthor().isBot())
                    RockBottomAPI.getGame().getChatLog().sendCommandSenderMessage(escape(message.getAuthor().getName()) + ": " + escape(message.getContent()), this);
            }
        }
    }

    public static String escape(String content) {
        int i;
        for (i = 0; i < content.length(); i++) { // Remove / to disallow commands
            int index = content.indexOf('/', i);
            if (index != i)
                break;
        }
        return content.substring(i);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public EventResult listen(EventResult result, ChatMessageEvent event) {
        if (event.sender != this)
            this.jda.getTextChannelById(RBB.discord_channel).sendMessage(event.sender.getName() + ": " + event.message).complete();
        return result;
    }
}
