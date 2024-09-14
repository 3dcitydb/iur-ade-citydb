/*
 * 3D City Database - The Open Source CityGML Database
 * https://www.3dcitydb.org/
 *
 * Copyright 2013 - 2024
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

import org.citydb.ade.iur.exporter.ExportManager;
import org.citydb.ade.iur.schema.ADETable;
import org.citydb.core.ade.exporter.CityGMLExportHelper;
import org.citydb.core.database.schema.mapping.AbstractType;
import org.citydb.core.operation.exporter.CityGMLExportException;
import org.citydb.core.query.filter.projection.CombinedProjectionFilter;
import org.citydb.core.query.filter.projection.ProjectionFilter;
import org.citydb.sqlbuilder.expression.PlaceHolder;
import org.citydb.sqlbuilder.schema.Table;
import org.citydb.sqlbuilder.select.Select;
import org.citydb.sqlbuilder.select.join.JoinFactory;
import org.citydb.sqlbuilder.select.operator.comparison.ComparisonFactory;
import org.citydb.sqlbuilder.select.operator.comparison.ComparisonName;
import org.citygml4j.ade.iur.model.module.StatisticalGridModule;
import org.citygml4j.ade.iur.model.urt.Agency;
import org.citygml4j.model.gml.basicTypes.Code;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AgencyExporter implements PublicTransitModuleExporter {
    private final PreparedStatement ps;
    private final String module;
    private final PublicTransitExporter publicTransitExporter;

    public AgencyExporter(Connection connection, CityGMLExportHelper helper, ExportManager manager) throws CityGMLExportException, SQLException {
        String tableName = manager.getSchemaMapper().getTableName(ADETable.AGENCY);
        CombinedProjectionFilter projectionFilter = helper.getCombinedProjectionFilter(tableName);
        module = StatisticalGridModule.v1_4.getNamespaceURI();

        publicTransitExporter = manager.getExporter(PublicTransitExporter.class);

        Table table = new Table(helper.getTableNameWithSchema(tableName));
        Table publicTransit = new Table(helper.getTableNameWithSchema(manager.getSchemaMapper().getTableName(ADETable.PUBLICTRANSIT)));

        Select select = new Select().addProjection(table.getColumns("name", "url", "timezone", "timezone_codespace"))
                .addJoin(JoinFactory.inner(publicTransit, "id", ComparisonName.EQUAL_TO, table.getColumn("id")))
                .addSelection(ComparisonFactory.equalTo(table.getColumn("id"), new PlaceHolder<>()));
        publicTransitExporter.addProjection(select, publicTransit, projectionFilter, "pt");
        if (projectionFilter.containsProperty("language", module))
            select.addProjection(table.getColumns("language", "language_codespace"));
        if (projectionFilter.containsProperty("phone", module))
            select.addProjection(table.getColumn("phone"));
        if (projectionFilter.containsProperty("fareUrl", module))
            select.addProjection(table.getColumn("fareurl"));
        if (projectionFilter.containsProperty("email", module))
            select.addProjection(table.getColumn("email"));
        if (projectionFilter.containsProperty("officialName", module))
            select.addProjection(table.getColumn("officialname"));
        if (projectionFilter.containsProperty("zipNumber", module))
            select.addProjection(table.getColumn("zipnumber"));
        if (projectionFilter.containsProperty("address", module))
            select.addProjection(table.getColumn("address"));
        if (projectionFilter.containsProperty("presidentPosition", module))
            select.addProjection(table.getColumn("presidentposition"));
        if (projectionFilter.containsProperty("presidentName", module))
            select.addProjection(table.getColumn("presidentname"));
        ps = connection.prepareStatement(select.toString());
    }

    public void doExport(Agency agency, long objectId, AbstractType<?> objectType, ProjectionFilter projectionFilter) throws CityGMLExportException, SQLException {
        ps.setLong(1, objectId);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                publicTransitExporter.doExport(agency, projectionFilter, "pt", rs);

                agency.setAgencyName(rs.getString("name"));
                agency.setUrl(rs.getString("url"));

                String timezone = rs.getString("timezone");
                if (!rs.wasNull()) {
                    Code code = new Code(timezone);
                    code.setCodeSpace(rs.getString("timezone_codespace"));
                    agency.setTimeZone(code);
                }

                if (projectionFilter.containsProperty("language", module)) {
                    String language = rs.getString("language");
                    if (!rs.wasNull()) {
                        Code code = new Code(language);
                        code.setCodeSpace(rs.getString("language_codespace"));
                        agency.setLanguage(code);
                    }
                }

                if (projectionFilter.containsProperty("phone", module))
                    agency.setPhone(rs.getString("phone"));

                if (projectionFilter.containsProperty("fareUrl", module))
                    agency.setFareUrl(rs.getString("fareurl"));

                if (projectionFilter.containsProperty("email", module))
                    agency.setEmail(rs.getString("email"));

                if (projectionFilter.containsProperty("officialName", module))
                    agency.setOfficialName(rs.getString("officialname"));

                if (projectionFilter.containsProperty("zipNumber", module))
                    agency.setZipNumber(rs.getString("zipnumber"));

                if (projectionFilter.containsProperty("address", module))
                    agency.setAddress(rs.getString("address"));

                if (projectionFilter.containsProperty("presidentPosition", module))
                    agency.setPresidentPosition(rs.getString("presidentposition"));

                if (projectionFilter.containsProperty("presidentName", module))
                    agency.setPresidentName(rs.getString("presidentname"));
            }
        }
    }

    @Override
    public void close() throws CityGMLExportException, SQLException {
        ps.close();
    }
}
