package ramdan.file.bpp.geneva.mapping;

import ramdan.file.bpp.geneva.config.GenevaRemoveConfig;
import ramdan.file.line.token.LineToken;
import ramdan.file.line.token.config.ConfigHolder;
import ramdan.file.line.token.data.LineTokenData;

/**
 * ramdan.file.bpp.geneva.mapping.GenevaPairMappingHandler
 */
public class GenevaRemoveHandler extends GenevaMappingHandler {
    private GenevaRemoveConfig config;

    public GenevaRemoveHandler() {
        this(ConfigHolder.getConfig(GenevaRemoveConfig.class));
    }

    public GenevaRemoveHandler(GenevaRemoveConfig config) {
        this.config = config;
    }

    @Override
    protected LineToken matchContent(LineToken lineToken) {
        if (config.match(lineToken.getTagname())) {
            return LineTokenData.EMPTY;
        }
        return  lineToken;
    }

}
