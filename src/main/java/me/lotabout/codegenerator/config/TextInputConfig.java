package me.lotabout.codegenerator.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Objects;

@XmlRootElement(name = "textInput")
@XmlAccessorType(XmlAccessType.FIELD)
public class TextInputConfig implements PipelineStep {
    public boolean enabled = true;
    public String postfix = "";
    @Override public String type() {
        return "text-input";
    }

    @Override
    public String postfix() {
        return postfix;
    }

    @Override
    public void postfix(String postfix) {
        this.postfix = postfix;
    }

    @Override
    public boolean enabled() {
        return enabled;
    }

    @Override
    public void enabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TextInputConfig that = (TextInputConfig) o;
        return enabled == that.enabled && Objects.equals(postfix, that.postfix);
    }

    @Override
    public int hashCode() {
        return Objects.hash(enabled, postfix);
    }
}
