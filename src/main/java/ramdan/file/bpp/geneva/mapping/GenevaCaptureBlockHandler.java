package ramdan.file.bpp.geneva.mapping;

import lombok.Setter;
import lombok.val;
import ramdan.file.bpp.geneva.config.GenevaCaptureBlockConfig;
import ramdan.file.line.token.LineToken;
import ramdan.file.line.token.Tokens;
import ramdan.file.line.token.data.LineTokenData;
import ramdan.file.line.token.data.LineTokensBlockSimple;
import ramdan.file.line.token.data.MultiLineData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * ramdan.file.bpp.geneva.mapping.GenevaPairMappingHandler
 */
public class GenevaCaptureBlockHandler extends GenevaMappingHandler {
    private List<LineToken> capture = new ArrayList<>() ;
    @Setter
    private GenevaCaptureBlockConfig config ;

    private String simpleConfig;
    private String blockConfig;
    private Block block= Block.NONE;

    private LineToken startBlock;
    private LineToken endBlock;
    protected List<Tokens> before=new ArrayList<>();
    protected List<Tokens> after=new ArrayList<>();
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
    public GenevaCaptureBlockHandler(String name,String startTagname, String endTagname, String simpleConfig, String blockConfig, boolean remove, Collection<String> tagnames) {
        this(new GenevaCaptureBlockConfig(name,startTagname,endTagname));
        this.simpleConfig = simpleConfig;
        this.blockConfig =blockConfig;
        if(tagnames!=null){
            for (String key : tagnames) {
                config.addTagname(key,remove);
            }
        }
        prepare();
    }
//    public GenevaCaptureBlockHandler(String startTagname, String endTagname, String simpleConfig, String blockConfig, boolean remove, Collection<String> tagnames) {
//        this(new GenevaCaptureBlockConfig(startTagname,endTagname));
//        this.simpleConfig = simpleConfig;
//        this.blockConfig =blockConfig;
//        if(tagnames!=null){
//            for (String key : tagnames) {
//                config.addTagname(key,remove);
//            }
//        }
//        prepare();
//    }

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
        before.clear();
        capture.clear();
        after.clear();
        block = Block.NONE;
        startBlock = null;//LineTokenData.newInstance(config.getStartTagname());
        endBlock=null;//LineTokenData.newInstance(config.getEndTagname());
    }

    protected boolean isMatchCapture(LineToken lineToken){
        return  config.isMatchCapture(lineToken);
    }
    protected boolean isMatchRemove(LineToken lineToken){
        return config.isMatchRemove(lineToken);
    }
    protected void handleCapture(List<LineToken> capture){

    }
    protected Tokens handleRelease(LineToken lineToken){
        handleCapture(capture);
        val result = new ArrayList<Tokens>(before);
        result.add(new LineTokensBlockSimple(
                config.getTagname(),
                config.getStartTagname(),
                config.getEndTagname(),
                startBlock,
                endBlock,
                capture));
        result.addAll(after);
        result.add(lineToken);
        before.clear();
        capture.clear();
        after.clear();
        block= Block.END;
        return MultiLineData.tokens(result);
    }
    @Override
    protected Tokens endTagHandle(LineToken lineToken) {
        if(block== Block.END){
            return lineToken;
        }
        return handleRelease(lineToken);
    }

    @Override
    protected Tokens matchContent(LineToken lineToken) {
        Tokens result = lineToken;
        if (lineToken.equalIgnoreCase(0, config.getStartTagname())) {
            block = Block.FOUND;
            startBlock =lineToken;
            result = LineTokenData.EMPTY;
        }else if (lineToken.equalIgnoreCase(0,config.getEndTagname())){
            endBlock=lineToken;
            result =handleRelease(LineTokenData.EMPTY);
        }else  if (block == Block.FOUND ){
            capture.add(lineToken);
            result = LineTokenData.EMPTY;
        } else if (isMatchCapture(lineToken)) {
            capture.add(lineToken);
            if(isMatchRemove(lineToken)){
                result = LineTokenData.EMPTY;
            }
        }
        return result;
    }

    enum Block {
        NONE,FOUND,END
    }

}
