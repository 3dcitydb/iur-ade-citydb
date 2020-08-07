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
import org.citygml4j.model.gml.basicTypes.Code;
import org.citygml4j.model.gml.basicTypes.Measure;
import org.citygml4j.ade.iur.model.module.UrbanFunctionModule;
import org.citygml4j.ade.iur.model.urf.DevelopmentProject;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DevelopmentProjectExporter implements ADEExporter {
    private final PreparedStatement ps;
    private final String module;
    private final UrbanFunctionExporter urbanFunctionExporter;

    public DevelopmentProjectExporter(Connection connection, CityGMLExportHelper helper, ExportManager manager) throws CityGMLExportException, SQLException {
        String tableName = manager.getSchemaMapper().getTableName(ADETable.DEVELOPMENTPROJECT);
        module = UrbanFunctionModule.v1_3.getNamespaceURI();

        Table table = new Table(helper.getTableNameWithSchema(tableName));
        Select select = new Select().addProjection(table.getColumns("benefitarea", "benefitarea_uom", "benefitperiod",
                "completedarea", "completedarea_uom", "cost", "dateofdecision", "dateofdesignationfortemporar",
                "mainpurpose", "mainpurpose_codespace", "ongoingarea", "ongoingarea_uom", "plannedarea", "plannedarea_uom",
                "status", "status_codespace"))
                .addSelection(ComparisonFactory.equalTo(table.getColumn("id"), new PlaceHolder<>()));
        ps = connection.prepareStatement(select.toString());

        urbanFunctionExporter = manager.getExporter(UrbanFunctionExporter.class);
    }

    public void doExport(DevelopmentProject developmentProject, long objectId, AbstractType<?> objectType, ProjectionFilter projectionFilter) throws CityGMLExportException, SQLException {
        ps.setLong(1, objectId);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                urbanFunctionExporter.doExport(developmentProject, objectId, objectType, projectionFilter);

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
            }
        }
    }

    @Override
    public void close() throws CityGMLExportException, SQLException {
        ps.close();
    }
}
