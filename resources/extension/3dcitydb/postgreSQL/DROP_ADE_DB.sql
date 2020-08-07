-- This document was automatically created by the ADE-Manager tool of 3DCityDB (https://www.3dcitydb.org) on 2020-03-20 09:53:29 
-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 
-- *********************************** Drop foreign keys ********************************** 
-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 
-- -------------------------------------------------------------------- 
-- ur_building 
-- -------------------------------------------------------------------- 
ALTER TABLE ur_building
    DROP CONSTRAINT ur_building_fk;

ALTER TABLE ur_building
    DROP CONSTRAINT ur_building_buildingdet_fk;

ALTER TABLE ur_building
    DROP CONSTRAINT ur_building_largecustom_fk;

-- -------------------------------------------------------------------- 
-- ur_censusblock 
-- -------------------------------------------------------------------- 
ALTER TABLE ur_censusblock
    DROP CONSTRAINT ur_censusblock_fk;

-- -------------------------------------------------------------------- 
-- ur_cityobjectgroup 
-- -------------------------------------------------------------------- 
ALTER TABLE ur_cityobjectgroup
    DROP CONSTRAINT ur_cityobjectgroup_fk;

-- -------------------------------------------------------------------- 
-- ur_developmentproject 
-- -------------------------------------------------------------------- 
ALTER TABLE ur_developmentproject
    DROP CONSTRAINT ur_developmentproject_fk;

-- -------------------------------------------------------------------- 
-- ur_disasterdamage 
-- -------------------------------------------------------------------- 
ALTER TABLE ur_disasterdamage
    DROP CONSTRAINT ur_disasterdamage_fk;

-- -------------------------------------------------------------------- 
-- ur_households 
-- -------------------------------------------------------------------- 
ALTER TABLE ur_households
    DROP CONSTRAINT ur_households_fk;

-- -------------------------------------------------------------------- 
-- ur_land_use 
-- -------------------------------------------------------------------- 
ALTER TABLE ur_land_use
    DROP CONSTRAINT ur_land_use_fk;

-- -------------------------------------------------------------------- 
-- ur_landpriceperlanduse 
-- -------------------------------------------------------------------- 
ALTER TABLE ur_landpriceperlanduse
    DROP CONSTRAINT ur_landpr_statis_landpr_fk;

-- -------------------------------------------------------------------- 
-- ur_numberofannualdiversio 
-- -------------------------------------------------------------------- 
ALTER TABLE ur_numberofannualdiversio
    DROP CONSTRAINT ur_number_statis_number_fk;

-- -------------------------------------------------------------------- 
-- ur_numberofhouseholds 
-- -------------------------------------------------------------------- 
ALTER TABLE ur_numberofhouseholds
    DROP CONSTRAINT ur_number_househ_number_fk;

ALTER TABLE ur_numberofhouseholds
    DROP CONSTRAINT ur_number_house_numbe_fk_1;

-- -------------------------------------------------------------------- 
-- ur_numberofhouseholds_1 
-- -------------------------------------------------------------------- 
ALTER TABLE ur_numberofhouseholds_1
    DROP CONSTRAINT ur_number_census_number_fk;

ALTER TABLE ur_numberofhouseholds_1
    DROP CONSTRAINT ur_number_censu_numbe_fk_1;

-- -------------------------------------------------------------------- 
-- ur_officesandemployees 
-- -------------------------------------------------------------------- 
ALTER TABLE ur_officesandemployees
    DROP CONSTRAINT ur_officesandemployees_fk;

-- -------------------------------------------------------------------- 
-- ur_pollution 
-- -------------------------------------------------------------------- 
ALTER TABLE ur_pollution
    DROP CONSTRAINT ur_pollution_fk;

-- -------------------------------------------------------------------- 
-- ur_population 
-- -------------------------------------------------------------------- 
ALTER TABLE ur_population
    DROP CONSTRAINT ur_population_fk;

-- -------------------------------------------------------------------- 
-- ur_populationbyageandsex 
-- -------------------------------------------------------------------- 
ALTER TABLE ur_populationbyageandsex
    DROP CONSTRAINT ur_popula_popula_popula_fk;

-- -------------------------------------------------------------------- 
-- ur_publictransit 
-- -------------------------------------------------------------------- 
ALTER TABLE ur_publictransit
    DROP CONSTRAINT ur_publictransit_fk;

-- -------------------------------------------------------------------- 
-- ur_recreations 
-- -------------------------------------------------------------------- 
ALTER TABLE ur_recreations
    DROP CONSTRAINT ur_recreations_fk;

-- -------------------------------------------------------------------- 
-- ur_statisticalgrid 
-- -------------------------------------------------------------------- 
ALTER TABLE ur_statisticalgrid
    DROP CONSTRAINT ur_statisticalgrid_fk;

ALTER TABLE ur_statisticalgrid
    DROP CONSTRAINT ur_statistica_objectcla_fk;

ALTER TABLE ur_statisticalgrid
    DROP CONSTRAINT ur_statisti_lod_1multis_fk;

ALTER TABLE ur_statisticalgrid
    DROP CONSTRAINT ur_statisti_lod_2multis_fk;

-- -------------------------------------------------------------------- 
-- ur_transportation_complex 
-- -------------------------------------------------------------------- 
ALTER TABLE ur_transportation_complex
    DROP CONSTRAINT ur_transportati_complex_fk;

ALTER TABLE ur_transportation_complex
    DROP CONSTRAINT ur_transp_comple_traffi_fk;

-- -------------------------------------------------------------------- 
-- ur_urbanfunc_to_cityobjec 
-- -------------------------------------------------------------------- 
ALTER TABLE ur_urbanfunc_to_cityobjec
    DROP CONSTRAINT ur_urbanfun_to_cityobj_fk1;

ALTER TABLE ur_urbanfunc_to_cityobjec
    DROP CONSTRAINT ur_urbanfun_to_cityobj_fk2;

-- -------------------------------------------------------------------- 
-- ur_urbanfunction 
-- -------------------------------------------------------------------- 
ALTER TABLE ur_urbanfunction
    DROP CONSTRAINT ur_urbanfunct_objectcla_fk;

ALTER TABLE ur_urbanfunction
    DROP CONSTRAINT ur_urbanfunction_fk;

ALTER TABLE ur_urbanfunction
    DROP CONSTRAINT ur_urbanfunct_legalgrou_fk;

ALTER TABLE ur_urbanfunction
    DROP CONSTRAINT ur_urbanfunction_area_fk;

-- -------------------------------------------------------------------- 
-- ur_urbanization 
-- -------------------------------------------------------------------- 
ALTER TABLE ur_urbanization
    DROP CONSTRAINT ur_urbanization_fk;

-- -------------------------------------------------------------------- 
-- ur_zone 
-- -------------------------------------------------------------------- 
ALTER TABLE ur_zone
    DROP CONSTRAINT ur_zone_objectclass_fk;

ALTER TABLE ur_zone
    DROP CONSTRAINT ur_zone_fk;

-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 
-- *********************************** Drop tables *************************************** 
-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 
-- -------------------------------------------------------------------- 
-- ur_building 
-- -------------------------------------------------------------------- 
DROP TABLE ur_building;

-- -------------------------------------------------------------------- 
-- ur_buildingdetails 
-- -------------------------------------------------------------------- 
DROP TABLE ur_buildingdetails;

-- -------------------------------------------------------------------- 
-- ur_censusblock 
-- -------------------------------------------------------------------- 
DROP TABLE ur_censusblock;

-- -------------------------------------------------------------------- 
-- ur_cityobjectgroup 
-- -------------------------------------------------------------------- 
DROP TABLE ur_cityobjectgroup;

-- -------------------------------------------------------------------- 
-- ur_developmentproject 
-- -------------------------------------------------------------------- 
DROP TABLE ur_developmentproject;

-- -------------------------------------------------------------------- 
-- ur_disasterdamage 
-- -------------------------------------------------------------------- 
DROP TABLE ur_disasterdamage;

-- -------------------------------------------------------------------- 
-- ur_households 
-- -------------------------------------------------------------------- 
DROP TABLE ur_households;

-- -------------------------------------------------------------------- 
-- ur_land_use 
-- -------------------------------------------------------------------- 
DROP TABLE ur_land_use;

-- -------------------------------------------------------------------- 
-- ur_landpriceperlanduse 
-- -------------------------------------------------------------------- 
DROP TABLE ur_landpriceperlanduse;

-- -------------------------------------------------------------------- 
-- ur_largecustomerfacilitie 
-- -------------------------------------------------------------------- 
DROP TABLE ur_largecustomerfacilitie;

-- -------------------------------------------------------------------- 
-- ur_legalgrounds 
-- -------------------------------------------------------------------- 
DROP TABLE ur_legalgrounds;

-- -------------------------------------------------------------------- 
-- ur_numberofannualdiversio 
-- -------------------------------------------------------------------- 
DROP TABLE ur_numberofannualdiversio;

-- -------------------------------------------------------------------- 
-- ur_numberofhouseholds 
-- -------------------------------------------------------------------- 
DROP TABLE ur_numberofhouseholds;

-- -------------------------------------------------------------------- 
-- ur_numberofhouseholds_1 
-- -------------------------------------------------------------------- 
DROP TABLE ur_numberofhouseholds_1;

-- -------------------------------------------------------------------- 
-- ur_officesandemployees 
-- -------------------------------------------------------------------- 
DROP TABLE ur_officesandemployees;

-- -------------------------------------------------------------------- 
-- ur_pollution 
-- -------------------------------------------------------------------- 
DROP TABLE ur_pollution;

-- -------------------------------------------------------------------- 
-- ur_population 
-- -------------------------------------------------------------------- 
DROP TABLE ur_population;

-- -------------------------------------------------------------------- 
-- ur_populationbyageandsex 
-- -------------------------------------------------------------------- 
DROP TABLE ur_populationbyageandsex;

-- -------------------------------------------------------------------- 
-- ur_publictransit 
-- -------------------------------------------------------------------- 
DROP TABLE ur_publictransit;

-- -------------------------------------------------------------------- 
-- ur_recreations 
-- -------------------------------------------------------------------- 
DROP TABLE ur_recreations;

-- -------------------------------------------------------------------- 
-- ur_statisticalgrid 
-- -------------------------------------------------------------------- 
DROP TABLE ur_statisticalgrid;

-- -------------------------------------------------------------------- 
-- ur_trafficvolume 
-- -------------------------------------------------------------------- 
DROP TABLE ur_trafficvolume;

-- -------------------------------------------------------------------- 
-- ur_transportation_complex 
-- -------------------------------------------------------------------- 
DROP TABLE ur_transportation_complex;

-- -------------------------------------------------------------------- 
-- ur_urbanfunc_to_cityobjec 
-- -------------------------------------------------------------------- 
DROP TABLE ur_urbanfunc_to_cityobjec;

-- -------------------------------------------------------------------- 
-- ur_urbanfunction 
-- -------------------------------------------------------------------- 
DROP TABLE ur_urbanfunction;

-- -------------------------------------------------------------------- 
-- ur_urbanization 
-- -------------------------------------------------------------------- 
DROP TABLE ur_urbanization;

-- -------------------------------------------------------------------- 
-- ur_zone 
-- -------------------------------------------------------------------- 
DROP TABLE ur_zone;

-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 
-- *********************************** Drop Sequences ************************************* 
-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 

DROP SEQUENCE ur_populationbyageand_seq;

DROP SEQUENCE ur_landpriceperlandus_seq;

DROP SEQUENCE ur_numberofannualdive_seq;

DROP SEQUENCE ur_numberofhouseholds_seq;

DROP SEQUENCE ur_buildingdetails_seq;

DROP SEQUENCE ur_largecustomerfacil_seq;

DROP SEQUENCE ur_trafficvolume_seq;

DROP SEQUENCE ur_legalgrounds_seq;

DROP SEQUENCE ur_numberofhousehol_seq_1;
