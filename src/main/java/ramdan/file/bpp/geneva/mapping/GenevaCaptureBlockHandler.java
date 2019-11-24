package ramdan.file.bpp.geneva.mapping;

import lombok.Setter;
import ramdan.file.bpp.geneva.config.GenevaCaptureBlockConfig;
import ramdan.file.line.token.LineToken;
import ramdan.file.line.token.data.LineTokenData;
import ramdan.file.line.token.data.MultiLineData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * ramdan.file.bpp.geneva.mapping.GenevaPairMappingHandler
 */
public class GenevaCaptureBlockHandler extends GenevaMappingHandler {
    private List<LineToken> capture = new ArrayList<>() ;
    @Setter
    private GenevaCaptureBlockConfig config ;
    protected String simpleConfig;
    protected String blockConfig;
    private Block block= Block.NONE;

    protected void addTagname(String tagname,Boolean remove){

        config.addTagname(tagname,remove);
    }
    protected void configToken(LineToken lineToken){
        if (simpleConfig!=null && lineToken.equalIgnoreCase(0,simpleConfig)){
            addTagname(lineToken.getValue(),Boolean.parseBoolean(lineToken.get(2)));
        }
//        else if(blockConfig!=null && lineToken.equalIgnoreCase(0,blockConfig)){
//
//        }
    }

    public GenevaCaptureBlockHandler(GenevaCaptureBlockConfig config){
        this.config = config;
    }
    public GenevaCaptureBlockHandler(String startTagname, String endTagname, boolean remove, String ... tagnames){
        this(startTagname,endTagname,null,null,remove,Arrays.asList(tagnames));
    }

    public GenevaCaptureBlockHandler(String startTagname, String endTagname, String simpleConfig, String blockConfig, boolean remove, Collection<String> tagnames) {
        this(new GenevaCaptureBlockConfig(startTagname,endTagname));
        this.simpleConfig = simpleConfig;
        this.blockConfig =blockConfig;
        if(tagnames!=null){
            for (String key : tagnames) {
                config.addTagname(key,remove);
            }
        }
        prepare();
    }

    private boolean prepared = false;
    @Override
    protected void prepare() {
        if(!prepared && config!=null) {
            prepared = true;
            super.prepare();
        }
    }

    protected void reset(){
        super.reset();
        capture.clear();
        block = Block.NONE;
    }

    protected boolean isMatchCapture(LineToken lineToken){
        return  config.isMatchCapture(lineToken);
    }
    protected boolean isMatchRemove(LineToken lineToken){
        return config.isMatchRemove(lineToken);
    }
    protected void handleCapture(List<LineToken> capture){

    }
    protected LineToken handleRelease(LineToken lineToken){
        handleCapture(capture);
        capture.add(lineToken);
        lineToken = MultiLineData.newInstance(capture);
        capture.clear();
        block= Block.END;
        return lineToken;
    }
    @Override
    protected LineToken endTagHandle(LineToken lineToken) {
        switch (block) {
            case NONE: capture.add(0,LineTokenData.newInstance(config.getStartTagname()));
            case FOUND:capture.add(LineTokenData.newInstance(config.getEndTagname())); break;
            case END: return lineToken;
        }
        return handleRelease(lineToken);
    }

    @Override
    protected LineToken matchContent(LineToken lineToken) {
        if (lineToken.equalIgnoreCase(0, config.getStartTagname())) {
            block = Block.FOUND;
            capture.add(0,lineToken);
            lineToken = LineTokenData.EMPTY;
        }else if (lineToken.equalIgnoreCase(0,config.getEndTagname())){
            lineToken =handleRelease(lineToken);
        }else  if (block == Block.FOUND ){
            capture.add(lineToken);
            lineToken = LineTokenData.EMPTY;
        } else if (isMatchCapture(lineToken)) {
            capture.add(lineToken);
            if(isMatchRemove(lineToken)){
                lineToken = LineTokenData.EMPTY;
            }
        }
        return lineToken;
    }

    enum Block {
        NONE,FOUND,END
    }

}
