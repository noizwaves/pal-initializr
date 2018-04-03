package io.pivotal.pal.initializr.custom;

import io.spring.initializr.generator.ProjectGenerator;
import io.spring.initializr.generator.ProjectRequest;
import io.spring.initializr.metadata.Dependency;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.metadata.MetadataElement;
import io.spring.initializr.util.Version;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

public class PalProjectGenerator extends ProjectGenerator {
    @Override
    protected File generateProjectStructure(ProjectRequest request, Map<String, Object> model) {
        File rootDir;
        try {
            rootDir = File.createTempFile("tmp", "", getTemporaryDirectory());
        }
        catch (IOException e) {
            throw new IllegalStateException("Cannot create temp dir", e);
        }
        addTempFile(rootDir.getName(), rootDir);
        rootDir.delete();
        rootDir.mkdirs();

        File dir = initializerProjectDir(rootDir, request);

        String applicationDirName = "";
        String componentDirName = "";


        if (isGradleBuild(request)) {
            String settings = new String(doGenerateGradleSettings(model));
            writeText(new File(dir, "settings.gradle"), settings);
            writeGradleWrapper(dir, Version.safeParse(request.getBootVersion()));
            applicationDirName = String.format("applications/%s-server/", request.getArtifactId());
            componentDirName = String.format("components/%s-library/", request.getArtifactId());

            palGradleBuild(request, dir, applicationDirName, componentDirName, model);
        }
        else {
            String pom = new String(doGenerateMavenPom(model));
            writeText(new File(dir, "pom.xml"), pom);
            writeMavenWrapper(dir);
        }

        generateGitIgnore(dir, request, model);

        String applicationName = request.getApplicationName();
        String language = request.getLanguage();

        String codeLocation = language;
        File src = new File(new File(dir, applicationDirName+"src/main/" + codeLocation),
                request.getPackageName().replace(".", "/"));
        src.mkdirs();
        String extension = ("kotlin".equals(language) ? "kt" : language);
        write(new File(src, applicationName + "." + extension),
                "applications/Application." + extension, model);

        File componentSrc = new File(new File(dir, componentDirName+"src/main/" + codeLocation),
                request.getPackageName().replace(".", "/"));
        componentSrc.mkdirs();
        write(new File(componentSrc, getComponentPrefix(request)+"Controller." + extension),
                "components/Controller." + extension, model);


        if ("war".equals(request.getPackaging())) {
            String fileName = "ServletInitializer." + extension;
            write(new File(src, fileName), fileName, model);
        }

        File test = new File(new File(dir, applicationDirName+"src/test/" + codeLocation),
                request.getPackageName().replace(".", "/"));
        test.mkdirs();
        setupTestModel(request, model);
        write(new File(test, applicationName + "Tests." + extension),
                "applications/ApplicationTests." + extension, model);

        File componentsTest = new File(new File(dir, componentDirName+"src/test/" + codeLocation),
                request.getPackageName().replace(".", "/"));
        componentsTest.mkdirs();
        setupTestModel(request, model);
        write(new File(componentsTest, getComponentPrefix(request) + "ControllerTests." + extension),
                "components/ControllerTests." + extension, model);


        File resources = new File(dir, applicationDirName+"src/main/resources");
        resources.mkdirs();
        write(new File(resources, "application.properties"), "applications/application.properties", model);

        if (request.hasWebFacet()) {
            new File(dir, applicationDirName+"src/main/resources/templates").mkdirs();
            new File(dir, applicationDirName+"src/main/resources/static").mkdirs();
        }
        return rootDir;
    }

    @Override
    protected Map<String, Object> resolveModel(ProjectRequest originalRequest) {
        Map<String, Object> model = super.resolveModel(originalRequest);

        InitializrMetadata metadata = metadataProvider.get();
        ProjectRequest request = requestResolver.resolve(originalRequest, metadata);
        List<Dependency> dependencies = request.getResolvedDependencies();

        model.put("isWeb", hasDependency(request, asList("web")));

        model.put("hasFlyway", hasDependency(request, asList("flyway")));
        model.put("h2CompatibilityMode", determineH2CompatibilityMode(dependencies));
        model.put("artifactId_underscored", StringUtils.replace(request.getArtifactId(), "-", "_"));
        model.put("databaseDependencies",
                filterDependencies(dependencies, DATABASES.keySet()));

        return model;
    }

