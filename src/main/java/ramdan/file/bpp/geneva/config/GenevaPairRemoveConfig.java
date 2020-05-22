package ramdan.file.bpp.geneva.config;

import ramdan.file.line.token.LineToken;
import ramdan.file.line.token.callback.Callback;
import ramdan.file.line.token.config.Config;
import ramdan.file.line.token.config.ConfigToken;

import java.util.ArrayList;
import java.util.List;

public class GenevaPairRemoveConfig extends ConfigToken implements Config {
    private List<RulePairRemove> rulePairs = new ArrayList<>() ;


    protected void configToken(LineToken lineToken){
        if (lineToken.equalIgnoreCase(0, "CONFIG_MAPPING_PAIR_REMOVE")) {
            rulePairs.add(new RulePairRemove(
                    lineToken.get(1),
                    lineToken.get(2),
                    lineToken.get(3)
            ));
        }
    }

    public boolean  pairRuleByStart(String tagname, Callback<RulePairRemove> callback){
        for (RulePairRemove rp: rulePairs) {
            if(rp.isMatchStart(tagname)){
                callback.call(rp);
                return true;
            }
        }
        return false;
    }
}
