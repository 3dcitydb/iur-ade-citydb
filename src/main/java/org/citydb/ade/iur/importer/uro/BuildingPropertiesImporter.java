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

package org.citydb.ade.iur.importer.uro;

import org.citydb.ade.importer.ADEImporter;
import org.citydb.ade.importer.ADEPropertyCollection;
import org.citydb.ade.importer.CityGMLImportHelper;
import org.citydb.citygml.importer.CityGMLImportException;
import org.citydb.database.schema.mapping.FeatureType;
import org.citygml4j.model.citygml.building.AbstractBuilding;
import org.citydb.ade.iur.importer.ImportManager;
import org.citydb.ade.iur.schema.ADETable;
import org.citydb.ade.iur.schema.SchemaMapper;
import org.citygml4j.ade.iur.model.uro.BuildingDetailsPropertyElement;
import org.citygml4j.ade.iur.model.uro.LargeCustomerFacilitiesPropertyElement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class BuildingPropertiesImporter implements ADEImporter {
    private final CityGMLImportHelper helper;
    private final SchemaMapper schemaMapper;
    private final PreparedStatement ps;

    private final BuildingDetailsImporter buildingDetailsImporter;
    private final LargeCustomerFacilitiesImporter largeCustomerFacilitiesImporter;

    private int batchCounter;

    public BuildingPropertiesImporter(Connection connection, CityGMLImportHelper helper, ImportManager manager) throws CityGMLImportException, SQLException {
        this.helper = helper;
        this.schemaMapper = manager.getSchemaMapper();

        ps = connection.prepareStatement("insert into " +
                helper.getTableNameWithSchema(schemaMapper.getTableName(ADETable.BUILDING)) + " " +
                "(id, buildingdetails_id, largecustomerfacilities_id) " +
                "values (?, ?, ?)");

        buildingDetailsImporter = manager.getImporter(BuildingDetailsImporter.class);
        largeCustomerFacilitiesImporter = manager.getImporter(LargeCustomerFacilitiesImporter.class);
    }

    public void doImport(ADEPropertyCollection properties, AbstractBuilding parent, long parentId, FeatureType parentType) throws CityGMLImportException, SQLException {
        ps.setLong(1, parentId);

        long buildingDetailsId = 0;
        if (properties.contains(BuildingDetailsPropertyElement.class)) {
            BuildingDetailsPropertyElement property = properties.getFirst(BuildingDetailsPropertyElement.class);
            if (property.isSetValue() && property.getValue().isSetObject()) {
                buildingDetailsId = buildingDetailsImporter.doImport(property.getValue().getObject());
                property.setValue(null);
            }
        }

        if (buildingDetailsId != 0)
            ps.setLong(2, buildingDetailsId);
        else
            ps.setNull(2, Types.NULL);

        long largeCustomerFacilitiesId = 0;
        if (properties.contains(LargeCustomerFacilitiesPropertyElement.class)) {
            LargeCustomerFacilitiesPropertyElement property = properties.getFirst(LargeCustomerFacilitiesPropertyElement.class);
            if (property.isSetValue() && property.getValue().isSetObject()) {
                largeCustomerFacilitiesId = largeCustomerFacilitiesImporter.doImport(property.getValue().getObject());
                property.setValue(null);
            }
        }

        if (largeCustomerFacilitiesId != 0)
            ps.setLong(3, largeCustomerFacilitiesId);
        else
            ps.setNull(3, Types.NULL);

        ps.addBatch();
        if (++batchCounter == helper.getDatabaseAdapter().getMaxBatchSize())
            helper.executeBatch(schemaMapper.getTableName(ADETable.BUILDING));
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
