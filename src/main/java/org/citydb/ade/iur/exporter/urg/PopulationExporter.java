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
import org.citygml4j.ade.iur.model.urg.Population;
import org.citygml4j.ade.iur.model.urg.PopulationByAgeAndSex;
import org.citygml4j.ade.iur.model.urg.PopulationByAgeAndSexProperty;
import org.citygml4j.model.gml.basicTypes.Code;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PopulationExporter implements StatisticalGridModuleExporter {
    private final PreparedStatement ps;
    private final String module;
    private final StatisticalGridExporter statisticalGridExporter;

    public PopulationExporter(Connection connection, CityGMLExportHelper helper, ExportManager manager) throws CityGMLExportException, SQLException {
        String tableName = manager.getSchemaMapper().getTableName(ADETable.POPULATION);
        CombinedProjectionFilter projectionFilter = helper.getCombinedProjectionFilter(tableName);
        module = StatisticalGridModule.v1_4.getNamespaceURI();

        statisticalGridExporter = manager.getExporter(StatisticalGridExporter.class);

        Table table = new Table(helper.getTableNameWithSchema(tableName));
        Table statisticalGrid = new Table(helper.getTableNameWithSchema(manager.getSchemaMapper().getTableName(ADETable.STATISTICALGRID)));

        Select select = statisticalGridExporter.addProjection(new Select(), statisticalGrid, projectionFilter, "sg")
                .addJoin(JoinFactory.inner(statisticalGrid, "id", ComparisonName.EQUAL_TO, table.getColumn("id")))
                .addSelection(ComparisonFactory.equalTo(table.getColumn("id"), new PlaceHolder<>()));
        if (projectionFilter.containsProperty("total", module))
            select.addProjection(table.getColumn("total"));
        if (projectionFilter.containsProperty("daytimePopulation", module))
            select.addProjection(table.getColumn("daytimepopulation"));
        if (projectionFilter.containsProperty("daytimePopulationDensity", module))
            select.addProjection(table.getColumn("daytimepopulationdensity"));
        if (projectionFilter.containsProperty("naturalIncrease", module))
            select.addProjection(table.getColumn("naturalincrease"));
        if (projectionFilter.containsProperty("births", module))
            select.addProjection(table.getColumn("births"));
        if (projectionFilter.containsProperty("deaths", module))
            select.addProjection(table.getColumn("deaths"));
        if (projectionFilter.containsProperty("socialIncrease", module))
            select.addProjection(table.getColumn("socialincrease"));
        if (projectionFilter.containsProperty("moveFrom", module))
            select.addProjection(table.getColumn("movefrom"));
        if (projectionFilter.containsProperty("moveTo", module))
            select.addProjection(table.getColumn("moveto"));
        if (projectionFilter.containsProperty("increasement", module))
            select.addProjection(table.getColumn("increasement"));
        if (projectionFilter.containsProperty("malePopulation", module))
            select.addProjection(table.getColumn("malepopulation"));
        if (projectionFilter.containsProperty("femalePopulation", module))
            select.addProjection(table.getColumn("femalepopulation"));
        if (projectionFilter.containsProperty("populationByAgeAndSex", module)) {
            Table population = new Table(helper.getTableNameWithSchema(manager.getSchemaMapper().getTableName(ADETable.POPULATIONBYAGEANDSEX)));
            select.addProjection(population.getColumns("ageandsex", "ageandsex_codespace", "number_"))
                    .addJoin(JoinFactory.left(population, "population_populationbyag_id", ComparisonName.EQUAL_TO, table.getColumn("id")));
        }
        ps = connection.prepareStatement(select.toString());
    }

    public void doExport(Population population, long objectId, AbstractType<?> objectType, ProjectionFilter projectionFilter) throws CityGMLExportException, SQLException {
        ps.setLong(1, objectId);

        try (ResultSet rs = ps.executeQuery()) {
            boolean isInitialized = false;

            while (rs.next()) {
                if (!isInitialized) {
                    statisticalGridExporter.doExport(population, projectionFilter, "sg", rs);

                    if (projectionFilter.containsProperty("total", module)) {
                        int total = rs.getInt("total");
                        if (!rs.wasNull())
                            population.setTotal(total);
                    }

                    if (projectionFilter.containsProperty("daytimePopulation", module)) {
                        int daytimePopulation = rs.getInt("daytimepopulation");
                        if (!rs.wasNull())
                            population.setDaytimePopulation(daytimePopulation);
                    }

                    if (projectionFilter.containsProperty("daytimePopulationDensity", module)) {
                        double daytimePopulationDensity = rs.getDouble("daytimepopulationdensity");
                        if (!rs.wasNull())
                            population.setDaytimePopulationDensity(daytimePopulationDensity);
                    }

                    if (projectionFilter.containsProperty("naturalIncrease", module)) {
                        int naturalIncrease = rs.getInt("naturalincrease");
                        if (!rs.wasNull())
                            population.setNaturalIncrease(naturalIncrease);
                    }

                    if (projectionFilter.containsProperty("births", module)) {
                        int births = rs.getInt("births");
                        if (!rs.wasNull())
                            population.setBirths(births);
                    }

                    if (projectionFilter.containsProperty("deaths", module)) {
                        int deaths = rs.getInt("deaths");
                        if (!rs.wasNull())
                            population.setDeaths(deaths);
                    }

                    if (projectionFilter.containsProperty("socialIncrease", module)) {
                        int socialIncrease = rs.getInt("socialincrease");
                        if (!rs.wasNull())
                            population.setSocialIncrease(socialIncrease);
                    }

                    if (projectionFilter.containsProperty("moveFrom", module)) {
                        int moveFrom = rs.getInt("movefrom");
                        if (!rs.wasNull())
                            population.setMoveFrom(moveFrom);
                    }

                    if (projectionFilter.containsProperty("moveTo", module)) {
                        int moveTo = rs.getInt("moveto");
                        if (!rs.wasNull())
                            population.setMoveTo(moveTo);
                    }

                    if (projectionFilter.containsProperty("increasement", module)) {
                        int increasement = rs.getInt("increasement");
                        if (!rs.wasNull())
                            population.setIncreasement(increasement);
                    }

                    if (projectionFilter.containsProperty("malePopulation", module)) {
                        int malePopulation = rs.getInt("malepopulation");
                        if (!rs.wasNull())
                            population.setMalePopulation(malePopulation);
                    }

                    if (projectionFilter.containsProperty("femalePopulation", module)) {
                        int femalePopulation = rs.getInt("femalepopulation");
                        if (!rs.wasNull())
                            population.setFemalePopulation(femalePopulation);
                    }


                    isInitialized = true;
                }

                if (projectionFilter.containsProperty("populationByAgeAndSex", module)) {
                    PopulationByAgeAndSex populationByAgeAndSex = new PopulationByAgeAndSex();

                    String age = rs.getString("ageandsex");
                    if (!rs.wasNull()) {
                        Code code = new Code(age);
                        code.setCodeSpace(rs.getString("ageandsex_codespace"));
                        populationByAgeAndSex.setAgeAndSex(code);
                    }

                    int number = rs.getInt("number_");
                    if (number != 0)
                        populationByAgeAndSex.setNumber(number);

                    population.getPopulationByAgeAndSex().add(new PopulationByAgeAndSexProperty(populationByAgeAndSex));
                }
            }
        }
    }

    @Override
    public void close() throws CityGMLExportException, SQLException {
        ps.close();
    }
}
