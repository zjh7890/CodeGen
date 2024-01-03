package me.lotabout.codegenerator.config;

import com.intellij.openapi.util.io.FileUtil;
import com.intellij.util.xmlb.annotations.AbstractCollection;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.jetbrains.java.generate.config.DuplicationPolicy;
import org.jetbrains.java.generate.config.InsertWhere;

import javax.xml.bind.annotation.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@XmlRootElement(name = "codeTemplate")
@XmlAccessorType(XmlAccessType.FIELD)
public class CodeTemplate {
    @XmlAttribute
    public static final String VERSION = "1.3";

    private UUID id;
    public String name = "Untitled";
    public String fileNamePattern = "(.*\\.java$)|(.*\\.class$)";
    public String type = "body";
    public boolean enabled = true;
    public String template = DEFAULT_TEMPLATE;
    public String fileEncoding = DEFAULT_ENCODING;
    @XmlElements({
            @XmlElement(name="memberSelection", type=MemberSelectionConfig.class),
            @XmlElement(name="classSelection", type=ClassSelectionConfig.class),
            @XmlElement(name="classSelection", type=TextInputConfig.class)
    })
    @XmlElementWrapper
    @AbstractCollection(elementTypes = {MemberSelectionConfig.class, ClassSelectionConfig.class, TextInputConfig.class})
    public List<PipelineStep> pipeline = new ArrayList<>();

    public InsertWhere insertNewMethodOption = InsertWhere.AT_CARET;
    public DuplicationPolicy whenDuplicatesOption = DuplicationPolicy.ASK;
    public boolean jumpToMethod = true; // jump cursor to toString method
    public String classNameVm = "${class0.qualifiedName}Test";
    public boolean alwaysPromptForPackage = false;

    public String projectLayer;

    public CodeTemplate(UUID id) {
        this.id = id;
    }
    public CodeTemplate(String id) {
        this.id = UUID.fromString(id);
    }

    public CodeTemplate() {
        this(UUID.randomUUID());
    }

    public void regenerateId() {
        this.id = UUID.randomUUID();
    }

    public String getId() {
        return this.id.toString();
    }

    public boolean isValid() {
        return true;
    }

    public static final String DEFAULT_ENCODING = "UTF-8";

    private static final String DEFAULT_TEMPLATE;

    static {
        String default_template;
        try {
            default_template = FileUtil.loadTextAndClose(CodeTemplate.class.getResourceAsStream("/template/default.vm"));
        } catch (IOException e) {
            default_template = "";
            e.printStackTrace();
        }
        DEFAULT_TEMPLATE = default_template;
    }

    @Override public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        CodeTemplate template1 = (CodeTemplate)o;

        return new EqualsBuilder()
                .append(enabled, template1.enabled)
                .append(jumpToMethod, template1.jumpToMethod)
                .append(alwaysPromptForPackage, template1.alwaysPromptForPackage)
                .append(id, template1.id)
                .append(name, template1.name)
                .append(fileNamePattern, template1.fileNamePattern)
                .append(type, template1.type)
                .append(template, template1.template)
                .append(fileEncoding, template1.fileEncoding)
                .append(pipeline, template1.pipeline)
                .append(insertNewMethodOption, template1.insertNewMethodOption)
                .append(whenDuplicatesOption, template1.whenDuplicatesOption)
                .append(classNameVm, template1.classNameVm)
                .append(projectLayer, template1.projectLayer)
                .isEquals();
    }

    @Override public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(name)
                .append(fileNamePattern)
                .append(type)
                .append(enabled)
                .append(template)
                .append(fileEncoding)
                .append(pipeline)
                .append(insertNewMethodOption)
                .append(whenDuplicatesOption)
                .append(jumpToMethod)
                .append(classNameVm)
                .append(alwaysPromptForPackage)
                .append(projectLayer)
                .toHashCode();
    }


}
