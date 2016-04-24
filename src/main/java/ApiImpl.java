import java.util.*;

public class ApiImpl implements Api {
    private Server serv;

    public ApiImpl(Server serv) {
        this.serv = serv;
    }

    public boolean leave() {
        this.serv.leave();
        return true;
    }

    public boolean deleteNode(String addr) {
        serv.deleteNode(addr);
        return true;
    }

    public boolean join(String destAddr) {
        serv.join(this.serv.getAddr(), destAddr);
        return true;
    }

    public boolean appendNode(String addr) {
        serv.setStatus(true);
        serv.appendNode(addr);
        return true;
    }

    public boolean runBully() {
        serv.runBully();
        return true;
    }

    public boolean adventureTime(boolean isAgrawala) {
        serv.adventureTime(isAgrawala);
        return true;
    }

    public boolean setStatus(Boolean status) {
        serv.setStatus(status);
        return true;
    }

    public boolean getStatus() {
        return serv.getStatus();
    }

    public List<String> getNetworkMembers() {
        Set<String> set = serv.getNetworkMembers();
        return new ArrayList<>(set);
    }

    public boolean setMasterAddress(String addr) {
        serv.setMasterAddr(addr);
        return true;
    }

    public boolean haveFun(boolean isAgrawala) {
        serv.haveFun(isAgrawala);
        return true;
    }

    public String getMasterAddress() {
        return serv.getMasterAddr();
    }

    public boolean setMasterString(String s) {
        serv.setMasterString(s);
        return true;
    }

    public boolean appendMasterStringRelease(String sourceAddr) {
        this.serv.appendMasterStringRelease(sourceAddr);
        return true;
    }

    public String getMasterString() {
        return serv.getMasterString();
    }

    public boolean appendMasterStringRequest(String sourceAddr) {
        serv.appendMasterStringRequest(sourceAddr);
        return true;
    }

//    public boolean electionBroadcast(String sourceAddr) {
//        return serv.electionBroadcast(sourceAddr);
//    }

    public boolean setVictoryBroadcasted(boolean status) {
        serv.setVictoryBroadcasted(status);
        return true;
    }

    public boolean agrawalaAppendHandle(Map timestamp) {
        serv.agrawalaAppendHandle(timestamp);
        return true;
    }

    public boolean checkAppend() {
        this.serv.checkAppend();
        return true;
    }

    public boolean checkMasterAvaliability() {
        this.serv.checkMasterAvaliability();
        return true;
    }
}
