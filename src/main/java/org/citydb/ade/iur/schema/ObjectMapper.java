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

package org.citydb.ade.iur.schema;

import org.citydb.ade.ADEExtensionException;
import org.citydb.ade.ADEObjectMapper;
import org.citydb.database.schema.mapping.AbstractObjectType;
import org.citydb.database.schema.mapping.ComplexType;
import org.citydb.database.schema.mapping.SchemaMapping;
import org.citygml4j.ade.iur.model.common.AbstractDataType;
import org.citygml4j.ade.iur.model.module.StatisticalGridModule;
import org.citygml4j.ade.iur.model.module.UrbanFunctionModule;
import org.citygml4j.ade.iur.model.urf.Administration;
import org.citygml4j.ade.iur.model.urf.Agreement;
import org.citygml4j.ade.iur.model.urf.AreaClassification;
import org.citygml4j.ade.iur.model.urf.CensusBlock;
import org.citygml4j.ade.iur.model.urf.DevelopmentProject;
import org.citygml4j.ade.iur.model.urf.DisasterDamage;
import org.citygml4j.ade.iur.model.urf.DisasterPreventionBase;
import org.citygml4j.ade.iur.model.urf.DistrictsAndZones;
import org.citygml4j.ade.iur.model.urf.HubCity;
import org.citygml4j.ade.iur.model.urf.LandUsePlan;
import org.citygml4j.ade.iur.model.urf.Pollution;
import org.citygml4j.ade.iur.model.urf.PublicTransportationFacility;
import org.citygml4j.ade.iur.model.urf.Recreations;
import org.citygml4j.ade.iur.model.urf.Regulation;
import org.citygml4j.ade.iur.model.urf.UrbanPlan;
import org.citygml4j.ade.iur.model.urf.Urbanization;
import org.citygml4j.ade.iur.model.urg.GenericGridCell;
import org.citygml4j.ade.iur.model.urg.Households;
import org.citygml4j.ade.iur.model.urg.LandPrice;
import org.citygml4j.ade.iur.model.urg.LandUseDiversion;
import org.citygml4j.ade.iur.model.urg.OfficesAndEmployees;
import org.citygml4j.ade.iur.model.urg.Population;
import org.citygml4j.ade.iur.model.urg.PublicTransitAccessibility;
import org.citygml4j.ade.iur.model.urt.Agency;
import org.citygml4j.ade.iur.model.urt.Attribution;
import org.citygml4j.ade.iur.model.urt.Calendar;
import org.citygml4j.ade.iur.model.urt.CalendarDate;
import org.citygml4j.ade.iur.model.urt.FareAttribute;
import org.citygml4j.ade.iur.model.urt.FareRule;
import org.citygml4j.ade.iur.model.urt.FeedInfo;
import org.citygml4j.ade.iur.model.urt.Frequency;
import org.citygml4j.ade.iur.model.urt.Level;
import org.citygml4j.ade.iur.model.urt.Office;
import org.citygml4j.ade.iur.model.urt.Pathway;
import org.citygml4j.ade.iur.model.urt.Route;
import org.citygml4j.ade.iur.model.urt.Shape;
import org.citygml4j.ade.iur.model.urt.Stop;
import org.citygml4j.ade.iur.model.urt.StopTime;
import org.citygml4j.ade.iur.model.urt.Transfer;
import org.citygml4j.ade.iur.model.urt.Translation;
import org.citygml4j.ade.iur.model.urt.TranslationJP;
import org.citygml4j.ade.iur.model.urt.Trip;
import org.citygml4j.model.gml.base.AbstractGML;
import org.citygml4j.model.module.citygml.CityGMLVersion;

import java.util.HashMap;
import java.util.Map;

public class ObjectMapper implements ADEObjectMapper {
    private final Map<Class<? extends AbstractGML>, Integer> objectClassIds = new HashMap<>();
    private final Map<Class<? extends AbstractDataType>, Integer> typeClassIds = new HashMap<>();

