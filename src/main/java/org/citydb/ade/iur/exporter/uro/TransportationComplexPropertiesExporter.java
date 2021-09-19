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
import org.citydb.core.query.filter.projection.CombinedProjectionFilter;
import org.citydb.core.query.filter.projection.ProjectionFilter;
import org.citydb.sqlbuilder.expression.PlaceHolder;
import org.citydb.sqlbuilder.schema.Table;
import org.citydb.sqlbuilder.select.Select;
import org.citydb.sqlbuilder.select.join.JoinFactory;
import org.citydb.sqlbuilder.select.operator.comparison.ComparisonFactory;
import org.citydb.sqlbuilder.select.operator.comparison.ComparisonName;
import org.citygml4j.ade.iur.model.module.UrbanObjectModule;
import org.citygml4j.ade.iur.model.uro.*;
import org.citygml4j.model.citygml.transportation.Road;
import org.citygml4j.model.citygml.transportation.TransportationComplex;
import org.citygml4j.model.gml.basicTypes.Code;
import org.citygml4j.model.gml.measures.Length;

import java.sql.*;
import java.time.Year;

public class TransportationComplexPropertiesExporter implements UrbanObjectModuleExporter {
    private final PreparedStatement ps;
    private final String module;

    public TransportationComplexPropertiesExporter(Connection connection, CityGMLExportHelper helper, ExportManager manager) throws CityGMLExportException, SQLException {
        String tableName = manager.getSchemaMapper().getTableName(ADETable.TRANSPORTATION_COMPLEX);
        CombinedProjectionFilter projectionFilter = helper.getCombinedProjectionFilter(tableName);
        module = UrbanObjectModule.v1_4.getNamespaceURI();

        Table table = new Table(helper.getTableNameWithSchema(tableName));
        Select select = new Select().addProjection(table.getColumns("trafficvolume_id", "width", "width_uom",
                "widthtype", "widthtype_codespace"));
        if (projectionFilter.containsProperty("trafficVolumeProperty", module)) {
            Table volume = new Table(helper.getTableNameWithSchema(manager.getSchemaMapper().getTableName(ADETable.TRAFFICVOLUME)));
            select.addProjection(volume.getColumn("areaclassificationtype", "area_2"), volume.getColumn("areaclassification_codespace", "area_codespace_2"),
                    volume.getColumn("city", "city_2"), volume.getColumn("city_codespace", "city_codespace_2"),
                    volume.getColumn("note", "note_2"), volume.getColumn("prefecture", "prefecture_2"),
                    volume.getColumn("prefecture_codespace", "prefecture_codespace_2"), volume.getColumn("reference", "reference_2"),
                    volume.getColumn("surveyyear", "surveyyear_2"), volume.getColumn("urbanplantype", "urban_2"),
                    volume.getColumn("urbanplantype_codespace", "urban_codespace_2"))
                    .addProjection(volume.getColumns("averagetravelspeedincongesti", "congestionrate", "largevehiclerate",
                            "observationpointname", "weekday12hourtrafficvolume", "weekday24hourtrafficvolume"))
                    .addJoin(JoinFactory.left(volume, "id", ComparisonName.EQUAL_TO, table.getColumn("trafficvolume_id")));
        }
        select.addSelection(ComparisonFactory.equalTo(table.getColumn("id"), new PlaceHolder<>()));
        ps = connection.prepareStatement(select.toString());
    }

    public void doExport(TransportationComplex parent, long parentId, FeatureType parentType, ProjectionFilter projectionFilter) throws CityGMLExportException, SQLException {
        if (parent instanceof Road) {
            Road road = (Road) parent;
            ps.setLong(1, parentId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    long trafficVolumeId = rs.getLong("trafficvolume_id");
                    if (trafficVolumeId != 0 && projectionFilter.containsProperty("trafficVolumeProperty", module)) {
                        TrafficVolume trafficVolume = new TrafficVolume();

                        trafficVolume.setNote(rs.getString("note_2"));
                        trafficVolume.setObservationPointName(rs.getString("observationpointname"));
                        trafficVolume.setReference(rs.getString("reference_2"));

                        double averageTravelSpeedInCongestion = rs.getDouble("averagetravelspeedincongesti");
                        if (!rs.wasNull())
                            trafficVolume.setAverageTravelSpeedInCongestion(averageTravelSpeedInCongestion);

                        double congestionRate = rs.getDouble("congestionrate");
                        if (!rs.wasNull())
                            trafficVolume.setCongestionRate(congestionRate);

                        double largeVehicleRate = rs.getDouble("largevehiclerate");
                        if (!rs.wasNull())
                            trafficVolume.setLargeVehicleRate(largeVehicleRate);

                        Date surveyYear = rs.getDate("surveyyear_2");
                        if (!rs.wasNull())
                            trafficVolume.setSurveyYear(Year.of(surveyYear.toLocalDate().getYear()));

                        int weekday12hourTrafficVolume = rs.getInt("weekday12hourtrafficvolume");
                        if (!rs.wasNull())
                            trafficVolume.setWeekday12hourTrafficVolume(weekday12hourTrafficVolume);

                        int weekday24hourTrafficVolume = rs.getInt("weekday24hourtrafficvolume");
                        if (!rs.wasNull())
                            trafficVolume.setWeekday24hourTrafficVolume(weekday24hourTrafficVolume);

                        String areaClassificationType = rs.getString("area_2");
                        if (!rs.wasNull()) {
                            Code code = new Code(areaClassificationType);
                            code.setCodeSpace(rs.getString("area_codespace_2"));
                            trafficVolume.setAreaClassificationType(code);
                        }

                        String city = rs.getString("city_2");
                        if (!rs.wasNull()) {
                            Code code = new Code(city);
                            code.setCodeSpace(rs.getString("city_codespace_2"));
                            trafficVolume.setCity(code);
                        }

                        String prefecture = rs.getString("prefecture_2");
                        if (!rs.wasNull()) {
                            Code code = new Code(prefecture);
                            code.setCodeSpace(rs.getString("prefecture_codespace_2"));
                            trafficVolume.setPrefecture(code);
                        }

                        String urbanPlanType = rs.getString("urban_2");
                        if (!rs.wasNull()) {
                            Code code = new Code(urbanPlanType);
                            code.setCodeSpace(rs.getString("urban_codespace_2"));
                            trafficVolume.setUrbanPlanType(code);
                        }

                        TrafficVolumePropertyElement property = new TrafficVolumePropertyElement(new TrafficVolumeProperty(trafficVolume));
                        road.addGenericApplicationPropertyOfRoad(property);
                    }

                    if (projectionFilter.containsProperty("width", module)) {
                        double width = rs.getDouble("width");
                        if (!rs.wasNull()) {
                            Length length = new Length(width);
                            length.setUom(rs.getString("width_uom"));
                            WidthProperty property = new WidthProperty(length);
                            road.addGenericApplicationPropertyOfRoad(property);
                        }
                    }

                    if (projectionFilter.containsProperty("widthType", module)) {
                        String widthType = rs.getString("widthtype");
                        if (!rs.wasNull()) {
                            Code code = new Code(widthType);
                            code.setCodeSpace(rs.getString("widthtype_codespace"));
                            WidthTypeProperty property = new WidthTypeProperty(code);
                            ((Road) parent).addGenericApplicationPropertyOfRoad(property);
                        }
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
