package io.pivotal.pal.initializr.custom;

import io.spring.initializr.generator.ProjectRequest;
import io.spring.initializr.metadata.InitializrConfiguration;
import io.spring.initializr.metadata.InitializrMetadata;
import org.springframework.util.StringUtils;

public class PalProjectRequest extends ProjectRequest {
    public static String FALLBACK_COMPONENT_PREFIX = "Demo";

    private String componentPrefix;

    public String getComponentPrefix() {
        return componentPrefix;
    }

    public void setComponentPrefix(String componentPrefix) {
        this.componentPrefix = componentPrefix;
    }

    @Override
    public void resolve(InitializrMetadata metadata) {
        if (!StringUtils.hasText(getComponentPrefix())) {
            setComponentPrefix(capitalizeFirst(getName(), metadata.getConfiguration()));
        }
        super.resolve(metadata);
    }

    private static String capitalizeFirst(String name, InitializrConfiguration config) {
        if (!StringUtils.hasText(name)) {
            return FALLBACK_COMPONENT_PREFIX;
        }
        String candidate = StringUtils.capitalize(name);
        if (hasInvalidChar(candidate)
                || config.getEnv().getInvalidApplicationNames().contains(candidate)) {
            return FALLBACK_COMPONENT_PREFIX;
        }
        return candidate;
    }

    private static boolean hasInvalidChar(String text) {
        if (!Character.isJavaIdentifierStart(text.charAt(0))) {
            return true;
        }
        if (text.length() > 1) {
            for (int i = 1; i < text.length(); i++) {
                if (!Character.isJavaIdentifierPart(text.charAt(i))) {
                    return true;
                }
            }
        }
        return false;
    }
}
