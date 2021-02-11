package org.citydb.ade.iur.importer.urt;

import org.citydb.ade.importer.CityGMLImportHelper;
import org.citydb.ade.iur.importer.ImportManager;
import org.citydb.ade.iur.schema.ADETable;
import org.citydb.ade.iur.schema.SchemaMapper;
import org.citydb.citygml.importer.CityGMLImportException;
import org.citygml4j.ade.iur.model.urt.Frequency;
import org.citygml4j.ade.iur.model.urt.Trip;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class FrequencyImporter implements PublicTransitModuleImporter {
    private final CityGMLImportHelper helper;
    private final SchemaMapper schemaMapper;
    private final PreparedStatement ps;
    private final PublicTransitDataTypeImporter dataTypeImporter;

    private int batchCounter;

    public FrequencyImporter(Connection connection, CityGMLImportHelper helper, ImportManager manager) throws CityGMLImportException, SQLException {
        this.helper = helper;
        this.schemaMapper = manager.getSchemaMapper();

        ps = connection.prepareStatement("insert into " +
                helper.getTableNameWithSchema(manager.getSchemaMapper().getTableName(ADETable.FREQUENCY)) + " " +
                "(id, endtime, exacttimes, exacttimes_codespace, headwaysecs, starttime, trip_id) " +
                "values (?, ?, ?, ?, ?, ?, ?)");

        dataTypeImporter = manager.getImporter(PublicTransitDataTypeImporter.class);
    }

    public void doImport(Frequency frequency, long cityObjectGroupId) throws CityGMLImportException, SQLException {
        long objectId = dataTypeImporter.doImport(frequency, cityObjectGroupId);
        ps.setLong(1, objectId);

        if (frequency.getEndTime() != null)
            ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.of(LocalDate.ofEpochDay(0), frequency.getEndTime())));
        else
            ps.setNull(2, Types.TIMESTAMP_WITH_TIMEZONE);

        if (frequency.getExactTimes() != null && frequency.getExactTimes().isSetValue()) {
            ps.setString(3, frequency.getExactTimes().getValue());
            ps.setString(4, frequency.getExactTimes().getCodeSpace());
        } else {
            ps.setNull(3, Types.VARCHAR);
            ps.setNull(4, Types.VARCHAR);
        }

        if (frequency.getHeadwaySecs() != null)
            ps.setInt(5, frequency.getHeadwaySecs());
        else
            ps.setNull(5, Types.INTEGER);

        if (frequency.getStartTime() != null)
            ps.setTimestamp(6, Timestamp.valueOf(LocalDateTime.of(LocalDate.ofEpochDay(0), frequency.getStartTime())));
        else
            ps.setNull(6, Types.TIMESTAMP_WITH_TIMEZONE);

        long tripId = 0;
        if (frequency.getTrip() != null) {
            Trip trip = frequency.getTrip().getObject();
            if (trip != null) {
                tripId = helper.importObject(trip);
                frequency.getTrip().unsetObject();
            } else {
                String href = frequency.getTrip().getHref();
                if (href != null && !href.isEmpty()) {
                    helper.propagateObjectXlink(
                            schemaMapper.getTableName(ADETable.FREQUENCY),
                            objectId, href, "trip_id");
                }
            }
        }

        if (tripId != 0)
            ps.setLong(7, tripId);
        else
            ps.setNull(7, Types.NULL);

        ps.addBatch();
        if (++batchCounter == helper.getDatabaseAdapter().getMaxBatchSize())
            helper.executeBatch(schemaMapper.getTableName(ADETable.FREQUENCY));
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
