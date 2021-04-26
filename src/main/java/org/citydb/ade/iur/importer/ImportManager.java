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

package org.citydb.ade.iur.importer;

import org.citydb.ade.ADEExtension;
import org.citydb.ade.importer.ADEImportManager;
import org.citydb.ade.importer.ADEImporter;
import org.citydb.ade.importer.ADEPropertyCollection;
import org.citydb.ade.importer.CityGMLImportHelper;
import org.citydb.ade.importer.ForeignKeys;
import org.citydb.ade.iur.importer.urf.CensusBlockImporter;
import org.citydb.ade.iur.importer.urf.DevelopmentProjectImporter;
import org.citydb.ade.iur.importer.urf.DisasterDamageImporter;
import org.citydb.ade.iur.importer.urf.LegalGroundsImporter;
import org.citydb.ade.iur.importer.urf.PollutionImporter;
import org.citydb.ade.iur.importer.urf.PublicTransportationFacilityImporter;
import org.citydb.ade.iur.importer.urf.RecreationsImporter;
import org.citydb.ade.iur.importer.urf.UrbanFunctionImporter;
import org.citydb.ade.iur.importer.urf.UrbanFunctionModuleImporter;
import org.citydb.ade.iur.importer.urf.UrbanFunctionToCityObjectImporter;
import org.citydb.ade.iur.importer.urf.UrbanizationImporter;
import org.citydb.ade.iur.importer.urf.ZoneImporter;
import org.citydb.ade.iur.importer.urg.AreaOfAnnualDiversionsImporter;
import org.citydb.ade.iur.importer.urg.HouseholdsImporter;
import org.citydb.ade.iur.importer.urg.LandPriceImporter;
import org.citydb.ade.iur.importer.urg.LandPricePerLandUseImporter;
import org.citydb.ade.iur.importer.urg.LandUseDiversionImporter;
import org.citydb.ade.iur.importer.urg.NumberOfAnnualDiversionsImporter;
import org.citydb.ade.iur.importer.urg.OfficesAndEmployeesImporter;
import org.citydb.ade.iur.importer.urg.PopulationByAgeAndSexImporter;
import org.citydb.ade.iur.importer.urg.PopulationImporter;
import org.citydb.ade.iur.importer.urg.StatisticalGridImporter;
import org.citydb.ade.iur.importer.urg.StatisticalGridModuleImporter;
import org.citydb.ade.iur.importer.uro.BuildingDetailsImporter;
import org.citydb.ade.iur.importer.uro.BuildingPropertiesImporter;
import org.citydb.ade.iur.importer.uro.LandUsePropertiesImporter;
import org.citydb.ade.iur.importer.uro.LargeCustomerFacilitiesImporter;
import org.citydb.ade.iur.importer.uro.TrafficVolumeImporter;
import org.citydb.ade.iur.importer.uro.TransportationComplexPropertiesImporter;
import org.citydb.ade.iur.importer.uro.UrbanObjectModuleImporter;
import org.citydb.ade.iur.importer.urt.AgencyImporter;
import org.citydb.ade.iur.importer.urt.AttributionImporter;
import org.citydb.ade.iur.importer.urt.CalendarDateImporter;
import org.citydb.ade.iur.importer.urt.CalendarImporter;
import org.citydb.ade.iur.importer.urt.DescriptionImporter;
import org.citydb.ade.iur.importer.urt.FareAttributeImporter;
import org.citydb.ade.iur.importer.urt.FareRuleImporter;
import org.citydb.ade.iur.importer.urt.FeedInfoImporter;
import org.citydb.ade.iur.importer.urt.FrequencyImporter;
import org.citydb.ade.iur.importer.urt.OfficeImporter;
import org.citydb.ade.iur.importer.urt.PathwayImporter;
import org.citydb.ade.iur.importer.urt.PointImporter;
import org.citydb.ade.iur.importer.urt.PublicTransitDataTypeImporter;
import org.citydb.ade.iur.importer.urt.PublicTransitImporter;
import org.citydb.ade.iur.importer.urt.PublicTransitModuleImporter;
import org.citydb.ade.iur.importer.urt.RouteImporter;
import org.citydb.ade.iur.importer.urt.StopImporter;
import org.citydb.ade.iur.importer.urt.StopTimeImporter;
import org.citydb.ade.iur.importer.urt.TransferImporter;
import org.citydb.ade.iur.importer.urt.TranslationImporter;
import org.citydb.ade.iur.importer.urt.TranslationJPImporter;
import org.citydb.ade.iur.importer.urt.TripImporter;
import org.citydb.ade.iur.schema.ADETable;
import org.citydb.ade.iur.schema.ObjectMapper;
import org.citydb.ade.iur.schema.SchemaMapper;
import org.citydb.citygml.importer.CityGMLImportException;
import org.citydb.database.schema.mapping.AbstractObjectType;
import org.citydb.database.schema.mapping.FeatureType;
import org.citygml4j.ade.iur.model.module.PublicTransitModule;
import org.citygml4j.ade.iur.model.module.StatisticalGridModule;
import org.citygml4j.ade.iur.model.module.UrbanFunctionModule;
import org.citygml4j.ade.iur.model.urf.CensusBlock;
import org.citygml4j.ade.iur.model.urf.DevelopmentProject;
import org.citygml4j.ade.iur.model.urf.DisasterDamage;
import org.citygml4j.ade.iur.model.urf.Pollution;
import org.citygml4j.ade.iur.model.urf.PublicTransportationFacility;
import org.citygml4j.ade.iur.model.urf.Recreations;
import org.citygml4j.ade.iur.model.urf.UrbanFunction;
import org.citygml4j.ade.iur.model.urf.Urbanization;
import org.citygml4j.ade.iur.model.urf.Zone;
import org.citygml4j.ade.iur.model.urg.Households;
import org.citygml4j.ade.iur.model.urg.LandPrice;
import org.citygml4j.ade.iur.model.urg.LandUseDiversion;
import org.citygml4j.ade.iur.model.urg.OfficesAndEmployees;
import org.citygml4j.ade.iur.model.urg.Population;
import org.citygml4j.ade.iur.model.urg.StatisticalGrid;
import org.citygml4j.ade.iur.model.urt.Agency;
import org.citygml4j.ade.iur.model.urt.Attribution;
import org.citygml4j.ade.iur.model.urt.Calendar;
import org.citygml4j.ade.iur.model.urt.CalendarDate;
import org.citygml4j.ade.iur.model.urt.DataTypeProperty;
import org.citygml4j.ade.iur.model.urt.FareAttribute;
import org.citygml4j.ade.iur.model.urt.Office;
import org.citygml4j.ade.iur.model.urt.Pathway;
import org.citygml4j.ade.iur.model.urt.PublicTransit;
import org.citygml4j.ade.iur.model.urt.Route;
import org.citygml4j.ade.iur.model.urt.Stop;
import org.citygml4j.ade.iur.model.urt.TranslationJP;
import org.citygml4j.ade.iur.model.urt.Trip;
import org.citygml4j.model.citygml.ade.binding.ADEModelObject;
import org.citygml4j.model.citygml.building.AbstractBuilding;
import org.citygml4j.model.citygml.cityobjectgroup.CityObjectGroup;
import org.citygml4j.model.citygml.landuse.LandUse;
import org.citygml4j.model.citygml.transportation.TransportationComplex;
import org.citygml4j.model.gml.feature.AbstractFeature;
import org.w3c.dom.Node;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ImportManager implements ADEImportManager {
    private final ADEExtension adeExtension;
    private final ObjectMapper objectMapper;
    private final SchemaMapper schemaMapper;
    private final Map<Class<? extends ADEImporter>, ADEImporter> importers;

    private Connection connection;
    private CityGMLImportHelper helper;
    private Transformer transformer;

    public ImportManager(ADEExtension adeExtension, ObjectMapper objectMapper, SchemaMapper schemaMapper) {
        this.adeExtension = adeExtension;
        this.objectMapper = objectMapper;
        this.schemaMapper = schemaMapper;
        importers = new HashMap<>();
    }

    @Override
    public void init(Connection connection, CityGMLImportHelper helper) throws CityGMLImportException, SQLException {
        this.connection = connection;
        this.helper = helper;
    }

    @Override
    public void importObject(ADEModelObject object, long objectId, AbstractObjectType<?> objectType, ForeignKeys foreignKeys) throws CityGMLImportException, SQLException {
        if (UrbanFunctionModule.v1_4.getModelPackageName().equals(object.getClass().getPackage().getName())) {
            if (object instanceof CensusBlock)
                getImporter(CensusBlockImporter.class).doImport((CensusBlock) object, objectId, objectType, foreignKeys);
            else if (object instanceof DevelopmentProject)
                getImporter(DevelopmentProjectImporter.class).doImport((DevelopmentProject) object, objectId, objectType, foreignKeys);
            else if (object instanceof DisasterDamage)
                getImporter(DisasterDamageImporter.class).doImport((DisasterDamage) object, objectId, objectType, foreignKeys);
            else if (object instanceof Pollution)
                getImporter(PollutionImporter.class).doImport((Pollution) object, objectId, objectType, foreignKeys);
            else if (object instanceof PublicTransportationFacility)
                getImporter(PublicTransportationFacilityImporter.class).doImport((PublicTransportationFacility) object, objectId, objectType, foreignKeys);
            else if (object instanceof Recreations)
                getImporter(RecreationsImporter.class).doImport((Recreations) object, objectId, objectType, foreignKeys);
            else if (object instanceof Urbanization)
                getImporter(UrbanizationImporter.class).doImport((Urbanization) object, objectId, objectType, foreignKeys);
            else if (object instanceof Zone)
                getImporter(ZoneImporter.class).doImport((Zone) object, objectId, objectType, foreignKeys);
            else if (object instanceof UrbanFunction)
                getImporter(UrbanFunctionImporter.class).doImport((UrbanFunction) object, objectId, objectType, foreignKeys);
        } else if (StatisticalGridModule.v1_4.getModelPackageName().equals(object.getClass().getPackage().getName())) {
            if (object instanceof Households)
                getImporter(HouseholdsImporter.class).doImport((Households) object, objectId, objectType, foreignKeys);
            else if (object instanceof LandPrice)
                getImporter(LandPriceImporter.class).doImport((LandPrice) object, objectId, objectType, foreignKeys);
            else if (object instanceof LandUseDiversion)
                getImporter(LandUseDiversionImporter.class).doImport((LandUseDiversion) object, objectId, objectType, foreignKeys);
            else if (object instanceof OfficesAndEmployees)
                getImporter(OfficesAndEmployeesImporter.class).doImport((OfficesAndEmployees) object, objectId, objectType, foreignKeys);
            else if (object instanceof Population)
                getImporter(PopulationImporter.class).doImport((Population) object, objectId, objectType, foreignKeys);
            else if (object instanceof StatisticalGrid)
                getImporter(StatisticalGridImporter.class).doImport((StatisticalGrid) object, objectId, objectType, foreignKeys);
        } else if (PublicTransitModule.v1_4.getModelPackageName().equals(object.getClass().getPackage().getName())) {
            if (object instanceof Agency)
                getImporter(AgencyImporter.class).doImport((Agency) object, objectId, objectType, foreignKeys);
            else if (object instanceof Attribution)
                getImporter(AttributionImporter.class).doImport((Attribution) object, objectId, objectType, foreignKeys);
            else if (object instanceof Calendar)
                getImporter(CalendarImporter.class).doImport((Calendar) object, objectId, objectType, foreignKeys);
            else if (object instanceof CalendarDate)
                getImporter(CalendarDateImporter.class).doImport((CalendarDate) object, objectId, objectType, foreignKeys);
            else if (object instanceof FareAttribute)
                getImporter(FareAttributeImporter.class).doImport((FareAttribute) object, objectId, objectType, foreignKeys);
            else if (object instanceof Office)
                getImporter(OfficeImporter.class).doImport((Office) object, objectId, objectType, foreignKeys);
            else if (object instanceof Pathway)
                getImporter(PathwayImporter.class).doImport((Pathway) object, objectId, objectType, foreignKeys);
            else if (object instanceof Route)
                getImporter(RouteImporter.class).doImport((Route) object, objectId, objectType, foreignKeys);
            else if (object instanceof Stop)
                getImporter(StopImporter.class).doImport((Stop) object, objectId, objectType, foreignKeys);
            else if (object instanceof TranslationJP)
                getImporter(TranslationJPImporter.class).doImport((TranslationJP) object, objectId, objectType, foreignKeys);
            else if (object instanceof Trip)
                getImporter(TripImporter.class).doImport((Trip) object, objectId, objectType, foreignKeys);
            else if (object instanceof PublicTransit)
                getImporter(PublicTransitImporter.class).doImport((PublicTransit) object, objectId, objectType, foreignKeys);
        }
    }

    @Override
    public void importGenericApplicationProperties(ADEPropertyCollection properties, AbstractFeature parent, long parentId, FeatureType parentType) throws CityGMLImportException, SQLException {
        if (parent instanceof AbstractBuilding)
            getImporter(BuildingPropertiesImporter.class).doImport(properties, (AbstractBuilding) parent, parentId, parentType);
        else if (parent instanceof LandUse)
            getImporter(LandUsePropertiesImporter.class).doImport(properties, (LandUse) parent, parentId, parentType);
        else if (parent instanceof TransportationComplex)
            getImporter(TransportationComplexPropertiesImporter.class).doImport(properties, (TransportationComplex) parent, parentId, parentType);
        else if (parent instanceof CityObjectGroup) {
            if (properties.containsOneOf(org.citygml4j.ade.iur.model.urf.FiscalYearOfPublicationProperty.class,
                    org.citygml4j.ade.iur.model.urf.LanguageProperty.class)) {
                getImporter(org.citydb.ade.iur.importer.urf.CityObjectGroupPropertiesImporter.class).doImport(properties, (CityObjectGroup) parent, parentId, parentType);
            }
            if (properties.containsOneOf(org.citygml4j.ade.iur.model.urg.FiscalYearOfPublicationProperty.class,
                    org.citygml4j.ade.iur.model.urg.LanguageProperty.class)) {
                getImporter(org.citydb.ade.iur.importer.urg.CityObjectGroupPropertiesImporter.class).doImport(properties, (CityObjectGroup) parent, parentId, parentType);
            }
            if (properties.containsOneOf(org.citygml4j.ade.iur.model.uro.FiscalYearOfPublicationProperty.class,
                    org.citygml4j.ade.iur.model.uro.LanguageProperty.class)) {
                getImporter(org.citydb.ade.iur.importer.uro.CityObjectGroupPropertiesImporter.class).doImport(properties, (CityObjectGroup) parent, parentId, parentType);
            }
            if (properties.contains(DataTypeProperty.class))
                getImporter(org.citydb.ade.iur.importer.urt.CityObjectGroupPropertiesImporter.class).doImport(properties, (CityObjectGroup) parent, parentId, parentType);
        }
    }

    @Override
    public void executeBatch(String tableName) throws CityGMLImportException, SQLException {
        ADETable adeTable = schemaMapper.fromTableName(tableName);
        if (adeTable != null) {
            ADEImporter importer = importers.get(adeTable.getImporterClass());
            if (importer != null)
                importer.executeBatch();
        } else
            throw new CityGMLImportException("The table " + tableName + " is not managed by the ADE extension for '" + adeExtension.getMetadata().getIdentifier() + "'.");
    }

    @Override
    public void close() throws CityGMLImportException, SQLException {
        for (ADEImporter importer : importers.values())
            importer.close();
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public SchemaMapper getSchemaMapper() {
        return schemaMapper;
    }

    public String marshal(Node node) throws TransformerException {
        if (transformer == null) {
            SAXTransformerFactory factory = (SAXTransformerFactory) TransformerFactory.newInstance();
            transformer = factory.newTransformer();
        }

        StringWriter writer = new StringWriter();
        transformer.setOutputProperty(OutputKeys.INDENT, "no");
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.transform(new DOMSource(node), new StreamResult(writer));
        transformer.reset();

        return writer.toString();
    }

    public <T extends ADEImporter> T getImporter(Class<T> type) throws CityGMLImportException, SQLException {
        ADEImporter importer = importers.get(type);

        if (importer == null) {
            if (UrbanFunctionModuleImporter.class.isAssignableFrom(type)) {
                // urban function module
                if (type == UrbanFunctionImporter.class)
                    importer = new UrbanFunctionImporter(connection, helper, this);
                else if (type == DevelopmentProjectImporter.class)
                    importer = new DevelopmentProjectImporter(connection, helper, this);
                else if (type == CensusBlockImporter.class)
                    importer = new CensusBlockImporter(connection, helper, this);
                else if (type == DisasterDamageImporter.class)
                    importer = new DisasterDamageImporter(connection, helper, this);
                else if (type == PollutionImporter.class)
                    importer = new PollutionImporter(connection, helper, this);
                else if (type == PublicTransportationFacilityImporter.class)
                    importer = new PublicTransportationFacilityImporter(connection, helper, this);
                else if (type == RecreationsImporter.class)
                    importer = new RecreationsImporter(connection, helper, this);
                else if (type == UrbanizationImporter.class)
                    importer = new UrbanizationImporter(connection, helper, this);
                else if (type == ZoneImporter.class)
                    importer = new ZoneImporter(connection, helper, this);
                else if (type == LegalGroundsImporter.class)
                    importer = new LegalGroundsImporter(connection, helper, this);
                else if (type == org.citydb.ade.iur.importer.urf.NumberOfHouseholdsImporter.class)
                    importer = new org.citydb.ade.iur.importer.urf.NumberOfHouseholdsImporter(connection, helper, this);
                else if (type == org.citydb.ade.iur.importer.urf.CityObjectGroupPropertiesImporter.class)
                    importer = new org.citydb.ade.iur.importer.urf.CityObjectGroupPropertiesImporter(connection, helper, this);
                else if (type == UrbanFunctionToCityObjectImporter.class)
                    importer = new UrbanFunctionToCityObjectImporter(connection, helper, this);
            } else if (StatisticalGridModuleImporter.class.isAssignableFrom(type)) {
                // statistical grid module
                if (type == StatisticalGridImporter.class)
                    importer = new StatisticalGridImporter(connection, helper, this);
                else if (type == HouseholdsImporter.class)
                    importer = new HouseholdsImporter(connection, helper, this);
                else if (type == LandPriceImporter.class)
                    importer = new LandPriceImporter(connection, helper, this);
                else if (type == LandUseDiversionImporter.class)
                    importer = new LandUseDiversionImporter(connection, helper, this);
                else if (type == OfficesAndEmployeesImporter.class)
                    importer = new OfficesAndEmployeesImporter(connection, helper, this);
                else if (type == PopulationImporter.class)
                    importer = new PopulationImporter(connection, helper, this);
                else if (type == org.citydb.ade.iur.importer.urg.NumberOfHouseholdsImporter.class)
                    importer = new org.citydb.ade.iur.importer.urg.NumberOfHouseholdsImporter(connection, helper, this);
                else if (type == LandPricePerLandUseImporter.class)
                    importer = new LandPricePerLandUseImporter(connection, helper, this);
                else if (type == AreaOfAnnualDiversionsImporter.class)
                    importer = new AreaOfAnnualDiversionsImporter(connection, helper, this);
                else if (type == NumberOfAnnualDiversionsImporter.class)
                    importer = new NumberOfAnnualDiversionsImporter(connection, helper, this);
                else if (type == PopulationByAgeAndSexImporter.class)
                    importer = new PopulationByAgeAndSexImporter(connection, helper, this);
                else if (type == org.citydb.ade.iur.importer.urg.KeyValuePairImporter.class)
                    importer = new org.citydb.ade.iur.importer.urg.KeyValuePairImporter(connection, helper, this);
                else if (type == org.citydb.ade.iur.importer.urg.CityObjectGroupPropertiesImporter.class)
                    importer = new org.citydb.ade.iur.importer.urg.CityObjectGroupPropertiesImporter(connection, helper, this);
            } else if (UrbanObjectModuleImporter.class.isAssignableFrom(type)) {
                // urban object module
                if (type == BuildingPropertiesImporter.class)
                    importer = new BuildingPropertiesImporter(connection, helper, this);
                else if (type == LandUsePropertiesImporter.class)
                    importer = new LandUsePropertiesImporter(connection, helper, this);
                else if (type == TransportationComplexPropertiesImporter.class)
                    importer = new TransportationComplexPropertiesImporter(connection, helper, this);
                else if (type == org.citydb.ade.iur.importer.uro.CityObjectGroupPropertiesImporter.class)
                    importer = new org.citydb.ade.iur.importer.uro.CityObjectGroupPropertiesImporter(connection, helper, this);
                else if (type == org.citydb.ade.iur.importer.uro.KeyValuePairImporter.class)
                    importer = new org.citydb.ade.iur.importer.uro.KeyValuePairImporter(connection, helper, this);
                else if (type == BuildingDetailsImporter.class)
                    importer = new BuildingDetailsImporter(connection, helper, this);
                else if (type == LargeCustomerFacilitiesImporter.class)
                    importer = new LargeCustomerFacilitiesImporter(connection, helper, this);
                else if (type == TrafficVolumeImporter.class)
                    importer = new TrafficVolumeImporter(connection, helper, this);
            } else if (PublicTransitModuleImporter.class.isAssignableFrom(type)) {
                // public transit module
                if (type == AgencyImporter.class)
                    importer = new AgencyImporter(connection, helper, this);
                else if (type == AttributionImporter.class)
                    importer = new AttributionImporter(connection, helper, this);
                else if (type == CalendarImporter.class)
                    importer = new CalendarImporter(connection, helper, this);
                else if (type == CalendarDateImporter.class)
                    importer = new CalendarDateImporter(connection, helper, this);
                else if (type == org.citydb.ade.iur.importer.urt.CityObjectGroupPropertiesImporter.class)
                    importer = new org.citydb.ade.iur.importer.urt.CityObjectGroupPropertiesImporter(connection, helper, this);
                else if (type == DescriptionImporter.class)
                    importer = new DescriptionImporter(connection, helper, this);
                else if (type == FareAttributeImporter.class)
                    importer = new FareAttributeImporter(connection, helper, this);
                else if (type == FareRuleImporter.class)
                    importer = new FareRuleImporter(connection, helper, this);
                else if (type == FeedInfoImporter.class)
                    importer = new FeedInfoImporter(connection, helper, this);
                else if (type == FrequencyImporter.class)
                    importer = new FrequencyImporter(connection, helper, this);
                else if (type == OfficeImporter.class)
                    importer = new OfficeImporter(connection, helper, this);
                else if (type == PathwayImporter.class)
                    importer = new PathwayImporter(connection, helper, this);
                else if (type == PointImporter.class)
                    importer = new PointImporter(connection, helper, this);
                else if (type == PublicTransitDataTypeImporter.class)
                    importer = new PublicTransitDataTypeImporter(connection, helper, this);
                else if (type == RouteImporter.class)
                    importer = new RouteImporter(connection, helper, this);
                else if (type == StopImporter.class)
                    importer = new StopImporter(connection, helper, this);
                else if (type == StopTimeImporter.class)
                    importer = new StopTimeImporter(connection, helper, this);
                else if (type == TransferImporter.class)
                    importer = new TransferImporter(connection, helper, this);
                else if (type == TranslationImporter.class)
                    importer = new TranslationImporter(connection, helper, this);
                else if (type == TranslationJPImporter.class)
                    importer = new TranslationJPImporter(connection, helper, this);
                else if (type == TripImporter.class)
                    importer = new TripImporter(connection, helper, this);
                else if (type == PublicTransitImporter.class)
                    importer = new PublicTransitImporter(connection, helper, this);
            }

            if (importer == null)
                throw new SQLException("Failed to build ADE importer of type " + type.getName() + ".");

            importers.put(type, importer);
        }

        return type.cast(importer);
    }
}
