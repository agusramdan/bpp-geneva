package ramdan.file.bpp.geneva.config;

import ramdan.file.line.token.filter.SimpleMultiLineTokenFilter;

public class RulePairCapture extends SimpleMultiLineTokenFilter {

    public RulePairCapture(String name, String start, String end) {
        super(name, start, end, null);
    }

}
