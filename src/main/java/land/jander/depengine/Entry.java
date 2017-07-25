package land.jander.depengine;

import java.util.Set;
import com.google.common.collect.Sets;

/**
 * Maps a generator to it's dependencies.
 *
 * @param <K>   the type of keys
 * @param <P>   the type of products
 */
class Entry<K, P>
{
    @SafeVarargs
    public Entry(
        Generator<K, P> generator,
        Depengine<K, P>.Dependency... dependencies)
    {
        this.generator = generator;
        this.dependencies = Sets.newHashSet(dependencies);
    }

    public Entry(
        Generator<K, P> generator,
        Set<Depengine<K, P>.Dependency> dependencies)
    {
        this.generator = generator;
        this.dependencies = dependencies;
    }

    public final Generator<K, P> generator;
    public final Set<Depengine<K, P>.Dependency> dependencies;
}
