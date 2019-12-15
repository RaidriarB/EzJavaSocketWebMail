import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
/**
  * Echo 客户端代码，模拟telnet发送TCP报文
  * @author ray
  *
  */
public class sss {
    public static void main(String[] args) {

        Socket client = null; //客户端实例
        BufferedReader input = null; //接受键盘输入的消息
        BufferedReader in = null; //接受服务器响应的数据

        PrintStream out = null; //向服务器输出信息

        try {
            client = new Socket("smtp.qq.com",25); //实例化客户端，并与服务器建立连接

            System.out.println(client.getLocalPort());

            input = new BufferedReader(new InputStreamReader(System.in)); //准备读入用户键入信息

            out = new PrintStream(client.getOutputStream()); //用于向服务器发送请求报文
            in = new BufferedReader(new InputStreamReader(client.getInputStream())); //接受服务器响应报文

            boolean isEnd = false;
            String inputMsg = null ,serverRespMsg = null;
            while(!isEnd){
                inputMsg = input.readLine(); //从键盘读入用户输入的信息，遇到换行符截止
                out.println(inputMsg); //将用户输入的信息发送给服务器
                if("bye".equalsIgnoreCase(inputMsg.trim())){
                    isEnd = true;
                } else {
                    serverRespMsg = in.readLine(); //读取服务器响应报文
                    System.out.println(serverRespMsg);
                }
            }

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                input.close();
                in.close();
                out.close();
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
