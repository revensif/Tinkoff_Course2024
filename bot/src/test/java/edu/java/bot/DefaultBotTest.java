import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SetMyCommands;
import edu.java.bot.DefaultBot;
import edu.java.bot.commands.Command;
import edu.java.bot.commands.StartCommand;
import edu.java.bot.configuration.ApplicationConfig;
import edu.java.bot.processor.DefaultUserMessageProcessor;
import edu.java.bot.processor.UserMessageProcessor;
import java.util.List;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mockito;
import org.springframework.stereotype.Component;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Component
public class DefaultBotTest {

    @Test
    @DisplayName("Menu creation test")
    public void shouldCreateMenu() {
        UserMessageProcessor processor = mock(DefaultUserMessageProcessor.class);
        Command startCommand = new StartCommand(processor);
        List<? extends Command> commands = List.of(startCommand);
        Mockito.<List<? extends Command>>when(processor.commands()).thenReturn(commands);
        String token = System.getenv("TOKEN");
        DefaultBot bot = new DefaultBot(new ApplicationConfig(token), processor);
        SetMyCommands result = bot.createMenu();
        BotCommand[] botCommands = (BotCommand[]) result.getParameters().get("commands");
        assertThat(botCommands).isEqualTo(List.of(startCommand.toApiCommand()).toArray());
    }

    @Test
    @DisplayName("Process test")
    public void shouldProcessUpdates() {
        UserMessageProcessor processor = new DefaultUserMessageProcessor();
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        Chat chat = mock(Chat.class);
        when(update.message()).thenReturn(message);
        when(message.text()).thenReturn("/start");
        when(message.chat()).thenReturn(chat);
        String token = System.getenv("TOKEN");
        DefaultBot bot = new DefaultBot(new ApplicationConfig(token), processor);
        int process = bot.process(List.of(update));
        assertThat(process).isEqualTo(-1);
    }
}
