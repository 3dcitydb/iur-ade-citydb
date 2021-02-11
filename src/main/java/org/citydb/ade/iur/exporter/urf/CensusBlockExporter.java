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

package org.citydb.ade.iur.exporter.urf;

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
import org.citydb.sqlbuilder.select.join.Join;
import org.citydb.sqlbuilder.select.join.JoinFactory;
import org.citydb.sqlbuilder.select.operator.comparison.ComparisonFactory;
import org.citydb.sqlbuilder.select.operator.comparison.ComparisonName;
import org.citydb.sqlbuilder.select.operator.logical.LogicalOperationName;
import org.citygml4j.ade.iur.model.module.UrbanFunctionModule;
import org.citygml4j.ade.iur.model.urf.CensusBlock;
import org.citygml4j.ade.iur.model.urf.NumberOfHouseholds;
import org.citygml4j.ade.iur.model.urf.NumberOfHouseholdsProperty;
import org.citygml4j.ade.iur.model.urf.TargetProperty;
import org.citygml4j.model.gml.basicTypes.Code;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class CensusBlockExporter implements UrbanFunctionModuleExporter {
    private final PreparedStatement ps;
    private final String module;
    private final UrbanFunctionExporter urbanFunctionExporter;

    public CensusBlockExporter(Connection connection, CityGMLExportHelper helper, ExportManager manager) throws CityGMLExportException, SQLException {
        String tableName = manager.getSchemaMapper().getTableName(ADETable.CENSUSBLOCK);
        CombinedProjectionFilter projectionFilter = helper.getCombinedProjectionFilter(tableName);
        module = UrbanFunctionModule.v1_4.getNamespaceURI();

        urbanFunctionExporter = manager.getExporter(UrbanFunctionExporter.class);

        Table table = new Table(helper.getTableNameWithSchema(tableName));
        Table urbanFunction = new Table(helper.getTableNameWithSchema(manager.getSchemaMapper().getTableName(ADETable.URBANFUNCTION)));

        Select select = new Select().addJoin(JoinFactory.inner(urbanFunction, "id", ComparisonName.EQUAL_TO, table.getColumn("id")))
                .addSelection(ComparisonFactory.equalTo(table.getColumn("id"), new PlaceHolder<>()));
        urbanFunctionExporter.addProjection(select, urbanFunction, projectionFilter, "uf");
        if (projectionFilter.containsProperty("daytimePopulation", module))
            select.addProjection(table.getColumn("daytimepopulation"));
        if (projectionFilter.containsProperty("daytimePopulationDensity", module))
            select.addProjection(table.getColumn("daytimepopulationdensity"));
        if (projectionFilter.containsProperty("numberOfOrdinaryHouseholds", module))
            select.addProjection(table.getColumn("numberofordinaryhouseholds"));
        if (projectionFilter.containsProperty("numberOfMainHouseholds", module))
            select.addProjection(table.getColumn("numberofmainhouseholds"));
        if (projectionFilter.containsProperty("numberOfHouseholdsByOwnership", module)
                || projectionFilter.containsProperty("numberOfHouseholdsByStructure", module)) {
            Table households = new Table(helper.getTableNameWithSchema(manager.getSchemaMapper().getTableName(ADETable.NUMBEROFHOUSEHOLDS_1)));
            select.addProjection(households.getColumn("id", "hhid"))
                    .addProjection(households.getColumns("censusblock_numberofhouse_id", "censusblock_numberofhou_id_1",
                    "class", "class_codespace", "number_"));
            Join join = JoinFactory.left(households, "censusblock_numberofhouse_id", ComparisonName.EQUAL_TO, table.getColumn("id"));
            join.addCondition(ComparisonFactory.equalTo(households.getColumn("censusblock_numberofhou_id_1"), table.getColumn("id")));
            join.setConditionOperationName(LogicalOperationName.OR);
            select.addJoin(join);
        }
        if (projectionFilter.containsProperty("target", module)) {
            Table targets = new Table(helper.getTableNameWithSchema(manager.getSchemaMapper().getTableName(ADETable.URBANFUNC_TO_CITYOBJEC)));
            Table cityObject = new Table(helper.getTableNameWithSchema(MappingConstants.CITYOBJECT));
            select.addProjection(cityObject.getColumn("id", "tid"), cityObject.getColumn("gmlid"))
                    .addJoin(JoinFactory.left(targets, "urbanfunction_id", ComparisonName.EQUAL_TO, table.getColumn("id")))
                    .addJoin(JoinFactory.left(cityObject, "id", ComparisonName.EQUAL_TO, targets.getColumn("cityobject_id")));
        }
        ps = connection.prepareStatement(select.toString());
    }

    public void doExport(CensusBlock censusBlock, long objectId, AbstractType<?> objectType, ProjectionFilter projectionFilter) throws CityGMLExportException, SQLException {
        ps.setLong(1, objectId);

        try (ResultSet rs = ps.executeQuery()) {
            boolean isInitialized = false;
            Set<Long> households = new HashSet<>();
            Set<Long> targets = new HashSet<>();

            while (rs.next()) {
                if (!isInitialized) {
                    urbanFunctionExporter.doExport(censusBlock, projectionFilter, "uf", rs);

                    if (projectionFilter.containsProperty("daytimePopulation", module)) {
                        int daytimePopulation = rs.getInt("daytimepopulation");
                        if (!rs.wasNull())
                            censusBlock.setDaytimePopulation(daytimePopulation);
                    }

                    if (projectionFilter.containsProperty("daytimePopulationDensity", module)) {
                        double daytimePopulationDensity = rs.getDouble("daytimepopulationdensity");
                        if (!rs.wasNull())
                            censusBlock.setDaytimePopulationDensity(daytimePopulationDensity);
                    }

                    if (projectionFilter.containsProperty("numberOfOrdinaryHouseholds", module)) {
                        int numberOfOrdinaryHouseholds = rs.getInt("numberofordinaryhouseholds");
                        if (!rs.wasNull())
                            censusBlock.setNumberOfOrdinaryHouseholds(numberOfOrdinaryHouseholds);
                    }

                    if (projectionFilter.containsProperty("numberOfMainHouseholds", module)) {
                        int numberOfMainHouseholds = rs.getInt("numberofmainhouseholds");
                        if (!rs.wasNull())
                            censusBlock.setNumberOfMainHouseholds(numberOfMainHouseholds);
                    }

                    isInitialized = true;
                }

                long housholdId = rs.getLong("hhid");
                if (!rs.wasNull() && households.add(housholdId)) {
                    if (projectionFilter.containsProperty("numberOfHouseholdsByOwnership", module)) {
                        long numberId = rs.getLong("censusblock_numberofhouse_id");
                        if (numberId != 0)
                            censusBlock.getNumberOfHouseholdsByOwnership().add(getNumberOfHouseholds(rs));
                    }

                    if (projectionFilter.containsProperty("numberOfHouseholdsByStruture", module)) {
                        long numberId = rs.getLong("censusblock_numberofhou_id_1");
                        if (numberId != 0)
                            censusBlock.getNumberOfHouseholdsByStructure().add(getNumberOfHouseholds(rs));
                    }
                }

                if (projectionFilter.containsProperty("target", module)) {
                    long targetId = rs.getLong("tid");
                    if (!rs.wasNull() && targets.add(targetId)) {
                        String gmlId = rs.getString("gmlid");
                        if (gmlId != null)
                            censusBlock.getTargets().add(new TargetProperty("#" + gmlId));
                    }
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
