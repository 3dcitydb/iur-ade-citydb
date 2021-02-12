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

import org.citydb.ade.iur.schema.SchemaMapper;
import org.citydb.ade.kmlExporter.ADEKmlExportException;
import org.citydb.ade.kmlExporter.ADEKmlExportHelper;
import org.citydb.ade.kmlExporter.ADEKmlExportManager;
import org.citydb.ade.kmlExporter.ADEKmlExporter;
import org.citydb.util.Util;
import org.citygml4j.ade.iur.model.urf.UrbanFunction;
import org.citygml4j.ade.iur.model.urg.StatisticalGrid;
import org.citygml4j.model.gml.base.AbstractGML;
import org.citygml4j.model.module.citygml.CityGMLVersion;

import java.util.HashMap;
import java.util.Map;

public class KMLExportManager implements ADEKmlExportManager {
	private final SchemaMapper schemaMapper;
	private final Map<Class<? extends ADEKmlExporter>, ADEKmlExporter> exporters;
	private ADEKmlExportHelper helper;

	public KMLExportManager(SchemaMapper schemaMapper) {
		this.schemaMapper = schemaMapper;
		exporters = new HashMap<>();
	}

	@Override
	public void init(ADEKmlExportHelper helper) {
		this.helper = helper;
	}

	@Override
	public ADEKmlExporter getKmlExporter(int objectClassId) throws ADEKmlExportException {
		AbstractGML modelObject = Util.createObject(objectClassId, CityGMLVersion.v2_0_0);
		ADEKmlExporter exporter = null;

		if (modelObject instanceof UrbanFunction) {
			exporter = getKmlExporter(UrbanFunctionKmlExporter.class);
		} else if (modelObject instanceof StatisticalGrid) {
			exporter = getKmlExporter(StatisticalGridKmlExporter.class);
		}

		return exporter;
	}

	public SchemaMapper getSchemaMapper() {
		return schemaMapper;
	}

	private <T extends ADEKmlExporter> T getKmlExporter(Class<T> type) throws ADEKmlExportException {
		ADEKmlExporter exporter = exporters.get(type);

		if (exporter == null) {
			if (type == StatisticalGridKmlExporter.class) {
				exporter = new StatisticalGridKmlExporter(helper, this);
			} else if (type == UrbanFunctionKmlExporter.class) {
				exporter = new UrbanFunctionKmlExporter(helper, this);
			}

			if (exporter == null)
				throw new ADEKmlExportException("Failed to build ADE KML exporter of type " + type.getName() + ".");

			exporters.put(type, exporter);
		}

		return type.cast(exporter);
	}
}
