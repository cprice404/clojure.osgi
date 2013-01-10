package clojure.osgi;

import java.util.concurrent.Callable;

/**
 *
 * @author rmoquin
 */
public interface IClojureLoader {

  public Object createInstance(final ClassLoader cl, final String className) throws Exception;

  public Object invoke(final ClassLoader cl, final Callable callable) throws Exception;
}
