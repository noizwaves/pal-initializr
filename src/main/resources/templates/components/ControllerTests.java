package {{packageName}};

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class {{componentPrefix}}ControllerTests {
    {{componentPrefix}}Controller controller;

    @Before
    public void setup() throws Exception {
        controller = new {{componentPrefix}}Controller();
    }

    @Test
    public void demo() throws Exception {
        String result = controller.demo();

        assertThat(result).isEqualTo("This is a demo");
    }
}