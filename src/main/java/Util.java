import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.XmlRpcClientException;
import org.apache.xmlrpc.client.util.ClientFactory;

import java.io.*;
import java.net.*;
import java.util.*;

public class Util {

    public static String getRandomWord() {
        //we have 1000 words in a file
        int r = (int) (Math.random()*999);
        Scanner sc = null;
        try {
            sc = new Scanner(new File("top.txt")).useDelimiter("\n");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        while(r >= 0) {
            sc.next();
            r--;
        }
        return sc.next().replace("\r","");
    }

    public static Api getNodeByIpAndPort(String addr) {
        URL serverUrl = null;
        try {
            serverUrl = new URL("http://" + addr + "/RPC2");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        XmlRpcClient serv = new XmlRpcClient();

        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        config.setServerURL(serverUrl);
        config.setEnabledForExtensions(true);
        config.setConnectionTimeout(60 * 1000);
        config.setReplyTimeout(60 * 1000);
        serv.setConfig(config);
        ClientFactory factory = new ClientFactory(serv);
        return (Api) factory.newInstance(Api.class);
    }

    public static String getOwnIp() {
        String res = null;
        try {
            res =  Collections.list(NetworkInterface.getNetworkInterfaces()).stream()
                    .flatMap(i -> Collections.list(i.getInetAddresses()).stream())
                    .filter(ip -> ip instanceof Inet4Address && ip.isSiteLocalAddress())
                    .findFirst().orElseThrow(RuntimeException::new)
                    .getHostAddress();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return res;
    }
}
