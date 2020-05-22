package ramdan.file.bpp.geneva.config;

import ramdan.file.line.token.LineToken;
import ramdan.file.line.token.callback.Callback;
import ramdan.file.line.token.config.Config;
import ramdan.file.line.token.config.ConfigToken;

import java.util.ArrayList;
import java.util.List;

public class GenevaPairConfig extends ConfigToken implements Config {
    private List<RulePair> rulePairs = new ArrayList<>() ;


    protected void configToken(LineToken lineToken){
        if (lineToken.equalIgnoreCase(0, "CONFIG_MAPPING_PAIR")) {
            rulePairs.add(new RulePair(
                    lineToken.get(1),
                    lineToken.get(2),
                    lineToken.get(3),
                    Boolean.valueOf(lineToken.get(4)),
                    lineToken.get(5).split(",")
            ));
        }
    }

    public void  pairRuleByStart(String tagname, Callback<RulePair> callback){
        for (RulePair rp: rulePairs) {
            if(rp.isMatchStart(tagname)){
                callback.call(rp);
                break;
            }
        }
    }
}
