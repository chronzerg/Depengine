package land.jander.depengine;

/**
 * Thrown when a dependency is missing.
 * This exception can be instantiated with or without
 * a destination value. This is due to the way this
 * exception propogates. Initially, when a depengine
 * realizes it cannot fulfill a dependency, an instance
 * of this exception is created without knowledge of
 * the destination. While bubbling up the call stack,
 * the exception will be caught by the destination
 * depengine. At this point, a new exception instance
 * will be thrown including the destination info.
 */
public class DependencyException extends Exception
{
    static final long serialVersionUID = 1L;

    public DependencyException(
            String key,
            String source)
    {
        this.key = key;
        this.source = source;
        this.destination = null;
    }

    public DependencyException(
            String key,
            String source,
            String destination)
    {
        this.key = key;
        this.source = source;
        this.destination = destination;
    }

    /**
     * The key of the missing product.
     */
    public final String key;

    /**
     * The ID of the depengine providing the
     * product.
     */
    public final String source;

    /**
     * The ID of the depengine receiving the
     * product.
     */
    public final String destination;
}
