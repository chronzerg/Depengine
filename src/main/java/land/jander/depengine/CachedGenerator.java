package land.jander.depengine;

import java.util.Map;

/**
 * A wrapper around a generator which caches the
 * product after the first time it's produced.
 * Subsequent call to this generator simply return
 * the cached product.
 *
 * @param <K>   the type of keys
 * @param <P>   the type of products
 */
class CachedGenerator<K, P> implements Generator<K, P> 
{
    private final Generator<K, P> generator;
    private P cachedValue;

    public CachedGenerator(Generator<K, P> generator)
    {
        this.generator = generator;
    }

    public P generate(Map<K, P> dependencies)
    {
        if (cachedValue == null) {
            cachedValue = generator.generate(dependencies);
        }
        return cachedValue;
    }
}
