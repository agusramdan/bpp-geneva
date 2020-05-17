package ramdan.file.bpp.geneva.config;

import lombok.Getter;
import lombok.val;
import lombok.var;
import ramdan.file.bpp.geneva.GenevaUtils;
import ramdan.file.line.token.LineToken;
import ramdan.file.line.token.data.TokenEditable;
import ramdan.file.line.token.filter.RegexMatchRule;

public class RuleSimple {
    @Getter
    final String name; // target
    final RegexMatchRule source;
    final String[] tokens;
    @Getter
    final boolean removeMatch;
    public RuleSimple(String name, String tagname, boolean removeMatch, String ... tokens) {
        this.name = name;
        this.removeMatch = removeMatch;
        source = new RegexMatchRule(tagname);
        this.tokens = tokens;
        for (int i = 0;i<tokens.length;i++){
            tokens[i]= tokens[i].trim();
        }
    }

    public boolean isMatchRule(String chek) {
        return source.isMatchRule(chek);
    }

    public LineToken mapping(LineToken lineToken){
        if(this.tokens.length==0){
            return lineToken.replaceToken(0,this.name);
        }else {
            val target = new TokenEditable(lineToken,this.name);
            var idxTarget = 0;
            for (String idxValueSource : this.tokens) {
                idxTarget++;
                try {
                    target.set(idxTarget,lineToken.get(Integer.parseInt(idxValueSource)));
                } catch (Exception e) {
                    target.set(idxTarget, GenevaUtils.tokenConstant(idxValueSource));
                }
            }
            return target;
        }
    }
}
