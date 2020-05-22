package ramdan.file.bpp.geneva.mapping;

import lombok.AllArgsConstructor;
import ramdan.file.bpp.geneva.config.GenevaPairConfig;
import ramdan.file.bpp.geneva.config.RulePair;
import ramdan.file.line.token.LineToken;
import ramdan.file.line.token.Tokens;
import ramdan.file.line.token.callback.Callback;
import ramdan.file.line.token.config.ConfigHolder;
import ramdan.file.line.token.data.LineTokenData;
import ramdan.file.line.token.data.MultiLineData;
import ramdan.file.line.token.data.TokenEditable;

/**
 * ramdan.file.bpp.geneva.mapping.GenevaPairMappingHandler
 */
public class GenevaPairMappingHandler extends GenevaMappingHandler {

    private GenevaPairConfig config;

    //private List<RulePair> rulePairs ;
    private TokenEditable currentRow = null;
    private RulePair rulePair = null;

//    protected void configToken(LineToken lineToken){
//        if(rulePairs==null) rulePairs = new ArrayList<>();
//        if (lineToken.equalIgnoreCase(0, "CONFIG_MAPPING_PAIR")) {
//            rulePairs.add(new RulePair(
//                    lineToken.get(1),
//                    lineToken.get(2),
//                    lineToken.get(3),
//                    Boolean.valueOf(lineToken.get(4)),
//                    lineToken.get(5).split(",")
//            ));
//        }
//    }

    public GenevaPairMappingHandler(GenevaPairConfig config) {
        this.config = config;
    }

    public GenevaPairMappingHandler() {
        this(ConfigHolder.getConfig(GenevaPairConfig.class));

    }

    protected void reset(){
        super.reset();
        currentRow = null;
        rulePair=null;
    }


    @Override
    public Tokens matchContent(LineToken lineToken) {
        String tagname = lineToken.getTagname();
        Tokens result = lineToken;
        if (rulePair == null) {
            config.pairRuleByStart(tagname, new CallbackRulePair(lineToken));
        }
        if (rulePair != null) {
            rulePair.mapping(currentRow,lineToken);

            if(rulePair.isMatchEnd(tagname)){
                if(rulePair.isRemoveMatchPair()){
                    result = currentRow;
                }else {
                    result = MultiLineData.merge(lineToken,currentRow);
                }
                currentRow = null;
                rulePair = null;
            }else if(rulePair.isRemoveMatchPair()){
                result = LineTokenData.EMPTY;
            }
        }
        return  result;
    }

    @AllArgsConstructor
    class CallbackRulePair implements Callback<RulePair> {
        private LineToken source;
        @Override
        public void call(RulePair rp) {
            rulePair=rp;
            currentRow= rp.template(source);
        }
    }
}
