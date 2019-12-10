import java.io.*;
import java.net.*;
import java.util.*;

public class MainServer {
    static int servPort = 8888;

    public static void main(String[] args) {
        try(ServerSocket ss = new ServerSocket(servPort)){
            int reqnum = 0;

            while(true){
                reqnum ++ ;
                Socket incoming = ss.accept();

                System.out.println("开始处理第"+reqnum+"个请求");

                Runnable r = new HttpThread(incoming);
                Thread t = new Thread(r);
                t.start();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
class HttpThread implements Runnable{

    private Socket incoming;
    public HttpThread(Socket incoming){
        this.incoming = incoming;
    }
    @Override
    public void run() {
        System.out.println("Start handle:[");
        new HTTPServerThread().handleHttpRequest(incoming);
        System.out.println("]handle complete.");
    }
}
