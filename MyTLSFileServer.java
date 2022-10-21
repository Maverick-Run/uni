import javax.net.ssl.*;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane.SystemMenuBar;

import java.io.*;
import java.security.*;
import java.security.cert.*;
import javax.naming.ldap.*;
import javax.net.*;

public class MyTLSFileServer {

    static OutputStream clientOutput = null;
    static BufferedInputStream fileInput = null;
    static SSLSocket s = null;
    static SSLServerSocket ss = null;
    static InputStream clientInput = null;
    static String filename = null;

    public static void main(String[] args) {
        /*
         * Get a password input from the console use it in getting our SSF then delete
         * it straight after
         */
        Console cons;
        char[] passwd;
        ServerSocketFactory ssf = null;
        if ((cons = System.console()) != null && (passwd = cons.readPassword("[%s]", "Password: ")) != null) {
            //use password we got
            ssf = getSSF(passwd);
            //empty our password array now
            java.util.Arrays.fill(passwd, ' ');
        }
        /*
         * use the getSSF (above) method to get a
         * SSLServerSocketFactory and create our
         * SSLServerSocket, bound to specified port
         */
        try {
            ss = (SSLServerSocket) ssf.createServerSocket(40202);
        } catch (Exception e){
            System.out.println("Error creating socket!");
            return;
        }
        //Setup the SSL Protocols we want to use (For our case, 1.2 & 1.3)
        String EnabledProtocols[] = { "TLSv1.2", "TLSv1.3" };
        ss.setEnabledProtocols(EnabledProtocols);
        System.out.println("Listening on port:" + ss.getLocalPort());
        //Accept the incoming connection
        try {
            s = (SSLSocket) ss.accept();
        } catch (IOException e1) {
            System.out.println("Handshake failed!");
            //e1.printStackTrace();
            return;
        }
        System.out.println("New connection!");
        //Get our streams from the connected client
        try {
            clientInput = s.getInputStream();
            clientOutput = s.getOutputStream();
        } catch (IOException e1) {
            System.out.println("Failed to get i/o streams from the socket!");
            //e1.printStackTrace();
            return;
        }
        //Setup the readers of the socket
        InputStreamReader is = new InputStreamReader(clientInput);
        BufferedReader reader = new BufferedReader(is);

        try {
            //Read in the filename (Should be the first thing sent to us)
            filename = reader.readLine();
            File readFile = new File(filename);
            fileInput = new BufferedInputStream(new FileInputStream(readFile));
        } catch (FileNotFoundException e) {
                System.out.print("File not found! Exiting");
                return;
        } catch (IOException e1) {
            System.out.println("Error Reading the filename? Has the connection closed?");
            return;
        }

        int count = 0;
        byte[] buffer = new byte[1024];
        try {
            while ((count = fileInput.read(buffer)) > 0) {
                clientOutput.write(buffer, 0, count);
            }
        } catch (IOException e) {
            System.out.println("Error in read/write loop!");
            //e.printStackTrace();
            return;
        }
    }

    private static ServerSocketFactory getSSF(char[] passPhrase) {
        try {
            /*
             * Get an SSL Context that speaks some version
             * of TLS, a KeyManager that can hold certs in * X.509 format, and a
             * JavaKeyStore (JKS)
             * instance
             */
            SSLContext ctx = SSLContext.getInstance("TLS");
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            KeyStore ks = KeyStore.getInstance("JKS");
            /*
             * load the keystore file. The passhrase is
             * an optional parameter to allow for integrity
             * checking of the keystore. Could be null
             */
            ks.load(new FileInputStream("server.jks"), passPhrase);
            /*
             * init the KeyManagerFactory with a source
             * of key material. The passphrase is necessary
             * to unlock the private key contained.
             */
            kmf.init(ks, passPhrase);
            /*
             * initialise the SSL context with the keys.
             */
            ctx.init(kmf.getKeyManagers(), null, null);
            /*
             * get the factory we will use to create
             * our SSLServerSocket
             */
            SSLServerSocketFactory ssf = ctx.getServerSocketFactory();
            return ssf;

        } catch (KeyStoreException kse) {
            System.out.println("KeyStoreException in getSSF()");
            kse.printStackTrace();
        } catch (NoSuchAlgorithmException nsae) {
            System.out.println("No Such Algorithm Exception in getSSF()");
            nsae.printStackTrace();
        } catch (IOException ioe) {
            System.out.println("IOException in getSSF()");
            ioe.printStackTrace();
        } catch (CertificateException ce) {
            System.out.println("CertificateException in getSSF()");
            ce.printStackTrace();
        } catch (UnrecoverableKeyException uke) {
            System.out.println("UnrecoverableKeyException in getSSF()");
            uke.printStackTrace();
        } catch (KeyManagementException kme) {
            System.out.println("KeyManagementException in getSSF()");
            kme.printStackTrace();
        }
        return null;
    }
}
