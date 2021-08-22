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
import org.citydb.core.ade.importer.ForeignKeys;
import org.citydb.core.database.schema.mapping.AbstractObjectType;
import org.citydb.core.operation.importer.CityGMLImportException;
import org.citygml4j.ade.iur.model.urt.Level;
import org.citygml4j.ade.iur.model.urt.PointProperty;
import org.citygml4j.ade.iur.model.urt.PublicTransit;
import org.citygml4j.ade.iur.model.urt.Shape;
import org.citygml4j.model.citygml.core.AbstractCityObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class PublicTransitImporter implements PublicTransitModuleImporter {
    private final CityGMLImportHelper helper;
    private final SchemaMapper schemaMapper;
    private final PreparedStatement ps;
    private final PointImporter pointImporter;

    private int batchCounter;

    public PublicTransitImporter(Connection connection, CityGMLImportHelper helper, ImportManager manager) throws CityGMLImportException, SQLException {
        this.helper = helper;
        this.schemaMapper = manager.getSchemaMapper();

        ps = connection.prepareStatement("insert into " +
                helper.getTableNameWithSchema(manager.getSchemaMapper().getTableName(ADETable.PUBLICTRANSIT)) + " " +
                "(id, objectclass_id, orgid, target_id, index_) " +
                "values (?, ?, ?, ?, ?)");

        pointImporter = manager.getImporter(PointImporter.class);
    }

    public void doImport(PublicTransit publicTransit, long objectId, AbstractObjectType<?> objectType, ForeignKeys foreignKeys) throws CityGMLImportException, SQLException {
        ps.setLong(1, objectId);
        ps.setInt(2, objectType.getObjectClassId());

        ps.setString(3,publicTransit.getOrgId());

        long cityObjectId = 0;
        if (publicTransit.getTarget() != null) {
            AbstractCityObject cityObject = publicTransit.getTarget().getObject();
            if (cityObject != null) {
                cityObjectId = helper.importObject(cityObject);
                publicTransit.getTarget().unsetObject();
            } else {
                String href = publicTransit.getTarget().getHref();
                if (href != null && !href.isEmpty()) {
                    helper.propagateObjectXlink(
                            schemaMapper.getTableName(ADETable.PUBLICTRANSIT),
                            objectId, href, "target_id");
                }
            }
        }

        if (cityObjectId != 0)
            ps.setLong(4, cityObjectId);
        else
            ps.setNull(4, Types.NULL);

        if (publicTransit instanceof Level)
            ps.setDouble(5, ((Level) publicTransit).getIndex());
        else
            ps.setNull(5, Types.DOUBLE);

        ps.addBatch();
        if (++batchCounter == helper.getDatabaseAdapter().getMaxBatchSize())
            helper.executeBatch(objectType);

        if (publicTransit instanceof Shape) {
            Shape shape = (Shape) publicTransit;
            for (PointProperty property : shape.getPoints()) {
                if (property.getObject() != null) {
                    pointImporter.doImport(property.getObject(), objectId);
                    property.unsetObject();
                }
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
