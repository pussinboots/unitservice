package fra.unit.runner;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;
import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;
import java.util.*;
import java.io.*;
import java.lang.reflect.*;

/**
 * Discovers all JUnit tests in a jar file and runs them in a suite.
 */
public final class JUnitJarRunner {
    private final static String JARFILE = "target/unitservice-tests.jar";

    public JUnitJarRunner() {
    }

    public static void addJar(String s) throws Exception {
        File f = new File(s);
        URL u = f.toURL();
        URLClassLoader urlClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        Class urlClass = URLClassLoader.class;
        Method method = urlClass.getDeclaredMethod("addURL", new Class[]{URL.class});
        method.setAccessible(true);
        method.invoke(urlClassLoader, new Object[]{u});
    }

    public static void runTest(String jarFile) throws Exception {
        addJar(jarFile);
        JUnitCore core=new JUnitCore();
        Class<?>[] classes=AllTestsRunner.findTestClasses(jarFile);
        for (Class<?> clazz : classes) {
            System.out.println(clazz.getName());
        }
        List<Failure> missingClasses=new ArrayList<Failure>();
        Result result=core.run(classes);
        result.wasSuccessful();
    }

    public static void main(String[] argv) throws Exception {
        runTest(JARFILE);
    }

    public static class AllTestsRunner {

        private static Class<?>[] findTestClasses(String jarFile) {
            List<String> classFiles = findClasses(jarFile);
            
            List<Class<?>> classes = convertToClasses(classFiles);
            return classes.toArray(new Class[classes.size()]);
        }

        public static final List<String> findClasses(String jarFile) {
            JarFile jf;
            List<String> classFiles = new ArrayList<String>();
            try {
                jf = new JarFile(jarFile);
                for (Enumeration<JarEntry> e = jf.entries(); e.hasMoreElements();) {
                    String name = e.nextElement().getName();
                    System.out.println(name);
                    if (name.contains("Test")
                            && !name.contains("$"))
                        classFiles.add(name.replaceAll("/", ".")
                                .substring(0, name.length() - 6));
                }
                jf.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return classFiles;
        }

        private static List<Class<?>> convertToClasses(
                final List<String> classFiles) {
            List<Class<?>> classes = new ArrayList<Class<?>>();
            for (String name : classFiles) {
                Class<?> c;
                try {
                    c = Class.forName(name);
                }
                catch (ClassNotFoundException e) {
                    throw new AssertionError(e);
                }
                if (!Modifier.isAbstract(c.getModifiers())) {
                    classes.add(c);
                }
            }
            return classes;
        }
    }

}