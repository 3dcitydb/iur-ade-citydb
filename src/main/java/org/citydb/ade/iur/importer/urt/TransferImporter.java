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
import org.citygml4j.ade.iur.model.urt.Transfer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class TransferImporter implements PublicTransitModuleImporter {
    private final CityGMLImportHelper helper;
    private final SchemaMapper schemaMapper;
    private final PreparedStatement ps;
    private final PublicTransitDataTypeImporter dataTypeImporter;

    private int batchCounter;

    public TransferImporter(Connection connection, CityGMLImportHelper helper, ImportManager manager) throws CityGMLImportException, SQLException {
        this.helper = helper;
        this.schemaMapper = manager.getSchemaMapper();

        ps = connection.prepareStatement("insert into " +
                helper.getTableNameWithSchema(manager.getSchemaMapper().getTableName(ADETable.TRANSFER)) + " " +
                "(id, mintransfertime, transfertype, transfertype_codespace, from_id, to_id) " +
                "values (?, ?, ?, ?, ?, ?)");

        dataTypeImporter = manager.getImporter(PublicTransitDataTypeImporter.class);
    }

    public void doImport(Transfer transfer, long cityObjectGroupId) throws CityGMLImportException, SQLException {
        long objectId = dataTypeImporter.doImport(transfer, cityObjectGroupId);
        ps.setLong(1, objectId);

        if (transfer.getMinTransferTime() != null)
            ps.setInt(2, transfer.getMinTransferTime());
        else
            ps.setNull(2, Types.INTEGER);

        if (transfer.getTransferType() != null && transfer.getTransferType().isSetValue()) {
            ps.setString(3, transfer.getTransferType().getValue());
            ps.setString(4, transfer.getTransferType().getCodeSpace());
        } else {
            ps.setNull(3, Types.VARCHAR);
            ps.setNull(4, Types.VARCHAR);
        }

        long fromId = 0;
        if (transfer.getFrom() != null) {
            Stop stop = transfer.getFrom().getObject();
            if (stop != null) {
                fromId = helper.importObject(stop);
                transfer.getFrom().unsetObject();
            } else {
                String href = transfer.getFrom().getHref();
                if (href != null && !href.isEmpty()) {
                    helper.propagateObjectXlink(
                            schemaMapper.getTableName(ADETable.TRANSFER),
                            objectId, href, "from_id");
                }
            }
        }

        if (fromId != 0)
            ps.setLong(5, fromId);
        else
            ps.setNull(5, Types.NULL);

        long toId = 0;
        if (transfer.getTo() != null) {
            Stop stop = transfer.getTo().getObject();
            if (stop != null) {
                toId = helper.importObject(stop);
                transfer.getTo().unsetObject();
            } else {
                String href = transfer.getTo().getHref();
                if (href != null && !href.isEmpty()) {
                    helper.propagateObjectXlink(
                            schemaMapper.getTableName(ADETable.TRANSFER),
                            objectId, href, "to_id");
                }
            }
        }

        if (toId != 0)
            ps.setLong(6, toId);
        else
            ps.setNull(6, Types.NULL);

        ps.addBatch();
        if (++batchCounter == helper.getDatabaseAdapter().getMaxBatchSize())
            helper.executeBatch(schemaMapper.getTableName(ADETable.TRANSFER));
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
