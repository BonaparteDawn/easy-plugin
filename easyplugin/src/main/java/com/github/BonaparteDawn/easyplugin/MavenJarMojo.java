package com.github.BonaparteDawn.easyplugin;

import com.github.BonaparteDawn.easyplugin.bean.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import java.io.File;
import java.util.*;

/**
 * the plug-in that it can install the external Jar package to local maven repository
 *
 * @author BonaparteDawn
 */
@Mojo(name="mavenJar",defaultPhase= LifecyclePhase.NONE)
public class MavenJarMojo extends AbstractMojo {

    /**
     * operate system type
     */
    private String osName = System.getProperty("os.name").toLowerCase();

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
            Map<String,String> mvnArg = mvnArg();
            for (int i=0;i<dependencies.length;i++){
                Dependency dependency = dependencies[i];
                if (dependency==null || !"system".equalsIgnoreCase(dependency.getScope())){
                    continue;
                }
                ProcessBuilder processBuilder = new ProcessBuilder();
                try {
                    processBuilder.redirectErrorStream(true);
                    processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
                    processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);
                    StringJoiner driver = new StringJoiner(File.separator);
                    List<String> driverArgs = new ArrayList<>();
                    if (mavenHome!=null && mavenHome.length()>0){
                        processBuilder.directory(new File(mavenHome+File.separator+"bin"));
                    }
                    File directory = processBuilder.directory();
                    if (directory!=null){
                        driver.add(directory.getAbsolutePath());
                    }
                    if (osName.indexOf("windows")>=0){
                        driver.add("mvn.cmd");
                    }else{
                        driver.add("mvn");
                    }
                    driverArgs.add("install:install-file");
                    //add the expand arguments
                    Map<String,String> args = new LinkedHashMap<>(mvnArg);
                    args.putAll(dependencyArg(dependency));
                    Iterator<Map.Entry<String, String>> argsIterator = args.entrySet().iterator();
                    while (argsIterator.hasNext()){
                        Map.Entry<String, String> next = argsIterator.next();
                        driverArgs.add("-D"+next.getKey()+"="+next.getValue());
                    }
                    List<String> command = processBuilder.command();
                    command.add(driver.toString());
                    command.addAll(driverArgs);
                    Process process = processBuilder.start();
                    process.waitFor();
                    process.destroy();
                }catch (Exception e){
                    e.printStackTrace();
                    throw new MojoFailureException(e.getMessage());
                }finally {
                    StringJoiner joiner = new StringJoiner(" ");
                    if (processBuilder.command()!=null){
                        File directory = processBuilder.directory();
                        if (directory==null){
                            log.info("shell home:"+null);
                        }else{
                            log.info("shell home:"+directory.getAbsolutePath());
                        }
                        for (String sp:processBuilder.command()){
                            joiner.add(sp);
                        }
                        log.info("cmd-"+i+" : "+joiner.toString());
                    }
                }
            }
            log.info("===========================================================================");
            log.info("finish installing the all dependencies to local maven repository");
            log.info("===========================================================================");
        }
    }

    /**
     * the arguments used for building the maven command, such as -D*
     * @return
     */
    private Map<String,String> mvnArg(){
        Map<String,String> res = new LinkedHashMap<>();
        if (encode!=null){
            res.put("file.encoding",encode);
        }
        if (mavenRepoLocal!=null){
            res.put("maven.repo.local",mavenRepoLocal);
        }
        return res;
    }

    /**
     * the dependency properties(used for building the maven command arguments ,such as -D*)
     * @return
     */
    private Map<String,String> dependencyArg(Dependency dependency){
        Map<String,String> res = new LinkedHashMap<>();
        res.put("groupId",dependency.getGroupId());
        res.put("artifactId",dependency.getArtifactId());
        res.put("version",dependency.getVersion());
        res.put("packaging","jar");
        res.put("file",dependency.getSystemPath());
        return res;
    }

}
