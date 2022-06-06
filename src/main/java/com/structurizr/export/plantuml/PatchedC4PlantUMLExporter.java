package com.structurizr.export.plantuml;

import com.structurizr.export.Diagram;
import com.structurizr.export.IndentingWriter;
import com.structurizr.model.*;
import com.structurizr.util.StringUtils;
import com.structurizr.view.*;

import java.util.Map;

import static java.lang.String.format;

/**
 * <p>This is a patched version of the {@link C4PlantUMLExporter} which is required to fix code which is private
 * or package-private. As soon as corresponding pull requests in the original repository are merged and released, this
 * class can be removed again.</p>
 *
 * <p>Do not extend from this class directly!</p>
 */
public class PatchedC4PlantUMLExporter extends AbstractPlantUMLExporter {

    /**
     * <p>Set this property to <code>true</code> by calling {@link Configuration#addProperty(String, String)} in your
     * {@link ViewSet} in order to have all {@link ModelItem#getProperties()} (for {@link Component}s and
     * {@link Relationship}s) being printed in the PlantUML diagrams.</p>
     *
     * <p>The default value is <code>false</code>.</p>
     *
     * <p>TODO remove as soon as <a href="https://github.com/structurizr/export/pull/12">https://github.com/structurizr/export/pull/12</a> is merged and released</p>
     *
     * @see ViewSet#getConfiguration()
     * @see Configuration#getProperties()
     */
    public static final String PLANTUML_ADD_PROPERTIES_PROPERTY = "plantuml.addProperties";

    private int groupId = 0;

    public PatchedC4PlantUMLExporter() {
    }

    @Override
    protected boolean isAnimationSupported(View view) {
        return !(view instanceof DynamicView);
    }

    @Override
    protected void writeHeader(View view, IndentingWriter writer) {
        super.writeHeader(view, writer);

        writer.writeLine("!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4.puml");
        writer.writeLine("!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4_Context.puml");

        if (view.getElements().stream().map(ElementView::getElement).anyMatch(e -> e instanceof Container || e instanceof ContainerInstance)) {
            writer.writeLine("!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4_Container.puml");
        }

        if (view.getElements().stream().map(ElementView::getElement).anyMatch(e -> e instanceof Component)) {
            writer.writeLine("!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4_Component.puml");
        }

        if (view instanceof DeploymentView) {
            writer.writeLine("!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4_Deployment.puml");
        }

        writeIncludes(view, writer);

        writer.writeLine();
    }

    @Override
    protected void writeFooter(View view, IndentingWriter writer) {
        if ("true".equalsIgnoreCase(view.getViewSet().getConfiguration().getProperties().getOrDefault(PLANTUML_LEGEND_PROPERTY, "true"))) {
            writer.writeLine();
            writer.writeLine("SHOW_LEGEND()");
        }

        super.writeFooter(view, writer);
    }

    @Override
    protected void startEnterpriseBoundary(View view, String enterpriseName, IndentingWriter writer) {
        writer.writeLine(String.format("Enterprise_Boundary(enterprise, \"%s\") {", enterpriseName));
        writer.indent();
    }

    @Override
    protected void endEnterpriseBoundary(View view, IndentingWriter writer) {
        writer.outdent();
        writer.writeLine("}");
        writer.writeLine();
    }

    @Override
    protected void startGroupBoundary(View view, String group, IndentingWriter writer) {
        writer.writeLine(String.format("Boundary(group_%s, \"%s\") {", groupId++, group));
        writer.indent();
    }

    @Override
    protected void endGroupBoundary(View view, IndentingWriter writer) {
        writer.outdent();
        writer.writeLine("}");
        writer.writeLine();
    }

    @Override
    protected void startSoftwareSystemBoundary(View view, SoftwareSystem softwareSystem, IndentingWriter writer) {
        writer.writeLine(String.format("System_Boundary(\"%s_boundary\", \"%s\") {", idOf(softwareSystem), softwareSystem.getName()));
        writer.indent();
    }

    @Override
    protected void endSoftwareSystemBoundary(View view, IndentingWriter writer) {
        writer.outdent();
        writer.writeLine("}");
        writer.writeLine();
    }

    @Override
    protected void startContainerBoundary(View view, Container container, IndentingWriter writer) {
        writer.writeLine(String.format("Container_Boundary(\"%s_boundary\", \"%s\") {", idOf(container), container.getName()));
        writer.indent();
    }

    @Override
    protected void endContainerBoundary(View view, IndentingWriter writer) {
        writer.outdent();
        writer.writeLine("}");
        writer.writeLine();
    }

