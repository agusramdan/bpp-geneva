package ramdan.file.bpp.geneva.mapping;

import lombok.val;
import ramdan.file.bpp.geneva.data.TokenEditable;
import ramdan.file.line.token.LineToken;
import ramdan.file.line.token.data.MultiLineData;

import java.io.File;

public class GenevaFileSourceInfoHandler extends GenevaMappingHandler {

    private TokenEditable fileSourceInfo ;
    private int count;

    @Override
    protected LineToken endTagHandle(LineToken lineToken) {
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
    protected LineToken matchContent(LineToken lineToken) {
        val line = lineToken.getSource();
        if(line!=null){
            count+=line.length()+1;
        }
        return super.matchContent(lineToken);
    }

    @Override
    protected LineToken alreadyStartTagHandle(LineToken lineToken) {
        val endtag = new TokenEditable(lineToken,false);
        endtag.set(0,"DOCEND");
        return MultiLineData.merge(
                endTagHandle(endtag),
                startTagHandle(lineToken)
        );
    }
}
