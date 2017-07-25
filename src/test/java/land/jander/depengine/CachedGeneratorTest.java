package land.jander.depengine;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import java.util.Map;

public class CachedGeneratorTest
{
    private int value = 0;

    private int generator(Map<String, Integer> dependencies)
    {
        return this.value++;
    }

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
