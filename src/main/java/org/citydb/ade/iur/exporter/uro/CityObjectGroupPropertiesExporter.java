package org.citydb.ade.iur.exporter.uro;

import org.citydb.ade.exporter.ADEExporter;
import org.citydb.ade.exporter.CityGMLExportHelper;
import org.citydb.ade.iur.exporter.ExportManager;
import org.citydb.ade.iur.schema.ADETable;
import org.citydb.citygml.exporter.CityGMLExportException;
import org.citydb.database.schema.mapping.FeatureType;
import org.citydb.query.filter.projection.ProjectionFilter;
import org.citydb.sqlbuilder.expression.PlaceHolder;
import org.citydb.sqlbuilder.schema.Table;
import org.citydb.sqlbuilder.select.Select;
import org.citydb.sqlbuilder.select.operator.comparison.ComparisonFactory;
import org.citygml4j.model.citygml.cityobjectgroup.CityObjectGroup;
import org.citygml4j.model.gml.basicTypes.Code;
import org.citygml4j.ade.iur.model.module.UrbanObjectModule;
import org.citygml4j.ade.iur.model.uro.FiscalYearOfPublicationProperty;
import org.citygml4j.ade.iur.model.uro.LanguageProperty;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Year;

public class CityObjectGroupPropertiesExporter implements ADEExporter {
    private final PreparedStatement ps;
    private final String module;

    public CityObjectGroupPropertiesExporter(Connection connection, CityGMLExportHelper helper, ExportManager manager) throws CityGMLExportException, SQLException {
        String tableName = manager.getSchemaMapper().getTableName(ADETable.CITYOBJECTGROUP);
        module = UrbanObjectModule.v1_3.getNamespaceURI();

        Table table = new Table(helper.getTableNameWithSchema(tableName));
        Select select = new Select().addProjection(table.getColumns("fiscalyearofpublication", "language", "language_codespace"))
                .addSelection(ComparisonFactory.equalTo(table.getColumn("id"), new PlaceHolder<>()));
        ps = connection.prepareStatement(select.toString());
    }

    public void doExport(CityObjectGroup parent, long parentId, FeatureType parentType, ProjectionFilter projectionFilter) throws CityGMLExportException, SQLException {
        ps.setLong(1, parentId);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                if (projectionFilter.containsProperty("fiscalYearOfPublication", module)) {
                    Date fiscalYearOfPublication = rs.getDate("fiscalyearofpublication");
                    if (!rs.wasNull()) {
                        FiscalYearOfPublicationProperty property = new FiscalYearOfPublicationProperty(Year.of(fiscalYearOfPublication.toLocalDate().getYear()));
                        parent.addGenericApplicationPropertyOfCityObjectGroup(property);
                    }
                }

                if (projectionFilter.containsProperty("language", module)) {
                    String language = rs.getString("language");
                    if (!rs.wasNull()) {
                        Code code = new Code(language);
                        code.setCodeSpace(rs.getString("language_codespace"));
                        LanguageProperty property = new LanguageProperty(code);
                        parent.addGenericApplicationPropertyOfCityObjectGroup(property);
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
