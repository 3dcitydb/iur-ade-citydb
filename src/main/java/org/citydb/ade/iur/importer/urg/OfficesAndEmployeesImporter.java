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

package org.citydb.ade.iur.importer.urg;

import org.citydb.ade.importer.CityGMLImportHelper;
import org.citydb.ade.importer.ForeignKeys;
import org.citydb.ade.iur.importer.ImportManager;
import org.citydb.ade.iur.schema.ADETable;
import org.citydb.citygml.importer.CityGMLImportException;
import org.citydb.database.schema.mapping.AbstractObjectType;
import org.citygml4j.ade.iur.model.urg.OfficesAndEmployees;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class OfficesAndEmployeesImporter implements StatisticalGridModuleImporter {
    private final CityGMLImportHelper helper;
    private final PreparedStatement ps;
    private final StatisticalGridImporter statisticalGridImporter;

    private int batchCounter;

    public OfficesAndEmployeesImporter(Connection connection, CityGMLImportHelper helper, ImportManager manager) throws CityGMLImportException, SQLException {
        this.helper = helper;

        ps = connection.prepareStatement("insert into " +
                helper.getTableNameWithSchema(manager.getSchemaMapper().getTableName(ADETable.OFFICESANDEMPLOYEES)) + " " +
                "(id, numberofemployees, numberofoffices) " +
                "values (?, ?, ?)");

        statisticalGridImporter = manager.getImporter(StatisticalGridImporter.class);
    }

    public void doImport(OfficesAndEmployees officesAndEmployees, long objectId, AbstractObjectType<?> objectType, ForeignKeys foreignKeys) throws CityGMLImportException, SQLException {
        statisticalGridImporter.doImport(officesAndEmployees, objectId, objectType, foreignKeys);
        ps.setLong(1, objectId);

        if (officesAndEmployees.getNumberOfEmployees() != null)
            ps.setInt(2, officesAndEmployees.getNumberOfEmployees());
        else
            ps.setNull(2, Types.INTEGER);

        if (officesAndEmployees.getNumberOfOffices() != null)
            ps.setInt(3, officesAndEmployees.getNumberOfOffices());
        else
            ps.setNull(3, Types.INTEGER);

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
