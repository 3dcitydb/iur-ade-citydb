/*
 * iur-ade-citydb - i-Urban Revitalization ADE extension for the 3DCityDB
 * https://github.com/3dcitydb/iur-ade-citydb
 *
 * iur-ade-citydb is part of the 3D City Database project
 *
 * Copyright 2019-2020 virtualcitySYSTEMS GmbH
 * https://www.virtualcitysystems.de/
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
import org.citydb.database.schema.mapping.MappingConstants;
import org.citydb.sqlbuilder.expression.PlaceHolder;
import org.citydb.sqlbuilder.schema.Table;
import org.citydb.sqlbuilder.select.Select;
import org.citydb.sqlbuilder.select.join.JoinFactory;
import org.citydb.sqlbuilder.select.operator.comparison.ComparisonFactory;
import org.citydb.sqlbuilder.select.operator.comparison.ComparisonName;
import org.citygml4j.ade.iur.model.urt.Frequency;
import org.citygml4j.ade.iur.model.urt.TripProperty;
import org.citygml4j.model.gml.basicTypes.Code;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;

public class FrequencyExporter implements PublicTransitModuleExporter {
    private final PreparedStatement ps;

    public FrequencyExporter(Connection connection, CityGMLExportHelper helper, ExportManager manager) throws CityGMLExportException, SQLException {
        String tableName = manager.getSchemaMapper().getTableName(ADETable.FREQUENCY);

        Table table = new Table(helper.getTableNameWithSchema(tableName));
        Table trip = new Table(helper.getTableNameWithSchema(MappingConstants.CITYOBJECT));

        Select select = new Select().addProjection(table.getColumns("endtime", "exacttimes", "exacttimes_codespace",
                "headwaysecs", "starttime"))
                .addProjection(trip.getColumn("gmlid", "tgmlid"))
                .addJoin(JoinFactory.left(trip, "id", ComparisonName.EQUAL_TO, table.getColumn("trip_id")))
                .addSelection(ComparisonFactory.equalTo(table.getColumn("id"), new PlaceHolder<>()));
        ps = connection.prepareStatement(select.toString());
    }

    public void doExport(Frequency frequency, long objectId) throws CityGMLExportException, SQLException {
        ps.setLong(1, objectId);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                Time startTime = rs.getTime("starttime");
                if (!rs.wasNull())
                    frequency.setStartTime(startTime.toLocalTime());

                Time endTime = rs.getTime("endtime");
                if (!rs.wasNull())
                    frequency.setEndTime(endTime.toLocalTime());

                int headwaySecs = rs.getInt("headwaysecs");
                if (!rs.wasNull())
                    frequency.setHeadwaySecs(headwaySecs);

                String exactTimes = rs.getString("exacttimes");
                if (!rs.wasNull()) {
                    Code code = new Code(exactTimes);
                    code.setCodeSpace(rs.getString("exacttimes_codespace"));
                    frequency.setExactTimes(code);
                }

                String tripGmlId = rs.getString("tgmlid");
                if (tripGmlId != null)
                    frequency.setTrip(new TripProperty("#" + tripGmlId));
            }
        }
    }

    @Override
    public void close() throws CityGMLExportException, SQLException {
        ps.close();
    }
}
