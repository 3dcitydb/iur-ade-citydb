package org.citydb.ade.iur.balloon;

import org.citydb.ade.iur.schema.ADETable;
import org.citydb.ade.iur.schema.SchemaMapper;
import org.citydb.modules.kml.ade.ADEBalloonHandler;

public class TransportationBalloonHandler implements ADEBalloonHandler {
	private final SchemaMapper schemaMapper;

	public TransportationBalloonHandler(BalloonManager manager) {
		this.schemaMapper = manager.getSchemaMapper();
	}

	@Override
	public String getSqlStatement(String table,
	                              String tableShortId,
	                              String aggregateColumnsClause,
	                              int lod,
	                              String schemaName) {

		String sqlStatement = null;

		if (schemaMapper.getTableName(ADETable.TRANSPORTATION_COMPLEX).equalsIgnoreCase(table)) {
			sqlStatement = "SELECT " + aggregateColumnsClause +
					" FROM " + schemaName + "." + table + " " + tableShortId +
					" WHERE " + tableShortId + ".id = ?";
		} else if (schemaMapper.getTableName(ADETable.TRAFFICVOLUME).equalsIgnoreCase(table)) {
			sqlStatement = "SELECT " + aggregateColumnsClause +
					" FROM " + schemaName + "." + table + " " + tableShortId + ", " + schemaName + "." + schemaMapper.getTableName(ADETable.TRANSPORTATION_COMPLEX) + " utc" +
					" WHERE " + tableShortId + ".id = utc.trafficvolume_id" +
					" AND utc.id = ?";
		}

		return sqlStatement;
	}

}
