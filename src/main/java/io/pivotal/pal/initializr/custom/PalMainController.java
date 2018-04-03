package io.pivotal.pal.initializr.custom;

import io.spring.initializr.generator.BasicProjectRequest;
import io.spring.initializr.generator.ProjectGenerator;
import io.spring.initializr.generator.ProjectRequest;
import io.spring.initializr.metadata.DependencyMetadataProvider;
import io.spring.initializr.metadata.InitializrMetadataProvider;
import io.spring.initializr.util.TemplateRenderer;
import io.spring.initializr.web.project.MainController;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.servlet.resource.ResourceUrlProvider;

import java.util.Map;

public class PalMainController extends MainController {
    public PalMainController(InitializrMetadataProvider metadataProvider, TemplateRenderer templateRenderer, ResourceUrlProvider resourceUrlProvider, ProjectGenerator projectGenerator, DependencyMetadataProvider dependencyMetadataProvider) {
        super(metadataProvider, templateRenderer, resourceUrlProvider, projectGenerator, dependencyMetadataProvider);
    }

    @Override
    public BasicProjectRequest projectRequest(@RequestHeader Map<String, String> headers) {
        ProjectRequest request = new PalProjectRequest();
        request.getParameters().putAll(headers);
        request.initialize(metadataProvider.get());
        return request;
    }
}
