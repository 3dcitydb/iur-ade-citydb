package org.citydb.ade.iur.importer.urt;

import org.citydb.ade.importer.CityGMLImportHelper;
import org.citydb.ade.iur.importer.ImportManager;
import org.citydb.ade.iur.schema.ADETable;
import org.citydb.ade.iur.schema.SchemaMapper;
import org.citydb.citygml.importer.CityGMLImportException;
import org.citygml4j.ade.iur.model.urt.FeedInfo;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class FeedInfoImporter implements PublicTransitModuleImporter {
    private final CityGMLImportHelper helper;
    private final SchemaMapper schemaMapper;
    private final PreparedStatement ps;
    private final PublicTransitDataTypeImporter dataTypeImporter;

    private int batchCounter;

    public FeedInfoImporter(Connection connection, CityGMLImportHelper helper, ImportManager manager) throws CityGMLImportException, SQLException {
        this.helper = helper;
        this.schemaMapper = manager.getSchemaMapper();

        ps = connection.prepareStatement("insert into " +
                helper.getTableNameWithSchema(manager.getSchemaMapper().getTableName(ADETable.FEEDINFO)) + " " +
                "(id, contactemail, contacturl, defaultlanguage, defaultlanguage_codespace, detailedinfo, " +
                "enddate, language, language_codespace, publishername, publisherurl, startdate, version) " +
                "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

        dataTypeImporter = manager.getImporter(PublicTransitDataTypeImporter.class);
    }

    public void doImport(FeedInfo feedInfo, long cityObjectGroupId) throws CityGMLImportException, SQLException {
        long objectId = dataTypeImporter.doImport(feedInfo, cityObjectGroupId);
        ps.setLong(1, objectId);

        ps.setString(2, feedInfo.getContactEmail());
        ps.setString(3, feedInfo.getContactURL());

        if (feedInfo.getDefaultLanguage() != null && feedInfo.getDefaultLanguage().isSetValue()) {
            ps.setString(4, feedInfo.getDefaultLanguage().getValue());
            ps.setString(5, feedInfo.getDefaultLanguage().getCodeSpace());
        } else {
            ps.setNull(4, Types.VARCHAR);
            ps.setNull(5, Types.VARCHAR);
        }

        ps.setString(6, feedInfo.getDetailedInfo());

        if (feedInfo.getEndDate() != null)
            ps.setDate(7, Date.valueOf(feedInfo.getEndDate()));
        else
            ps.setNull(7, Types.DATE);

        if (feedInfo.getLanguage() != null && feedInfo.getLanguage().isSetValue()) {
            ps.setString(8, feedInfo.getLanguage().getValue());
            ps.setString(9, feedInfo.getLanguage().getCodeSpace());
        } else {
            ps.setNull(8, Types.VARCHAR);
            ps.setNull(9, Types.VARCHAR);
        }

        ps.setString(10, feedInfo.getPublisherName());
        ps.setString(11, feedInfo.getPublisherUrl());

        if (feedInfo.getStartDate() != null)
            ps.setDate(12, Date.valueOf(feedInfo.getStartDate()));
        else
            ps.setNull(12, Types.DATE);

        ps.setString(13, feedInfo.getVersion());

        ps.addBatch();
        if (++batchCounter == helper.getDatabaseAdapter().getMaxBatchSize())
            helper.executeBatch(schemaMapper.getTableName(ADETable.FEEDINFO));
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
