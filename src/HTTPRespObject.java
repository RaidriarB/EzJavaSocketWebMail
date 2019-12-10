import java.util.ArrayList;
import java.util.HashMap;

public class HTTPRespObject {

    private String Protocol;
    private int statcode;
    private String statString;
    private String Server;
    private String Content_Type;
    private int Content_Length;

    private ArrayList<String> body = new ArrayList<>();

    public String getProtocol() {
        return Protocol;
    }

    public void setProtocol(String protocol) {
        Protocol = protocol;
    }

    public int getStatcode() {
        return statcode;
    }

    public void setStatcode(int statcode) {
        this.statcode = statcode;
    }

    public String getStatString() {
        return statString;
    }

    public void setStatString(String statString) {
        this.statString = statString;
    }

    public String getServer() {
        return Server;
    }

    public void setServer(String server) {
        Server = server;
    }

    public String getContent_Type() {
        return Content_Type;
    }

    public void setContent_Type(String content_Type) {
        Content_Type = content_Type;
    }

    public int getContent_Length() {
        return Content_Length;
    }

    public void setContent_Length(int content_Length) {
        Content_Length = content_Length;
    }

    public ArrayList<String> getBody() {
        return body;
    }

    public void addLineToBody(String line) {
        //System.out.println(line);
        this.body.add(line);
    }

}
