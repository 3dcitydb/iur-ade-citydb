package org.citydb.ade.iur.balloon;

import org.citydb.ade.iur.schema.ADETable;
import org.citydb.ade.iur.schema.SchemaMapper;
import org.citydb.modules.kml.ade.ADEBalloonHandler;

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
				|| schemaMapper.getTableName(ADETable.PUBLICTRANSIT).equalsIgnoreCase(table)
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
