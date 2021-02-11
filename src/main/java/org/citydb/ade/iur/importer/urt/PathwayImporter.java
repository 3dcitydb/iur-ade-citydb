package org.citydb.ade.iur.importer.urt;

import org.citydb.ade.importer.CityGMLImportHelper;
import org.citydb.ade.importer.ForeignKeys;
import org.citydb.ade.iur.importer.ImportManager;
import org.citydb.ade.iur.schema.ADETable;
import org.citydb.ade.iur.schema.SchemaMapper;
import org.citydb.citygml.importer.CityGMLImportException;
import org.citydb.database.schema.mapping.AbstractObjectType;
import org.citygml4j.ade.iur.model.urt.Pathway;
import org.citygml4j.ade.iur.model.urt.Stop;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class PathwayImporter implements PublicTransitModuleImporter {
    private final CityGMLImportHelper helper;
    private final SchemaMapper schemaMapper;
    private final PreparedStatement ps;
    private final PublicTransitImporter publicTransitImporter;

    private int batchCounter;

    public PathwayImporter(Connection connection, CityGMLImportHelper helper, ImportManager manager) throws CityGMLImportException, SQLException {
        this.helper = helper;
        this.schemaMapper = manager.getSchemaMapper();

        ps = connection.prepareStatement("insert into " +
                helper.getTableNameWithSchema(manager.getSchemaMapper().getTableName(ADETable.PATHWAY)) + " " +
                "(id, isbidirectional, isbidirectional_codespace, length, length_uom, maxslope, minwidth, " +
                "mode_, mode_codespace, reversedsignpostedas, signpostedas, staircount, traversaltime, " +
                "from_id, to_id) " +
                "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

        publicTransitImporter = manager.getImporter(PublicTransitImporter.class);
    }

    public void doImport(Pathway pathway, long objectId, AbstractObjectType<?> objectType, ForeignKeys foreignKeys) throws CityGMLImportException, SQLException {
        publicTransitImporter.doImport(pathway, objectId, objectType, foreignKeys);
        ps.setLong(1, objectId);

        if (pathway.getIsBidirectional() != null && pathway.getIsBidirectional().isSetValue()) {
            ps.setString(2, pathway.getIsBidirectional().getValue());
            ps.setString(3, pathway.getIsBidirectional().getCodeSpace());
        } else {
            ps.setNull(2, Types.VARCHAR);
            ps.setNull(3, Types.VARCHAR);
        }

        if (pathway.getLength() != null && pathway.getLength().isSetValue()) {
            ps.setDouble(4, pathway.getLength().getValue());
            ps.setString(5, pathway.getLength().getUom());
        } else {
            ps.setNull(4, Types.DOUBLE);
            ps.setNull(5, Types.VARCHAR);
        }

        if (pathway.getMaxSlope() != null)
            ps.setDouble(6, pathway.getMaxSlope());
        else
            ps.setNull(6, Types.DOUBLE);

        if (pathway.getMinWidth() != null)
            ps.setDouble(7, pathway.getMinWidth());
        else
            ps.setNull(7, Types.DOUBLE);

        if (pathway.getMode() != null && pathway.getMode().isSetValue()) {
            ps.setString(8, pathway.getMode().getValue());
            ps.setString(9, pathway.getMode().getCodeSpace());
        } else {
            ps.setNull(8, Types.VARCHAR);
            ps.setNull(9, Types.VARCHAR);
        }

        ps.setString(10, pathway.getReversedSignpostedAs());
        ps.setString(11, pathway.getSignpostedAs());

        if (pathway.getStairCount() != null)
            ps.setInt(12, pathway.getStairCount());
        else
            ps.setNull(12, Types.INTEGER);

        if (pathway.getTraversalTime() != null)
            ps.setInt(13, pathway.getTraversalTime());
        else
            ps.setNull(13, Types.INTEGER);

        long fromId = 0;
        if (pathway.getFrom() != null) {
            Stop object = pathway.getFrom().getObject();
            if (object != null) {
                fromId = helper.importObject(object);
                pathway.getFrom().unsetObject();
            } else {
                String href = pathway.getFrom().getHref();
                if (href != null && !href.isEmpty()) {
                    helper.propagateObjectXlink(
                            schemaMapper.getTableName(ADETable.PATHWAY),
                            objectId, href, "from_id");
                }
            }
        }

        if (fromId != 0)
            ps.setLong(14, fromId);
        else
            ps.setNull(14, Types.NULL);

        long toId = 0;
        if (pathway.getTo() != null) {
            Stop object = pathway.getTo().getObject();
            if (object != null) {
                toId = helper.importObject(object);
                pathway.getTo().unsetObject();
            } else {
                String href = pathway.getTo().getHref();
                if (href != null && !href.isEmpty()) {
                    helper.propagateObjectXlink(
                            schemaMapper.getTableName(ADETable.PATHWAY),
                            objectId, href, "to_id");
                }
            }
        }

        if (toId != 0)
            ps.setLong(15, toId);
        else
            ps.setNull(15, Types.NULL);

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
