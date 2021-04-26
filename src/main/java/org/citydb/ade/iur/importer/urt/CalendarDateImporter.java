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

import org.citydb.ade.importer.CityGMLImportHelper;
import org.citydb.ade.importer.ForeignKeys;
import org.citydb.ade.iur.importer.ImportManager;
import org.citydb.ade.iur.schema.ADETable;
import org.citydb.ade.iur.schema.SchemaMapper;
import org.citydb.citygml.importer.CityGMLImportException;
import org.citydb.database.schema.mapping.AbstractObjectType;
import org.citygml4j.ade.iur.model.urt.Calendar;
import org.citygml4j.ade.iur.model.urt.CalendarDate;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class CalendarDateImporter implements PublicTransitModuleImporter {
    private final CityGMLImportHelper helper;
    private final SchemaMapper schemaMapper;
    private final PreparedStatement ps;
    private final PublicTransitImporter publicTransitImporter;

    private int batchCounter;

    public CalendarDateImporter(Connection connection, CityGMLImportHelper helper, ImportManager manager) throws CityGMLImportException, SQLException {
        this.helper = helper;
        this.schemaMapper = manager.getSchemaMapper();

        ps = connection.prepareStatement("insert into " +
                helper.getTableNameWithSchema(manager.getSchemaMapper().getTableName(ADETable.CALENDARDATE)) + " " +
                "(id, date_, exceptiontype, exceptiontype_codespace, calendar_id) " +
                "values (?, ?, ?, ?, ?)");

        publicTransitImporter = manager.getImporter(PublicTransitImporter.class);
    }

    public void doImport(CalendarDate calendarDate, long objectId, AbstractObjectType<?> objectType, ForeignKeys foreignKeys) throws CityGMLImportException, SQLException {
        publicTransitImporter.doImport(calendarDate, objectId, objectType, foreignKeys);
        ps.setLong(1, objectId);

        if (calendarDate.getDate() != null)
            ps.setDate(2, Date.valueOf(calendarDate.getDate()));
        else
            ps.setNull(2, Types.DATE);

        if (calendarDate.getExceptionType() != null && calendarDate.getExceptionType().isSetValue()) {
            ps.setString(3, calendarDate.getExceptionType().getValue());
            ps.setString(4, calendarDate.getExceptionType().getCodeSpace());
        } else {
            ps.setNull(3, Types.VARCHAR);
            ps.setNull(4, Types.VARCHAR);
        }

        long calendarId = 0;
        if (calendarDate.getCalendar() != null) {
            Calendar calendar = calendarDate.getCalendar().getObject();
            if (calendar != null) {
                calendarId = helper.importObject(calendar);
                calendarDate.getCalendar().unsetObject();
            } else {
                String href = calendarDate.getCalendar().getHref();
                if (href != null && !href.isEmpty()) {
                    helper.propagateObjectXlink(
                            schemaMapper.getTableName(ADETable.CALENDARDATE),
                            objectId, href, "calendar_id");
                }
            }
        }

        if (calendarId != 0)
            ps.setLong(5, calendarId);
        else
            ps.setNull(5, Types.NULL);

        ps.addBatch();
        if (++batchCounter == helper.getDatabaseAdapter().getMaxBatchSize())
            helper.executeBatch(objectType);
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
