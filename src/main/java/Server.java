import org.apache.log4j.Logger;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcServer;
import org.apache.xmlrpc.server.XmlRpcServerConfigImpl;
import org.apache.xmlrpc.webserver.WebServer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

public class Server implements Runnable {
    Logger  logger = Logger.getLogger("logger");
    private String addr;
    private boolean status = false;
    private Set<String> networkMembers;
    private String masterAddr = "";
    private String masterString = "";
    private Clock clock;
    private boolean isVictoryBroadcasted = false;
    private boolean isInterestedInMasterString = false;
    private List<String> wordlistToCheck = new ArrayList<>();

    private Queue<String> stringQueue;

    public String getMasterAddr() {
        return masterAddr;
    }

    public void setMasterAddr(String masterAddr) {
        this.masterAddr = masterAddr;
    }

    public Server(int port) {
        this.addr = Util.getOwnIp() + ":" + port;
        networkMembers = new TreeSet<>();
        networkMembers.add(this.addr);
        this.stringQueue = new LinkedList<>();

        this.clock = new Clock(addr);
        try {
            WebServer server = new WebServer(port);
            XmlRpcServer xmlRpcServer = server.getXmlRpcServer();
            PropertyHandlerMapping phm = new PropertyHandlerMapping();
            Api api = new ApiImpl(this);
            phm.setRequestProcessorFactoryFactory(new ApiFactoryPlant(api));
            phm.setVoidMethodEnabled(true);
            phm.addHandler("Api", ApiImpl.class);
            xmlRpcServer.setHandlerMapping(phm);
            XmlRpcServerConfigImpl serverConfig = (XmlRpcServerConfigImpl)
                    xmlRpcServer.getConfig();
            serverConfig.setEnabledForExtensions(true);
            serverConfig.setContentLengthOptional(false);
            server.start();
        } catch (XmlRpcException | IOException e) {
            e.printStackTrace();
        }
    }

    public void join(String sourceAddr, String destAddr) {
        //ugly but quick workaround
        String[] ip_port = destAddr.split(":");
        if(ip_port[0].equals("127.0.0.1") || ip_port[0].equals("localhost")) {
            destAddr = Util.getOwnIp() + ":" + ip_port[1];
        }

        Api destApi = Util.getNodeByIpAndPort(destAddr);
        this.networkMembers.addAll(destApi.getNetworkMembers());

        for (String addr:this.networkMembers) {
            if(!this.addr.equals(addr)) {
                Util.getNodeByIpAndPort(addr).appendNode(sourceAddr);
            }
        }

        if(destApi.getMasterAddress().equals("")) {
            runBully();
            for(String node:this.networkMembers) {
                Util.getNodeByIpAndPort(node).setVictoryBroadcasted(false);
            }
        } else {
            masterAddr = destApi.getMasterAddress();
        }
    }

    public void cleanUp(String nodeAddr) {
        for (String addr: this.networkMembers) {
           Util.getNodeByIpAndPort(addr).deleteNode(nodeAddr);
        }
    }

    public void appendNode(String addr) {
        System.out.println("Adding node " + addr);
        this.networkMembers.add(addr);
    }

    public String getAddr() {
        return addr;
    }

