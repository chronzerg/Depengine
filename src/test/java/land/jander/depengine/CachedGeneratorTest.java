package land.jander.depengine;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import java.util.Map;

public class CachedGeneratorTest
{
    private int value = 0;

    /**
     * Returns a different integer at every invocation.
     */
    private int generator(Map<String, Integer> dependencies)
    {
        return this.value++;
    }

    /**
     * Tests that the CachedGenerator actually caches the
     * product of it's underlying generator.
     * This is done by invoking the generator twice and
     * making sure the product remains the same, even
     * though the given generator returns a different
     * product on every execution.
     */
    @Test
    public void cacheValue()
    {
        CachedGenerator<String, Integer> gen
            = new CachedGenerator<>(this::generator);

        int firstValue = gen.generate(null);
        int secondValue = gen.generate(null);

        assertEquals(firstValue, secondValue);
    }
}
