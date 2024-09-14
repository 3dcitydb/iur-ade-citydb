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
import org.citygml4j.ade.iur.model.urt.Frequency;
import org.citygml4j.ade.iur.model.urt.TripProperty;
import org.citygml4j.model.gml.basicTypes.Code;

import java.sql.*;

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
