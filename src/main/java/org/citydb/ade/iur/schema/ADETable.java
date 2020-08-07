package org.citydb.ade.iur.schema;

import org.citydb.ade.importer.ADEImporter;
import org.citydb.ade.iur.importer.urf.CensusBlockImporter;
import org.citydb.ade.iur.importer.urf.DisasterDamageImporter;
import org.citydb.ade.iur.importer.urf.LegalGroundsImporter;
import org.citydb.ade.iur.importer.urf.PollutionImporter;
import org.citydb.ade.iur.importer.urf.PublicTransitImporter;
import org.citydb.ade.iur.importer.urf.RecreationsImporter;
import org.citydb.ade.iur.importer.urf.UrbanFunctionImporter;
import org.citydb.ade.iur.importer.urf.UrbanFunctionToCityObjectImporter;
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
import org.citydb.ade.iur.importer.urf.DevelopmentProjectImporter;
import org.citydb.ade.iur.importer.urf.UrbanizationImporter;

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
