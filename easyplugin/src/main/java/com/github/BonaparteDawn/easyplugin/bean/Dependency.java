package com.github.BonaparteDawn.easyplugin.bean;


import java.util.ArrayList;
import java.util.List;

/**
 * maven dependency bean
 * @author BonaparteDawn
 */
public class Dependency {
    private String scope;
    private String groupId;
    private String artifactId;
    private String version;
    private String systemPath;
    private String newVersion;
    private List<String> cleans = new ArrayList<>();


    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getSystemPath() {
        return systemPath;
    }

    public void setSystemPath(String systemPath) {
        this.systemPath = systemPath;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getNewVersion() {
        return newVersion;
    }

    public void setNewVersion(String newVersion) {
        this.newVersion = newVersion;
    }

    public List<String> getCleans() {
        return cleans;
    }

    public void setCleans(List<String> cleans) {
        this.cleans = cleans;
    }

    @Override
    public String toString() {
        return groupId+"---"+artifactId+"---"+version;
    }
}
