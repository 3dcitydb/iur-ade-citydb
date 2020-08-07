package org.citydb.ade.iur.exporter.urg;

import org.citydb.ade.exporter.ADEExporter;
import org.citydb.ade.exporter.CityGMLExportHelper;
import org.citydb.ade.iur.exporter.ExportManager;
import org.citydb.ade.iur.schema.ADETable;
import org.citydb.citygml.exporter.CityGMLExportException;
import org.citydb.citygml.exporter.database.content.SurfaceGeometry;
import org.citydb.citygml.exporter.util.AttributeValueSplitter;
import org.citydb.database.schema.mapping.AbstractType;
import org.citydb.query.filter.projection.ProjectionFilter;
import org.citydb.sqlbuilder.expression.PlaceHolder;
import org.citydb.sqlbuilder.schema.Table;
import org.citydb.sqlbuilder.select.Select;
import org.citydb.sqlbuilder.select.operator.comparison.ComparisonFactory;
import org.citygml4j.model.gml.GMLClass;
import org.citygml4j.model.gml.basicTypes.Code;
import org.citygml4j.model.gml.geometry.aggregates.MultiSurface;
import org.citygml4j.model.gml.geometry.aggregates.MultiSurfaceProperty;
import org.xml.sax.SAXException;
import org.citygml4j.ade.iur.model.module.StatisticalGridModule;
import org.citygml4j.ade.iur.model.module.UrbanFunctionModule;
import org.citygml4j.ade.iur.model.urg.PublicTransportationAccessibility;
import org.citygml4j.ade.iur.model.urg.StatisticalGrid;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Year;

public class StatisticalGridExporter implements ADEExporter {
    private final CityGMLExportHelper helper;
    private final ExportManager manager;
    private final PreparedStatement ps;
    private final String module;

    private final AttributeValueSplitter valueSplitter;

    public StatisticalGridExporter(Connection connection, CityGMLExportHelper helper, ExportManager manager) throws CityGMLExportException, SQLException {
        this.helper = helper;
        this.manager = manager;

        String tableName = manager.getSchemaMapper().getTableName(ADETable.STATISTICALGRID);
        module = UrbanFunctionModule.v1_3.getNamespaceURI();

        Table table = new Table(helper.getTableNameWithSchema(tableName));
        Select select = new Select().addProjection(table.getColumns("areaclassificationtype", "areaclassification_codespace",
                "city", "city_codespace", "class", "class_codespace", "lod_1multisurfacegeometry_id",
                "lod_2multisurfacegeometry_id", "prefecture", "prefecture_codespace", "surveyyear", "urbanplantype",
                "urbanplantype_codespace", "value", "availability"))
                .addSelection(ComparisonFactory.equalTo(table.getColumn("id"), new PlaceHolder<>()));
        ps = connection.prepareStatement(select.toString());

        valueSplitter = helper.getAttributeValueSplitter();
    }

