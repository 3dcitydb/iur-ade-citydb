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

package org.citydb.ade.iur.balloon;

import org.citydb.ade.iur.schema.ADETable;
import org.citydb.ade.iur.schema.SchemaMapper;
import org.citydb.ade.kmlExporter.ADEBalloonHandler;

public class UrbanFunctionBalloonHandler implements ADEBalloonHandler {
	private final SchemaMapper schemaMapper;

	public UrbanFunctionBalloonHandler(BalloonManager manager) {
		this.schemaMapper = manager.getSchemaMapper();
	}

	@Override
	public String getSqlStatement(String table,
	                              String tableShortId,
	                              String aggregateColumnsClause,
	                              int lod,
	                              String schemaName) {

		String sqlStatement = null;

		if (schemaMapper.getTableName(ADETable.URBANFUNCTION).equalsIgnoreCase(table)
				|| schemaMapper.getTableName(ADETable.ZONE).equalsIgnoreCase(table)
				|| schemaMapper.getTableName(ADETable.DEVELOPMENTPROJECT).equalsIgnoreCase(table)
				|| schemaMapper.getTableName(ADETable.POLLUTION).equalsIgnoreCase(table)
				|| schemaMapper.getTableName(ADETable.PUBLICTRANSPORTATIONFA).equalsIgnoreCase(table)
				|| schemaMapper.getTableName(ADETable.RECREATIONS).equalsIgnoreCase(table)
				|| schemaMapper.getTableName(ADETable.CENSUSBLOCK).equalsIgnoreCase(table)
				|| schemaMapper.getTableName(ADETable.URBANIZATION).equalsIgnoreCase(table)
				|| schemaMapper.getTableName(ADETable.DISASTERDAMAGE).equalsIgnoreCase(table)) {
			sqlStatement = "SELECT " + aggregateColumnsClause +
					" FROM " + schemaName + "." + table + " " + tableShortId +
					" WHERE " + tableShortId + ".id = ?";
		} else if (schemaMapper.getTableName(ADETable.LEGALGROUNDS).equalsIgnoreCase(table)) {
			sqlStatement = "SELECT " + aggregateColumnsClause +
					" FROM " + schemaName + "." + table + " " + tableShortId + ", " + schemaName + "." + schemaMapper.getTableName(ADETable.URBANFUNCTION) + " uub" +
					" WHERE " + tableShortId + ".id = uub.legalgrounds_id" +
					" AND uub.id = ?";
		} else if (schemaMapper.getTableName(ADETable.NUMBEROFHOUSEHOLDS_1).equalsIgnoreCase(table)) {
			sqlStatement = "SELECT " + aggregateColumnsClause +
					" FROM " + schemaName + "." + table + " " + tableShortId +
					" WHERE " + tableShortId + ".censusblock_numberofhou_id_1 = ? OR " + tableShortId + ".censusblock_numberofhouse_id = ?";
		} else if (schemaMapper.getTableName(ADETable.URBANFUNC_TO_CITYOBJEC).equalsIgnoreCase(table)) {
			sqlStatement = "SELECT " + aggregateColumnsClause +
					" FROM " + schemaName + "." + table + " " + tableShortId +
					" WHERE " + tableShortId + ".urbanfunction_id = ?";
		}

		return sqlStatement;
	}

}
