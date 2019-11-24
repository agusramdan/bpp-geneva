package ramdan.file.bpp.geneva.mapping;

import lombok.AllArgsConstructor;
import ramdan.file.bpp.geneva.config.GenevaPairConfig;
import ramdan.file.bpp.geneva.config.RulePair;
import ramdan.file.bpp.geneva.data.TokenEditable;
import ramdan.file.line.token.LineToken;
import ramdan.file.line.token.config.ConfigHolder;
import ramdan.file.line.token.data.LineTokenData;
import ramdan.file.line.token.data.MultiLineData;
import ramdan.file.line.token.handler.Callback;

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
    public LineToken matchContent(LineToken lineToken) {
        String tagname = lineToken.getTagname();
        if (rulePair == null) {
            config.pairRuleByStart(tagname, new CallbackRulePair(lineToken));
        }
        if (rulePair != null) {
            rulePair.mapping(currentRow,lineToken);

            if(rulePair.isMatchEnd(tagname)){
                if(rulePair.isRemoveMatchPair()){
                    lineToken = currentRow;
                }else {
                    lineToken = MultiLineData.merge(lineToken,currentRow);
                }
                currentRow = null;
                rulePair = null;
            }else if(rulePair.isRemoveMatchPair()){
                lineToken = LineTokenData.EMPTY;
            }
        }
        return  lineToken;
    }

    @AllArgsConstructor
    class CallbackRulePair implements Callback<RulePair>{
        private LineToken source;
        @Override
        public void call(RulePair rp) {
            rulePair=rp;
            currentRow= rp.template(source);
        }
    }
}
