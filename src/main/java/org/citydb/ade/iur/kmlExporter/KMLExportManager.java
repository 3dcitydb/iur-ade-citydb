package org.citydb.ade.iur.kmlExporter;

import org.citydb.modules.kml.ade.ADEKmlExportException;
import org.citydb.modules.kml.ade.ADEKmlExportHelper;
import org.citydb.modules.kml.ade.ADEKmlExportManager;
import org.citydb.modules.kml.ade.ADEKmlExporter;
import org.citydb.util.Util;
import org.citygml4j.model.gml.base.AbstractGML;
import org.citygml4j.model.module.citygml.CityGMLVersion;
import org.citydb.ade.iur.schema.SchemaMapper;
import org.citygml4j.ade.iur.model.urf.UrbanFunction;
import org.citygml4j.ade.iur.model.urg.StatisticalGrid;

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
