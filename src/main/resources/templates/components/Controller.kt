package {{packageName}}

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = arrayOf("/demo"))
class {{componentPrefix}}Controller {
    @GetMapping
    fun demo(): String {
        return "This is a demo";
    }
}
