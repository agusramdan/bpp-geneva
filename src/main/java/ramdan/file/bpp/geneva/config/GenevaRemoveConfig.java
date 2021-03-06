package ramdan.file.bpp.geneva.config;

import ramdan.file.line.token.LineToken;
import ramdan.file.line.token.config.Config;
import ramdan.file.line.token.config.ConfigToken;

import java.util.HashSet;
import java.util.Set;

public class GenevaRemoveConfig extends ConfigToken implements Config {
    private Set<String> rulePairs ;
    private String tagname ;

    public GenevaRemoveConfig(String tagname) {
        this.tagname = tagname;
    }

    public GenevaRemoveConfig() {
        this("CONFIG_MAPPING_REMOVE");
    }

    protected void configToken(LineToken lineToken){
        if(rulePairs==null) rulePairs = new HashSet<>();
        if (lineToken.equalIgnoreCase(0, tagname)) {
            rulePairs.add(lineToken.getValue());
        }
    }
    public boolean match(String tagname){
        return false;
    }
}
