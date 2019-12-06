import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class HTTPServerThread {

    private boolean haveReq;
    private InetAddress ip;
    private int port;

    public ArrayList<String> getHttpRequest(Socket incoming){
        ArrayList<String> httpreq = new ArrayList<>();

        this.haveReq = true;
        this.ip = incoming.getInetAddress();
        this.port = incoming.getPort();

        System.out.println(this.ip+":"+this.port);

        try {
            InputStream inStream = incoming.getInputStream();
            OutputStream outStream = incoming.getOutputStream();

            try (Scanner in = new Scanner(inStream, "UTF-8")) {
                PrintWriter out = new PrintWriter(
                        new OutputStreamWriter(outStream, "UTF-8"),
                        true
                );
                while (!incoming.isClosed() && in.hasNextLine()) {
                    String line = in.nextLine();
                    System.out.println(line);
                    //read Two \r\n to escape
                    if (line.isEmpty()) {
                        break;
                    }
                    httpreq.add(line);
                }
                return httpreq;
            }
        }catch (IOException e){
            e.printStackTrace();
            System.out.println("HTTP Server Thread的Socket部分出错啦");
            return null;
        }
    }

    public static void main(String[] args) {
        try(ServerSocket ss = new ServerSocket(8888)){
            ss.setSoTimeout(3000);
            try(Socket incoming = ss.accept()){
                ArrayList<String> httpreq = new HTTPServerThread()
                        .getHttpRequest(incoming);
                HttpObject myhttp = new HTTPParser().Parse(httpreq);
                System.out.println(myhttp.toString());
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
