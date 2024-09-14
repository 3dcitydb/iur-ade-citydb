/*
 * 3D City Database - The Open Source CityGML Database
 * https://www.3dcitydb.org/
 *
 * Copyright 2013 - 2024
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
import org.citydb.core.ade.importer.CityGMLImportHelper;
import org.citydb.core.ade.importer.ForeignKeys;
import org.citydb.core.database.schema.mapping.AbstractObjectType;
import org.citydb.core.operation.importer.CityGMLImportException;
import org.citygml4j.ade.iur.model.urt.Agency;
import org.citygml4j.ade.iur.model.urt.FareAttribute;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class FareAttributeImporter implements PublicTransitModuleImporter {
    private final CityGMLImportHelper helper;
    private final SchemaMapper schemaMapper;
    private final PreparedStatement ps;
    private final PublicTransitImporter publicTransitImporter;

    private int batchCounter;

    public FareAttributeImporter(Connection connection, CityGMLImportHelper helper, ImportManager manager) throws CityGMLImportException, SQLException {
        this.helper = helper;
        this.schemaMapper = manager.getSchemaMapper();

        ps = connection.prepareStatement("insert into " +
                helper.getTableNameWithSchema(manager.getSchemaMapper().getTableName(ADETable.FAREATTRIBUTE)) + " " +
                "(id, currencytype, currencytype_codespace, paymentmethod, paymentmethod_codespace, price, " +
                "transferduration, transfers, transfers_codespace, agency_id) " +
                "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

        publicTransitImporter = manager.getImporter(PublicTransitImporter.class);
    }

    public void doImport(FareAttribute fareAttribute, long objectId, AbstractObjectType<?> objectType, ForeignKeys foreignKeys) throws CityGMLImportException, SQLException {
        publicTransitImporter.doImport(fareAttribute, objectId, objectType, foreignKeys);
        ps.setLong(1, objectId);

        if (fareAttribute.getCurrencyType() != null && fareAttribute.getCurrencyType().isSetValue()) {
            ps.setString(2, fareAttribute.getCurrencyType().getValue());
            ps.setString(3, fareAttribute.getCurrencyType().getCodeSpace());
        } else {
            ps.setNull(2, Types.VARCHAR);
            ps.setNull(3, Types.VARCHAR);
        }

        if (fareAttribute.getPaymentMethod() != null && fareAttribute.getPaymentMethod().isSetValue()) {
            ps.setString(4, fareAttribute.getPaymentMethod().getValue());
            ps.setString(5, fareAttribute.getPaymentMethod().getCodeSpace());
        } else {
            ps.setNull(4, Types.VARCHAR);
            ps.setNull(5, Types.VARCHAR);
        }

        ps.setDouble(6, fareAttribute.getPrice());

        if (fareAttribute.getTransferDuration() != null)
            ps.setInt(7, fareAttribute.getTransferDuration());
        else
            ps.setNull(7, Types.INTEGER);

        if (fareAttribute.getTransfers() != null && fareAttribute.getTransfers().isSetValue()) {
            ps.setString(8, fareAttribute.getTransfers().getValue());
            ps.setString(9, fareAttribute.getTransfers().getCodeSpace());
        } else {
            ps.setNull(8, Types.VARCHAR);
            ps.setNull(9, Types.VARCHAR);
        }

        long agencyId = 0;
        if (fareAttribute.getAgency() != null) {
            Agency agency = fareAttribute.getAgency().getObject();
            if (agency != null) {
                agencyId = helper.importObject(agency);
                fareAttribute.getAgency().unsetObject();
            } else {
                String href = fareAttribute.getAgency().getHref();
                if (href != null && !href.isEmpty()) {
                    helper.propagateObjectXlink(
                            schemaMapper.getTableName(ADETable.ROUTE),
                            objectId, href, "agency_id");
                }
            }
        }

        if (agencyId != 0)
            ps.setLong(10, agencyId);
        else
            ps.setNull(10, Types.NULL);

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
