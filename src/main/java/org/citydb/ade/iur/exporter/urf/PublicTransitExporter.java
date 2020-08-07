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
import org.citygml4j.ade.iur.model.urf.PublicTransit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PublicTransitExporter implements ADEExporter {
    private final PreparedStatement ps;
    private final UrbanFunctionExporter urbanFunctionExporter;

    public PublicTransitExporter(Connection connection, CityGMLExportHelper helper, ExportManager manager) throws CityGMLExportException, SQLException {
        String tableName = manager.getSchemaMapper().getTableName(ADETable.PUBLICTRANSIT);

        Table table = new Table(helper.getTableNameWithSchema(tableName));
        Select select = new Select().addProjection(table.getColumns("companyname", "frequencyofservice",
                "numberofcustomers", "routename", "sectionname"))
                .addSelection(ComparisonFactory.equalTo(table.getColumn("id"), new PlaceHolder<>()));
        ps = connection.prepareStatement(select.toString());

        urbanFunctionExporter = manager.getExporter(UrbanFunctionExporter.class);
    }

    public void doExport(PublicTransit publicTransit, long objectId, AbstractType<?> objectType, ProjectionFilter projectionFilter) throws CityGMLExportException, SQLException {
        ps.setLong(1, objectId);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                urbanFunctionExporter.doExport(publicTransit, objectId, objectType, projectionFilter);

                publicTransit.setCompanyName(rs.getString("companyname"));
                publicTransit.setRouteName(rs.getString("routename"));
                publicTransit.setSectionName(rs.getString("sectionname"));

                int frequencyOfService = rs.getInt("frequencyofservice");
                if (!rs.wasNull())
                    publicTransit.setFrequencyOfService(frequencyOfService);

                double numberOfCustomers = rs.getDouble("numberofcustomers");
                if (!rs.wasNull())
                    publicTransit.setNumberOfCustomers(numberOfCustomers);
            }
        }
    }

    @Override
    public void close() throws CityGMLExportException, SQLException {
        ps.close();
    }
}
