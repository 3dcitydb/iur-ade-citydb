/*
 * 3D City Database - The Open Source CityGML Database
 * https://www.3dcitydb.org/
 *
 * Copyright 2013 - 2021
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

package org.citydb.ade.iur.exporter;

import org.citydb.ade.iur.exporter.urf.*;
import org.citydb.ade.iur.exporter.urg.*;
import org.citydb.ade.iur.exporter.uro.BuildingPropertiesExporter;
import org.citydb.ade.iur.exporter.uro.LandUsePropertiesExporter;
import org.citydb.ade.iur.exporter.uro.TransportationComplexPropertiesExporter;
import org.citydb.ade.iur.exporter.uro.UrbanObjectModuleExporter;
import org.citydb.ade.iur.exporter.urt.*;
import org.citydb.ade.iur.schema.ADETable;
import org.citydb.ade.iur.schema.ObjectMapper;
import org.citydb.ade.iur.schema.SchemaMapper;
import org.citydb.core.ade.exporter.ADEExportManager;
import org.citydb.core.ade.exporter.ADEExporter;
import org.citydb.core.ade.exporter.CityGMLExportHelper;
import org.citydb.core.database.schema.mapping.AbstractObjectType;
import org.citydb.core.database.schema.mapping.FeatureType;
import org.citydb.core.operation.exporter.CityGMLExportException;
import org.citydb.core.query.filter.projection.ProjectionFilter;
import org.citygml4j.ade.iur.model.module.PublicTransitModule;
import org.citygml4j.ade.iur.model.module.StatisticalGridModule;
import org.citygml4j.ade.iur.model.module.UrbanFunctionModule;
import org.citygml4j.ade.iur.model.urf.*;
import org.citygml4j.ade.iur.model.urg.LandUseDiversion;
import org.citygml4j.ade.iur.model.urg.*;
import org.citygml4j.ade.iur.model.urt.*;
import org.citygml4j.model.citygml.ade.binding.ADEModelObject;
import org.citygml4j.model.citygml.building.AbstractBuilding;
import org.citygml4j.model.citygml.cityobjectgroup.CityObjectGroup;
import org.citygml4j.model.citygml.landuse.LandUse;
import org.citygml4j.model.citygml.transportation.TransportationComplex;
import org.citygml4j.model.gml.feature.AbstractFeature;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

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
        if (UrbanFunctionModule.v1_4.getModelPackageName().equals(object.getClass().getPackage().getName())) {
            if (object instanceof CensusBlock)
                getExporter(CensusBlockExporter.class).doExport((CensusBlock) object, objectId, objectType, projectionFilter);
            else if (object instanceof DisasterDamage)
                getExporter(DisasterDamageExporter.class).doExport((DisasterDamage) object, objectId, objectType, projectionFilter);
            else if (object instanceof DisasterPreventionBase)
                getExporter(DisasterPreventionBaseExporter.class).doExport((DisasterPreventionBase) object, objectId, objectType, projectionFilter);
            else if (object instanceof Pollution)
                getExporter(PollutionExporter.class).doExport((Pollution) object, objectId, objectType, projectionFilter);
            else if (object instanceof PublicTransportationFacility)
                getExporter(PublicTransportationFacilityExporter.class).doExport((PublicTransportationFacility) object, objectId, objectType, projectionFilter);
            else if (object instanceof Recreations)
                getExporter(RecreationsExporter.class).doExport((Recreations) object, objectId, objectType, projectionFilter);
            else if (object instanceof Urbanization)
                getExporter(UrbanizationExporter.class).doExport((Urbanization) object, objectId, objectType, projectionFilter);
            else if (object instanceof DevelopmentProject)
                getExporter(DevelopmentProjectExporter.class).doExport((DevelopmentProject) object, objectId, objectType, projectionFilter);
            else if (object instanceof Zone)
                getExporter(ZoneExporter.class).doExport((Zone) object, objectId, objectType, projectionFilter);
            else if (object instanceof UrbanFunction)
                getExporter(UrbanFunctionExporter.class).doExport((UrbanFunction) object, objectId, objectType, projectionFilter);
        } else if (StatisticalGridModule.v1_4.getModelPackageName().equals(object.getClass().getPackage().getName())) {
            if (object instanceof GenericGridCell)
                getExporter(GenericGridCellExporter.class).doExport((GenericGridCell) object, objectId, objectType, projectionFilter);
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
            else if (object instanceof PublicTransitAccessibility)
                getExporter(PublicTransitAccessibilityExporter.class).doExport((PublicTransitAccessibility) object, objectId, objectType, projectionFilter);
        } else if (PublicTransitModule.v1_4.getModelPackageName().equals(object.getClass().getPackage().getName())) {
            if (object instanceof Agency)
                getExporter(AgencyExporter.class).doExport((Agency) object, objectId, objectType, projectionFilter);
            else if (object instanceof Attribution)
                getExporter(AttributionExporter.class).doExport((Attribution) object, objectId, objectType, projectionFilter);
            else if (object instanceof Calendar)
                getExporter(CalendarExporter.class).doExport((Calendar) object, objectId, objectType, projectionFilter);
            else if (object instanceof CalendarDate)
                getExporter(CalendarDateExporter.class).doExport((CalendarDate) object, objectId, objectType, projectionFilter);
            else if (object instanceof FareAttribute)
                getExporter(FareAttributeExporter.class).doExport((FareAttribute) object, objectId, objectType, projectionFilter);
            else if (object instanceof Level)
                getExporter(LevelExporter.class).doExport((Level) object, objectId, objectType, projectionFilter);
            else if (object instanceof Office)
                getExporter(OfficeExporter.class).doExport((Office) object, objectId, objectType, projectionFilter);
            else if (object instanceof Pathway)
                getExporter(PathwayExporter.class).doExport((Pathway) object, objectId, objectType, projectionFilter);
            else if (object instanceof Route)
                getExporter(RouteExporter.class).doExport((Route) object, objectId, objectType, projectionFilter);
            else if (object instanceof Shape)
                getExporter(ShapeExporter.class).doExport((Shape) object, objectId, objectType, projectionFilter);
            else if (object instanceof Stop)
                getExporter(StopExporter.class).doExport((Stop) object, objectId, objectType, projectionFilter);
            else if (object instanceof TranslationJP)
                getExporter(TranslationJPExporter.class).doExport((TranslationJP) object, objectId, objectType, projectionFilter);
            else if (object instanceof Trip)
                getExporter(TripExporter.class).doExport((Trip) object, objectId, objectType, projectionFilter);
        }
    }

    @Override
    public void exportGenericApplicationProperties(String adeHookTable, AbstractFeature parent, long parentId, FeatureType parentType, ProjectionFilter projectionFilter) throws CityGMLExportException, SQLException {
        if (adeHookTable.equals(schemaMapper.getTableName(ADETable.BUILDING)) && parent instanceof AbstractBuilding)
            getExporter(BuildingPropertiesExporter.class).doExport((AbstractBuilding) parent, parentId, parentType, projectionFilter);
        else if (adeHookTable.equals(schemaMapper.getTableName(ADETable.LAND_USE)) && parent instanceof LandUse)
            getExporter(LandUsePropertiesExporter.class).doExport((LandUse) parent, parentId, parentType, projectionFilter);
        else if (adeHookTable.equals(schemaMapper.getTableName(ADETable.TRANSPORTATION_COMPLEX)) && parent instanceof TransportationComplex)
            getExporter(TransportationComplexPropertiesExporter.class).doExport((TransportationComplex) parent, parentId, parentType, projectionFilter);
        else if (parent instanceof CityObjectGroup) {
            if (adeHookTable.equals(schemaMapper.getTableName(ADETable.CITYOBJECTGROUP)))
                getExporter(org.citydb.ade.iur.exporter.urg.CityObjectGroupPropertiesExporter.class).doExport((CityObjectGroup) parent, parentId, parentType, projectionFilter);
            else if (adeHookTable.equals(schemaMapper.getTableName(ADETable.CITYOBJECTGROUP_1)))
                getExporter(org.citydb.ade.iur.exporter.uro.CityObjectGroupPropertiesExporter.class).doExport((CityObjectGroup) parent, parentId, parentType, projectionFilter);
            else if (adeHookTable.equals(schemaMapper.getTableName(ADETable.CITYOBJECTGROUP_2)))
                getExporter(org.citydb.ade.iur.exporter.urf.CityObjectGroupPropertiesExporter.class).doExport((CityObjectGroup) parent, parentId, parentType, projectionFilter);
            else if (adeHookTable.equals(schemaMapper.getTableName(ADETable.CITYOBJECTGROUP_3)))
                getExporter(org.citydb.ade.iur.exporter.urt.CityObjectGroupPropertiesExporter.class).doExport((CityObjectGroup) parent, parentId, parentType, projectionFilter);
        }
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
            if (UrbanFunctionModuleExporter.class.isAssignableFrom(type)) {
                // urban function module
                if (type == UrbanFunctionExporter.class)
                    exporter = new UrbanFunctionExporter(connection, helper, this);
                else if (type == DevelopmentProjectExporter.class)
                    exporter = new DevelopmentProjectExporter(connection, helper, this);
                else if (type == CensusBlockExporter.class)
                    exporter = new CensusBlockExporter(connection, helper, this);
                else if (type == DisasterDamageExporter.class)
                    exporter = new DisasterDamageExporter(connection, helper, this);
                else if (type == DisasterPreventionBaseExporter.class)
                    exporter = new DisasterPreventionBaseExporter(connection, helper, this);
                else if (type == PollutionExporter.class)
                    exporter = new PollutionExporter(connection, helper, this);
                else if (type == PublicTransportationFacilityExporter.class)
                    exporter = new PublicTransportationFacilityExporter(connection, helper, this);
                else if (type == RecreationsExporter.class)
                    exporter = new RecreationsExporter(connection, helper, this);
                else if (type == UrbanizationExporter.class)
                    exporter = new UrbanizationExporter(connection, helper, this);
                else if (type == ZoneExporter.class)
                    exporter = new ZoneExporter(connection, helper, this);
                else if (type == org.citydb.ade.iur.exporter.urf.CityObjectGroupPropertiesExporter.class)
                    exporter = new org.citydb.ade.iur.exporter.urf.CityObjectGroupPropertiesExporter(connection, helper, this);
            } else if (StatisticalGridModuleExporter.class.isAssignableFrom(type)) {
                // statistical grid module
                if (type == StatisticalGridExporter.class)
                    exporter = new StatisticalGridExporter(connection, helper, this);
                else if (type == GenericGridCellExporter.class)
                    exporter = new GenericGridCellExporter(connection, helper, this);
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
                else if (type == PublicTransitAccessibilityExporter.class)
                    exporter = new PublicTransitAccessibilityExporter(connection, helper, this);
                else if (type == org.citydb.ade.iur.exporter.urg.CityObjectGroupPropertiesExporter.class)
                    exporter = new org.citydb.ade.iur.exporter.urg.CityObjectGroupPropertiesExporter(connection, helper, this);
            } else if (UrbanObjectModuleExporter.class.isAssignableFrom(type)) {
                // urban object module
                if (type == BuildingPropertiesExporter.class)
                    exporter = new BuildingPropertiesExporter(connection, helper, this);
                else if (type == LandUsePropertiesExporter.class)
                    exporter = new LandUsePropertiesExporter(connection, helper, this);
                else if (type == TransportationComplexPropertiesExporter.class)
                    exporter = new TransportationComplexPropertiesExporter(connection, helper, this);
                else if (type == org.citydb.ade.iur.exporter.uro.CityObjectGroupPropertiesExporter.class)
                    exporter = new org.citydb.ade.iur.exporter.uro.CityObjectGroupPropertiesExporter(connection, helper, this);
            } else if (PublicTransitModuleExporter.class.isAssignableFrom(type)) {
                // public transit module
                if (type == AgencyExporter.class)
                    exporter = new AgencyExporter(connection, helper, this);
                else if (type == AttributionExporter.class)
                    exporter = new AttributionExporter(connection, helper, this);
                else if (type == CalendarExporter.class)
                    exporter = new CalendarExporter(connection, helper, this);
                else if (type == CalendarDateExporter.class)
                    exporter = new CalendarDateExporter(connection, helper, this);
                else if (type == org.citydb.ade.iur.exporter.urt.CityObjectGroupPropertiesExporter.class)
                    exporter = new org.citydb.ade.iur.exporter.urt.CityObjectGroupPropertiesExporter(connection, helper, this);
                else if (type == FareAttributeExporter.class)
                    exporter = new FareAttributeExporter(connection, helper, this);
                else if (type == FareRuleExporter.class)
                    exporter = new FareRuleExporter(connection, helper, this);
                else if (type == FeedInfoExporter.class)
                    exporter = new FeedInfoExporter(connection, helper, this);
                else if (type == FrequencyExporter.class)
                    exporter = new FrequencyExporter(connection, helper, this);
                else if (type == LevelExporter.class)
                    exporter = new LevelExporter(connection, helper, this);
                else if (type == OfficeExporter.class)
                    exporter = new OfficeExporter(connection, helper, this);
                else if (type == PathwayExporter.class)
                    exporter = new PathwayExporter(connection, helper, this);
                else if (type == PublicTransitExporter.class)
                    exporter = new PublicTransitExporter(connection, helper, this);
                else if (type == RouteExporter.class)
                    exporter = new RouteExporter(connection, helper, this);
                else if (type == ShapeExporter.class)
                    exporter = new ShapeExporter(connection, helper, this);
                else if (type == StopExporter.class)
                    exporter = new StopExporter(connection, helper, this);
                else if (type == StopTimeExporter.class)
                    exporter = new StopTimeExporter(connection, helper, this);
                else if (type == TransferExporter.class)
                    exporter = new TransferExporter(connection, helper, this);
                else if (type == TranslationExporter.class)
                    exporter = new TranslationExporter(connection, helper, this);
                else if (type == TranslationJPExporter.class)
                    exporter = new TranslationJPExporter(connection, helper, this);
                else if (type == TripExporter.class)
                    exporter = new TripExporter(connection, helper, this);
            }

            if (exporter == null)
                throw new SQLException("Failed to build ADE exporter of type " + type.getName() + ".");

            exporters.put(type, exporter);
        }

        return type.cast(exporter);
    }
}
