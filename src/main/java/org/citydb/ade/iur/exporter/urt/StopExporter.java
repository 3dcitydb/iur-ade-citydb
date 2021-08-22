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

package org.citydb.ade.iur.exporter.urt;

import org.citydb.ade.iur.exporter.ExportManager;
import org.citydb.ade.iur.schema.ADETable;
import org.citydb.config.geometry.GeometryObject;
import org.citydb.core.ade.exporter.CityGMLExportHelper;
import org.citydb.core.database.schema.mapping.AbstractType;
import org.citydb.core.database.schema.mapping.MappingConstants;
import org.citydb.core.operation.exporter.CityGMLExportException;
import org.citydb.core.operation.exporter.database.content.GMLConverter;
import org.citydb.core.query.filter.projection.CombinedProjectionFilter;
import org.citydb.core.query.filter.projection.ProjectionFilter;
import org.citydb.sqlbuilder.expression.PlaceHolder;
import org.citydb.sqlbuilder.schema.Table;
import org.citydb.sqlbuilder.select.Select;
import org.citydb.sqlbuilder.select.join.JoinFactory;
import org.citydb.sqlbuilder.select.operator.comparison.ComparisonFactory;
import org.citydb.sqlbuilder.select.operator.comparison.ComparisonName;
import org.citygml4j.ade.iur.model.module.StatisticalGridModule;
import org.citygml4j.ade.iur.model.urt.LevelProperty;
import org.citygml4j.ade.iur.model.urt.Stop;
import org.citygml4j.ade.iur.model.urt.StopProperty;
import org.citygml4j.model.gml.basicTypes.Code;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StopExporter implements PublicTransitModuleExporter {
    private final CityGMLExportHelper helper;
    private final PreparedStatement ps;
    private final String module;

    private final PublicTransitExporter publicTransitExporter;
    private final GMLConverter gmlConverter;

    public StopExporter(Connection connection, CityGMLExportHelper helper, ExportManager manager) throws CityGMLExportException, SQLException {
        this.helper = helper;

        String tableName = manager.getSchemaMapper().getTableName(ADETable.STOP);
        CombinedProjectionFilter projectionFilter = helper.getCombinedProjectionFilter(tableName);
        module = StatisticalGridModule.v1_4.getNamespaceURI();

        publicTransitExporter = manager.getExporter(PublicTransitExporter.class);
        gmlConverter = helper.getGMLConverter();

        Table table = new Table(helper.getTableNameWithSchema(tableName));
        Table publicTransit = new Table(helper.getTableNameWithSchema(manager.getSchemaMapper().getTableName(ADETable.PUBLICTRANSIT)));

        Select select = new Select().addJoin(JoinFactory.inner(publicTransit, "id", ComparisonName.EQUAL_TO, table.getColumn("id")))
                .addSelection(ComparisonFactory.equalTo(table.getColumn("id"), new PlaceHolder<>()));
        publicTransitExporter.addProjection(select, publicTransit, projectionFilter, "pt");
        if (projectionFilter.containsProperty("code", module))
            select.addProjection(table.getColumns("code", "code_codespace"));
        if (projectionFilter.containsProperty("ttsName", module))
            select.addProjection(table.getColumn("ttsname"));
        if (projectionFilter.containsProperty("latitude", module))
            select.addProjection(table.getColumn("latitude"));
        if (projectionFilter.containsProperty("longitude", module))
            select.addProjection(table.getColumn("longitude"));
        if (projectionFilter.containsProperty("zoneId", module))
            select.addProjection(table.getColumns("zoneid", "zoneid_codespace"));
        if (projectionFilter.containsProperty("url", module))
            select.addProjection(table.getColumn("url"));
        if (projectionFilter.containsProperty("locationType", module))
            select.addProjection(table.getColumns("locationtype", "locationtype_codespace"));
        if (projectionFilter.containsProperty("timeZone", module))
            select.addProjection(table.getColumns("timezone", "timezone_codespace"));
        if (projectionFilter.containsProperty("wheelchairBoarding", module))
            select.addProjection(table.getColumns("wheelchairboarding", "wheelchairboarding_codespace"));
        if (projectionFilter.containsProperty("platformCode", module))
            select.addProjection(table.getColumn("platformcode"));
        if (projectionFilter.containsProperty("point", module))
            select.addProjection(table.getColumn("point"));
        if (projectionFilter.containsProperty("level", module)) {
            Table cityObject = new Table(helper.getTableNameWithSchema(MappingConstants.CITYOBJECT));
            select.addProjection(cityObject.getColumn("gmlid", "lgmlid"))
                    .addJoin(JoinFactory.left(cityObject, "id", ComparisonName.EQUAL_TO, table.getColumn("level_id")));
        }
        if (projectionFilter.containsProperty("parentStation", module)) {
            Table cityObject = new Table(helper.getTableNameWithSchema(MappingConstants.CITYOBJECT));
            select.addProjection(cityObject.getColumn("gmlid", "pgmlid"))
                    .addJoin(JoinFactory.left(cityObject, "id", ComparisonName.EQUAL_TO, table.getColumn("parentstation_id")));
        }
        ps = connection.prepareStatement(select.toString());
    }

    public void doExport(Stop stop, long objectId, AbstractType<?> objectType, ProjectionFilter projectionFilter) throws CityGMLExportException, SQLException {
        ps.setLong(1, objectId);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                publicTransitExporter.doExport(stop, projectionFilter, "pt", rs);

                if (projectionFilter.containsProperty("code", module)) {
                    String codeValue = rs.getString("code");
                    if (!rs.wasNull()) {
                        Code code = new Code(codeValue);
                        code.setCodeSpace(rs.getString("code_codespace"));
                        stop.setCode(code);
                    }
                }

                if (projectionFilter.containsProperty("ttsName", module))
                    stop.setTtsName(rs.getString("ttsname"));

                if (projectionFilter.containsProperty("latitude", module)) {
                    double latitude = rs.getDouble("latitude");
                    if (!rs.wasNull())
                        stop.setLatitude(latitude);
                }

                if (projectionFilter.containsProperty("longitude", module)) {
                    double longitude = rs.getDouble("longitude");
                    if (!rs.wasNull())
                        stop.setLongitude(longitude);
                }

                if (projectionFilter.containsProperty("zoneId", module)) {
                    String zoneId = rs.getString("zoneid");
                    if (!rs.wasNull()) {
                        Code code = new Code(zoneId);
                        code.setCodeSpace(rs.getString("zoneid_codespace"));
                        stop.setZoneId(code);
                    }
                }

                if (projectionFilter.containsProperty("url", module))
                    stop.setUrl(rs.getString("url"));

                if (projectionFilter.containsProperty("locationType", module)) {
                    String locationType = rs.getString("locationtype");
                    if (!rs.wasNull()) {
                        Code code = new Code(locationType);
                        code.setCodeSpace(rs.getString("locationtype_codespace"));
                        stop.setLocationType(code);
                    }
                }

                if (projectionFilter.containsProperty("timeZone", module)) {
                    String timeZone = rs.getString("timezone");
                    if (!rs.wasNull()) {
                        Code code = new Code(timeZone);
                        code.setCodeSpace(rs.getString("timezone_codespace"));
                        stop.setTimeZone(code);
                    }
                }

                if (projectionFilter.containsProperty("wheelchairBoarding", module)) {
                    String wheelchairBoarding = rs.getString("wheelchairboarding");
                    if (!rs.wasNull()) {
                        Code code = new Code(wheelchairBoarding);
                        code.setCodeSpace(rs.getString("wheelchairboarding_codespace"));
                        stop.setWheelchairBoarding(code);
                    }
                }

                if (projectionFilter.containsProperty("platformCode", module))
                    stop.setPlatformCode(rs.getString("platformcode"));

                if (projectionFilter.containsProperty("point", module)) {
                    Object geometry = rs.getObject("point");
                    if (!rs.wasNull()) {
                        GeometryObject point = helper.getDatabaseAdapter().getGeometryConverter().getPoint(geometry);
                        stop.setPoint(gmlConverter.getPointProperty(point));
                    }
                }

                if (projectionFilter.containsProperty("level", module)) {
                    String gmlId = rs.getString("lgmlid");
                    if (gmlId != null)
                        stop.setLevel(new LevelProperty("#" + gmlId));
                }

                if (projectionFilter.containsProperty("parentStation", module)) {
                    String gmlId = rs.getString("pgmlid");
                    if (gmlId != null)
                        stop.setParentStation(new StopProperty("#" + gmlId));
                }
            }
        }
    }

    @Override
    public void close() throws CityGMLExportException, SQLException {
        ps.close();
    }
}
