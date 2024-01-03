package me.lotabout.codegenerator.config;

import me.lotabout.codegenerator.model.DomainObject;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.UUID;

/**
 * @Author: zjh
 * @Date: 2023/12/31 21:47
 */
@XmlRootElement(name = "projectLayer")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProjectLayerConfig extends DomainObject {
    @XmlAttribute
    public static final String VERSION = "1.3";

    public UUID id;

    private String name;

    private String module;

    private String path;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return name;
    }
}
