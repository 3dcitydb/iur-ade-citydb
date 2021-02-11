/*
 * iur-ade-citydb - i-Urban Revitalization ADE extension for the 3DCityDB
 * https://github.com/3dcitydb/iur-ade-citydb
 *
 * iur-ade-citydb is part of the 3D City Database project
 *
 * Copyright 2019-2020 virtualcitySYSTEMS GmbH
 * https://www.virtualcitysystems.de/
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

import org.citydb.ade.exporter.CityGMLExportHelper;
import org.citydb.ade.iur.exporter.ExportManager;
import org.citydb.ade.iur.schema.ADETable;
import org.citydb.citygml.exporter.CityGMLExportException;
import org.citydb.database.schema.mapping.AbstractType;
import org.citydb.query.filter.projection.CombinedProjectionFilter;
import org.citydb.query.filter.projection.ProjectionFilter;
import org.citydb.sqlbuilder.expression.PlaceHolder;
import org.citydb.sqlbuilder.schema.Table;
import org.citydb.sqlbuilder.select.Select;
import org.citydb.sqlbuilder.select.join.Join;
import org.citydb.sqlbuilder.select.join.JoinFactory;
import org.citydb.sqlbuilder.select.operator.comparison.ComparisonFactory;
import org.citydb.sqlbuilder.select.operator.comparison.ComparisonName;
import org.citydb.sqlbuilder.select.operator.logical.LogicalOperationName;
import org.citygml4j.ade.iur.model.module.StatisticalGridModule;
import org.citygml4j.ade.iur.model.urg.Households;
import org.citygml4j.ade.iur.model.urg.NumberOfHouseholds;
import org.citygml4j.ade.iur.model.urg.NumberOfHouseholdsProperty;
import org.citygml4j.model.gml.basicTypes.Code;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class HouseholdsExporter implements StatisticalGridModuleExporter {
    private final PreparedStatement ps;
    private final String module;
    private final StatisticalGridExporter statisticalGridExporter;

    public HouseholdsExporter(Connection connection, CityGMLExportHelper helper, ExportManager manager) throws CityGMLExportException, SQLException {
        String tableName = manager.getSchemaMapper().getTableName(ADETable.HOUSEHOLDS);
        CombinedProjectionFilter projectionFilter = helper.getCombinedProjectionFilter(tableName);
        module = StatisticalGridModule.v1_4.getNamespaceURI();

        statisticalGridExporter = manager.getExporter(StatisticalGridExporter.class);

        Table table = new Table(helper.getTableNameWithSchema(tableName));
        Table statisticalGrid = new Table(helper.getTableNameWithSchema(manager.getSchemaMapper().getTableName(ADETable.STATISTICALGRID)));

        Select select = statisticalGridExporter.addProjection(new Select(), statisticalGrid, projectionFilter, "sg")
                .addJoin(JoinFactory.inner(statisticalGrid, "id", ComparisonName.EQUAL_TO, table.getColumn("id")))
                .addSelection(ComparisonFactory.equalTo(table.getColumn("id"), new PlaceHolder<>()));
        if (projectionFilter.containsProperty("numberOfOrdinaryHousehold", module))
            select.addProjection(table.getColumn("numberofordinaryhousehold"));
        if (projectionFilter.containsProperty("numberOfMainHousehold", module))
            select.addProjection(table.getColumn("numberofmainhousehold"));
        if (projectionFilter.containsProperty("numberOfHouseholdsByOwnership", module)
                || projectionFilter.containsProperty("numberOfHouseholdsByStructure", module)) {
            Table households = new Table(helper.getTableNameWithSchema(manager.getSchemaMapper().getTableName(ADETable.NUMBEROFHOUSEHOLDS)));
            select.addProjection(households.getColumns("households_numberofhouseh_id", "households_numberofhous_id_1",
                    "class", "class_codespace", "number_"));
            Join join = JoinFactory.left(households, "households_numberofhouseh_id", ComparisonName.EQUAL_TO, table.getColumn("id"));
            join.addCondition(ComparisonFactory.equalTo(households.getColumn("households_numberofhous_id_1"), table.getColumn("id")));
            join.setConditionOperationName(LogicalOperationName.OR);
            select.addJoin(join);
        }
        ps = connection.prepareStatement(select.toString());
    }

    public void doExport(Households households, long objectId, AbstractType<?> objectType, ProjectionFilter projectionFilter) throws CityGMLExportException, SQLException {
        ps.setLong(1, objectId);

        try (ResultSet rs = ps.executeQuery()) {
            boolean isInitialized = false;

            while (rs.next()) {
                if (!isInitialized) {
                    statisticalGridExporter.doExport(households, projectionFilter, "sg", rs);

                    if (projectionFilter.containsProperty("numberOfOrdinaryHousehold", module)) {
                        int numberOfOrdinaryHousehold = rs.getInt("numberofordinaryhousehold");
                        if (!rs.wasNull())
                            households.setNumberOfOrdinaryHouseholds(numberOfOrdinaryHousehold);
                    }

                    if (projectionFilter.containsProperty("numberOfMainHousehold", module)) {
                        int numberOfMainHousehold = rs.getInt("numberofmainhousehold");
                        if (!rs.wasNull())
                            households.setNumberOfMainHouseholds(numberOfMainHousehold);
                    }

                    isInitialized = true;
                }

                if (projectionFilter.containsProperty("numberOfHouseholdsByOwnership", module)) {
                    long numberId = rs.getLong("households_numberofhouseh_id");
                    if (numberId != 0)
                        households.getNumberOfHouseholdsByOwnership().add(getNumberOfHouseholds(rs));
                }

                if (projectionFilter.containsProperty("numberOfHouseholdsByStructure", module)) {
                    long numberId = rs.getLong("households_numberofhous_id_1");
                    if (numberId != 0)
                        households.getNumberOfHouseholdsByStructure().add(getNumberOfHouseholds(rs));
                }
            }
        }
    }

    private NumberOfHouseholdsProperty getNumberOfHouseholds(ResultSet rs) throws SQLException {
        NumberOfHouseholds numberOfHouseholds = new NumberOfHouseholds();

        String classifier = rs.getString("class");
        if (!rs.wasNull()) {
            Code code = new Code(classifier);
            code.setCodeSpace(rs.getString("class_codespace"));
            numberOfHouseholds.setClassifier(code);
        }

        int number = rs.getInt("number_");
        if (!rs.wasNull())
            numberOfHouseholds.setNumber(number);

        return new NumberOfHouseholdsProperty(numberOfHouseholds);
    }

    @Override
    public void close() throws CityGMLExportException, SQLException {
        ps.close();
    }
}
