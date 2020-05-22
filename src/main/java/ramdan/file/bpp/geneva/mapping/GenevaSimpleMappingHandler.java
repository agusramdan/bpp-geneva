package ramdan.file.bpp.geneva.mapping;

import lombok.Setter;
import lombok.val;
import ramdan.file.bpp.geneva.config.GenevaSimpleConfig;
import ramdan.file.bpp.geneva.config.RuleSimple;
import ramdan.file.line.token.LineToken;
import ramdan.file.line.token.Tokens;
import ramdan.file.line.token.callback.Callback;
import ramdan.file.line.token.config.ConfigHolder;
import ramdan.file.line.token.data.MultiLineData;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * ramdan.file.bpp.geneva.mapping.GenevaPairMappingHandler
 */
public class GenevaSimpleMappingHandler extends GenevaMappingHandler {

    private GenevaSimpleConfig config;

    public GenevaSimpleMappingHandler(GenevaSimpleConfig config) {
        this.config = config;
    }

    public GenevaSimpleMappingHandler() {
        this(ConfigHolder.getConfig(GenevaSimpleConfig.class));
    }


    @Override
    public Tokens matchContent(LineToken lineToken) {
        val result = new ArrayList<LineToken>();

        val input = new LinkedList<LineToken>();
        val removeMatch = new AtomicBoolean(false);
        val callback = new CallbackRuleSimple(input,removeMatch);
        input.add(lineToken);
        while (!input.isEmpty()){
            val lt = input.removeFirst();
            callback.setLt(lt);
            removeMatch.set(false);
            config.matchRule(lt.getTagname(), callback);
            if(!removeMatch.get()){
                result.add(lt);
            }
        }

        return  MultiLineData.tokens(result);
    }
    private class CallbackRuleSimple implements Callback<RuleSimple> {

        @Setter
        LineToken lt;

        Deque<LineToken> back;
        AtomicBoolean removeMatch;

        public CallbackRuleSimple(Deque<LineToken> back, AtomicBoolean removeMatch) {
            this.back = back;
            this.removeMatch = removeMatch;
        }

        @Override
        public void call(RuleSimple rp) {
            back.addLast(rp.mapping(lt));
            removeMatch.set(removeMatch.get()||rp.isRemoveMatch());
        }
    }

}
