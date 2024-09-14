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

package org.citydb.ade.iur.importer.urf;

import org.citydb.ade.iur.importer.ImportManager;
import org.citydb.ade.iur.schema.ADETable;
import org.citydb.core.ade.importer.CityGMLImportHelper;
import org.citydb.core.ade.importer.ForeignKeys;
import org.citydb.core.database.schema.mapping.AbstractObjectType;
import org.citydb.core.operation.importer.CityGMLImportException;
import org.citygml4j.ade.iur.model.urf.PublicTransportationFacility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class PublicTransportationFacilityImporter implements UrbanFunctionModuleImporter {
    private final CityGMLImportHelper helper;
    private final PreparedStatement ps;
    private final UrbanFunctionImporter urbanFunctionImporter;

    private int batchCounter;

    public PublicTransportationFacilityImporter(Connection connection, CityGMLImportHelper helper, ImportManager manager) throws CityGMLImportException, SQLException {
        this.helper = helper;

        ps = connection.prepareStatement("insert into " +
                helper.getTableNameWithSchema(manager.getSchemaMapper().getTableName(ADETable.PUBLICTRANSPORTATIONFA)) + " " +
                "(id, companyname, frequencyofservice, numberofcustomers, routename, sectionname) " +
                "values (?, ?, ?)");

        urbanFunctionImporter = manager.getImporter(UrbanFunctionImporter.class);
    }

    public void doImport(PublicTransportationFacility publicTransit, long objectId, AbstractObjectType<?> objectType, ForeignKeys foreignKeys) throws CityGMLImportException, SQLException {
        urbanFunctionImporter.doImport(publicTransit, objectId, objectType, foreignKeys);
        ps.setLong(1, objectId);

        ps.setString(2, publicTransit.getCompanyName());

        if (publicTransit.getFrequencyOfService() != null)
            ps.setInt(3, publicTransit.getFrequencyOfService());
        else
            ps.setNull(3, Types.INTEGER);

        if (publicTransit.getNumberOfCustomers() != null)
            ps.setDouble(4, publicTransit.getNumberOfCustomers());
        else
            ps.setNull(4, Types.DOUBLE);

        ps.setString(5, publicTransit.getRouteName());
        ps.setString(6, publicTransit.getSectionName());

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
