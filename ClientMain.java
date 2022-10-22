
 
import java.net.URI;
import java.net.URISyntaxException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;

 
public class ClientMain {

    private static double gasolina;
    private static double velocitat;
    private static double temperatura;

    public static void main(String[] args) {
        // Connectar amb ServidorMain
        int port = 8000;
        String host = "localhost";
        String uri = "ws://" + host + ":" + port;
        ClientSocket socket = connecta(uri);
        boolean running = true;

        while (running) {
            System.out.print("\033[H\033[2J");  
            System.out.flush(); 
            System.out.println("Mesures rebudes:\n") ;
            System.out.println("Temperatura: " + temperatura);
            System.out.println("Gasolina: " + gasolina);
            System.out.println("Velocitat: " + velocitat);

            // Forçar espera per no col·lapsar el bucle
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) { e.printStackTrace(); }
        }

        socket.close();
    }

    public static ClientSocket connecta (String location) {
        ClientSocket client = null;

        try {
            client = new ClientSocket(new URI(location), (Draft) new Draft_6455()) {
                @Override
                public void onMessage(String message) {
                    String[] arr = message.split("=");
                    if (arr.length == 2) {
                        try {
                            NumberFormat format = NumberFormat.getInstance(Locale.getDefault());
                            Number number = format.parse(arr[1]);
                            Double value = number.doubleValue();
                            if (message.contains("temperatura")) {
                                temperatura = value;
                            }
                            else if (message.contains("velocitat")) {
                                velocitat = value;
                            }
                            else if (message.contains("gasolina")) {
                                gasolina = value;
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                     }
                }
            };
            client.connect();
        } catch (URISyntaxException e) { 
            e.printStackTrace(); 
            System.out.println("Error: " + location + " no és una direcció URI de WebSocket vàlida");
        }

        return client;
    }
}