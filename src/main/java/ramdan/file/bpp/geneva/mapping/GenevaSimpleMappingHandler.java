package ramdan.file.bpp.geneva.mapping;

import lombok.Getter;
import lombok.val;
import lombok.var;
import ramdan.file.bpp.geneva.row.TokenEditable;
import ramdan.file.bpp.geneva.row.TokenMapping;
import ramdan.file.line.token.LineToken;
import ramdan.file.line.token.data.LineTokenData;
import ramdan.file.line.token.data.MultiLineData;
import ramdan.file.line.token.filter.RegexMatchRule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ramdan.file.bpp.geneva.mapping.GenevaPairMappingHandler
 */
public class GenevaSimpleMappingHandler extends GenevaMappingHandler {

    @Getter
    private String decimal = "";
    private List<RuleSimple> rulePairs;

    protected void configToken(LineToken lineToken){
        if(rulePairs == null) rulePairs = new ArrayList<>();

        if (lineToken.containIgnoreCase(0, "CONFIG_MAPPING_SIMPLE")) {
            rulePairs.add(new RuleSimple(
                    lineToken.get(1),
                    lineToken.get(2),
                    Boolean.valueOf(lineToken.get(3)),
                    lineToken.notEmpty(4)?lineToken.get(4).split(","):new String[0]
            ));
        }
    }

    public GenevaSimpleMappingHandler() {
        super();
    }

    protected void reset(){
        super.reset();
    }


    @Override
    public LineToken process(LineToken lineToken) {
        val result = new ArrayList<LineToken>();
        var removeMatch = false;
        String tagname = lineToken.getTagname();
        for (RuleSimple rp: rulePairs) {
            if(rp.source.isMatchRule(tagname)){
                result.add(tokenMapping(rp,lineToken));
                removeMatch = removeMatch||rp.removeMatch;
            }
        }
        if(!removeMatch){
            result.add(0,lineToken);
        }
        return  MultiLineData.newInstance(result);
    }

    public LineToken tokenMapping(RuleSimple ruleSimple,LineToken lineToken){
        if(ruleSimple.tokens.length==0){
            return lineToken.replaceToken(0,ruleSimple.name);
        }else {
            val target = new TokenMapping(lineToken);
            target.set(0,ruleSimple.name);
            var idxTarget = 1;
            for (String idxValueSource : ruleSimple.tokens) {
                idxTarget++;
                try {
                    target.map(Integer.parseInt(idxValueSource), idxTarget);
                } catch (Exception e) {
                    target.set(idxTarget, idxValueSource);
                }
            }
            return target;
        }
    }

    private static class RuleSimple {
        final String name; // target
        final RegexMatchRule source;
        final String[] tokens;
        final boolean removeMatch;
        RuleSimple(String name, String tagname, boolean removeMatch, String ... tokens) {
            this.name = name;
            this.removeMatch = removeMatch;
            source = new RegexMatchRule(tagname);
            this.tokens = tokens;
        }
    }

}
