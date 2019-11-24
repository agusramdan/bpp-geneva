package ramdan.file.bpp.geneva.mapping;

import lombok.Setter;
import ramdan.file.bpp.geneva.config.GenevaPairCaptureConfig;
import ramdan.file.bpp.geneva.config.RulePairCapture;
import ramdan.file.line.token.LineToken;
import ramdan.file.line.token.config.ConfigHolder;
import ramdan.file.line.token.data.LineTokenData;

/**
 * ramdan.file.bpp.geneva.mapping.GenevaPairMappingHandler
 */
public class GenevaPairCaptureMappingHandler extends GenevaMappingHandler {

    @Setter
    private GenevaPairCaptureConfig config;
    private RulePairCapture rulePairCapture = null;
    public GenevaPairCaptureMappingHandler() {
        config=ConfigHolder.getConfig(GenevaPairCaptureConfig.class);
    }

    public GenevaPairCaptureMappingHandler(GenevaPairCaptureConfig config) {
        this.config = config;
    }

    protected void reset(){
        super.reset();
        rulePairCapture = null;
    }

    @Override
    protected LineToken matchContent(LineToken lineToken) {
        String tagname = lineToken.getTagname();
        if (rulePairCapture == null) {
            if((rulePairCapture = config.getPairRuleByStart(tagname))!=null){
                return lineToken;
            }
           lineToken = LineTokenData.EMPTY;
        } else
        if(rulePairCapture.isMatchEnd(tagname)){
            rulePairCapture =null;
        }
        return  lineToken;
    }


}
