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
import org.citydb.database.schema.mapping.SchemaMapping;
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
import org.citygml4j.ade.iur.model.urf.PublicTransit;
import org.citygml4j.ade.iur.model.urf.Recreations;
import org.citygml4j.ade.iur.model.urf.Regulation;
import org.citygml4j.ade.iur.model.urf.UrbanPlan;
import org.citygml4j.ade.iur.model.urf.Urbanization;
import org.citygml4j.ade.iur.model.urg.Households;
import org.citygml4j.ade.iur.model.urg.LandPrice;
import org.citygml4j.ade.iur.model.urg.LandUseDiversion;
import org.citygml4j.ade.iur.model.urg.OfficesAndEmployees;
import org.citygml4j.ade.iur.model.urg.Population;
import org.citygml4j.ade.iur.model.urg.PublicTransportationAccessibility;
import org.citygml4j.model.gml.base.AbstractGML;
import org.citygml4j.model.module.citygml.CityGMLVersion;

import java.util.HashMap;
import java.util.Map;

public class ObjectMapper implements ADEObjectMapper {
    private Map<Class<? extends AbstractGML>, Integer> objectClassIds = new HashMap<>();

    public void populateObjectClassIds(SchemaMapping schemaMapping) throws ADEExtensionException {
        for (AbstractObjectType<?> type : schemaMapping.getAbstractObjectTypes()) {
            int objectClassId = type.getObjectClassId();

            switch (type.getPath()) {
                case "Population":
                    objectClassIds.put(Population.class, objectClassId);
                    break;
                case "PublicTransportationAccessibility":
                    objectClassIds.put(PublicTransportationAccessibility.class, objectClassId);
                    break;
                case "LandPrice":
                    objectClassIds.put(LandPrice.class, objectClassId);
                    break;
                case "LandUseDiversion":
                    if (type.getSchema().matchesNamespaceURI(StatisticalGridModule.v1_3.getNamespaceURI()))
                        objectClassIds.put(LandUseDiversion.class, objectClassId);
                    else if (type.getSchema().matchesNamespaceURI(UrbanFunctionModule.v1_3.getNamespaceURI()))
                        objectClassIds.put(org.citygml4j.ade.iur.model.urf.LandUseDiversion.class, objectClassId);
                    break;
                case "Households":
                    objectClassIds.put(Households.class, objectClassId);
                    break;
                case "OfficesAndEmployees":
                    objectClassIds.put(OfficesAndEmployees.class, objectClassId);
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
                case "PublicTransit":
                    objectClassIds.put(PublicTransit.class, objectClassId);
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
}