    public void populateObjectClassIds(SchemaMapping schemaMapping) throws ADEExtensionException {
        for (AbstractObjectType<?> type : schemaMapping.getAbstractObjectTypes()) {
            int objectClassId = type.getObjectClassId();

            switch (type.getPath()) {
                case "Population":
                    objectClassIds.put(Population.class, objectClassId);
                    break;
                case "PublicTransitAccessibility":
                    objectClassIds.put(PublicTransitAccessibility.class, objectClassId);
                    break;
                case "LandPrice":
                    objectClassIds.put(LandPrice.class, objectClassId);
                    break;
                case "LandUseDiversion":
                    if (type.getSchema().matchesNamespaceURI(StatisticalGridModule.v1_4.getNamespaceURI()))
                        objectClassIds.put(LandUseDiversion.class, objectClassId);
                    else if (type.getSchema().matchesNamespaceURI(UrbanFunctionModule.v1_4.getNamespaceURI()))
                        objectClassIds.put(org.citygml4j.ade.iur.model.urf.LandUseDiversion.class, objectClassId);
                    break;
                case "Households":
                    objectClassIds.put(Households.class, objectClassId);
                    break;
                case "OfficesAndEmployees":
                    objectClassIds.put(OfficesAndEmployees.class, objectClassId);
                    break;
                case "GenericGridCell":
                    objectClassIds.put(GenericGridCell.class, objectClassId);
                    break;
                case "Administration":
                    objectClassIds.put(Administration.class, objectClassId);
                    break;
                case "LandUsePlan":
                    objectClassIds.put(LandUsePlan.class, objectClassId);
                    break;
                case "UrbanPlan":
                    objectClassIds.put(UrbanPlan.class, objectClassId);
                    break;
                case "Agreement":
                    objectClassIds.put(Agreement.class, objectClassId);
                    break;
                case "Regulation":
                    objectClassIds.put(Regulation.class, objectClassId);
                    break;
                case "AreaClassification":
                    objectClassIds.put(AreaClassification.class, objectClassId);
                    break;
                case "DistrictsAndZones":
                    objectClassIds.put(DistrictsAndZones.class, objectClassId);
                    break;
                case "DevelopmentProject":
                    objectClassIds.put(DevelopmentProject.class, objectClassId);
                    break;
                case "CensusBlock":
                    objectClassIds.put(CensusBlock.class, objectClassId);
                    break;
                case "DisasterDamage":
                    objectClassIds.put(DisasterDamage.class, objectClassId);
                    break;
                case "Pollution":
                    objectClassIds.put(Pollution.class, objectClassId);
                    break;
                case "DisasterPreventionBase":
                    objectClassIds.put(DisasterPreventionBase.class, objectClassId);
                    break;
                case "Recreations":
                    objectClassIds.put(Recreations.class, objectClassId);
                    break;
                case "HubCity":
                    objectClassIds.put(HubCity.class, objectClassId);
                    break;
                case "Urbanization":
                    objectClassIds.put(Urbanization.class, objectClassId);
                    break;
                case "PublicTransportationFacility":
                    objectClassIds.put(PublicTransportationFacility.class, objectClassId);
                    break;
                case "Route":
                    objectClassIds.put(Route.class, objectClassId);
                    break;
                case "Agency":
                    objectClassIds.put(Agency.class, objectClassId);
                    break;
                case "Stop":
                    objectClassIds.put(Stop.class, objectClassId);
                    break;
                case "Level":
                    objectClassIds.put(Level.class, objectClassId);
                    break;
                case "Trip":
                    objectClassIds.put(Trip.class, objectClassId);
                    break;
                case "Calendar":
                    objectClassIds.put(Calendar.class, objectClassId);
                    break;
                case "CalendarDate":
                    objectClassIds.put(CalendarDate.class, objectClassId);
                    break;
                case "Office":
                    objectClassIds.put(Office.class, objectClassId);
                    break;
                case "Shape":
                    objectClassIds.put(Shape.class, objectClassId);
                    break;
                case "FareAttribute":
                    objectClassIds.put(FareAttribute.class, objectClassId);
                    break;
                case "Pathway":
                    objectClassIds.put(Pathway.class, objectClassId);
                    break;
                case "TranslationJP":
                    objectClassIds.put(TranslationJP.class, objectClassId);
                    break;
                case "Attribution":
                    objectClassIds.put(Attribution.class, objectClassId);
                    break;
            }
        }

        for (ComplexType type : schemaMapping.getComplexTypes()) {
            int typeClassId = type.getObjectClassId();

            switch (type.getPath()) {
                case "FeedInfo":
                    typeClassIds.put(FeedInfo.class, typeClassId);
                    break;
                case "Translation":
                    typeClassIds.put(Translation.class, typeClassId);
                    break;
                case "Transfer":
                    typeClassIds.put(Transfer.class, typeClassId);
                    break;
                case "Frequency":
                    typeClassIds.put(Frequency.class, typeClassId);
                    break;
                case "StopTime":
                    typeClassIds.put(StopTime.class, typeClassId);
                    break;
                case "FareRule":
                    typeClassIds.put(FareRule.class, typeClassId);
                    break;
            }
        }
    }

    @Override
    public AbstractGML createObject(int objectClassId, CityGMLVersion version) {
        if (version == CityGMLVersion.v2_0_0) {
            for (Map.Entry<Class<? extends AbstractGML>, Integer> entry : objectClassIds.entrySet()) {
                if (entry.getValue() == objectClassId) {
                    try {
                        return entry.getKey().getDeclaredConstructor().newInstance();
                    } catch (Exception e) {
                        //
                    }
                }
            }
        }

        return null;
    }

    @Override
    public int getObjectClassId(Class<? extends AbstractGML> adeObjectClass) {
        Integer objectClassId = objectClassIds.get(adeObjectClass);
        return objectClassId != null ? objectClassId : 0;
    }

    public <T> T createType(int objectClassId, Class<T> type) {
        for (Map.Entry<Class<? extends AbstractDataType>, Integer> entry : typeClassIds.entrySet()) {
            if (entry.getValue() == objectClassId) {
                try {
                    AbstractDataType dataType = entry.getKey().getDeclaredConstructor().newInstance();
                    return type.isInstance(dataType) ? type.cast(dataType) : null;
                } catch (Exception e) {
                    //
                }
            }
        }

        return null;
    }

    public int getTypeClassId(Class<? extends AbstractDataType> adeObjectClass) {
        Integer objectClassId = typeClassIds.get(adeObjectClass);
        return objectClassId != null ? objectClassId : 0;
    }
}
