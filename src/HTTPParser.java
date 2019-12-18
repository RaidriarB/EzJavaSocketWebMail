import java.io.*;
import java.lang.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * 解析HTTP协议的类
 * Parse方法得到HttpObject
 */
public class HTTPParser {
    private HttpObject httpObject;

    /**
     * 解析Http协议
     * @param httpstr http协议有很多行，把这些存入一个字符串ArrayList
     * @return HttpObject对象，这个对象支持了Http的常用子集，包含了Http协议的信息。
     */
    public HttpObject Parse(ArrayList<String> httpstr){
        if(httpstr.isEmpty()){
            return null;
        }
        this.httpObject = new HttpObject();

        try{
            for(String line : httpstr){
                if(line.startsWith("GET")){
                    //解析请求行
                    ParseMethod(line);
                }else if(line.startsWith("Cookie:")){
                    //解析Cookie，存入HashMap
                    ParseCookie(line);
                }else{
                    //解析其他行
                    ParseString(line);
                }
            }
            return httpObject;
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
            System.out.println("URL解析出错啦！");
            return null;
        }
    }

    /**
     * 把HTTPRespObject转换成可以发送的字串
     * @param respObject 待转换的对象
     * @return 字串ArrayList
     */
    public static ArrayList<String> GenerateRespStr(HTTPRespObject respObject) {
        ArrayList<String> respStr = new ArrayList<>();
        String line = "";

        //First line
        if (respObject.getProtocol() != null) {
            line += respObject.getProtocol();
            if (respObject.getStatcode() != 0) {
                int statcode = respObject.getStatcode();
                line += " ";
                if (statcode == 200) {
                    line += "200 OK";
                } else if (statcode == 404) {
                    line += "404 Not Found";
                }
                respStr.add(line);
            }
        }

        //Server
        if (respObject.getServer() != null) {
            line = ("Server: " + respObject.getServer());
            respStr.add(line);
        }

        //content_type
        if (respObject.getContent_Type() != null) {
            line = ("Content-Type: " + respObject.getContent_Type());
            respStr.add(line);
        }

        //body
        respStr.add("");
        if (respObject.getBody().size() != 0) {
            for (String eachLine : respObject.getBody()) {
                respStr.add(eachLine);
            }
        }
        return respStr;
    }

    /**
     * 解析URL请求行，包括动作、url和get方法的参数、协议版本。
     * @param line 要解析的请求行
     * @throws UnsupportedEncodingException 遇到了不能解析的URL编码，有可能是恶意攻击
     */
    private void ParseMethod(String line)throws UnsupportedEncodingException{
        String[] strs = new String[3];
        strs = line.trim().split(" ");
        this.httpObject.setMethod(strs[0]);
        this.httpObject.setURL(strs[1].split("\\?",2)[0]);
        this.httpObject.setProtocol(strs[2]);

        //解析URL中带有的参数
        if(strs[1].contains("?")){
            if(! strs[1].trim().endsWith("?")){
                String[] params = strs[1].split("\\?",2)[1].split("&");
                for(String param:params){
                    String paramKey = URLDecoder.decode(param.split("=")[0], StandardCharsets.UTF_8);
                    String paramValue;
                    try{
                        paramValue = URLDecoder.decode(param.split("=")[1], StandardCharsets.UTF_8);
                    }catch (ArrayIndexOutOfBoundsException e){
                        paramValue = "";
                    }
                    //System.out.println("key:"+paramKey+",value:"+paramValue);
                    this.httpObject.addParam(paramKey,paramValue);
                }
            }
        }
        //没有参数，什么都不做。
    }

    /**
     * 解析HTTP的其他字段，除去Cookie和请求行
     * @param line 要解析的行
     */
    private void ParseString(String line){
        if(line.startsWith("Host")){
            this.httpObject.setHost(
                    line.replaceAll("Host:", "").trim());
        }else if (line.startsWith("User-Agent")){
            this.httpObject.setUA(
                    line.replaceAll("User-Agent:", "").trim());
        }else{
            return;
        }
    }

    /**
     * 解析Cookie到HashMap
     * @param line 要解析的cookie行
     * @throws UnsupportedEncodingException 遇到了不能解析的URL编码
     */
    private void ParseCookie(String line) throws UnsupportedEncodingException{
        String[] cookies = line.substring(7).trim().split(";");
        for(String cookie : cookies){
            cookie = cookie.trim();
            String cKey = URLDecoder.decode(cookie.split("=")[0], StandardCharsets.UTF_8);
            String cValue = URLDecoder.decode(cookie.split("=")[1], StandardCharsets.UTF_8);
            this.httpObject.addCookie(cKey, cValue);
        }
    }

    public static void main(String[] args) throws IOException{

        //这是一个示例文件，是抓包抓下来的。
        BufferedReader br = new BufferedReader(new FileReader(new File("src/httpexam")));
        ArrayList<String> httpstr = new ArrayList<String>();
        while(true){
            String str = "";
            str = br.readLine();
            if(str != null){
                httpstr.add(str);
            }else break;
        }
        //下一步的目的，就是获得这个httpstr的ArrayList。
        HttpObject myhttp = new HTTPParser().Parse(httpstr);
        //System.out.println(myhttp.toString());
    }
}