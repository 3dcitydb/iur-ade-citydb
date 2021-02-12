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
import org.citygml4j.ade.iur.model.module.StatisticalGridModule;
import org.citygml4j.ade.iur.model.urg.LandUseDiversion;
import org.citygml4j.ade.iur.model.urg.NumberOfAnnualDiversions;
import org.citygml4j.ade.iur.model.urg.NumberOfAnnualDiversionsProperty;
import org.citygml4j.model.gml.basicTypes.Measure;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Year;

public class LandUseDiversionExporter implements ADEExporter {
    private final PreparedStatement ps;
    private final String module;
    private final StatisticalGridExporter statisticalGridExporter;

    public LandUseDiversionExporter(Connection connection, CityGMLExportHelper helper, ExportManager manager) throws CityGMLExportException, SQLException {
        String tableName = manager.getSchemaMapper().getTableName(ADETable.NUMBEROFANNUALDIVERSIO);
        module = StatisticalGridModule.v1_3.getNamespaceURI();

        Table table = new Table(helper.getTableNameWithSchema(tableName));
        Select select = new Select().addProjection(table.getColumns("area", "area_uom", "count", "year"))
                .addSelection(ComparisonFactory.equalTo(table.getColumn("statisticalg_numberofannu_id"), new PlaceHolder<>()));
        ps = connection.prepareStatement(select.toString());

        statisticalGridExporter = manager.getExporter(StatisticalGridExporter.class);
    }

    public void doExport(LandUseDiversion landUseDiversion, long objectId, AbstractType<?> objectType, ProjectionFilter projectionFilter) throws CityGMLExportException, SQLException {
        statisticalGridExporter.doExport(landUseDiversion, objectId, objectType, projectionFilter);

        if (projectionFilter.containsProperty("numberOfAnnuallDiversion", module)) {
            ps.setLong(1, objectId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    NumberOfAnnualDiversions number = new NumberOfAnnualDiversions();

                    double area = rs.getDouble("area");
                    if (!rs.wasNull()) {
                        Measure measure = new Measure(area);
                        measure.setUom(rs.getString("area_uom"));
                        number.setArea(measure);
                    }

                    int count = rs.getInt("count");
                    if (!rs.wasNull())
                        number.setCount(count);

                    Date year = rs.getDate("year");
                    if (!rs.wasNull())
                        number.setYear(Year.of(year.toLocalDate().getYear()));

                    landUseDiversion.getNumberOfAnnualDiversions().add(new NumberOfAnnualDiversionsProperty(number));
                }
            }
        }
    }

    @Override
    public void close() throws CityGMLExportException, SQLException {
        ps.close();
    }
}
