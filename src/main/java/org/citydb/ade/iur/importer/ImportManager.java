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
import org.citydb.ade.iur.importer.urf.PublicTransitImporter;
import org.citydb.ade.iur.importer.urf.RecreationsImporter;
import org.citydb.ade.iur.importer.urf.UrbanFunctionImporter;
import org.citydb.ade.iur.importer.urf.UrbanFunctionToCityObjectImporter;
import org.citydb.ade.iur.importer.urf.UrbanizationImporter;
import org.citydb.ade.iur.importer.urg.HouseholdsImporter;
import org.citydb.ade.iur.importer.urg.LandPricePerLandUseImporter;
import org.citydb.ade.iur.importer.urg.NumberOfAnnualDiversionsImporter;
import org.citydb.ade.iur.importer.urg.NumberOfHouseholdsImporter;
import org.citydb.ade.iur.importer.urg.OfficesAndEmployeesImporter;
import org.citydb.ade.iur.importer.urg.PopulationByAgeAndSexImporter;
import org.citydb.ade.iur.importer.urg.PopulationImporter;
import org.citydb.ade.iur.importer.urg.StatisticalGridImporter;
import org.citydb.ade.iur.importer.uro.BuildingDetailsImporter;
import org.citydb.ade.iur.importer.uro.BuildingPropertiesImporter;
import org.citydb.ade.iur.importer.uro.CityObjectGroupPropertiesImporter;
import org.citydb.ade.iur.importer.uro.LandUsePropertiesImporter;
import org.citydb.ade.iur.importer.uro.LargeCustomerFacilitiesImporter;
import org.citydb.ade.iur.importer.uro.TrafficVolumeImporter;
import org.citydb.ade.iur.importer.uro.TransportationComplexPropertiesImporter;
import org.citydb.ade.iur.schema.ADETable;
import org.citydb.ade.iur.schema.SchemaMapper;
import org.citydb.citygml.importer.CityGMLImportException;
import org.citydb.database.schema.mapping.AbstractObjectType;
import org.citydb.database.schema.mapping.FeatureType;
import org.citygml4j.ade.iur.model.urf.CensusBlock;
import org.citygml4j.ade.iur.model.urf.DevelopmentProject;
import org.citygml4j.ade.iur.model.urf.DisasterDamage;
import org.citygml4j.ade.iur.model.urf.Pollution;
import org.citygml4j.ade.iur.model.urf.PublicTransit;
import org.citygml4j.ade.iur.model.urf.Recreations;
import org.citygml4j.ade.iur.model.urf.UrbanFunction;
import org.citygml4j.ade.iur.model.urf.Urbanization;
import org.citygml4j.ade.iur.model.urg.Households;
import org.citygml4j.ade.iur.model.urg.OfficesAndEmployees;
import org.citygml4j.ade.iur.model.urg.Population;
import org.citygml4j.ade.iur.model.urg.StatisticalGrid;
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
    private final SchemaMapper schemaMapper;
    private final Map<Class<? extends ADEImporter>, ADEImporter> importers;

    private Connection connection;
    private CityGMLImportHelper helper;
    private Transformer transformer;

    public ImportManager(ADEExtension adeExtension, SchemaMapper schemaMapper) {
        this.adeExtension = adeExtension;
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
        if (object instanceof CensusBlock)
            getImporter(CensusBlockImporter.class).doImport((CensusBlock) object, objectId, objectType, foreignKeys);
        else if (object instanceof DevelopmentProject)
            getImporter(DevelopmentProjectImporter.class).doImport((DevelopmentProject) object, objectId, objectType, foreignKeys);
        else if (object instanceof DisasterDamage)
            getImporter(DisasterDamageImporter.class).doImport((DisasterDamage) object, objectId, objectType, foreignKeys);
        else if (object instanceof Pollution)
            getImporter(PollutionImporter.class).doImport((Pollution) object, objectId, objectType, foreignKeys);
        else if (object instanceof PublicTransit)
            getImporter(PublicTransitImporter.class).doImport((PublicTransit) object, objectId, objectType, foreignKeys);
        else if (object instanceof Recreations)
            getImporter(RecreationsImporter.class).doImport((Recreations) object, objectId, objectType, foreignKeys);
        else if (object instanceof Urbanization)
            getImporter(UrbanizationImporter.class).doImport((Urbanization) object, objectId, objectType, foreignKeys);
        else if (object instanceof UrbanFunction)
            getImporter(UrbanFunctionImporter.class).doImport((UrbanFunction) object, objectId, objectType, foreignKeys);
        else if (object instanceof Households)
            getImporter(HouseholdsImporter.class).doImport((Households) object, objectId, objectType, foreignKeys);
        else if (object instanceof OfficesAndEmployees)
            getImporter(OfficesAndEmployeesImporter.class).doImport((OfficesAndEmployees) object, objectId, objectType, foreignKeys);
        else if (object instanceof Population)
            getImporter(PopulationImporter.class).doImport((Population) object, objectId, objectType, foreignKeys);
        else if (object instanceof StatisticalGrid)
            getImporter(StatisticalGridImporter.class).doImport((StatisticalGrid) object, objectId, objectType, foreignKeys);
    }

    @Override
    public void importGenericApplicationProperties(ADEPropertyCollection properties, AbstractFeature parent, long parentId, FeatureType parentType) throws CityGMLImportException, SQLException {
        if (parent instanceof AbstractBuilding)
            getImporter(BuildingPropertiesImporter.class).doImport(properties, (AbstractBuilding) parent, parentId, parentType);
        else if (parent instanceof LandUse)
            getImporter(LandUsePropertiesImporter.class).doImport(properties, (LandUse) parent, parentId, parentType);
        else if (parent instanceof TransportationComplex)
            getImporter(TransportationComplexPropertiesImporter.class).doImport(properties, (TransportationComplex) parent, parentId, parentType);
        else if (parent instanceof CityObjectGroup)
            getImporter(CityObjectGroupPropertiesImporter.class).doImport(properties, (CityObjectGroup) parent, parentId, parentType);
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
            else if (type == PublicTransitImporter.class)
                importer = new PublicTransitImporter(connection, helper, this);
            else if (type == RecreationsImporter.class)
                importer = new RecreationsImporter(connection, helper, this);
            else if (type == UrbanizationImporter.class)
                importer = new UrbanizationImporter(connection, helper, this);
            else if (type == LegalGroundsImporter.class)
                importer = new LegalGroundsImporter(connection, helper, this);
            else if (type == org.citydb.ade.iur.importer.urf.NumberOfHouseholdsImporter.class)
                importer = new org.citydb.ade.iur.importer.urf.NumberOfHouseholdsImporter(connection, helper, this);
            else if (type == UrbanFunctionToCityObjectImporter.class)
                importer = new UrbanFunctionToCityObjectImporter(connection, helper, this);

            // statistical grid module
            else if (type == StatisticalGridImporter.class)
                importer = new StatisticalGridImporter(connection, helper, this);
            else if (type == HouseholdsImporter.class)
                importer = new HouseholdsImporter(connection, helper, this);
            else if (type == OfficesAndEmployeesImporter.class)
                importer = new OfficesAndEmployeesImporter(connection, helper, this);
            else if (type == PopulationImporter.class)
                importer = new PopulationImporter(connection, helper, this);
            else if (type == NumberOfHouseholdsImporter.class)
                importer = new NumberOfHouseholdsImporter(connection, helper, this);
            else if (type == LandPricePerLandUseImporter.class)
                importer = new LandPricePerLandUseImporter(connection, helper, this);
            else if (type == NumberOfAnnualDiversionsImporter.class)
                importer = new NumberOfAnnualDiversionsImporter(connection, helper, this);
            else if (type == PopulationByAgeAndSexImporter.class)
                importer = new PopulationByAgeAndSexImporter(connection, helper, this);

            // urban object module
            else if (type == BuildingPropertiesImporter.class)
                importer = new BuildingPropertiesImporter(connection, helper, this);
            else if (type == LandUsePropertiesImporter.class)
                importer = new LandUsePropertiesImporter(connection, helper, this);
            else if (type == TransportationComplexPropertiesImporter.class)
                importer = new TransportationComplexPropertiesImporter(connection, helper, this);
            else if (type == CityObjectGroupPropertiesImporter.class)
                importer = new CityObjectGroupPropertiesImporter(connection, helper, this);
            else if (type == BuildingDetailsImporter.class)
                importer = new BuildingDetailsImporter(connection, helper, this);
            else if (type == LargeCustomerFacilitiesImporter.class)
                importer = new LargeCustomerFacilitiesImporter(connection, helper, this);
            else if (type == TrafficVolumeImporter.class)
                importer = new TrafficVolumeImporter(connection, helper, this);

            if (importer == null)
                throw new SQLException("Failed to build ADE importer of type " + type.getName() + ".");

            importers.put(type, importer);
        }

        return type.cast(importer);
    }
}
