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
import org.citygml4j.ade.iur.model.urg.*;
import org.citygml4j.model.gml.basicTypes.Measure;

import java.sql.*;
import java.time.Year;
import java.util.HashSet;
import java.util.Set;

public class LandUseDiversionExporter implements StatisticalGridModuleExporter {
    private final PreparedStatement ps;
    private final String module;
    private final StatisticalGridExporter statisticalGridExporter;

    public LandUseDiversionExporter(Connection connection, CityGMLExportHelper helper, ExportManager manager) throws CityGMLExportException, SQLException {
        String tableName = manager.getSchemaMapper().getTableName(ADETable.LANDUSEDIVERSION);
        CombinedProjectionFilter projectionFilter = helper.getCombinedProjectionFilter(tableName);
        module = StatisticalGridModule.v1_4.getNamespaceURI();

        statisticalGridExporter = manager.getExporter(StatisticalGridExporter.class);

        Table table = new Table(helper.getTableNameWithSchema(tableName));
        Table statisticalGrid = new Table(helper.getTableNameWithSchema(manager.getSchemaMapper().getTableName(ADETable.STATISTICALGRID)));

        Select select = statisticalGridExporter.addProjection(new Select(), statisticalGrid, projectionFilter, "sg")
                .addJoin(JoinFactory.inner(statisticalGrid, "id", ComparisonName.EQUAL_TO, table.getColumn("id")))
                .addSelection(ComparisonFactory.equalTo(table.getColumn("id"), new PlaceHolder<>()));
        if (projectionFilter.containsProperty("numberOfAnnualDiversion", module)) {
            Table number = new Table(helper.getTableNameWithSchema(manager.getSchemaMapper().getTableName(ADETable.NUMBEROFANNUALDIVERSIO)));
            select.addProjection(number.getColumn("id", "nid"), number.getColumn("count"), number.getColumn("year", "nyear"))
                    .addJoin(JoinFactory.left(number, "landusediver_numberofannu_id", ComparisonName.EQUAL_TO, table.getColumn("id")));
        }
        if (projectionFilter.containsProperty("areaOfAnnualDiversion", module)) {
            Table area = new Table(helper.getTableNameWithSchema(manager.getSchemaMapper().getTableName(ADETable.AREAOFANNUALDIVERSIONS)));
            select.addProjection(area.getColumn("id", "aid"), area.getColumn("year", "ayear"))
                    .addProjection(area.getColumns("area", "area_uom"))
                    .addJoin(JoinFactory.left(area, "landusediver_areaofannual_id", ComparisonName.EQUAL_TO, table.getColumn("id")));
        }
        ps = connection.prepareStatement(select.toString());
    }

    public void doExport(LandUseDiversion landUseDiversion, long objectId, AbstractType<?> objectType, ProjectionFilter projectionFilter) throws CityGMLExportException, SQLException {
        ps.setLong(1, objectId);

        try (ResultSet rs = ps.executeQuery()) {
            boolean isInitialized = false;
            Set<Long> numberOfAnnualDiversions = new HashSet<>();
            Set<Long> areaOfAnnualDiversions = new HashSet<>();

            while (rs.next()) {
                if (!isInitialized) {
                    statisticalGridExporter.doExport(landUseDiversion, projectionFilter, "sg", rs);
                    isInitialized = true;
                }

                if (projectionFilter.containsProperty("numberOfAnnuallDiversion", module)) {
                    long numberId = rs.getLong("nid");
                    if (!rs.wasNull() && numberOfAnnualDiversions.add(numberId)) {
                        NumberOfAnnualDiversions number = new NumberOfAnnualDiversions();

                        int count = rs.getInt("count");
                        if (!rs.wasNull())
                            number.setCount(count);

                        Date year = rs.getDate("nyear");
                        if (!rs.wasNull())
                            number.setYear(Year.of(year.toLocalDate().getYear()));

                        landUseDiversion.getNumberOfAnnualDiversions().add(new NumberOfAnnualDiversionsProperty(number));
                    }
                }

                if (projectionFilter.containsProperty("areaOfAnnualDiversion", module)) {
                    long areaId = rs.getLong("aid");
                    if (!rs.wasNull() && areaOfAnnualDiversions.add(areaId)) {
                        AreaOfAnnualDiversions area = new AreaOfAnnualDiversions();

                        double areaValue = rs.getDouble("area");
                        if (!rs.wasNull()) {
                            Measure measure = new Measure(areaValue);
                            measure.setUom(rs.getString("area_uom"));
                            area.setArea(measure);
                        }

                        Date year = rs.getDate("ayear");
                        if (!rs.wasNull())
                            area.setYear(Year.of(year.toLocalDate().getYear()));

                        landUseDiversion.getAreaOfAnnualDiversions().add(new AreaOfAnnualDiversionsProperty(area));
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
