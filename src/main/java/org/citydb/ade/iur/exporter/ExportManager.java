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

package org.citydb.ade.iur.exporter;

import org.citydb.ade.exporter.ADEExportManager;
import org.citydb.ade.exporter.ADEExporter;
import org.citydb.ade.exporter.CityGMLExportHelper;
import org.citydb.ade.iur.schema.ADETable;
import org.citydb.ade.iur.schema.ObjectMapper;
import org.citydb.ade.iur.schema.SchemaMapper;
import org.citydb.citygml.exporter.CityGMLExportException;
import org.citydb.database.schema.mapping.AbstractObjectType;
import org.citydb.database.schema.mapping.FeatureType;
import org.citydb.query.filter.projection.ProjectionFilter;
import org.citygml4j.model.citygml.ade.binding.ADEModelObject;
import org.citygml4j.model.citygml.building.AbstractBuilding;
import org.citygml4j.model.citygml.cityobjectgroup.CityObjectGroup;
import org.citygml4j.model.citygml.landuse.LandUse;
import org.citygml4j.model.citygml.transportation.TransportationComplex;
import org.citygml4j.model.gml.feature.AbstractFeature;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.citydb.ade.iur.exporter.urf.CensusBlockExporter;
import org.citydb.ade.iur.exporter.urf.DevelopmentProjectExporter;
import org.citydb.ade.iur.exporter.urf.DisasterDamageExporter;
import org.citydb.ade.iur.exporter.urf.PollutionExporter;
import org.citydb.ade.iur.exporter.urf.PublicTransitExporter;
import org.citydb.ade.iur.exporter.urf.RecreationsExporter;
import org.citydb.ade.iur.exporter.urf.UrbanFunctionExporter;
import org.citydb.ade.iur.exporter.urf.UrbanizationExporter;
import org.citydb.ade.iur.exporter.urg.HouseholdsExporter;
import org.citydb.ade.iur.exporter.urg.LandPriceExporter;
import org.citydb.ade.iur.exporter.urg.LandUseDiversionExporter;
import org.citydb.ade.iur.exporter.urg.OfficesAndEmployeesExporter;
import org.citydb.ade.iur.exporter.urg.PopulationExporter;
import org.citydb.ade.iur.exporter.urg.StatisticalGridExporter;
import org.citydb.ade.iur.exporter.uro.BuildingPropertiesExporter;
import org.citydb.ade.iur.exporter.uro.CityObjectGroupPropertiesExporter;
import org.citydb.ade.iur.exporter.uro.LandUsePropertiesExporter;
import org.citydb.ade.iur.exporter.uro.TransportationComplexPropertiesExporter;
import org.citygml4j.ade.iur.model.urf.CensusBlock;
import org.citygml4j.ade.iur.model.urf.DevelopmentProject;
import org.citygml4j.ade.iur.model.urf.DisasterDamage;
import org.citygml4j.ade.iur.model.urf.Pollution;
import org.citygml4j.ade.iur.model.urf.PublicTransit;
import org.citygml4j.ade.iur.model.urf.Recreations;
import org.citygml4j.ade.iur.model.urf.UrbanFunction;
import org.citygml4j.ade.iur.model.urf.Urbanization;
import org.citygml4j.ade.iur.model.urg.Households;
import org.citygml4j.ade.iur.model.urg.LandPrice;
import org.citygml4j.ade.iur.model.urg.LandUseDiversion;
import org.citygml4j.ade.iur.model.urg.OfficesAndEmployees;
import org.citygml4j.ade.iur.model.urg.Population;
import org.citygml4j.ade.iur.model.urg.StatisticalGrid;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ExportManager implements ADEExportManager {
    private final ObjectMapper objectMapper;
    private final SchemaMapper schemaMapper;
    private final Map<Class<? extends ADEExporter>, ADEExporter> exporters;

    private Connection connection;
    private CityGMLExportHelper helper;
    private DocumentBuilder builder;

    public ExportManager(ObjectMapper objectMapper, SchemaMapper schemaMapper) {
        this.objectMapper = objectMapper;
        this.schemaMapper = schemaMapper;
        exporters = new HashMap<>();
    }

    @Override
    public void init(Connection connection, CityGMLExportHelper helper) throws CityGMLExportException, SQLException {
        this.connection = connection;
        this.helper = helper;
    }

    @Override
    public void exportObject(ADEModelObject object, long objectId, AbstractObjectType<?> objectType, ProjectionFilter projectionFilter) throws CityGMLExportException, SQLException {
        if (object instanceof CensusBlock)
            getExporter(CensusBlockExporter.class).doExport((CensusBlock) object, objectId, objectType, projectionFilter);
        else if (object instanceof DisasterDamage)
            getExporter(DisasterDamageExporter.class).doExport((DisasterDamage) object, objectId, objectType, projectionFilter);
        else if (object instanceof Pollution)
            getExporter(PollutionExporter.class).doExport((Pollution) object, objectId, objectType, projectionFilter);
        else if (object instanceof PublicTransit)
            getExporter(PublicTransitExporter.class).doExport((PublicTransit) object, objectId, objectType, projectionFilter);
        else if (object instanceof Recreations)
            getExporter(RecreationsExporter.class).doExport((Recreations) object, objectId, objectType, projectionFilter);
        else if (object instanceof Urbanization)
            getExporter(UrbanizationExporter.class).doExport((Urbanization) object, objectId, objectType, projectionFilter);
        else if (object instanceof DevelopmentProject)
            getExporter(DevelopmentProjectExporter.class).doExport((DevelopmentProject) object, objectId, objectType, projectionFilter);
        else if (object instanceof UrbanFunction)
            getExporter(UrbanFunctionExporter.class).doExport((UrbanFunction) object, objectId, objectType, projectionFilter);
        else if (object instanceof Households)
            getExporter(HouseholdsExporter.class).doExport((Households) object, objectId, objectType, projectionFilter);
        else if (object instanceof LandPrice)
            getExporter(LandPriceExporter.class).doExport((LandPrice) object, objectId, objectType, projectionFilter);
        else if (object instanceof LandUseDiversion)
            getExporter(LandUseDiversionExporter.class).doExport((LandUseDiversion) object, objectId, objectType, projectionFilter);
        else if (object instanceof OfficesAndEmployees)
            getExporter(OfficesAndEmployeesExporter.class).doExport((OfficesAndEmployees) object, objectId, objectType, projectionFilter);
        else if (object instanceof Population)
            getExporter(PopulationExporter.class).doExport((Population) object, objectId, objectType, projectionFilter);
        else if (object instanceof StatisticalGrid)
            getExporter(StatisticalGridExporter.class).doExport((StatisticalGrid) object, objectId, objectType, projectionFilter);
    }

    @Override
    public void exportGenericApplicationProperties(String adeHookTable, AbstractFeature parent, long parentId, FeatureType parentType, ProjectionFilter projectionFilter) throws CityGMLExportException, SQLException {
        if (adeHookTable.equals(schemaMapper.getTableName(ADETable.BUILDING)) && parent instanceof AbstractBuilding)
            getExporter(BuildingPropertiesExporter.class).doExport((AbstractBuilding) parent, parentId, parentType, projectionFilter);
        else if (adeHookTable.equals(schemaMapper.getTableName(ADETable.LAND_USE)) && parent instanceof LandUse)
            getExporter(LandUsePropertiesExporter.class).doExport((LandUse) parent, parentId, parentType, projectionFilter);
        else if (adeHookTable.equals(schemaMapper.getTableName(ADETable.TRANSPORTATION_COMPLEX)) && parent instanceof TransportationComplex)
            getExporter(TransportationComplexPropertiesExporter.class).doExport((TransportationComplex) parent, parentId, parentType, projectionFilter);
        else if (adeHookTable.equals(schemaMapper.getTableName(ADETable.CITYOBJECTGROUP)) && parent instanceof CityObjectGroup)
            getExporter(CityObjectGroupPropertiesExporter.class).doExport((CityObjectGroup) parent, parentId, parentType, projectionFilter);
    }

    @Override
    public void close() throws CityGMLExportException, SQLException {
        for (ADEExporter exporter : exporters.values())
            exporter.close();
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public SchemaMapper getSchemaMapper() {
        return schemaMapper;
    }

    public Element unmarshal(String element) throws ParserConfigurationException, IOException, SAXException {
        if (builder == null) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            builder = factory.newDocumentBuilder();
        }

        try {
            return builder.parse(new InputSource(new StringReader(element))).getDocumentElement();
        } finally {
            builder.reset();
        }
    }

    public <T extends ADEExporter> T getExporter(Class<T> type) throws CityGMLExportException, SQLException {
        ADEExporter exporter = exporters.get(type);

        if (exporter == null) {
            // urban function module
            if (type == UrbanFunctionExporter.class)
                exporter = new UrbanFunctionExporter(connection, helper, this);
            else if (type == CensusBlockExporter.class)
                exporter = new CensusBlockExporter(connection, helper, this);
            else if (type == DisasterDamageExporter.class)
                exporter = new DisasterDamageExporter(connection, helper, this);
            else if (type == PollutionExporter.class)
                exporter = new PollutionExporter(connection, helper, this);
            else if (type == PublicTransitExporter.class)
                exporter = new PublicTransitExporter(connection, helper, this);
            else if (type == RecreationsExporter.class)
                exporter = new RecreationsExporter(connection, helper, this);
            else if (type == UrbanizationExporter.class)
                exporter = new UrbanizationExporter(connection, helper, this);
            else if (type == DevelopmentProjectExporter.class)
                exporter = new DevelopmentProjectExporter(connection, helper, this);

            // statistical grid module
            else if (type == StatisticalGridExporter.class)
                exporter = new StatisticalGridExporter(connection, helper, this);
            else if (type == HouseholdsExporter.class)
                exporter = new HouseholdsExporter(connection, helper, this);
            else if (type == LandPriceExporter.class)
                exporter = new LandPriceExporter(connection, helper, this);
            else if (type == LandUseDiversionExporter.class)
                exporter = new LandUseDiversionExporter(connection, helper, this);
            else if (type == OfficesAndEmployeesExporter.class)
                exporter = new OfficesAndEmployeesExporter(connection, helper, this);
            else if (type == PopulationExporter.class)
                exporter = new PopulationExporter(connection, helper, this);

            // urban object module
            else if (type == BuildingPropertiesExporter.class)
                exporter = new BuildingPropertiesExporter(connection, helper, this);
            else if (type == LandUsePropertiesExporter.class)
                exporter = new LandUsePropertiesExporter(connection, helper, this);
            else if (type == TransportationComplexPropertiesExporter.class)
                exporter = new TransportationComplexPropertiesExporter(connection, helper, this);
            else if (type == CityObjectGroupPropertiesExporter.class)
                exporter = new CityObjectGroupPropertiesExporter(connection, helper, this);

            if (exporter == null)
                throw new SQLException("Failed to build ADE exporter of type " + type.getName() + ".");

            exporters.put(type, exporter);
        }

        return type.cast(exporter);
    }
}
