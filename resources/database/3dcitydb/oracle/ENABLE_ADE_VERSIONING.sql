-- This document was automatically created by the ADE-Manager tool of 3DCityDB (https://www.3dcitydb.org) on 2020-03-20 09:53:29 
-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 
-- *********************************** Enable Versioning ********************************** 
-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 

exec DBMS_WM.EnableVersioning('ur_building,ur_buildingdetails,ur_censusblock,ur_cityobjectgroup,ur_developmentproject,ur_disasterdamage,ur_households,ur_land_use,ur_landpriceperlanduse,ur_largecustomerfacilitie,ur_legalgrounds,ur_numberofannualdiversio,ur_numberofhouseholds,ur_numberofhouseholds_1,ur_officesandemployees,ur_pollution,ur_population,ur_populationbyageandsex,ur_publictransit,ur_recreations,ur_statisticalgrid,ur_trafficvolume,ur_transportation_complex,ur_urbanfunc_to_cityobjec,ur_urbanfunction,ur_urbanization,ur_zone','VIEW_WO_OVERWRITE');
