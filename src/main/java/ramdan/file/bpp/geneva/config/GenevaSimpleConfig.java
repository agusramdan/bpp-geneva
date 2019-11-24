package ramdan.file.bpp.geneva.config;

import ramdan.file.line.token.LineToken;
import ramdan.file.line.token.config.Config;
import ramdan.file.line.token.config.ConfigToken;
import ramdan.file.line.token.handler.Callback;

import java.util.ArrayList;
import java.util.List;

public class GenevaSimpleConfig extends ConfigToken implements Config {
    private List<RuleSimple> ruleSimples;

    protected void configToken(LineToken lineToken){
        if(ruleSimples == null) ruleSimples = new ArrayList<>();
        if (lineToken.equalIgnoreCase(0, "CONFIG_MAPPING_SIMPLE")) {
            ruleSimples.add(new RuleSimple(
                    lineToken.get(1),
                    lineToken.get(2),
                    Boolean.valueOf(lineToken.get(3)),
                    lineToken.notEmpty(4)?lineToken.get(4).split(","):new String[0]
            ));
        }
    }
    public void matchRule(String tagname, Callback<RuleSimple> callback){
        for (RuleSimple rp: ruleSimples) {
            if(rp.isMatchRule(tagname)){
                callback.call(rp);
            }
        }
    }
}
