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
import org.citydb.sqlbuilder.select.join.JoinFactory;
import org.citydb.sqlbuilder.select.operator.comparison.ComparisonFactory;
import org.citydb.sqlbuilder.select.operator.comparison.ComparisonName;
import org.citygml4j.ade.iur.model.module.StatisticalGridModule;
import org.citygml4j.ade.iur.model.urg.LandPrice;
import org.citygml4j.ade.iur.model.urg.LandPricePerLandUse;
import org.citygml4j.ade.iur.model.urg.LandPricePerLandUseProperty;
import org.citygml4j.model.gml.basicTypes.Code;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LandPriceExporter implements StatisticalGridModuleExporter {
    private final PreparedStatement ps;
    private final String module;
    private final StatisticalGridExporter statisticalGridExporter;

    public LandPriceExporter(Connection connection, CityGMLExportHelper helper, ExportManager manager) throws CityGMLExportException, SQLException {
        String tableName = manager.getSchemaMapper().getTableName(ADETable.LANDPRICE);
        CombinedProjectionFilter projectionFilter = helper.getCombinedProjectionFilter(tableName);
        module = StatisticalGridModule.v1_4.getNamespaceURI();

        statisticalGridExporter = manager.getExporter(StatisticalGridExporter.class);

        Table table = new Table(helper.getTableNameWithSchema(tableName));
        Table statisticalGrid = new Table(helper.getTableNameWithSchema(manager.getSchemaMapper().getTableName(ADETable.STATISTICALGRID)));

        Select select = statisticalGridExporter.addProjection(new Select(), statisticalGrid, projectionFilter, "sg")
                .addJoin(JoinFactory.inner(statisticalGrid, "id", ComparisonName.EQUAL_TO, table.getColumn("id")))
                .addSelection(ComparisonFactory.equalTo(table.getColumn("id"), new PlaceHolder<>()));
        if (projectionFilter.containsProperty("currencyUnit", module))
            select.addProjection(table.getColumns("currencyunit", "currencyunit_codespace"));
        if (projectionFilter.containsProperty("landPrice", module)) {
            Table landPricePerLandUse = new Table(helper.getTableNameWithSchema(manager.getSchemaMapper().getTableName(ADETable.LANDPRICEPERLANDUSE)));
            select.addProjection(landPricePerLandUse.getColumn("id", "lpid"))
                    .addProjection(landPricePerLandUse.getColumns("landprice", "landuse", "landuse_codespace"))
                    .addJoin(JoinFactory.left(landPricePerLandUse, "landprice_landprice_id", ComparisonName.EQUAL_TO, table.getColumn("id")));
        }
        ps = connection.prepareStatement(select.toString());
    }

    public void doExport(LandPrice landPrice, long objectId, AbstractType<?> objectType, ProjectionFilter projectionFilter) throws CityGMLExportException, SQLException {
        ps.setLong(1, objectId);

        try (ResultSet rs = ps.executeQuery()) {
            boolean isInitialized = false;

            while (rs.next()) {
                if (!isInitialized) {
                    statisticalGridExporter.doExport(landPrice, projectionFilter, "sg", rs);

                    if (projectionFilter.containsProperty("currencyUnit", module)) {
                        String currencyUnit = rs.getString("currencyunit");
                        if (!rs.wasNull()) {
                            Code code = new Code(currencyUnit);
                            code.setCodeSpace(rs.getString("currencyunit_codespace"));
                            landPrice.setCurrencyUnit(code);
                        }
                    }

                    isInitialized = true;
                }

                if (projectionFilter.containsProperty("landPrice", module)) {
                    rs.getLong("lpid");
                    if (rs.wasNull())
                        continue;

                    LandPricePerLandUse landPricePerLandUse = new LandPricePerLandUse();

                    int price = rs.getInt("landprice");
                    if (!rs.wasNull())
                        landPricePerLandUse.setLandPrice(price);

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
