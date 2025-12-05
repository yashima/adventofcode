package de.delusions.tools;

import lombok.extern.slf4j.Slf4j;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Locale;

@Slf4j
public class DynamicCompiler {

    public static boolean compile(String filename) {
        List<String> options = List.of(
                "-d", "target",            // compiled classes go under ./out
                "-classpath", "target"     // or wherever your classpath should be
        );

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        try(StandardJavaFileManager fm = compiler.getStandardFileManager(null, Locale.ENGLISH, Charset.forName("UTF-8"))){
            Iterable<? extends JavaFileObject> units =
                    fm.getJavaFileObjectsFromStrings(List.of(filename));

            JavaCompiler.CompilationTask task = compiler.getTask(null,fm,null,options,null,units);
            return task.call();
        } catch (IOException e) {
            return false;
        }
    }



}
