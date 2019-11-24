import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
            InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());

            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

            StringBuilder request = new StringBuilder(80);
            while (true) {
                int c = inputStreamReader.read();
                if (c == '\r' || c == '\n' || c == -1) break;
                request.append((char) c);
            }

            if (request.toString().contains("HTTP/")) {
                Path path = Paths.get("/home/jay/Dropbox/University of Illinois-Springfield/FS2019/Algorithms and Computation/Lectures/Module 10/module_10_dynamic_2.html");
                byte[] data = Files.readAllBytes(path);
                for (byte datum : data) {
                    dataOutputStream.writeByte(datum);
                }

            }

            System.out.println("got: " + request);

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ex) {
                    // ignore
                }
            }
        }

        return null;

    }
}
