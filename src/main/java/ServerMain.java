import handlers.GetHandler;
import handlers.RequestHandler;
import org.eclipse.jetty.server.Server;
import wondertrade.WondertradeManager;

public class ServerMain {
        public static void main(String[] args) throws Exception {
            WondertradeManager wondertradeManager = new WondertradeManager();
            Server server = new Server(8080);
            server.setHandler(new RequestHandler(wondertradeManager));
            server.start();
            server.join();
        }
}
