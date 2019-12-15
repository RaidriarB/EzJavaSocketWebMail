import java.io.*;
import java.net.Socket;

public class SMTPMain {
    public static void main(String[] args) {
        String sender = "1085455474@qq.com";
        String receiver = "1085455474@qq.com";
        String password = "computer";
        String user = "MTA4NTQ1NTQ3NEBxcS5jb20=";
        String pass = "dmFkdmhhbGxscXhpaWdpZQ==";
        try {
            Socket socket = new Socket("smtp.qq.com", 25);
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            PrintWriter writter = new PrintWriter(outputStream, true);  //我TM去 这个true太关键了!
            System.out.println(reader.readLine());
            //HELO
            writter.println("HELO huan");
            System.out.println(reader.readLine());
            //AUTH LOGIN
            writter.println("auth login");
            System.out.println(reader.readLine());
            writter.println(user);
            System.out.println(reader.readLine());
            writter.println(pass);
            System.out.println(reader.readLine());
            //Set mail from   and   rcpt to
            writter.println("mail from:<" + sender +">");
            System.out.println(reader.readLine());
            writter.println("rcpt to:<" + receiver +">");
            System.out.println(reader.readLine());

            //Set data
            writter.println("data");
            System.out.println(reader.readLine());
            writter.println("subject:女神，是我");
            writter.println("from:" + sender);
            writter.println("to:" + receiver);
            writter.println("Content-Type: text/plain;charset=\"gb2312\"");
            writter.println();
            writter.println("女神，晚上可以共进晚餐吗？");
            writter.println(".");
            writter.println("");
            System.out.println(reader.readLine());

            //Say GoodBye
            writter.println("rset");
            System.out.println(reader.readLine());
            writter.println("quit");
            System.out.println(reader.readLine());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}