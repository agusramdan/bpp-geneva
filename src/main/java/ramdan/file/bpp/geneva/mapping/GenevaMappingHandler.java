package ramdan.file.bpp.geneva.mapping;

import lombok.Getter;
import lombok.val;
import lombok.var;
import ramdan.file.bpp.geneva.row.TokenEditable;
import ramdan.file.line.token.LineToken;
import ramdan.file.line.token.config.FileConfigHolder;
import ramdan.file.line.token.data.LineTokenData;
import ramdan.file.line.token.data.MultiLineData;
import ramdan.file.line.token.filter.DefaultMultiLineTokenFilter;
import ramdan.file.line.token.filter.SimpleMultiLineTokenFilter;
import ramdan.file.line.token.handler.MappingContentLineTokenHandler;
import ramdan.file.line.token.listener.LineTokenListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        super(new DefaultMultiLineTokenFilter("DOC","DOCSTART_\\d","DOCEND","\\w*"), removeNotMatch);
        prepare();
    }

    public GenevaMappingHandler() {
        this(false,"\\w*");
    }

    protected void reset(){
        super.reset();
    }
}
