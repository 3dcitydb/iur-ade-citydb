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

package org.citydb.ade.iur.exporter.uro;

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
import org.citygml4j.ade.iur.model.module.UrbanObjectModule;
import org.citygml4j.ade.iur.model.uro.FiscalYearOfPublicationProperty;
import org.citygml4j.ade.iur.model.uro.LanguageProperty;
import org.citygml4j.model.citygml.cityobjectgroup.CityObjectGroup;
import org.citygml4j.model.gml.basicTypes.Code;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Year;

public class CityObjectGroupPropertiesExporter implements UrbanObjectModuleExporter {
    private final PreparedStatement ps;
    private final String module;

    public CityObjectGroupPropertiesExporter(Connection connection, CityGMLExportHelper helper, ExportManager manager) throws CityGMLExportException, SQLException {
        String tableName = manager.getSchemaMapper().getTableName(ADETable.CITYOBJECTGROUP_1);
        module = UrbanObjectModule.v1_4.getNamespaceURI();

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
