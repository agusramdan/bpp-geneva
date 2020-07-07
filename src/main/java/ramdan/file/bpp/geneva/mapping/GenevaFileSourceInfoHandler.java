package ramdan.file.bpp.geneva.mapping;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import ramdan.file.line.token.LineToken;
import ramdan.file.line.token.Tokens;
import ramdan.file.line.token.data.MultiLineData;
import ramdan.file.line.token.data.TokenEditable;

import java.io.File;

@Slf4j
public class GenevaFileSourceInfoHandler extends GenevaMappingHandler {

    private TokenEditable fileSourceInfo ;
    private int count;

    @Override
    protected Tokens endTagHandle(LineToken lineToken) {
        val line = lineToken.getSource();
        if(line!=null){
            count+=line.length()+1;
        }
        fileSourceInfo.set(3,lineToken.getEnd());
        fileSourceInfo.set(4,fileSourceInfo.getInt(3)-fileSourceInfo.getInt(2)+1);
        fileSourceInfo.set(5,count);
        return MultiLineData.merge(
                fileSourceInfo,
                lineToken
        );
    }

    @Override
    protected LineToken startTagHandle(LineToken lineToken) {
        log.info("Start {}",lineToken.getSource().getSource());
        count = 0;
        File file = null;
        val line = lineToken.getSource();
        if(line!=null){
            file=line.getSource();
            count+=line.length()+1;
        }
        fileSourceInfo = new TokenEditable(lineToken,false);
        fileSourceInfo.set(0,"FILE_SOURCE_INFO");
        fileSourceInfo.set(1,lineToken.getFileName());
        fileSourceInfo.set(2,lineToken.getStart());

        if(file!=null) {
            fileSourceInfo.set(8, file.getParent());
            fileSourceInfo.set(9, file.getAbsolutePath());
        }
        return lineToken;
    }

    @Override
    protected Tokens matchContent(LineToken lineToken) {
        val line = lineToken.getSource();
        if(line!=null){
            count+=line.length()+1;
        }
        return super.matchContent(lineToken);
    }

}
