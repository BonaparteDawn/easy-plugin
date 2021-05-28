package com.github.BonaparteDawn.easyplugin;

import com.github.BonaparteDawn.easyplugin.bean.Dependency;
import com.github.BonaparteDawn.easyplugin.context.JarMojoContext;
import com.github.BonaparteDawn.easyplugin.util.ResourceUtil;
import org.apache.maven.settings.Settings;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * the plug-in that it can install the external Jar package to local maven repository
 *
 * @author BonaparteDawn
 */
@Mojo(name="mavenJar",defaultPhase= LifecyclePhase.NONE)
public class MavenJarMojo extends AbstractMojo {

    /**
     * the external dependencies from the external Jar package
     */
    @Parameter
    private Dependency[] dependencies;

    /**
     * local maven home path
     */
    @Parameter(property = "maven.home")
    private String mavenHome;

    /**
     * local maven repository path
     */
    @Parameter(property = "maven.repo.local")
    private String mavenRepoLocal;


    /**
     * The system settings for Maven. This is the instance resulting from
     * merging global and user-level settings files.
     */
    @Parameter( defaultValue = "${settings}", readonly = true, required = true )
    private Settings settings;

    /**
     * file encoding type
     */
    @Parameter(property = "file.encoding")
    private String encode;


    @Override
    public void execute() throws MojoFailureException {
        if (dependencies!=null){
            Log log = getLog();
            log.info("===========================================================================");
            log.info("start to install the all dependencies to local maven repository");
            log.info("===========================================================================");
            for (int i=0;i<dependencies.length;i++){
                try {
                    Dependency dependency = dependencies[i];
                    JarMojoContext jarMojoContext = new JarMojoContext(dependency);
                    jarMojoContext.setMavenHome(mavenHome);
                    jarMojoContext.setMavenRepoLocal(mavenRepoLocal);
                    jarMojoContext.setSettings(settings);
                    jarMojoContext.setEncode(encode);
                    jarMojoContext.init();
                    jarMojoContext.routeEngine().run();
                    ResourceUtil.release(jarMojoContext.getResourceStock());
                }catch (Exception e){
                    getLog().error(e);
                    throw new MojoFailureException(e.getMessage());
                }
            }
            log.info("===========================================================================");
            log.info("finish installing the all dependencies to local maven repository");
            log.info("===========================================================================");
        }
    }

}
