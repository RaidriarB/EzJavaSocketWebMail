import java.io.*;
import java.lang.reflect.Array;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class HTTPServerThread {

    private boolean haveReq;
    private InetAddress ip;
    private int port;

    private ArrayList<String> getHttpRequest(Socket incoming,Scanner in){
        ArrayList<String> httpreq = new ArrayList<>();
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

    public void handleHttpRequest(Socket incoming){

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

                ArrayList<String> httpStr = getHttpRequest(incoming, in);
                HttpObject httpObject = new HTTPParser().Parse(httpStr);

                sendHttpResponse(incoming,out);
            }
        }catch (IOException e){
            e.printStackTrace();
            System.out.println("HTTP Server Thread的Socket部分出错啦");
        }
    }

    private void sendHttpResponse(Socket incoming,PrintWriter out){
        if(haveReq == false){
            return;
        }
        out.println("This is a test resp.");

    }

    public static void main(String[] args) {
        try(ServerSocket ss = new ServerSocket(8888)){
            try(Socket incoming = ss.accept()){
                HTTPServerThread hs = new HTTPServerThread();
                hs.handleHttpRequest(incoming);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
