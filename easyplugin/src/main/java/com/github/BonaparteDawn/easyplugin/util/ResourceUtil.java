package com.github.BonaparteDawn.easyplugin.util;


import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;
import java.io.Closeable;
import java.io.File;
import java.util.Stack;

/**
 * TODO
 *
 * @author BonaparteDawn
 */
public class ResourceUtil {

    private static Log log = new SystemStreamLog();

    public static void release(Stack<? extends Object> resource){
        if (resource!=null){
            while (!resource.isEmpty()){
                try {
                    Object obj = resource.pop();
                    if (obj!=null){
                        if (obj instanceof File){
                            File file = (File)obj;
                            if (file.exists()){
                                log.info("delete file: "+file.getAbsolutePath());
                                file.delete();
                            }
                        }else if (obj instanceof Closeable){
                            ((Closeable)obj).close();
                        }else{
                            throw new RuntimeException("不支持的释放资源类型:"+obj.getClass());
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
}
