package org.citydb.ade.iur.importer.urg;

import org.citydb.ade.importer.ADEImporter;
import org.citydb.ade.importer.CityGMLImportHelper;
import org.citydb.ade.importer.ForeignKeys;
import org.citydb.citygml.importer.CityGMLImportException;
import org.citydb.database.schema.mapping.AbstractObjectType;
import org.citydb.ade.iur.importer.ImportManager;
import org.citydb.ade.iur.schema.ADETable;
import org.citygml4j.ade.iur.model.urg.Households;
import org.citygml4j.ade.iur.model.urg.NumberOfHouseholdsProperty;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class HouseholdsImporter implements ADEImporter {
    private final CityGMLImportHelper helper;
    private final PreparedStatement ps;

    private final NumberOfHouseholdsImporter numberOfHouseholdsImporter;
    private final StatisticalGridImporter statisticalGridImporter;

    private int batchCounter;

    public HouseholdsImporter(Connection connection, CityGMLImportHelper helper, ImportManager manager) throws CityGMLImportException, SQLException {
        this.helper = helper;

        ps = connection.prepareStatement("insert into " +
                helper.getTableNameWithSchema(manager.getSchemaMapper().getTableName(ADETable.HOUSEHOLDS)) + " " +
                "(id, numberofmainhousehold, numberofordinaryhousehold) " +
                "values (?, ?, ?)");

        numberOfHouseholdsImporter = manager.getImporter(NumberOfHouseholdsImporter.class);
        statisticalGridImporter = manager.getImporter(StatisticalGridImporter.class);
    }

    public void doImport(Households households, long objectId, AbstractObjectType<?> objectType, ForeignKeys foreignKeys) throws CityGMLImportException, SQLException {
        statisticalGridImporter.doImport(households, objectId, objectType, foreignKeys);
        ps.setLong(1, objectId);

        if (households.getNumberOfMainHouseholds() != null)
            ps.setInt(2, households.getNumberOfMainHouseholds());
        else
            ps.setNull(2, Types.INTEGER);

        if (households.getNumberOfOrdinaryHouseholds() != null)
            ps.setInt(3, households.getNumberOfOrdinaryHouseholds());
        else
            ps.setNull(3, Types.INTEGER);

        ps.addBatch();
        if (++batchCounter == helper.getDatabaseAdapter().getMaxBatchSize())
            helper.executeBatch(objectType);

        for (NumberOfHouseholdsProperty property : households.getNumberOfHouseholdsByOwnership()) {
            if (property.isSetObject())
                numberOfHouseholdsImporter.doImport(property.getObject(), NumberOfHouseholdsImporter.Type.BY_OWNERSHIP, objectId);
        }

        for (NumberOfHouseholdsProperty property : households.getNumberOfHouseholdsByStructure()) {
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
