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

import org.citydb.ade.importer.ADEImporter;
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

public enum ADETable {
    BUILDING(BuildingPropertiesImporter.class),
    BUILDINGDETAILS(BuildingDetailsImporter.class),
    CENSUSBLOCK(CensusBlockImporter.class),
    CITYOBJECTGROUP(CityObjectGroupPropertiesImporter.class),
    DEVELOPMENTPROJECT(DevelopmentProjectImporter.class),
    DISASTERDAMAGE(DisasterDamageImporter.class),
    HOUSEHOLDS(HouseholdsImporter.class),
    LAND_USE(LandUsePropertiesImporter.class),
    LANDPRICEPERLANDUSE(LandPricePerLandUseImporter.class),
    LARGECUSTOMERFACILITIE(LargeCustomerFacilitiesImporter.class),
    LEGALGROUNDS(LegalGroundsImporter.class),
    NUMBEROFANNUALDIVERSIO(NumberOfAnnualDiversionsImporter.class),
    NUMBEROFHOUSEHOLDS(NumberOfHouseholdsImporter.class),
    NUMBEROFHOUSEHOLDS_1(org.citydb.ade.iur.importer.urf.NumberOfHouseholdsImporter.class),
    OFFICESANDEMPLOYEES(OfficesAndEmployeesImporter.class),
    POLLUTION(PollutionImporter.class),
    POPULATION(PopulationImporter.class),
    POPULATIONBYAGEANDSEX(PopulationByAgeAndSexImporter.class),
    PUBLICTRANSIT(PublicTransitImporter.class),
    RECREATIONS(RecreationsImporter.class),
    STATISTICALGRID(StatisticalGridImporter.class),
    TRAFFICVOLUME(TrafficVolumeImporter.class),
    TRANSPORTATION_COMPLEX(TransportationComplexPropertiesImporter.class),
    URBANFUNC_TO_CITYOBJEC(UrbanFunctionToCityObjectImporter.class),
    URBANFUNCTION(UrbanFunctionImporter.class),
    URBANIZATION(UrbanizationImporter.class),
    ZONE(UrbanFunctionImporter.class);

    private Class<? extends ADEImporter> importerClass;

    ADETable(Class<? extends ADEImporter> importerClass) {
        this.importerClass = importerClass;
    }

    public Class<? extends ADEImporter> getImporterClass() {
        return importerClass;
    }
}
