import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class Main {

    public static void main(String[] args) throws IOException {

        System.out.println("Waiting for an incoming connection on port 8088...");

        ExecutorService pool = Executors.newFixedThreadPool(50);

        try (ServerSocket serverSocket = new ServerSocket(8088)){
            while (true){
                try{

                    Socket socket = serverSocket.accept();

                    Callable<Void> connection = new RequestObject(socket);
                    pool.submit(connection);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }



    }

}

class RequestObject implements Callable<Void> {

    private final Socket socket;
    private static final Logger logger = Logger.getLogger("SingleFileHTTPServer");

    RequestObject(Socket socket){
        this.socket = socket;
    }

    @Override
    public Void call() {

        try{
            InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());

            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

            // read the first line only; that's all we need
            StringBuilder request = new StringBuilder(80);
            while (true) {
                int c = inputStreamReader.read();
                if (c == '\r' || c == '\n' || c == -1) break;
                request.append((char) c);
            }
            // If this is HTTP/1.0 or later send a MIME header
            if (request.toString().contains("HTTP/")) {
                dataOutputStream.writeUTF("GET ");
            }

            logger.info("Accepting connections on port " + socket.getLocalPort());
            logger.info("Data to be sent:");


        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }
}
