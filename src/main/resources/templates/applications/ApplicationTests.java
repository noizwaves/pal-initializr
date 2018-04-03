package {{packageName}};

import org.junit.Test;
import org.junit.runner.RunWith;
{{#isWeb}}
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;
{{/isWeb}}
{{testImports}}
{{#newTestInfrastructure}}
{{#isWeb}}
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

{{/isWeb}}
@RunWith(SpringRunner.class)
{{^isWeb}}
@SpringBootTest
{{/isWeb}}
{{#isWeb}}
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
{{/isWeb}}
{{/newTestInfrastructure}}
{{^newTestInfrastructure}}
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {{applicationName}}.class)
{{/newTestInfrastructure}}
{{testAnnotations}}public class {{applicationName}}Tests {

{{^isWeb}}
	@Test
	public void contextLoads() {
	}

{{/isWeb}}
{{#isWeb}}
	@Value("${local.server.port}")
	String port;

	@Test
	public void testDemo() throws IOException {
		RestTemplate restTemplate = new RestTemplate();

		String response = restTemplate.getForObject(
				"http://localhost:"+port+"/demo", String.class);

		assertThat(response).isEqualTo("This is a demo");
	}
{{/isWeb}}
}
