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
import org.citygml4j.ade.iur.model.urt.FeedInfo;

import java.sql.*;

public class FeedInfoImporter implements PublicTransitModuleImporter {
    private final CityGMLImportHelper helper;
    private final SchemaMapper schemaMapper;
    private final PreparedStatement ps;
    private final PublicTransitDataTypeImporter dataTypeImporter;

    private int batchCounter;

    public FeedInfoImporter(Connection connection, CityGMLImportHelper helper, ImportManager manager) throws CityGMLImportException, SQLException {
        this.helper = helper;
        this.schemaMapper = manager.getSchemaMapper();

        ps = connection.prepareStatement("insert into " +
                helper.getTableNameWithSchema(manager.getSchemaMapper().getTableName(ADETable.FEEDINFO)) + " " +
                "(id, contactemail, contacturl, defaultlanguage, defaultlanguage_codespace, detailedinfo, " +
                "enddate, language, language_codespace, publishername, publisherurl, startdate, version) " +
                "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

        dataTypeImporter = manager.getImporter(PublicTransitDataTypeImporter.class);
    }

    public void doImport(FeedInfo feedInfo, long cityObjectGroupId) throws CityGMLImportException, SQLException {
        long objectId = dataTypeImporter.doImport(feedInfo, cityObjectGroupId);
        ps.setLong(1, objectId);

        ps.setString(2, feedInfo.getContactEmail());
        ps.setString(3, feedInfo.getContactURL());

        if (feedInfo.getDefaultLanguage() != null && feedInfo.getDefaultLanguage().isSetValue()) {
            ps.setString(4, feedInfo.getDefaultLanguage().getValue());
            ps.setString(5, feedInfo.getDefaultLanguage().getCodeSpace());
        } else {
            ps.setNull(4, Types.VARCHAR);
            ps.setNull(5, Types.VARCHAR);
        }

        ps.setString(6, feedInfo.getDetailedInfo());

        if (feedInfo.getEndDate() != null)
            ps.setDate(7, Date.valueOf(feedInfo.getEndDate()));
        else
            ps.setNull(7, Types.DATE);

        if (feedInfo.getLanguage() != null && feedInfo.getLanguage().isSetValue()) {
            ps.setString(8, feedInfo.getLanguage().getValue());
            ps.setString(9, feedInfo.getLanguage().getCodeSpace());
        } else {
            ps.setNull(8, Types.VARCHAR);
            ps.setNull(9, Types.VARCHAR);
        }

        ps.setString(10, feedInfo.getPublisherName());
        ps.setString(11, feedInfo.getPublisherUrl());

        if (feedInfo.getStartDate() != null)
            ps.setDate(12, Date.valueOf(feedInfo.getStartDate()));
        else
            ps.setNull(12, Types.DATE);

        ps.setString(13, feedInfo.getVersion());

        ps.addBatch();
        if (++batchCounter == helper.getDatabaseAdapter().getMaxBatchSize())
            helper.executeBatch(schemaMapper.getTableName(ADETable.FEEDINFO));
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
