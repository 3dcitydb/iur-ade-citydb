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

import org.citydb.modules.kml.ade.ADEKmlExportHelper;
import org.citydb.modules.kml.ade.ADEKmlExporter;
import org.citydb.ade.iur.schema.ADETable;
import org.citydb.ade.iur.schema.SchemaMapper;

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
		if (lod == 1 || lod == 2) {
			return new StringBuilder("select sg.geometry, ")
					.append(helper.getSQLQueries().getImplicitGeometryNullColumns())
					.append("from ")
					.append(schema).append(".surface_geometry sg ")
					.append("where sg.root_id in (")
			        .append("select usg.lod_").append(lod).append("multisurfacegeometry_id from ")
					.append(schema).append(".").append(schemaMapper.getTableName(ADETable.STATISTICALGRID)).append(" usg ")
					.append("WHERE usg.id=? and sg.geometry is not null)").toString();
		} else {
			return null;
		}
	}

	@Override
	public String getSurfaceGeometryRefIdsQuery(int lod) {
		if (lod == 1 || lod == 2) {
			return new StringBuilder("select usg.lod_").append(lod).append("multisurfacegeometry_id, usg.objectclass_id, ")
					.append(helper.getSQLQueries().getImplicitGeometryNullColumns())
					.append("from ").append(schema).append(".").append(schemaMapper.getTableName(ADETable.STATISTICALGRID)).append(" usg ")
					.append("WHERE usg.id=? and usg.lod_").append(lod).append("multisurfacegeometry_id is not null").toString();
		} else {
			return null;
		}
	}

}
