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
    private static OutputStream fileOut;

    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Usage: java MyTLSFileClient <host> <port> <file>");
            return;
        }
        //Grab our input arguments
        String host = args[0];
        Integer port = Integer.parseInt(args[1]);
        String file = args[2];
        try {
            SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            SSLSocket socket = (SSLSocket) factory.createSocket(host, port);

            // Make java use hostname verification
            SSLParameters p = socket.getSSLParameters();
            p.setEndpointIdentificationAlgorithm("https");
            socket.setSSLParameters(p);
            // start our handshake
            try {
                socket.startHandshake();
            } catch (IOException e){
                System.out.println("Handshake Failed!");
                return;
            }
            /*
             * at this point, can getInputStream and
             * getOutputStream as you would a regular Socket
             */
            InputStream inputStream = socket.getInputStream();
            read = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            write = new PrintWriter(socket.getOutputStream(), true);
            //Write our filename to the socket
            write.println(file);
            write.flush();
            //setup our file for writing to
            String saveName = "_" + file;
            fileOut = new FileOutputStream(saveName);

            //Counter to get the length of bytes we read from the socket
            int count;
            //Buffer to read into
            byte[] buffer = new byte[1024];
            //inputStream.read(buffer) returns amount of bytes read, so as long as this keeps reading it will keep writing to the file
            //When it hits null this will exit the loop
            while ((count = inputStream.read(buffer)) > 0) {
                //Write the buffer to file
                fileOut.write(buffer, 0, count);
            }
            System.out.println("File recieved, Saved as: " + saveName);

        } catch (UnknownHostException e) {
            System.out.println("Is the hostname entered correct?");
            // e.printStackTrace();
            return;
        } catch (IOException e) {
            System.out.println("IO Exception!");
            //e.printStackTrace();
            return;
        }
    }
}
