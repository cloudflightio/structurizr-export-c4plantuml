# Structurizr Export for C4PlantUML

[![License](https://img.shields.io/badge/License-Apache_2.0-green.svg)](https://opensource.org/licenses/Apache-2.0)
[![Maven Central](https://img.shields.io/maven-central/v/io.cloudflight.structurizr/structurizr-export-c4plantuml.svg?label=Maven%20Central)](https://search.maven.org/artifact/io.cloudflight.structurizr/structurizr-export-c4plantuml)


This library is an extension / replacement for the [C4PlantUML](https://github.com/plantuml-stdlib/C4-PlantUML) part of [Structurizr Exports](https://github.com/structurizr/export).
It contains [pull requests of the original library that have not been merged yet](https://github.com/structurizr/export/pulls) but also
adds new functionality, specifically:

* Support for tags (this allows you to do custom styling of your diagrams with colors and line styles)
* Add [Element and Relationship properties](https://github.com/plantuml-stdlib/C4-PlantUML#element-and-relationship-properties)

## Usage

Add the library [io.cloudflight.structurizr:structurizr-export-c4plantuml](https://search.maven.org/artifact/io.cloudflight.structurizr/structurizr-export-c4plantuml) to your classpath first.

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

Diagram diagram = new ExtendedC4PlantUmlExporter().export(view);
````

and this will create the following diagram:

!(/fullExample.png)
