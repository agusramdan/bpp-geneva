package ramdan.file.bpp.geneva.mapping;

import lombok.Getter;
import lombok.val;
import ramdan.file.line.token.LineToken;
import ramdan.file.line.token.data.LineTokenData;
import ramdan.file.line.token.filter.MultiLineTokenFilter;
import ramdan.file.line.token.filter.RegexMatchRule;
import ramdan.file.line.token.handler.MappingContentLineTokenHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GenevaDocLineTokenHandler extends MappingContentLineTokenHandler {

    @Getter
    private String decimal = "";
    @Getter
    private boolean docStart = false;
    private boolean skip = false;
    private RegexMatchRule tagnameValueGetData  ;
    private List<LineToken> lineTokens = new ArrayList<>();
    private Map<String,String> tagNameValue = new HashMap<>();

    public GenevaDocLineTokenHandler(MultiLineTokenFilter filter, boolean removeNotMatch) {
        this(null,filter, removeNotMatch);
    }

    public GenevaDocLineTokenHandler(MultiLineTokenFilter filter) {
        this(null,filter,false);
    }

    public String getValue(String tag){
        val value = tagNameValue.get(tag);
        return value==null?"":value;
    }
    public GenevaDocLineTokenHandler(RegexMatchRule tagnameValueGetData,MultiLineTokenFilter filter, boolean removeNotMatch) {
        super(filter, removeNotMatch);
        if(tagnameValueGetData ==null){
            tagnameValueGetData = new RegexMatchRule("CUSTOMERREF|CUSTOMERTYPE|ACCOUNTNO|BILLREF");
        }
        this.tagnameValueGetData = tagnameValueGetData;
    }
    protected void docReset(){

    }
    private void docReset1(){
        reset();
        docReset();
        decimal=".";
        docStart=false;
        skip=false;
        lineTokens.clear();
        tagNameValue.clear();
    }

    protected void skip(){
        skip=true;
    }
    @Override
    public LineToken process(LineToken lineToken) {
        String tagname = lineToken.getTagname();
        if(tagname.startsWith("DOCSTART_")){
            if(docStart){
                lineToken= docStartAlreadyHandle(lineToken);
                docReset();
            }
            lineToken = docStartHandle(lineToken);
            docStart=true;
            return lineToken;
        }else if(tagname.equals("DOCEND")) {
            if(!docStart){
                return docEndNotReadyHandle(lineToken);
            }
            lineToken = docEndHandle(lineToken);
            docReset1();
            return lineToken;
        }
        if(docStart && !skip){
            if(tagnameValueGetData.accept(lineToken)){
                lineTokens.add(lineToken);
                tagNameValue.put(lineToken.getTagname(),lineToken.getValue());
            }
            return super.process(lineToken);
        }
        return LineTokenData.EMPTY;
    }

    protected LineToken docEndNotReadyHandle(LineToken lineToken) {
        return lineToken;
    }

    protected LineToken docEndHandle(LineToken lineToken) {
        return lineToken;
    }

    protected LineToken docStartHandle(LineToken lineToken) {
        return lineToken;
    }

    protected LineToken docStartAlreadyHandle(LineToken lineToken) {
        throw new RuntimeException("Already Doc Start");
    }

}
