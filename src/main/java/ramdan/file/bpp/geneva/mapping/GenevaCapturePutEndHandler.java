package ramdan.file.bpp.geneva.mapping;

import ramdan.file.line.token.LineToken;
import ramdan.file.line.token.Tokens;
import ramdan.file.line.token.data.LineTokenData;
import ramdan.file.line.token.data.MultiLineData;

import java.util.ArrayList;
import java.util.List;

public class GenevaCapturePutEndHandler extends GenevaMappingHandler {

    private String start;
    private String end;
    private boolean remove;

    public GenevaCapturePutEndHandler(String start, String end, boolean remove) {
        this.start = start;
        this.end = end;
        this.remove = remove;
    }

    private boolean capture;
    private List<Tokens> listTokens = new ArrayList<>();
    @Override
    public Tokens process(LineToken lineToken) {

        if(lineToken.isTagname(start)){
            capture=true;
        }
        if(capture){
            listTokens.add(lineToken);
            if(lineToken.isTagname(end)){
                capture=false;
            }
            if(remove){
                return LineTokenData.REMOVE;
            }
        }
        return super.process(lineToken);
    }

    @Override
    protected void reset() {
        super.reset();
        capture=false;
        listTokens = new ArrayList<>();
    }

    @Override
    protected Tokens endTagHandle(LineToken lineToken) {
        listTokens.add(super.endTagHandle(lineToken));
        return MultiLineData.tokens(listTokens);
    }
}
