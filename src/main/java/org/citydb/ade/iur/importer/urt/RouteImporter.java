package org.citydb.ade.iur.importer.urt;

import org.citydb.ade.importer.CityGMLImportHelper;
import org.citydb.ade.importer.ForeignKeys;
import org.citydb.ade.iur.importer.ImportManager;
import org.citydb.ade.iur.schema.ADETable;
import org.citydb.ade.iur.schema.SchemaMapper;
import org.citydb.citygml.importer.CityGMLImportException;
import org.citydb.citygml.importer.database.content.GeometryConverter;
import org.citydb.config.geometry.GeometryObject;
import org.citydb.database.schema.mapping.AbstractObjectType;
import org.citygml4j.ade.iur.model.urt.Agency;
import org.citygml4j.ade.iur.model.urt.Route;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class RouteImporter implements PublicTransitModuleImporter {
    private final Connection connection;
    private final CityGMLImportHelper helper;
    private final SchemaMapper schemaMapper;
    private final PreparedStatement ps;

    private final PublicTransitImporter publicTransitImporter;
    private final DescriptionImporter descriptionImporter;
    private final GeometryConverter geometryConverter;

    private int batchCounter;

    public RouteImporter(Connection connection, CityGMLImportHelper helper, ImportManager manager) throws CityGMLImportException, SQLException {
        this.connection = connection;
        this.helper = helper;
        this.schemaMapper = manager.getSchemaMapper();

        ps = connection.prepareStatement("insert into " +
                helper.getTableNameWithSchema(manager.getSchemaMapper().getTableName(ADETable.ROUTE)) + " " +
                "(id, color, continuousdropoff, continuousdropoff_codespace, continuouspickup, continuouspickup_codespace, " +
                "destinationstop, lod0multicurve, longname, originstop, routesortorder, shortname, textcolor, " +
                "type, type_codespace, updatedate, url, viastop, description_id, agency_id, parentroute_id) " +
                "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

        publicTransitImporter = manager.getImporter(PublicTransitImporter.class);
        descriptionImporter = manager.getImporter(DescriptionImporter.class);
        geometryConverter = helper.getGeometryConverter();
    }

    public void doImport(Route route, long objectId, AbstractObjectType<?> objectType, ForeignKeys foreignKeys) throws CityGMLImportException, SQLException {
        publicTransitImporter.doImport(route, objectId, objectType, foreignKeys);
        ps.setLong(1, objectId);

        ps.setString(2, route.getColor());

        if (route.getContinuousDropOff() != null && route.getContinuousDropOff().isSetValue()) {
            ps.setString(3, route.getContinuousDropOff().getValue());
            ps.setString(4, route.getContinuousDropOff().getCodeSpace());
        } else {
            ps.setNull(3, Types.VARCHAR);
            ps.setNull(4, Types.VARCHAR);
        }

        if (route.getContinuousPickup() != null && route.getContinuousPickup().isSetValue()) {
            ps.setString(5, route.getContinuousPickup().getValue());
            ps.setString(6, route.getContinuousPickup().getCodeSpace());
        } else {
            ps.setNull(5, Types.VARCHAR);
            ps.setNull(6, Types.VARCHAR);
        }

        ps.setString(7, route.getDestinationStop());

        GeometryObject multiCurve = geometryConverter.getMultiCurve(route.getLod0MultiCurve());
        if (multiCurve != null) {
            ps.setObject(8, helper.getDatabaseAdapter().getGeometryConverter().getDatabaseObject(multiCurve, connection));
            route.setLod0MultiCurve(null);
        } else
            ps.setNull(8, helper.getDatabaseAdapter().getGeometryConverter().getNullGeometryType(),
                    helper.getDatabaseAdapter().getGeometryConverter().getNullGeometryTypeName());

        ps.setString(9, route.getLongName());
        ps.setString(10, route.getOriginStop());

        if (route.getRouteSortOrder() != null)
            ps.setInt(11, route.getRouteSortOrder());
        else
            ps.setNull(11, Types.INTEGER);

        ps.setString(12, route.getShortName());
        ps.setString(13, route.getTextColor());

        if (route.getType() != null && route.getType().isSetValue()) {
            ps.setString(14, route.getType().getValue());
            ps.setString(15, route.getType().getCodeSpace());
        } else {
            ps.setNull(14, Types.VARCHAR);
            ps.setNull(15, Types.VARCHAR);
        }

        if (route.getUpdateDate() != null)
            ps.setDate(16, Date.valueOf(route.getUpdateDate()));
        else
            ps.setNull(16, Types.DATE);

        ps.setString(17, route.getUrl());
        ps.setString(18, route.getViaStop());

        long descriptionId = 0;
        if (route.getRouteDescription() != null && route.getRouteDescription().getObject() != null) {
            descriptionId = descriptionImporter.doImport(route.getRouteDescription().getObject());
            route.getRouteDescription().setObject(null);
        }

        if (descriptionId != 0)
            ps.setLong(19, descriptionId);
        else
            ps.setNull(19, Types.NULL);

        long agencyId = 0;
        if (route.getAgency() != null) {
            Agency agency = route.getAgency().getObject();
            if (agency != null) {
                agencyId = helper.importObject(agency);
                route.getAgency().unsetObject();
            } else {
                String href = route.getAgency().getHref();
                if (href != null && !href.isEmpty()) {
                    helper.propagateObjectXlink(
                            schemaMapper.getTableName(ADETable.ROUTE),
                            objectId, href, "agency_id");
                }
            }
        }

        if (agencyId != 0)
            ps.setLong(20, agencyId);
        else
            ps.setNull(20, Types.NULL);

        long parentRouteId = 0;
        if (route.getParentRoute() != null) {
            Route parent = route.getParentRoute().getObject();
            if (parent != null) {
                parentRouteId = helper.importObject(parent);
                route.getParentRoute().unsetObject();
            } else {
                String href = route.getParentRoute().getHref();
                if (href != null && !href.isEmpty()) {
                    helper.propagateObjectXlink(
                            schemaMapper.getTableName(ADETable.ROUTE),
                            objectId, href, "parentroute_id");
                }
            }
        }

        if (parentRouteId != 0)
            ps.setLong(21, parentRouteId);
        else
            ps.setNull(21, Types.NULL);

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
