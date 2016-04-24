import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

public interface Api {
    List<String> getNetworkMembers();

    boolean join(String destAddr);
    boolean leave();

    boolean appendNode(String addr);
    boolean deleteNode(String addr);

    String getMasterString();
    String getMasterAddress();
    boolean appendMasterStringRequest(String sourceAddr);
    boolean appendMasterStringRelease(String sourceAddr);
    boolean setMasterString(String s);
    boolean setMasterAddress(String s);

    boolean haveFun(boolean isAgrawala);
    boolean adventureTime(boolean isAgrawala);

    boolean setStatus(Boolean status);
    boolean getStatus();

    boolean runBully();
//    boolean electionBroadcast(String sourceAddr);
    boolean setVictoryBroadcasted(boolean status);

    boolean agrawalaAppendHandle(Map timestamp);

    boolean checkAppend();

    boolean checkMasterAvaliability();
}

