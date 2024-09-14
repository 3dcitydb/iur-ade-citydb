/*
 * 3D City Database - The Open Source CityGML Database
 * https://www.3dcitydb.org/
 *
 * Copyright 2013 - 2024
 * Chair of Geoinformatics
 * Technical University of Munich, Germany
 * https://www.lrg.tum.de/gis/
 *
 * The 3D City Database is jointly developed with the following
 * cooperation partners:
 *
 * Virtual City Systems, Berlin <https://vc.systems/>
 * M.O.S.S. Computer Grafik Systeme GmbH, Taufkirchen <http://www.moss.de/>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.citydb.ade.iur.exporter.urg;

import org.citydb.ade.iur.exporter.ExportManager;
import org.citydb.ade.iur.schema.ADETable;
import org.citydb.core.ade.exporter.CityGMLExportHelper;
import org.citydb.core.operation.exporter.CityGMLExportException;
import org.citydb.core.operation.exporter.database.content.SurfaceGeometryExporter;
import org.citydb.core.operation.exporter.util.AttributeValueSplitter;
import org.citydb.core.operation.exporter.util.SplitValue;
import org.citydb.core.query.filter.projection.CombinedProjectionFilter;
import org.citydb.core.query.filter.projection.ProjectionFilter;
import org.citydb.sqlbuilder.expression.PlaceHolder;
import org.citydb.sqlbuilder.schema.Table;
import org.citydb.sqlbuilder.select.Select;
import org.citydb.sqlbuilder.select.operator.comparison.ComparisonFactory;
import org.citygml4j.ade.iur.model.module.StatisticalGridModule;
import org.citygml4j.ade.iur.model.module.UrbanFunctionModule;
import org.citygml4j.ade.iur.model.urg.StatisticalGrid;
import org.citygml4j.model.gml.basicTypes.Code;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.sql.*;
import java.time.Year;

public class StatisticalGridExporter implements StatisticalGridModuleExporter {
    private final SurfaceGeometryExporter surfaceGeometryExporter;
    private final ExportManager manager;
    private final PreparedStatement ps;
    private final String module;

    private final AttributeValueSplitter valueSplitter;

    public StatisticalGridExporter(Connection connection, CityGMLExportHelper helper, ExportManager manager) throws CityGMLExportException, SQLException {
        this.manager = manager;

        String tableName = manager.getSchemaMapper().getTableName(ADETable.STATISTICALGRID);
        CombinedProjectionFilter projectionFilter = helper.getCombinedProjectionFilter(tableName);
        module = UrbanFunctionModule.v1_4.getNamespaceURI();

        Table table = new Table(helper.getTableNameWithSchema(tableName));
        Select select = addProjection(new Select(), table, projectionFilter, "")
                .addSelection(ComparisonFactory.equalTo(table.getColumn("id"), new PlaceHolder<>()));
        ps = connection.prepareStatement(select.toString());

        surfaceGeometryExporter = helper.getSurfaceGeometryExporter();
        valueSplitter = helper.getAttributeValueSplitter();
    }

    public Select addProjection(Select select, Table table, CombinedProjectionFilter projectionFilter, String prefix) {
        select.addProjection(table.getColumn("id", prefix + "id"));
        if (projectionFilter.containsProperty("class", module))
            select.addProjection(table.getColumn("class", prefix + "class"), table.getColumn("class_codespace", prefix + "class_codespace"));
        if (projectionFilter.containsProperty("urbanPlanType", module))
            select.addProjection(table.getColumn("urbanplantype", prefix + "urbanplantype"), table.getColumn("urbanplantype_codespace", prefix + "urbanplantype_codespace"));
        if (projectionFilter.containsProperty("areaClassificationType", module))
            select.addProjection(table.getColumn("areaclassificationtype", prefix + "areaclassificationtype"), table.getColumn("areaclassification_codespace", prefix + "areaclassification_codespace"));
        if (projectionFilter.containsProperty("prefecture", module))
            select.addProjection(table.getColumn("prefecture", prefix + "prefecture"), table.getColumn("prefecture_codespace", prefix + "prefecture_codespace"));
        if (projectionFilter.containsProperty("city", module))
            select.addProjection(table.getColumn("city", prefix + "city"), table.getColumn("city_codespace", prefix + "city_codespace"));
        if (projectionFilter.containsProperty("surveyYear", module))
            select.addProjection(table.getColumn("surveyyear", prefix + "surveyyear"));
        if (projectionFilter.containsProperty("lod-1MultiSurface", module))
            select.addProjection(table.getColumn("lod_1multisurface_id", prefix + "lod_1multisurface_id"));
        if (projectionFilter.containsProperty("lod-2MultiSurface", module))
            select.addProjection(table.getColumn("lod_2multisurface_id", prefix + "lod_2multisurface_id"));
        if (projectionFilter.containsProperty("value", module))
            select.addProjection(table.getColumn("value", prefix + "value"));
        if (projectionFilter.containsProperty("availability", module))
            select.addProjection(table.getColumn("availability", prefix + "availability"));

        return select;
    }

    public void doExport(StatisticalGrid statisticalGrid, ProjectionFilter projectionFilter, String prefix, ResultSet rs) throws CityGMLExportException, SQLException {
        if (projectionFilter.containsProperty("class", module)) {
            String classifier = rs.getString(prefix + "class");
            if (!rs.wasNull()) {
                Code code = new Code(classifier);
                code.setCodeSpace(rs.getString(prefix + "class_codespace"));
                statisticalGrid.setClassifier(code);
            }
        }

        if (projectionFilter.containsProperty("urbanPlanType", module)) {
            String urbanPlanType = rs.getString(prefix + "urbanplantype");
            if (!rs.wasNull()) {
                Code code = new Code(urbanPlanType);
                code.setCodeSpace(rs.getString(prefix + "urbanplantype_codespace"));
                statisticalGrid.setUrbanPlanType(code);
            }
        }

        if (projectionFilter.containsProperty("areaClassificationType", module)) {
            String areaClassificationType = rs.getString(prefix + "areaclassificationtype");
            if (!rs.wasNull()) {
                Code code = new Code(areaClassificationType);
                code.setCodeSpace(rs.getString(prefix + "areaclassification_codespace"));
                statisticalGrid.setAreaClassificationType(code);
            }
        }

        if (projectionFilter.containsProperty("prefecture", module)) {
            String prefecture = rs.getString(prefix + "prefecture");
            if (!rs.wasNull()) {
                Code code = new Code(prefecture);
                code.setCodeSpace(rs.getString(prefix + "prefecture_codespace"));
                statisticalGrid.setPrefecture(code);
            }
        }

        if (projectionFilter.containsProperty("city", module)) {
            String city = rs.getString(prefix + "city");
            if (!rs.wasNull()) {
                Code code = new Code(city);
                code.setCodeSpace(rs.getString(prefix + "city_codespace"));
                statisticalGrid.setCity(code);
            }
        }

        if (projectionFilter.containsProperty("surveyYear", module)) {
            Date surveyYear = rs.getDate(prefix + "surveyyear");
            if (!rs.wasNull())
                statisticalGrid.setSurveyYear(Year.of(surveyYear.toLocalDate().getYear()));
        }

        for (int i = 0; i < 2; i++) {
            if (!projectionFilter.containsProperty("lod-" + (i + 1) + "MultiSurface", module))
                continue;

            long surfaceGeometryId = rs.getLong(prefix + "lod_" + (i + 1) + "multisurface_id");
            if (rs.wasNull())
                continue;

            switch (i) {
                case 0:
                    surfaceGeometryExporter.addBatch(surfaceGeometryId, statisticalGrid::setLod1MultiSurface);
                    break;
                case 1:
                    surfaceGeometryExporter.addBatch(surfaceGeometryId, statisticalGrid::setLod2MultiSurface);
                    break;
            }
        }

        if (projectionFilter.containsProperty("value", module)) {
            try {
                for (SplitValue splitValue : valueSplitter.split(rs.getString(prefix + "value"))) {
                    String value = "<urg:value xmlns:urg=\"" + StatisticalGridModule.v1_4.getNamespaceURI() + "\">" +
                            splitValue.result(0) + "</urg:value>";
                    statisticalGrid.getValues().add(manager.unmarshal(value));
                }
            } catch (ParserConfigurationException | IOException | SAXException e) {
                throw new CityGMLExportException("Failed to create <urg:value> element.", e);
            }
        }
    }

    @Override
    public void close() throws CityGMLExportException, SQLException {
        ps.close();
    }
}
