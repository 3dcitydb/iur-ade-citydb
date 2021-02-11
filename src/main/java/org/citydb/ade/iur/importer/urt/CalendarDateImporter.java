package org.citydb.ade.iur.importer.urt;

import org.citydb.ade.importer.CityGMLImportHelper;
import org.citydb.ade.importer.ForeignKeys;
import org.citydb.ade.iur.importer.ImportManager;
import org.citydb.ade.iur.schema.ADETable;
import org.citydb.ade.iur.schema.SchemaMapper;
import org.citydb.citygml.importer.CityGMLImportException;
import org.citydb.database.schema.mapping.AbstractObjectType;
import org.citygml4j.ade.iur.model.urt.Calendar;
import org.citygml4j.ade.iur.model.urt.CalendarDate;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class CalendarDateImporter implements PublicTransitModuleImporter {
    private final CityGMLImportHelper helper;
    private final SchemaMapper schemaMapper;
    private final PreparedStatement ps;
    private final PublicTransitImporter publicTransitImporter;

    private int batchCounter;

    public CalendarDateImporter(Connection connection, CityGMLImportHelper helper, ImportManager manager) throws CityGMLImportException, SQLException {
        this.helper = helper;
        this.schemaMapper = manager.getSchemaMapper();

        ps = connection.prepareStatement("insert into " +
                helper.getTableNameWithSchema(manager.getSchemaMapper().getTableName(ADETable.CALENDARDATE)) + " " +
                "(id, date_, exceptiontype, exceptiontype_codespace, calendar_id) " +
                "values (?, ?, ?, ?, ?)");

        publicTransitImporter = manager.getImporter(PublicTransitImporter.class);
    }

    public void doImport(CalendarDate calendarDate, long objectId, AbstractObjectType<?> objectType, ForeignKeys foreignKeys) throws CityGMLImportException, SQLException {
        publicTransitImporter.doImport(calendarDate, objectId, objectType, foreignKeys);
        ps.setLong(1, objectId);

        if (calendarDate.getDate() != null)
            ps.setDate(2, Date.valueOf(calendarDate.getDate()));
        else
            ps.setNull(2, Types.DATE);

        if (calendarDate.getExceptionType() != null && calendarDate.getExceptionType().isSetValue()) {
            ps.setString(3, calendarDate.getExceptionType().getValue());
            ps.setString(4, calendarDate.getExceptionType().getCodeSpace());
        } else {
            ps.setNull(3, Types.VARCHAR);
            ps.setNull(4, Types.VARCHAR);
        }

        long calendarId = 0;
        if (calendarDate.getCalendar() != null) {
            Calendar calendar = calendarDate.getCalendar().getObject();
            if (calendar != null) {
                calendarId = helper.importObject(calendar);
                calendarDate.getCalendar().unsetObject();
            } else {
                String href = calendarDate.getCalendar().getHref();
                if (href != null && !href.isEmpty()) {
                    helper.propagateObjectXlink(
                            schemaMapper.getTableName(ADETable.CALENDARDATE),
                            objectId, href, "calendar_id");
                }
            }
        }

        if (calendarId != 0)
            ps.setLong(5, calendarId);
        else
            ps.setNull(5, Types.NULL);

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
