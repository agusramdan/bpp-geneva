package ramdan.file.bpp.geneva.config;

import ramdan.file.line.token.filter.SimpleMultiLineTokenFilter;

public class RulePairRemove extends SimpleMultiLineTokenFilter {

    public RulePairRemove(String name, String start, String end) {
        super(name, start, end, null);
    }

}
