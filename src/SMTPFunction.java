import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Base64;

public class SMTPFunction {

    private static final String qqMailServer = "smtp.qq.com";
    private static final String nc163MailServer = "smtp.163.com";

    private Boolean debug = false;//debug = true,开启回显

    public SMTPFunction(Boolean debug){
        this.debug = debug;
    }

    /**
     * 字符串转换成base64
     * @param str 要转换的字符串
     * @return base64编码后的字符串
     */
    public String getB64(String str){
        Base64.Encoder encoder = Base64.getEncoder();
        String nstr = null;
        try{
            nstr = encoder.encodeToString(str.getBytes("UTF-8"));
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }

        if(debug) System.out.println(nstr);
        return nstr;
    }

    /**
     * SMTP 第一个环节，发送HELO
     * @param srv
     * @return
     */
    public String getHELO(String srv){
        String nstr = null;
        if(srv == "qq")nstr = "ehlo " + qqMailServer;
        if(srv == "163")nstr = "ehlo "+ nc163MailServer;

        if(debug) System.out.println(nstr);
        return nstr;
    }

    public String getMailFrom(String mail){
        if(debug) System.out.println("MAIL FROM: <"+mail+">");
        return "MAIL FROM: <"+mail+">";
    }

    public String getRcptTo(String mail){
        if(debug) System.out.println("RCPT TO: <"+mail+">");
        return "RCPT TO: <"+mail+">";
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        String test = "123@qq.com";
        System.out.println(test.split("@")[1]);
    }
/*
telnet smtp.qq.com 25
ehlo smtp.qq.com
AUTH LOGIN
MTA4NTQ1NTQ3NEBxcS5jb20= 1085455474@qq.com -> b64
dmFkdmhhbGxscXhpaWdpZQ==  vadvhalllqxiigie -> b64
MAIL FROM: <1085455474@qq.com>
Rcpt TO: <test@qq.com>
 */
}
