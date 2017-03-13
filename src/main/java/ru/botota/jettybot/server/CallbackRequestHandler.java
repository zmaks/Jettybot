package ru.botota.jettybot.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.botota.jettybot.callback.CallbackApiHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Collectors;


public class CallbackRequestHandler extends AbstractHandler {

    private static final Logger LOG = LoggerFactory.getLogger(CallbackRequestHandler.class);

    private final static String OK_BODY = "ok";
    private final static String SECRET_ELEMENT_NAME = "secret";
    private String secret;
    private final CallbackApiHandler callbackApiHandler;

    public CallbackRequestHandler(String secret) {
        this.secret = secret;
        callbackApiHandler = new CallbackApiHandler();
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            return;
        }

        String body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        JsonObject jsonObject = new Gson().fromJson(body, JsonObject.class);
        if (!jsonObject.get(SECRET_ELEMENT_NAME).getAsString().equals(secret)){
            return;
        }
        System.out.println(body);
        boolean handled = callbackApiHandler.parse(jsonObject);
        if (!handled) {
            return;
        }

        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);
        response.getWriter().println(OK_BODY);
    }
}
