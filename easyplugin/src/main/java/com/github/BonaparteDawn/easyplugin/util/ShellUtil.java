package com.github.BonaparteDawn.easyplugin.util;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;
import java.io.File;
import java.util.*;

/**
 *
 * @author BonaparteDawn
 */
public class ShellUtil {
    private static Log log = new SystemStreamLog();
    public static void runShell(String home,List<String> command) throws Exception{
        ProcessBuilder processBuilder = new ProcessBuilder();
        try {
            processBuilder.redirectErrorStream(true);
            processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);
            if (home!=null && home.length()>0){
                processBuilder.directory(new File(home));
            }
            processBuilder.command().addAll(command);
            print(processBuilder);
            Process process = processBuilder.start();
            process.waitFor();
            process.destroy();
        }catch (Exception e){
            throw e;
        }
    }

    private static void print(ProcessBuilder processBuilder){
        StringJoiner joiner = new StringJoiner(" ");
        if (processBuilder.command()!=null){
            File directory = processBuilder.directory();
            if (directory==null){
                log.info("shell home: "+null);
            }else{
                log.info("shell home: "+directory.getAbsolutePath());
            }
            for (String sp:processBuilder.command()){
                joiner.add(sp);
            }
            log.info("cmd : "+joiner.toString());
        }
    }
}
