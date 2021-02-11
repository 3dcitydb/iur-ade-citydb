package org.citydb.ade.iur.exporter.urt;

import org.citydb.ade.exporter.CityGMLExportHelper;
import org.citydb.ade.iur.exporter.ExportManager;
import org.citydb.ade.iur.schema.ADETable;
import org.citydb.citygml.exporter.CityGMLExportException;
import org.citydb.citygml.exporter.database.content.GMLConverter;
import org.citydb.config.geometry.GeometryObject;
import org.citydb.database.schema.mapping.AbstractType;
import org.citydb.database.schema.mapping.MappingConstants;
import org.citydb.query.filter.projection.CombinedProjectionFilter;
import org.citydb.query.filter.projection.ProjectionFilter;
import org.citydb.sqlbuilder.expression.PlaceHolder;
import org.citydb.sqlbuilder.schema.Table;
import org.citydb.sqlbuilder.select.Select;
import org.citydb.sqlbuilder.select.join.JoinFactory;
import org.citydb.sqlbuilder.select.operator.comparison.ComparisonFactory;
import org.citydb.sqlbuilder.select.operator.comparison.ComparisonName;
import org.citygml4j.ade.iur.model.module.StatisticalGridModule;
import org.citygml4j.ade.iur.model.urt.AgencyProperty;
import org.citygml4j.ade.iur.model.urt.Description;
import org.citygml4j.ade.iur.model.urt.Route;
import org.citygml4j.ade.iur.model.urt.RouteProperty;
import org.citygml4j.model.gml.basicTypes.Code;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RouteExporter implements PublicTransitModuleExporter {
    private final CityGMLExportHelper helper;
    private final PreparedStatement ps;
    private final String module;

    private final PublicTransitExporter publicTransitExporter;
    private final GMLConverter gmlConverter;

    public RouteExporter(Connection connection, CityGMLExportHelper helper, ExportManager manager) throws CityGMLExportException, SQLException {
        this.helper = helper;

        String tableName = manager.getSchemaMapper().getTableName(ADETable.ROUTE);
        CombinedProjectionFilter projectionFilter = helper.getCombinedProjectionFilter(tableName);
        module = StatisticalGridModule.v1_4.getNamespaceURI();

        publicTransitExporter = manager.getExporter(PublicTransitExporter.class);
        gmlConverter = helper.getGMLConverter();

        Table table = new Table(helper.getTableNameWithSchema(tableName));
        Table publicTransit = new Table(helper.getTableNameWithSchema(manager.getSchemaMapper().getTableName(ADETable.PUBLICTRANSIT)));
        Table agency = new Table(helper.getTableNameWithSchema(MappingConstants.CITYOBJECT));

        Select select = new Select().addProjection(table.getColumns("type", "type_codespace"))
                .addProjection(agency.getColumn("gmlid", "agmlid"))
                .addJoin(JoinFactory.inner(publicTransit, "id", ComparisonName.EQUAL_TO, table.getColumn("id")))
                .addJoin(JoinFactory.left(agency, "id", ComparisonName.EQUAL_TO, table.getColumn("agency_id")))
                .addSelection(ComparisonFactory.equalTo(table.getColumn("id"), new PlaceHolder<>()));
        publicTransitExporter.addProjection(select, publicTransit, projectionFilter, "pt");
        if (projectionFilter.containsProperty("shortName", module))
            select.addProjection(table.getColumn("shortname"));
        if (projectionFilter.containsProperty("longName", module))
            select.addProjection(table.getColumn("longname"));
        if (projectionFilter.containsProperty("url", module))
            select.addProjection(table.getColumn("url"));
        if (projectionFilter.containsProperty("color", module))
            select.addProjection(table.getColumn("color"));
        if (projectionFilter.containsProperty("textColor", module))
            select.addProjection(table.getColumn("textcolor"));
        if (projectionFilter.containsProperty("updateDate", module))
            select.addProjection(table.getColumn("updatedate"));
        if (projectionFilter.containsProperty("originStop", module))
            select.addProjection(table.getColumn("originstop"));
        if (projectionFilter.containsProperty("viaStop", module))
            select.addProjection(table.getColumn("viastop"));
        if (projectionFilter.containsProperty("destinationStop", module))
            select.addProjection(table.getColumn("destinationstop"));
        if (projectionFilter.containsProperty("routeSortOrder", module))
            select.addProjection(table.getColumn("routesortorder"));
        if (projectionFilter.containsProperty("continuousPickup", module))
            select.addProjection(table.getColumns("continuouspickup", "continuouspickup_codespace"));
        if (projectionFilter.containsProperty("continuousDropOff", module))
            select.addProjection(table.getColumns("continuousdropoff", "continuousdropoff_codespace"));
        if (helper.getLodFilter().isEnabled(0) && projectionFilter.containsProperty("lod0MultiCurve", module))
            select.addProjection(table.getColumn("lod0multicurve"));
        if (projectionFilter.containsProperty("parentRoute", module)) {
            Table parent = new Table(helper.getTableNameWithSchema(MappingConstants.CITYOBJECT));
            select.addProjection(parent.getColumn("gmlid", "pgmlid"))
                    .addJoin(JoinFactory.left(parent, "id", ComparisonName.EQUAL_TO, table.getColumn("parentroute_id")));
        }
        if (projectionFilter.containsProperty("description", module)) {
            Table description = new Table(helper.getTableNameWithSchema(manager.getSchemaMapper().getTableName(ADETable.DESCRIPTION)));
            select.addProjection(table.getColumn("description_id"))
                    .addProjection(description.getColumns("description", "frequencyofservice", "numberofcustomers"))
                    .addJoin(JoinFactory.left(description, "id", ComparisonName.EQUAL_TO, table.getColumn("description_id")));
        }
        ps = connection.prepareStatement(select.toString());
    }

    public void doExport(Route route, long objectId, AbstractType<?> objectType, ProjectionFilter projectionFilter) throws CityGMLExportException, SQLException {
        ps.setLong(1, objectId);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                publicTransitExporter.doExport(route, projectionFilter, "pt", rs);

                String type = rs.getString("type");
                if (!rs.wasNull()) {
                    Code code = new Code(type);
                    code.setCodeSpace(rs.getString("type_codespace"));
                    route.setType(code);
                }

                String agencyGmlId = rs.getString("agmlid");
                if (agencyGmlId != null)
                    route.setAgency(new AgencyProperty("#" + agencyGmlId));

                if (projectionFilter.containsProperty("shortName", module))
                    route.setShortName(rs.getString("shortname"));

                if (projectionFilter.containsProperty("longName", module))
                    route.setLongName(rs.getString("longname"));

                if (projectionFilter.containsProperty("url", module))
                    route.setUrl(rs.getString("url"));

                if (projectionFilter.containsProperty("color", module))
                    route.setColor(rs.getString("color"));

                if (projectionFilter.containsProperty("textColor", module))
                    route.setTextColor(rs.getString("textcolor"));

                Date updateDate = rs.getDate("updatedate");
                if (!rs.wasNull())
                    route.setUpdateDate(updateDate.toLocalDate());

                if (projectionFilter.containsProperty("originStop", module))
                    route.setOriginStop(rs.getString("originstop"));

                if (projectionFilter.containsProperty("viaStop", module))
                    route.setViaStop(rs.getString("viastop"));

                if (projectionFilter.containsProperty("destinationStop", module))
                    route.setDestinationStop(rs.getString("destinationstop"));

                if (projectionFilter.containsProperty("routeSortOrder", module)) {
                    int routeSortOrder = rs.getInt("routesortorder");
                    if (!rs.wasNull())
                        route.setRouteSortOrder(routeSortOrder);
                }

                if (projectionFilter.containsProperty("continuousPickup", module)) {
                    String continuousPickup = rs.getString("continuouspickup");
                    if (!rs.wasNull()) {
                        Code code = new Code(continuousPickup);
                        code.setCodeSpace(rs.getString("continuouspickup_codespace"));
                        route.setContinuousPickup(code);
                    }
                }

                if (projectionFilter.containsProperty("continuousDropOff", module)) {
                    String continuousDropOff = rs.getString("continuousdropoff");
                    if (!rs.wasNull()) {
                        Code code = new Code(continuousDropOff);
                        code.setCodeSpace(rs.getString("continuousdropoff_codespace"));
                        route.setContinuousDropOff(code);
                    }
                }

                if (helper.getLodFilter().isEnabled(0)
                        && projectionFilter.containsProperty("lod0MultiCurve", module)) {
                    Object geometry = rs.getObject("lod0multicurve");
                    if (!rs.wasNull()) {
                        GeometryObject multiCurve = helper.getDatabaseAdapter().getGeometryConverter().getMultiCurve(geometry);
                        route.setLod0MultiCurve(gmlConverter.getMultiCurveProperty(multiCurve));
                    }
                }

                if (projectionFilter.containsProperty("parentRoute", module)) {
                    String gmlId = rs.getString("pgmlid");
                    if (gmlId != null)
                        route.setParentRoute(new RouteProperty("#" + gmlId));
                }

                if (projectionFilter.containsProperty("description", module)) {
                    rs.getLong("description_id");
                    if (!rs.wasNull()) {
                        Description description = new Description();

                        description.setDescription(rs.getString("description"));

                        int frequencyOfService = rs.getInt("frequencyofservice");
                        if (!rs.wasNull())
                            description.setFrequencyOfService(frequencyOfService);

                        int numberOfCustomers = rs.getInt("numberofcustomers");
                        if (!rs.wasNull())
                            description.setNumberOfCustomers(numberOfCustomers);
                    }
                }
            }
        }
    }

    @Override
    public void close() throws CityGMLExportException, SQLException {
        ps.close();
    }
}
