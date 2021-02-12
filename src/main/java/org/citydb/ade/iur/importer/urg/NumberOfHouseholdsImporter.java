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

package org.citydb.ade.iur.importer.urg;

import org.citydb.ade.importer.ADEImporter;
import org.citydb.ade.importer.CityGMLImportHelper;
import org.citydb.ade.iur.importer.ImportManager;
import org.citydb.ade.iur.schema.ADESequence;
import org.citydb.ade.iur.schema.ADETable;
import org.citydb.ade.iur.schema.SchemaMapper;
import org.citydb.citygml.importer.CityGMLImportException;
import org.citygml4j.ade.iur.model.urg.NumberOfHouseholds;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class NumberOfHouseholdsImporter implements ADEImporter {
    private final CityGMLImportHelper helper;
    private final SchemaMapper schemaMapper;
    private final PreparedStatement ps;

    private int batchCounter;

    enum Type {
        BY_OWNERSHIP,
        BY_STRUCTURE
    }

    public NumberOfHouseholdsImporter(Connection connection, CityGMLImportHelper helper, ImportManager manager) throws CityGMLImportException, SQLException {
        this.helper = helper;
        this.schemaMapper = manager.getSchemaMapper();

        ps = connection.prepareStatement("insert into " +
                helper.getTableNameWithSchema(schemaMapper.getTableName(ADETable.NUMBEROFHOUSEHOLDS)) + " " +
                "(id, households_numberofhouseh_id, households_numberofhous_id_1, class, class_codespace, number_) " +
                "values (?, ?, ?, ?, ?, ?)");
    }

    public void doImport(NumberOfHouseholds numberOfHouseholds, Type type, long parentId) throws CityGMLImportException, SQLException {
        long objectId = helper.getNextSequenceValue(schemaMapper.getSequenceName(ADESequence.NUMBEROFHOUSEHOLDS_SEQ));
        ps.setLong(1, objectId);

        if (type == Type.BY_OWNERSHIP) {
            ps.setLong(2, parentId);
            ps.setNull(3, Types.NULL);
        } else {
            ps.setNull(2, Types.NULL);
            ps.setLong(3, parentId);
        }

        if (numberOfHouseholds.getClassifier() != null && numberOfHouseholds.getClassifier().isSetValue()) {
            ps.setString(4, numberOfHouseholds.getClassifier().getValue());
            ps.setString(5, numberOfHouseholds.getClassifier().getCodeSpace());
        } else {
            ps.setNull(4, Types.VARCHAR);
            ps.setNull(5, Types.VARCHAR);
        }

        if (numberOfHouseholds.getNumber() != null)
            ps.setInt(6, numberOfHouseholds.getNumber());
        else
            ps.setNull(6, Types.INTEGER);

        ps.addBatch();
        if (++batchCounter == helper.getDatabaseAdapter().getMaxBatchSize())
            helper.executeBatch(schemaMapper.getTableName(ADETable.NUMBEROFHOUSEHOLDS));
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
