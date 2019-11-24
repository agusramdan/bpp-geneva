package ramdan.file.bpp.geneva.mapping;

import lombok.Setter;
import ramdan.file.bpp.geneva.config.GenevaPairRemoveConfig;
import ramdan.file.bpp.geneva.config.RulePairRemove;
import ramdan.file.line.token.LineToken;
import ramdan.file.line.token.config.ConfigHolder;
import ramdan.file.line.token.data.LineTokenData;
import ramdan.file.line.token.handler.Callback;

/**
 * ramdan.file.bpp.geneva.mapping.GenevaPairMappingHandler
 */
public class GenevaPairRemoveHandler extends GenevaMappingHandler {

    @Setter
    private GenevaPairRemoveConfig config;
    private final CallbackRulePairRemove callback = new CallbackRulePairRemove();
    //private List<RulePairRemove> rulePairRemoves;
    private RulePairRemove rulePairRemove = null;
//    protected void configToken(LineToken lineToken){
//        if(rulePairRemoves ==null) rulePairRemoves = new ArrayList<>();
//        if (lineToken.equalIgnoreCase(0, "CONFIG_MAPPING_PAIR_REMOVE")) {
//            rulePairRemoves.add(new RulePairRemove(
//                    lineToken.get(1),
//                    lineToken.get(2),
//                    lineToken.get(3)
//            ));
//        }
//    }

    public GenevaPairRemoveHandler(GenevaPairRemoveConfig config) {
        this.config = config;
    }

    public GenevaPairRemoveHandler() {
        this(ConfigHolder.getConfig(GenevaPairRemoveConfig.class));
    }

    protected void reset(){
        super.reset();
        rulePairRemove = null;
    }

    @Override
    protected LineToken matchContent(LineToken lineToken) {
        String tagname = lineToken.getTagname();
        if (rulePairRemove == null) {
            if(config.pairRuleByStart(tagname, callback)){
                return LineTokenData.EMPTY;
            }
        }else{
            if(rulePairRemove.isMatchEnd(tagname)){
                rulePairRemove =null;
            }
            return LineTokenData.EMPTY;
        }
        return  lineToken;
    }

    class CallbackRulePairRemove implements Callback<RulePairRemove>{

        @Override
        public void call(RulePairRemove rp) {
            rulePairRemove = rp;
        }
    }
}
