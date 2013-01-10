package clojure.osgi.internal;

import clojure.lang.RT;
import clojure.lang.Var;
import clojure.osgi.IClojureLoader;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author rmoquin
 */
public class ClojureLoader implements IClojureLoader {

  private ExecutorService executor = Executors.newSingleThreadExecutor();

  @Override
  public Object createInstance(final ClassLoader cl, final String className) throws Exception {
    return executor.submit(new Callable<Object>() {
      @Override
      public Object call() throws Exception {
        Thread.currentThread().setContextClassLoader(cl);
        Var.pushThreadBindings(RT.map(clojure.lang.Compiler.LOADER, cl));
        try {
          Class clazz = cl.loadClass(className);
          return clazz.newInstance();
        } finally {
          Var.popThreadBindings();
        }
      }
    }).get();
  }

  @Override
  public Object invoke(final ClassLoader cl, final Callable callable) throws Exception {
    return executor.submit(new Callable<Object>() {
      @Override
      public Object call() throws Exception {
        Thread.currentThread().setContextClassLoader(cl);
        Var.pushThreadBindings(RT.map(clojure.lang.Compiler.LOADER, cl));
        try {
          return callable.call();
        } finally {
          Var.popThreadBindings();
        }
      }
    }).get();
  }

  public void destroy() {
    this.executor.shutdownNow();
  }
}
