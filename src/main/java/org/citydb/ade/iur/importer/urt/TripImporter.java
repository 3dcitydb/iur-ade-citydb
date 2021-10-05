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

package org.citydb.ade.iur.importer.urt;

import org.citydb.ade.iur.importer.ImportManager;
import org.citydb.ade.iur.schema.ADETable;
import org.citydb.ade.iur.schema.SchemaMapper;
import org.citydb.config.geometry.GeometryObject;
import org.citydb.core.ade.importer.CityGMLImportHelper;
import org.citydb.core.ade.importer.ForeignKeys;
import org.citydb.core.database.schema.mapping.AbstractObjectType;
import org.citydb.core.operation.importer.CityGMLImportException;
import org.citydb.core.operation.importer.util.GeometryConverter;
import org.citygml4j.ade.iur.model.urt.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class TripImporter implements PublicTransitModuleImporter {
    private final Connection connection;
    private final CityGMLImportHelper helper;
    private final SchemaMapper schemaMapper;
    private final PreparedStatement ps;

    private final PublicTransitImporter publicTransitImporter;
    private final GeometryConverter geometryConverter;

    private int batchCounter;

    public TripImporter(Connection connection, CityGMLImportHelper helper, ImportManager manager) throws CityGMLImportException, SQLException {
        this.connection = connection;
        this.helper = helper;
        this.schemaMapper = manager.getSchemaMapper();

        ps = connection.prepareStatement("insert into " +
                helper.getTableNameWithSchema(manager.getSchemaMapper().getTableName(ADETable.TRIP)) + " " +
                "(id, bikeallowed, bikeallowed_codespace, blockid, directionid, directionid_codespace, headsign, " +
                "lod0multicurve, shortname, symbol, wheelchairaccessible, wheelchairaccessib_codespace, " +
                "calendar_id, calendardate_id, office_id, route_id, shape_id) " +
                "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

        publicTransitImporter = manager.getImporter(PublicTransitImporter.class);
        geometryConverter = helper.getGeometryConverter();
    }

    public void doImport(Trip trip, long objectId, AbstractObjectType<?> objectType, ForeignKeys foreignKeys) throws CityGMLImportException, SQLException {
        publicTransitImporter.doImport(trip, objectId, objectType, foreignKeys);
        ps.setLong(1, objectId);

        if (trip.getBikeAllowed() != null && trip.getBikeAllowed().isSetValue()) {
            ps.setString(2, trip.getBikeAllowed().getValue());
            ps.setString(3, trip.getBikeAllowed().getCodeSpace());
        } else {
            ps.setNull(2, Types.VARCHAR);
            ps.setNull(3, Types.VARCHAR);
        }

        ps.setString(4, trip.getBlockId());

        if (trip.getDirectionId() != null && trip.getDirectionId().isSetValue()) {
            ps.setString(5, trip.getDirectionId().getValue());
            ps.setString(6, trip.getDirectionId().getCodeSpace());
        } else {
            ps.setNull(5, Types.VARCHAR);
            ps.setNull(6, Types.VARCHAR);
        }

        ps.setString(7, trip.getHeadsign());

        GeometryObject multiCurve = geometryConverter.getMultiCurve(trip.getLod0MultiCurve());
        if (multiCurve != null) {
            ps.setObject(8, helper.getDatabaseAdapter().getGeometryConverter().getDatabaseObject(multiCurve, connection));
            trip.setLod0MultiCurve(null);
        } else
            ps.setNull(8, helper.getDatabaseAdapter().getGeometryConverter().getNullGeometryType(),
                    helper.getDatabaseAdapter().getGeometryConverter().getNullGeometryTypeName());

        ps.setString(9, trip.getShortName());
        ps.setString(10, trip.getSymbol());

        if (trip.getWheelchairAccessible() != null && trip.getWheelchairAccessible().isSetValue()) {
            ps.setString(11, trip.getWheelchairAccessible().getValue());
            ps.setString(12, trip.getWheelchairAccessible().getCodeSpace());
        } else {
            ps.setNull(11, Types.VARCHAR);
            ps.setNull(12, Types.VARCHAR);
        }

        long calendarId = 0;
        if (trip.getCalendar() != null) {
            Calendar calendar = trip.getCalendar().getObject();
            if (calendar != null) {
                calendarId = helper.importObject(calendar);
                trip.getCalendar().unsetObject();
            } else {
                String href = trip.getCalendar().getHref();
                if (href != null && !href.isEmpty()) {
                    helper.propagateObjectXlink(
                            schemaMapper.getTableName(ADETable.TRIP),
                            objectId, href, "calendar_id");
                }
            }
        }

        if (calendarId != 0)
            ps.setLong(13, calendarId);
        else
            ps.setNull(13, Types.NULL);

        long calendarDateId = 0;
        if (trip.getCalendarDate() != null) {
            CalendarDate calendarDate = trip.getCalendarDate().getObject();
            if (calendarDate != null) {
                calendarDateId = helper.importObject(calendarDate);
                trip.getCalendarDate().unsetObject();
            } else {
                String href = trip.getCalendarDate().getHref();
                if (href != null && !href.isEmpty()) {
                    helper.propagateObjectXlink(
                            schemaMapper.getTableName(ADETable.TRIP),
                            objectId, href, "calendardate_id");
                }
            }
        }

        if (calendarDateId != 0)
            ps.setLong(14, calendarDateId);
        else
            ps.setNull(14, Types.NULL);

        long officeId = 0;
        if (trip.getOffice() != null) {
            Office office = trip.getOffice().getObject();
            if (office != null) {
                officeId = helper.importObject(office);
                trip.getOffice().unsetObject();
            } else {
                String href = trip.getOffice().getHref();
                if (href != null && !href.isEmpty()) {
                    helper.propagateObjectXlink(
                            schemaMapper.getTableName(ADETable.TRIP),
                            objectId, href, "office_id");
                }
            }
        }

        if (officeId != 0)
            ps.setLong(15, officeId);
        else
            ps.setNull(15, Types.NULL);

        long routeId = 0;
        if (trip.getRoute() != null) {
            Route route = trip.getRoute().getObject();
            if (route != null) {
                routeId = helper.importObject(route);
                trip.getRoute().unsetObject();
            } else {
                String href = trip.getRoute().getHref();
                if (href != null && !href.isEmpty()) {
                    helper.propagateObjectXlink(
                            schemaMapper.getTableName(ADETable.TRIP),
                            objectId, href, "route_id");
                }
            }
        }

        if (routeId != 0)
            ps.setLong(16, routeId);
        else
            ps.setNull(16, Types.NULL);

        long shapeId = 0;
        if (trip.getShape() != null) {
            Shape shape = trip.getShape().getObject();
            if (shape != null) {
                shapeId = helper.importObject(shape);
                trip.getShape().unsetObject();
            } else {
                String href = trip.getShape().getHref();
                if (href != null && !href.isEmpty()) {
                    helper.propagateObjectXlink(
                            schemaMapper.getTableName(ADETable.TRIP),
                            objectId, href, "shape_id");
                }
            }
        }

        if (shapeId != 0)
            ps.setLong(17, shapeId);
        else
            ps.setNull(17, Types.NULL);

        ps.addBatch();
        if (++batchCounter == helper.getDatabaseAdapter().getMaxBatchSize())
            helper.executeBatch(objectType);
    }

    @Override
    public void executeBatch() throws CityGMLImportException, SQLException {
        if (batchCounter > 0) {
            ps.executeBatch();
            batchCounter = 0;
        }
    }

    @Override
    public void close() throws CityGMLImportException, SQLException {
        ps.close();
    }
}
