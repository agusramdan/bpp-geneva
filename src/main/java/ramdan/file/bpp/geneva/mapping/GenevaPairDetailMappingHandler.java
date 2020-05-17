package ramdan.file.bpp.geneva.mapping;

import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.val;
import lombok.var;
import ramdan.file.bpp.geneva.config.GenevaPairDetailConfig;
import ramdan.file.bpp.geneva.config.RulePairDetail;
import ramdan.file.line.token.LineToken;
import ramdan.file.line.token.Tokens;
import ramdan.file.line.token.config.ConfigHolder;
import ramdan.file.line.token.data.LineTokenData;
import ramdan.file.line.token.data.MultiLineData;
import ramdan.file.line.token.data.TokenEditable;
import ramdan.file.line.token.handler.Callback;

import java.util.ArrayList;
import java.util.List;

/**
 * ramdan.file.bpp.geneva.mapping.GenevaPairMappingHandler
 */
public class GenevaPairDetailMappingHandler extends GenevaMappingHandler {

    @Setter
    private GenevaPairDetailConfig config;
    private List<DataHolder> activeRules = new ArrayList<>();

    public GenevaPairDetailMappingHandler() {
        this(ConfigHolder.getConfig(GenevaPairDetailConfig.class));
    }

    public GenevaPairDetailMappingHandler(GenevaPairDetailConfig config) {
        this.config = config;
    }

    protected void reset(){
        super.reset();
        activeRules.clear();
    }

    @Override
    public Tokens matchContent(LineToken lineToken) {
        val beforeLineToken = new ArrayList<Tokens>();
        val afterLineToken = new ArrayList<LineToken>();
        var removeToken = false;
        String tagname = lineToken.getTagname();
        var indexNew = activeRules.size();
        config.pairRuleByStart(tagname, new CallbackRulePairDetail(lineToken));
        for (int i = 0; i < activeRules.size() ; i++) {
            val holder = activeRules.get(i);
            val rule = holder.rule;
            if(rule.isMatchStart(tagname) && i <indexNew){
                indexNew--;
                activeRules.remove(i);
                i--;
                if(!holder.data.isEmpty()) {
                    switch (rule.getAction()){
                        case "BEFORE_END":beforeLineToken.addAll(holder.data);break;
                        case "AFTER_END": afterLineToken.addAll(holder.data);break;
                    }
                }
                continue;
            }
            if(rule.isMatchDetail(tagname)){
                removeToken=removeToken||rule.isRemoveMatchDetail();
                val detail = new TokenEditable(lineToken,false);
                detail.copyTokensFrom(holder.template);
                mapping(rule,detail,lineToken);
                detail.set(0,rule.name());
                switch (rule.getAction()){
                    case "BEFORE_END":
                    case "AFTER_END": holder.data.add(detail);break;
                    case "INLINE_BEFORE": beforeLineToken.add(detail);break;
                    case "INLINE_AFTER":afterLineToken.add(detail);break;
                }
            }else if(rule.isMatchContent(tagname)){
                // set template
                mapping(rule,holder.template,lineToken);
            }
            if(rule.isMatchEnd(tagname)){
                activeRules.remove(i);
                i--;
                if(!holder.data.isEmpty()) {
                    switch (rule.getAction()){
                        case "BEFORE_END":beforeLineToken.addAll(holder.data);break;
                        case "AFTER_END": afterLineToken.addAll(holder.data);break;
                    }
                }
            }
        }

        if(!removeToken) {
            beforeLineToken.add(lineToken);
        }
        beforeLineToken.addAll(afterLineToken);
        if(beforeLineToken.isEmpty()){
            return LineTokenData.EMPTY;
        }
        if(beforeLineToken.size()==1){
            return beforeLineToken.get(0);
        }
        return MultiLineData.tokens(beforeLineToken);
    }
    private void mapping(RulePairDetail rule,TokenEditable target, LineToken source){
        rule.mapping(target,source);
    }

    @AllArgsConstructor
    class CallbackRulePairDetail implements Callback<RulePairDetail>{
        private LineToken source;
        @Override
        public void call(RulePairDetail rp) {
            activeRules.add(new DataHolder(rp,source));
        }
    }
    class DataHolder{
        final RulePairDetail rule;
        private final List<TokenEditable> data = new ArrayList<>();
        private final TokenEditable template ;
        public DataHolder(RulePairDetail rule,LineToken source) {
            this.rule = rule;
            this.template=rule.template(source);
        }
    }

}
