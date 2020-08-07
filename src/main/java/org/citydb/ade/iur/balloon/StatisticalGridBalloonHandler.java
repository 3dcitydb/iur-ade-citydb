package org.citydb.ade.iur.balloon;

import org.citydb.ade.iur.schema.ADETable;
import org.citydb.ade.iur.schema.SchemaMapper;
import org.citydb.modules.kml.ade.ADEBalloonHandler;

public class StatisticalGridBalloonHandler implements ADEBalloonHandler {
	private final SchemaMapper schemaMapper;

	public StatisticalGridBalloonHandler(BalloonManager manager) {
		this.schemaMapper = manager.getSchemaMapper();
	}

	@Override
	public String getSqlStatement(String table,
	                              String tableShortId,
	                              String aggregateColumnsClause,
	                              int lod,
	                              String schemaName) {

		String sqlStatement = null;

		if (schemaMapper.getTableName(ADETable.STATISTICALGRID).equalsIgnoreCase(table)
				|| schemaMapper.getTableName(ADETable.POPULATION).equalsIgnoreCase(table)
				|| schemaMapper.getTableName(ADETable.HOUSEHOLDS).equalsIgnoreCase(table)
				|| schemaMapper.getTableName(ADETable.OFFICESANDEMPLOYEES).equalsIgnoreCase(table)) {
			sqlStatement = "SELECT " + aggregateColumnsClause +
					" FROM " + schemaName + "." + table + " " + tableShortId +
					" WHERE " + tableShortId + ".id = ?";
		} else if (schemaMapper.getTableName(ADETable.POPULATIONBYAGEANDSEX).equalsIgnoreCase(table)) {
			sqlStatement = "SELECT " + aggregateColumnsClause +
					" FROM " + schemaName + "." + table + " " + tableShortId +
					" WHERE " + tableShortId + ".population_populationbyag_id = ?";
		} else if (schemaMapper.getTableName(ADETable.NUMBEROFHOUSEHOLDS).equalsIgnoreCase(table)) {
			sqlStatement = "SELECT " + aggregateColumnsClause +
					" FROM " + schemaName + "." + table + " " + tableShortId +
					" WHERE " + tableShortId + ".households_numberofhous_id_1 = ? OR " + tableShortId + ".households_numberofhouseh_id = ?";
		} else if (schemaMapper.getTableName(ADETable.LANDPRICEPERLANDUSE).equalsIgnoreCase(table)) {
			sqlStatement = "SELECT " + aggregateColumnsClause +
					" FROM " + schemaName + "." + table + " " + tableShortId +
					" WHERE " + tableShortId + ".statisticalgrid_landprice_id = ?";
		} else if (schemaMapper.getTableName(ADETable.NUMBEROFANNUALDIVERSIO).equalsIgnoreCase(table)) {
			sqlStatement = "SELECT " + aggregateColumnsClause +
					" FROM " + schemaName + "." + table + " " + tableShortId +
					" WHERE " + tableShortId + ".statisticalg_numberofannu_id = ?";
		}

		return sqlStatement;
	}

}
