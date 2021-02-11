package org.citydb.ade.iur.exporter.urt;

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
import org.citygml4j.ade.iur.model.urt.Calendar;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CalendarExporter implements PublicTransitModuleExporter {
    private final PreparedStatement ps;
    private final PublicTransitExporter publicTransitExporter;

    public CalendarExporter(Connection connection, CityGMLExportHelper helper, ExportManager manager) throws CityGMLExportException, SQLException {
        String tableName = manager.getSchemaMapper().getTableName(ADETable.CALENDAR);
        CombinedProjectionFilter projectionFilter = helper.getCombinedProjectionFilter(tableName);

        publicTransitExporter = manager.getExporter(PublicTransitExporter.class);

        Table table = new Table(helper.getTableNameWithSchema(tableName));
        Table publicTransit = new Table(helper.getTableNameWithSchema(manager.getSchemaMapper().getTableName(ADETable.PUBLICTRANSIT)));

        Select select = new Select().addProjection(table.getColumns("monday", "tuesday", "wednesday", "thursday",
                "friday", "saturday", "sunday", "startdate", "enddate"))
                .addJoin(JoinFactory.inner(publicTransit, "id", ComparisonName.EQUAL_TO, table.getColumn("id")))
                .addSelection(ComparisonFactory.equalTo(table.getColumn("id"), new PlaceHolder<>()));
        publicTransitExporter.addProjection(select, publicTransit, projectionFilter, "pt");
        ps = connection.prepareStatement(select.toString());
    }

    public void doExport(Calendar calendar, long objectId, AbstractType<?> objectType, ProjectionFilter projectionFilter) throws CityGMLExportException, SQLException {
        ps.setLong(1, objectId);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                publicTransitExporter.doExport(calendar, projectionFilter, "pt", rs);

                calendar.setMonday(rs.getBoolean("monday"));
                calendar.setTuesday(rs.getBoolean("tuesday"));
                calendar.setWednesday(rs.getBoolean("wednesday"));
                calendar.setThursday(rs.getBoolean("thursday"));
                calendar.setFriday(rs.getBoolean("friday"));
                calendar.setSaturday(rs.getBoolean("saturday"));
                calendar.setSunday(rs.getBoolean("sunday"));

                Date startDate = rs.getDate("startdate");
                if (!rs.wasNull())
                    calendar.setStartDate(startDate.toLocalDate());

                Date endDate = rs.getDate("enddate");
                if (!rs.wasNull())
                    calendar.setEndDate(endDate.toLocalDate());
            }
        }
    }

    @Override
    public void close() throws CityGMLExportException, SQLException {
        ps.close();
    }
}
