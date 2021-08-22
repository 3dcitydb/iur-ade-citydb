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

import org.citydb.ade.iur.importer.ImportManager;
import org.citydb.ade.iur.schema.ADETable;
import org.citydb.core.ade.importer.CityGMLImportHelper;
import org.citydb.core.ade.importer.ForeignKeys;
import org.citydb.core.database.schema.mapping.AbstractObjectType;
import org.citydb.core.operation.importer.CityGMLImportException;
import org.citygml4j.ade.iur.model.urg.AreaOfAnnualDiversionsProperty;
import org.citygml4j.ade.iur.model.urg.LandUseDiversion;
import org.citygml4j.ade.iur.model.urg.NumberOfAnnualDiversionsProperty;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class LandUseDiversionImporter implements StatisticalGridModuleImporter {
    private final CityGMLImportHelper helper;
    private final PreparedStatement ps;

    private final NumberOfAnnualDiversionsImporter numberOfAnnualDiversionsImporter;
    private final AreaOfAnnualDiversionsImporter areaOfAnnualDiversionsImporter;
    private final StatisticalGridImporter statisticalGridImporter;

    private int batchCounter;

    public LandUseDiversionImporter(Connection connection, CityGMLImportHelper helper, ImportManager manager) throws CityGMLImportException, SQLException {
        this.helper = helper;

        ps = connection.prepareStatement("insert into " +
                helper.getTableNameWithSchema(manager.getSchemaMapper().getTableName(ADETable.LANDUSEDIVERSION)) + " " +
                "(id) values (?)");

        numberOfAnnualDiversionsImporter = manager.getImporter(NumberOfAnnualDiversionsImporter.class);
        areaOfAnnualDiversionsImporter = manager.getImporter(AreaOfAnnualDiversionsImporter.class);
        statisticalGridImporter = manager.getImporter(StatisticalGridImporter.class);
    }

    public void doImport(LandUseDiversion landUseDiversion, long objectId, AbstractObjectType<?> objectType, ForeignKeys foreignKeys) throws CityGMLImportException, SQLException {
        statisticalGridImporter.doImport(landUseDiversion, objectId, objectType, foreignKeys);
        ps.setLong(1, objectId);

        for (NumberOfAnnualDiversionsProperty property : landUseDiversion.getNumberOfAnnualDiversions()) {
            if (property.isSetObject())
                numberOfAnnualDiversionsImporter.doImport(property.getObject(), objectId);
        }

        for (AreaOfAnnualDiversionsProperty property : landUseDiversion.getAreaOfAnnualDiversions()) {
            if (property.isSetObject())
                areaOfAnnualDiversionsImporter.doImport(property.getObject(), objectId);
        }

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
