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
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.drombler.jstore.jap.maven.plugin.util.MavenAssemblyPluginUtils;

import java.util.Optional;


/**
 * Sets default values for some properties of other Maven Plugin (currently only the Maven Assembly Plugin), if they aren't set yet.
 */
@Mojo(name = "set-maven-properties", defaultPhase = LifecyclePhase.INITIALIZE)
public class SetMavenProperties extends AbstractJapMojo {


    /**
     * {@inheritDoc }
     */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        Optional<Plugin> mavenAssemblyPlugin = findPlugin(MAVEN_ASSEMBLY_PLUGIN_COORDINATES);

        ensureMavenProperty(MAVEN_ASSEMBLY_PLUGIN_COORDINATES, MavenAssemblyPluginUtils.ATTACH_PROPERTY, mavenAssemblyPlugin, Boolean.FALSE.toString());
    }

}