    @Override
    protected void startDeploymentNodeBoundary(DeploymentView view, DeploymentNode deploymentNode, IndentingWriter writer) {
        String url = deploymentNode.getUrl();
        if (!StringUtils.isNullOrEmpty(url)) {
            url = "[[" + url + "]]";
        } else {
            url = "";
        }

        if (StringUtils.isNullOrEmpty(deploymentNode.getTechnology())) {
            writer.writeLine(
                    format("Deployment_Node(%s, \"%s\", $tags=\"%s\")%s {",
                            idOf(deploymentNode),
                            deploymentNode.getName() + (deploymentNode.getInstances() > 1 ? " (x" + deploymentNode.getInstances() + ")" : ""),
                            tagsOf(deploymentNode),
                            url
                    )
            );
        } else {
            writer.writeLine(
                    format("Deployment_Node(%s, \"%s\", \"%s\", $tags=\"%s\")%s {",
                            idOf(deploymentNode),
                            deploymentNode.getName() + (deploymentNode.getInstances() > 1 ? " (x" + deploymentNode.getInstances() + ")" : ""),
                            deploymentNode.getTechnology(),
                            tagsOf(deploymentNode),
                            url
                    )
            );
        }
        writer.indent();

        if (!isVisible(view, deploymentNode)) {
            writer.writeLine("hide " + idOf(deploymentNode));
        }
    }

    @Override
    protected void endDeploymentNodeBoundary(View view, IndentingWriter writer) {
        writer.outdent();
        writer.writeLine("}");
        writer.writeLine();
    }

    @Override
    public Diagram export(CustomView view) {
        return null;
    }

    @Override
    public Diagram export(DynamicView view) {
        if (useSequenceDiagrams(view)) {
            throw new UnsupportedOperationException("Sequence diagrams are not supported by C4-PlantUML");
        } else {
            return super.export(view);
        }
    }

    @Override
    protected void writeElement(View view, Element element, IndentingWriter writer) {
        if (element instanceof CustomElement) {
            return;
        }

        Element elementToWrite = element;
        String id = idOf(element);

        String url = element.getUrl();
        if (!StringUtils.isNullOrEmpty(url)) {
            url = "[[" + url + "]]";
        } else {
            url = "";
        }

        addProperties(view, writer, element);

        if (element instanceof StaticStructureElementInstance) {
            StaticStructureElementInstance elementInstance = (StaticStructureElementInstance) element;
            element = elementInstance.getElement();

            if (StringUtils.isNullOrEmpty(url)) {
                url = element.getUrl();
                if (!StringUtils.isNullOrEmpty(url)) {
                    url = "[[" + url + "]]";
                } else {
                    url = "";
                }
            }
        }

        String name = element.getName();
        String description = element.getDescription();

        if (StringUtils.isNullOrEmpty(description)) {
            description = "";
        }

        if (element instanceof Person) {
            Person person = (Person) element;
            if (person.getLocation() == Location.External) {
                writer.writeLine(String.format("Person_Ext(%s, \"%s\", \"%s\", $tags=\"%s\")%s", id, name, description, tagsOf(elementToWrite), url));
            } else {
                writer.writeLine(String.format("Person(%s, \"%s\", \"%s\", $tags=\"%s\")%s", id, name, description, tagsOf(elementToWrite), url));
            }
        } else if (element instanceof SoftwareSystem) {
            SoftwareSystem softwareSystem = (SoftwareSystem) element;
            if (softwareSystem.getLocation() == Location.External) {
                writer.writeLine(String.format("System_Ext(%s, \"%s\", \"%s\", $tags=\"%s\")%s", id, name, description, tagsOf(elementToWrite), url));
            } else {
                writer.writeLine(String.format("System(%s, \"%s\", \"%s\", $tags=\"%s\")%s", id, name, description, tagsOf(elementToWrite), url));
            }
        } else if (element instanceof Container) {
            Container container = (Container) element;
            ElementStyle elementStyle = view.getViewSet().getConfiguration().getStyles().findElementStyle(element);
            String shape = "";
            if (elementStyle.getShape() == Shape.Cylinder) {
                shape = "Db";
            } else if (elementStyle.getShape() == Shape.Pipe) {
                shape = "Queue";
            }

            if (StringUtils.isNullOrEmpty(container.getTechnology())) {
                writer.writeLine(String.format("Container%s(%s, \"%s\", \"%s\", $tags=\"%s\")%s", shape, id, name, description, tagsOf(elementToWrite), url));
            } else {
                writer.writeLine(String.format("Container%s(%s, \"%s\", \"%s\", \"%s\", $tags=\"%s\")%s", shape, id, name, container.getTechnology(), description, tagsOf(elementToWrite), url));
            }
        } else if (element instanceof Component) {
            Component component = (Component) element;
            if (StringUtils.isNullOrEmpty(component.getTechnology())) {
                writer.writeLine(String.format("Component(%s, \"%s\", \"%s\", $tags=\"%s\")%s", id, name, description, tagsOf(elementToWrite), url));
            } else {
                writer.writeLine(String.format("Component(%s, \"%s\", \"%s\", \"%s\", $tags=\"%s\")%s", id, name, component.getTechnology(), description, tagsOf(elementToWrite), url));
            }
        } else if (element instanceof InfrastructureNode) {
            InfrastructureNode infrastructureNode = (InfrastructureNode) element;
            if (StringUtils.isNullOrEmpty(infrastructureNode.getTechnology())) {
                writer.writeLine(format("Deployment_Node(%s, \"%s\", $tags=\"%s\")%s", idOf(infrastructureNode), name, tagsOf(elementToWrite), url));
            } else {
                if (StringUtils.isNullOrEmpty(infrastructureNode.getTechnology())) {
                    writer.writeLine(format("Deployment_Node(%s, \"%s\", \"%s\", $tags=\"%s\")%s", idOf(infrastructureNode), name, infrastructureNode.getTechnology(), tagsOf(elementToWrite), url));
                }
            }
        }

        if (!isVisible(view, elementToWrite)) {
            writer.writeLine("hide " + id);
        }
    }

