package land.jander.depengine;

import java.util.Map;
import java.util.HashMap;

/**
 * Dependency Generation Engine.
 * A depengine is a collection of generator methods capable of
 * producing desired products. These generators may depend on
 * the products of other generators registered in a Depengine
 * instance. The depengine helps facilitate the execution of
 * these generators, and the propogation of depencies to their
 * dependent generators.
 *
 * Each generator is registered with a key and a dependency
 * list. The key provides an ID for the generator and it's
 * product. The depedency list specifies what other products
 * must be produced and provided to said generator before the
 * generator can execute.
 *
 * @param <K>   the type of keys
 * @param <P>   the type of products
 */
public class Depengine<K, P>
{
    /**
     * Represents a unresolved dependency to a
     * particular product.
     */
    public class Dependency
    {
        private final Depengine<K, P> engine;

        public Dependency(Depengine<K, P> engine, K key)
        {
            this.engine = engine;
            this.key = key;
        }

        /**
         * The key of the product depended on.
         */
        public final K key;

        /**
         * Resolves the dependency by retrieving the
         * desired product from it's depengine.
         */
        public P resolve() throws DependencyException
        {
            return engine.getProduct(key);
        }
    }

    private final Map<K, P> initials;
    private final Map<K, Entry<K, P>> entries
        = new HashMap<>();

    public Depengine(String id, Map<K, P> initials)
    {
        this.id = id;
        this.initials = initials;
    }

    public Depengine(String id)
    {
        this.id = id;
        this.initials = new HashMap<>();
    }

    /**
     * The ID of this depengine.
     */
    public final String id;

    /**
     * Registers a generator with this depengine.
     */
    @SafeVarargs
    public final void addGenerator(
        K key,
        Generator<K, P> generator,
        Depengine<K, P>.Dependency... dependencies)
    {
        // Wrap the given generator in a CachedGenerator
        // so that any product is only generated once.
        entries.put(key, new Entry<>(
            new CachedGenerator<>(generator),
            dependencies));
    }

    /**
     * Creates a dependency on a product produced by
     * this depengine.
     */
    public Dependency getDependency(K key)
    {
        return this.new Dependency(this, key);
    }

    /**
     * Executes all generators registered with this
     * depengine, returning the products as values in
     * a map.
     * The key associated with each generator is used
     * as the key for it's product in the returned map.
     */
    public Map<K, P> getProducts() throws DependencyException
    {
        Map<K, P> products = new HashMap<>();
        for (K key : entries.keySet())
        {
            products.put(key, getProduct(key));
        }
        return products;
    }

    /**
     * Generates a particular product, generating any
     * dependencies necessary.
     */
    private P getProduct(K key) throws DependencyException
    {
        // If we have a registered generator for this key,
        // use this for generate the product.
        if (entries.containsKey(key))
        {
            Entry<K, P> mapping = entries.get(key);
            Map<K, P> dependencies = new HashMap<>();

            for (Depengine<K, P>.Dependency d : mapping.dependencies)
            {
                try
                {
                    dependencies.put(d.key, d.resolve());
                }
                catch (DependencyException ex)
                {
                    // If the destination of this exception hasn't
                    // been set, it means this depengine is the
                    // destination. Throw a new exception that
                    // includes this information. Otherwise, just
                    // rethrow the exception.
                    if (ex.destination == null)
                    {
                        throw new DependencyException(
                            ex.key,
                            ex.source,
                            this.id);
                    }
                    else
                    {
                        throw ex;
                    }
                }
            }

            return mapping.generator.generate(dependencies);
        }

        // If we don't have a registered generator, but we
        // have an initial for this key, simply return the
        // initial.
        else if (initials.containsKey(key))
        {
            return initials.get(key);
        }

        // If we don't have a generator or initial for this
        // key, throw a new dependency exception.
        else
        {
            // Note that at this point we don't know the destination
            // for this unmet dependency, so we only fill out the
            // key and source parts of the exception.
            throw new DependencyException(key.toString(), this.id);
        }
    }
}
