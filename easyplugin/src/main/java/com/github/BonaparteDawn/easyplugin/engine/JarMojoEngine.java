package com.github.BonaparteDawn.easyplugin.engine;

import com.github.BonaparteDawn.easyplugin.context.JarMojoContext;
import com.github.BonaparteDawn.easyplugin.util.ShellUtil;
import java.util.*;

/**
 *
 * @author BonaparteDawn
 */
public class JarMojoEngine {
    private JarMojoContext jarMojoContext;

    public JarMojoEngine(JarMojoContext jarMojoContext) {
        this.jarMojoContext = jarMojoContext;
    }

    public JarMojoCommandBuilder routeJarMojoCommandBuilder(){
        if ("system".equalsIgnoreCase(jarMojoContext.getScope())){
            return new SystemJarMojoCommandBuilder(jarMojoContext);
        }else{
            return new JarMojoCommandBuilder(jarMojoContext);
        }
    }

    public void run() throws Exception{
        JarMojoCommandBuilder jarMojoCommandBuilder = routeJarMojoCommandBuilder();
        List<String> downloadJarCommand = jarMojoCommandBuilder.buildDownloadJarCommands();
        if (downloadJarCommand!=null && downloadJarCommand.size()>0){
            ShellUtil.runShell(jarMojoContext.getMavenHome(),downloadJarCommand);
        }
        List<String> installFileCommand = jarMojoCommandBuilder.buildInstallFileCommands();
        if (installFileCommand!=null && installFileCommand.size()>0){
            ShellUtil.runShell(jarMojoContext.getMavenHome(),installFileCommand);
        }
    }
}
