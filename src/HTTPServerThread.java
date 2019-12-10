import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class HTTPServerThread {

    private static String webRoot = "web";

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

    private HTTPRespObject getTestResp(){
        HTTPRespObject resp = new HTTPRespObject();
        resp.setProtocol("HTTP/1.1");
        resp.setServer("Raidriar_Test_Server");
        resp.setStatcode(200);
        resp.setContent_Type("text/html; charset=UTF-8");
        resp.setContent_Length(225);


        resp.addLineToBody(new String("<html>"));
        resp.addLineToBody(new String("\t<head>"));
        resp.addLineToBody(new String("\t\t<title>xssTest</title>"));
        resp.addLineToBody(new String("\t</head>"));
        resp.addLineToBody(new String("\t<body>"));
        resp.addLineToBody(new String("\t\t<form name=\"input\" action=\"xss.php\" method=\"get\">"));
        resp.addLineToBody(new String("\t\tUsername: <input type=\"text\" name=\"user\">"));
        resp.addLineToBody(new String("\t\t<input type=\"submit\" value=\"Submit\">"));
        resp.addLineToBody(new String("\t\t</form>"));
        resp.addLineToBody(new String("\t</body>"));
        resp.addLineToBody(new String("</html>"));
        resp.addLineToBody(new String("Hello,"));

        return resp;
    }

    private HTTPRespObject getRespFromReq(HttpObject httpObject){
        try{
            String path = httpObject.getURL();
            File retFile;
            if(path.endsWith("/")){
                retFile = new File(webRoot+path+"index.html");
            }else{
                retFile = new File(webRoot+path);
            }
            System.out.println(retFile);

            FileInputStream fin = new FileInputStream(retFile);
            HTTPRespObject resp = new HTTPRespObject();

            resp.setProtocol("HTTP/1.1");
            resp.setServer("Raidriar_Test_Server");
            resp.setStatcode(200);
            resp.setContent_Type("text/html; charset=UTF-8");
            resp.setStatString("OK");

            try(Scanner in = new Scanner(fin,"UTF-8")){
                while(in.hasNextLine()){
                    resp.addLineToBody(in.nextLine());
                }
            }
            return resp;

        }catch (FileNotFoundException e){
            System.out.println("请求的资源没有找到");
            //应当返回404页面
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
                HTTPRespObject respObject = new HTTPRespObject();

                HTTPRespObject resp = getRespFromReq(httpObject);

                sendHttpResponse(incoming,out,resp);
            }
        }catch (IOException e){
            e.printStackTrace();
            System.out.println("HTTP Server Thread的Socket部分出错啦");
        }
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
