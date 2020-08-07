package org.citydb.ade.iur.importer.urf;

import org.citydb.ade.importer.ADEImporter;
import org.citydb.ade.importer.CityGMLImportHelper;
import org.citydb.ade.importer.ForeignKeys;
import org.citydb.citygml.importer.CityGMLImportException;
import org.citydb.database.schema.mapping.AbstractObjectType;
import org.citydb.ade.iur.importer.ImportManager;
import org.citydb.ade.iur.schema.ADETable;
import org.citygml4j.ade.iur.model.urf.CensusBlock;
import org.citygml4j.ade.iur.model.urf.NumberOfHouseholdsProperty;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class CensusBlockImporter implements ADEImporter {
    private final CityGMLImportHelper helper;
    private final PreparedStatement ps;

    private final NumberOfHouseholdsImporter numberOfHouseholdsImporter;
    private final UrbanFunctionImporter urbanFunctionImporter;

    private int batchCounter;

    public CensusBlockImporter(Connection connection, CityGMLImportHelper helper, ImportManager manager) throws CityGMLImportException, SQLException {
        this.helper = helper;

        ps = connection.prepareStatement("insert into " +
                helper.getTableNameWithSchema(manager.getSchemaMapper().getTableName(ADETable.CENSUSBLOCK)) + " " +
                "(id, daytimepopulation, daytimepopulationdensity, numberofmainhouseholds, numberofordinaryhouseholds) " +
                "values (?, ?, ?, ?, ?)");

        numberOfHouseholdsImporter = manager.getImporter(NumberOfHouseholdsImporter.class);
        urbanFunctionImporter = manager.getImporter(UrbanFunctionImporter.class);
    }

    public void doImport(CensusBlock censusBlock, long objectId, AbstractObjectType<?> objectType, ForeignKeys foreignKeys) throws CityGMLImportException, SQLException {
        urbanFunctionImporter.doImport(censusBlock, objectId, objectType, foreignKeys);
        ps.setLong(1, objectId);

        if (censusBlock.getDaytimePopulation() != null)
            ps.setInt(2, censusBlock.getDaytimePopulation());
        else
            ps.setNull(2, Types.INTEGER);

        if (censusBlock.getDaytimePopulationDensity() != null)
            ps.setDouble(3, censusBlock.getDaytimePopulationDensity());
        else
            ps.setNull(3, Types.DOUBLE);

        if (censusBlock.getNumberOfMainHouseholds() != null)
            ps.setInt(4, censusBlock.getNumberOfMainHouseholds());
        else
            ps.setNull(4, Types.INTEGER);

        if (censusBlock.getNumberOfOrdinaryHouseholds() != null)
            ps.setInt(5, censusBlock.getNumberOfOrdinaryHouseholds());
        else
            ps.setNull(5, Types.INTEGER);

        ps.addBatch();
        if (++batchCounter == helper.getDatabaseAdapter().getMaxBatchSize())
            helper.executeBatch(objectType);

        for (NumberOfHouseholdsProperty property : censusBlock.getNumberOfHouseholdsByOwnership()) {
            if (property.isSetObject())
                numberOfHouseholdsImporter.doImport(property.getObject(), NumberOfHouseholdsImporter.Type.BY_OWNERSHIP, objectId);
        }

        for (NumberOfHouseholdsProperty property : censusBlock.getNumberOfHouseholdsByStructure()) {
            if (property.isSetObject())
                numberOfHouseholdsImporter.doImport(property.getObject(), NumberOfHouseholdsImporter.Type.BY_STRUCTURE, objectId);
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
