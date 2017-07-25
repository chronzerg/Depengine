package land.jander.depengine;

import java.util.Map;

/**
 * A generator method which produces a 
 * desired product.
 *
 * @param <K>   the type of keys
 * @param <P>   the type of products
 */
@FunctionalInterface
public interface Generator<K, P>
{
   P generate(Map<K, P> dependencies);
}
