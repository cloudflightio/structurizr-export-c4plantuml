plugins {
    id("io.cloudflight.autoconfigure-gradle") version "0.4.0"
    `maven-publish`
    id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
    signing
}

description = "Extension of the Structurizr Export Library"
group = "io.cloudflight.structurizr"
version = "1.0.0"

autoConfigure {
    java {
        languageVersion.set(JavaLanguageVersion.of(8))
        vendorName.set("Cloudflight")
    }
}

repositories {
    mavenCentral()
}

dependencies {
    api(libs.structurizr.export)  {
        // TODO remove when https://github.com/structurizr/export/pull/9 is merged and released
        exclude(module = "structurizr-client")
    }
    // TODO remove when https://github.com/structurizr/export/pull/9 is merged and released
    api(libs.structurizr.core)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.assertj)
}

java {
    withJavadocJar()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            pom {
                name.set(project.name)
                description.set(project.description)
                url.set("https://github.com/cloudflightio/structurizr-export-c4plantuml")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                inceptionYear.set("2022")
                organization {
                    name.set("Cloudflight")
                    url.set("https://cloudflight.io")
                }
                developers {
                    developer {
                        id.set("klu2")
                        name.set("Klaus Lehner")
                        email.set("klaus.lehner@cloudflight.io")
                    }
                }
                scm {
                    connection.set("scm:ggit@github.com:cloudflightio/structurizr-export-c4plantuml.git")
                    developerConnection.set("scm:git@github.com:cloudflightio/structurizr-export-c4plantuml.git")
                    url.set("https://github.com/cloudflightio/structurizr-export-c4plantuml")
                }
            }
        }
    }
}

nexusPublishing {
    repositories {
        sonatype {  //only for users registered in Sonatype after 24 Feb 2021
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
            username.set(System.getenv("MAVEN_USERNAME"))
            password.set(System.getenv("MAVEN_PASSWORD"))
        }
    }
}

signing {
    setRequired {
        System.getenv("PGP_SECRET") != null
    }
    useInMemoryPgpKeys(System.getenv("PGP_SECRET"), System.getenv("PGP_PASSPHRASE"))
    sign(publishing.publications.getByName("maven"))
}
