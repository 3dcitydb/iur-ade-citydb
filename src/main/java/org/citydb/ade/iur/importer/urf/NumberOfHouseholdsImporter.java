package org.citydb.ade.iur.importer.urf;

import org.citydb.ade.importer.ADEImporter;
import org.citydb.ade.importer.CityGMLImportHelper;
import org.citydb.ade.iur.importer.ImportManager;
import org.citydb.ade.iur.schema.ADESequence;
import org.citydb.ade.iur.schema.ADETable;
import org.citydb.ade.iur.schema.SchemaMapper;
import org.citydb.citygml.importer.CityGMLImportException;
import org.citygml4j.ade.iur.model.urf.NumberOfHouseholds;

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
                helper.getTableNameWithSchema(schemaMapper.getTableName(ADETable.NUMBEROFHOUSEHOLDS_1)) + " " +
                "(id, censusblock_numberofhouse_id, censusblock_numberofhou_id_1, class, class_codespace, number_) " +
                "values (?, ?, ?, ?, ?, ?)");
    }

    public void doImport(NumberOfHouseholds numberOfHouseholds, Type type, long parentId) throws CityGMLImportException, SQLException {
        long objectId = helper.getNextSequenceValue(schemaMapper.getSequenceName(ADESequence.NUMBEROFHOUSEHOL_SEQ_1));
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
            helper.executeBatch(schemaMapper.getTableName(ADETable.NUMBEROFHOUSEHOLDS_1));
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
