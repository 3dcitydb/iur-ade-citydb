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

package org.citydb.ade.iur.balloon;

import org.citydb.ade.iur.schema.ADETableMapper;
import org.citydb.ade.iur.schema.SchemaMapper;
import org.citydb.ade.kmlExporter.ADEBalloonException;
import org.citydb.ade.kmlExporter.ADEBalloonHandler;
import org.citydb.ade.kmlExporter.ADEBalloonManager;
import org.citydb.util.Util;
import org.citygml4j.ade.iur.model.urf.UrbanFunction;
import org.citygml4j.ade.iur.model.urg.StatisticalGrid;
import org.citygml4j.model.citygml.building.AbstractBuilding;
import org.citygml4j.model.citygml.cityobjectgroup.CityObjectGroup;
import org.citygml4j.model.citygml.landuse.LandUse;
import org.citygml4j.model.citygml.transportation.TransportationComplex;
import org.citygml4j.model.gml.base.AbstractGML;
import org.citygml4j.model.module.citygml.CityGMLVersion;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BalloonManager implements ADEBalloonManager {
	private final ADETableMapper tableMapper;
	private final SchemaMapper schemaMapper;
	private final Map<Class<? extends ADEBalloonHandler>, ADEBalloonHandler> balloonHandlers;

	public BalloonManager(ADETableMapper tableMapper, SchemaMapper schemaMapper) {
		this.tableMapper = tableMapper;
		this.schemaMapper = schemaMapper;
		this.balloonHandlers = new HashMap<>();
	}

	@Override
	public ADEBalloonHandler getBalloonHandler(int objectClassId) throws ADEBalloonException {
		AbstractGML modelObject = Util.createObject(objectClassId, CityGMLVersion.v2_0_0);
		ADEBalloonHandler balloonHandler = null;

		if (modelObject instanceof AbstractBuilding) {
			balloonHandler = getBalloonHandler(BuildingBalloonHandler.class);
		} else if (modelObject instanceof LandUse) {
			balloonHandler = getBalloonHandler(LandUseBalloonHandler.class);
		} else if (modelObject instanceof TransportationComplex) {
			balloonHandler = getBalloonHandler(TransportationBalloonHandler.class);
		} else if (modelObject instanceof CityObjectGroup) {
			balloonHandler = getBalloonHandler(CityObjectGroupBalloonHandler.class);
		} else if (modelObject instanceof UrbanFunction) {
			balloonHandler = getBalloonHandler(UrbanFunctionBalloonHandler.class);
		} else if (modelObject instanceof StatisticalGrid) {
			balloonHandler = getBalloonHandler(StatisticalGridBalloonHandler.class);
		}

		return balloonHandler;
	}

	public SchemaMapper getSchemaMapper() {
		return schemaMapper;
	}

	private <T extends ADEBalloonHandler> T getBalloonHandler(Class<T> type) throws ADEBalloonException {
		ADEBalloonHandler balloonHandler = balloonHandlers.get(type);

		if (balloonHandler == null) {
			if (type == BuildingBalloonHandler.class) {
				balloonHandler = new BuildingBalloonHandler(this);
			} else if (type == LandUseBalloonHandler.class) {
				balloonHandler = new LandUseBalloonHandler(this);
			} else if (type == TransportationBalloonHandler.class) {
				balloonHandler = new TransportationBalloonHandler(this);
			} else if (type == CityObjectGroupBalloonHandler.class) {
				balloonHandler = new CityObjectGroupBalloonHandler(this);
			} else if (type == UrbanFunctionBalloonHandler.class) {
				balloonHandler = new UrbanFunctionBalloonHandler(this);
			} else if (type == StatisticalGridBalloonHandler.class) {
				balloonHandler = new StatisticalGridBalloonHandler(this);
			}

			if (balloonHandler == null)
				throw new ADEBalloonException("Failed to build ADE Balloon handler for the type " + type.getName() + ".");

			balloonHandlers.put(type, balloonHandler);
		}

		return type.cast(balloonHandler);
	}

	@Override
	public HashMap<String, Set<String>> getTablesAndColumns() {
		return tableMapper.getTablesAndColumns();
	}
}
