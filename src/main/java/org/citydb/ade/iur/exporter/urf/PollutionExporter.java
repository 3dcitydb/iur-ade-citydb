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
import org.citygml4j.ade.iur.model.urf.Pollution;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PollutionExporter implements ADEExporter {
    private final PreparedStatement ps;
    private final String module;
    private final UrbanFunctionExporter urbanFunctionExporter;

    public PollutionExporter(Connection connection, CityGMLExportHelper helper, ExportManager manager) throws CityGMLExportException, SQLException {
        String tableName = manager.getSchemaMapper().getTableName(ADETable.POLLUTION);
        module = UrbanFunctionModule.v1_3.getNamespaceURI();

        Table table = new Table(helper.getTableNameWithSchema(tableName));
        Select select = new Select().addProjection(table.getColumns("cause", "damagedarea", "damagedarea_uom"))
                .addSelection(ComparisonFactory.equalTo(table.getColumn("id"), new PlaceHolder<>()));
        ps = connection.prepareStatement(select.toString());

        urbanFunctionExporter = manager.getExporter(UrbanFunctionExporter.class);
    }

    public void doExport(Pollution pollution, long objectId, AbstractType<?> objectType, ProjectionFilter projectionFilter) throws CityGMLExportException, SQLException {
        ps.setLong(1, objectId);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                urbanFunctionExporter.doExport(pollution, objectId, objectType, projectionFilter);

                if (projectionFilter.containsProperty("cause", module))
                    pollution.setCause(rs.getString("cause"));

                if (projectionFilter.containsProperty("damagedArea", module)) {
                    double damagedArea = rs.getInt("damagedarea");
                    if (!rs.wasNull()) {
                        Measure measure = new Measure(damagedArea);
                        measure.setUom(rs.getString("damagedarea_uom"));
                        pollution.setDamagedArea(measure);
                    }
                }
            }
        }
    }

    @Override
    public void close() throws CityGMLExportException, SQLException {
        ps.close();
    }
}
