package com.github.BonaparteDawn.easyplugin.context;

import com.github.BonaparteDawn.easyplugin.bean.Dependency;
import com.github.BonaparteDawn.easyplugin.engine.JarMojoEngine;
import org.apache.maven.settings.Settings;
import java.util.Stack;

/**
 * jarMojo context
 *
 * @author BonaparteDawn
 */
public class JarMojoContext {

    /**
     * dependency
     */
    private Dependency dependency;

    /**
     * system/none
     */
    private String scope;

    /**
     * local maven home path
     */
    private String mavenHome;

    /**
     * local maven repository path
     */
    private String mavenRepoLocal;


    /**
     * The system settings for Maven. This is the instance resulting from
     * merging global and user-level settings files.
     */
    private Settings settings;

    /**
     * file encoding type
     */
    private String encode;

    /**
     * resource hook
     */
    private Stack<Object> resourceStock = new Stack<>();

    public JarMojoContext(Dependency dependency) {
        this.dependency = dependency;
    }

    public void init(){
        scope = dependency.getScope();
    }

    public JarMojoEngine routeEngine(){
        if (dependency==null){
            return null;
        }
        if ("system".equalsIgnoreCase(scope)){
            return new JarMojoEngine(this);
        }else{
            return new JarMojoEngine(this);
        }
    }

    public String getMavenHome() {
        return mavenHome;
    }

    public void setMavenHome(String mavenHome) {
        this.mavenHome = mavenHome;
    }

    public String getMavenRepoLocal() {
        return mavenRepoLocal;
    }

    public void setMavenRepoLocal(String mavenRepoLocal) {
        this.mavenRepoLocal = mavenRepoLocal;
    }

    public String getEncode() {
        return encode;
    }

    public void setEncode(String encode) {
        this.encode = encode;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public Dependency getDependency() {
        return dependency;
    }

    public void setDependency(Dependency dependency) {
        this.dependency = dependency;
    }

    public Stack<Object> getResourceStock() {
        return resourceStock;
    }

    public void setResourceStock(Stack<Object> resourceStock) {
        this.resourceStock = resourceStock;
    }

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }
}
