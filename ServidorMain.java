import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

// Compilar amb:
// javac -cp "lib/*:." Servidor.java
// java -cp "lib/*:." Servidor

// Tutorials: http://tootallnate.github.io/Java-WebSocket/

public class ServidorMain extends WebSocketServer {

    static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

    public static void main(String[] args) throws InterruptedException, IOException {
        int port = 8000; 
        int waitMillis = 5000;
        long timeBegin = 0;
        boolean running = true;

        // Deshabilitar SSLv3 per clients Android
        java.lang.System.setProperty("jdk.tls.client.protocols", "TLSv1,TLSv1.1,TLSv1.2");

        ServidorMain socket = new ServidorMain(port);
        socket.start();
        System.out.println("Servidor funciona al port: " + socket.getPort());

        // Crear les mesures
        ArrayList<ServidorMesura> mesures = new ArrayList<>();
        mesures.add(new ServidorMesura("temperatura", -5, 45));
        mesures.add(new ServidorMesura("gasolina", 0, 100));
        mesures.add(new ServidorMesura("velocitat", 30, 120));

        timeBegin = (new Date()).getTime();
        while (running) {
            // Calcular temps i % actual
            long timeNow = (new Date()).getTime();
            long timeDif = timeNow - timeBegin;
            double percentage = (timeDif * 100) / waitMillis;
            if (percentage > 100) percentage = 100;

            // Mostrar (i enviar) dades de les mesures actualitzades
            System.out.print("\033[H\033[2J");  
            System.out.flush(); 
            System.out.println("Mesures servidor:\n") ;
            for (int cnt = 0; cnt < mesures.size(); cnt = cnt + 1) {
                ServidorMesura ref = mesures.get(cnt);
                String line = ref.toString(percentage);
                System.out.println(line);
                socket.broadcast(line);
            }

            // Recalcular mesures si el temps sobrepassa 'waitMillis'
            if (timeDif > waitMillis) {
                timeBegin = (new Date()).getTime();
                for (int cnt = 0; cnt < mesures.size(); cnt = cnt + 1) {
                    ServidorMesura ref = mesures.get(cnt);
                    ref.setValueRandom();
                }
            }

            // Forçar espera per no col·lapsar el bucle
            TimeUnit.MILLISECONDS.sleep(100);
        }    

        System.out.println("Aturant Servidor");
        socket.stop(1000);
    }

    public ServidorMain(int port) throws UnknownHostException {
        super(new InetSocketAddress(port));
    }

    public ServidorMain(InetSocketAddress address) {
        super(address);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {

        // Saludem personalment al nou client
        conn.send("Benvingut a WsServer"); 

        // Enviem la direcció URI del nou client a tothom 
        broadcast("Nova connexió: " + handshake.getResourceDescriptor());

        // Mostrem per pantalla (servidor) la nova connexió
        String host = conn.getRemoteSocketAddress().getAddress().getHostAddress();
        System.out.println(host + " s'ha connectat");
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {

        // Informem a tothom que el client s'ha desconnectat
        broadcast(conn + " s'ha desconnectat");

        // Mostrem per pantalla (servidor) la desconnexió
        System.out.println(conn + " s'ha desconnectat");
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        // Aquest servidor no rep missatges
    }

    @Override
    public void onMessage(WebSocket conn, ByteBuffer message) {
        // Aquest servidor no rep missatges
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
    }

    @Override
    public void onStart() {
        // S'inicia el servidor
        setConnectionLostTimeout(0);
        setConnectionLostTimeout(100);
    }
}