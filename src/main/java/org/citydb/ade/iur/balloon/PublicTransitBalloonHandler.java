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

package org.citydb.ade.iur.balloon;

import org.citydb.ade.iur.schema.ADETable;
import org.citydb.ade.iur.schema.SchemaMapper;
import org.citydb.core.ade.visExporter.ADEBalloonHandler;

public class PublicTransitBalloonHandler implements ADEBalloonHandler {
    private final SchemaMapper schemaMapper;

    public PublicTransitBalloonHandler(BalloonManager manager) {
        this.schemaMapper = manager.getSchemaMapper();
    }

    @Override
    public String getSqlStatement(String table,
                                  String tableShortId,
                                  String aggregateColumnsClause,
                                  int lod,
                                  String schemaName) {

        String sqlStatement = null;

        if (schemaMapper.getTableName(ADETable.PUBLICTRANSIT).equalsIgnoreCase(table)
                || schemaMapper.getTableName(ADETable.AGENCY).equalsIgnoreCase(table)
                || schemaMapper.getTableName(ADETable.ATTRIBUTION).equalsIgnoreCase(table)
                || schemaMapper.getTableName(ADETable.CALENDAR).equalsIgnoreCase(table)
                || schemaMapper.getTableName(ADETable.CALENDARDATE).equalsIgnoreCase(table)
                || schemaMapper.getTableName(ADETable.FAREATTRIBUTE).equalsIgnoreCase(table)
                || schemaMapper.getTableName(ADETable.OFFICE).equalsIgnoreCase(table)
                || schemaMapper.getTableName(ADETable.PATHWAY).equalsIgnoreCase(table)
                || schemaMapper.getTableName(ADETable.ROUTE).equalsIgnoreCase(table)
                || schemaMapper.getTableName(ADETable.STOP).equalsIgnoreCase(table)
                || schemaMapper.getTableName(ADETable.TRANSLATIONJP).equalsIgnoreCase(table)
                || schemaMapper.getTableName(ADETable.TRIP).equalsIgnoreCase(table)) {
            sqlStatement = "SELECT " + aggregateColumnsClause +
                    " FROM " + schemaName + "." + table + " " + tableShortId +
                    " WHERE " + tableShortId + ".id = ?";
        } else if (schemaMapper.getTableName(ADETable.DESCRIPTION).equalsIgnoreCase(table)) {
            sqlStatement = "SELECT " + aggregateColumnsClause +
                    " FROM " + schemaName + "." + table + " " + tableShortId + ", " + schemaName + "." + schemaMapper.getTableName(ADETable.ROUTE) + " r" +
                    " WHERE " + tableShortId + ".id = r.description_id" +
                    " AND r.id = ?";
        } else if (schemaMapper.getTableName(ADETable.POINT).equalsIgnoreCase(table)) {
            sqlStatement = "SELECT " + aggregateColumnsClause +
                    " FROM " + schemaName + "." + table + " " + tableShortId +
                    " WHERE " + tableShortId + ".publictransit_point_id = ?";
        }

        return sqlStatement;
    }

}
