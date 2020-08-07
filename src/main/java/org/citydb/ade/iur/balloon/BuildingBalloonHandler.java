package org.citydb.ade.iur.balloon;

import org.citydb.ade.iur.schema.ADETable;
import org.citydb.ade.iur.schema.SchemaMapper;
import org.citydb.modules.kml.ade.ADEBalloonHandler;

public class BuildingBalloonHandler implements ADEBalloonHandler {
	private final SchemaMapper schemaMapper;

	public BuildingBalloonHandler(BalloonManager manager) {
		this.schemaMapper = manager.getSchemaMapper();
	}

	@Override
	public String getSqlStatement(String table,
	                              String tableShortId,
	                              String aggregateColumnsClause,
	                              int lod,
	                              String schemaName) {

		String sqlStatement = null;

		if (schemaMapper.getTableName(ADETable.BUILDING).equalsIgnoreCase(table)) {
			sqlStatement = "SELECT " + aggregateColumnsClause +
					" FROM " + schemaName + "." + table + " " + tableShortId +
					" WHERE " + tableShortId + ".id = ?";
		} else if (schemaMapper.getTableName(ADETable.BUILDINGDETAILS).equalsIgnoreCase(table)) {
			sqlStatement = "SELECT " + aggregateColumnsClause +
					" FROM " + schemaName + "." + table + " " + tableShortId + ", " + schemaName + "." + schemaMapper.getTableName(ADETable.BUILDING) + " ub" +
					" WHERE " + tableShortId + ".id = ub.buildingdetails_id" +
					" AND ub.id = ?";
		} else if (schemaMapper.getTableName(ADETable.LARGECUSTOMERFACILITIE).equalsIgnoreCase(table)) {
			sqlStatement = "SELECT " + aggregateColumnsClause +
					" FROM " + schemaName + "." + table + " " + tableShortId + ", " + schemaName + "." + schemaMapper.getTableName(ADETable.BUILDING) + " ub" +
					" WHERE " + tableShortId + ".id = ub.largecustomerfacilitiespr_id" +
					" AND ub.id = ?";
		}

		return sqlStatement;
	}

}
