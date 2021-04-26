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

package org.citydb.ade.iur.importer.urg;

import org.citydb.ade.importer.CityGMLImportHelper;
import org.citydb.ade.iur.importer.ImportManager;
import org.citydb.ade.iur.schema.ADESequence;
import org.citydb.ade.iur.schema.ADETable;
import org.citydb.ade.iur.schema.SchemaMapper;
import org.citydb.citygml.importer.CityGMLImportException;
import org.citygml4j.ade.iur.model.urg.LandPricePerLandUse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class LandPricePerLandUseImporter implements StatisticalGridModuleImporter {
    private final CityGMLImportHelper helper;
    private final SchemaMapper schemaMapper;
    private final PreparedStatement ps;

    private int batchCounter;

    public LandPricePerLandUseImporter(Connection connection, CityGMLImportHelper helper, ImportManager manager) throws CityGMLImportException, SQLException {
        this.helper = helper;
        this.schemaMapper = manager.getSchemaMapper();

        ps = connection.prepareStatement("insert into " +
                helper.getTableNameWithSchema(schemaMapper.getTableName(ADETable.LANDPRICEPERLANDUSE)) + " " +
                "(id, landprice_landprice_id, landprice, landuse, landuse_codespace) " +
                "values (?, ?, ?, ?, ?)");
    }

    public void doImport(LandPricePerLandUse landPricePerLandUse, long parentId) throws CityGMLImportException, SQLException {
        long objectId = helper.getNextSequenceValue(schemaMapper.getSequenceName(ADESequence.LANDPRICEPERLANDUS_SEQ));
        ps.setLong(1, objectId);
        ps.setLong(2, parentId);

        if (landPricePerLandUse.getLandPrice() != null)
            ps.setInt(3, landPricePerLandUse.getLandPrice());
        else
            ps.setNull(3, Types.INTEGER);

        if (landPricePerLandUse.getLandUse() != null && landPricePerLandUse.getLandUse().isSetValue()) {
            ps.setString(4, landPricePerLandUse.getLandUse().getValue());
            ps.setString(5, landPricePerLandUse.getLandUse().getCodeSpace());
        } else {
            ps.setNull(4, Types.VARCHAR);
            ps.setNull(5, Types.VARCHAR);
        }

        ps.addBatch();
        if (++batchCounter == helper.getDatabaseAdapter().getMaxBatchSize())
            helper.executeBatch(schemaMapper.getTableName(ADETable.LANDPRICEPERLANDUSE));
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
