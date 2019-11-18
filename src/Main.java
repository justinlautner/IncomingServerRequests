import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

    RequestObject(Socket socket){
        this.socket = socket;
    }

    @Override
    public Void call() {

        try{
            /*InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());

            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

            dataOutputStream.writeUTF("GET /home/jay/Downloads/rclone-v1.50.1-linux-amd64/rclone.1" + " HTTP/1.1\n" +
                    "Connection: close\n");
            dataOutputStream.writeUTF("\r\n");
            dataOutputStream.flush();*/

            OutputStream out = new BufferedOutputStream(
                    socket.getOutputStream()
            );
            InputStream in = new BufferedInputStream(
                    socket.getInputStream()
            );
            // read the first line only; that's all we need
            StringBuilder request = new StringBuilder(80);
            while (true) {
                int c = in.read();
                if (c == '\r' || c == '\n' || c == -1) break;
                request.append((char) c);
            }
            // If this is HTTP/1.0 or later send a MIME header
            if (request.toString().indexOf("HTTP/") != -1) {
                out.write(header);
            }
            out.write(content);
            out.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }
}
