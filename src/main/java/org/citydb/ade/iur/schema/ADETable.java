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

package org.citydb.ade.iur.schema;

import org.citydb.ade.iur.importer.urf.CensusBlockImporter;
import org.citydb.ade.iur.importer.urf.DevelopmentProjectImporter;
import org.citydb.ade.iur.importer.urf.DisasterDamageImporter;
import org.citydb.ade.iur.importer.urf.LegalGroundsImporter;
import org.citydb.ade.iur.importer.urf.PollutionImporter;
import org.citydb.ade.iur.importer.urf.PublicTransportationFacilityImporter;
import org.citydb.ade.iur.importer.urf.RecreationsImporter;
import org.citydb.ade.iur.importer.urf.UrbanFunctionImporter;
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
import org.citydb.ade.iur.importer.uro.BuildingDetailsImporter;
import org.citydb.ade.iur.importer.uro.BuildingPropertiesImporter;
import org.citydb.ade.iur.importer.uro.LandUsePropertiesImporter;
import org.citydb.ade.iur.importer.uro.LargeCustomerFacilitiesImporter;
import org.citydb.ade.iur.importer.uro.TrafficVolumeImporter;
import org.citydb.ade.iur.importer.uro.TransportationComplexPropertiesImporter;
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
import org.citydb.ade.iur.importer.urt.RouteImporter;
import org.citydb.ade.iur.importer.urt.StopImporter;
import org.citydb.ade.iur.importer.urt.StopTimeImporter;
import org.citydb.ade.iur.importer.urt.TransferImporter;
import org.citydb.ade.iur.importer.urt.TranslationImporter;
import org.citydb.ade.iur.importer.urt.TranslationJPImporter;
import org.citydb.ade.iur.importer.urt.TripImporter;
import org.citydb.core.ade.importer.ADEImporter;

public enum ADETable {
    AGENCY(AgencyImporter.class),
    AREAOFANNUALDIVERSIONS(AreaOfAnnualDiversionsImporter.class),
    ATTRIBUTION(AttributionImporter.class),
    BUILDING(BuildingPropertiesImporter.class),
    BUILDINGDETAILS(BuildingDetailsImporter.class),
    CALENDAR(CalendarImporter.class),
    CALENDARDATE(CalendarDateImporter.class),
    CENSUSBLOCK(CensusBlockImporter.class),
    CITYOBJECTGROUP(org.citydb.ade.iur.importer.urg.CityObjectGroupPropertiesImporter.class),
    CITYOBJECTGROUP_1(org.citydb.ade.iur.importer.uro.CityObjectGroupPropertiesImporter.class),
    CITYOBJECTGROUP_2(org.citydb.ade.iur.importer.urf.CityObjectGroupPropertiesImporter.class),
    CITYOBJECTGROUP_3(org.citydb.ade.iur.importer.urt.CityObjectGroupPropertiesImporter.class),
    DESCRIPTION(DescriptionImporter.class),
    DEVELOPMENTPROJECT(DevelopmentProjectImporter.class),
    DISASTERDAMAGE(DisasterDamageImporter.class),
    FAREATTRIBUTE(FareAttributeImporter.class),
    FARERULE(FareRuleImporter.class),
    FEEDINFO(FeedInfoImporter.class),
    FREQUENCY(FrequencyImporter.class),
    HOUSEHOLDS(HouseholdsImporter.class),
    KEYVALUEPAIR(org.citydb.ade.iur.importer.urg.KeyValuePairImporter.class),
    KEYVALUEPAIR_1(org.citydb.ade.iur.importer.uro.KeyValuePairImporter.class),
    LAND_USE(LandUsePropertiesImporter.class),
    LANDPRICE(LandPriceImporter.class),
    LANDPRICEPERLANDUSE(LandPricePerLandUseImporter.class),
    LANDUSEDIVERSION(LandUseDiversionImporter.class),
    LARGECUSTOMERFACILITIE(LargeCustomerFacilitiesImporter.class),
    LEGALGROUNDS(LegalGroundsImporter.class),
    NUMBEROFANNUALDIVERSIO(NumberOfAnnualDiversionsImporter.class),
    NUMBEROFHOUSEHOLDS(org.citydb.ade.iur.importer.urg.NumberOfHouseholdsImporter.class),
    NUMBEROFHOUSEHOLDS_1(org.citydb.ade.iur.importer.urf.NumberOfHouseholdsImporter.class),
    OFFICE(OfficeImporter.class),
    OFFICESANDEMPLOYEES(OfficesAndEmployeesImporter.class),
    PATHWAY(PathwayImporter.class),
    POINT(PointImporter.class),
    POLLUTION(PollutionImporter.class),
    POPULATION(PopulationImporter.class),
    POPULATIONBYAGEANDSEX(PopulationByAgeAndSexImporter.class),
    PUBLICTRANSIT(PublicTransitImporter.class),
    PUBLICTRANSITDATATYPE(PublicTransitDataTypeImporter.class),
    PUBLICTRANSPORTATIONFA(PublicTransportationFacilityImporter.class),
    RECREATIONS(RecreationsImporter.class),
    ROUTE(RouteImporter.class),
    STATISTICALGRID(StatisticalGridImporter.class),
    STOP(StopImporter.class),
    STOPTIME(StopTimeImporter.class),
    TRAFFICVOLUME(TrafficVolumeImporter.class),
    TRANSFER(TransferImporter.class),
    TRANSLATION(TranslationImporter.class),
    TRANSLATIONJP(TranslationJPImporter.class),
    TRANSPORTATION_COMPLEX(TransportationComplexPropertiesImporter.class),
    TRIP(TripImporter.class),
    URBANFUNC_TO_CITYOBJEC(UrbanFunctionToCityObjectImporter.class),
    URBANFUNCTION(UrbanFunctionImporter.class),
    URBANIZATION(UrbanizationImporter.class),
    ZONE(ZoneImporter.class);

    private final Class<? extends ADEImporter> importerClass;

    ADETable(Class<? extends ADEImporter> importerClass) {
        this.importerClass = importerClass;
    }

    public Class<? extends ADEImporter> getImporterClass() {
        return importerClass;
    }
}
