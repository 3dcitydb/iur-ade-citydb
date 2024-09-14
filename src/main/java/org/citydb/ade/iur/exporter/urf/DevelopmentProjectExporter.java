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
import org.citygml4j.ade.iur.model.urf.DevelopmentProject;
import org.citygml4j.ade.iur.model.urf.TargetProperty;
import org.citygml4j.model.gml.basicTypes.Code;
import org.citygml4j.model.gml.basicTypes.Measure;

import java.sql.*;

public class DevelopmentProjectExporter implements UrbanFunctionModuleExporter {
    private final PreparedStatement ps;
    private final String module;
    private final UrbanFunctionExporter urbanFunctionExporter;

    public DevelopmentProjectExporter(Connection connection, CityGMLExportHelper helper, ExportManager manager) throws CityGMLExportException, SQLException {
        String tableName = manager.getSchemaMapper().getTableName(ADETable.DEVELOPMENTPROJECT);
        CombinedProjectionFilter projectionFilter = helper.getCombinedProjectionFilter(tableName);
        module = UrbanFunctionModule.v1_4.getNamespaceURI();

        urbanFunctionExporter = manager.getExporter(UrbanFunctionExporter.class);

        Table table = new Table(helper.getTableNameWithSchema(tableName));
        Table urbanFunction = new Table(helper.getTableNameWithSchema(manager.getSchemaMapper().getTableName(ADETable.URBANFUNCTION)));

        Select select = new Select().addJoin(JoinFactory.inner(urbanFunction, "id", ComparisonName.EQUAL_TO, table.getColumn("id")))
                .addSelection(ComparisonFactory.equalTo(table.getColumn("id"), new PlaceHolder<>()));
        urbanFunctionExporter.addProjection(select, urbanFunction, projectionFilter, "uf");
        if (projectionFilter.containsProperty("status", module))
            select.addProjection(table.getColumns("status", "status_codespace"));
        if (projectionFilter.containsProperty("mainPurpose", module))
            select.addProjection(table.getColumns("mainpurpose", "mainpurpose_codespace"));
        if (projectionFilter.containsProperty("benefitArea", module))
            select.addProjection(table.getColumns("benefitarea", "benefitarea_uom"));
        if (projectionFilter.containsProperty("benefitPeriod", module))
            select.addProjection(table.getColumn("benefitperiod"));
        if (projectionFilter.containsProperty("cost", module))
            select.addProjection(table.getColumn("cost"));
        if (projectionFilter.containsProperty("plannedArea", module))
            select.addProjection(table.getColumns("plannedarea", "plannedarea_uom"));
        if (projectionFilter.containsProperty("ongoingArea", module))
            select.addProjection(table.getColumns("ongoingarea", "ongoingarea_uom"));
        if (projectionFilter.containsProperty("completedArea", module))
            select.addProjection(table.getColumns("completedarea", "completedarea_uom"));
        if (projectionFilter.containsProperty("dateOfDecision", module))
            select.addProjection(table.getColumn("dateofdecision"));
        if (projectionFilter.containsProperty("dateOfDesignationForTemporaryReplotting", module))
            select.addProjection(table.getColumn("dateofdesignationfortemporar"));
        if (projectionFilter.containsProperty("target", module)) {
            Table targets = new Table(helper.getTableNameWithSchema(manager.getSchemaMapper().getTableName(ADETable.URBANFUNC_TO_CITYOBJEC)));
            Table cityObject = new Table(helper.getTableNameWithSchema(MappingConstants.CITYOBJECT));
            select.addProjection(cityObject.getColumn("gmlid"))
                    .addJoin(JoinFactory.left(targets, "urbanfunction_id", ComparisonName.EQUAL_TO, table.getColumn("id")))
                    .addJoin(JoinFactory.left(cityObject, "id", ComparisonName.EQUAL_TO, targets.getColumn("cityobject_id")));
        }
        ps = connection.prepareStatement(select.toString());
    }

