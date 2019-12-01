package ramdan.file.bpp.geneva.config;

import lombok.Getter;
import lombok.val;
import lombok.var;
import ramdan.file.line.token.LineToken;
import ramdan.file.line.token.config.Config;
import ramdan.file.line.token.config.ConfigToken;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GenevaMultiLineConfig extends ConfigToken implements Config {

    @Getter
    private final Map<String,RuleMultLine> ruleMultLines = new HashMap<>();

    @Getter
    private final Map<String, Set<String>> mappingRule = new HashMap<>();

    protected void configToken(LineToken lineToken){
        if (lineToken.equalIgnoreCase(0, "CONFIG_MAPPING_MULTILINE")) {
            val rule = new RuleMultLine(
                    lineToken.get(1),
                    lineToken.get(2),
                    Boolean.valueOf(lineToken.get(3)),
                    lineToken.get(4).split(",")
            );
            ruleMultLines.put(lineToken.getValue(),rule);
            val tagnames=rule.getTagnames();
            for (String tagname: tagnames) {
                var set = mappingRule.get(tagname);
                if(set==null){
                    mappingRule.put(tagname,set = new HashSet<>());
                }
                set.add(rule.getName());
            }
        }
    }


}
