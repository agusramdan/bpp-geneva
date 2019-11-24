package ramdan.file.bpp.geneva;

import ramdan.file.line.token.LineToken;

import java.util.List;

public class GenevaUtils {

    public static String tokenConstant(String token){
        int idx = token!=null?token.indexOf("'"):-1;
        return idx>=0 ? token.substring(idx+1):token;
    }
    public static LineToken fineFirst(List<LineToken> lineTokens,String tokenname){
        LineToken found = null;

        for (int i = 0; found==null && i< lineTokens.size() ; i++) {
            found = lineTokens.get(i);
            if(!found.equal(0,tokenname)){
                found=null;
            }
        }

        return found;
    }
}
