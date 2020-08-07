package org.citydb.ade.iur.exporter.urf;

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
import org.citydb.sqlbuilder.select.join.Join;
import org.citydb.sqlbuilder.select.join.JoinFactory;
import org.citydb.sqlbuilder.select.operator.comparison.ComparisonFactory;
import org.citydb.sqlbuilder.select.operator.comparison.ComparisonName;
import org.citydb.sqlbuilder.select.operator.logical.LogicalOperationName;
import org.citygml4j.model.gml.basicTypes.Code;
import org.citygml4j.ade.iur.model.module.UrbanFunctionModule;
import org.citygml4j.ade.iur.model.urf.CensusBlock;
import org.citygml4j.ade.iur.model.urf.NumberOfHouseholds;
import org.citygml4j.ade.iur.model.urf.NumberOfHouseholdsProperty;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CensusBlockExporter implements ADEExporter {
    private final PreparedStatement ps;
    private final String module;
    private final UrbanFunctionExporter urbanFunctionExporter;

    public CensusBlockExporter(Connection connection, CityGMLExportHelper helper, ExportManager manager) throws CityGMLExportException, SQLException {
        String tableName = manager.getSchemaMapper().getTableName(ADETable.CENSUSBLOCK);
        CombinedProjectionFilter projectionFilter = helper.getCombinedProjectionFilter(tableName);
        module = UrbanFunctionModule.v1_3.getNamespaceURI();

        Table table = new Table(helper.getTableNameWithSchema(tableName));
        Table households = new Table(manager.getSchemaMapper().getTableName(ADETable.NUMBEROFHOUSEHOLDS_1));

        Select select = new Select().addProjection(table.getColumns("daytimepopulation", "daytimepopulationdensity",
                "numberofmainhouseholds", "numberofordinaryhouseholds"));
        if (projectionFilter.containsProperty("numberOfHouseholdsByOwnership", module)
                || projectionFilter.containsProperty("numberOfHouseholdsByStructure", module)) {
            select.addProjection(households.getColumns("censusblock_numberofhouse_id", "censusblock_numberofhou_id_1",
                    "class", "class_codespace", "number_"));
            Join join = JoinFactory.left(households, "censusblock_numberofhouse_id", ComparisonName.EQUAL_TO, table.getColumn("id"));
            join.addCondition(ComparisonFactory.equalTo(households.getColumn("censusblock_numberofhou_id_1"), table.getColumn("id")));
            join.setConditionOperationName(LogicalOperationName.OR);
            select.addJoin(join);
        }
        select.addSelection(ComparisonFactory.equalTo(table.getColumn("id"), new PlaceHolder<>()));
        ps = connection.prepareStatement(select.toString());

        urbanFunctionExporter = manager.getExporter(UrbanFunctionExporter.class);
    }

    public void doExport(CensusBlock censusBlock, long objectId, AbstractType<?> objectType, ProjectionFilter projectionFilter) throws CityGMLExportException, SQLException {
        ps.setLong(1, objectId);

        try (ResultSet rs = ps.executeQuery()) {
            boolean isInitialized = false;

            while (rs.next()) {
                if (!isInitialized) {
                    urbanFunctionExporter.doExport(censusBlock, objectId, objectType, projectionFilter);

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
