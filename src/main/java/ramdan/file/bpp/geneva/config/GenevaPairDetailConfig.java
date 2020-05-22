package ramdan.file.bpp.geneva.config;

import ramdan.file.line.token.LineToken;
import ramdan.file.line.token.callback.Callback;
import ramdan.file.line.token.config.Config;
import ramdan.file.line.token.config.ConfigToken;

import java.util.ArrayList;
import java.util.List;

public class GenevaPairDetailConfig extends ConfigToken implements Config {

    private final List<RulePairDetail> rulePairDetails = new ArrayList<>();

    protected void configToken(LineToken lineToken){
        if (lineToken.equalIgnoreCase(0, "CONFIG_MAPPING_PAIR_DETAIL")) {
            rulePairDetails.add(new RulePairDetail(
                    lineToken.get(1),
                    lineToken.get(2),
                    lineToken.get(3),
                    lineToken.get(4),
                    lineToken.get(5),
                    Boolean.valueOf(lineToken.get(6)),
                    lineToken.get(7).split(",")
            ));
        }
    }

    public void  pairRuleByStart(String tagname, Callback<RulePairDetail> callback){
        for (RulePairDetail rp: rulePairDetails) {
            if(rp.isMatchStart(tagname)){
                callback.call(rp);
            }
        }
    }
}
