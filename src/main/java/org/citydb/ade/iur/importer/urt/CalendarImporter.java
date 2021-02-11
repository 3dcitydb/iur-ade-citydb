package org.citydb.ade.iur.importer.urt;

import org.citydb.ade.importer.CityGMLImportHelper;
import org.citydb.ade.importer.ForeignKeys;
import org.citydb.ade.iur.importer.ImportManager;
import org.citydb.ade.iur.schema.ADETable;
import org.citydb.citygml.importer.CityGMLImportException;
import org.citydb.database.schema.mapping.AbstractObjectType;
import org.citygml4j.ade.iur.model.urt.Calendar;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class CalendarImporter implements PublicTransitModuleImporter {
    private final CityGMLImportHelper helper;
    private final PreparedStatement ps;
    private final PublicTransitImporter publicTransitImporter;

    private int batchCounter;

    public CalendarImporter(Connection connection, CityGMLImportHelper helper, ImportManager manager) throws CityGMLImportException, SQLException {
        this.helper = helper;

        ps = connection.prepareStatement("insert into " +
                helper.getTableNameWithSchema(manager.getSchemaMapper().getTableName(ADETable.CALENDAR)) + " " +
                "(id, monday, tuesday, wednesday, thursday, friday, saturday, sunday, startdate, enddate) " +
                "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

        publicTransitImporter = manager.getImporter(PublicTransitImporter.class);
    }

    public void doImport(Calendar calendar, long objectId, AbstractObjectType<?> objectType, ForeignKeys foreignKeys) throws CityGMLImportException, SQLException {
        publicTransitImporter.doImport(calendar, objectId, objectType, foreignKeys);
        ps.setLong(1, objectId);

        ps.setInt(2, calendar.isMonday() ? 1 : 0);
        ps.setInt(3, calendar.isTuesday() ? 1 : 0);
        ps.setInt(4, calendar.isWednesday() ? 1 : 0);
        ps.setInt(5, calendar.isThursday() ? 1 : 0);
        ps.setInt(6, calendar.isFriday() ? 1 : 0);
        ps.setInt(7, calendar.isSaturday() ? 1 : 0);
        ps.setInt(8, calendar.isSunday() ? 1 : 0);

        if (calendar.getStartDate() != null)
            ps.setDate(9, Date.valueOf(calendar.getStartDate()));
        else
            ps.setNull(9, Types.DATE);

        if (calendar.getEndDate() != null)
            ps.setDate(10, Date.valueOf(calendar.getEndDate()));
        else
            ps.setNull(10, Types.DATE);

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
