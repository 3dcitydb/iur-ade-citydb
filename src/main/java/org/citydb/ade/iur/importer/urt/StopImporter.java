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
import org.citydb.core.operation.importer.database.content.GeometryConverter;
import org.citygml4j.ade.iur.model.urt.Level;
import org.citygml4j.ade.iur.model.urt.Stop;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class StopImporter implements PublicTransitModuleImporter {
    private final Connection connection;
    private final CityGMLImportHelper helper;
    private final SchemaMapper schemaMapper;
    private final PreparedStatement ps;

    private final PublicTransitImporter publicTransitImporter;
    private final GeometryConverter geometryConverter;

    private int batchCounter;

    public StopImporter(Connection connection, CityGMLImportHelper helper, ImportManager manager) throws CityGMLImportException, SQLException {
        this.connection = connection;
        this.helper = helper;
        this.schemaMapper = manager.getSchemaMapper();

        ps = connection.prepareStatement("insert into " +
                helper.getTableNameWithSchema(manager.getSchemaMapper().getTableName(ADETable.STOP)) + " " +
                "(id, code, code_codespace, latitude, longitude, locationtype, locationtype_codespace, platformcode, " +
                "point, timezone, timezone_codespace, ttsname, url, wheelchairboarding, wheelchairboarding_codespace, " +
                "zoneid, zoneid_codespace, level_id, parentstation_id) " +
                "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

        publicTransitImporter = manager.getImporter(PublicTransitImporter.class);
        geometryConverter = helper.getGeometryConverter();
    }

    public void doImport(Stop stop, long objectId, AbstractObjectType<?> objectType, ForeignKeys foreignKeys) throws CityGMLImportException, SQLException {
        publicTransitImporter.doImport(stop, objectId, objectType, foreignKeys);
        ps.setLong(1, objectId);

        if (stop.getCode() != null && stop.getCode().isSetValue()) {
            ps.setString(2, stop.getCode().getValue());
            ps.setString(3, stop.getCode().getCodeSpace());
        } else {
            ps.setNull(2, Types.VARCHAR);
            ps.setNull(3, Types.VARCHAR);
        }

        if (stop.getLatitude() != null)
            ps.setDouble(4, stop.getLatitude());
        else
            ps.setNull(4, Types.DOUBLE);

        if (stop.getLongitude() != null)
            ps.setDouble(5, stop.getLongitude());
        else
            ps.setNull(5, Types.DOUBLE);

        if (stop.getLocationType() != null && stop.getLocationType().isSetValue()) {
            ps.setString(6, stop.getLocationType().getValue());
            ps.setString(7, stop.getLocationType().getCodeSpace());
        } else {
            ps.setNull(6, Types.VARCHAR);
            ps.setNull(7, Types.VARCHAR);
        }

        ps.setString(8, stop.getPlatformCode());

        GeometryObject point = geometryConverter.getPoint(stop.getPoint());
        if (point != null) {
            ps.setObject(9, helper.getDatabaseAdapter().getGeometryConverter().getDatabaseObject(point, connection));
            stop.setPoint(null);
        } else
            ps.setNull(9, helper.getDatabaseAdapter().getGeometryConverter().getNullGeometryType(),
                    helper.getDatabaseAdapter().getGeometryConverter().getNullGeometryTypeName());

        if (stop.getTimeZone() != null && stop.getTimeZone().isSetValue()) {
            ps.setString(10, stop.getTimeZone().getValue());
            ps.setString(11, stop.getTimeZone().getCodeSpace());
        } else {
            ps.setNull(10, Types.VARCHAR);
            ps.setNull(11, Types.VARCHAR);
        }

        ps.setString(12, stop.getTtsName());
        ps.setString(13, stop.getUrl());

        if (stop.getWheelchairBoarding() != null && stop.getWheelchairBoarding().isSetValue()) {
            ps.setString(14, stop.getWheelchairBoarding().getValue());
            ps.setString(15, stop.getWheelchairBoarding().getCodeSpace());
        } else {
            ps.setNull(14, Types.VARCHAR);
            ps.setNull(15, Types.VARCHAR);
        }

        if (stop.getZoneId() != null && stop.getZoneId().isSetValue()) {
            ps.setString(16, stop.getZoneId().getValue());
            ps.setString(17, stop.getZoneId().getCodeSpace());
        } else {
            ps.setNull(16, Types.VARCHAR);
            ps.setNull(17, Types.VARCHAR);
        }

        long levelId = 0;
        if (stop.getLevel() != null) {
            Level level = stop.getLevel().getObject();
            if (level != null) {
                levelId = helper.importObject(level);
                stop.getLevel().unsetObject();
            } else {
                String href = stop.getLevel().getHref();
                if (href != null && !href.isEmpty()) {
                    helper.propagateObjectXlink(
                            schemaMapper.getTableName(ADETable.STOP),
                            objectId, href, "level_id");
                }
            }
        }

        if (levelId != 0)
            ps.setLong(18, levelId);
        else
            ps.setNull(18, Types.NULL);

        long parentStationId = 0;
        if (stop.getParentStation() != null) {
            Stop parent = stop.getParentStation().getObject();
            if (parent != null) {
                parentStationId = helper.importObject(parent);
                stop.getParentStation().unsetObject();
            } else {
                String href = stop.getParentStation().getHref();
                if (href != null && !href.isEmpty()) {
                    helper.propagateObjectXlink(
                            schemaMapper.getTableName(ADETable.STOP),
                            objectId, href, "parentstation_id");
                }
            }
        }

        if (parentStationId != 0)
            ps.setLong(19, parentStationId);
        else
            ps.setNull(19, Types.NULL);

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
