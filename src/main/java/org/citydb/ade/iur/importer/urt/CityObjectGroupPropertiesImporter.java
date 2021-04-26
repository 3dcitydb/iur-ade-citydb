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

import org.citydb.ade.importer.ADEPropertyCollection;
import org.citydb.ade.importer.CityGMLImportHelper;
import org.citydb.ade.iur.importer.ImportManager;
import org.citydb.ade.iur.schema.ADETable;
import org.citydb.ade.iur.schema.SchemaMapper;
import org.citydb.citygml.importer.CityGMLImportException;
import org.citydb.database.schema.mapping.FeatureType;
import org.citygml4j.ade.iur.model.urt.DataTypeProperty;
import org.citygml4j.ade.iur.model.urt.FareRule;
import org.citygml4j.ade.iur.model.urt.FeedInfo;
import org.citygml4j.ade.iur.model.urt.Frequency;
import org.citygml4j.ade.iur.model.urt.PublicTransitDataType;
import org.citygml4j.ade.iur.model.urt.StopTime;
import org.citygml4j.ade.iur.model.urt.Transfer;
import org.citygml4j.ade.iur.model.urt.Translation;
import org.citygml4j.model.citygml.cityobjectgroup.CityObjectGroup;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CityObjectGroupPropertiesImporter implements PublicTransitModuleImporter {
    private final CityGMLImportHelper helper;
    private final SchemaMapper schemaMapper;
    private final PreparedStatement ps;

    private final FareRuleImporter fareRuleImporter;
    private final FeedInfoImporter feedInfoImporter;
    private final FrequencyImporter frequencyImporter;
    private final StopTimeImporter stopTimeImporter;
    private final TransferImporter transferImporter;
    private final TranslationImporter translationImporter;

    private int batchCounter;

    public CityObjectGroupPropertiesImporter(Connection connection, CityGMLImportHelper helper, ImportManager manager) throws CityGMLImportException, SQLException {
        this.helper = helper;
        this.schemaMapper = manager.getSchemaMapper();

        ps = connection.prepareStatement("insert into " +
                helper.getTableNameWithSchema(schemaMapper.getTableName(ADETable.CITYOBJECTGROUP_3)) + " " +
                "(id) " +
                "values (?)");

        fareRuleImporter = manager.getImporter(FareRuleImporter.class);
        feedInfoImporter = manager.getImporter(FeedInfoImporter.class);
        frequencyImporter = manager.getImporter(FrequencyImporter.class);
        stopTimeImporter = manager.getImporter(StopTimeImporter.class);
        transferImporter = manager.getImporter(TransferImporter.class);
        translationImporter = manager.getImporter(TranslationImporter.class);
    }

    public void doImport(ADEPropertyCollection properties, CityObjectGroup parent, long parentId, FeatureType parentType) throws CityGMLImportException, SQLException {
        ps.setLong(1, parentId);

        ps.addBatch();
        if (++batchCounter == helper.getDatabaseAdapter().getMaxBatchSize())
            helper.executeBatch(schemaMapper.getTableName(ADETable.CITYOBJECTGROUP_3));

        for (DataTypeProperty property : properties.getAll(DataTypeProperty.class)) {
            if (property.isSetValue() && property.getValue().isSetObject()) {
                PublicTransitDataType dataType = property.getValue().getObject();
                if (dataType instanceof FareRule)
                    fareRuleImporter.doImport((FareRule) dataType, parentId);
                else if (dataType instanceof FeedInfo)
                    feedInfoImporter.doImport((FeedInfo) dataType, parentId);
                else if (dataType instanceof Frequency)
                    frequencyImporter.doImport((Frequency) dataType, parentId);
                else if (dataType instanceof StopTime)
                    stopTimeImporter.doImport((StopTime) dataType, parentId);
                else if (dataType instanceof Transfer)
                    transferImporter.doImport((Transfer) dataType, parentId);
                else if (dataType instanceof Translation)
                    translationImporter.doImport((Translation) dataType, parentId);
            }
        }
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
