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

package org.citydb.ade.iur.exporter.urt;

import org.citydb.ade.exporter.CityGMLExportHelper;
import org.citydb.ade.iur.exporter.ExportManager;
import org.citydb.ade.iur.schema.ADETable;
import org.citydb.citygml.exporter.CityGMLExportException;
import org.citydb.database.schema.mapping.MappingConstants;
import org.citydb.sqlbuilder.expression.PlaceHolder;
import org.citydb.sqlbuilder.schema.Table;
import org.citydb.sqlbuilder.select.Select;
import org.citydb.sqlbuilder.select.join.JoinFactory;
import org.citydb.sqlbuilder.select.operator.comparison.ComparisonFactory;
import org.citydb.sqlbuilder.select.operator.comparison.ComparisonName;
import org.citygml4j.ade.iur.model.urt.PublicTransitProperty;
import org.citygml4j.ade.iur.model.urt.Translation;
import org.citygml4j.model.gml.basicTypes.Code;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TranslationExporter implements PublicTransitModuleExporter {
    private final PreparedStatement ps;

    public TranslationExporter(Connection connection, CityGMLExportHelper helper, ExportManager manager) throws CityGMLExportException, SQLException {
        String tableName = manager.getSchemaMapper().getTableName(ADETable.TRANSLATION);

        Table table = new Table(helper.getTableNameWithSchema(tableName));
        Table trip = new Table(helper.getTableNameWithSchema(MappingConstants.CITYOBJECT));

        Select select = new Select().addProjection(table.getColumns("fieldname", "fieldvalue", "language",
                "language_codespace", "recordsubid", "tablename", "tablename_codespace", "translation"))
                .addProjection(trip.getColumn("gmlid", "rgmlid"))
                .addJoin(JoinFactory.left(trip, "id", ComparisonName.EQUAL_TO, table.getColumn("recordid_id")))
                .addSelection(ComparisonFactory.equalTo(table.getColumn("id"), new PlaceHolder<>()));
        ps = connection.prepareStatement(select.toString());
    }

    public void doExport(Translation translation, long objectId) throws CityGMLExportException, SQLException {
        ps.setLong(1, objectId);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                String tableName = rs.getString("tablename");
                if (!rs.wasNull()) {
                    Code code = new Code(tableName);
                    code.setCodeSpace(rs.getString("tablename_codespace"));
                    translation.setTableName(code);
                }

                translation.setFieldName(rs.getString("fieldname"));

                String language = rs.getString("language");
                if (!rs.wasNull()) {
                    Code code = new Code(language);
                    code.setCodeSpace(rs.getString("language_codespace"));
                    translation.setLanguage(code);
                }

                translation.setTranslation(rs.getString("translation"));
                translation.setFieldValue(rs.getString("fieldvalue"));

                String recordId = rs.getString("rgmlid");
                if (recordId != null)
                    translation.setRecordId(new PublicTransitProperty("#" + recordId));

                translation.setRecordSubId(rs.getString("recordsubid"));
            }
        }
    }

    @Override
    public void close() throws CityGMLExportException, SQLException {
        ps.close();
    }
}
