package ru.botota.jettybot.commands.vk;

/**
 * Created by Maksim on 14.03.2017.
 */
public class HelpCommand extends VkCommand {

    public HelpCommand(Integer vkId) {
        super(vkId);
    }

    @Override
    public void execute() throws Exception {
        sendMessage("Збс");
    }
}
