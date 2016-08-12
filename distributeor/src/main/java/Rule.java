import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by migle on 2016/8/12.
 */
public class Rule implements Serializable{
    //TODO:应该由过滤条件和输出组成
    private String evernid;
    private List<Exp> exp;

    public Rule(String evernid, List<Exp> exp) {
        this.evernid = evernid;
        this.exp = exp;
    }

    public Rule() {
    }

    public boolean rule(Map<String,String> data){
        return Integer.valueOf(data.getOrDefault("fee","0"))>10;
    }

    class Exp{
        String var;
        String op;  //eq\lt\gt\ge\le\in\nin\dan\ro
        String value;


    }
}
