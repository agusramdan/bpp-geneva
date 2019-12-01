package ramdan.file.bpp.geneva.mapping;

import lombok.Getter;
import lombok.val;
import ramdan.file.line.token.LineToken;
import ramdan.file.line.token.Tokens;
import ramdan.file.line.token.data.LineTokenData;
import ramdan.file.line.token.data.MultiLineData;
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
    public Tokens process(LineToken lineToken) {
        String tagname = lineToken.getTagname();
        Tokens result;
        if(tagname.startsWith("DOCSTART_")){
            val tokens= new ArrayList<Tokens>();
            if(docStart) {
                result= docStartAlreadyHandle(lineToken);
                docReset1();
                return MultiLineData.merge(result,docStartHandle(lineToken));
            }else {
                result= docStartHandle(lineToken);
            }

            docStart=true;
            return result;
        }else if(tagname.equals("DOCEND")) {
            if(!docStart){
                return docEndNotReadyHandle(lineToken);
            }else {
                result = docEndHandle(lineToken);
                docReset1();
            }
            return result;
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

    protected Tokens docEndNotReadyHandle(LineToken lineToken) {
        return null;
    }

    protected Tokens docEndHandle(LineToken lineToken) {
        return lineToken;
    }

    protected Tokens docStartHandle(LineToken lineToken) {
        return lineToken;
    }

    protected Tokens docStartAlreadyHandle(LineToken lineToken) {
        throw new RuntimeException("Already Doc Start");
    }

}
