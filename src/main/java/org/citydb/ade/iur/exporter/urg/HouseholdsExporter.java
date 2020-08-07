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
import org.citydb.sqlbuilder.select.join.Join;
import org.citydb.sqlbuilder.select.join.JoinFactory;
import org.citydb.sqlbuilder.select.operator.comparison.ComparisonFactory;
import org.citydb.sqlbuilder.select.operator.comparison.ComparisonName;
import org.citydb.sqlbuilder.select.operator.logical.LogicalOperationName;
import org.citygml4j.model.gml.basicTypes.Code;
import org.citygml4j.ade.iur.model.module.StatisticalGridModule;
import org.citygml4j.ade.iur.model.urg.Households;
import org.citygml4j.ade.iur.model.urg.NumberOfHouseholds;
import org.citygml4j.ade.iur.model.urg.NumberOfHouseholdsProperty;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class HouseholdsExporter implements ADEExporter {
    private final PreparedStatement ps;
    private final String module;
    private final StatisticalGridExporter statisticalGridExporter;

    public HouseholdsExporter(Connection connection, CityGMLExportHelper helper, ExportManager manager) throws CityGMLExportException, SQLException {
        String tableName = manager.getSchemaMapper().getTableName(ADETable.HOUSEHOLDS);
        CombinedProjectionFilter projectionFilter = helper.getCombinedProjectionFilter(tableName);
        module = StatisticalGridModule.v1_3.getNamespaceURI();

        Table table = new Table(helper.getTableNameWithSchema(tableName));
        Table households = new Table(manager.getSchemaMapper().getTableName(ADETable.NUMBEROFHOUSEHOLDS));

        Select select = new Select().addProjection(table.getColumns("numberofmainhousehold", "numberofordinaryhousehold"));
        if (projectionFilter.containsProperty("numberOfHouseholdsByOwnership", module)
                || projectionFilter.containsProperty("numberOfHouseholdsByStructure", module)) {
            select.addProjection(households.getColumns("households_numberofhouseh_id", "households_numberofhous_id_1",
                    "class", "class_codespace", "number_"));
            Join join = JoinFactory.left(households, "households_numberofhouseh_id", ComparisonName.EQUAL_TO, table.getColumn("id"));
            join.addCondition(ComparisonFactory.equalTo(households.getColumn("households_numberofhous_id_1"), table.getColumn("id")));
            join.setConditionOperationName(LogicalOperationName.OR);
            select.addJoin(join);
        }
        select.addSelection(ComparisonFactory.equalTo(table.getColumn("id"), new PlaceHolder<>()));
        ps = connection.prepareStatement(select.toString());

        statisticalGridExporter = manager.getExporter(StatisticalGridExporter.class);
    }

    public void doExport(Households households, long objectId, AbstractType<?> objectType, ProjectionFilter projectionFilter) throws CityGMLExportException, SQLException {
        ps.setLong(1, objectId);

        try (ResultSet rs = ps.executeQuery()) {
            boolean isInitialized = false;

            while (rs.next()) {
                if (!isInitialized) {
                    statisticalGridExporter.doExport(households, objectId, objectType, projectionFilter);

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
