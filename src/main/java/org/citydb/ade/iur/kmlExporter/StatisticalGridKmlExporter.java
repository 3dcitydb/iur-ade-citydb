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