    private String tagsOf(Element element) {
        String tags;
        if (element instanceof StaticStructureElementInstance) {
            tags = ((StaticStructureElementInstance) element).getElement().getTags() + "," + element.getTags();
        } else {
            tags = element.getTags();
        }

        return prepareTagsForC4PlantUml(tags);
    }

    private String tagsOf(Relationship relationship) {
        String tags;

        if (!StringUtils.isNullOrEmpty(relationship.getLinkedRelationshipId())) {
            tags = relationship.getModel().getRelationship(relationship.getLinkedRelationshipId()).getTags();

            if (!StringUtils.isNullOrEmpty(relationship.getTags())) {
                tags = tags + "," + relationship.getTags();
            }
        } else {
            tags = relationship.getTags();
        }

        return prepareTagsForC4PlantUml(tags);
    }

    /**
     * C4PlantUML docu says: "If 2 tags define the same skinparam, the first definition is used.". As {@link Element#getTags()}
     * puts the default tags ("Container", "RelationShip") to the first position, this would mean you can never override any tags.
     * We therefore reverse the list of tags here and separate them with `+` instead of `,` characters.
     *
     * @param tags the original tags list from Structurizr
     * @return the tag string is as required by C4PlantUML
     */
    private String prepareTagsForC4PlantUml(String tags) {
        final StringBuilder result = new StringBuilder();
        final String[] split = tags.split(",");
        for (int i = split.length - 1; i >= 0; i--) {
            result.append(split[i]);
            if (i != 0) {
                result.append("+");
            }
        }
        return result.toString();
    }

    @Override
    protected void writeRelationship(View view, RelationshipView relationshipView, IndentingWriter writer) {
        Relationship relationship = relationshipView.getRelationship();
        Element source = relationship.getSource();
        Element destination = relationship.getDestination();

        if (source instanceof CustomElement || destination instanceof CustomElement) {
            return;
        }

        addProperties(view, writer, relationship);

        if (relationshipView.isResponse() != null && relationshipView.isResponse()) {
            source = relationship.getDestination();
            destination = relationship.getSource();
        }

        String description = "";

        if (!StringUtils.isNullOrEmpty(relationshipView.getOrder())) {
            description = relationshipView.getOrder() + ". ";
        }

        description += (hasValue(relationshipView.getDescription()) ? relationshipView.getDescription() : hasValue(relationshipView.getRelationship().getDescription()) ? relationshipView.getRelationship().getDescription() : "");

        if (StringUtils.isNullOrEmpty(relationship.getTechnology())) {
            writer.writeLine(format("Rel_D(%s, %s, \"%s\", $tags=\"%s\")", idOf(source), idOf(destination), description, tagsOf(relationship)));
        } else {
            writer.writeLine(format("Rel_D(%s, %s, \"%s\", \"%s\", $tags=\"%s\")", idOf(source), idOf(destination), description, relationship.getTechnology(), tagsOf(relationship)));
        }
    }

    /**
     * TODO remove as soon as <a href="https://github.com/structurizr/export/pull/12">https://github.com/structurizr/export/pull/12</a> is merged and released
     */
    private void addProperties(View view, IndentingWriter writer, ModelItem element) {
        if ("true".equalsIgnoreCase(view.getViewSet().getConfiguration().getProperties().getOrDefault(PLANTUML_ADD_PROPERTIES_PROPERTY, "false"))) {
            Map<String, String> properties = element.getProperties();
            if (!properties.isEmpty()) {
                writer.writeLine("WithoutPropertyHeader()");
                properties.keySet().stream().sorted().forEach(key ->
                        writer.writeLine(String.format("AddProperty(\"%s\",\"%s\")", key, properties.get(key)))
                );
            }
        }
    }


}
