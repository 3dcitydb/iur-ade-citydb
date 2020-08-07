package org.citydb.ade.iur.importer.urg;

import org.citydb.ade.importer.ADEImporter;
import org.citydb.ade.importer.CityGMLImportHelper;
import org.citydb.citygml.importer.CityGMLImportException;
import org.citydb.ade.iur.importer.ImportManager;
import org.citydb.ade.iur.schema.ADESequence;
import org.citydb.ade.iur.schema.ADETable;
import org.citydb.ade.iur.schema.SchemaMapper;
import org.citygml4j.ade.iur.model.urg.NumberOfAnnualDiversions;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;

public class NumberOfAnnualDiversionsImporter implements ADEImporter {
    private final CityGMLImportHelper helper;
    private final SchemaMapper schemaMapper;
    private final PreparedStatement ps;

    private int batchCounter;

    public NumberOfAnnualDiversionsImporter(Connection connection, CityGMLImportHelper helper, ImportManager manager) throws CityGMLImportException, SQLException {
        this.helper = helper;
        this.schemaMapper = manager.getSchemaMapper();

        ps = connection.prepareStatement("insert into " +
                helper.getTableNameWithSchema(schemaMapper.getTableName(ADETable.NUMBEROFANNUALDIVERSIO)) + " " +
                "(id, statisticalg_numberofannu_id, area, area_uom, count, year) " +
                "values (?, ?, ?, ?, ?)");
    }

    public void doImport(NumberOfAnnualDiversions numberOfAnnualDiversions, long parentId) throws CityGMLImportException, SQLException {
        long objectId = helper.getNextSequenceValue(schemaMapper.getSequenceName(ADESequence.NUMBEROFANNUALDIVE_SEQ));
        ps.setLong(1, objectId);
        ps.setLong(2, parentId);
        
        if (numberOfAnnualDiversions.getArea() != null && numberOfAnnualDiversions.getArea().isSetValue()) {
            ps.setDouble(3, numberOfAnnualDiversions.getArea().getValue());
            ps.setString(4, numberOfAnnualDiversions.getArea().getUom());
        } else {
            ps.setNull(3, Types.DOUBLE);
            ps.setNull(4, Types.VARCHAR);
        }

        if (numberOfAnnualDiversions.getCount() != null)
            ps.setInt(5, numberOfAnnualDiversions.getCount());
        else
            ps.setNull(5, Types.INTEGER);

        if (numberOfAnnualDiversions.getYear() != null)
            ps.setDate(6, Date.valueOf(LocalDate.of(numberOfAnnualDiversions.getYear().getValue(), 1, 1)));
        else
            ps.setNull(6, Types.DATE);

        ps.addBatch();
        if (++batchCounter == helper.getDatabaseAdapter().getMaxBatchSize())
            helper.executeBatch(schemaMapper.getTableName(ADETable.NUMBEROFANNUALDIVERSIO));
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
