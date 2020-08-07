package org.citydb.ade.iur.importer.urf;

import org.citydb.ade.importer.ADEImporter;
import org.citydb.ade.importer.CityGMLImportHelper;
import org.citydb.citygml.importer.CityGMLImportException;
import org.citydb.ade.iur.importer.ImportManager;
import org.citydb.ade.iur.schema.ADESequence;
import org.citydb.ade.iur.schema.ADETable;
import org.citydb.ade.iur.schema.SchemaMapper;
import org.citygml4j.ade.iur.model.urf.LegalGrounds;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class LegalGroundsImporter implements ADEImporter {
    private final CityGMLImportHelper helper;
    private final SchemaMapper schemaMapper;
    private final PreparedStatement ps;

    private int batchCounter;

    public LegalGroundsImporter(Connection connection, CityGMLImportHelper helper, ImportManager manager) throws CityGMLImportException, SQLException {
        this.helper = helper;
        this.schemaMapper = manager.getSchemaMapper();

        ps = connection.prepareStatement("insert into " +
                helper.getTableNameWithSchema(schemaMapper.getTableName(ADETable.LEGALGROUNDS)) + " " +
                "(id, articlesofregulation, articlesofregulati_codespace, date_, nameofregulation, nameofregulation_codespace) " +
                "values (?, ?, ?, ?, ?, ?)");
    }

    public long doImport(LegalGrounds legalGrounds) throws CityGMLImportException, SQLException {
        long objectId = helper.getNextSequenceValue(schemaMapper.getSequenceName(ADESequence.LEGALGROUNDS_SEQ));
        ps.setLong(1, objectId);

        if (legalGrounds.getArticlesOfRegulation() != null && legalGrounds.getArticlesOfRegulation().isSetValue()) {
            ps.setString(2, legalGrounds.getArticlesOfRegulation().getValue());
            ps.setString(3, legalGrounds.getArticlesOfRegulation().getCodeSpace());
        } else {
            ps.setNull(2, Types.VARCHAR);
            ps.setNull(3, Types.VARCHAR);
        }

        if (legalGrounds.getDate() != null)
            ps.setDate(4, Date.valueOf(legalGrounds.getDate()));
        else
            ps.setNull(4, Types.DATE);

        if (legalGrounds.getNameOfRegulation() != null && legalGrounds.getNameOfRegulation().isSetValue()) {
            ps.setString(5, legalGrounds.getNameOfRegulation().getValue());
            ps.setString(6, legalGrounds.getNameOfRegulation().getCodeSpace());
        } else {
            ps.setNull(5, Types.VARCHAR);
            ps.setNull(6, Types.VARCHAR);
        }

        ps.addBatch();
        if (++batchCounter == helper.getDatabaseAdapter().getMaxBatchSize())
            helper.executeBatch(schemaMapper.getTableName(ADETable.LEGALGROUNDS));

        return objectId;
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
