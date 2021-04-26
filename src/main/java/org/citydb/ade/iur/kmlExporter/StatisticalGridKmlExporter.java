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

package org.citydb.ade.iur.kmlExporter;

import org.citydb.ade.iur.schema.ADETable;
import org.citydb.ade.iur.schema.SchemaMapper;
import org.citydb.ade.kmlExporter.ADEKmlExportHelper;
import org.citydb.ade.kmlExporter.ADEKmlExporter;

public class StatisticalGridKmlExporter implements ADEKmlExporter {
	private final ADEKmlExportHelper helper;
	private final String schema;
	private final SchemaMapper schemaMapper;

	public StatisticalGridKmlExporter(ADEKmlExportHelper helper, KMLExportManager manager) {
		this.helper = helper;
		this.schema = helper.getDatabaseAdapter().getConnectionDetails().getSchema();
		this.schemaMapper = manager.getSchemaMapper();
	}

	@Override
	public String getPointAndCurveQuery(int lod) {
		return null;
	}

	@Override
	public String getSurfaceGeometryQuery(int lod) {
		// always export lod -1 and -2
		return "select sg.geometry, " +
				helper.getSQLQueryHelper().getImplicitGeometryNullColumns() +
				"from " + schema + ".surface_geometry sg " +
				"where sg.root_id in (" +
				"select usg.lod_1multisurface_id from " + schema + "." + schemaMapper.getTableName(ADETable.STATISTICALGRID) + " usg " +
				"WHERE usg.id=? and sg.geometry is not null) " +
				"UNION ALL " +
				"select sg.geometry, " +
				helper.getSQLQueryHelper().getImplicitGeometryNullColumns() +
				"from " + schema + ".surface_geometry sg " +
				"where sg.root_id in (" +
				"select usg.lod_2multisurface_id from " + schema + "." + schemaMapper.getTableName(ADETable.STATISTICALGRID) + " usg " +
				"WHERE usg.id=? and sg.geometry is not null)";
	}

	@Override
	public String getSurfaceGeometryRefIdsQuery(int lod) {
		// always export lod -1 and -2
		return "select usg.lod_1multisurface_id, usg.objectclass_id, " +
				helper.getSQLQueryHelper().getImplicitGeometryNullColumns() +
				"from " + schema + "." + schemaMapper.getTableName(ADETable.STATISTICALGRID) + " usg " +
				"WHERE usg.id=? and usg.lod_1multisurface_id is not null " +
				"UNION ALL " +
				"select usg.lod_2multisurface_id, usg.objectclass_id, " +
				helper.getSQLQueryHelper().getImplicitGeometryNullColumns() +
				"from " + schema + "." + schemaMapper.getTableName(ADETable.STATISTICALGRID) + " usg " +
				"WHERE usg.id=? and usg.lod_2multisurface_id is not null";
	}

}
