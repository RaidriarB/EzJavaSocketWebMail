import java.io.UnsupportedEncodingException;
import java.util.Base64;

public class SMTPFunction {

    //邮件服务器的类型字串
    private static final String qqMailServer = "smtp.qq.com";
    private static final String nc163MailServer = "smtp.163.com";

    /**
     * 从邮箱检测邮件服务器的类型字串
     * @param email 邮箱
     * @return 邮件服务器的类型字串
     */
    public static String getSrv(String email){
        if(email.contains("qq"))return "qq";
        if(email.contains("163"))return "163";

        return null;
    }

    /**
     * 字符串转换成base64
     * @param str 要转换的字符串
     * @return base64编码后的字符串
     */
    public static String getB64(String str){
        Base64.Encoder encoder = Base64.getEncoder();
        String nstr = null;
        nstr = encoder.encodeToString(str.getBytes());
        return nstr;
    }

    /**
     * SMTP 第一个环节，发送HELO
     * @param srv
     * @return
     */
    public static String getHELO(String srv){
        String nstr = null;
        if(srv == "qq")nstr = "helo " + qqMailServer;
        if(srv == "163")nstr = "helo "+ nc163MailServer;
        return nstr;
    }

    /**
     * 获得mail from 格式的字串
     * @param mail 邮箱
     * @return mail from格式的字串
     */
    public static String getMailFrom(String mail){
        return "MAIL FROM: <"+mail+">";
    }

    /**
     *  获得rcpt to格式的字串
     * @param mail 邮箱
     * @return rcpt to格式的字串
     */
    public static String getRcptTo(String mail){
        return "RCPT TO: <"+mail+">";
    }


}
