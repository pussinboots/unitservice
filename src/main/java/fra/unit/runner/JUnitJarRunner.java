package fra.unit.runner;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;
import java.util.*;
import java.io.*;
import java.lang.reflect.*;

/**
 * Discovers all JUnit tests in a jar file and runs them in a suite.
 */
@RunWith(JUnitJarRunner.AllTestsRunner.class)
public final class JUnitJarRunner {
    private final static String JARFILE = "target/unitservice-tests.jar";

    public JUnitJarRunner() {
    }

    public static void main(String[] argv) {
    	for(String name : AllTestsRunner.findClasses()) {
            System.out.println(name);
        }
    	/*JUnitCore core=new JUnitCore();
		argv=new String[]{TestDatasetAccessorHTTP.class.getName()};
		List<Class<?>> classes=new ArrayList<Class<?>>();
		List<Failure> missingClasses=new ArrayList<Failure>();
		for (  String each : argv)   try {
			classes.add(Class.forName(each));
		}
		catch (  ClassNotFoundException e) {
			System.out.println("Could not find class: " + each);
			Description description=Description.createSuiteDescription(each);
			Failure failure=new Failure(description,e);
			missingClasses.add(failure);
		}
		core.addListener(new TextListenerOneLine(System.out));
		Result result=core.run(classes.toArray(new Class<?>[0]));
		System.exit(result.wasSuccessful() ? 0 : 1);*/
    }

    public static class AllTestsRunner extends Suite {

        public AllTestsRunner(final Class<?> clazz) throws InitializationError {
            super(clazz, findTestClasses());
        }

        private static Class<?>[] findTestClasses() {
            List<String> classFiles = findClasses();
            
            List<Class<?>> classes = convertToClasses(classFiles);
            return classes.toArray(new Class[classes.size()]);
        }

        public static final List<String> findClasses() {
            JarFile jf;
            List<String> classFiles = new ArrayList<String>();
            try {
                jf = new JarFile(JARFILE);
                for (Enumeration<JarEntry> e = jf.entries(); e.hasMoreElements();) {
                    String name = e.nextElement().getName();
                    if (name.startsWith("suneido/") && name.startsWith("Test.class")
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