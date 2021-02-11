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
import org.citygml4j.ade.iur.model.urg.GenericGridCell;
import org.citygml4j.ade.iur.model.urg.KeyValuePair;
import org.citygml4j.ade.iur.model.urg.KeyValuePairProperty;
import org.citygml4j.model.gml.basicTypes.Code;
import org.citygml4j.model.gml.basicTypes.Measure;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GenericGridCellExporter implements StatisticalGridModuleExporter {
    private final PreparedStatement ps;
    private final StatisticalGridExporter statisticalGridExporter;

    public GenericGridCellExporter(Connection connection, CityGMLExportHelper helper, ExportManager manager) throws CityGMLExportException, SQLException {
        String tableName = manager.getSchemaMapper().getTableName(ADETable.STATISTICALGRID);
        CombinedProjectionFilter projectionFilter = helper.getCombinedProjectionFilter(tableName);

        statisticalGridExporter = manager.getExporter(StatisticalGridExporter.class);

        Table table = new Table(helper.getTableNameWithSchema(tableName));
        Table keyValuePair = new Table(helper.getTableNameWithSchema(manager.getSchemaMapper().getTableName(ADETable.KEYVALUEPAIR)));

        Select select = statisticalGridExporter.addProjection(new Select(), table, projectionFilter, "sg")
                .addProjection(keyValuePair.getColumns("key", "key_codespace", "codevalue", "codevalue_codespace",
                        "datevalue", "doublevalue", "intvalue", "measuredvalue", "measuredvalue_uom", "stringvalue", "urivalue"))
                .addJoin(JoinFactory.left(keyValuePair, "statisticalg_genericvalue_id", ComparisonName.EQUAL_TO, table.getColumn("id")))
                .addSelection(ComparisonFactory.equalTo(table.getColumn("id"), new PlaceHolder<>()));
        ps = connection.prepareStatement(select.toString());
    }

    public void doExport(GenericGridCell genericGridCell, long objectId, AbstractType<?> objectType, ProjectionFilter projectionFilter) throws CityGMLExportException, SQLException {
        ps.setLong(1, objectId);

        try (ResultSet rs = ps.executeQuery()) {
            boolean isInitialized = false;

            while (rs.next()) {
                if (!isInitialized) {
                    statisticalGridExporter.doExport(genericGridCell, projectionFilter, "sg", rs);
                    isInitialized = true;
                }

                KeyValuePair keyValuePair = new KeyValuePair();

                String key = rs.getString("key");
                if (!rs.wasNull()) {
                    Code code = new Code(key);
                    code.setCodeSpace(rs.getString("key_codespace"));
                    keyValuePair.setKey(code);
                } else
                    continue;

                String codeValue = rs.getString("codevalue");
                if (!rs.wasNull()) {
                    Code code = new Code(codeValue);
                    code.setCodeSpace(rs.getString("codevalue_codespace"));
                    keyValuePair.setCodeValue(code);
                }

                Date dateValue = rs.getDate("datevalue");
                if (!rs.wasNull())
                    keyValuePair.setDateValue(dateValue.toLocalDate());

                double doubleValue = rs.getDouble("doublevalue");
                if (!rs.wasNull())
                    keyValuePair.setDoubleValue(doubleValue);

                int intValue = rs.getInt("intvalue");
                if (!rs.wasNull())
                    keyValuePair.setIntValue(intValue);

                double measuredValue = rs.getDouble("measuredvalue");
                if (!rs.wasNull()) {
                    Measure measure = new Measure(measuredValue);
                    measure.setUom(rs.getString("measuredvalue_uom"));
                    keyValuePair.setMeasuredValue(measure);
                }

                String stringValue = rs.getString("stringvalue");
                if (!rs.wasNull())
                    keyValuePair.setStringValue(stringValue);

                String uriValue = rs.getString("urivalue");
                if (!rs.wasNull())
                    keyValuePair.setUriValue(uriValue);

                if (keyValuePair.isSetValue())
                    genericGridCell.getGenericValues().add(new KeyValuePairProperty(keyValuePair));
            }
        }
    }

    @Override
    public void close() throws CityGMLExportException, SQLException {
        ps.close();
    }
}
