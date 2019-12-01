package ramdan.file.bpp.geneva.mapping;

import lombok.Setter;
import lombok.val;
import lombok.var;
import ramdan.file.bpp.geneva.config.GenevaMultiLineConfig;
import ramdan.file.bpp.geneva.config.RuleMultLine;
import ramdan.file.line.token.LineToken;
import ramdan.file.line.token.Tokens;
import ramdan.file.line.token.config.ConfigHolder;
import ramdan.file.line.token.data.LineTokenData;
import ramdan.file.line.token.data.MultiLineData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * ramdan.file.bpp.geneva.mapping.GenevaPairMappingHandler
 */
public class GenevaMultiLineMappingHandler extends GenevaMappingHandler {

    @Setter
    private GenevaMultiLineConfig config;
    private Map<String,DataHolder> activeRules = new HashMap<>();
    public GenevaMultiLineMappingHandler() {
        this(ConfigHolder.getConfig(GenevaMultiLineConfig.class));
    }

    public GenevaMultiLineMappingHandler(GenevaMultiLineConfig config) {
        this.config = config;
    }

    protected void reset(){
        super.reset();
        activeRules.clear();
    }

    @Override
    public Tokens matchContent(LineToken lineToken) {
        val beforeLineToken = new ArrayList<LineToken>();
        //val afterLineToken = new ArrayList<Tokens>();
        var removeToken = false;
        val tagname = lineToken.getTagname();
        val mappings = config.getMappingRule().get(tagname);
        if(mappings==null) return lineToken;

        for (String mapping: mappings){
            var data = activeRules.get(mapping);
            if(data==null){
                val rule = config.getRuleMultLines().get(mapping);
                data = new DataHolder(rule);
                activeRules.put(mapping,data);
            }
            data.add(lineToken);
            if(data.readyRelease()){
                beforeLineToken.add(data.release());
                activeRules.remove(mapping);
            }
            removeToken=removeToken||data.rule.isRemoveMatchDetail();
        }

        if(!removeToken) {
            beforeLineToken.add(0,lineToken);
        }
        if(beforeLineToken.isEmpty()){
            return LineTokenData.EMPTY;
        }
        if(beforeLineToken.size()==1){
            return beforeLineToken.get(0);
        }
        return MultiLineData.tokens(beforeLineToken);
    }
//    private void mapping(RulePairDetail rule,TokenEditable target, LineToken source){
//        rule.mapping(target,source);
//    }

//    @AllArgsConstructor
//    class CallbackRulePairDetail implements Callback<RulePairDetail>{
//        private LineToken source;
//        @Override
//        public void call(RulePairDetail rp) {
//            activeRules.add(new DataHolder(rp,source));
//        }
//    }
    class DataHolder{
        final RuleMultLine rule;
        private final Map<String,LineToken> data = new HashMap<>();
        public DataHolder(RuleMultLine rule) {
            this.rule = rule;
        }
        public void add(LineToken lineToken){
            data.put(lineToken.getTagname(),lineToken);
        }
        public boolean readyRelease(){
            val count = data.entrySet().size();
            val countTagRule = rule.getTagnames().size();
            return "IMMEDIATE".equalsIgnoreCase(rule.getAction())&& count==countTagRule;
        }

        public LineToken release() {
            val list = new ArrayList<LineToken>(data.values());
            val prepare = rule.template(list.get(0));
            for (int i = 1; i < list.size(); i++) {
                rule.mapping(prepare,list.get(i));
            }
            return prepare;
        }
    }

}