    private void palGradleBuild(ProjectRequest request, File dir, String applicationDirName, String componentDirName, Map<String, Object> model) {
        generateFile(dir, "README.md", "README.md", model);
        generateFile(dir, "settings.gradle", "settings.gradle", model);
        generateFile(dir, "gradle.properties", "gradle.properties", model);
        generateFile(dir, "starter-build.gradle", "build.gradle", model);
        generateFile(dir, "applications/application-build.gradle", applicationDirName, "build.gradle", model);
        if (Boolean.TRUE.equals(model.get("isWeb"))) {
            generateFile(dir, "components/component-build.gradle", componentDirName, "build.gradle", model);
        }
        if (Boolean.TRUE.equals(model.get("hasFlyway"))){
            initializerDatabaseDir(dir, request);
            generateFile(dir, "databases/db.gradle", "databases/", "db.gradle", model);
            generateFile(dir, "databases/build.gradle", "databases/"+request.getArtifactId()+"-database/", "build.gradle", model);
            generateFile(dir, "databases/initial.sql", "databases/"+request.getArtifactId()+"-database/src/main/resources/db/migrations/", "V1__initial.sql", model);
        }
    }

    private void generateFile(File dir, String templateName, String fileName, Map<String, Object> model) {
        String fileContent = new String(doGenerateTemplate(templateName, model));
        writeText(new File(dir, fileName), fileContent);
    }

    private void generateFile(File dir,String templateName,  String subDir, String fileName, Map<String, Object> model) {
        String fileContent = new String(doGenerateTemplate(templateName, model));
        if(StringUtils.isEmpty(subDir) == false) {
            File directory = new File(dir, subDir);
            directory.mkdirs();
        }
        writeText(new File(dir, subDir+fileName), fileContent);
    }

    private void initializerDatabaseDir(File rootDir, ProjectRequest request) {
        File dir = new File(rootDir, "databases/"+request.getArtifactId()+"-database/src/main/resources/db/migrations");
        dir.mkdirs();
    }

    private byte[] doGenerateTemplate(String templateName, Map<String, Object> model) {
        return templateRenderer.process(templateName, model).getBytes();
    }

    final private static Map<String, String> DATABASES;
    static {
        DATABASES = new HashMap<>();
        DATABASES.put("hsql", "HSQLDB");
        DATABASES.put("derby", "Derby");
        DATABASES.put("mysql", "MySQL");
        DATABASES.put("postgresql", "PostgreSQL");
        DATABASES.put("sqlserver", "MSSQLServer");
    }

    private String determineH2CompatibilityMode(List<Dependency> dependencies) {
        Set<String> dbTypes = DATABASES.keySet();
        return dependencies.stream()
                .filter(dependency -> dbTypes.contains(dependency.getId()))
                .map(dependency -> DATABASES.get(dependency.getId()))
                .findFirst()
                .orElse("");
    }

    private boolean hasDependency(ProjectRequest request, Collection<String> names) {
        return request.getResolvedDependencies().stream()
                .filter(dependency -> names.contains(dependency.getId()))
                .findFirst()
                .isPresent();
    }

    private static List<Dependency> filterDependencies(List<Dependency> dependencies,
                                                       Collection<String> values) {
        return dependencies.stream().filter(dep -> values.contains(dep.getId()))
                .sorted(Comparator.comparing(MetadataElement::getId))
                .collect(Collectors.toList());
    }

    private void generateGitIgnore(File dir, ProjectRequest request, Map<String, Object> requestModel) {
        Map<String, Object> model = new LinkedHashMap<>();
        model.putAll(requestModel);
        if (isMavenBuild(request)) {
            model.put("build", "maven");
            model.put("mavenBuild", true);
        }
        else {
            model.put("build", "gradle");
        }
        write(new File(dir, ".gitignore"), "gitignore.tmpl", model);
    }

    private static String getComponentPrefix(ProjectRequest projectRequest) {
        if (projectRequest instanceof PalProjectRequest) {
            return ((PalProjectRequest) projectRequest).getComponentPrefix();
        }

        return PalProjectRequest.FALLBACK_COMPONENT_PREFIX;
    }
}
