package handlers;

import handlers.utils.HttpConstants;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import wondertrade.WondertradeManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RequestHandler extends AbstractHandler {
    private WondertradeManager wondertradeManager;

    public RequestHandler(WondertradeManager wondertradeManager) {
        this.wondertradeManager = wondertradeManager;
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (HttpConstants.POST.equals(request.getMethod())) {
            handlePost(baseRequest, target, response);
        } else if (HttpConstants.GET.equals(request.getMethod())) {
            handleGet(baseRequest, target, response);
        }
    }

    private void prepareResponse(HttpServletResponse response) {
        response.setContentType(HttpConstants.TEXT_CONTENT_TYPE);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    private void handlePost(Request baseRequest, String target, HttpServletResponse response) throws IOException {
        prepareResponse(response);
        switch (target) {
            case "/wondertrade":
                wondertradeManager.handleWondertradeRequest(baseRequest, response);
                break;
            default:
                response.getWriter().println("This was a post");
                break;
        }
        baseRequest.setHandled(true);
    }

    private void handleGet(Request baseRequest, String target, HttpServletResponse response) throws IOException {
        prepareResponse(response);
        baseRequest.setHandled(true);
        response.getWriter().println("This was a get");
    }
}
