import com.github.rskupnik.basic.App;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AppTest {

    @Test
    public void test() {
        final App app = new App();

        assertEquals("testString", app.getTestString());
    }
}
