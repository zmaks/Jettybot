package ru.botota.jettybot.callback;

import com.vk.api.sdk.objects.messages.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.botota.jettybot.commands.vk.HelpCommand;

public class MessagesHandler {

   // private static final Logger LOG = LoggerFactory.getLogger(MembersUpdateJob.class);

    private static Integer parseInt(String[] args, int index, int defaultValue) {
        if (args.length <= index) {
            return defaultValue;
        }

        try {
            return Integer.parseInt(args[index]);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static void parseMessage(Integer groupId, Message message) {
        Integer vkId = message.getUserId();
        String[] args = message.getBody().split(" ");
        String command = args[0];

        try {
            switch (command.toLowerCase()) {
                case "help":
                    new HelpCommand(vkId).execute();
                    break;

                /*case "login":
                    if (args.length != 2) {
                        new HelpCommand(vkId).run();
                        break;
                    }

                    String ytLogin = args[1];
                    new LoginCommand(vkId, ytLogin).run();
                    break;

                case "logout":
                    new LogoutCommand(vkId).run();
                    break;

                case "mine":
                    new MineTasksCommand(vkId, parseInt(args, 1, 10)).run();
                    break;

                case "search":
                    String filter = message.getBody().substring(command.length() + 1);
                    new SearchTasksCommand(vkId, filter, 10).run();
                    break;

                case "config":
                    new GetConfigCommand(vkId).run();
                    break;

                case "setconfig":
                    if (args.length != 3) {
                        new HelpCommand(vkId).run();
                        break;
                    }

                    String name = args[1];
                    String value = args[2];

                    new SetConfigCommand(vkId, name, value).run();
                    break;

                case "uptime":
                    new UptimeCommand(vkId).run();
                    break;
*/
                default:
                    new HelpCommand(vkId).execute();
            }

            //Statistic.increment(Statistic.Event.COMMAND);
        } catch (Exception e) {
            //LOG.error("Can't execute command", e);
            //Statistic.increment(Statistic.Event.FAILED_COMMAND);
        }
    }
}
