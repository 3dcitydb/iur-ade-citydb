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

package org.citydb.ade.iur.exporter.urg;

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
import org.citygml4j.ade.iur.model.urg.OfficesAndEmployees;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OfficesAndEmployeesExporter implements StatisticalGridModuleExporter {
    private final PreparedStatement ps;
    private final String module;
    private final StatisticalGridExporter statisticalGridExporter;

    public OfficesAndEmployeesExporter(Connection connection, CityGMLExportHelper helper, ExportManager manager) throws CityGMLExportException, SQLException {
        String tableName = manager.getSchemaMapper().getTableName(ADETable.OFFICESANDEMPLOYEES);
        CombinedProjectionFilter projectionFilter = helper.getCombinedProjectionFilter(tableName);
        module = StatisticalGridModule.v1_4.getNamespaceURI();

        statisticalGridExporter = manager.getExporter(StatisticalGridExporter.class);

        Table table = new Table(helper.getTableNameWithSchema(tableName));
        Table statisticalGrid = new Table(helper.getTableNameWithSchema(manager.getSchemaMapper().getTableName(ADETable.STATISTICALGRID)));

        Select select = statisticalGridExporter.addProjection(new Select(), statisticalGrid, projectionFilter, "sg")
                .addJoin(JoinFactory.inner(statisticalGrid, "id", ComparisonName.EQUAL_TO, table.getColumn("id")))
                .addSelection(ComparisonFactory.equalTo(table.getColumn("id"), new PlaceHolder<>()));
        if (projectionFilter.containsProperty("numberOfOffices", module))
            select.addProjection(table.getColumn("numberofoffices"));
        if (projectionFilter.containsProperty("numberOfEmployees", module))
            select.addProjection(table.getColumn("numberofemployees"));
        ps = connection.prepareStatement(select.toString());
    }

    public void doExport(OfficesAndEmployees officesAndEmployees, long objectId, AbstractType<?> objectType, ProjectionFilter projectionFilter) throws CityGMLExportException, SQLException {
        ps.setLong(1, objectId);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                statisticalGridExporter.doExport(officesAndEmployees, projectionFilter, "sg", rs);

                if (projectionFilter.containsProperty("numberOfOffices", module)) {
                    int numberOfOffices = rs.getInt("numberofoffices");
                    if (!rs.wasNull())
                        officesAndEmployees.setNumberOfOffices(numberOfOffices);
                }

                if (projectionFilter.containsProperty("numberOfEmployees", module)) {
                    int numberOfEmployees = rs.getInt("numberofemployees");
                    if (!rs.wasNull())
                        officesAndEmployees.setNumberOfEmployees(numberOfEmployees);
                }
            }
        }
    }

    @Override
    public void close() throws CityGMLExportException, SQLException {
        ps.close();
    }
}
