package ramdan.file.bpp.geneva.mapping;

import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.val;
import lombok.var;
import ramdan.file.bpp.geneva.config.GenevaPairDetailConfig;
import ramdan.file.bpp.geneva.config.RulePairDetail;
import ramdan.file.bpp.geneva.data.TokenEditable;
import ramdan.file.line.token.LineToken;
import ramdan.file.line.token.config.ConfigHolder;
import ramdan.file.line.token.data.LineTokenData;
import ramdan.file.line.token.data.MultiLineData;
import ramdan.file.line.token.handler.Callback;

import java.util.ArrayList;
import java.util.List;

/**
 * ramdan.file.bpp.geneva.mapping.GenevaPairMappingHandler
 */
public class GenevaPairDetailMappingHandler extends GenevaMappingHandler {

    @Setter
    private GenevaPairDetailConfig config;
    private List<DataHolder> activeRules = new ArrayList<>();

    public GenevaPairDetailMappingHandler() {
        this(ConfigHolder.getConfig(GenevaPairDetailConfig.class));
    }

    public GenevaPairDetailMappingHandler(GenevaPairDetailConfig config) {
        this.config = config;
    }

    protected void reset(){
        super.reset();
        activeRules.clear();
    }


    @Override
    public LineToken matchContent(LineToken lineToken) {
        val beforeLineToken = new ArrayList<LineToken>();
        val afterLineToken = new ArrayList<LineToken>();
        var removeToken = false;
        String tagname = lineToken.getTagname();
        var indexNew = activeRules.size();
        config.pairRuleByStart(tagname, new CallbackRulePairDetail(lineToken));
        for (int i = 0; i < activeRules.size() ; i++) {
            val holder = activeRules.get(i);
            val rule = holder.rule;
            if(rule.isMatchStart(tagname) && i <indexNew){
                indexNew--;
                activeRules.remove(i);
                i--;
                if(!holder.data.isEmpty()) {
                    switch (rule.getAction()){
                        case "BEFORE_END":beforeLineToken.addAll(holder.data);break;
                        case "AFTER_END": afterLineToken.addAll(holder.data);break;
                    }
                }
                continue;
            }
            if(rule.isMatchDetail(tagname)){
                removeToken=removeToken||rule.isRemoveMatchDetail();
                val detail = new TokenEditable(lineToken,false);
                detail.copyTokensFrom(holder.template);
                mapping(rule,detail,lineToken);
                detail.set(0,rule.name());
                switch (rule.getAction()){
                    case "BEFORE_END":
                    case "AFTER_END": holder.data.add(detail);break;
                    case "INLINE_BEFORE": beforeLineToken.add(detail);break;
                    case "INLINE_AFTER":afterLineToken.add(detail);break;
                }
            }else if(rule.isMatchContent(tagname)){
                // set template
                mapping(rule,holder.template,lineToken);
            }
            if(rule.isMatchEnd(tagname)){
                activeRules.remove(i);
                i--;
                if(!holder.data.isEmpty()) {
                    switch (rule.getAction()){
                        case "BEFORE_END":beforeLineToken.addAll(holder.data);break;
                        case "AFTER_END": afterLineToken.addAll(holder.data);break;
                    }
                }
            }
        }

        if(!removeToken) {
            beforeLineToken.add(lineToken);
        }
        beforeLineToken.addAll(afterLineToken);

        if(beforeLineToken.isEmpty()){
            return LineTokenData.EMPTY;
        }
        if(beforeLineToken.size()==1){
            return beforeLineToken.get(0);
        }
        return MultiLineData.newInstance(beforeLineToken);
    }
    private void mapping(RulePairDetail rule,TokenEditable target, LineToken source){
        rule.mapping(target,source);
//        val tagname = source.getTagname();
//        val tokens = rule.getTokens(tagname);
//        if (tokens != null && !tokens.isEmpty()) {
//            for (int t = 0; t < tokens.size(); t++) {
//                val token = tokens.get(t);
//                val idx = rule.getMatchIndex(tagname, token);
//                if (idx >= 0) {
//                    target.set(idx + 1, source.get(token));
//                }
//            }
//        }
    }

//    public static class RulePairDetail extends SimpleMultiLineTokenFilter {
//        @Getter
//        private boolean removeMatchDetail;
//
//        private Map<Integer,String>contentConstant = new HashMap<>();
//
//        private Map<String,List<Integer>> contentMap = new HashMap<>();
//        private String[]contentArrs;
//
//        private String detail;
//        @Getter
//        private String action;
//
//        public RulePairDetail(String name, String start, String end, String detail, String action, boolean removeMatchDetail, String... content) {
//            super(name, start, end, content);
//            this.detail = detail;
//            this.action = action;
//            this.removeMatchDetail = removeMatchDetail;
//            int length = content.length;
//            contentArrs = new String[length];
//            for (int i = 0; i < length; i++) {
//                String str = content[i];
//                String key;
//                int token=1;
//                if(str == null){
//                    str ="";
//                }else {
//                    str = str.trim();
//                }
//                if(str.isEmpty()||str.startsWith("'")){
//                    contentConstant.put(i, GenevaUtils.tokenConstant(str));
//                    str="";
//                }else{
//                    try{
//                        token = Integer.parseInt(str);
//                        key = detail;
//                        str=key+":"+token;
//                    }catch (Exception e){
//                        if(!str.contains(":")){
//                            key=str;
//                            str+=":1";
//                        }else {
//                            val keyToken=str.split(":");
//                            key=keyToken[0].trim();
//                            token=Integer.parseInt(keyToken[1]);
//                        }
//                    }
//                    var list = this.contentMap.get(key);
//                    if(list == null){
//                        contentMap.put(key,list=new ArrayList<>());
//                    }
//                    list.add(token);
//                }
//                contentArrs[i]=str;
//            }
//
//        }
//
//        public boolean isMatchDetail(String value){
//            return detail.equals(value);
//        }
//
//        public boolean isMatchContent(String value) {
//            return contentMap.containsKey(value);
//        }
//        public List<Integer> getTokens(String value) {
//            return contentMap.get(value);
//        }
//        public int getMatchIndex(String value,int token) {
//            val valueToken = value+":"+token;
//            for(int index = 0; index < this.contentArrs.length; ++index) {
//                if (this.contentArrs[index].equals(valueToken)) {
//                    return index;
//                }
//            }
//            return -1;
//        }
//
//        public void mapping(TokenEditable target, LineToken source) {
//            val tagname = source.getTagname();
//            val tokens = getTokens(tagname);
//            if (tokens != null && !tokens.isEmpty()) {
//                for (int t = 0; t < tokens.size(); t++) {
//                    val token = tokens.get(t);
//                    val valueToken = tagname+":"+token;
//                    for(int index = 0; index < this.contentArrs.length; ++index) {
//                        if (this.contentArrs[index].equals(valueToken)) {
//                            target.set(index+1,source.get(token));
//                        }
//                    }
//                }
//            }
//        }
//    }

    @AllArgsConstructor
    class CallbackRulePairDetail implements Callback<RulePairDetail>{
        private LineToken source;
        @Override
        public void call(RulePairDetail rp) {
            activeRules.add(new DataHolder(rp,source));
        }
    }
    class DataHolder{
        final RulePairDetail rule;
        private final List<TokenEditable> data = new ArrayList<>();
        private final TokenEditable template ;
        public DataHolder(RulePairDetail rule,LineToken source) {
            this.rule = rule;
            this.template=rule.template(source);
        }
    }

}
