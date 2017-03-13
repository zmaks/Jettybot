package ru.botota.jettybot;


import com.google.gson.Gson;
import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.groups.responses.GetCallbackConfirmationCodeResponse;
import com.vk.api.sdk.objects.groups.responses.GetCallbackServerSettingsResponse;
import com.vk.api.sdk.objects.groups.responses.SetCallbackServerResponse;
import com.vk.api.sdk.objects.groups.responses.SetCallbackServerResponseStateCode;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import ru.botota.jettybot.server.CallbackRequestHandler;
import ru.botota.jettybot.server.ConfirmationCodeRequestHandler;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * Created by Maksim on 13.03.2017.
 */
public class Application {

    private static VkApiClient vk;
    private static Integer groupId = null;
    private static GroupActor actor = null;



    public static void main(String[] args){
        try {
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }

        run();
        System.out.println("STARTED");
    }

    private static void init() throws Exception {
        Properties properties = loadConfiguration();

        groupId = Integer.valueOf(properties.getProperty("vk.group.id"));
        initVkClient(properties);
        initServer(properties);
    }

    private static void initVkClient(Properties properties) {
        TransportClient client = HttpTransportClient.getInstance();
        vk = new VkApiClient(client, new Gson());
        actor = new GroupActor(Integer.parseInt(properties.getProperty("vk.group.id")), properties.getProperty("vk.group.token"));
    }

    private static void run() {
    }

    private static void initServer(Properties properties) throws Exception {
        Integer port = Integer.valueOf(properties.getProperty("server.port"));
        String host = properties.getProperty("server.host");
        String secret = properties.getProperty("server.secret");
        HandlerCollection handlers = new HandlerCollection();

        ConfirmationCodeRequestHandler confirmationCodeRequestHandler = null;

        GetCallbackServerSettingsResponse getCallbackServerSettingsResponse = vk.groups().getCallbackServerSettings(actor, actor.getGroupId()).execute();
        if (!getCallbackServerSettingsResponse.getServerUrl().equals(host)) {
            GetCallbackConfirmationCodeResponse getCallbackConfirmationCodeResponse = vk().groups().getCallbackConfirmationCode(actor, actor.getGroupId()).execute();
            String confirmationCode = getCallbackConfirmationCodeResponse.getCode();
            confirmationCodeRequestHandler = new ConfirmationCodeRequestHandler(confirmationCode);
        }

        vk.groups().setCallbackSettings(actor, actor.getGroupId()).messageNew(true).execute();

        CallbackRequestHandler callbackRequestHandler = new CallbackRequestHandler(secret);

        if (confirmationCodeRequestHandler != null) {
            handlers.setHandlers(new Handler[]{confirmationCodeRequestHandler, callbackRequestHandler});
        } else {
            handlers.setHandlers(new Handler[]{callbackRequestHandler}); //temp solution
        }

        //handlers.setHandlers(new Handler[]{callbackRequestHandler});


        Server server = new Server(port);
        server.setHandler(handlers);

        server.start();

        for (int i = 0; i < 10; i++) {
            SetCallbackServerResponse response = vk.groups().setCallbackServer(actor, actor.getGroupId())
                    .serverUrl(host)
                    .execute();

            if (response.getStateCode() == SetCallbackServerResponseStateCode.FAILED) {
                throw new IllegalStateException("Can't set callback server");
            }

            if (response.getStateCode() == SetCallbackServerResponseStateCode.OK) {
                return;
            }

            TimeUnit.SECONDS.sleep(1);
        }

        server.join();
    }

    private static Properties loadConfiguration() {
        Properties properties = new Properties();
        try (InputStream is = Application.class.getResourceAsStream("/bot.properties")) {
            properties.load(is);
        } catch (IOException e) {
            //LOG.error("Can't load properties file", e);
            throw new IllegalStateException(e);
        }

        return properties;
    }

    public static VkApiClient vk() {
        return vk;
    }

    public static GroupActor actor() {
        return actor;
    }
}
