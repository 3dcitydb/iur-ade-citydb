package org.citydb.ade.iur.exporter.urg;

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
import org.citygml4j.model.gml.basicTypes.Code;
import org.citygml4j.ade.iur.model.module.StatisticalGridModule;
import org.citygml4j.ade.iur.model.urg.LandPrice;
import org.citygml4j.ade.iur.model.urg.LandPricePerLandUse;
import org.citygml4j.ade.iur.model.urg.LandPricePerLandUseProperty;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LandPriceExporter implements ADEExporter {
    private final PreparedStatement ps;
    private final String module;
    private final StatisticalGridExporter statisticalGridExporter;

    public LandPriceExporter(Connection connection, CityGMLExportHelper helper, ExportManager manager) throws CityGMLExportException, SQLException {
        String tableName = manager.getSchemaMapper().getTableName(ADETable.LANDPRICEPERLANDUSE);
        module = StatisticalGridModule.v1_3.getNamespaceURI();

        Table table = new Table(helper.getTableNameWithSchema(tableName));
        Select select = new Select().addProjection(table.getColumns("currencyunit", "currencyunit_codespace", "landprice",
                "landuse", "landuse_codespace"))
                .addSelection(ComparisonFactory.equalTo(table.getColumn("statisticalgrid_landprice_id"), new PlaceHolder<>()));
        ps = connection.prepareStatement(select.toString());

        statisticalGridExporter = manager.getExporter(StatisticalGridExporter.class);
    }

    public void doExport(LandPrice landPrice, long objectId, AbstractType<?> objectType, ProjectionFilter projectionFilter) throws CityGMLExportException, SQLException {
        statisticalGridExporter.doExport(landPrice, objectId, objectType, projectionFilter);

        if (projectionFilter.containsProperty("landPrice", module)) {
            ps.setLong(1, objectId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LandPricePerLandUse landPricePerLandUse = new LandPricePerLandUse();

                    int price = rs.getInt("landprice");
                    if (!rs.wasNull())
                        landPricePerLandUse.setLandPrice(price);

                    String currencyUnit = rs.getString("currencyunit");
                    if (!rs.wasNull()) {
                        Code code = new Code(currencyUnit);
                        code.setCodeSpace(rs.getString("currencyunit_codespace"));
                        landPricePerLandUse.setCurrencyUnit(code);
                    }

                    String landUse = rs.getString("landuse");
                    if (!rs.wasNull()) {
                        Code code = new Code(landUse);
                        code.setCodeSpace(rs.getString("landuse_codespace"));
                        landPricePerLandUse.setLandUse(code);
                    }

                    landPrice.getLandPrices().add(new LandPricePerLandUseProperty(landPricePerLandUse));
                }
            }
        }
    }

    @Override
    public void close() throws CityGMLExportException, SQLException {
        ps.close();
    }
}