package io.cloudflight.structurizr.plantuml;

import com.structurizr.export.IndentingWriter;
import com.structurizr.export.plantuml.PatchedC4PlantUMLExporter;
import com.structurizr.model.Tags;
import com.structurizr.view.*;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.structurizr.util.StringUtils.isNullOrEmpty;
import static io.cloudflight.structurizr.plantuml.MapUtils.mapToString;
import static io.cloudflight.structurizr.plantuml.StringUtils.quote;
import static java.lang.String.format;

public class ExtendedC4PlantUmlExporter extends PatchedC4PlantUMLExporter {
    /**
     * <p>Set this property to <code>true</code> by calling {@link Configuration#addProperty(String, String)} in your
     * {@link ViewSet} in order to have all tags that are used in your view being added in the PlantUML diagrams
     * in order to add styling for your elements (colors, background, line styling).</p>
     *
     * <p>The default value is <code>false</code>.</p>
     *
     * @see ViewSet#getConfiguration()
     * @see Configuration#getProperties()
     */
    public static final String PLANTUML_ADD_TAGS_PROPERTY = "plantuml.addTags";

    @Override
    protected void writeHeader(View view, IndentingWriter writer) {
        super.writeHeader(view, writer);
        if ("true".equalsIgnoreCase(view.getViewSet().getConfiguration().getProperties().getOrDefault(PLANTUML_ADD_TAGS_PROPERTY, "false"))) {
            writeTags(view, writer);
        }
    }

    private void writeTags(View view, IndentingWriter writer) {
        // https://github.com/plantuml-stdlib/C4-PlantUML#custom-tagsstereotypes-support-and-skinparam-updates
        writer.writeLine();
        view.getElements().stream()
                .flatMap(elementView -> elementView.getElement().getTagsAsSet().stream())
                .distinct().forEach(tag -> {
                            ElementStyle style = view.getViewSet().getConfiguration().getStyles().findElementStyle(tag);
                            Map<String, String> attributes = new LinkedHashMap<>();
                            if (!isNullOrEmpty(style.getBackground())) {
                                attributes.put("$bgColor", quote(style.getBackground()));
                            }
                            if (!isNullOrEmpty(style.getColor())) {
                                attributes.put("$fontColor", quote(style.getColor()));
                            }
                            if (!isNullOrEmpty(style.getStroke())) {
                                attributes.put("$borderColor", quote(style.getStroke()));
                            }
                            if (style.getShape() == Shape.RoundedBox) {
                                attributes.put("$shape", "RoundedBoxShape()");
                            }
                            if (!isNullOrEmpty(style.getIcon())) {
                                attributes.put("$sprite", quote("img:" + style.getIcon()));
                            }
                            if (!attributes.isEmpty()) {
                                writer.writeLine(format("AddElementTag(\"%s\", %s)", tag, mapToString(attributes)));
                            }
                            if (!isNullOrEmpty(style.getBackground()) && !isNullOrEmpty(style.getColor())) {
                                if (Tags.PERSON.equals(tag) || Tags.COMPONENT.equals(tag) || Tags.CONTAINER.equals(tag)) {
                                    // if background and color is set, and this is the element style of one of the default tags
                                    // when we can call UpdateElementStyle with the lowercase (i.e. UpdateElementStyle("person")) in
                                    // order to remove this line from the legend
                                    writer.writeLine(format("UpdateElementStyle(\"%s\")", tag.toLowerCase()));
                                }
                            }
                        }
                );
        view.getRelationships().stream()
                .flatMap(relationshipView -> relationshipView.getRelationship().getTagsAsSet().stream())
                .distinct().forEach(tag -> {
                            RelationshipStyle style = view.getViewSet().getConfiguration().getStyles().findRelationshipStyle(tag);
                            Map<String, String> attributes = new LinkedHashMap<>();
                            if (!isNullOrEmpty(style.getColor())) {
                                attributes.put("$lineColor", quote(style.getColor()));
                                attributes.put("$textColor", quote(style.getColor()));
                            }
                            if (style.getStyle() == LineStyle.Dashed) {
                                attributes.put("$lineStyle", "DashedLine()");
                            } else if (style.getStyle() == LineStyle.Dotted) {
                                attributes.put("$lineStyle", "DottedLine()");
                            }
                            if (!attributes.isEmpty()) {
                                writer.writeLine(format("AddRelTag(\"%s\", %s)", tag, mapToString(attributes)));
                            }
                        }
                );
    }
}
