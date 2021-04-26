/*
 * 3D City Database - The Open Source CityGML Database
 * https://www.3dcitydb.org/
 *
 * Copyright 2013 - 2021
 * Chair of Geoinformatics
 * Technical University of Munich, Germany
 * https://www.lrg.tum.de/gis/
 *
 * The 3D City Database is jointly developed with the following
 * cooperation partners:
 *
 * Virtual City Systems, Berlin <https://vc.systems/>
 * M.O.S.S. Computer Grafik Systeme GmbH, Taufkirchen <http://www.moss.de/>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.citydb.ade.iur.exporter.urt;

import org.citydb.ade.exporter.CityGMLExportHelper;
import org.citydb.ade.iur.exporter.ExportManager;
import org.citydb.ade.iur.schema.ADETable;
import org.citydb.citygml.exporter.CityGMLExportException;
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
import org.citygml4j.ade.iur.model.urt.Attribution;
import org.citygml4j.ade.iur.model.urt.RouteProperty;
import org.citygml4j.ade.iur.model.urt.TripProperty;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AttributionExporter implements PublicTransitModuleExporter {
    private final PreparedStatement ps;
    private final String module;
    private final PublicTransitExporter publicTransitExporter;

    public AttributionExporter(Connection connection, CityGMLExportHelper helper, ExportManager manager) throws CityGMLExportException, SQLException {
        String tableName = manager.getSchemaMapper().getTableName(ADETable.ATTRIBUTION);
        CombinedProjectionFilter projectionFilter = helper.getCombinedProjectionFilter(tableName);
        module = StatisticalGridModule.v1_4.getNamespaceURI();

        publicTransitExporter = manager.getExporter(PublicTransitExporter.class);

        Table table = new Table(helper.getTableNameWithSchema(tableName));
        Table publicTransit = new Table(helper.getTableNameWithSchema(manager.getSchemaMapper().getTableName(ADETable.PUBLICTRANSIT)));

        Select select = new Select().addProjection(table.getColumn("organizationname"))
                .addJoin(JoinFactory.inner(publicTransit, "id", ComparisonName.EQUAL_TO, table.getColumn("id")))
                .addSelection(ComparisonFactory.equalTo(table.getColumn("id"), new PlaceHolder<>()));
        publicTransitExporter.addProjection(select, publicTransit, projectionFilter, "pt");
        if (projectionFilter.containsProperty("isProducer", module))
            select.addProjection(table.getColumn("isproducer"));
        if (projectionFilter.containsProperty("isOperator", module))
            select.addProjection(table.getColumn("isoperator"));
        if (projectionFilter.containsProperty("isAuthority", module))
            select.addProjection(table.getColumn("isauthority"));
        if (projectionFilter.containsProperty("url", module))
            select.addProjection(table.getColumn("url"));
        if (projectionFilter.containsProperty("email", module))
            select.addProjection(table.getColumn("email"));
        if (projectionFilter.containsProperty("phoneNumber", module))
            select.addProjection(table.getColumn("phonenumber"));
        if (projectionFilter.containsProperty("agency", module)) {
            Table parent = new Table(helper.getTableNameWithSchema(MappingConstants.CITYOBJECT));
            select.addProjection(parent.getColumn("gmlid", "agmlid"))
                    .addJoin(JoinFactory.left(parent, "id", ComparisonName.EQUAL_TO, table.getColumn("agency_id")));
        }
        if (projectionFilter.containsProperty("route", module)) {
            Table parent = new Table(helper.getTableNameWithSchema(MappingConstants.CITYOBJECT));
            select.addProjection(parent.getColumn("gmlid", "rgmlid"))
                    .addJoin(JoinFactory.left(parent, "id", ComparisonName.EQUAL_TO, table.getColumn("route_id")));
        }
        if (projectionFilter.containsProperty("trip", module)) {
            Table parent = new Table(helper.getTableNameWithSchema(MappingConstants.CITYOBJECT));
            select.addProjection(parent.getColumn("gmlid", "tgmlid"))
                    .addJoin(JoinFactory.left(parent, "id", ComparisonName.EQUAL_TO, table.getColumn("trip_id")));
        }
        ps = connection.prepareStatement(select.toString());
    }

    public void doExport(Attribution attribution, long objectId, AbstractType<?> objectType, ProjectionFilter projectionFilter) throws CityGMLExportException, SQLException {
        ps.setLong(1, objectId);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                publicTransitExporter.doExport(attribution, projectionFilter, "pt", rs);

                attribution.setOrganizationName(rs.getString("organizationname"));

                if (projectionFilter.containsProperty("isProducer", module)) {
                    boolean isProducer = rs.getBoolean("isproducer");
                    if (!rs.wasNull())
                        attribution.setIsProducer(isProducer);
                }

                if (projectionFilter.containsProperty("isOperator", module)) {
                    boolean isOperator = rs.getBoolean("isoperator");
                    if (!rs.wasNull())
                        attribution.setIsOperator(isOperator);
                }

                if (projectionFilter.containsProperty("isAuthority", module)) {
                    boolean isAuthority = rs.getBoolean("isauthority");
                    if (!rs.wasNull())
                        attribution.setIsAuthority(isAuthority);
                }

                if (projectionFilter.containsProperty("url", module))
                    attribution.setUrl(rs.getString("url"));

                if (projectionFilter.containsProperty("email", module))
                    attribution.setEmail(rs.getString("email"));

                if (projectionFilter.containsProperty("phoneNumber", module))
                    attribution.setPhoneNumber(rs.getString("phonenumber"));

                if (projectionFilter.containsProperty("agency", module)) {
                    String gmlId = rs.getString("agmlid");
                    if (gmlId != null)
                        attribution.setAgency(new AgencyProperty("#" + gmlId));
                }

                if (projectionFilter.containsProperty("route", module)) {
                    String gmlId = rs.getString("rgmlid");
                    if (gmlId != null)
                        attribution.setRoute(new RouteProperty("#" + gmlId));
                }

                if (projectionFilter.containsProperty("trip", module)) {
                    String gmlId = rs.getString("tgmlid");
                    if (gmlId != null)
                        attribution.setTrip(new TripProperty("#" + gmlId));
                }
            }
        }
    }

    @Override
    public void close() throws CityGMLExportException, SQLException {
        ps.close();
    }
}
