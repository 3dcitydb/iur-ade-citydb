package org.citydb.ade.iur.kmlExporter;

import org.citydb.modules.kml.ade.ADEKmlExportHelper;
import org.citydb.modules.kml.ade.ADEKmlExporter;
import org.citydb.ade.iur.schema.ADETable;
import org.citydb.ade.iur.schema.SchemaMapper;

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
				helper.getSQLQueries().getImplicitGeometryNullColumns() +
				"from " + schema + "." + schemaMapper.getTableName(ADETable.URBANFUNCTION) + " uf " +
				"WHERE uf.id=? and uf.location is not null " +
				"UNION ALL " +
				"select uf.boundary, " +
				helper.getSQLQueries().getImplicitGeometryNullColumns() +
				"from " + schema + "." + schemaMapper.getTableName(ADETable.URBANFUNCTION) + " uf " +
				"WHERE uf.id=? and uf.boundary is not null";
	}

	@Override
	public String getSurfaceGeometryQuery(int lod) {
		return "select sg.geometry, " +
				helper.getSQLQueries().getImplicitGeometryNullColumns() +
				"from " + schema + ".surface_geometry sg " +
				"where sg.root_id in (" +
				"select uf.area_id from " + schema + "." + schemaMapper.getTableName(ADETable.URBANFUNCTION) + " uf " +
				"WHERE uf.id=? and sg.geometry is not null)";
	}

	@Override
	public String getSurfaceGeometryRefIdsQuery(int lod) {
		return "select uf.area_id, uf.objectclass_id, " +
				helper.getSQLQueries().getImplicitGeometryNullColumns() +
				"from " + schema + "." + schemaMapper.getTableName(ADETable.URBANFUNCTION) + " uf " +
				"WHERE uf.id=? and uf.area_id is not null";
	}

}
