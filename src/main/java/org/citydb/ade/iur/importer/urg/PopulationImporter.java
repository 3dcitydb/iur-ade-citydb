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

package org.citydb.ade.iur.importer.urg;

import org.citydb.ade.iur.importer.ImportManager;
import org.citydb.ade.iur.schema.ADETable;
import org.citydb.core.ade.importer.CityGMLImportHelper;
import org.citydb.core.ade.importer.ForeignKeys;
import org.citydb.core.database.schema.mapping.AbstractObjectType;
import org.citydb.core.operation.importer.CityGMLImportException;
import org.citygml4j.ade.iur.model.urg.Population;
import org.citygml4j.ade.iur.model.urg.PopulationByAgeAndSexProperty;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class PopulationImporter implements StatisticalGridModuleImporter {
    private final CityGMLImportHelper helper;
    private final PreparedStatement ps;

    private final PopulationByAgeAndSexImporter populationByAgeAndSexImporter;
    private final StatisticalGridImporter statisticalGridImporter;

    private int batchCounter;

    public PopulationImporter(Connection connection, CityGMLImportHelper helper, ImportManager manager) throws CityGMLImportException, SQLException {
        this.helper = helper;

        ps = connection.prepareStatement("insert into " +
                helper.getTableNameWithSchema(manager.getSchemaMapper().getTableName(ADETable.POPULATION)) + " " +
                "(id, births, daytimepopulation, daytimepopulationdensity, deaths, femalepopulation, increasement, " +
                "malepopulation, movefrom, moveto, naturalincrease, socialincrease, total) " +
                "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

        populationByAgeAndSexImporter = manager.getImporter(PopulationByAgeAndSexImporter.class);
        statisticalGridImporter = manager.getImporter(StatisticalGridImporter.class);
    }

    public void doImport(Population population, long objectId, AbstractObjectType<?> objectType, ForeignKeys foreignKeys) throws CityGMLImportException, SQLException {
        statisticalGridImporter.doImport(population, objectId, objectType, foreignKeys);
        ps.setLong(1, objectId);

        if (population.getBirths() != null)
            ps.setInt(2, population.getBirths());
        else
            ps.setNull(2, Types.INTEGER);

        if (population.getDaytimePopulation() != null)
            ps.setInt(3, population.getDaytimePopulation());
        else
            ps.setNull(3, Types.INTEGER);

        if (population.getDaytimePopulationDensity() != null)
            ps.setDouble(4, population.getDaytimePopulationDensity());
        else
            ps.setNull(4, Types.DOUBLE);

        if (population.getDeaths() != null)
            ps.setInt(5, population.getDeaths());
        else
            ps.setNull(5, Types.INTEGER);

        if (population.getFemalePopulation() != null)
            ps.setInt(6, population.getFemalePopulation());
        else
            ps.setNull(6, Types.INTEGER);

        if (population.getIncreasement() != null)
            ps.setInt(7, population.getIncreasement());
        else
            ps.setNull(7, Types.INTEGER);

        if (population.getMalePopulation() != null)
            ps.setInt(8, population.getMalePopulation());
        else
            ps.setNull(8, Types.INTEGER);

        if (population.getMoveFrom() != null)
            ps.setInt(9, population.getMoveFrom());
        else
            ps.setNull(9, Types.INTEGER);

        if (population.getMoveTo() != null)
            ps.setInt(10, population.getMoveTo());
        else
            ps.setNull(10, Types.INTEGER);

        if (population.getNaturalIncrease() != null)
            ps.setInt(11, population.getNaturalIncrease());
        else
            ps.setNull(11, Types.INTEGER);

        if (population.getSocialIncrease() != null)
            ps.setInt(12, population.getSocialIncrease());
        else
            ps.setNull(12, Types.INTEGER);

        if (population.getTotal() != null)
            ps.setInt(13, population.getTotal());
        else
            ps.setNull(13, Types.INTEGER);

        ps.addBatch();
        if (++batchCounter == helper.getDatabaseAdapter().getMaxBatchSize())
            helper.executeBatch(objectType);

        for (PopulationByAgeAndSexProperty property : population.getPopulationByAgeAndSex()) {
            if (property.isSetObject())
                populationByAgeAndSexImporter.doImport(property.getObject(), objectId);
        }
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
