package ramdan.file.bpp.geneva.mapping;

import ramdan.file.line.token.LineToken;
import ramdan.file.line.token.config.FileConfigHolder;
import ramdan.file.line.token.filter.DefaultMultiLineTokenFilter;
import ramdan.file.line.token.handler.MappingContentLineTokenHandler;
import ramdan.file.line.token.listener.LineTokenListener;

/**
 * ramdan.file.bpp.geneva.mapping.GenevaPairMappingHandler
 */
public class GenevaMappingHandler extends MappingContentLineTokenHandler {

    private LineTokenListener configListener = new LineTokenListener() {
        @Override
        public void event(LineToken lineToken) {
            configToken(lineToken);
        }
    };
    protected void configToken(LineToken lineToken){
    }
    protected void prepare(){
        FileConfigHolder.read(configListener);
    }

    public GenevaMappingHandler(boolean removeNotMatch, String ... content) {
        super(new DefaultMultiLineTokenFilter("DOC","DOCSTART_\\d","DOCEND",content), removeNotMatch);
        prepare();
    }

    public GenevaMappingHandler() {
        this(false,"\\w*");
    }

    protected void reset(){
        super.reset();
    }
}
