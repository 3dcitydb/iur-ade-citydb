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

import org.citydb.ade.importer.CityGMLImportHelper;
import org.citydb.ade.iur.importer.ImportManager;
import org.citydb.ade.iur.schema.ADETable;
import org.citydb.ade.iur.schema.SchemaMapper;
import org.citydb.citygml.importer.CityGMLImportException;
import org.citygml4j.ade.iur.model.urt.Stop;
import org.citygml4j.ade.iur.model.urt.StopTime;
import org.citygml4j.ade.iur.model.urt.Trip;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class StopTimeImporter implements PublicTransitModuleImporter {
    private final CityGMLImportHelper helper;
    private final SchemaMapper schemaMapper;
    private final PreparedStatement ps;
    private final PublicTransitDataTypeImporter dataTypeImporter;

    private int batchCounter;

    public StopTimeImporter(Connection connection, CityGMLImportHelper helper, ImportManager manager) throws CityGMLImportException, SQLException {
        this.helper = helper;
        this.schemaMapper = manager.getSchemaMapper();

        ps = connection.prepareStatement("insert into " +
                helper.getTableNameWithSchema(manager.getSchemaMapper().getTableName(ADETable.STOPTIME)) + " " +
                "(id, arrivaltime, continuousdropofftype, continuousdropofft_codespace, continuouspickuptype, " +
                "continuouspickupty_codespace, departuretime, dropofftype, dropofftype_codespace, headsign, " +
                "pickuptype, pickuptype_codespace, shapedistancetraveled, stopsequence, timepoint, timepoint_codespace, " +
                "stop_id, trip_id) " +
                "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

        dataTypeImporter = manager.getImporter(PublicTransitDataTypeImporter.class);
    }

    public void doImport(StopTime stopTime, long cityObjectGroupId) throws CityGMLImportException, SQLException {
        long objectId = dataTypeImporter.doImport(stopTime, cityObjectGroupId);
        ps.setLong(1, objectId);

        if (stopTime.getArrivalTime() != null)
            ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.of(LocalDate.ofEpochDay(0), stopTime.getArrivalTime())));
        else
            ps.setNull(2, Types.TIMESTAMP_WITH_TIMEZONE);

        if (stopTime.getContinuousDropoffType() != null && stopTime.getContinuousDropoffType().isSetValue()) {
            ps.setString(3, stopTime.getContinuousDropoffType().getValue());
            ps.setString(4, stopTime.getContinuousDropoffType().getCodeSpace());
        } else {
            ps.setNull(3, Types.VARCHAR);
            ps.setNull(4, Types.VARCHAR);
        }

        if (stopTime.getContinuousPickupType() != null && stopTime.getContinuousPickupType().isSetValue()) {
            ps.setString(5, stopTime.getContinuousPickupType().getValue());
            ps.setString(6, stopTime.getContinuousPickupType().getCodeSpace());
        } else {
            ps.setNull(5, Types.VARCHAR);
            ps.setNull(6, Types.VARCHAR);
        }

        if (stopTime.getDepartureTime() != null)
            ps.setTimestamp(7, Timestamp.valueOf(LocalDateTime.of(LocalDate.ofEpochDay(0), stopTime.getDepartureTime())));
        else
            ps.setNull(7, Types.TIMESTAMP_WITH_TIMEZONE);

        if (stopTime.getDropoffType() != null && stopTime.getDropoffType().isSetValue()) {
            ps.setString(8, stopTime.getDropoffType().getValue());
            ps.setString(9, stopTime.getDropoffType().getCodeSpace());
        } else {
            ps.setNull(8, Types.VARCHAR);
            ps.setNull(9, Types.VARCHAR);
        }

        ps.setString(10, stopTime.getHeadsign());

        if (stopTime.getPickupType() != null && stopTime.getPickupType().isSetValue()) {
            ps.setString(11, stopTime.getPickupType().getValue());
            ps.setString(12, stopTime.getPickupType().getCodeSpace());
        } else {
            ps.setNull(11, Types.VARCHAR);
            ps.setNull(12, Types.VARCHAR);
        }

        if (stopTime.getShapeDistanceTraveled() != null)
            ps.setDouble(13, stopTime.getShapeDistanceTraveled());
        else
            ps.setNull(13, Types.DOUBLE);

        if (stopTime.getStopSequence() != null)
            ps.setInt(14, stopTime.getStopSequence());
        else
            ps.setNull(14, Types.INTEGER);

        if (stopTime.getTimePoint() != null && stopTime.getTimePoint().isSetValue()) {
            ps.setString(15, stopTime.getTimePoint().getValue());
            ps.setString(16, stopTime.getTimePoint().getCodeSpace());
        } else {
            ps.setNull(15, Types.VARCHAR);
            ps.setNull(16, Types.VARCHAR);
        }

        long stopId = 0;
        if (stopTime.getStop() != null) {
            Stop stop = stopTime.getStop().getObject();
            if (stop != null) {
                stopId = helper.importObject(stop);
                stopTime.getStop().unsetObject();
            } else {
                String href = stopTime.getStop().getHref();
                if (href != null && !href.isEmpty()) {
                    helper.propagateObjectXlink(
                            schemaMapper.getTableName(ADETable.STOPTIME),
                            objectId, href, "stop_id");
                }
            }
        }

        if (stopId != 0)
            ps.setLong(17, stopId);
        else
            ps.setNull(17, Types.NULL);

        long tripId = 0;
        if (stopTime.getTrip() != null) {
            Trip trip = stopTime.getTrip().getObject();
            if (trip != null) {
                tripId = helper.importObject(trip);
                stopTime.getTrip().unsetObject();
            } else {
                String href = stopTime.getTrip().getHref();
                if (href != null && !href.isEmpty()) {
                    helper.propagateObjectXlink(
                            schemaMapper.getTableName(ADETable.STOPTIME),
                            objectId, href, "trip_id");
                }
            }
        }

        if (tripId != 0)
            ps.setLong(18, tripId);
        else
            ps.setNull(18, Types.NULL);

        ps.addBatch();
        if (++batchCounter == helper.getDatabaseAdapter().getMaxBatchSize())
            helper.executeBatch(schemaMapper.getTableName(ADETable.STOPTIME));
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
