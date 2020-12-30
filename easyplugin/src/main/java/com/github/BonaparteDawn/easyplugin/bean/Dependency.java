package com.github.BonaparteDawn.easyplugin.bean;


/**
 * maven dependency bean
 * @author BonaparteDawn
 * @date 2020-12-30 14:49
 */
public class Dependency {
    private String scope;
    private String groupId;
    private String artifactId;
    private String version;
    private String systemPath;

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

    @Override
    public String toString() {
        return groupId+"---"+artifactId+"---"+version;
    }
}
