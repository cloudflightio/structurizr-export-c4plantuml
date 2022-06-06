package io.cloudflight.structurizr.plantuml;

import com.structurizr.Workspace;
import com.structurizr.export.AbstractDiagramExporter;
import com.structurizr.export.Diagram;
import com.structurizr.export.plantuml.C4PlantUMLExporter;
import com.structurizr.model.Container;
import com.structurizr.model.Relationship;
import com.structurizr.model.SoftwareSystem;
import com.structurizr.model.Tags;
import com.structurizr.view.*;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ExtendedC4PlantUmlExporterTest {

    private final AbstractDiagramExporter exporter = new ExtendedC4PlantUmlExporter();

    @Test
    void printTags() {
        final String SERVER = "server";
        final String DB = "db";
        final String CRITICAL = "critical";
        Workspace workspace = new Workspace("Name", "Description");
        Styles styles = workspace.getViews().getConfiguration().getStyles();
        styles.addElementStyle(SERVER).background("#00ff00").color("#ff00ff");
        styles.addElementStyle(DB).background("#ff00ff").color("#00ff00").shape(Shape.Cylinder);
        styles.addElementStyle(Tags.CONTAINER).background("#eFeFeF");
        styles.addRelationshipStyle(CRITICAL).color("#ff0000").style(LineStyle.Dotted);

        SoftwareSystem softwareSystem = workspace.getModel().addSoftwareSystem("SoftwareSystem");

        Container webServer = softwareSystem.addContainer("WebServer", "my web server", "Spring Boot");
        webServer.addTags(SERVER);

        Container database = softwareSystem.addContainer("Database", "my database", "MariaDB");
        database.addTags(DB);

        Relationship relationship = webServer.uses(database, "fetches data", "JDBC");
        relationship.addTags(CRITICAL);

        workspace.getViews().getConfiguration().addProperty(ExtendedC4PlantUmlExporter.PLANTUML_ADD_TAGS_PROPERTY, Boolean.TRUE.toString());
        ContainerView view = workspace.getViews().createContainerView(softwareSystem, "containerView", "");
        view.addDefaultElements();

        Diagram diagram = exporter.export(view);

        assertThat(new File("./src/test/resources/tags/printTags.puml")).hasContent(diagram.getDefinition());
    }

    /**
     * TODO remove as soon as <a href="https://github.com/structurizr/export/pull/12">https://github.com/structurizr/export/pull/12</a> is merged and released
     */
    @Test
    void test_printProperties() {
        Workspace workspace = new Workspace("Name", "Description");
        SoftwareSystem softwareSystem = workspace.getModel().addSoftwareSystem("SoftwareSystem");
        Container container1 = softwareSystem.addContainer("Container 1");
        container1.addProperty("IP", "127.0.0.1");
        container1.addProperty("Region", "East");
        Container container2 = softwareSystem.addContainer("Container 2");
        container2.addProperty("Region", "West");
        container2.addProperty("IP", "127.0.0.2");
        Relationship relationship = container1.uses(container2, "");
        relationship.addProperty("Prop1", "Value1");
        relationship.addProperty("Prop2", "Value2");

        workspace.getViews().getConfiguration().addProperty(ExtendedC4PlantUmlExporter.PLANTUML_ADD_PROPERTIES_PROPERTY, "true");
        ContainerView view = workspace.getViews().createContainerView(softwareSystem, "containerView", "");
        view.addDefaultElements();

        Diagram diagram = new ExtendedC4PlantUmlExporter().export(view);

        assertThat(new File("./src/test/resources/tags/printProperties-containerView.puml")).hasContent(diagram.getDefinition());
    }

    @Test
    void fullExample() {
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

        assertThat(new File("./src/test/resources/tags/fullExample.puml")).hasContent(diagram.getDefinition());
    }
}
