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

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;

public class ADETableMapper {
	private HashMap<String, Set<String>> tableColumns;

	public void populateTableColumns(SchemaMapper schemaMapper) {
		tableColumns = new HashMap<String, Set<String>>() {{
			put(schemaMapper.getTableName(ADETable.BUILDING).toUpperCase(), new LinkedHashSet<String>() {{
				add("ID");
				add("BUILDINGDETAILS_ID");
				add("LARGECUSTOMERFACILITIES_ID");
			}});
			put(schemaMapper.getTableName(ADETable.BUILDINGDETAILS).toUpperCase(), new LinkedHashSet<String>() {{
				add("ID");
				add("AREACLASSIFICATION_CODESPACE");
				add("AREACLASSIFICATIONTYPE");
				add("BUILDINGFOOTPRINTAREA");
				add("BUILDINGFOOTPRINTAREA_UOM");
				add("BUILDINGROOFEDGEAREA");
				add("BUILDINGROOFEDGEAREA_UOM");
				add("BUILDINGSTRUCTURET_CODESPACE");
				add("BUILDINGSTRUCTURETYPE");
				add("CITY");
				add("CITY_CODESPACE");
				add("DEVELOPMENTAREA");
				add("DEVELOPMENTAREA_UOM");
				add("DISTRICTSANDZONEST_CODESPACE");
				add("DISTRICTSANDZONESTYPE");
				add("FIREPROOFSTRUCTURE_CODESPACE");
				add("FIREPROOFSTRUCTURETYPE");
				add("IMPLEMENTINGBODY");
				add("LANDUSEPLANTYPE");
				add("LANDUSEPLANTYPE_CODESPACE");
				add("NOTE");
				add("PREFECTURE");
				add("PREFECTURE_CODESPACE");
				add("REFERENCE");
				add("SERIALNUMBEROFBUILDINGCERTIF");
				add("SITEAREA");
				add("SITEAREA_UOM");
				add("SURVEYYEAR");
				add("TOTALFLOORAREA");
				add("TOTALFLOORAREA_UOM");
				add("URBANPLANTYPE");
				add("URBANPLANTYPE_CODESPACE");
			}});
			put(schemaMapper.getTableName(ADETable.CENSUSBLOCK).toUpperCase(), new LinkedHashSet<String>() {{
				add("ID");
				add("DAYTIMEPOPULATION");
				add("DAYTIMEPOPULATIONDENSITY");
				add("NUMBEROFMAINHOUSEHOLDS");
				add("NUMBEROFORDINARYHOUSEHOLDS");
			}});
			put(schemaMapper.getTableName(ADETable.CITYOBJECTGROUP).toUpperCase(), new LinkedHashSet<String>() {{
				add("ID");
				add("FISCALYEAROFPUBLICATION");
				add("LANGUAGE");
				add("LANGUAGE_CODESPACE");
			}});
			put(schemaMapper.getTableName(ADETable.DEVELOPMENTPROJECT).toUpperCase(), new LinkedHashSet<String>() {{
				add("ID");
				add("BENEFITAREA");
				add("BENEFITAREA_UOM");
				add("BENEFITPERIOD");
				add("COMPLETEDAREA");
				add("COMPLETEDAREA_UOM");
				add("COST");
				add("DATEOFDECISION");
				add("DATEOFDESIGNATIONFORTEMPORAR");
				add("MAINPURPOSE");
				add("MAINPURPOSE_CODESPACE");
				add("ONGOINGAREA");
				add("ONGOINGAREA_UOM");
				add("PLANNEDAREA");
				add("PLANNEDAREA_UOM");
				add("STATUS");
				add("STATUS_CODESPACE");
			}});
			put(schemaMapper.getTableName(ADETable.DISASTERDAMAGE).toUpperCase(), new LinkedHashSet<String>() {{
				add("ID");
				add("DAMAGEDAREA");
				add("DAMAGEDAREA_UOM");
				add("MAXIMUMRAINFALLPERHOUR");
				add("NUMBEROFDAMAGEDHOUSES");
				add("NUMBEROFHOUSESFLOODEDABOVEFL");
				add("NUMBEROFHOUSESFLOODEDBELOWFL");
				add("TOTALRAINFALL");
			}});
			put(schemaMapper.getTableName(ADETable.HOUSEHOLDS).toUpperCase(), new LinkedHashSet<String>() {{
				add("ID");
				add("NUMBEROFMAINHOUSEHOLD");
				add("NUMBEROFORDINARYHOUSEHOLD");
			}});
			put(schemaMapper.getTableName(ADETable.LAND_USE).toUpperCase(), new LinkedHashSet<String>() {{
				add("ID");
				add("AREACLASSIFICATION_CODESPACE");
				add("AREACLASSIFICATIONTYPE");
				add("AREAINHA");
				add("AREAINHA_UOM");
				add("AREAINSQUAREMETER");
				add("AREAINSQUAREMETER_UOM");
				add("CITY");
				add("CITY_CODESPACE");
				add("DISTRICTSANDZONEST_CODESPACE");
				add("DISTRICTSANDZONESTYPE");
				add("LANDUSEPLANTYPE");
				add("LANDUSEPLANTYPE_CODESPACE");
				add("NOMINALAREA");
				add("NOMINALAREA_UOM");
				add("NOTE");
				add("OWNER");
				add("OWNERTYPE");
				add("OWNERTYPE_CODESPACE");
				add("PREFECTURE");
				add("PREFECTURE_CODESPACE");
				add("REFERENCE");
				add("SURVEYYEAR");
				add("URBANPLANTYPE");
				add("URBANPLANTYPE_CODESPACE");
			}});
			put(schemaMapper.getTableName(ADETable.LANDPRICEPERLANDUSE).toUpperCase(), new LinkedHashSet<String>() {{
				add("ID");
				add("CURRENCYUNIT");
				add("CURRENCYUNIT_CODESPACE");
				add("LANDPRICE");
				add("LANDUSE");
				add("LANDUSE_CODESPACE");
				add("STATISTICALGRID_LANDPRICE_ID");
			}});
			put(schemaMapper.getTableName(ADETable.LARGECUSTOMERFACILITIE).toUpperCase(), new LinkedHashSet<String>() {{
				add("ID");
				add("AREACLASSIFICATION_CODESPACE");
				add("AREACLASSIFICATIONTYPE");
				add("AVAILABILITY");
				add("CAPACITY");
				add("CITY");
				add("CITY_CODESPACE");
				add("CLASS");
				add("CLASS_CODESPACE");
				add("DISTRICTSANDZONEST_CODESPACE");
				add("DISTRICTSANDZONESTYPE");
				add("INAUGURALDATE");
				add("KEYTENANTS");
				add("LANDUSEPLANTYPE");
				add("LANDUSEPLANTYPE_CODESPACE");
				add("NAME");
				add("NOTE");
				add("OWNER");
				add("PREFECTURE");
				add("PREFECTURE_CODESPACE");
				add("REFERENCE");
				add("SURVEYYEAR");
				add("TOTALFLOORAREA");
				add("TOTALFLOORAREA_UOM");
				add("TOTALSTOREFLOORAREA");
				add("TOTALSTOREFLOORAREA_UOM");
				add("URBANPLANTYPE");
				add("URBANPLANTYPE_CODESPACE");
			}});
			put(schemaMapper.getTableName(ADETable.LEGALGROUNDS).toUpperCase(), new LinkedHashSet<String>() {{
				add("ID");
				add("ARTICLESOFREGULATI_CODESPACE");
				add("ARTICLESOFREGULATION");
				add("DATE_");
				add("NAMEOFREGULATION");
				add("NAMEOFREGULATION_CODESPACE");
			}});
			put(schemaMapper.getTableName(ADETable.NUMBEROFANNUALDIVERSIO).toUpperCase(), new LinkedHashSet<String>() {{
				add("ID");
				add("AREA");
				add("AREA_UOM");
				add("COUNT");
				add("STATISTICALG_NUMBEROFANNU_ID");
				add("YEAR");
			}});
			put(schemaMapper.getTableName(ADETable.NUMBEROFHOUSEHOLDS).toUpperCase(), new LinkedHashSet<String>() {{
				add("ID");
				add("CLASS");
				add("CLASS_CODESPACE");
				add("HOUSEHOLDS_NUMBEROFHOUS_ID_1");
				add("HOUSEHOLDS_NUMBEROFHOUSEH_ID");
				add("NUMBER_");
			}});
			put(schemaMapper.getTableName(ADETable.NUMBEROFHOUSEHOLDS_1).toUpperCase(), new LinkedHashSet<String>() {{
				add("ID");
				add("CENSUSBLOCK_NUMBEROFHOU_ID_1");
				add("CENSUSBLOCK_NUMBEROFHOUSE_ID");
				add("CLASS");
				add("CLASS_CODESPACE");
				add("NUMBER_");
			}});
			put(schemaMapper.getTableName(ADETable.OFFICESANDEMPLOYEES).toUpperCase(), new LinkedHashSet<String>() {{
				add("ID");
				add("NUMBEROFEMPLOYEES");
				add("NUMBEROFOFFICES");
			}});
			put(schemaMapper.getTableName(ADETable.POLLUTION).toUpperCase(), new LinkedHashSet<String>() {{
				add("ID");
				add("CAUSE");
				add("DAMAGEDAREA");
				add("DAMAGEDAREA_UOM");
			}});
			put(schemaMapper.getTableName(ADETable.POPULATION).toUpperCase(), new LinkedHashSet<String>() {{
				add("ID");
				add("BIRTHS");
				add("DAYTIMEPOPULATION");
				add("DAYTIMEPOPULATIONDENSITY");
				add("DEATHS");
				add("FEMALEPOPULATION");
				add("INCREASEMENT");
				add("MALEPOPULATION");
				add("MOVEFROM");
				add("MOVETO");
				add("NATURALINCREASE");
				add("SOCIALINCREASE");
				add("TOTAL");
			}});
			put(schemaMapper.getTableName(ADETable.POPULATIONBYAGEANDSEX).toUpperCase(), new LinkedHashSet<String>() {{
				add("ID");
				add("AGE");
				add("AGE_CODESPACE");
				add("NUMBER_");
				add("POPULATION_POPULATIONBYAG_ID");
				add("SEX");
				add("SEX_CODESPACE");
			}});
			put(schemaMapper.getTableName(ADETable.PUBLICTRANSIT).toUpperCase(), new LinkedHashSet<String>() {{
				add("ID");
				add("COMPANYNAME");
				add("FREQUENCYOFSERVICE");
				add("NUMBEROFCUSTOMERS");
				add("ROUTENAME");
				add("SECTIONNAME");
			}});
			put(schemaMapper.getTableName(ADETable.RECREATIONS).toUpperCase(), new LinkedHashSet<String>() {{
				add("ID");
				add("CAPACITY");
				add("NUMBEROFUSERS");
			}});
			put(schemaMapper.getTableName(ADETable.STATISTICALGRID).toUpperCase(), new LinkedHashSet<String>() {{
				add("ID");
				add("AREACLASSIFICATION_CODESPACE");
				add("AREACLASSIFICATIONTYPE");
				add("AVAILABILITY");
				add("CITY");
				add("CITY_CODESPACE");
				add("CLASS");
				add("CLASS_CODESPACE");
				add("LOD_1MULTISURFACEGEOMETRY_ID");
				add("LOD_2MULTISURFACEGEOMETRY_ID");
				add("OBJECTCLASS_ID");
				add("PREFECTURE");
				add("PREFECTURE_CODESPACE");
				add("SURVEYYEAR");
				add("URBANPLANTYPE");
				add("URBANPLANTYPE_CODESPACE");
				add("VALUE");
			}});
			put(schemaMapper.getTableName(ADETable.TRAFFICVOLUME).toUpperCase(), new LinkedHashSet<String>() {{
				add("ID");
				add("AREACLASSIFICATION_CODESPACE");
				add("AREACLASSIFICATIONTYPE");
				add("AVERAGETRAVELSPEEDINCONGESTI");
				add("CITY");
				add("CITY_CODESPACE");
				add("CONGESTIONRATE");
				add("LARGEVEHICLERATE");
				add("NOTE");
				add("OBSERVATIONPOINTNAME");
				add("PREFECTURE");
				add("PREFECTURE_CODESPACE");
				add("REFERENCE");
				add("SURVEYYEAR");
				add("URBANPLANTYPE");
				add("URBANPLANTYPE_CODESPACE");
				add("WEEKDAY12HOURTRAFFICVOLUME");
				add("WEEKDAY24HOURTRAFFICVOLUME");
			}});
			put(schemaMapper.getTableName(ADETable.TRANSPORTATION_COMPLEX).toUpperCase(), new LinkedHashSet<String>() {{
				add("ID");
				add("TRAFFICVOLUME_ID");
				add("WIDTH");
				add("WIDTH_UOM");
				add("WIDTHTYPE");
				add("WIDTHTYPE_CODESPACE");
			}});
			put(schemaMapper.getTableName(ADETable.URBANFUNC_TO_CITYOBJEC).toUpperCase(), new LinkedHashSet<String>() {{
				add("CITYOBJECT_ID");
				add("URBANFUNCTION_ID");
			}});
			put(schemaMapper.getTableName(ADETable.URBANFUNCTION).toUpperCase(), new LinkedHashSet<String>() {{
				add("ID");
				add("ABSTRACT");
				add("AREA_ID");
				add("AREACLASSIFICATION_CODESPACE");
				add("AREACLASSIFICATIONTYPE");
				add("BOUNDARY");
				add("CAPACITY");
				add("CITY");
				add("CITY_CODESPACE");
				add("CLASS");
				add("CLASS_CODESPACE");
				add("CUSTODIAN");
				add("ENACTMENTDATE");
				add("ENACTMENTFISCALYEAR");
				add("EXPIRATIONDATE");
				add("EXPIRATIONFISCALYEAR");
				add("FUNCTION");
				add("FUNCTION_CODESPACE");
				add("LEGALGROUNDS_ID");
				add("LOCATION");
				add("NOMINALAREA");
				add("NOMINALAREA_UOM");
				add("NOTE");
				add("OBJECTCLASS_ID");
				add("PREFECTURE");
				add("PREFECTURE_CODESPACE");
				add("REFERENCE");
				add("SURVEYYEAR");
				add("URBANPLANTYPE");
				add("URBANPLANTYPE_CODESPACE");
				add("VALIDITY");
			}});
			put(schemaMapper.getTableName(ADETable.URBANIZATION).toUpperCase(), new LinkedHashSet<String>() {{
				add("ID");
				add("PERIOD");
				add("RESOURCES");
			}});
			put(schemaMapper.getTableName(ADETable.ZONE).toUpperCase(), new LinkedHashSet<String>() {{
				add("ID");
				add("AREAAPPLIED");
				add("FINALPUBLICATIONDATE");
				add("OBJECTCLASS_ID");
			}});
		}};
	}

	public HashMap<String, Set<String>> getTablesAndColumns() {
		return tableColumns;
	}

}
