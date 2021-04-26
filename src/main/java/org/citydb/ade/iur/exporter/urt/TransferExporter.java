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
import org.citydb.database.schema.mapping.MappingConstants;
import org.citydb.sqlbuilder.expression.PlaceHolder;
import org.citydb.sqlbuilder.schema.Table;
import org.citydb.sqlbuilder.select.Select;
import org.citydb.sqlbuilder.select.join.JoinFactory;
import org.citydb.sqlbuilder.select.operator.comparison.ComparisonFactory;
import org.citydb.sqlbuilder.select.operator.comparison.ComparisonName;
import org.citygml4j.ade.iur.model.urt.StopProperty;
import org.citygml4j.ade.iur.model.urt.Transfer;
import org.citygml4j.model.gml.basicTypes.Code;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TransferExporter implements PublicTransitModuleExporter {
    private final PreparedStatement ps;

    public TransferExporter(Connection connection, CityGMLExportHelper helper, ExportManager manager) throws CityGMLExportException, SQLException {
        String tableName = manager.getSchemaMapper().getTableName(ADETable.TRANSFER);

        Table table = new Table(helper.getTableNameWithSchema(tableName));
        Table from = new Table(helper.getTableNameWithSchema(MappingConstants.CITYOBJECT));
        Table to = new Table(helper.getTableNameWithSchema(MappingConstants.CITYOBJECT));

        Select select = new Select().addProjection(table.getColumns("mintransfertime", "transfertype", "transfertype_codespace"))
                .addProjection(from.getColumn("gmlid", "fgmlid"), to.getColumn("gmlid", "tgmlid"))
                .addJoin(JoinFactory.left(from, "id", ComparisonName.EQUAL_TO, table.getColumn("from_id")))
                .addJoin(JoinFactory.left(to, "id", ComparisonName.EQUAL_TO, table.getColumn("to_id")))
                .addSelection(ComparisonFactory.equalTo(table.getColumn("id"), new PlaceHolder<>()));
        ps = connection.prepareStatement(select.toString());
    }

    public void doExport(Transfer transfer, long objectId) throws CityGMLExportException, SQLException {
        ps.setLong(1, objectId);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                String transferType = rs.getString("transfertype");
                if (!rs.wasNull()) {
                    Code code = new Code(transferType);
                    code.setCodeSpace(rs.getString("transfertype_codespace"));
                    transfer.setTransferType(code);
                }

                int minTransferTime = rs.getInt("mintransfertime");
                if (!rs.wasNull())
                    transfer.setMinTransferTime(minTransferTime);

                String fromGmlId = rs.getString("fgmlid");
                if (fromGmlId != null)
                    transfer.setFrom(new StopProperty("#" + fromGmlId));

                String toGmlId = rs.getString("tgmlid");
                if (toGmlId != null)
                    transfer.setTo(new StopProperty("#" + toGmlId));
            }
        }
    }

    @Override
    public void close() throws CityGMLExportException, SQLException {
        ps.close();
    }
}
