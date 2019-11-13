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

import java.util.*;

/**
 * ramdan.file.bpp.geneva.mapping.GenevaPairMappingHandler
 */
public class GenevaPairMappingHandler extends GenevaMappingHandler {

    @Getter
    private String decimal = "";
    private List<RulePair> rulePairs ;
    private TokenEditable currentRow = null;
    private RulePair rulePair = null;

    protected void configToken(LineToken lineToken){
        if(rulePairs==null) rulePairs = new ArrayList<>();
        if (lineToken.containIgnoreCase(0, "CONFIG_MAPPING_PAIR")) {
            rulePairs.add(new RulePair(
                    lineToken.get(1),
                    lineToken.get(2),
                    lineToken.get(3),
                    Boolean.valueOf(lineToken.get(4)),
                    lineToken.get(5).split(",")
            ));
        }
    }

    public GenevaPairMappingHandler() {
        super();
    }

    protected void reset(){
        super.reset();
        currentRow = null;
    }


    @Override
    public LineToken process(LineToken lineToken) {

        String tagname = lineToken.getTagname();
        if (rulePair == null) {
            for (RulePair rp: rulePairs) {
                if(rp.isMatchStart(tagname)){
                    rulePair = rp;
                    currentRow = new TokenEditable(lineToken,false);
                    currentRow.set(0,rulePair.name());
                    break;
                }
            }

        }
        if (rulePair != null) {

            val tokens = rulePair.getTokens(tagname);
            if(tokens!= null && !tokens.isEmpty()){
                for (int i = 0; i < tokens.size(); i++) {
                    val token = tokens.get(i);
                    val idx = rulePair.getMatchIndex(tagname,token);
                    if(idx>=0) {
                        currentRow.set(idx + 1, lineToken.get(token));
                    }
                }
            }

            if(rulePair.isMatchEnd(tagname)){
                if(rulePair.removeMatchPair){
                    lineToken = currentRow;
                }else {
                    lineToken = MultiLineData.merge(lineToken,currentRow);
                }
                currentRow = null;
                rulePair = null;
            }else if(rulePair.removeMatchPair){
                lineToken = LineTokenData.EMPTY;
            }
        }
        return  lineToken;
    }


    public static class RulePair extends SimpleMultiLineTokenFilter {
        @Getter
        private boolean removeMatchPair;
        private Map<String,List<Integer>> contentMap = new HashMap<>();
        private String[]contentArrs;

        public RulePair(String name, String start, String end, boolean removeMatchPair, String... content) {
            super(name, start, end, content);
            this.removeMatchPair = removeMatchPair;
            int length = content.length;
            contentArrs = new String[length];
            for (int i = 0; i < length; i++) {
                String str = content[i];
                String key;
                int token=1;
                if(str == null){
                    key=str = "";
                }else
                if(!str.contains(":")){
                    key=str;
                    str+=":1";
                }else {
                    val keyToken=str.split(":");
                    key=keyToken[0].trim();
                    token=Integer.parseInt(keyToken[1]);
                }

                var list = this.contentMap.get(key);
                if(list == null){
                    contentMap.put(key,list=new ArrayList<>());
                }
                list.add(token);
                contentArrs[i]=str;
            }

        }

        public boolean isMatchContent(String value) {
            return contentMap.containsKey(value);
        }
        public List<Integer> getTokens(String value) {
            return contentMap.get(value);
        }
        public int getMatchIndex(String value,int token) {
            val valueToken = value+":"+token;
            for(int index = 0; index < this.contentArrs.length; ++index) {
                if (this.contentArrs[index].equals(valueToken)) {
                    return index;
                }
            }
            return -1;
        }
    }



}
