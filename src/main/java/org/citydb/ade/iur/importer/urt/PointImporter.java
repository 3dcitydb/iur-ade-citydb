package org.citydb.ade.iur.importer.urt;

import org.citydb.ade.importer.CityGMLImportHelper;
import org.citydb.ade.iur.importer.ImportManager;
import org.citydb.ade.iur.schema.ADESequence;
import org.citydb.ade.iur.schema.ADETable;
import org.citydb.ade.iur.schema.SchemaMapper;
import org.citydb.citygml.importer.CityGMLImportException;
import org.citydb.citygml.importer.database.content.GeometryConverter;
import org.citydb.config.geometry.GeometryObject;
import org.citygml4j.ade.iur.model.urt.Point;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class PointImporter implements PublicTransitModuleImporter {
    private final Connection connection;
    private final CityGMLImportHelper helper;
    private final SchemaMapper schemaMapper;
    private final PreparedStatement ps;
    private final GeometryConverter geometryConverter;

    private int batchCounter;

    public PointImporter(Connection connection, CityGMLImportHelper helper, ImportManager manager) throws CityGMLImportException, SQLException {
        this.connection = connection;
        this.helper = helper;
        this.schemaMapper = manager.getSchemaMapper();

        ps = connection.prepareStatement("insert into " +
                helper.getTableNameWithSchema(manager.getSchemaMapper().getTableName(ADETable.POINT)) + " " +
                "(id, publictransit_point_id, latitude, longitude, point, pointdistancetraveled, pointsequence) " +
                "values (?, ?, ?, ?, ?, ?, ?)");

        geometryConverter = helper.getGeometryConverter();
    }

    public void doImport(Point point, long parentId) throws CityGMLImportException, SQLException {
        long objectId = helper.getNextSequenceValue(schemaMapper.getSequenceName(ADESequence.POINT_SEQ));
        ps.setLong(1, objectId);
        ps.setLong(2, parentId);

        if (point.getLatitude() != null)
            ps.setDouble(3, point.getLatitude());
        else
            ps.setNull(3, Types.DOUBLE);

        if (point.getLongitude() != null)
            ps.setDouble(4, point.getLongitude());
        else
            ps.setNull(4, Types.DOUBLE);

        GeometryObject geometry = geometryConverter.getPoint(point.getPoint());
        if (geometry != null) {
            ps.setObject(5, helper.getDatabaseAdapter().getGeometryConverter().getDatabaseObject(geometry, connection));
            point.setPoint(null);
        } else
            ps.setNull(5, helper.getDatabaseAdapter().getGeometryConverter().getNullGeometryType(),
                    helper.getDatabaseAdapter().getGeometryConverter().getNullGeometryTypeName());

        if (point.getPointDistanceTraveled() != null)
            ps.setDouble(6, point.getPointDistanceTraveled());
        else
            ps.setNull(6, Types.DOUBLE);

        if (point.getPointSequence() != null)
            ps.setInt(7, point.getPointSequence());
        else
            ps.setNull(7, Types.INTEGER);

        ps.addBatch();
        if (++batchCounter == helper.getDatabaseAdapter().getMaxBatchSize())
            helper.executeBatch(schemaMapper.getTableName(ADETable.POINT));
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
