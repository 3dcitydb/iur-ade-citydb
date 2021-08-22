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

package org.citydb.ade.iur.importer.urt;

import org.citydb.ade.iur.importer.ImportManager;
import org.citydb.ade.iur.schema.ADETable;
import org.citydb.ade.iur.schema.SchemaMapper;
import org.citydb.core.ade.importer.CityGMLImportHelper;
import org.citydb.core.operation.importer.CityGMLImportException;
import org.citygml4j.ade.iur.model.urt.PublicTransit;
import org.citygml4j.ade.iur.model.urt.Translation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class TranslationImporter implements PublicTransitModuleImporter {
    private final CityGMLImportHelper helper;
    private final SchemaMapper schemaMapper;
    private final PreparedStatement ps;
    private final PublicTransitDataTypeImporter dataTypeImporter;

    private int batchCounter;

    public TranslationImporter(Connection connection, CityGMLImportHelper helper, ImportManager manager) throws CityGMLImportException, SQLException {
        this.helper = helper;
        this.schemaMapper = manager.getSchemaMapper();

        ps = connection.prepareStatement("insert into " +
                helper.getTableNameWithSchema(manager.getSchemaMapper().getTableName(ADETable.TRANSLATION)) + " " +
                "(id, fieldname, fieldvalue, language, language_codespace, recordsubid, tablename, tablename_codespace " +
                "translation, recordid_id) " +
                "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

        dataTypeImporter = manager.getImporter(PublicTransitDataTypeImporter.class);
    }

    public void doImport(Translation translation, long cityObjectGroupId) throws CityGMLImportException, SQLException {
        long objectId = dataTypeImporter.doImport(translation, cityObjectGroupId);
        ps.setLong(1, objectId);

        ps.setString(2, translation.getFieldName());
        ps.setString(3, translation.getFieldValue());

        if (translation.getLanguage() != null && translation.getLanguage().isSetValue()) {
            ps.setString(4, translation.getLanguage().getValue());
            ps.setString(5, translation.getLanguage().getCodeSpace());
        } else {
            ps.setNull(4, Types.VARCHAR);
            ps.setNull(5, Types.VARCHAR);
        }

        ps.setString(6, translation.getRecordSubId());

        if (translation.getTableName() != null && translation.getTableName().isSetValue()) {
            ps.setString(7, translation.getTableName().getValue());
            ps.setString(8, translation.getTableName().getCodeSpace());
        } else {
            ps.setNull(7, Types.VARCHAR);
            ps.setNull(8, Types.VARCHAR);
        }

        ps.setString(9, translation.getTranslation());

        long recordId = 0;
        if (translation.getRecordId() != null) {
            PublicTransit publicTransit = translation.getRecordId().getObject();
            if (publicTransit != null) {
                recordId = helper.importObject(publicTransit);
                translation.getRecordId().unsetObject();
            } else {
                String href = translation.getRecordId().getHref();
                if (href != null && !href.isEmpty()) {
                    helper.propagateObjectXlink(
                            schemaMapper.getTableName(ADETable.TRANSLATION),
                            objectId, href, "recordid_id");
                }
            }
        }

        if (recordId != 0)
            ps.setLong(10, recordId);
        else
            ps.setNull(10, Types.NULL);

        ps.addBatch();
        if (++batchCounter == helper.getDatabaseAdapter().getMaxBatchSize())
            helper.executeBatch(schemaMapper.getTableName(ADETable.TRANSLATION));
    }

    @Override
    public void executeBatch() throws CityGMLImportException, SQLException {
        if (batchCounter > 0) {
            ps.executeBatch();
            batchCounter = 0;
        }
    }

    @Override
    public void close() throws CityGMLImportException, SQLException {
        ps.close();
    }
}
