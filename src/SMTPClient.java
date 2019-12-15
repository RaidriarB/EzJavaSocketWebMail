import javax.net.SocketFactory;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Scanner;

public class SMTPClient {

    public Boolean debug = true;

    /**
     * 使用socket发送邮件
     * @param srv 邮件服务器类别，比如qq，163
     * @param srcmail 发送者邮箱
     * @param dstmail 接受者邮箱
     * @param authstr 发送者的授权码
     * @param subject 邮件主题
     * @param data 邮件正文，用ArrayList存储每一行
     * @return 是否发送成功
     */
    public boolean sendMail(String srv,String srcmail,String dstmail,String authstr,String subject,ArrayList<String> data){

        try{
            Socket smtpSocket = new Socket("smtp.qq.com",25);
            System.out.println(smtpSocket.getLocalPort());

            InputStream inputStream = smtpSocket.getInputStream();
            OutputStream outputStream = smtpSocket.getOutputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            //Scanner reader = new Scanner(inputStream,"utf-8");

            PrintWriter prewritter = new PrintWriter(new OutputStreamWriter(
                    outputStream,"utf-8"), true);//true很关键

            MyPrintWritter writter = new MyPrintWritter(prewritter,debug);

            SMTPFunction sf = new SMTPFunction(false);

            System.out.println(reader.readLine());
            //System.out.println("开始发送一个SMTP消息 {");
            //HELO
            writter.println(sf.getHELO(srv));
            System.out.println(reader.readLine());
            //AUTH LOGIN
            writter.println("AUTH LOGIN");
            System.out.println(reader.readLine());
            writter.println(sf.getB64(srcmail));
            System.out.println(reader.readLine());
            writter.println(sf.getB64(authstr));
            System.out.println(reader.readLine());
            //mail from rcpt to
            writter.println(sf.getMailFrom(srcmail));
            System.out.println(reader.readLine());
            writter.println(sf.getRcptTo(dstmail));
            System.out.println(reader.readLine());
            //data
            writter.println("DATA");
            System.out.println(reader.readLine());
            //发送邮件
            writter.println("subject:"+subject);
            writter.println("from:" + srcmail);
            writter.println("to:" + dstmail);
            writter.println("Content-Type: text/plain;charset=\"UTF-8\"");//如果发送正文必须加这个，而且下面要有一个空行
            writter.println("");
            //发送的正文
            for(String line:data){
                writter.println(line);
            }
            writter.println(".");//告诉服务器我发送的内容完毕了
            writter.println("");
            System.out.println(reader.readLine());
            //
            writter.println("quit");
            System.out.println(reader.readLine());
            return true;

        }catch (IOException e){
            e.printStackTrace();
        }
        return false;
    }

    public static void main(String[] args) {
        String srcmail = "1085455474.com";
        String srv = "qq";
        String dstmail = "1085455474@qq.com";
        String authstr = "vadvhalllqxiigie";
        String subject = "测试邮件";
        ArrayList<String> data = new ArrayList<>();
        data.add("这是第一行，首次使用java发送邮件成功！");
        data.add("这是第二行");
        data.add("This is Third line.");

        SMTPClient sc = new SMTPClient();
        Boolean isSuccess = sc.sendMail(srv, srcmail, dstmail, authstr, subject, data);
        System.out.println(isSuccess);
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
class MyPrintWritter{
    public PrintWriter printWriter;
    private Boolean debug = false;
    public MyPrintWritter(PrintWriter pr,Boolean debug){
        this.printWriter = pr;
        this.debug = debug;
    }

    public void println(String str){

        printWriter.println(str);
        if(str == null)return;
        if(debug == true)System.out.println(str);
    }
}