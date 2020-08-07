package org.citydb.ade.iur.exporter.urf;

import org.citydb.ade.exporter.ADEExporter;
import org.citydb.ade.exporter.CityGMLExportHelper;
import org.citydb.ade.iur.exporter.ExportManager;
import org.citydb.ade.iur.schema.ADETable;
import org.citydb.citygml.exporter.CityGMLExportException;
import org.citydb.database.schema.mapping.AbstractType;
import org.citydb.query.filter.projection.ProjectionFilter;
import org.citydb.sqlbuilder.expression.PlaceHolder;
import org.citydb.sqlbuilder.schema.Table;
import org.citydb.sqlbuilder.select.Select;
import org.citydb.sqlbuilder.select.operator.comparison.ComparisonFactory;
import org.citygml4j.model.gml.basicTypes.Measure;
import org.citygml4j.ade.iur.model.module.UrbanFunctionModule;
import org.citygml4j.ade.iur.model.urf.DisasterDamage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DisasterDamageExporter implements ADEExporter {
    private final PreparedStatement ps;
    private final String module;
    private final UrbanFunctionExporter urbanFunctionExporter;

    public DisasterDamageExporter(Connection connection, CityGMLExportHelper helper, ExportManager manager) throws CityGMLExportException, SQLException {
        String tableName = manager.getSchemaMapper().getTableName(ADETable.DISASTERDAMAGE);
        module = UrbanFunctionModule.v1_3.getNamespaceURI();

        Table table = new Table(helper.getTableNameWithSchema(tableName));
        Select select = new Select().addProjection(table.getColumns("damagedarea", "damagedarea_uom", "maximumrainfallperhour",
                "numberofdamagedhouses", "numberofhousesfloodedabovefl", "numberofhousesfloodedbelowfl", "totalrainfall"))
                .addSelection(ComparisonFactory.equalTo(table.getColumn("id"), new PlaceHolder<>()));
        ps = connection.prepareStatement(select.toString());

        urbanFunctionExporter = manager.getExporter(UrbanFunctionExporter.class);
    }

    public void doExport(DisasterDamage disasterDamage, long objectId, AbstractType<?> objectType, ProjectionFilter projectionFilter) throws CityGMLExportException, SQLException {
        ps.setLong(1, objectId);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                urbanFunctionExporter.doExport(disasterDamage, objectId, objectType, projectionFilter);

                if (projectionFilter.containsProperty("damagedArea", module)) {
                    double damagedArea = rs.getDouble("damagedarea");
                    if (!rs.wasNull()) {
                        Measure measure = new Measure(damagedArea);
                        measure.setUom(rs.getString("damagedarea_uom"));
                        disasterDamage.setDamagedArea(measure);
                    }
                }

                if (projectionFilter.containsProperty("numberOfDamagedHouses", module)) {
                    int numberOfDamagedHouses = rs.getInt("numberofdamagedhouses");
                    if (!rs.wasNull())
                        disasterDamage.setNumberOfDamagedHouses(numberOfDamagedHouses);
                }

                if (projectionFilter.containsProperty("numberOfHousesFloodedAboveFloorLevel", module)) {
                    int numberOfHousesFloodedAboveFloorLevel = rs.getInt("numberofhousesfloodedabovefl");
                    if (!rs.wasNull())
                        disasterDamage.setNumberOfHousesFloodedAboveFloorLevel(numberOfHousesFloodedAboveFloorLevel);
                }

                if (projectionFilter.containsProperty("numberOfHousesFloodedBelowFloorLevel", module)) {
                    int numberOfHousesFloodedBelowFloorLevel = rs.getInt("numberofhousesfloodedbelowfl");
                    if (!rs.wasNull())
                        disasterDamage.setNumberOfHousesFloodedBelowFloorLevel(numberOfHousesFloodedBelowFloorLevel);
                }

                if (projectionFilter.containsProperty("maximumRainfallPerHour", module)) {
                    int maximumRainfallPerHour = rs.getInt("maximumrainfallperhour");
                    if (!rs.wasNull())
                        disasterDamage.setMaximumRainfallPerHour(maximumRainfallPerHour);
                }

                if (projectionFilter.containsProperty("totalRainfall", module)) {
                    int totalRainfall = rs.getInt("totalrainfall");
                    if (!rs.wasNull())
                        disasterDamage.setTotalRainfall(totalRainfall);
                }
            }
        }
    }

    @Override
    public void close() throws CityGMLExportException, SQLException {
        ps.close();
    }
}
