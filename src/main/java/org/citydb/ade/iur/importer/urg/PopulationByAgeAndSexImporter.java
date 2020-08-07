package org.citydb.ade.iur.importer.urg;

import org.citydb.ade.importer.ADEImporter;
import org.citydb.ade.importer.CityGMLImportHelper;
import org.citydb.citygml.importer.CityGMLImportException;
import org.citydb.ade.iur.importer.ImportManager;
import org.citydb.ade.iur.schema.ADESequence;
import org.citydb.ade.iur.schema.ADETable;
import org.citydb.ade.iur.schema.SchemaMapper;
import org.citygml4j.ade.iur.model.urg.PopulationByAgeAndSex;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class PopulationByAgeAndSexImporter implements ADEImporter {
    private final CityGMLImportHelper helper;
    private final SchemaMapper schemaMapper;
    private final PreparedStatement ps;

    private int batchCounter;

    public PopulationByAgeAndSexImporter(Connection connection, CityGMLImportHelper helper, ImportManager manager) throws CityGMLImportException, SQLException {
        this.helper = helper;
        this.schemaMapper = manager.getSchemaMapper();

        ps = connection.prepareStatement("insert into " +
                helper.getTableNameWithSchema(schemaMapper.getTableName(ADETable.POPULATIONBYAGEANDSEX)) + " " +
                "(id, population_populationbyag_id, age, age_codespace, number_, sex, sex_codespace) " +
                "values (?, ?, ?, ?, ?, ?, ?)");
    }

    public void doImport(PopulationByAgeAndSex populationByAgeAndSex, long parentId) throws CityGMLImportException, SQLException {
        long objectId = helper.getNextSequenceValue(schemaMapper.getSequenceName(ADESequence.POPULATIONBYAGEAND_SEQ));
        ps.setLong(1, objectId);
        ps.setLong(2, parentId);

        if (populationByAgeAndSex.getAge() != null && populationByAgeAndSex.getAge().isSetValue()) {
            ps.setString(3, populationByAgeAndSex.getAge().getValue());
            ps.setString(4, populationByAgeAndSex.getAge().getCodeSpace());
        } else {
            ps.setNull(3, Types.VARCHAR);
            ps.setNull(4, Types.VARCHAR);
        }

        if (populationByAgeAndSex.getNumber() != null)
            ps.setInt(5, populationByAgeAndSex.getNumber());
        else
            ps.setNull(5, Types.INTEGER);

        if (populationByAgeAndSex.getSex() != null && populationByAgeAndSex.getSex().isSetValue()) {
            ps.setString(6, populationByAgeAndSex.getSex().getValue());
            ps.setString(7, populationByAgeAndSex.getSex().getCodeSpace());
        } else {
            ps.setNull(6, Types.VARCHAR);
            ps.setNull(7, Types.VARCHAR);
        }

        ps.addBatch();
        if (++batchCounter == helper.getDatabaseAdapter().getMaxBatchSize())
            helper.executeBatch(schemaMapper.getTableName(ADETable.POPULATIONBYAGEANDSEX));
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
