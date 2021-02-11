package org.citydb.ade.iur.importer.urt;

import org.citydb.ade.importer.CityGMLImportHelper;
import org.citydb.ade.importer.ForeignKeys;
import org.citydb.ade.iur.importer.ImportManager;
import org.citydb.ade.iur.schema.ADETable;
import org.citydb.ade.iur.schema.SchemaMapper;
import org.citydb.citygml.importer.CityGMLImportException;
import org.citydb.database.schema.mapping.AbstractObjectType;
import org.citygml4j.ade.iur.model.urt.Level;
import org.citygml4j.ade.iur.model.urt.PointProperty;
import org.citygml4j.ade.iur.model.urt.PublicTransit;
import org.citygml4j.ade.iur.model.urt.Shape;
import org.citygml4j.model.citygml.core.AbstractCityObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class PublicTransitImporter implements PublicTransitModuleImporter {
    private final CityGMLImportHelper helper;
    private final SchemaMapper schemaMapper;
    private final PreparedStatement ps;
    private final PointImporter pointImporter;

    private int batchCounter;

    public PublicTransitImporter(Connection connection, CityGMLImportHelper helper, ImportManager manager) throws CityGMLImportException, SQLException {
        this.helper = helper;
        this.schemaMapper = manager.getSchemaMapper();

        ps = connection.prepareStatement("insert into " +
                helper.getTableNameWithSchema(manager.getSchemaMapper().getTableName(ADETable.PUBLICTRANSIT)) + " " +
                "(id, objectclass_id, orgid, target_id, index_) " +
                "values (?, ?, ?, ?, ?)");

        pointImporter = manager.getImporter(PointImporter.class);
    }

    public void doImport(PublicTransit publicTransit, long objectId, AbstractObjectType<?> objectType, ForeignKeys foreignKeys) throws CityGMLImportException, SQLException {
        ps.setLong(1, objectId);
        ps.setInt(2, objectType.getObjectClassId());

        ps.setString(3,publicTransit.getOrgId());

        long cityObjectId = 0;
        if (publicTransit.getTarget() != null) {
            AbstractCityObject cityObject = publicTransit.getTarget().getObject();
            if (cityObject != null) {
                cityObjectId = helper.importObject(cityObject);
                publicTransit.getTarget().unsetObject();
            } else {
                String href = publicTransit.getTarget().getHref();
                if (href != null && !href.isEmpty()) {
                    helper.propagateObjectXlink(
                            schemaMapper.getTableName(ADETable.PUBLICTRANSIT),
                            objectId, href, "target_id");
                }
            }
        }

        if (cityObjectId != 0)
            ps.setLong(4, cityObjectId);
        else
            ps.setNull(4, Types.NULL);

        if (publicTransit instanceof Level)
            ps.setDouble(5, ((Level) publicTransit).getIndex());
        else
            ps.setNull(5, Types.DOUBLE);

        ps.addBatch();
        if (++batchCounter == helper.getDatabaseAdapter().getMaxBatchSize())
            helper.executeBatch(objectType);

        if (publicTransit instanceof Shape) {
            Shape shape = (Shape) publicTransit;
            for (PointProperty property : shape.getPoints()) {
                if (property.getObject() != null) {
                    pointImporter.doImport(property.getObject(), objectId);
                    property.unsetObject();
                }
            }
        }
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
