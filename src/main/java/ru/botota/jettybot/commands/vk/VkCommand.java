package ru.botota.jettybot.commands.vk;

import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.queries.messages.MessagesSendQuery;
import ru.botota.jettybot.Application;

import java.util.Random;

/**
 * Created by Maksim on 14.03.2017.
 */
public abstract class VkCommand {
    private Integer vkId;

    public VkCommand(Integer vkId) {
        this.vkId = vkId;
    }

    public abstract void execute() throws Exception;

    public Integer getVkId() {
        return vkId;
    }

    public void sendMessage(String msg) throws ClientException, ApiException {
        getDefaultMsgSendQuery(msg).execute();
    }

    public MessagesSendQuery getDefaultMsgSendQuery(String msg){
        return Application.vk().messages().send(Application.actor())
                .randomId(new Random().nextInt(10000))
                .message(msg)
                .peerId(getVkId());
    }
}
