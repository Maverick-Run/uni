import javax.net.ssl.*;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane.SystemMenuBar;

import java.io.*;
import java.security.*;
import java.security.cert.*;
import javax.naming.ldap.*;
import javax.net.*;

public class MyTLSFileServer {
    public static void main(String[] args) {
            /*
             * Get a password input from the console use it in getting our SSF then delete it straight after 
             */
            Console cons;
            char[] passwd;
            ServerSocketFactory ssf = null;
        if ((cons = System.console()) != null && (passwd = cons.readPassword("[%s]", "Password: ")) != null){
            ssf = getSSF(passwd);
            java.util.Arrays.fill(passwd, ' ');
        }
        /*
        * use the getSSF (above) method to get a
        * SSLServerSocketFactory and create our
        * SSLServerSocket, bound to specified port
        */
        SSLServerSocket ss = null;
        try {
            ss =
            (SSLServerSocket) ssf.createServerSocket(40202);
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        String EnabledProtocols[] =
        {"TLSv1.2", "TLSv1.3"};
        ss.setEnabledProtocols(EnabledProtocols);
        System.out.println("Listening on port:" + ss.getLocalPort());
        SSLSocket s = null; 
        try {
            s = (SSLSocket)ss.accept();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        System.out.println("New connection!");
        InputStream clientInput = null;
        try {
            clientInput = s.getInputStream();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            System.out.println("Failed to get input stream?");
            e1.printStackTrace();
        }
        InputStreamReader is = new InputStreamReader(clientInput);
        BufferedReader reader = new BufferedReader(is);
        OutputStream clientOutput = null;

        try {
            clientOutput = s.getOutputStream();
        } catch (IOException e1) {
            System.out.println("Failed to get output stream?");
            e1.printStackTrace();
        }
        PrintWriter writer = new PrintWriter(clientOutput);
        String filename = null;
        try {
            filename = reader.readLine();
            System.out.println("Sending File: " + filename);
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        BufferedInputStream fileInput = null;
        try {
            fileInput = new BufferedInputStream(new FileInputStream(filename));
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        int count = 0;
        byte[] buffer = new byte[1024];
        try {
            while((count = fileInput.read(buffer))>0){
                clientOutput.write(buffer, 0, count);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static ServerSocketFactory getSSF(char[] passPhrase) {
        try{
            /*
            * Get an SSL Context that speaks some version
            * of TLS, a KeyManager that can hold certs in * X.509 format, and a JavaKeyStore (JKS)
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
            
        }catch(KeyStoreException kse){
            System.out.println("KeyStoreException in getSSF()");
            kse.printStackTrace();
        }catch(NoSuchAlgorithmException nsae){
            System.out.println("No Such Algorithm Exception in getSSF()");
            nsae.printStackTrace();
        }catch(IOException ioe){
            System.out.println("IOException in getSSF()");
            ioe.printStackTrace();
        }catch(CertificateException ce){
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
