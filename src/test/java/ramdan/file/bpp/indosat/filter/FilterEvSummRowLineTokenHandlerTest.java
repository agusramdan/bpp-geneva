package ramdan.file.bpp.indosat.filter;

import lombok.val;
import org.junit.Assert;
import org.junit.Test;
import ramdan.file.line.token.filter.RegexMatchRule;

public class FilterEvSummRowLineTokenHandlerTest {

    @Test
    public void regexTets(){
        val startEvSummGroup = new RegexMatchRule("BSTARTEVSUMMGROUP_\\d*_((?!304).)*");
        Assert.assertTrue(startEvSummGroup.isMatchRule("BSTARTEVSUMMGROUP_1_1"));
        Assert.assertTrue(startEvSummGroup.isMatchRule("BSTARTEVSUMMGROUP_1_305"));
        Assert.assertFalse(startEvSummGroup.isMatchRule("BSTARTEVSUMMGROUP_1_304"));
        //Assert.assertFalse(startEvSummGroup.isMatchRule("BSTARTEVSUMMGROUP_1_(!304)"));
    }
}
