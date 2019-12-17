import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * http会话中所需要的逻辑
 */
public class HTTPServerThread {

    private static String webRoot = "web";

    private boolean haveReq;
    private InetAddress ip;
    private int port;

    /**
     * 将Socket的字符流保存在ArrayList中
     * @param incoming 从这个socket接收数据
     * @param in 从这个输入流接收数据
     * @return 接收到的数据传入ArrayList
     */
    private ArrayList<String> getHttpRequest(Socket incoming,Scanner in){
        ArrayList<String> httpreq = new ArrayList<>();
        while (!incoming.isClosed() && in.hasNextLine()) {
            String line = in.nextLine();
            //System.out.println(line);
            if (line.isEmpty()) {
                break;
            }
            httpreq.add(line);
        }
        return httpreq;
    }

    /**
     * 根据Request来判断返回的Response，是HTTP服务器的核心逻辑
     * 每个目录的默认访问位置是index.html
     * 出错页面为404.html
     * 两个api：checkLogin和sendMail
     * @param httpObject request封装的httpObject
     * @return response封装的对象
     */
    private HTTPRespObject getRespFromReq(HttpObject httpObject){
        try{
            String path = httpObject.getURL();
            HashMap<String,String> params = httpObject.getParams();
            HashMap<String,String> cookies = httpObject.getCookies();
            File retFile;

            //核心逻辑
            if(!path.endsWith(".html")){
                retFile = new File(webRoot+path+"index.html");
            }else if(path == "sendMail"){
                String srcmail = cookies.get("email");
                String srv = SMTPFunction.getSrv(srcmail);
                String dstmail = params.get("dst");
                String authstr = cookies.get("pwd");
                String subject = params.get("subj");
                ArrayList<String> data = new ArrayList<>();
                String dataStr = params.get("data");

                for(String line : dataStr.split("\n")){
                    data.add(line);
                }
                SMTPClient sc = new SMTPClient(true);
                Boolean isSuccess = sc.sendMail(srv, srcmail, dstmail, authstr, subject, data);
                System.out.println(isSuccess);
                retFile = new File(webRoot+"index.html");

            }else if(path == "checkLogin") {
                retFile = new File(webRoot+"index.html");

            }else{
                retFile = new File(webRoot + path);
            }

            FileInputStream fin = new FileInputStream(retFile);
            HTTPRespObject resp = new HTTPRespObject();

            //设置Response头部
            System.out.println("请求的资源为:"+retFile.getName());
            resp.setProtocol("HTTP/1.1");
            resp.setServer("Raidriar_Test_Server");
            resp.setStatcode(200);
            resp.setContent_Type("text/html; charset=UTF-8");
            resp.setStatString("OK");

            //把HTML文件传入Resp的对象
            try(Scanner in = new Scanner(fin,"UTF-8")){
                while(in.hasNextLine()){
                    resp.addLineToBody(in.nextLine());
                }
            }

            return resp;

        }catch (FileNotFoundException e){//应当返回404页面

            System.out.println("请求的资源没有找到,返回404页面");
            HTTPRespObject resp = new HTTPRespObject();
            resp.setProtocol("HTTP/1.1");
            resp.setServer("Raidriar_Test_Server");
            resp.setStatcode(404);
            resp.setContent_Type("text/html; charset=UTF-8");
            resp.setStatString("Not Found");

            try(FileInputStream fin = new FileInputStream(
                    new File(webRoot+"/404.html"))){

                try(Scanner in = new Scanner(fin,"UTF-8")){
                    while(in.hasNextLine()){
                        resp.addLineToBody(in.nextLine());
                    }
                }
                return resp;

            }catch (FileNotFoundException e1){
                e1.printStackTrace();
                System.out.println("catch中的404出错了");
                return null;
            }catch (IOException e2){
                e2.printStackTrace();
                return null;
            }
        }
    }

    /**
     * 将Response对象传送出去
     * @param incoming 从这个socket传送数据
     * @param out 从这个输出流传送数据
     * @param resp 传送这个response
     */
    private void sendHttpResponse(Socket incoming,PrintWriter out,HTTPRespObject resp){
        if(haveReq == false){
            return;
        }
        ArrayList<String> respString = HTTPParser.GenerateRespStr(resp);
        if(respString == null){
            System.out.println("没有响应对象。");
        }else{
            for(String line : respString){
                out.println(line);
            }
        }
    }

    /**
     * 核心逻辑，在一个socket打开到关闭的期间，处理HTTP的请求
     * @param incoming 传来的socket
     */
    public void handleHttpRequest(Socket incoming){

        this.haveReq = true;
        this.ip = incoming.getInetAddress();
        this.port = incoming.getPort();
        System.out.println("请求的客户端IP为："+this.ip+"，端口是"+this.port);

        try {
            InputStream inStream = incoming.getInputStream();
            OutputStream outStream = incoming.getOutputStream();

            try (Scanner in = new Scanner(inStream, "UTF-8")) {
                PrintWriter out = new PrintWriter(
                        new OutputStreamWriter(outStream, "UTF-8"),
                        true
                );

                //核心逻辑
                //从socket的字符流读取到ArrayList对象
                ArrayList<String> httpStr = getHttpRequest(incoming, in);

                //从ArrayList构造HttpObject对象
                HttpObject httpObject = new HTTPParser().Parse(httpStr);

                //根据业务逻辑，从request给出响应的Response
                HTTPRespObject respObject = new HTTPRespObject();
                HTTPRespObject resp = getRespFromReq(httpObject);

                //发送Response
                sendHttpResponse(incoming,out,resp);
            }
        }catch (IOException e){
            e.printStackTrace();
            System.out.println("HTTP Server Thread的Socket部分出错啦");
        }
    }

    /**
     * 单元测试，这里只是单线程地实现了一下
     */
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
