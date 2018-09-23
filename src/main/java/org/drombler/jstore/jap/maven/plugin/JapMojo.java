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

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.zip.ZipArchiver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Generates the JAP file.
 */
@Mojo(name = "jap", defaultPhase = LifecyclePhase.PACKAGE)
public class JapMojo extends AbstractJapMojo {


    @Parameter(defaultValue = "${project.build.finalName}", required = true)
    private String finalName;

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    @Component(role = Archiver.class, hint = "zip")
    private ZipArchiver zipArchiver;

    @Component
    private ArtifactFactory artifactFactory;
    /**
     * {@inheritDoc }
     */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            final Path targetDirectoryPath = getTargetDirectoryPath();
            if (!Files.exists(targetDirectoryPath)) {
                Files.createDirectories(targetDirectoryPath);
            }
            createJapFile();
        } catch (IOException ex) {
            throw new MojoExecutionException("Generating application resources failed!", ex);
        }
    }

    private void createJapFile() throws IOException, MojoExecutionException {
        Path targetDirectoryPath = getTargetDirectoryPath();
        Path applicationJsonFilePath = getApplicationJsonFilePath(getOutputDirectoryPath());
        Path applicationZipFilePath = getApplicationZipFilePath();
        if (Files.exists(applicationJsonFilePath)) {
            getLog().info("Adding " + applicationJsonFilePath.getFileName().toString() + " as application.zip ...");
            zipArchiver.addFile(applicationJsonFilePath.toFile(), "application.json");
        } else {
            throw new MojoExecutionException("application.json file does not exist! Current value: " + applicationJsonFilePath.toString());
        }
        if (Files.exists(applicationZipFilePath)) {
            getLog().info("Adding " + applicationZipFilePath.getFileName().toString() + " as application.zip ...");
            zipArchiver.addFile(applicationZipFilePath.toFile(), "application.zip");
        } else {
            throw new MojoExecutionException("applicationZipFile does not exist! Current value: " + applicationZipFilePath.toString());
        }
        zipArchiver.setDestFile(targetDirectoryPath.resolve(finalName + ".jap").toFile());
        zipArchiver.createArchive();
        Artifact buildArtifact = artifactFactory.createBuildArtifact(project.getGroupId(), project.getArtifactId(), project.getVersion(), project.getPackaging());
        buildArtifact.setFile(zipArchiver.getDestFile());
        project.setArtifact(buildArtifact);
    }

}
