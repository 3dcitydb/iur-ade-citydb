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
import org.citydb.core.ade.exporter.CityGMLExportHelper;
import org.citydb.core.database.schema.mapping.MappingConstants;
import org.citydb.core.operation.exporter.CityGMLExportException;
import org.citydb.sqlbuilder.expression.PlaceHolder;
import org.citydb.sqlbuilder.schema.Table;
import org.citydb.sqlbuilder.select.Select;
import org.citydb.sqlbuilder.select.join.JoinFactory;
import org.citydb.sqlbuilder.select.operator.comparison.ComparisonFactory;
import org.citydb.sqlbuilder.select.operator.comparison.ComparisonName;
import org.citygml4j.ade.iur.model.urt.StopProperty;
import org.citygml4j.ade.iur.model.urt.StopTime;
import org.citygml4j.ade.iur.model.urt.TripProperty;
import org.citygml4j.model.gml.basicTypes.Code;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;

public class StopTimeExporter implements PublicTransitModuleExporter {
    private final PreparedStatement ps;

    public StopTimeExporter(Connection connection, CityGMLExportHelper helper, ExportManager manager) throws CityGMLExportException, SQLException {
        String tableName = manager.getSchemaMapper().getTableName(ADETable.STOPTIME);

        Table table = new Table(helper.getTableNameWithSchema(tableName));
        Table stop = new Table(helper.getTableNameWithSchema(MappingConstants.CITYOBJECT));
        Table trip = new Table(helper.getTableNameWithSchema(MappingConstants.CITYOBJECT));

        Select select = new Select().addProjection(table.getColumns("arrivaltime", "continuousdropofftype",
                "continuousdropofft_codespace", "continuouspickuptype", "continuouspickupty_codespace", "departuretime",
                "dropofftype", "dropofftype_codespace", "headsign", "pickuptype", "pickuptype_codespace",
                "shapedistancetraveled", "stopsequence", "timepoint", "timepoint_codespace"))
                .addProjection(stop.getColumn("gmlid", "sgmlid"), trip.getColumn("gmlid", "tgmlid"))
                .addJoin(JoinFactory.left(stop, "id", ComparisonName.EQUAL_TO, table.getColumn("stop_id")))
                .addJoin(JoinFactory.left(trip, "id", ComparisonName.EQUAL_TO, table.getColumn("trip_id")))
                .addSelection(ComparisonFactory.equalTo(table.getColumn("id"), new PlaceHolder<>()));
        ps = connection.prepareStatement(select.toString());
    }

    public void doExport(StopTime stopTime, long objectId) throws CityGMLExportException, SQLException {
        ps.setLong(1, objectId);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                Time arrivalTime = rs.getTime("arrivaltime");
                if (!rs.wasNull())
                    stopTime.setArrivalTime(arrivalTime.toLocalTime());

                Time departureTime = rs.getTime("departuretime");
                if (!rs.wasNull())
                    stopTime.setDepartureTime(departureTime.toLocalTime());

                int stopSequence = rs.getInt("stopsequence");
                if (!rs.wasNull())
                    stopTime.setStopSequence(stopSequence);

                stopTime.setHeadsign(rs.getString("headsign"));

                String pickupType = rs.getString("pickuptype");
                if (!rs.wasNull()) {
                    Code code = new Code(pickupType);
                    code.setCodeSpace(rs.getString("pickuptype_codespace"));
                    stopTime.setPickupType(code);
                }

                String dropoffType = rs.getString("dropofftype");
                if (!rs.wasNull()) {
                    Code code = new Code(dropoffType);
                    code.setCodeSpace(rs.getString("dropofftype_codespace"));
                    stopTime.setDropoffType(code);
                }

                String continuousPickupType = rs.getString("continuouspickuptype");
                if (!rs.wasNull()) {
                    Code code = new Code(continuousPickupType);
                    code.setCodeSpace(rs.getString("continuouspickupty_codespace"));
                    stopTime.setContinuousPickupType(code);
                }

                String continuousDropoffType = rs.getString("continuousdropofftype");
                if (!rs.wasNull()) {
                    Code code = new Code(continuousDropoffType);
                    code.setCodeSpace(rs.getString("continuousdropofft_codespace"));
                    stopTime.setContinuousDropoffType(code);
                }

                double shapeDistanceTraveled = rs.getDouble("shapedistancetraveled");
                if (!rs.wasNull())
                    stopTime.setShapeDistanceTraveled(shapeDistanceTraveled);

                String timePoint = rs.getString("timepoint");
                if (!rs.wasNull()) {
                    Code code = new Code(timePoint);
                    code.setCodeSpace(rs.getString("timepoint_codespace"));
                    stopTime.setTimePoint(code);
                }

                String tripGmlId = rs.getString("tgmlid");
                if (tripGmlId != null)
                    stopTime.setTrip(new TripProperty("#" + tripGmlId));

                String stopGmlId = rs.getString("sgmlid");
                if (stopGmlId != null)
                    stopTime.setStop(new StopProperty("#" + stopGmlId));
            }
        }
    }

    @Override
    public void close() throws CityGMLExportException, SQLException {
        ps.close();
    }
}
