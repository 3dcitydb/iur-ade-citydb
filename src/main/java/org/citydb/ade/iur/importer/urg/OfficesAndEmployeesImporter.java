package org.citydb.ade.iur.importer.urg;

import org.citydb.ade.importer.ADEImporter;
import org.citydb.ade.importer.CityGMLImportHelper;
import org.citydb.ade.importer.ForeignKeys;
import org.citydb.citygml.importer.CityGMLImportException;
import org.citydb.database.schema.mapping.AbstractObjectType;
import org.citydb.ade.iur.importer.ImportManager;
import org.citydb.ade.iur.schema.ADETable;
import org.citygml4j.ade.iur.model.urg.OfficesAndEmployees;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class OfficesAndEmployeesImporter implements ADEImporter {
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
