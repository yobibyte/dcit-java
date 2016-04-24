import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yobibyte on 02/01/16.
 */
//extended lamport clock
public class Clock implements Comparable {
    String id;
    int val;

    public Clock(String id) {
        this.id = id;
        this.val = 1;
    }

    public Map getVal() {
        Map res = new HashMap<>();
        res.put("id", id);
        res.put("val", val);
        return res;
    }

    public void tick() {
        this.val+=1;
    }

    public void setClock(int val) {
        this.val = val;
    }

    @Override
    public int compareTo(Object o) {
        Map c = (HashMap) o;
        int cval = (int) c.get("val");
        String cid = (String) c.get("id");

        if(val == cval && id.compareTo(cid) == 0) {
            return 0;
        }

        if(val == cval) {
            if(id.compareTo(cid) > 0) {
                return 1;
            } else {
                return -1;
            }
        }

        if(val < cval) {
            return -1;
        } else {
            return 1;
        }
    }
}
