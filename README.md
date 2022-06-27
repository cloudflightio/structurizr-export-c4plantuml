# Structurizr Export for C4PlantUML

[![License](https://img.shields.io/badge/License-Apache_2.0-green.svg)](https://opensource.org/licenses/Apache-2.0)
[![Maven Central](https://img.shields.io/maven-central/v/io.cloudflight.structurizr/structurizr-export-c4plantuml.svg?label=Maven%20Central)](https://search.maven.org/artifact/io.cloudflight.structurizr/structurizr-export-c4plantuml)


This library is an extension / replacement for the [C4PlantUML](https://github.com/plantuml-stdlib/C4-PlantUML) part of [Structurizr Exports](https://github.com/structurizr/export).
It contains [pull requests of the original library that have not been merged yet](https://github.com/structurizr/export/pulls) but also
adds new functionality, specifically:

* Support for tags (this allows you to do custom styling of your diagrams with colors and line styles)
* Add [Element and Relationship properties](https://github.com/plantuml-stdlib/C4-PlantUML#element-and-relationship-properties)

## Usage

Add the library [io.cloudflight.structurizr:structurizr-export-c4plantuml](https://search.maven.org/artifact/io.cloudflight.structurizr/structurizr-export-c4plantuml) to your classpath first, 
i.e. with Gradle:

````groovy
repositories {
    mavenCentral()
}

dependencies {
    implementation("io.cloudflight.structurizr:structurizr-export-c4plantuml:1.0.0")
}
````

The following code makes use of tags and properties:

````java
final String SERVER = "server";
final String DB = "db";
final String CRITICAL = "critical";
Workspace workspace = new Workspace("Name", "Description");

// activate property and tag printing
Configuration configuration = workspace.getViews().getConfiguration();
configuration.addProperty(ExtendedC4PlantUmlExporter.PLANTUML_ADD_PROPERTIES_PROPERTY, Boolean.TRUE.toString());
configuration.addProperty(ExtendedC4PlantUmlExporter.PLANTUML_ADD_TAGS_PROPERTY, Boolean.TRUE.toString());

Styles styles = configuration.getStyles();
styles.addElementStyle(SERVER).background("#00ff00").color("#ff00ff");
styles.addElementStyle(DB).background("#ff00ff").color("#00ff00").shape(Shape.Cylinder);
styles.addElementStyle(Tags.CONTAINER).background("#eFeFeF");
styles.addRelationshipStyle(CRITICAL).color("#ff0000").style(LineStyle.Dotted);

SoftwareSystem softwareSystem = workspace.getModel().addSoftwareSystem("SoftwareSystem");

Container container1 = softwareSystem.addContainer("Container 1");
container1.addProperty("IP", "127.0.0.1");
container1.addProperty("Region", "East");
container1.addTags(SERVER);

Container container2 = softwareSystem.addContainer("Container 2");
container2.addProperty("Region", "West");
container2.addProperty("IP", "127.0.0.2");
container2.addTags(DB);

Relationship relationship = container1.uses(container2, "fetches data");
relationship.addProperty("Prop1", "Value1");
relationship.addProperty("Prop2", "Value2");
relationship.addTags(CRITICAL);

ContainerView view = workspace.getViews().createContainerView(softwareSystem, "containerView", "");
view.addDefaultElements();

// use the ExtendedC4PlantUmlExporter instead of the default C4PlantUmlExporter
Diagram diagram = new ExtendedC4PlantUmlExporter().export(view);
````

and this will create the following PlantUML diagram:

````puml
@startuml
title SoftwareSystem - Containers

top to bottom direction

!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4.puml
!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4_Context.puml
!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4_Container.puml


AddElementTag("Container", $bgColor="#efefef")
AddElementTag("server", $bgColor="#00ff00", $fontColor="#ff00ff")
AddElementTag("db", $bgColor="#ff00ff", $fontColor="#00ff00")
AddRelTag("critical", $lineColor="#ff0000", $textColor="#ff0000", $lineStyle=DottedLine())
System_Boundary("SoftwareSystem_boundary", "SoftwareSystem") {
  WithoutPropertyHeader()
  AddProperty("IP","127.0.0.1")
  AddProperty("Region","East")
  Container(SoftwareSystem.Container1, "Container 1", "", $tags="server+Container+Element")
  WithoutPropertyHeader()
  AddProperty("IP","127.0.0.2")
  AddProperty("Region","West")
  ContainerDb(SoftwareSystem.Container2, "Container 2", "", $tags="db+Container+Element")
}

WithoutPropertyHeader()
AddProperty("Prop1","Value1")
AddProperty("Prop2","Value2")
Rel_D(SoftwareSystem.Container1, SoftwareSystem.Container2, "fetches data", $tags="critical+Relationship")

SHOW_LEGEND()
@enduml
````

which can be rendered to this image using PlantUML:

![Full Example](/fullExample.png)

## Add Icons

In the combination with the [Architecture Icons library](https://github.com/cloudflightio/architecture-icons) you can also easily
add icons to your files:

````java
 Workspace workspace = new Workspace("Name", "Description");

// activate property and tag printing
Configuration configuration = workspace.getViews().getConfiguration();
configuration.addProperty(ExtendedC4PlantUmlExporter.PLANTUML_ADD_PROPERTIES_PROPERTY, Boolean.TRUE.toString());
configuration.addProperty(ExtendedC4PlantUmlExporter.PLANTUML_ADD_TAGS_PROPERTY, Boolean.TRUE.toString());

configuration.addTheme(DevIcons2.STRUCTURIZR_THEME_URL);

SoftwareSystem softwareSystem = workspace.getModel().addSoftwareSystem("My SoftwareSystem");
softwareSystem.addTags(DevIcons2.ANGULARJS.getName());

ThemeUtils.loadThemes(workspace);

SystemLandscapeView landscape = workspace.getViews().createSystemLandscapeView("landscape", "");
landscape.addAllSoftwareSystems();

Diagram diagram = new ExtendedC4PlantUmlExporter().export(landscape);
System.out.println(diagram.getDefinition());
````

This will render the following file:

````puml
@startuml
title System Landscape

top to bottom direction

!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4.puml
!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4_Context.puml


AddElementTag("angularjs", $sprite="img:https://raw.githubusercontent.com/tupadr3/plantuml-icon-font-sprites/master/devicons2/angularjs.png")
System(MySoftwareSystem, "My SoftwareSystem", "", $tags="angularjs+Software System+Element")


SHOW_LEGEND()
@enduml
````

which renders to the following image:

![Icon Example](/iconExample.png)