package org.citydb.ade.iur.importer.urg;

import org.citydb.ade.importer.ADEImporter;
import org.citydb.ade.importer.CityGMLImportHelper;
import org.citydb.citygml.importer.CityGMLImportException;
import org.citydb.ade.iur.importer.ImportManager;
import org.citydb.ade.iur.schema.ADESequence;
import org.citydb.ade.iur.schema.ADETable;
import org.citydb.ade.iur.schema.SchemaMapper;
import org.citygml4j.ade.iur.model.urg.LandPricePerLandUse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class LandPricePerLandUseImporter implements ADEImporter {
    private final CityGMLImportHelper helper;
    private final SchemaMapper schemaMapper;
    private final PreparedStatement ps;

    private int batchCounter;

    public LandPricePerLandUseImporter(Connection connection, CityGMLImportHelper helper, ImportManager manager) throws CityGMLImportException, SQLException {
        this.helper = helper;
        this.schemaMapper = manager.getSchemaMapper();

        ps = connection.prepareStatement("insert into " +
                helper.getTableNameWithSchema(schemaMapper.getTableName(ADETable.LANDPRICEPERLANDUSE)) + " " +
                "(id, statisticalgrid_landprice_id, currencyunit, currencyunit_codespace, landprice, landuse, landuse_codespace) " +
                "values (?, ?, ?, ?, ?, ?, ?)");
    }

    public void doImport(LandPricePerLandUse landPricePerLandUse, long parentId) throws CityGMLImportException, SQLException {
        long objectId = helper.getNextSequenceValue(schemaMapper.getSequenceName(ADESequence.LANDPRICEPERLANDUS_SEQ));
        ps.setLong(1, objectId);
        ps.setLong(2, parentId);

        if (landPricePerLandUse.getCurrencyUnit() != null && landPricePerLandUse.getCurrencyUnit().isSetValue()) {
            ps.setString(3, landPricePerLandUse.getCurrencyUnit().getValue());
            ps.setString(4, landPricePerLandUse.getCurrencyUnit().getCodeSpace());
        } else {
            ps.setNull(3, Types.VARCHAR);
            ps.setNull(4, Types.VARCHAR);
        }

        if (landPricePerLandUse.getLandPrice() != null)
            ps.setInt(5, landPricePerLandUse.getLandPrice());
        else
            ps.setNull(5, Types.INTEGER);

        if (landPricePerLandUse.getLandUse() != null && landPricePerLandUse.getLandUse().isSetValue()) {
            ps.setString(6, landPricePerLandUse.getLandUse().getValue());
            ps.setString(7, landPricePerLandUse.getLandUse().getCodeSpace());
        } else {
            ps.setNull(6, Types.VARCHAR);
            ps.setNull(7, Types.VARCHAR);
        }

        ps.addBatch();
        if (++batchCounter == helper.getDatabaseAdapter().getMaxBatchSize())
            helper.executeBatch(schemaMapper.getTableName(ADETable.LANDPRICEPERLANDUSE));
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
