package com.github.BonaparteDawn.easyplugin.engine;


import com.github.BonaparteDawn.easyplugin.bean.Dependency;
import com.github.BonaparteDawn.easyplugin.context.JarMojoContext;
import com.github.BonaparteDawn.easyplugin.util.ResourceUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.maven.settings.Settings;
import java.io.*;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

/**
 *
 * @author BonaparteDawn
 */
public class JarMojoCommandBuilder {
    private JarMojoContext jarMojoContext;

    public JarMojoCommandBuilder(JarMojoContext jarMojoContext) {
        this.jarMojoContext = jarMojoContext;
    }

    public List<String> buildDownloadJarCommands(){
        List<String> commands = new ArrayList<>();
        Dependency dependency = jarMojoContext.getDependency();
        commands.add(buildDriver());
        commands.add("dependency:get");
        Map<String,String> arg = new LinkedHashMap<>();
        arg.put("groupId",dependency.getGroupId());
        arg.put("artifactId",dependency.getArtifactId());
        arg.put("version",dependency.getVersion());
        arg.put("transitive","false");
        Iterator<Map.Entry<String, String>> iterator = arg.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String, String> next = iterator.next();
            commands.add("-D"+next.getKey()+"="+next.getValue());
        }
        return commands;
    }

    public List<String> buildInstallFileCommands(){
        List<String> commands = new ArrayList<>();
        Dependency dependency = jarMojoContext.getDependency();
        commands.add(buildDriver());
        commands.add("install:install-file");
        Map<String,String> arg = new LinkedHashMap<>();
        if (jarMojoContext.getEncode()!=null){
            arg.put("file.encoding",jarMojoContext.getEncode());
        }
        if (jarMojoContext.getMavenRepoLocal()!=null){
            arg.put("maven.repo.local",jarMojoContext.getMavenRepoLocal());
        }else{
            if (jarMojoContext.getSettings()!=null){
                Settings settings = jarMojoContext.getSettings();
                if (settings.getLocalRepository()!=null){
                    arg.put("maven.repo.local",settings.getLocalRepository());
                }
            }
        }
        arg.put("groupId",dependency.getGroupId());
        arg.put("artifactId",dependency.getArtifactId());
        if (dependency.getNewVersion()!=null){
            arg.put("version",dependency.getNewVersion());
        }else{
            arg.put("version",dependency.getVersion());
        }
        arg.put("packaging","jar");
        arg.put("file",systemPath());
        Iterator<Map.Entry<String, String>> iterator = arg.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String, String> next = iterator.next();
            commands.add("-D"+next.getKey()+"="+next.getValue());
        }
        return commands;
    }

    public String systemPath(){
        File systemPathFile = null;
        try {
            String tempDir = System.getProperty("java.io.tmpdir");
            Dependency dependency = jarMojoContext.getDependency();
            File sourceJar = null;
            if (dependency.getSystemPath()!=null){
                sourceJar = new File(dependency.getSystemPath());
            }else{
                StringJoiner pathJoiner = new StringJoiner(File.separator);
                if (jarMojoContext.getMavenRepoLocal()!=null){
                    pathJoiner.add(jarMojoContext.getMavenRepoLocal());
                }
                if (dependency.getGroupId()!=null){
                    String[] sps = dependency.getGroupId().split("\\.");
                    for (String s:sps){
                        pathJoiner.add(s);
                    }
                }
                if (dependency.getArtifactId()!=null){
                    String[] sps = dependency.getArtifactId().split("\\.");
                    for (String s:sps){
                        pathJoiner.add(s);
                    }
                }
                if (dependency.getVersion()!=null){
                    pathJoiner.add(dependency.getVersion());
                }
                if (dependency.getArtifactId()!=null && dependency.getVersion()!=null){
                    pathJoiner.add(dependency.getArtifactId()+"-"+dependency.getVersion()+".jar");
                }
                if (dependency.getNewVersion()==null){
                    //如果不存在新版本号，那么就需要拷贝一份原版本号的文件为临时文件，同时删除原文件
                    File pathFile = new File(pathJoiner.toString());
                    sourceJar = new File(tempDir+File.separator+UUID.randomUUID().toString()+".jar");
                    //该临时文件最终需要被释放
                    jarMojoContext.getResourceStock().push(sourceJar);
                    FileUtils.copyFile(pathFile,sourceJar);
                    pathFile.delete();
                }else{
                    sourceJar = new File(pathJoiner.toString());
                }
            }
            if (dependency.getCleans()!=null && !dependency.getCleans().isEmpty()){
                systemPathFile = new File(tempDir+File.separator+UUID.randomUUID().toString()+".jar");
                //该临时文件最终需要被释放
                jarMojoContext.getResourceStock().push(systemPathFile);
                //更新jar包内容
                updateJarFile(systemPathFile,sourceJar,dependency.getCleans());
            }else{
                systemPathFile = sourceJar;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return systemPathFile.getAbsolutePath();
    }



    /**
     * jar包的文件更新
     * @param newJarFile
     * @param sourceJarFile
     * @param cleans
     */
    public void updateJarFile(File newJarFile, File sourceJarFile, List<String> cleans) throws Exception {
        Stack<Closeable> ioStack = new Stack<>();
        try {
            if (newJarFile.exists()){
                newJarFile.delete();
            }
            FileOutputStream fileOutputStream =new FileOutputStream(newJarFile);
            ioStack.push(fileOutputStream);
            JarOutputStream jarOutputStream = new JarOutputStream(fileOutputStream);
            ioStack.push(jarOutputStream);
            JarFile jarFile = new JarFile(sourceJarFile);
            Enumeration<JarEntry> jarEntries = jarFile.entries();
            while (jarEntries.hasMoreElements()) {
                JarEntry entry = jarEntries.nextElement();
                boolean skip = false;
                for (String clean:cleans){
                    if (entry.getName().startsWith(clean)){
                        skip = true;
                    }
                }
                if (!skip){
                    InputStream inputStream = jarFile.getInputStream(entry);
                    ioStack.push(inputStream);
                    jarOutputStream.putNextEntry(entry);
                    IOUtils.copy(inputStream,jarOutputStream);
                }
            }
        }catch (Exception e){
            throw e;
        }finally {
            ResourceUtil.release(ioStack);
        }
    }


    protected String buildDriver(){
        StringJoiner driverJoiner = new StringJoiner(File.separator);
        if (jarMojoContext.getMavenHome()!=null){
            File file = new File(jarMojoContext.getMavenHome());
            if (file.exists()){
                driverJoiner.add(file.getAbsolutePath());
            }
        }
        driverJoiner.add("bin");
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.indexOf("windows")>=0){
            driverJoiner.add("mvn.cmd");
        }else{
            driverJoiner.add("mvn");
        }
        return driverJoiner.toString();
    }
}
