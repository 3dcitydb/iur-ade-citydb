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

package org.citydb.ade.iur.kmlExporter;

import org.citydb.ade.iur.schema.ADETable;
import org.citydb.ade.iur.schema.SchemaMapper;
import org.citydb.ade.kmlExporter.ADEKmlExportHelper;
import org.citydb.ade.kmlExporter.ADEKmlExporter;

public class UrbanFunctionKmlExporter implements ADEKmlExporter {
	private final ADEKmlExportHelper helper;
	private final String schema;
	private final SchemaMapper schemaMapper;

	public UrbanFunctionKmlExporter(ADEKmlExportHelper helper, KMLExportManager manager) {
		this.helper = helper;
		this.schema = helper.getDatabaseAdapter().getConnectionDetails().getSchema();
		this.schemaMapper = manager.getSchemaMapper();
	}

	@Override
	public String getPointAndCurveQuery(int lod) {
		return "select uf.location, " +
				helper.getSQLQueryHelper().getImplicitGeometryNullColumns() +
				"from " + schema + "." + schemaMapper.getTableName(ADETable.URBANFUNCTION) + " uf " +
				"WHERE uf.id=? and uf.location is not null " +
				"UNION ALL " +
				"select uf.boundary, " +
				helper.getSQLQueryHelper().getImplicitGeometryNullColumns() +
				"from " + schema + "." + schemaMapper.getTableName(ADETable.URBANFUNCTION) + " uf " +
				"WHERE uf.id=? and uf.boundary is not null";
	}

	@Override
	public String getSurfaceGeometryQuery(int lod) {
		return "select sg.geometry, " +
				helper.getSQLQueryHelper().getImplicitGeometryNullColumns() +
				"from " + schema + ".surface_geometry sg " +
				"where sg.root_id in (" +
				"select uf.area_id from " + schema + "." + schemaMapper.getTableName(ADETable.URBANFUNCTION) + " uf " +
				"WHERE uf.id=? and sg.geometry is not null)";
	}

	@Override
	public String getSurfaceGeometryRefIdsQuery(int lod) {
		return "select uf.area_id, uf.objectclass_id, " +
				helper.getSQLQueryHelper().getImplicitGeometryNullColumns() +
				"from " + schema + "." + schemaMapper.getTableName(ADETable.URBANFUNCTION) + " uf " +
				"WHERE uf.id=? and uf.area_id is not null";
	}

}
