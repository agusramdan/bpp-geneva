package ramdan.file.bpp.geneva.mapping;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import ramdan.file.line.token.LineToken;
import ramdan.file.line.token.Tokens;

import java.util.Date;

@Slf4j
public class GenevaPerformanceInfoHandler extends GenevaMappingHandler {

    //private TokenEditable fileSourceInfo ;

    private long startTime;
    private long endTime;
    private long duration;
    @Override
    protected Tokens startTagHandle(LineToken lineToken) {
        val line = lineToken.getSource();
        if(line==null){
            startTime = lineToken.timestamp();
        }else {
            startTime=line.timestamp();
        }
        return lineToken;
    }
    @Override
    protected Tokens endTagHandle(LineToken lineToken) {
        endTime=System.currentTimeMillis();
        long endTagReadTime=0;
        val line = lineToken.getSource();
        if(line==null){
            endTagReadTime = lineToken.timestamp();
        }else {
            endTagReadTime=line.timestamp();
        }
        duration=(endTime-startTime);
        log.info("Proses {} to {} duration {} menit , latency {} detik",
                new Date(startTime).toString(),
                new Date(endTime).toString(),
                duration/60000,
                (endTime-endTagReadTime)/6000
        );
        return lineToken;
    }
}
