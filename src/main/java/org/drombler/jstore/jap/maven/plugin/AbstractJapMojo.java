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

import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.drombler.jstore.jap.maven.plugin.util.PluginCoordinates;
import org.drombler.jstore.jap.maven.plugin.util.PluginProperty;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;

/**
 * @author puce
 */
public abstract class AbstractJapMojo extends AbstractMojo {

    protected static final PluginCoordinates MAVEN_ASSEMBLY_PLUGIN_COORDINATES = new PluginCoordinates("org.apache.maven.plugins", "maven-assembly-plugin");

    /**
     * The target directory for the JAP file.
     */
    @Parameter(property = "jap.targetDirectory",
            defaultValue = "${project.build.directory}", required = true)
    private File targetDirectory;

    /**
     * The output directory for generated files.
     */
    @Parameter(defaultValue = "${project.build.directory}/generated-sources/jap", required = true)
    private File outputDirectory;

    /**
     * The application ZIP file.
     */
    @Parameter(property = "jap.applicationZipFile",
            defaultValue = "${project.build.directory}/${project.artifactId}-${project.version}-applicationZip.zip", required = true)
    private File applicationZipFile;
    /**
     * The Maven project.
     */
    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    protected MavenProject project;

    protected void ensureMavenProperty(PluginCoordinates pluginCoordinates, PluginProperty pluginProperty, Optional<Plugin> plugin, String defaultValue) {
        Optional<String> propertyValue = getPropertyValue(pluginProperty, plugin);
        if (propertyValue.isPresent()) {
            getLog().info("Plugin (" + pluginCoordinates + "): Found property '" + pluginProperty + "' = '" + propertyValue.get() + "'");
        } else {
            getLog().info("Plugin (" + pluginCoordinates + "): Setting property '" + pluginProperty.getMavenPropertyName() + "' = '" + defaultValue + "'");
            project.getProperties().setProperty(pluginProperty.getMavenPropertyName(), defaultValue);
        }
    }

    private Optional<String> getPluginConfigurationPropertyValue(Plugin plugin, String propertyName) {
        if (plugin.getConfiguration() instanceof Xpp3Dom) {
            Xpp3Dom configurationDom = (Xpp3Dom) plugin.getConfiguration();
            final Xpp3Dom child = configurationDom.getChild(propertyName);
            if (child != null) {
                return Optional.ofNullable(child.getValue());
            } else {
                return Optional.empty();
            }
        } else {
            return Optional.empty();
        }
    }

    private Optional<String> getPropertyValue(final PluginProperty pluginProperty, Optional<Plugin> plugin) {
        Optional<String> propertyValue = Optional.empty();
        if (plugin.isPresent()) {
            propertyValue = getPluginConfigurationPropertyValue(plugin.get(), pluginProperty.getConfigurationPropertyName());
        }
        if (!propertyValue.isPresent() && project.getProperties().containsKey(pluginProperty.getMavenPropertyName())) {
            propertyValue = Optional.of(project.getProperties().getProperty(pluginProperty.getMavenPropertyName()));
        }
        return propertyValue;
    }

    protected Optional<Plugin> findPlugin(PluginCoordinates pluginCoordinates) {
        return project.getBuild().getPlugins().stream()
                .filter(pluginCoordinates::matchesPlugin)
                .findFirst();
    }

    /**
     * @return the targetDirectory
     */
    public Path getTargetDirectoryPath() {
        return targetDirectory.toPath();
    }

    /**
     * @return the outputDirectory
     */
    public Path getOutputDirectoryPath() {
        return outputDirectory.toPath();
    }

    /**
     * @return the applicationZipFile
     */
    public Path getApplicationZipFilePath() {
        return applicationZipFile.toPath();
    }

    public Path getApplicationJsonFilePath(Path outputDirPath) {
        return outputDirPath.resolve("application.json");
    }
}