    public void doExport(StatisticalGrid statisticalGrid, long objectId, AbstractType<?> objectType, ProjectionFilter projectionFilter) throws CityGMLExportException, SQLException {
        ps.setLong(1, objectId);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                if (projectionFilter.containsProperty("class", module)) {
                    String classifier = rs.getString("class");
                    if (!rs.wasNull()) {
                        Code code = new Code(classifier);
                        code.setCodeSpace(rs.getString("class_codespace"));
                        statisticalGrid.setClassifier(code);
                    }
                }

                if (projectionFilter.containsProperty("urbanPlanType", module)) {
                    String urbanPlanType = rs.getString("urbanplantype");
                    if (!rs.wasNull()) {
                        Code code = new Code(urbanPlanType);
                        code.setCodeSpace(rs.getString("urbanplantype_codespace"));
                        statisticalGrid.setUrbanPlanType(code);
                    }
                }

                if (projectionFilter.containsProperty("areaClassificationType", module)) {
                    String areaClassificationType = rs.getString("areaclassificationtype");
                    if (!rs.wasNull()) {
                        Code code = new Code(areaClassificationType);
                        code.setCodeSpace(rs.getString("areaclassification_codespace"));
                        statisticalGrid.setAreaClassificationType(code);
                    }
                }

                if (projectionFilter.containsProperty("prefecture", module)) {
                    String prefecture = rs.getString("prefecture");
                    if (!rs.wasNull()) {
                        Code code = new Code(prefecture);
                        code.setCodeSpace(rs.getString("prefecture_codespace"));
                        statisticalGrid.setPrefecture(code);
                    }
                }

                if (projectionFilter.containsProperty("city", module)) {
                    String city = rs.getString("city");
                    if (!rs.wasNull()) {
                        Code code = new Code(city);
                        code.setCodeSpace(rs.getString("city_codespace"));
                        statisticalGrid.setCity(code);
                    }
                }

                if (projectionFilter.containsProperty("surveyYear", module)) {
                    Date surveyYear = rs.getDate("surveyyear");
                    if (!rs.wasNull())
                        statisticalGrid.setSurveyYear(Year.of(surveyYear.toLocalDate().getYear()));
                }

                if (projectionFilter.containsProperty("lod-1MultiSurfaceGeometry", module)) {
                    long lod1MultiSurfaceId = rs.getLong("lod_1multisurfacegeometry_id");
                    if (!rs.wasNull()) {
                        SurfaceGeometry geometry = helper.exportSurfaceGeometry(lod1MultiSurfaceId);
                        if (geometry != null && geometry.getType() == GMLClass.MULTI_SURFACE) {
                            MultiSurfaceProperty multiSurfaceProperty = new MultiSurfaceProperty();
                            if (geometry.isSetGeometry())
                                multiSurfaceProperty.setMultiSurface((MultiSurface) geometry.getGeometry());
                            else
                                multiSurfaceProperty.setHref(geometry.getReference());

                            statisticalGrid.setLod1MultiSurfaceGeometry(multiSurfaceProperty);
                        }
                    }
                }

                if (projectionFilter.containsProperty("lod-2MultiSurfaceGeometry", module)) {
                    long lod2MultiSurfaceId = rs.getLong("lod_2multisurfacegeometry_id");
                    if (!rs.wasNull()) {
                        SurfaceGeometry geometry = helper.exportSurfaceGeometry(lod2MultiSurfaceId);
                        if (geometry != null && geometry.getType() == GMLClass.MULTI_SURFACE) {
                            MultiSurfaceProperty multiSurfaceProperty = new MultiSurfaceProperty();
                            if (geometry.isSetGeometry())
                                multiSurfaceProperty.setMultiSurface((MultiSurface) geometry.getGeometry());
                            else
                                multiSurfaceProperty.setHref(geometry.getReference());

                            statisticalGrid.setLod2MultiSurfaceGeometry(multiSurfaceProperty);
                        }
                    }
                }

                if (projectionFilter.containsProperty("value", module)) {
                    try {
                        for (AttributeValueSplitter.SplitValue splitValue : valueSplitter.split(rs.getString("value"))) {
                            String value = "<urg:value xmlns:urg=\"" + StatisticalGridModule.v1_3.getNamespaceURI() + "\">" +
                                    splitValue.result(0) + "</urg:value>";
                            statisticalGrid.getValues().add(manager.unmarshal(value));
                        }
                    } catch (ParserConfigurationException | IOException | SAXException e) {
                        throw new CityGMLExportException("Failed to create <urg:value> element.", e);
                    }
                }

                if (statisticalGrid instanceof PublicTransportationAccessibility && projectionFilter.containsProperty("availability", module)) {
                    boolean availability = rs.getBoolean("availability");
                    if (!rs.wasNull())
                        ((PublicTransportationAccessibility) statisticalGrid).setAvailability(availability);
                }
            }
        }
    }

    @Override
    public void close() throws CityGMLExportException, SQLException {
        ps.close();
    }
}
