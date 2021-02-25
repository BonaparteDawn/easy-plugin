package com.github.BonaparteDawn.easyplugin.engine;

import com.github.BonaparteDawn.easyplugin.context.JarMojoContext;
import java.util.List;

/**
 *
 * @author BonaparteDawn
 */
public class SystemJarMojoCommandBuilder extends JarMojoCommandBuilder{

    public SystemJarMojoCommandBuilder(JarMojoContext jarMojoContext) {
        super(jarMojoContext);
    }

    @Override
    public List<String> buildDownloadJarCommands() {
        return null;
    }

}
