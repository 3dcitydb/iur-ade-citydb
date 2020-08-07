package org.citydb.ade.iur.exporter.urg;

import org.citydb.ade.exporter.ADEExporter;
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
import org.citydb.sqlbuilder.select.join.JoinFactory;
import org.citydb.sqlbuilder.select.operator.comparison.ComparisonFactory;
import org.citydb.sqlbuilder.select.operator.comparison.ComparisonName;
import org.citygml4j.model.gml.basicTypes.Code;
import org.citygml4j.ade.iur.model.module.StatisticalGridModule;
import org.citygml4j.ade.iur.model.urg.Population;
import org.citygml4j.ade.iur.model.urg.PopulationByAgeAndSex;
import org.citygml4j.ade.iur.model.urg.PopulationByAgeAndSexProperty;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PopulationExporter implements ADEExporter {
    private final PreparedStatement ps;
    private final String module;
    private final StatisticalGridExporter statisticalGridExporter;

    public PopulationExporter(Connection connection, CityGMLExportHelper helper, ExportManager manager) throws CityGMLExportException, SQLException {
        String tableName = manager.getSchemaMapper().getTableName(ADETable.POPULATION);
        CombinedProjectionFilter projectionFilter = helper.getCombinedProjectionFilter(tableName);
        module = StatisticalGridModule.v1_3.getNamespaceURI();

        Table table = new Table(helper.getTableNameWithSchema(tableName));
        Table population = new Table(manager.getSchemaMapper().getTableName(ADETable.POPULATIONBYAGEANDSEX));

        Select select = new Select().addProjection(table.getColumns("births", "daytimepopulation",
                "daytimepopulationdensity", "deaths", "femalepopulation", "increasement",
                "malepopulation", "movefrom", "moveto", "naturalincrease", "socialincrease", "total"));
        if (projectionFilter.containsProperty("populationByAgeAndSex", module)) {
            select.addProjection(population.getColumns("age", "age_codespace", "number_", "sex", "sex_codespace"))
                    .addJoin(JoinFactory.left(population, "population_populationbyag_id", ComparisonName.EQUAL_TO, table.getColumn("id")));
        }
        select.addSelection(ComparisonFactory.equalTo(table.getColumn("id"), new PlaceHolder<>()));
        ps = connection.prepareStatement(select.toString());

        statisticalGridExporter = manager.getExporter(StatisticalGridExporter.class);
    }

    public void doExport(Population population, long objectId, AbstractType<?> objectType, ProjectionFilter projectionFilter) throws CityGMLExportException, SQLException {
        ps.setLong(1, objectId);

        try (ResultSet rs = ps.executeQuery()) {
            boolean isInitialized = false;

            while (rs.next()) {
                if (!isInitialized) {
                    statisticalGridExporter.doExport(population, objectId, objectType, projectionFilter);

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

                    int number = rs.getInt("number_");
                    if (number != 0)
                        populationByAgeAndSex.setNumber(number);

                    String age = rs.getString("age");
                    if (!rs.wasNull()) {
                        Code code = new Code(age);
                        code.setCodeSpace(rs.getString("age_codespace"));
                        populationByAgeAndSex.setAge(code);
                    }

                    String sex = rs.getString("sex");
                    if (!rs.wasNull()) {
                        Code code = new Code(sex);
                        code.setCodeSpace(rs.getString("sex_codespace"));
                        populationByAgeAndSex.setSex(code);
                    }

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
