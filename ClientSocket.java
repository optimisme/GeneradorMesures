import java.net.URI;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;

public abstract class ClientSocket extends WebSocketClient {
    
    public ClientSocket (URI uri, Draft draft) {
        super (uri, draft);
    }

    @Override
    public abstract void onMessage(String message);

    @Override
    public void onOpen(ServerHandshake handshake) {
        System.out.println("T'has connectat a: " + getURI());
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("T'has desconnectat de: " + getURI());
    }

    @Override
    public void onError(Exception ex) {
        System.out.println("Error amb la connexió del socket");
    }
}
