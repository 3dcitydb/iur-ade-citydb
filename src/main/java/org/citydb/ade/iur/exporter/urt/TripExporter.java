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

import org.citydb.ade.exporter.CityGMLExportHelper;
import org.citydb.ade.iur.exporter.ExportManager;
import org.citydb.ade.iur.schema.ADETable;
import org.citydb.citygml.exporter.CityGMLExportException;
import org.citydb.citygml.exporter.database.content.GMLConverter;
import org.citydb.config.geometry.GeometryObject;
import org.citydb.database.schema.mapping.AbstractType;
import org.citydb.database.schema.mapping.MappingConstants;
import org.citydb.query.filter.projection.CombinedProjectionFilter;
import org.citydb.query.filter.projection.ProjectionFilter;
import org.citydb.sqlbuilder.expression.PlaceHolder;
import org.citydb.sqlbuilder.schema.Table;
import org.citydb.sqlbuilder.select.Select;
import org.citydb.sqlbuilder.select.join.JoinFactory;
import org.citydb.sqlbuilder.select.operator.comparison.ComparisonFactory;
import org.citydb.sqlbuilder.select.operator.comparison.ComparisonName;
import org.citygml4j.ade.iur.model.module.StatisticalGridModule;
import org.citygml4j.ade.iur.model.urt.CalendarDateProperty;
import org.citygml4j.ade.iur.model.urt.CalendarProperty;
import org.citygml4j.ade.iur.model.urt.OfficeProperty;
import org.citygml4j.ade.iur.model.urt.RouteProperty;
import org.citygml4j.ade.iur.model.urt.ShapeProperty;
import org.citygml4j.ade.iur.model.urt.Trip;
import org.citygml4j.model.gml.basicTypes.Code;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TripExporter implements PublicTransitModuleExporter {
    private final CityGMLExportHelper helper;
    private final PreparedStatement ps;
    private final String module;

    private final PublicTransitExporter publicTransitExporter;
    private final GMLConverter gmlConverter;

    public TripExporter(Connection connection, CityGMLExportHelper helper, ExportManager manager) throws CityGMLExportException, SQLException {
        this.helper = helper;

        String tableName = manager.getSchemaMapper().getTableName(ADETable.TRIP);
        CombinedProjectionFilter projectionFilter = helper.getCombinedProjectionFilter(tableName);
        module = StatisticalGridModule.v1_4.getNamespaceURI();

        publicTransitExporter = manager.getExporter(PublicTransitExporter.class);
        gmlConverter = helper.getGMLConverter();

        Table table = new Table(helper.getTableNameWithSchema(tableName));
        Table publicTransit = new Table(helper.getTableNameWithSchema(manager.getSchemaMapper().getTableName(ADETable.PUBLICTRANSIT)));
        Table route = new Table(helper.getTableNameWithSchema(MappingConstants.CITYOBJECT));

        Select select = new Select().addProjection(route.getColumn("gmlid", "rgmlid"))
                .addJoin(JoinFactory.inner(publicTransit, "id", ComparisonName.EQUAL_TO, table.getColumn("id")))
                .addJoin(JoinFactory.left(route, "id", ComparisonName.EQUAL_TO, table.getColumn("route_id")))
                .addSelection(ComparisonFactory.equalTo(table.getColumn("id"), new PlaceHolder<>()));
        publicTransitExporter.addProjection(select, publicTransit, projectionFilter, "pt");
        if (projectionFilter.containsProperty("headsign", module))
            select.addProjection(table.getColumn("headsign"));
        if (projectionFilter.containsProperty("shortName", module))
            select.addProjection(table.getColumn("shortname"));
        if (projectionFilter.containsProperty("directionId", module))
            select.addProjection(table.getColumns("directionid", "directionid_codespace"));
        if (projectionFilter.containsProperty("blockId", module))
            select.addProjection(table.getColumn("blockid"));
        if (projectionFilter.containsProperty("wheelchairAccessible", module))
            select.addProjection(table.getColumns("wheelchairaccessible", "wheelchairaccessib_codespace"));
        if (projectionFilter.containsProperty("bikeAllowed", module))
            select.addProjection(table.getColumns("bikeallowed", "bikeallowed_codespace"));
        if (projectionFilter.containsProperty("symbol", module))
            select.addProjection(table.getColumn("symbol"));
        if (helper.getLodFilter().isEnabled(0) && projectionFilter.containsProperty("lod0MultiCurve", module))
            select.addProjection(table.getColumn("lod0multicurve"));
        if (projectionFilter.containsProperty("calendar", module)) {
            Table cityObject = new Table(helper.getTableNameWithSchema(MappingConstants.CITYOBJECT));
            select.addProjection(cityObject.getColumn("gmlid", "cgmlid"))
                    .addJoin(JoinFactory.left(cityObject, "id", ComparisonName.EQUAL_TO, table.getColumn("calendar_id")));
        }
        if (projectionFilter.containsProperty("calendarDate", module)) {
            Table cityObject = new Table(helper.getTableNameWithSchema(MappingConstants.CITYOBJECT));
            select.addProjection(cityObject.getColumn("gmlid", "cdgmlid"))
                    .addJoin(JoinFactory.left(cityObject, "id", ComparisonName.EQUAL_TO, table.getColumn("calendardate_id")));
        }
        if (projectionFilter.containsProperty("office", module)) {
            Table cityObject = new Table(helper.getTableNameWithSchema(MappingConstants.CITYOBJECT));
            select.addProjection(cityObject.getColumn("gmlid", "ogmlid"))
                    .addJoin(JoinFactory.left(cityObject, "id", ComparisonName.EQUAL_TO, table.getColumn("office_id")));
        }
        if (projectionFilter.containsProperty("shape", module)) {
            Table cityObject = new Table(helper.getTableNameWithSchema(MappingConstants.CITYOBJECT));
            select.addProjection(cityObject.getColumn("gmlid", "sgmlid"))
                    .addJoin(JoinFactory.left(cityObject, "id", ComparisonName.EQUAL_TO, table.getColumn("shape_id")));
        }
        ps = connection.prepareStatement(select.toString());
    }

    public void doExport(Trip trip, long objectId, AbstractType<?> objectType, ProjectionFilter projectionFilter) throws CityGMLExportException, SQLException {
        ps.setLong(1, objectId);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                publicTransitExporter.doExport(trip, projectionFilter, "pt", rs);

                String routeGmlId = rs.getString("rgmlid");
                if (routeGmlId != null)
                    trip.setRoute(new RouteProperty("#" + routeGmlId));

                if (projectionFilter.containsProperty("headsign", module))
                    trip.setHeadsign(rs.getString("headsign"));

                if (projectionFilter.containsProperty("shortName", module))
                    trip.setShortName(rs.getString("shortname"));

                if (projectionFilter.containsProperty("directionId", module)) {
                    String directionId = rs.getString("directionid");
                    if (!rs.wasNull()) {
                        Code code = new Code(directionId);
                        code.setCodeSpace(rs.getString("directionid_codespace"));
                        trip.setDirectionId(code);
                    }
                }

                if (projectionFilter.containsProperty("blockId", module))
                    trip.setBlockId(rs.getString("blockid"));

                if (projectionFilter.containsProperty("wheelchairAccessible", module)) {
                    String wheelchairAccessible = rs.getString("wheelchairaccessible");
                    if (!rs.wasNull()) {
                        Code code = new Code(wheelchairAccessible);
                        code.setCodeSpace(rs.getString("wheelchairaccessib_codespace"));
                        trip.setWheelchairAccessible(code);
                    }
                }

                if (projectionFilter.containsProperty("bikeAllowed", module)) {
                    String bikeAllowed = rs.getString("bikeallowed");
                    if (!rs.wasNull()) {
                        Code code = new Code(bikeAllowed);
                        code.setCodeSpace(rs.getString("bikeallowed_codespace"));
                        trip.setBikeAllowed(code);
                    }
                }

                if (projectionFilter.containsProperty("symbol", module))
                    trip.setBlockId(rs.getString("symbol"));

                if (helper.getLodFilter().isEnabled(0)
                        && projectionFilter.containsProperty("lod0MultiCurve", module)) {
                    Object geometry = rs.getObject("lod0multicurve");
                    if (!rs.wasNull()) {
                        GeometryObject multiCurve = helper.getDatabaseAdapter().getGeometryConverter().getMultiCurve(geometry);
                        trip.setLod0MultiCurve(gmlConverter.getMultiCurveProperty(multiCurve));
                    }
                }

                if (projectionFilter.containsProperty("calendar", module)) {
                    String gmlId = rs.getString("cgmlid");
                    if (gmlId != null)
                        trip.setCalendar(new CalendarProperty("#" + gmlId));
                }

                if (projectionFilter.containsProperty("calendarDate", module)) {
                    String gmlId = rs.getString("cdgmlid");
                    if (gmlId != null)
                        trip.setCalendarDate(new CalendarDateProperty("#" + gmlId));
                }

                if (projectionFilter.containsProperty("office", module)) {
                    String gmlId = rs.getString("ogmlid");
                    if (gmlId != null)
                        trip.setOffice(new OfficeProperty("#" + gmlId));
                }

                if (projectionFilter.containsProperty("shape", module)) {
                    String gmlId = rs.getString("sgmlid");
                    if (gmlId != null)
                        trip.setShape(new ShapeProperty("#" + gmlId));
                }
            }
        }
    }

    @Override
    public void close() throws CityGMLExportException, SQLException {
        ps.close();
    }
}