    public void doExport(DevelopmentProject developmentProject, long objectId, AbstractType<?> objectType, ProjectionFilter projectionFilter) throws CityGMLExportException, SQLException {
        ps.setLong(1, objectId);

        try (ResultSet rs = ps.executeQuery()) {
            boolean isInitialized = false;

            while (rs.next()) {
                if (!isInitialized) {
                    urbanFunctionExporter.doExport(developmentProject, projectionFilter, "uf", rs);

                    if (projectionFilter.containsProperty("status", module)) {
                        String status = rs.getString("status");
                        if (!rs.wasNull()) {
                            Code code = new Code(status);
                            code.setCodeSpace(rs.getString("status_codespace"));
                            developmentProject.setStatus(code);
                        }
                    }

                    if (projectionFilter.containsProperty("mainPurpose", module)) {
                        String status = rs.getString("mainpurpose");
                        if (!rs.wasNull()) {
                            Code code = new Code(status);
                            code.setCodeSpace(rs.getString("mainpurpose_codespace"));
                            developmentProject.setStatus(code);
                        }
                    }

                    if (projectionFilter.containsProperty("benefitArea", module)) {
                        double benefitArea = rs.getDouble("benefitarea");
                        if (!rs.wasNull()) {
                            Measure measure = new Measure(benefitArea);
                            measure.setUom(rs.getString("benefitarea_uom"));
                            developmentProject.setBenefitArea(measure);
                        }
                    }

                    if (projectionFilter.containsProperty("benefitPeriod", module))
                        developmentProject.setBenefitPeriod(rs.getString("benefitperiod"));

                    if (projectionFilter.containsProperty("cost", module)) {
                        int cost = rs.getInt("cost");
                        if (!rs.wasNull())
                            developmentProject.setCost(cost);
                    }

                    if (projectionFilter.containsProperty("plannedArea", module)) {
                        double plannedArea = rs.getDouble("plannedarea");
                        if (!rs.wasNull()) {
                            Measure measure = new Measure(plannedArea);
                            measure.setUom(rs.getString("plannedarea_uom"));
                            developmentProject.setPlannedArea(measure);
                        }
                    }

                    if (projectionFilter.containsProperty("ongoingArea", module)) {
                        double ongoingArea = rs.getDouble("ongoingarea");
                        if (!rs.wasNull()) {
                            Measure measure = new Measure(ongoingArea);
                            measure.setUom(rs.getString("ongoingarea_uom"));
                            developmentProject.setOngoingArea(measure);
                        }
                    }

                    if (projectionFilter.containsProperty("completedArea", module)) {
                        double completedArea = rs.getDouble("completedarea");
                        if (!rs.wasNull()) {
                            Measure measure = new Measure(completedArea);
                            measure.setUom(rs.getString("completedarea_uom"));
                            developmentProject.setCompletedArea(measure);
                        }
                    }

                    if (projectionFilter.containsProperty("dateOfDecision", module)) {
                        Date dateOfDecision = rs.getDate("dateofdecision");
                        if (!rs.wasNull())
                            developmentProject.setDateOfDecision(dateOfDecision.toLocalDate());
                    }

                    if (projectionFilter.containsProperty("dateOfDesignationForTemporaryReplotting", module)) {
                        Date dateOfDesignationForTemporaryReplotting = rs.getDate("dateofdesignationfortemporar");
                        if (!rs.wasNull())
                            developmentProject.setDateOfDesignationForTemporaryReplotting(dateOfDesignationForTemporaryReplotting.toLocalDate());
                    }

                    isInitialized = true;
                }

                if (projectionFilter.containsProperty("target", module)) {
                    String gmlId = rs.getString("gmlid");
                    if (gmlId != null)
                        developmentProject.getTargets().add(new TargetProperty("#" + gmlId));
                }
            }
        }
    }

    @Override
    public void close() throws CityGMLExportException, SQLException {
        ps.close();
    }
}
