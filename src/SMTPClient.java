import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * SMTP的客户端
 * 功能1：检查邮箱和授权码是否可以登录
 * 功能2：发送邮件
 */
public class SMTPClient {

    private static final int timeOut = 1500;

    public Boolean debug = false;//如果开启debug，SMTP消息将显示在控制台
    private Boolean inFile = false;//如果开启inFile，消息将被输出在文件中

    public SMTPClient(Boolean debug){
        this.debug = debug;
    }

    public SMTPClient(Boolean debug,Boolean inFile){
        this(debug);
        this.inFile = inFile;
    }

    /**
     * 检查邮箱与授权码可否登录
     * @param srv 邮件服务器类型
     * @param srcmail 邮箱
     * @param authstr 授权码
     * @return
     */
    public boolean checkLogin(String srv,String srcmail,String authstr){
        try{
            Socket smtpSocket = new Socket("smtp.qq.com",25);
            smtpSocket.setSoTimeout(timeOut);
            System.out.println(smtpSocket.getLocalPort());

            InputStream inputStream = smtpSocket.getInputStream();
            OutputStream outputStream = smtpSocket.getOutputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            PrintWriter prewritter = new PrintWriter(new OutputStreamWriter(
                    outputStream, StandardCharsets.UTF_8), true);//true很关键

            MyPrintWritter writter = new MyPrintWritter(prewritter,debug,inFile);

            reader.readLine();
            //HELO
            writter.println(SMTPFunction.getHELO(srv));
            reader.readLine();
            //AUTH LOGIN
            writter.println("AUTH LOGIN");
            reader.readLine();
            writter.println(SMTPFunction.getB64(srcmail));
            reader.readLine();
            writter.println(SMTPFunction.getB64(authstr));
           // System.out.println(reader.readLine());

             String authCode = reader.readLine();
            if(authCode.contains("235")){
                System.out.println("Login Checked.");
                return true;
            }else{
                System.out.println(authCode+":login Failed.");
                return false;
            }
        }catch (IOException e){
            e.printStackTrace();
            System.out.println("Exception,login Failed.");
            return false;
        }
    }

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
    public boolean sendMail(
            String srv,String srcmail,String dstmail,
            String authstr,String subject,ArrayList<String> data){
        try{
            Socket smtpSocket = new Socket("smtp.qq.com",25);
            System.out.println(smtpSocket.getLocalPort());

            InputStream inputStream = smtpSocket.getInputStream();
            OutputStream outputStream = smtpSocket.getOutputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            PrintWriter prewritter = new PrintWriter(new OutputStreamWriter(
                    outputStream, StandardCharsets.UTF_8), true);//true很关键

            MyPrintWritter writter = new MyPrintWritter(prewritter,debug,inFile);

            System.out.println(reader.readLine());
            //HELO
            writter.println(SMTPFunction.getHELO(srv));
            System.out.println(reader.readLine());

            //AUTH LOGIN
            writter.println("AUTH LOGIN");
            System.out.println(reader.readLine());
            writter.println(SMTPFunction.getB64(srcmail));
            System.out.println(reader.readLine());
            writter.println(SMTPFunction.getB64(authstr));
            System.out.println(reader.readLine());

            //mail from rcpt to
            //writter.println(SMTPFunction.getMailFrom("123@qq.com"));
            writter.println(SMTPFunction.getMailFrom(srcmail));
            System.out.println(reader.readLine());
            writter.println(SMTPFunction.getRcptTo(dstmail));
            System.out.println(reader.readLine());

            //data
            writter.println("DATA");
            System.out.println(reader.readLine());
            writter.println("subject:"+subject);
            writter.println("from:" + srcmail);
            //writter.println("from:" + "951775489@qq.com");
            writter.println("to:" + dstmail);
            writter.println("Content-Type: text/plain;charset=\"UTF-8\"");//如果发送正文必须加这个，而且下面要有一个空行
            writter.println("");
            for(String line:data){
                writter.println(line);
            }
            writter.println(".");//告诉服务器我发送的内容完毕了

            //很关键，如果不加这一行，服务器是接收不到连续<CR><LF>序列的。猜测是服务器线程安全问题
            Thread.sleep(200);

            writter.println("");
            String sendLine = reader.readLine();
            System.out.println(sendLine);
            //quit
            writter.println("quit");
            String endLine = reader.readLine();
            System.out.println(endLine);

            if(sendLine.contains("250 Ok")){
                return true;
            }
            return false;
        }catch (IOException e){
            e.printStackTrace();
            return false;
        }catch (InterruptedException ex){
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * 测试模块：检查授权码和邮箱是否可以登录，以及发送邮件。
     */
    public static void main(String[] args) {

        String srcmail = "1085455474@qq.com";
        String srv = "qq";
        String dstmail = "1085455474@qq.com";
        String authstr = "jypzxfpwwaqofidc";
        String subject = "测试邮件";
        ArrayList<String> data = new ArrayList<>();
        data.add("这是第一行，首次使用java发送邮件成功！");
        data.add("这是第二行");
        data.add("This is Third line.");

        SMTPClient sc = new SMTPClient(true);
        Boolean checked = sc.checkLogin(srv,srcmail, authstr);
        if(checked == true){
            Boolean isSuccess = sc.sendMail(srv, srcmail, dstmail, authstr, subject, data);
            System.out.println(isSuccess);
        }
    }
}

/**
 * 由于Windows系统和Linux系统默认的换行命令不一致，为了统一\n和\r\n，封装PrintWriter类。
 * 目的：Println函数输出<CR><LF>序列。
 */
class MyPrintWritter{
    public PrintWriter printWriter;
    private Boolean debug = false;
    private Boolean inFile = false;

    /**
     * @param pr 封装的输出器
     * @param debug 调试选项。打开的话，会在控制台输出Print的内容
     */
    public MyPrintWritter(PrintWriter pr,Boolean debug,Boolean inFile){
        this.printWriter = pr;
        this.debug = debug;
        this.inFile = inFile;
    }

    /**
     * 重新以\r\n方式输出字符串
     * @param str 输出的字符串
     */
    public void println(String str){
        if(str == null) str="";
        printWriter.println(str+"\r");
        //printWriter.print(str+"\r\n");//这样写是不行的
        if(debug == true)System.out.println(str);
        if(inFile == true){
            //什么都不做
        }
    }
}
