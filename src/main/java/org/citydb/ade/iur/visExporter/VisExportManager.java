/*
 * 3D City Database - The Open Source CityGML Database
 * https://www.3dcitydb.org/
 *
 * Copyright 2013 - 2024
 * Chair of Geoinformatics
 * Technical University of Munich, Germany
 * https://www.lrg.tum.de/gis/
 *
 * The 3D City Database is jointly developed with the following
 * cooperation partners:
 *
 * Virtual City Systems, Berlin <https://vc.systems/>
 * M.O.S.S. Computer Grafik Systeme GmbH, Taufkirchen <http://www.moss.de/>
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

package org.citydb.ade.iur.visExporter;

import org.citydb.ade.iur.schema.SchemaMapper;
import org.citydb.core.ade.visExporter.ADEVisExportException;
import org.citydb.core.ade.visExporter.ADEVisExportHelper;
import org.citydb.core.ade.visExporter.ADEVisExportManager;
import org.citydb.core.ade.visExporter.ADEVisExporter;
import org.citydb.core.util.Util;
import org.citygml4j.ade.iur.model.urf.UrbanFunction;
import org.citygml4j.ade.iur.model.urg.StatisticalGrid;
import org.citygml4j.ade.iur.model.urt.Route;
import org.citygml4j.ade.iur.model.urt.Shape;
import org.citygml4j.ade.iur.model.urt.Stop;
import org.citygml4j.ade.iur.model.urt.Trip;
import org.citygml4j.model.gml.base.AbstractGML;
import org.citygml4j.model.module.citygml.CityGMLVersion;

import java.util.HashMap;
import java.util.Map;

public class VisExportManager implements ADEVisExportManager {
    private final SchemaMapper schemaMapper;
    private final Map<Class<? extends ADEVisExporter>, ADEVisExporter> exporters;
    private ADEVisExportHelper helper;

    public VisExportManager(SchemaMapper schemaMapper) {
        this.schemaMapper = schemaMapper;
        exporters = new HashMap<>();
    }

    @Override
    public void init(ADEVisExportHelper helper) {
        this.helper = helper;
    }

    @Override
    public ADEVisExporter getVisExporter(int objectClassId) throws ADEVisExportException {
        AbstractGML modelObject = Util.createObject(objectClassId, CityGMLVersion.v2_0_0);
        ADEVisExporter exporter = null;

        if (modelObject instanceof UrbanFunction) {
            exporter = getVisExporter(UrbanFunctionVisExporter.class);
        } else if (modelObject instanceof StatisticalGrid) {
            exporter = getVisExporter(StatisticalGridVisExporter.class);
        } else if (modelObject instanceof Route) {
            exporter = getVisExporter(RouteVisExporter.class);
        } else if (modelObject instanceof Shape) {
            exporter = getVisExporter(ShapeVisExporter.class);
        } else if (modelObject instanceof Stop) {
            exporter = getVisExporter(StopVisExporter.class);
        } else if (modelObject instanceof Trip) {
            exporter = getVisExporter(TripVisExporter.class);
        }

        return exporter;
    }

    public SchemaMapper getSchemaMapper() {
        return schemaMapper;
    }

    private <T extends ADEVisExporter> T getVisExporter(Class<T> type) throws ADEVisExportException {
        ADEVisExporter exporter = exporters.get(type);

        if (exporter == null) {
            if (type == StatisticalGridVisExporter.class) {
                exporter = new StatisticalGridVisExporter(helper, this);
            } else if (type == UrbanFunctionVisExporter.class) {
                exporter = new UrbanFunctionVisExporter(helper, this);
            } else if (type == RouteVisExporter.class) {
                exporter = new RouteVisExporter(helper, this);
            } else if (type == ShapeVisExporter.class) {
                exporter = new ShapeVisExporter(helper, this);
            } else if (type == StopVisExporter.class) {
                exporter = new StopVisExporter(helper, this);
            } else if (type == TripVisExporter.class) {
                exporter = new TripVisExporter(helper, this);
            }

            if (exporter == null)
                throw new ADEVisExportException("Failed to build ADE KML exporter of type " + type.getName() + ".");

            exporters.put(type, exporter);
        }

        return type.cast(exporter);
    }
}
