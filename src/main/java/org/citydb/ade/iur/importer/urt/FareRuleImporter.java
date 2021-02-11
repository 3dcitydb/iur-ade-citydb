package org.citydb.ade.iur.importer.urt;

import org.citydb.ade.importer.CityGMLImportHelper;
import org.citydb.ade.iur.importer.ImportManager;
import org.citydb.ade.iur.schema.ADETable;
import org.citydb.ade.iur.schema.SchemaMapper;
import org.citydb.citygml.importer.CityGMLImportException;
import org.citygml4j.ade.iur.model.urt.FareAttribute;
import org.citygml4j.ade.iur.model.urt.FareRule;
import org.citygml4j.ade.iur.model.urt.Route;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class FareRuleImporter implements PublicTransitModuleImporter {
    private final CityGMLImportHelper helper;
    private final SchemaMapper schemaMapper;
    private final PreparedStatement ps;
    private final PublicTransitDataTypeImporter dataTypeImporter;

    private int batchCounter;

    public FareRuleImporter(Connection connection, CityGMLImportHelper helper, ImportManager manager) throws CityGMLImportException, SQLException {
        this.helper = helper;
        this.schemaMapper = manager.getSchemaMapper();

        ps = connection.prepareStatement("insert into " +
                helper.getTableNameWithSchema(manager.getSchemaMapper().getTableName(ADETable.FARERULE)) + " " +
                "(id, containsid, containsid_codespace, destinationid, destinationid_codespace, originid, " +
                "originid_codespace, fare_id, route_id) " +
                "values (?, ?, ?, ?, ?, ?, ?, ?, ?)");

        dataTypeImporter = manager.getImporter(PublicTransitDataTypeImporter.class);
    }

    public void doImport(FareRule fareRule, long cityObjectGroupId) throws CityGMLImportException, SQLException {
        long objectId = dataTypeImporter.doImport(fareRule, cityObjectGroupId);
        ps.setLong(1, objectId);

        if (fareRule.getContainsId() != null && fareRule.getContainsId().isSetValue()) {
            ps.setString(2, fareRule.getContainsId().getValue());
            ps.setString(3, fareRule.getContainsId().getCodeSpace());
        } else {
            ps.setNull(2, Types.VARCHAR);
            ps.setNull(3, Types.VARCHAR);
        }

        if (fareRule.getDestinationId() != null && fareRule.getDestinationId().isSetValue()) {
            ps.setString(4, fareRule.getDestinationId().getValue());
            ps.setString(5, fareRule.getDestinationId().getCodeSpace());
        } else {
            ps.setNull(4, Types.VARCHAR);
            ps.setNull(5, Types.VARCHAR);
        }

        if (fareRule.getOriginId() != null && fareRule.getOriginId().isSetValue()) {
            ps.setString(6, fareRule.getOriginId().getValue());
            ps.setString(7, fareRule.getOriginId().getCodeSpace());
        } else {
            ps.setNull(6, Types.VARCHAR);
            ps.setNull(7, Types.VARCHAR);
        }

        long fareId = 0;
        if (fareRule.getFare() != null) {
            FareAttribute fareAttribute = fareRule.getFare().getObject();
            if (fareAttribute != null) {
                fareId = helper.importObject(fareAttribute);
                fareRule.getFare().unsetObject();
            } else {
                String href = fareRule.getFare().getHref();
                if (href != null && !href.isEmpty()) {
                    helper.propagateObjectXlink(
                            schemaMapper.getTableName(ADETable.FARERULE),
                            objectId, href, "fare_id");
                }
            }
        }

        if (fareId != 0)
            ps.setLong(8, fareId);
        else
            ps.setNull(8, Types.NULL);

        long routeId = 0;
        if (fareRule.getRoute() != null) {
            Route route = fareRule.getRoute().getObject();
            if (route != null) {
                routeId = helper.importObject(route);
                fareRule.getRoute().unsetObject();
            } else {
                String href = fareRule.getRoute().getHref();
                if (href != null && !href.isEmpty()) {
                    helper.propagateObjectXlink(
                            schemaMapper.getTableName(ADETable.FARERULE),
                            objectId, href, "route_id");
                }
            }
        }

        if (routeId != 0)
            ps.setLong(9, routeId);
        else
            ps.setNull(9, Types.NULL);

        ps.addBatch();
        if (++batchCounter == helper.getDatabaseAdapter().getMaxBatchSize())
            helper.executeBatch(schemaMapper.getTableName(ADETable.FARERULE));
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
