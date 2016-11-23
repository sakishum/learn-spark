package me.migle;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by migle on 2016/7/22.
 */
class Cacher{
    private String host;
    private int port;
    private Map<String,Object> data;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }
}
 class Hashing {
    private final Map<String,Cacher> cmap = new LinkedHashMap<>();
    public Hashing(List<Cacher> cachers){
        for(Cacher c:cachers){
            cmap.put(c.getHost(),c);
        }
    }


}
public class CHash{
    public static void main(String[] args) {

    }
}
