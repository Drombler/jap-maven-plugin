/*
 *         COMMON DEVELOPMENT AND DISTRIBUTION LICENSE (CDDL) Notice
 *
 * The contents of this file are subject to the COMMON DEVELOPMENT AND DISTRIBUTION LICENSE (CDDL)
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.opensource.org/licenses/cddl1.txt
 *
 * The Original Code is Drombler.org. The Initial Developer of the
 * Original Code is Florian Brunner (GitHub user: puce77).
 * Copyright 2017 Drombler.org. All Rights Reserved.
 *
 * Contributor(s): .
 */
package org.drombler.jstore.jap.maven.plugin;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.drombler.jstore.protocol.json.Application;
import org.drombler.jstore.protocol.json.ApplicationId;
import org.drombler.jstore.protocol.json.JreInfo;
import org.drombler.jstore.protocol.json.ManagedNativeComponents;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;


/**
 * Generates the JAP resources (currently only: application.json) in the outputDirectory (default: ${project.build.directory}/generated-sources/jap) to be packaged in the JAP archive.
 */
@Mojo(name = "generate-application-resources", defaultPhase = LifecyclePhase.GENERATE_RESOURCES)
public class GenerateApplicationResourcesMojo extends AbstractJapMojo {

    @Parameter(defaultValue = "${project.groupId}", readonly = true, required = true)
    private String groupId;

    @Parameter(defaultValue = "${project.artifactId}", readonly = true, required = true)
    private String artifactId;

    @Parameter(defaultValue = "${project.version}", readonly = true, required = true)
    private String version;

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;

    /**
     * {@inheritDoc }
     */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            final Path outputDirPath = getOutputDirectoryPath();
            if (!Files.exists(outputDirPath)) {
                Files.createDirectories(outputDirPath);
            }
            createApplicationJsonFile(getApplicationJsonFilePath(outputDirPath));
        } catch (IOException ex) {
            throw new MojoExecutionException("Generating application resources failed!", ex);
        }
    }

    private void createApplicationJsonFile(Path applicationJsonFilePath) throws IOException {
        getLog().info("Generating application.json file: " + applicationJsonFilePath);

        Application application = createApplicationInfo();

        ObjectMapper objectMapper = new ObjectMapper();
        String indented = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(application);
        Files.write(applicationJsonFilePath, indented.getBytes(Charset.forName("UTF-8")));
    }

    private Application createApplicationInfo() {
        Application application = new Application();
        ApplicationId applicationId = new ApplicationId();
        applicationId.setGroupId(project.getGroupId());
        applicationId.setArtifactId(project.getArtifactId());
        application.setApplicationId(applicationId);
        application.setVersion(project.getVersion());
        ManagedNativeComponents managedNativeComponents = new ManagedNativeComponents();
        JreInfo jreInfo = new JreInfo();
//        System.getProperties().list(System.out);
        jreInfo.setJreVendorId(System.getProperty("java.vendor"));
        jreInfo.setJavaSpecificationVersion(System.getProperty("java.specification.version"));
        managedNativeComponents.setJreInfo(jreInfo);
        application.setManagedNativeComponents(managedNativeComponents);
        return application;
    }

}
