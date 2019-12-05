import java.io.*;
import java.net.*;
import java.util.*;

public class EchoServer {
    public static void main(String[] args) throws IOException{
        try(ServerSocket s = new ServerSocket(8888)){
            try(Socket incoming = s.accept()){
                InputStream inStream = incoming.getInputStream();
                OutputStream outStream = incoming.getOutputStream();

                try(Scanner in = new Scanner(inStream,"UTF-8")){
                    PrintWriter out = new PrintWriter(
                            new OutputStreamWriter(outStream,"UTF-8"),
                            true
                    );

                    out.println("Hello From Server. You have Connected,Send BYE to Quit.");

                    boolean done = false;

                    while(!done && in.hasNextLine()){
                        String line = in.nextLine();
                        out.println("Echo : "+line);
                        if(line.trim().equals("BYE"))done = true;
                    }
                }
            }
        }
    }
}
