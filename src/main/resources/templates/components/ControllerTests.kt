package {{packageName}}

import org.junit.Assert.assertEquals
import org.junit.Test as test

class {{componentPrefix}}ControllerTests() {
    @test fun demo() {
        val controller = {{componentPrefix}}Controller()

        assertEquals(controller.demo(), "This is a demo")
    }
}