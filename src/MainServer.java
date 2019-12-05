import java.io.*;
import java.net.*;
import java.util.*;

public class MainServer {
    private static MainServer ourInstance = new MainServer();

    public static MainServer getInstance() {
        return ourInstance;
    }

    private MainServer() {
    }
}
