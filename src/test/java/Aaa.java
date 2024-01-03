import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.codeStyle.CodeStyleSettingsManager;
import com.intellij.psi.codeStyle.NameUtil;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import com.intellij.testFramework.TestDataPath;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import com.rits.cloning.Cloner;
import me.lotabout.codegenerator.config.ProjectLayerConfig;
import me.lotabout.codegenerator.util.EntryFactory;
import me.lotabout.codegenerator.util.GenerationUtil;
import org.apache.velocity.VelocityContext;
import org.jetbrains.java.generate.element.GenerationHelper;
import org.jetbrains.java.generate.velocity.VelocityFactory;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @Author: zjh
 * @Date: 2024/1/1 12:13
 */
@TestDataPath("$CONTENT_ROOT/src/test/testData")
public class Aaa extends BasePlatformTestCase {


    public void test() {

        var sw = new StringWriter();
        var velocity = VelocityFactory.getVelocityEngine();
        var vc = new VelocityContext();

//        vc.put("settings", CodeStyleSettingsManager.getSettings(project));
//        vc.put("project", project);
        vc.put("helper", GenerationHelper.class);
        vc.put("StringUtil", StringUtil.class);
        vc.put("NameUtil", NameUtil.class);
        vc.put("PsiShortNamesCache", PsiShortNamesCache.class);
        vc.put("JavaPsiFacade", JavaPsiFacade.class);
        vc.put("GlobalSearchScope", GlobalSearchScope.class);
        vc.put("EntryFactory", EntryFactory.class);
        vc.put("class0", "shit");

        ArrayList<Object> list = new ArrayList<>();
        HashMap<Object, Object> e = new HashMap<>();
        e.put("whatName", "nice");
        list.add(e);
        list.add(e);

        vc.put("allProducts", list.toArray());

        String template = "#foreach( $product in $allProducts )\n" +
                "    <li>$whatName</li>\n" +
                "#end";

        velocity.evaluate(vc, sw, GenerationUtil.class.getName(), template);
        System.out.println("hit");

//
//        Cloner cloner = new Cloner();
//        cloner.nullInsteadOfClone();
//        ProjectLayerConfig o = new ProjectLayerConfig();
//        ProjectLayerConfig config = cloner.deepClone(o);
//        System.out.println(config);
    }
}