    public void deleteNode(String addr) {
        if(this.networkMembers.contains(addr)) {
            networkMembers.remove(addr);
        }
        if(this.networkMembers.size() == 1) {
            this.status = false;
        }
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public boolean getStatus() {
        return this.status;
    }

    public Set<String> getNetworkMembers() {
        return networkMembers;
    }

    public void setNetworkMembers(Set<String> nodes) {
        this.networkMembers = nodes;
    }

    public void run() {
        logger.warn("Started serv on address " + this.addr);
    }

    public void haveFun(boolean isAgrawala) {
        System.out.println("Having fun here");
        this.wordlistToCheck = new ArrayList<>();
        long stTime = System.nanoTime();
        Api master = Util.getNodeByIpAndPort(masterAddr);
        while (System.nanoTime() - stTime < 20000000000L) {
            try {
                Thread.sleep((long) (Math.random() * 20000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            String w = Util.getRandomWord();
            this.wordlistToCheck.add(w);
            if(!isAgrawala) {
                logger.warn("Sent request for access critical section");
                master.appendMasterStringRequest(this.addr);
                logger.warn("Entering critical section");
                String ms = master.getMasterString();
                logger.warn("Got master string " + ms);
                master.setMasterString(ms+w);
                logger.warn("Appending word " + w);
                master.appendMasterStringRelease(this.addr);
                logger.warn("Left critical section");
            } else {
                agrawalaAppendRequest(w);
            }
        }
    }

    public void checkMasterAvaliability() {
        //checking if master is alive
        try {
            Api master = Util.getNodeByIpAndPort(this.masterAddr);
            master.getStatus();
        } catch(Exception e) {
            e.printStackTrace();
            this.networkMembers.remove(this.masterAddr);
            cleanUp(this.masterAddr);
            runBully();
        }
    }

    public List<String> getWordlistToCheck() {
        return this.wordlistToCheck;
    }

    public void adventureTime(boolean isAgrawala) {
        this.checkMasterAvaliability();

        List<Thread> threads = new ArrayList<>();
        System.out.println(this.networkMembers.toString());
        for(String addr:this.networkMembers) {
            Thread t = new Thread(new Runnable() {
                public void run() {
                    Util.getNodeByIpAndPort(addr).haveFun(isAgrawala);
                }
            });
            threads.add(t);
            t.start();
        }
        for(Thread t:threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        for(String node:this.networkMembers) {
            Api a = Util.getNodeByIpAndPort(node);
            a.checkAppend();
        }
    }

    public void checkAppend() {
        String ms = this.getMasterString();
        logger.warn("Node: " + this.addr + ", master string: " + ms);
        List<String> failedWords = new ArrayList<>();
        for(String word:this.wordlistToCheck) {
            if(!ms.contains(word)) {
                failedWords.add(word);
            }
        }
        if(failedWords.isEmpty()) {
            logger.warn("All the appended words are in the master string: " + this.wordlistToCheck.toString());
        } else {
            logger.warn("The following words were not appended into master string" + failedWords.toString());
        }
    }

    //P broadcasts an election message (inquiry) to all other processes with higher process IDs, expecting an "I am alive" response from them if they are alive.
    //If P hears from no process with a higher process ID than it, it wins the election and broadcasts victory.
    //If P hears from a process with a higher ID, P waits a certain amount of time for any process with a higher ID to broadcast itself as the leader. If it does not receive this message in time, it re-broadcasts the election message.
    //If P gets an election message (inquiry) from another process with a lower ID it sends an "I am alive" message back and starts new elections.
    //Every time we use runBully, we need to clean up victoryBroadcasted variable
    public void runBully() {
        int electionSleepTime = 1000; //in ms
        boolean isThereAnybodyHere = false;
        List<Thread> threads = new ArrayList<>();

        for (String addr:networkMembers) {
            if(addr.compareTo(this.addr) > 0) {
                try {
                    Thread t1 = new Thread(new Runnable() {
                        public void run() {
                            Util.getNodeByIpAndPort(addr).runBully();
                        }
                    });
                    t1.start();
                    threads.add(t1);
                    isThereAnybodyHere = true;
                    //isThereAnybodyHere = Util.getNodeByIpAndPort(addr).electionBroadcast(addr);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        for (Thread t:threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (isThereAnybodyHere) {
            try {
                Thread.sleep(electionSleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(!isVictoryBroadcasted) {
                runBully();
            }
        } else {
            for (String addr:networkMembers) {
                Util.getNodeByIpAndPort(addr).setVictoryBroadcasted(true);
            }
            setMaster(addr);
        }
    }

//    public boolean electionBroadcast(String sourceAddr) {
//        Thread t1 = new Thread(new Runnable() {
//            public void run() {
//                runBully();
//            }
//        });
//        t1.start();
//        return true;
//    }

    public void setMaster(String newIp) {
        logger.warn("New master elected with addr " + newIp);
        //get old params
        String oldString = "";

        try {
            Api master = Util.getNodeByIpAndPort(this.masterAddr);
            master.getStatus();
            Api oldMaster = Util.getNodeByIpAndPort(this.masterAddr);
            oldString = oldMaster.getMasterString();
            oldMaster.setMasterString("");
        } catch (Exception e) {
           //e.printStackTrace();
        }

        //find new master
        Api newMaster = Util.getNodeByIpAndPort(newIp);
        newMaster.setMasterString(oldString);

        //set master ip for others
        for (String addr:networkMembers) {
            Api a = Util.getNodeByIpAndPort(addr);
            a.setMasterAddress(newIp);
        }
    }

    public String getMasterString() {
        if(masterAddr.equals(addr)) {
            return masterString;
        } else {
            return Util.getNodeByIpAndPort(masterAddr).getMasterString();
        }
    }

    public boolean setMasterString(String s) {
        this.masterString = s;
        return true;
    }

    public void appendMasterStringRequest(String sourceAddr) {
        this.stringQueue.add(sourceAddr);
        while(!this.stringQueue.peek().equals(sourceAddr)) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void appendMasterStringRelease(String sourceAddr) {
        if(this.stringQueue.peek().equals(sourceAddr)) {
            stringQueue.poll();
        }
    }

    public void appendMasterString(String word) {
        this.masterString+=word;
        logger.warn("Master string changed. Now looks like: " + this.masterString);
    }

    public void agrawalaAppendRequest(String word) {
        clock.tick();
        List<Thread> threads = new ArrayList<>();
        for(String addr:networkMembers) {
            if (!addr.equals(this.addr)) {
                clock.tick();
                Map cl = this.clock.getVal();
                Thread t = new Thread(new Runnable() {
                    public void run() {
                        Util.getNodeByIpAndPort(addr).agrawalaAppendHandle(cl);
                    }
                });
                t.start();
                threads.add(t);
            }
        }
        for(Thread t: threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        isInterestedInMasterString = true;
        logger.warn("Node is in critical section");
        clock.tick();
        Api master = Util.getNodeByIpAndPort(masterAddr);
        logger.warn("Want to append word " + word);
        clock.tick();
        String ms = master.getMasterString();
        logger.warn("Got master string:" + ms);
        clock.tick();
        master.setMasterString(ms+word);
        clock.tick();
        isInterestedInMasterString = false;
        logger.warn("Left critical section");
        clock.tick();
    }

    public void agrawalaAppendHandle(Map timestamp) {
        clock.tick();
        if((int) clock.getVal().get("val") < (int) timestamp.get("val")) {
            clock.setClock((int) timestamp.get("val") + 1);
        } else {
            clock.tick();
        }

        while (isInterestedInMasterString && this.clock.compareTo(timestamp) < 0) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        clock.tick();
    }
//send if not interested or lower priority
//not send if not in
    public void setVictoryBroadcasted(boolean status) {
        isVictoryBroadcasted = status;
    }

    public void leave() {
        this.networkMembers.remove(this.addr);
        this.status = false;
        cleanUp(this.addr);
        //ugly workaround
        if(this.masterAddr.equals(this.addr) && this.networkMembers.size() > 1) {
            Util.getNodeByIpAndPort((String) this.networkMembers.toArray()[0]).runBully();
        }
        for(String node:this.networkMembers) {
            Util.getNodeByIpAndPort(node).setVictoryBroadcasted(false);
        }
        this.networkMembers = new TreeSet<>();
        this.networkMembers.add(this.addr);
        this.masterAddr = "";
        this.masterString = "";
        this.isVictoryBroadcasted = false;
    }
}