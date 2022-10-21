import javax.net.ssl.*;
import java.io.*;
import java.net.UnknownHostException;
import java.security.*;
import java.security.cert.*;
import javax.naming.ldap.*;
import javax.net.*;

public class MyTLSFileClient {
    private static BufferedReader read;
    private static PrintWriter write;
    public static void main(String[] args){
        if (args.length != 3){
            System.out.println("Usage: java MyTLSFileClient <host> <port> <file>");
            return;
        }

        String host = args[0];
        Integer port = Integer.parseInt(args[1]);
        String file = args[2];
        try {
            SSLSocketFactory factory = (SSLSocketFactory)SSLSocketFactory.getDefault();
            SSLSocket socket =
            (SSLSocket)factory.createSocket(host, port);
            SSLParameters p = socket.getSSLParameters();
            p.setEndpointIdentificationAlgorithm("https");
            socket.setSSLParameters(p);
            socket.startHandshake();
        /*
        * at this point, can getInputStream and
        * getOutputStream as you would a regular Socket
        */

        read = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        write = new PrintWriter(socket.getOutputStream(), true);
        write.println(file);
        write.flush();
        while (true){
            String in = read.readLine();
            if (in == null ){break;}
            System.out.println(in);
        }
            
        } catch (UnknownHostException e) {
            // TODO: handle exception
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
