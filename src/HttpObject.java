import java.util.Arrays;
import java.util.HashMap;

/**
 * http协议抽象成HttpObject
 */
public class HttpObject {

    private boolean valid = true;
    private String Method;

    private HashMap<String,String> params = new HashMap<>();

    private String URL;
    private String Protocol;
    private String Host;
    private String UA;

    private HashMap<String,String> cookies = new HashMap<>();

    public void addParam(String key,String value){
        params.put(key,value);
    }
    public HashMap<String,String> getParams(){
        return this.params;
    }

    public void addCookie(String key,String value){
        cookies.put(key,value);
    }
    public HashMap<String,String> getCookies(){
        return this.cookies;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public String getMethod() {
        return Method;
    }

    public void setMethod(String method) {
        Method = method;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getProtocol() {
        return Protocol;
    }

    public void setProtocol(String protocol) {
        Protocol = protocol;
    }

    public String getHost() {
        return Host;
    }

    public void setHost(String host) {
        Host = host;
    }

    public String getUA() {
        return UA;
    }

    public void setUA(String UA) {
        this.UA = UA;
    }

    @Override
    public String toString() {
        return "HttpObject{" +"\n"+
                "valid=" + valid +"\n"+
                ", Method='" + Method + '\'' +"\n"+
                ", params=" + params +"\n"+
                ", URL='" + URL + '\'' +"\n"+
                ", Protocol='" + Protocol + '\'' +"\n"+
                ", Host='" + Host + '\'' +"\n"+
                ", UA='" + UA + '\'' +"\n"+
                ", Cookie=" + cookies +"\n"+
                '}';
    }
}
