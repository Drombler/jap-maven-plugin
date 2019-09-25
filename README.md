# Java Application Package Maven Plugin

The JAP Maven Plugin creates a platform-independent application packaging format and meta data to be used by the 
[Drombler JStore](https://github.com/Drombler/drombler-jstore) components, using Maven tooling.

For more information see the [documentation](http://www.drombler.org/jap-maven-plugin/).

## Build the project from sources
```bash
mvn clean install
```
Please note that the develop branch (SNAPSHOT version) of the project might depend on SNAPSHOT versions of other projects.

If you don't want to build the dependent projects as well, please make sure to define a proxy in your [Maven Repository Manager](https://maven.apache.org/repository-management.html) to the following Maven Repository: https://oss.sonatype.org/content/repositories/snapshots/ and include it in your [single group](https://help.sonatype.com/repomanager3/formats/maven-repositories#MavenRepositories-ConfiguringApacheMaven).
