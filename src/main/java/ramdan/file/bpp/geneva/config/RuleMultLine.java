package ramdan.file.bpp.geneva.config;

import lombok.Getter;
import lombok.val;
import lombok.var;
import ramdan.file.bpp.geneva.GenevaUtils;
import ramdan.file.line.token.LineToken;
import ramdan.file.line.token.data.TokenEditable;

import java.util.*;

public class RuleMultLine  {


    private Map<Integer, String> contentConstant = new HashMap<>();
    private Map<String, List<Integer>> contentMap = new HashMap<>();
    private String[] contentArrs;

    private String detail;

    @Getter
    private String name;
    @Getter
    private String action;
    @Getter
    private boolean removeMatchDetail;

    public RuleMultLine(String name, String action, boolean removeMatchDetail, String... content) {
        this.name=name;
        this.detail = detail;
        this.action = action;
        this.removeMatchDetail = removeMatchDetail;
        int length = content.length;
        contentArrs = new String[length];
        for (int i = 0; i < length; i++) {
            String str = content[i];
            String key;
            int token = 1;
            if (str == null) {
                str = "";
            } else {
                str = str.trim();
            }
            if (str.isEmpty() || str.startsWith("'")) {
                contentConstant.put(i, GenevaUtils.tokenConstant(str));
                str = "";
            } else {
                try {
                    token = Integer.parseInt(str);
                    key = detail;
                    str = key + ":" + token;
                } catch (Exception e) {
                    if (!str.contains(":")) {
                        key = str;
                        str += ":1";
                    } else {
                        val keyToken = str.split(":");
                        key = keyToken[0].trim();
                        token = Integer.parseInt(keyToken[1]);
                    }
                }
                var list = this.contentMap.get(key);
                if (list == null) {
                    contentMap.put(key, list = new ArrayList<>());
                }
                list.add(token);
            }
            contentArrs[i] = str;
        }

    }

    public boolean isMatchDetail(String value) {
        return detail.equals(value);
    }

    public boolean isMatchContent(String value) {
        return contentMap.containsKey(value);
    }

    public List<Integer> getTokens(String value) {
        return contentMap.get(value);
    }

    public Set<String> getTagnames() {
        return contentMap.keySet();
    }

    public TokenEditable template(LineToken source){
        val template = new TokenEditable(source,name,false);
        for (Map.Entry<Integer,String> e:contentConstant.entrySet()) {
            template.set(e.getKey()+1,e.getValue());
        }
        return template;
    }
    public void mapping(TokenEditable target, LineToken source) {
        val tagname = source.getTagname();
        val tokens = getTokens(tagname);
        if (tokens != null && !tokens.isEmpty()) {
            for (int t = 0; t < tokens.size(); t++) {
                val token = tokens.get(t);
                val valueToken = tagname + ":" + token;
                for (int index = 0; index < this.contentArrs.length; ++index) {
                    if (this.contentArrs[index].equals(valueToken)) {
                        target.set(index + 1, source.get(token));
                    }
                }
            }
        }
    }
}
