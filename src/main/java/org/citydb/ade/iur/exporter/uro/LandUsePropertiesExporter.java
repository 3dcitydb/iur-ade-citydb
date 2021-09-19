/*
 * 3D City Database - The Open Source CityGML Database
 * https://www.3dcitydb.org/
 *
 * Copyright 2013 - 2021
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

package org.citydb.ade.iur.exporter.uro;

import org.citydb.ade.iur.exporter.ExportManager;
import org.citydb.ade.iur.schema.ADETable;
import org.citydb.core.ade.exporter.CityGMLExportHelper;
import org.citydb.core.database.schema.mapping.FeatureType;
import org.citydb.core.operation.exporter.CityGMLExportException;
import org.citydb.core.query.filter.projection.ProjectionFilter;
import org.citydb.sqlbuilder.expression.PlaceHolder;
import org.citydb.sqlbuilder.schema.Table;
import org.citydb.sqlbuilder.select.Select;
import org.citydb.sqlbuilder.select.operator.comparison.ComparisonFactory;
import org.citygml4j.ade.iur.model.module.UrbanObjectModule;
import org.citygml4j.ade.iur.model.uro.*;
import org.citygml4j.model.citygml.landuse.LandUse;
import org.citygml4j.model.gml.basicTypes.Code;
import org.citygml4j.model.gml.basicTypes.Measure;

import java.sql.*;
import java.time.Year;

public class LandUsePropertiesExporter implements UrbanObjectModuleExporter {
    private final PreparedStatement ps;
    private final String module;

    public LandUsePropertiesExporter(Connection connection, CityGMLExportHelper helper, ExportManager manager) throws CityGMLExportException, SQLException {
        String tableName = manager.getSchemaMapper().getTableName(ADETable.LAND_USE);
        module = UrbanObjectModule.v1_4.getNamespaceURI();

        Table table = new Table(helper.getTableNameWithSchema(tableName));
        Select select = new Select().addProjection(table.getColumns("areaclassification_codespace", "areaclassificationtype",
                "areainha", "areainha_uom", "areainsquaremeter", "areainsquaremeter_uom", "city", "city_codespace",
                "districtsandzonest_codespace", "districtsandzonestype", "landuseplantype", "landuseplantype_codespace",
                "nominalarea", "nominalarea_uom", "note", "owner", "ownertype", "ownertype_codespace",
                "prefecture", "prefecture_codespace", "reference", "surveyyear", "urbanplantype", "urbanplantype_codespace"))
                .addSelection(ComparisonFactory.equalTo(table.getColumn("id"), new PlaceHolder<>()));
        ps = connection.prepareStatement(select.toString());
    }

    public void doExport(LandUse parent, long parentId, FeatureType parentType, ProjectionFilter projectionFilter) throws CityGMLExportException, SQLException {
        ps.setLong(1, parentId);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                if (projectionFilter.containsProperty("areaClassificationType", module)) {
                    String areaClassificationType = rs.getString("areaclassificationtype");
                    if (!rs.wasNull()) {
                        Code code = new Code(areaClassificationType);
                        code.setCodeSpace(rs.getString("areaclassification_codespace"));
                        AreaClassificationTypeProperty property = new AreaClassificationTypeProperty(code);
                        parent.addGenericApplicationPropertyOfLandUse(property);
                    }
                }

                if (projectionFilter.containsProperty("areaInHa", module)) {
                    double areaInHa = rs.getDouble("areainha");
                    if (!rs.wasNull()) {
                        Measure measure = new Measure(areaInHa);
                        measure.setUom(rs.getString("areainha_uom"));
                        AreaInHaProperty property = new AreaInHaProperty(measure);
                        parent.addGenericApplicationPropertyOfLandUse(property);
                    }
                }

                if (projectionFilter.containsProperty("areaInSquareMeter", module)) {
                    double areaInSquareMeter = rs.getDouble("areainsquaremeter");
                    if (!rs.wasNull()) {
                        Measure measure = new Measure(areaInSquareMeter);
                        measure.setUom(rs.getString("areainsquaremeter_uom"));
                        AreaInSquareMeterProperty property = new AreaInSquareMeterProperty(measure);
                        parent.addGenericApplicationPropertyOfLandUse(property);
                    }
                }

                if (projectionFilter.containsProperty("city", module)) {
                    String city = rs.getString("city");
                    if (!rs.wasNull()) {
                        Code code = new Code(city);
                        code.setCodeSpace(rs.getString("city_codespace"));
                        CityProperty property = new CityProperty(code);
                        parent.addGenericApplicationPropertyOfLandUse(property);
                    }
                }

                if (projectionFilter.containsProperty("districtsAndZonesType", module)) {
                    String districtsAndZonesType = rs.getString("districtsandzonestype");
                    if (!rs.wasNull()) {
                        Code code = new Code(districtsAndZonesType);
                        code.setCodeSpace(rs.getString("districtsandzonest_codespace"));
                        DistrictsAndZonesTypeProperty property = new DistrictsAndZonesTypeProperty(code);
                        parent.addGenericApplicationPropertyOfLandUse(property);
                    }
                }

                if (projectionFilter.containsProperty("landUsePlanType", module)) {
                    String landUsePlanType = rs.getString("landuseplantype");
                    if (!rs.wasNull()) {
                        Code code = new Code(landUsePlanType);
                        code.setCodeSpace(rs.getString("landuseplantype_codespace"));
                        LandUsePlanTypeProperty property = new LandUsePlanTypeProperty(code);
                        parent.addGenericApplicationPropertyOfLandUse(property);
                    }
                }

                if (projectionFilter.containsProperty("nominalArea", module)) {
                    double nominalarea = rs.getDouble("nominalarea");
                    if (!rs.wasNull()) {
                        Measure measure = new Measure(nominalarea);
                        measure.setUom(rs.getString("nominalarea_uom"));
                        AreaInSquareMeterProperty property = new AreaInSquareMeterProperty(measure);
                        parent.addGenericApplicationPropertyOfLandUse(property);
                    }
                }

                if (projectionFilter.containsProperty("note", module)) {
                    String note = rs.getString("note");
                    if (!rs.wasNull()) {
                        NoteProperty property = new NoteProperty(note);
                        parent.addGenericApplicationPropertyOfLandUse(property);
                    }
                }

                if (projectionFilter.containsProperty("owner", module)) {
                    String owner = rs.getString("owner");
                    if (!rs.wasNull()) {
                        OwnerProperty property = new OwnerProperty(owner);
                        parent.addGenericApplicationPropertyOfLandUse(property);
                    }
                }

                if (projectionFilter.containsProperty("ownerType", module)) {
                    String ownerType = rs.getString("ownertype");
                    if (!rs.wasNull()) {
                        Code code = new Code(ownerType);
                        code.setCodeSpace(rs.getString("ownertype_codespace"));
                        OwnerTypeProperty property = new OwnerTypeProperty(code);
                        parent.addGenericApplicationPropertyOfLandUse(property);
                    }
                }

                if (projectionFilter.containsProperty("prefecture", module)) {
                    String prefecture = rs.getString("prefecture");
                    if (!rs.wasNull()) {
                        Code code = new Code(prefecture);
                        code.setCodeSpace(rs.getString("prefecture_codespace"));
                        PrefectureProperty property = new PrefectureProperty(code);
                        parent.addGenericApplicationPropertyOfLandUse(property);
                    }
                }

                if (projectionFilter.containsProperty("reference", module)) {
                    String reference = rs.getString("reference");
                    if (!rs.wasNull()) {
                        ReferenceProperty property = new ReferenceProperty(reference);
                        parent.addGenericApplicationPropertyOfLandUse(property);
                    }
                }

                if (projectionFilter.containsProperty("surveyYear", module)) {
                    Date surveyYear = rs.getDate("surveyyear");
                    if (!rs.wasNull()) {
                        SurveyYearProperty property = new SurveyYearProperty(Year.of(surveyYear.toLocalDate().getYear()));
                        parent.addGenericApplicationPropertyOfLandUse(property);
                    }
                }

                if (projectionFilter.containsProperty("urbanPlanType", module)) {
                    String urbanPlanType = rs.getString("urbanplantype");
                    if (!rs.wasNull()) {
                        Code code = new Code(urbanPlanType);
                        code.setCodeSpace(rs.getString("urbanplantype_codespace"));
                        UrbanPlanTypeProperty property = new UrbanPlanTypeProperty(code);
                        parent.addGenericApplicationPropertyOfLandUse(property);
                    }
                }
            }
        }
    }

    @Override
    public void close() throws CityGMLExportException, SQLException {
        ps.close();
    }
}
