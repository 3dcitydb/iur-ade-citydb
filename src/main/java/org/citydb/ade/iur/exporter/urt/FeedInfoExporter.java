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

package org.citydb.ade.iur.exporter.urt;

import org.citydb.ade.iur.exporter.ExportManager;
import org.citydb.ade.iur.schema.ADETable;
import org.citydb.core.ade.exporter.CityGMLExportHelper;
import org.citydb.core.operation.exporter.CityGMLExportException;
import org.citydb.sqlbuilder.expression.PlaceHolder;
import org.citydb.sqlbuilder.schema.Table;
import org.citydb.sqlbuilder.select.Select;
import org.citydb.sqlbuilder.select.operator.comparison.ComparisonFactory;
import org.citygml4j.ade.iur.model.urt.FeedInfo;
import org.citygml4j.model.gml.basicTypes.Code;

import java.sql.*;

public class FeedInfoExporter implements PublicTransitModuleExporter {
    private final PreparedStatement ps;

    public FeedInfoExporter(Connection connection, CityGMLExportHelper helper, ExportManager manager) throws CityGMLExportException, SQLException {
        String tableName = manager.getSchemaMapper().getTableName(ADETable.FEEDINFO);

        Table table = new Table(helper.getTableNameWithSchema(tableName));
        Select select = new Select().addProjection(table.getColumns("contactemail", "contacturl", "defaultlanguage",
                        "defaultlanguage_codespace", "detailedinfo", "enddate", "language", "language_codespace",
                        "publishername", "publisherurl", "startdate", "version"))
                .addSelection(ComparisonFactory.equalTo(table.getColumn("id"), new PlaceHolder<>()));
        ps = connection.prepareStatement(select.toString());
    }

    public void doExport(FeedInfo feedInfo, long objectId) throws CityGMLExportException, SQLException {
        ps.setLong(1, objectId);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                feedInfo.setPublisherName(rs.getString("publishername"));
                feedInfo.setPublisherUrl(rs.getString("publisherurl"));

                String language = rs.getString("language");
                if (!rs.wasNull()) {
                    Code code = new Code(language);
                    code.setCodeSpace(rs.getString("language_codespace"));
                    feedInfo.setLanguage(code);
                }

                String defaultLanguage = rs.getString("defaultlanguage");
                if (!rs.wasNull()) {
                    Code code = new Code(defaultLanguage);
                    code.setCodeSpace(rs.getString("defaultlanguage_codespace"));
                    feedInfo.setDefaultLanguage(code);
                }

                Date startDate = rs.getDate("startdate");
                if (!rs.wasNull())
                    feedInfo.setStartDate(startDate.toLocalDate());

                Date endDate = rs.getDate("enddate");
                if (!rs.wasNull())
                    feedInfo.setEndDate(endDate.toLocalDate());

                feedInfo.setVersion(rs.getString("version"));
                feedInfo.setContactEmail(rs.getString("contactemail"));
                feedInfo.setContactURL(rs.getString("contacturl"));
                feedInfo.setDetailedInfo(rs.getString("detailedinfo"));
            }
        }
    }

    @Override
    public void close() throws CityGMLExportException, SQLException {
        ps.close();
    }
}
