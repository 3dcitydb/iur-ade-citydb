/*
 * 3D City Database - The Open Source CityGML Database
 * https://www.3dcitydb.org/
 *
 * Copyright 2013 - 2024
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

package org.citydb.ade.iur.exporter.urf;

import org.citydb.ade.iur.exporter.ExportManager;
import org.citydb.ade.iur.schema.ADETable;
import org.citydb.core.ade.exporter.CityGMLExportHelper;
import org.citydb.core.database.schema.mapping.AbstractType;
import org.citydb.core.database.schema.mapping.MappingConstants;
import org.citydb.core.operation.exporter.CityGMLExportException;
import org.citydb.core.query.filter.projection.CombinedProjectionFilter;
import org.citydb.core.query.filter.projection.ProjectionFilter;
import org.citydb.sqlbuilder.expression.PlaceHolder;
import org.citydb.sqlbuilder.schema.Table;
import org.citydb.sqlbuilder.select.Select;
import org.citydb.sqlbuilder.select.join.JoinFactory;
import org.citydb.sqlbuilder.select.operator.comparison.ComparisonFactory;
import org.citydb.sqlbuilder.select.operator.comparison.ComparisonName;
import org.citygml4j.ade.iur.model.module.UrbanFunctionModule;
import org.citygml4j.ade.iur.model.urf.DisasterDamage;
import org.citygml4j.ade.iur.model.urf.TargetProperty;
import org.citygml4j.model.gml.basicTypes.Measure;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DisasterDamageExporter implements UrbanFunctionModuleExporter {
    private final PreparedStatement ps;
    private final String module;
    private final UrbanFunctionExporter urbanFunctionExporter;

    public DisasterDamageExporter(Connection connection, CityGMLExportHelper helper, ExportManager manager) throws CityGMLExportException, SQLException {
        String tableName = manager.getSchemaMapper().getTableName(ADETable.DISASTERDAMAGE);
        CombinedProjectionFilter projectionFilter = helper.getCombinedProjectionFilter(tableName);
        module = UrbanFunctionModule.v1_4.getNamespaceURI();

        urbanFunctionExporter = manager.getExporter(UrbanFunctionExporter.class);

        Table table = new Table(helper.getTableNameWithSchema(tableName));
        Table urbanFunction = new Table(helper.getTableNameWithSchema(manager.getSchemaMapper().getTableName(ADETable.URBANFUNCTION)));

        Select select = new Select().addJoin(JoinFactory.inner(urbanFunction, "id", ComparisonName.EQUAL_TO, table.getColumn("id")))
                .addSelection(ComparisonFactory.equalTo(table.getColumn("id"), new PlaceHolder<>()));
        urbanFunctionExporter.addProjection(select, urbanFunction, projectionFilter, "uf");
        if (projectionFilter.containsProperty("damagedArea", module))
            select.addProjection(table.getColumns("damagedarea", "damagedarea_uom"));
        if (projectionFilter.containsProperty("numberOfDamagedHouses", module))
            select.addProjection(table.getColumn("numberofdamagedhouses"));
        if (projectionFilter.containsProperty("numberOfHousesFloodedAboveFloorLevel", module))
            select.addProjection(table.getColumn("numberofhousesfloodedabovefl"));
        if (projectionFilter.containsProperty("numberOfHousesFloodedBelowFloorLevel", module))
            select.addProjection(table.getColumn("numberofhousesfloodedbelowfl"));
        if (projectionFilter.containsProperty("maximumRainfallPerHour", module))
            select.addProjection(table.getColumn("maximumrainfallperhour"));
        if (projectionFilter.containsProperty("totalRainfall", module))
            select.addProjection(table.getColumn("totalrainfall"));
        if (projectionFilter.containsProperty("target", module)) {
            Table targets = new Table(helper.getTableNameWithSchema(manager.getSchemaMapper().getTableName(ADETable.URBANFUNC_TO_CITYOBJEC)));
            Table cityObject = new Table(helper.getTableNameWithSchema(MappingConstants.CITYOBJECT));
            select.addProjection(cityObject.getColumn("gmlid"))
                    .addJoin(JoinFactory.left(targets, "urbanfunction_id", ComparisonName.EQUAL_TO, table.getColumn("id")))
                    .addJoin(JoinFactory.left(cityObject, "id", ComparisonName.EQUAL_TO, targets.getColumn("cityobject_id")));
        }
        ps = connection.prepareStatement(select.toString());
    }

    public void doExport(DisasterDamage disasterDamage, long objectId, AbstractType<?> objectType, ProjectionFilter projectionFilter) throws CityGMLExportException, SQLException {
        ps.setLong(1, objectId);

        try (ResultSet rs = ps.executeQuery()) {
            boolean isInitialized = false;

            while (rs.next()) {
                if (!isInitialized) {
                    urbanFunctionExporter.doExport(disasterDamage, projectionFilter, "uf", rs);

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

                    isInitialized = true;
                }

                if (projectionFilter.containsProperty("target", module)) {
                    String gmlId = rs.getString("gmlid");
                    if (gmlId != null)
                        disasterDamage.getTargets().add(new TargetProperty("#" + gmlId));
                }
            }
        }
    }

    @Override
    public void close() throws CityGMLExportException, SQLException {
        ps.close();
    }
}
