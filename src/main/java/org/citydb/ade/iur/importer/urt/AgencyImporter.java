package org.citydb.ade.iur.importer.urt;

import org.citydb.ade.importer.CityGMLImportHelper;
import org.citydb.ade.importer.ForeignKeys;
import org.citydb.ade.iur.importer.ImportManager;
import org.citydb.ade.iur.schema.ADETable;
import org.citydb.citygml.importer.CityGMLImportException;
import org.citydb.database.schema.mapping.AbstractObjectType;
import org.citygml4j.ade.iur.model.urt.Agency;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class AgencyImporter implements PublicTransitModuleImporter {
    private final CityGMLImportHelper helper;
    private final PreparedStatement ps;
    private final PublicTransitImporter publicTransitImporter;

    private int batchCounter;

    public AgencyImporter(Connection connection, CityGMLImportHelper helper, ImportManager manager) throws CityGMLImportException, SQLException {
        this.helper = helper;

        ps = connection.prepareStatement("insert into " +
                helper.getTableNameWithSchema(manager.getSchemaMapper().getTableName(ADETable.AGENCY)) + " " +
                "(id, address, email, fareurl, language, language_codespace, name, officialname, phone, " +
                "presidentname, presidentposition, timezone, timezone_codespace, url, zipnumber) " +
                "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

        publicTransitImporter = manager.getImporter(PublicTransitImporter.class);
    }

    public void doImport(Agency agency, long objectId, AbstractObjectType<?> objectType, ForeignKeys foreignKeys) throws CityGMLImportException, SQLException {
        publicTransitImporter.doImport(agency, objectId, objectType, foreignKeys);
        ps.setLong(1, objectId);

        ps.setString(2, agency.getAddress());
        ps.setString(3, agency.getEmail());
        ps.setString(4, agency.getFareUrl());

        if (agency.getLanguage() != null && agency.getLanguage().isSetValue()) {
            ps.setString(5, agency.getLanguage().getValue());
            ps.setString(6, agency.getLanguage().getCodeSpace());
        } else {
            ps.setNull(5, Types.VARCHAR);
            ps.setNull(6, Types.VARCHAR);
        }

        ps.setString(7, agency.getAgencyName());
        ps.setString(8, agency.getOfficialName());
        ps.setString(9, agency.getPhone());
        ps.setString(10, agency.getPresidentName());
        ps.setString(11, agency.getPresidentPosition());

        if (agency.getTimeZone() != null && agency.getTimeZone().isSetValue()) {
            ps.setString(12, agency.getTimeZone().getValue());
            ps.setString(13, agency.getTimeZone().getCodeSpace());
        } else {
            ps.setNull(12, Types.VARCHAR);
            ps.setNull(13, Types.VARCHAR);
        }

        ps.setString(14, agency.getUrl());
        ps.setString(15, agency.getZipNumber());

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
