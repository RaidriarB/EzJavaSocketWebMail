import java.io.*;
import java.net.*;

/**
 * 主线程，服务器从这里启动。
 */
public class MainServer {
    static int servPort = 8888;

    public static void main(String[] args) {
        try(ServerSocket ss = new ServerSocket(servPort)){
            int reqnum = 0;

            while(true){
                reqnum ++ ;
                Socket incoming = ss.accept();

                System.out.println("开始处理第"+reqnum+"个请求");

                Runnable r = new HttpThread(incoming,reqnum);
                Thread t = new Thread(r);
                t.start();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}

/**
 * 每来一个HTTP请求，都创建一个新的线程进行处理
 */
class HttpThread implements Runnable{

    private Socket incoming;
    private int reqnum;//request的编号

    public HttpThread(Socket incoming,int num){
        this.incoming = incoming;
        this.reqnum = num;
    }

    @Override
    public void run() {
        System.out.println("Start handle Req("+reqnum+"):{");
        new HTTPServerThread().handleHttpRequest(incoming);
        System.out.println("}handle Req("+reqnum+") complete.");
    }
}
