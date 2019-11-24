package ramdan.file.bpp.geneva.config;

import lombok.Getter;
import lombok.val;
import ramdan.file.line.token.LineToken;
import ramdan.file.line.token.config.Config;

import java.util.HashMap;
import java.util.Map;

public class GenevaCaptureBlockConfig implements Config {

    private Map<String,Boolean> tagnames =new HashMap<>();
    @Getter
    protected String startTagname;
    @Getter
    protected String endTagname;

    public GenevaCaptureBlockConfig(String startTagname, String endTagname) {
        this.startTagname = startTagname;
        this.endTagname = endTagname;
    }
    public void addTagname(String tagname,Boolean remove){
        tagnames.put(tagname,remove);
    }
    public boolean isMatchCapture(LineToken lineToken){
        return  tagnames.containsKey(lineToken.getTagname());
    }
    public boolean isMatchRemove(LineToken lineToken){
        val remove = tagnames.get(lineToken.getTagname());
        return  remove!=null && remove;
    }
}
