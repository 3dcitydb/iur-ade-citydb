package org.citydb.ade.iur.importer.uro;

import org.citydb.ade.importer.ADEImporter;
import org.citydb.ade.importer.ADEPropertyCollection;
import org.citydb.ade.importer.CityGMLImportHelper;
import org.citydb.citygml.importer.CityGMLImportException;
import org.citydb.database.schema.mapping.FeatureType;
import org.citygml4j.model.citygml.cityobjectgroup.CityObjectGroup;
import org.citydb.ade.iur.importer.ImportManager;
import org.citydb.ade.iur.schema.ADETable;
import org.citydb.ade.iur.schema.SchemaMapper;
import org.citygml4j.ade.iur.model.uro.FiscalYearOfPublicationProperty;
import org.citygml4j.ade.iur.model.uro.LanguageProperty;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;

public class CityObjectGroupPropertiesImporter implements ADEImporter {
    private final CityGMLImportHelper helper;
    private final SchemaMapper schemaMapper;
    private final PreparedStatement ps;

    private int batchCounter;

    public CityObjectGroupPropertiesImporter(Connection connection, CityGMLImportHelper helper, ImportManager manager) throws CityGMLImportException, SQLException {
        this.helper = helper;
        this.schemaMapper = manager.getSchemaMapper();

        ps = connection.prepareStatement("insert into " +
                helper.getTableNameWithSchema(schemaMapper.getTableName(ADETable.CITYOBJECTGROUP)) + " " +
                "(id, fiscalyearofpublication, language, language_codespace) " +
                "values (?, ?, ?, ?)");
    }

    public void doImport(ADEPropertyCollection properties, CityObjectGroup parent, long parentId, FeatureType parentType) throws CityGMLImportException, SQLException {
        ps.setLong(1, parentId);

        FiscalYearOfPublicationProperty fiscalYearOfPublication = properties.getFirst(FiscalYearOfPublicationProperty.class);
        if (fiscalYearOfPublication != null && fiscalYearOfPublication.isSetValue())
            ps.setDate(2, Date.valueOf(LocalDate.of(fiscalYearOfPublication.getValue().getValue(), 1, 1)));
        else
            ps.setNull(2, Types.DATE);

        LanguageProperty language = properties.getFirst(LanguageProperty.class);
        if (language != null && language.isSetValue()) {
            ps.setString(3, language.getValue().getValue());
            ps.setString(4, language.getValue().getCodeSpace());
        } else {
            ps.setNull(3, Types.VARCHAR);
            ps.setNull(4, Types.VARCHAR);
        }

        ps.addBatch();
        if (++batchCounter == helper.getDatabaseAdapter().getMaxBatchSize())
            helper.executeBatch(schemaMapper.getTableName(ADETable.CITYOBJECTGROUP));
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
