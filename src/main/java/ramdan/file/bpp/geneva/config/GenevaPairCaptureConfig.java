package ramdan.file.bpp.geneva.config;

import ramdan.file.line.token.LineToken;
import ramdan.file.line.token.config.Config;
import ramdan.file.line.token.config.ConfigToken;

import java.util.ArrayList;
import java.util.List;

public class GenevaPairCaptureConfig extends ConfigToken implements Config {

    private final List<RulePairCapture> rulePairCaptures = new ArrayList<>();

    protected void configToken(LineToken lineToken){
        if (lineToken.equalIgnoreCase(0, "CONFIG_MAPPING_PAIR_CAPTURE")) {
            rulePairCaptures.add(new RulePairCapture(
                    lineToken.get(1),
                    lineToken.get(2),
                    lineToken.get(3)
            ));
        }
    }

    public RulePairCapture getPairRuleByStart(String tagname){
        for (RulePairCapture rp: rulePairCaptures) {
            if(rp.isMatchStart(tagname)){
                return rp;
            }
        }
        return null;
    }
}
