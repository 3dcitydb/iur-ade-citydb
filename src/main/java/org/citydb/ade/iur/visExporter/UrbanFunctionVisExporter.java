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

package org.citydb.ade.iur.visExporter;

import org.citydb.ade.iur.schema.ADETable;
import org.citydb.ade.iur.schema.SchemaMapper;
import org.citydb.core.ade.visExporter.ADEVisExportHelper;
import org.citydb.core.ade.visExporter.ADEVisExporter;

public class UrbanFunctionVisExporter implements ADEVisExporter {
	private final ADEVisExportHelper helper;
	private final String schema;
	private final SchemaMapper schemaMapper;

	public UrbanFunctionVisExporter(ADEVisExportHelper helper, VisExportManager manager) {
		this.helper = helper;
		this.schema = helper.getDatabaseAdapter().getConnectionDetails().getSchema();
		this.schemaMapper = manager.getSchemaMapper();
	}

	@Override
	public String getPointAndCurveQuery(int lod) {
		if (lod == 0) {
			return "select uf.lod0multicurve, " +
					helper.getSQLQueryHelper().getImplicitGeometryNullColumns() +
					"from " + schema + "." + schemaMapper.getTableName(ADETable.URBANFUNCTION) + " uf " +
					"WHERE uf.id=? and uf.lod0multicurve is not null " +
					"UNION ALL " +
					"select uf.lod0multipoint, " +
					helper.getSQLQueryHelper().getImplicitGeometryNullColumns() +
					"from " + schema + "." + schemaMapper.getTableName(ADETable.URBANFUNCTION) + " uf " +
					"WHERE uf.id=? and uf.lod0multipoint is not null";
		} else {
			// always export lod -1 and -2
			return "select uf.lod_1multicurve, " +
					helper.getSQLQueryHelper().getImplicitGeometryNullColumns() +
					"from " + schema + "." + schemaMapper.getTableName(ADETable.URBANFUNCTION) + " uf " +
					"WHERE uf.id=? and uf.lod_1multicurve is not null " +
					"UNION ALL " +
					"select uf.lod_1multipoint, " +
					helper.getSQLQueryHelper().getImplicitGeometryNullColumns() +
					"from " + schema + "." + schemaMapper.getTableName(ADETable.URBANFUNCTION) + " uf " +
					"WHERE uf.id=? and uf.lod_1multipoint is not null " +
					"UNION ALL " +
					"select uf.lod_2multicurve, " +
					helper.getSQLQueryHelper().getImplicitGeometryNullColumns() +
					"from " + schema + "." + schemaMapper.getTableName(ADETable.URBANFUNCTION) + " uf " +
					"WHERE uf.id=? and uf.lod_2multicurve is not null " +
					"UNION ALL " +
					"select uf.lod_2multipoint, " +
					helper.getSQLQueryHelper().getImplicitGeometryNullColumns() +
					"from " + schema + "." + schemaMapper.getTableName(ADETable.URBANFUNCTION) + " uf " +
					"WHERE uf.id=? and uf.lod_2multipoint is not null";
		}
	}

	@Override
	public String getSurfaceGeometryQuery(int lod) {
		if (lod == 0) {
			return "select sg.geometry, " +
					helper.getSQLQueryHelper().getImplicitGeometryNullColumns() +
					"from " + schema + ".surface_geometry sg " +
					"where sg.root_id in (" +
					"select uf.lod0multisurface_id from " + schema + "." + schemaMapper.getTableName(ADETable.URBANFUNCTION) + " uf " +
					"WHERE uf.id=? and sg.geometry is not null)";
		} else {
			// always export lod -1 and -2
			return "select sg.geometry, " +
					helper.getSQLQueryHelper().getImplicitGeometryNullColumns() +
					"from " + schema + ".surface_geometry sg " +
					"where sg.root_id in (" +
					"select uf.lod_1multisurface_id from " + schema + "." + schemaMapper.getTableName(ADETable.URBANFUNCTION) + " uf " +
					"WHERE uf.id=? and sg.geometry is not null) " +
					"UNION ALL " +
					"select sg.geometry, " +
					helper.getSQLQueryHelper().getImplicitGeometryNullColumns() +
					"from " + schema + ".surface_geometry sg " +
					"where sg.root_id in (" +
					"select uf.lod_2multisurface_id from " + schema + "." + schemaMapper.getTableName(ADETable.URBANFUNCTION) + " uf " +
					"WHERE uf.id=? and sg.geometry is not null)";
		}
	}

	@Override
	public String getSurfaceGeometryRefIdsQuery(int lod) {
		if (lod == 0) {
			return "select uf.lod0multisurface_id, uf.objectclass_id, " +
					helper.getSQLQueryHelper().getImplicitGeometryNullColumns() +
					"from " + schema + "." + schemaMapper.getTableName(ADETable.URBANFUNCTION) + " uf " +
					"WHERE uf.id=? and uf.lod0multisurface_id is not null";
		} else {
			// always export lod -1 and -2
			return "select uf.lod_1multisurface_id, uf.objectclass_id, " +
					helper.getSQLQueryHelper().getImplicitGeometryNullColumns() +
					"from " + schema + "." + schemaMapper.getTableName(ADETable.URBANFUNCTION) + " uf " +
					"WHERE uf.id=? and uf.lod_1multisurface_id is not null " +
					"UNION ALL " +
					"select uf.lod_2multisurface_id, uf.objectclass_id, " +
					helper.getSQLQueryHelper().getImplicitGeometryNullColumns() +
					"from " + schema + "." + schemaMapper.getTableName(ADETable.URBANFUNCTION) + " uf " +
					"WHERE uf.id=? and uf.lod_2multisurface_id is not null";
		}
	}

}
