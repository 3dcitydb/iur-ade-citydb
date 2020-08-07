-- This document was automatically created by the ADE-Manager tool of 3DCityDB (https://www.3dcitydb.org) on 2020-03-20 09:53:29 
-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 
-- *********************************** Create tables ************************************** 
-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 
-- -------------------------------------------------------------------- 
-- ur_building 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_building
(
    id INTEGER NOT NULL,
    buildingdetails_id INTEGER,
    largecustomerfacilities_id INTEGER,
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_buildingdetails 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_buildingdetails
(
    id INTEGER NOT NULL,
    areaclassification_codespace VARCHAR(1000),
    areaclassificationtype VARCHAR(1000),
    buildingfootprintarea NUMERIC,
    buildingfootprintarea_uom VARCHAR(1000),
    buildingroofedgearea NUMERIC,
    buildingroofedgearea_uom VARCHAR(1000),
    buildingstructuret_codespace VARCHAR(1000),
    buildingstructuretype VARCHAR(1000),
    city VARCHAR(1000),
    city_codespace VARCHAR(1000),
    developmentarea NUMERIC,
    developmentarea_uom VARCHAR(1000),
    districtsandzonest_codespace VARCHAR(1000),
    districtsandzonestype VARCHAR(1000),
    fireproofstructure_codespace VARCHAR(1000),
    fireproofstructuretype VARCHAR(1000),
    implementingbody VARCHAR(1000),
    landuseplantype VARCHAR(1000),
    landuseplantype_codespace VARCHAR(1000),
    note VARCHAR(1000),
    prefecture VARCHAR(1000),
    prefecture_codespace VARCHAR(1000),
    reference VARCHAR(1000),
    serialnumberofbuildingcertif VARCHAR(1000),
    sitearea NUMERIC,
    sitearea_uom VARCHAR(1000),
    surveyyear DATE,
    totalfloorarea NUMERIC,
    totalfloorarea_uom VARCHAR(1000),
    urbanplantype VARCHAR(1000),
    urbanplantype_codespace VARCHAR(1000),
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_censusblock 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_censusblock
(
    id INTEGER NOT NULL,
    daytimepopulation INTEGER,
    daytimepopulationdensity NUMERIC,
    numberofmainhouseholds INTEGER,
    numberofordinaryhouseholds INTEGER,
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_cityobjectgroup 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_cityobjectgroup
(
    id INTEGER NOT NULL,
    fiscalyearofpublication DATE,
    language VARCHAR(1000),
    language_codespace VARCHAR(1000),
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_developmentproject 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_developmentproject
(
    id INTEGER NOT NULL,
    benefitarea NUMERIC,
    benefitarea_uom VARCHAR(1000),
    benefitperiod VARCHAR(1000),
    completedarea NUMERIC,
    completedarea_uom VARCHAR(1000),
    cost INTEGER,
    dateofdecision TIMESTAMP WITH TIME ZONE,
    dateofdesignationfortemporar TIMESTAMP WITH TIME ZONE,
    mainpurpose VARCHAR(1000),
    mainpurpose_codespace VARCHAR(1000),
    ongoingarea NUMERIC,
    ongoingarea_uom VARCHAR(1000),
    plannedarea NUMERIC,
    plannedarea_uom VARCHAR(1000),
    status VARCHAR(1000),
    status_codespace VARCHAR(1000),
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_disasterdamage 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_disasterdamage
(
    id INTEGER NOT NULL,
    damagedarea NUMERIC,
    damagedarea_uom VARCHAR(1000),
    maximumrainfallperhour INTEGER,
    numberofdamagedhouses INTEGER,
    numberofhousesfloodedabovefl INTEGER,
    numberofhousesfloodedbelowfl INTEGER,
    totalrainfall INTEGER,
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_households 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_households
(
    id INTEGER NOT NULL,
    numberofmainhousehold INTEGER,
    numberofordinaryhousehold INTEGER,
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_land_use 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_land_use
(
    id INTEGER NOT NULL,
    areaclassification_codespace VARCHAR(1000),
    areaclassificationtype VARCHAR(1000),
    areainha NUMERIC,
    areainha_uom VARCHAR(1000),
    areainsquaremeter NUMERIC,
    areainsquaremeter_uom VARCHAR(1000),
    city VARCHAR(1000),
    city_codespace VARCHAR(1000),
    districtsandzonest_codespace VARCHAR(1000),
    districtsandzonestype VARCHAR(1000),
    landuseplantype VARCHAR(1000),
    landuseplantype_codespace VARCHAR(1000),
    nominalarea NUMERIC,
    nominalarea_uom VARCHAR(1000),
    note VARCHAR(1000),
    owner VARCHAR(1000),
    ownertype VARCHAR(1000),
    ownertype_codespace VARCHAR(1000),
    prefecture VARCHAR(1000),
    prefecture_codespace VARCHAR(1000),
    reference VARCHAR(1000),
    surveyyear DATE,
    urbanplantype VARCHAR(1000),
    urbanplantype_codespace VARCHAR(1000),
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_landpriceperlanduse 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_landpriceperlanduse
(
    id INTEGER NOT NULL,
    currencyunit VARCHAR(1000),
    currencyunit_codespace VARCHAR(1000),
    landprice INTEGER,
    landuse VARCHAR(1000),
    landuse_codespace VARCHAR(1000),
    statisticalgrid_landprice_id INTEGER,
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_largecustomerfacilitie 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_largecustomerfacilitie
(
    id INTEGER NOT NULL,
    areaclassification_codespace VARCHAR(1000),
    areaclassificationtype VARCHAR(1000),
    availability NUMERIC,
    capacity INTEGER,
    city VARCHAR(1000),
    city_codespace VARCHAR(1000),
    class VARCHAR(1000),
    class_codespace VARCHAR(1000),
    districtsandzonest_codespace VARCHAR(1000),
    districtsandzonestype VARCHAR(1000),
    inauguraldate TIMESTAMP WITH TIME ZONE,
    keytenants VARCHAR(1000),
    landuseplantype VARCHAR(1000),
    landuseplantype_codespace VARCHAR(1000),
    name VARCHAR(1000),
    note VARCHAR(1000),
    owner VARCHAR(1000),
    prefecture VARCHAR(1000),
    prefecture_codespace VARCHAR(1000),
    reference VARCHAR(1000),
    surveyyear DATE,
    totalfloorarea NUMERIC,
    totalfloorarea_uom VARCHAR(1000),
    totalstorefloorarea NUMERIC,
    totalstorefloorarea_uom VARCHAR(1000),
    urbanplantype VARCHAR(1000),
    urbanplantype_codespace VARCHAR(1000),
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_legalgrounds 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_legalgrounds
(
    id INTEGER NOT NULL,
    articlesofregulati_codespace VARCHAR(1000),
    articlesofregulation VARCHAR(1000),
    date_ TIMESTAMP WITH TIME ZONE,
    nameofregulation VARCHAR(1000),
    nameofregulation_codespace VARCHAR(1000),
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_numberofannualdiversio 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_numberofannualdiversio
(
    id INTEGER NOT NULL,
    area NUMERIC,
    area_uom VARCHAR(1000),
    count INTEGER,
    statisticalg_numberofannu_id INTEGER,
    year DATE,
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_numberofhouseholds 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_numberofhouseholds
(
    id INTEGER NOT NULL,
    class VARCHAR(1000),
    class_codespace VARCHAR(1000),
    households_numberofhous_id_1 INTEGER,
    households_numberofhouseh_id INTEGER,
    number_ INTEGER,
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_numberofhouseholds_1 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_numberofhouseholds_1
(
    id INTEGER NOT NULL,
    censusblock_numberofhou_id_1 INTEGER,
    censusblock_numberofhouse_id INTEGER,
    class VARCHAR(1000),
    class_codespace VARCHAR(1000),
    number_ INTEGER,
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_officesandemployees 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_officesandemployees
(
    id INTEGER NOT NULL,
    numberofemployees INTEGER,
    numberofoffices INTEGER,
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_pollution 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_pollution
(
    id INTEGER NOT NULL,
    cause VARCHAR(1000),
    damagedarea NUMERIC,
    damagedarea_uom VARCHAR(1000),
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_population 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_population
(
    id INTEGER NOT NULL,
    births INTEGER,
    daytimepopulation INTEGER,
    daytimepopulationdensity NUMERIC,
    deaths INTEGER,
    femalepopulation INTEGER,
    increasement INTEGER,
    malepopulation INTEGER,
    movefrom INTEGER,
    moveto INTEGER,
    naturalincrease INTEGER,
    socialincrease INTEGER,
    total INTEGER,
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_populationbyageandsex 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_populationbyageandsex
(
    id INTEGER NOT NULL,
    age VARCHAR(1000),
    age_codespace VARCHAR(1000),
    number_ INTEGER,
    population_populationbyag_id INTEGER,
    sex VARCHAR(1000),
    sex_codespace VARCHAR(1000),
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_publictransit 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_publictransit
(
    id INTEGER NOT NULL,
    companyname VARCHAR(1000),
    frequencyofservice INTEGER,
    numberofcustomers NUMERIC,
    routename VARCHAR(1000),
    sectionname VARCHAR(1000),
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_recreations 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_recreations
(
    id INTEGER NOT NULL,
    capacity INTEGER,
    numberofusers INTEGER,
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_statisticalgrid 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_statisticalgrid
(
    id INTEGER NOT NULL,
    areaclassification_codespace VARCHAR(1000),
    areaclassificationtype VARCHAR(1000),
    availability NUMERIC,
    city VARCHAR(1000),
    city_codespace VARCHAR(1000),
    class VARCHAR(1000),
    class_codespace VARCHAR(1000),
    lod_1multisurfacegeometry_id INTEGER,
    lod_2multisurfacegeometry_id INTEGER,
    objectclass_id INTEGER,
    prefecture VARCHAR(1000),
    prefecture_codespace VARCHAR(1000),
    surveyyear DATE,
    urbanplantype VARCHAR(1000),
    urbanplantype_codespace VARCHAR(1000),
    value TEXT,
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_trafficvolume 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_trafficvolume
(
    id INTEGER NOT NULL,
    areaclassification_codespace VARCHAR(1000),
    areaclassificationtype VARCHAR(1000),
    averagetravelspeedincongesti NUMERIC,
    city VARCHAR(1000),
    city_codespace VARCHAR(1000),
    congestionrate NUMERIC,
    largevehiclerate NUMERIC,
    note VARCHAR(1000),
    observationpointname VARCHAR(1000),
    prefecture VARCHAR(1000),
    prefecture_codespace VARCHAR(1000),
    reference VARCHAR(1000),
    surveyyear DATE,
    urbanplantype VARCHAR(1000),
    urbanplantype_codespace VARCHAR(1000),
    weekday12hourtrafficvolume INTEGER,
    weekday24hourtrafficvolume INTEGER,
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_transportation_complex 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_transportation_complex
(
    id INTEGER NOT NULL,
    trafficvolume_id INTEGER,
    width NUMERIC,
    width_uom VARCHAR(1000),
    widthtype VARCHAR(1000),
    widthtype_codespace VARCHAR(1000),
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_urbanfunc_to_cityobjec 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_urbanfunc_to_cityobjec
(
    cityobject_id INTEGER NOT NULL,
    urbanfunction_id INTEGER NOT NULL,
    PRIMARY KEY (cityobject_id, urbanfunction_id)
);

-- -------------------------------------------------------------------- 
-- ur_urbanfunction 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_urbanfunction
(
    id INTEGER NOT NULL,
    abstract VARCHAR(1000),
    area_id INTEGER,
    areaclassification_codespace VARCHAR(1000),
    areaclassificationtype VARCHAR(1000),
    boundary geometry(GEOMETRYZ),
    capacity INTEGER,
    city VARCHAR(1000),
    city_codespace VARCHAR(1000),
    class VARCHAR(1000),
    class_codespace VARCHAR(1000),
    custodian VARCHAR(1000),
    enactmentdate TIMESTAMP WITH TIME ZONE,
    enactmentfiscalyear DATE,
    expirationdate TIMESTAMP WITH TIME ZONE,
    expirationfiscalyear DATE,
    function VARCHAR(1000),
    function_codespace VARCHAR(1000),
    legalgrounds_id INTEGER,
    location geometry(GEOMETRYZ),
    nominalarea NUMERIC,
    nominalarea_uom VARCHAR(1000),
    note VARCHAR(1000),
    objectclass_id INTEGER,
    prefecture VARCHAR(1000),
    prefecture_codespace VARCHAR(1000),
    reference VARCHAR(1000),
    surveyyear DATE,
    urbanplantype VARCHAR(1000),
    urbanplantype_codespace VARCHAR(1000),
    validity NUMERIC,
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_urbanization 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_urbanization
(
    id INTEGER NOT NULL,
    period VARCHAR(1000),
    resources VARCHAR(1000),
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_zone 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_zone
(
    id INTEGER NOT NULL,
    areaapplied VARCHAR(1000),
    finalpublicationdate TIMESTAMP WITH TIME ZONE,
    objectclass_id INTEGER,
    PRIMARY KEY (id)
);

-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 
-- *********************************** Create foreign keys ******************************** 
-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 
-- -------------------------------------------------------------------- 
-- ur_building 
-- -------------------------------------------------------------------- 
ALTER TABLE ur_building ADD CONSTRAINT ur_building_fk FOREIGN KEY (id)
REFERENCES building (id);

ALTER TABLE ur_building ADD CONSTRAINT ur_building_buildingdet_fk FOREIGN KEY (buildingdetails_id)
REFERENCES ur_buildingdetails (id)
ON DELETE SET NULL;

ALTER TABLE ur_building ADD CONSTRAINT ur_building_largecustom_fk FOREIGN KEY (largecustomerfacilities_id)
REFERENCES ur_largecustomerfacilitie (id)
ON DELETE SET NULL;

-- -------------------------------------------------------------------- 
-- ur_censusblock 
-- -------------------------------------------------------------------- 
ALTER TABLE ur_censusblock ADD CONSTRAINT ur_censusblock_fk FOREIGN KEY (id)
REFERENCES ur_urbanfunction (id);

-- -------------------------------------------------------------------- 
-- ur_cityobjectgroup 
-- -------------------------------------------------------------------- 
ALTER TABLE ur_cityobjectgroup ADD CONSTRAINT ur_cityobjectgroup_fk FOREIGN KEY (id)
REFERENCES cityobjectgroup (id);

-- -------------------------------------------------------------------- 
-- ur_developmentproject 
-- -------------------------------------------------------------------- 
ALTER TABLE ur_developmentproject ADD CONSTRAINT ur_developmentproject_fk FOREIGN KEY (id)
REFERENCES ur_zone (id);

-- -------------------------------------------------------------------- 
-- ur_disasterdamage 
-- -------------------------------------------------------------------- 
ALTER TABLE ur_disasterdamage ADD CONSTRAINT ur_disasterdamage_fk FOREIGN KEY (id)
REFERENCES ur_urbanfunction (id);

-- -------------------------------------------------------------------- 
-- ur_households 
-- -------------------------------------------------------------------- 
ALTER TABLE ur_households ADD CONSTRAINT ur_households_fk FOREIGN KEY (id)
REFERENCES ur_statisticalgrid (id);

-- -------------------------------------------------------------------- 
-- ur_land_use 
-- -------------------------------------------------------------------- 
ALTER TABLE ur_land_use ADD CONSTRAINT ur_land_use_fk FOREIGN KEY (id)
REFERENCES land_use (id);

-- -------------------------------------------------------------------- 
-- ur_landpriceperlanduse 
-- -------------------------------------------------------------------- 
ALTER TABLE ur_landpriceperlanduse ADD CONSTRAINT ur_landpr_statis_landpr_fk FOREIGN KEY (statisticalgrid_landprice_id)
REFERENCES ur_statisticalgrid (id);

-- -------------------------------------------------------------------- 
-- ur_numberofannualdiversio 
-- -------------------------------------------------------------------- 
ALTER TABLE ur_numberofannualdiversio ADD CONSTRAINT ur_number_statis_number_fk FOREIGN KEY (statisticalg_numberofannu_id)
REFERENCES ur_statisticalgrid (id);

-- -------------------------------------------------------------------- 
-- ur_numberofhouseholds 
-- -------------------------------------------------------------------- 
ALTER TABLE ur_numberofhouseholds ADD CONSTRAINT ur_number_househ_number_fk FOREIGN KEY (households_numberofhouseh_id)
REFERENCES ur_households (id);

ALTER TABLE ur_numberofhouseholds ADD CONSTRAINT ur_number_house_numbe_fk_1 FOREIGN KEY (households_numberofhous_id_1)
REFERENCES ur_households (id);

-- -------------------------------------------------------------------- 
-- ur_numberofhouseholds_1 
-- -------------------------------------------------------------------- 
ALTER TABLE ur_numberofhouseholds_1 ADD CONSTRAINT ur_number_census_number_fk FOREIGN KEY (censusblock_numberofhouse_id)
REFERENCES ur_censusblock (id);

ALTER TABLE ur_numberofhouseholds_1 ADD CONSTRAINT ur_number_censu_numbe_fk_1 FOREIGN KEY (censusblock_numberofhou_id_1)
REFERENCES ur_censusblock (id);

-- -------------------------------------------------------------------- 
-- ur_officesandemployees 
-- -------------------------------------------------------------------- 
ALTER TABLE ur_officesandemployees ADD CONSTRAINT ur_officesandemployees_fk FOREIGN KEY (id)
REFERENCES ur_statisticalgrid (id);

-- -------------------------------------------------------------------- 
-- ur_pollution 
-- -------------------------------------------------------------------- 
ALTER TABLE ur_pollution ADD CONSTRAINT ur_pollution_fk FOREIGN KEY (id)
REFERENCES ur_urbanfunction (id);

-- -------------------------------------------------------------------- 
-- ur_population 
-- -------------------------------------------------------------------- 
ALTER TABLE ur_population ADD CONSTRAINT ur_population_fk FOREIGN KEY (id)
REFERENCES ur_statisticalgrid (id);

-- -------------------------------------------------------------------- 
-- ur_populationbyageandsex 
-- -------------------------------------------------------------------- 
ALTER TABLE ur_populationbyageandsex ADD CONSTRAINT ur_popula_popula_popula_fk FOREIGN KEY (population_populationbyag_id)
REFERENCES ur_population (id);

-- -------------------------------------------------------------------- 
-- ur_publictransit 
-- -------------------------------------------------------------------- 
ALTER TABLE ur_publictransit ADD CONSTRAINT ur_publictransit_fk FOREIGN KEY (id)
REFERENCES ur_urbanfunction (id);

-- -------------------------------------------------------------------- 
-- ur_recreations 
-- -------------------------------------------------------------------- 
ALTER TABLE ur_recreations ADD CONSTRAINT ur_recreations_fk FOREIGN KEY (id)
REFERENCES ur_urbanfunction (id);

-- -------------------------------------------------------------------- 
-- ur_statisticalgrid 
-- -------------------------------------------------------------------- 
ALTER TABLE ur_statisticalgrid ADD CONSTRAINT ur_statisticalgrid_fk FOREIGN KEY (id)
REFERENCES cityobject (id);

ALTER TABLE ur_statisticalgrid ADD CONSTRAINT ur_statistica_objectcla_fk FOREIGN KEY (objectclass_id)
REFERENCES objectclass (id);

ALTER TABLE ur_statisticalgrid ADD CONSTRAINT ur_statisti_lod_1multis_fk FOREIGN KEY (lod_1multisurfacegeometry_id)
REFERENCES surface_geometry (id);

ALTER TABLE ur_statisticalgrid ADD CONSTRAINT ur_statisti_lod_2multis_fk FOREIGN KEY (lod_2multisurfacegeometry_id)
REFERENCES surface_geometry (id);

-- -------------------------------------------------------------------- 
-- ur_transportation_complex 
-- -------------------------------------------------------------------- 
ALTER TABLE ur_transportation_complex ADD CONSTRAINT ur_transportati_complex_fk FOREIGN KEY (id)
REFERENCES transportation_complex (id);

ALTER TABLE ur_transportation_complex ADD CONSTRAINT ur_transp_comple_traffi_fk FOREIGN KEY (trafficvolume_id)
REFERENCES ur_trafficvolume (id)
ON DELETE SET NULL;

-- -------------------------------------------------------------------- 
-- ur_urbanfunc_to_cityobjec 
-- -------------------------------------------------------------------- 
ALTER TABLE ur_urbanfunc_to_cityobjec ADD CONSTRAINT ur_urbanfun_to_cityobj_fk1 FOREIGN KEY (urbanfunction_id)
REFERENCES ur_urbanfunction (id)
ON DELETE CASCADE;

ALTER TABLE ur_urbanfunc_to_cityobjec ADD CONSTRAINT ur_urbanfun_to_cityobj_fk2 FOREIGN KEY (cityobject_id)
REFERENCES cityobject (id)
ON DELETE CASCADE;

-- -------------------------------------------------------------------- 
-- ur_urbanfunction 
-- -------------------------------------------------------------------- 
ALTER TABLE ur_urbanfunction ADD CONSTRAINT ur_urbanfunct_objectcla_fk FOREIGN KEY (objectclass_id)
REFERENCES objectclass (id);

ALTER TABLE ur_urbanfunction ADD CONSTRAINT ur_urbanfunction_fk FOREIGN KEY (id)
REFERENCES cityobject (id);

ALTER TABLE ur_urbanfunction ADD CONSTRAINT ur_urbanfunct_legalgrou_fk FOREIGN KEY (legalgrounds_id)
REFERENCES ur_legalgrounds (id)
ON DELETE SET NULL;

ALTER TABLE ur_urbanfunction ADD CONSTRAINT ur_urbanfunction_area_fk FOREIGN KEY (area_id)
REFERENCES surface_geometry (id);

-- -------------------------------------------------------------------- 
-- ur_urbanization 
-- -------------------------------------------------------------------- 
ALTER TABLE ur_urbanization ADD CONSTRAINT ur_urbanization_fk FOREIGN KEY (id)
REFERENCES ur_urbanfunction (id);

-- -------------------------------------------------------------------- 
-- ur_zone 
-- -------------------------------------------------------------------- 
ALTER TABLE ur_zone ADD CONSTRAINT ur_zone_objectclass_fk FOREIGN KEY (objectclass_id)
REFERENCES objectclass (id);

ALTER TABLE ur_zone ADD CONSTRAINT ur_zone_fk FOREIGN KEY (id)
REFERENCES ur_urbanfunction (id);

-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 
-- *********************************** Create Indexes ************************************* 
-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 
-- -------------------------------------------------------------------- 
-- ur_building 
-- -------------------------------------------------------------------- 
CREATE INDEX ur_building_buildingde_fkx ON ur_building
    USING btree
    (
      buildingdetails_id ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

CREATE INDEX ur_building_largecusto_fkx ON ur_building
    USING btree
    (
      largecustomerfacilities_id ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

-- -------------------------------------------------------------------- 
-- ur_landpriceperlanduse 
-- -------------------------------------------------------------------- 
CREATE INDEX ur_landpr_statis_landp_fkx ON ur_landpriceperlanduse
    USING btree
    (
      statisticalgrid_landprice_id ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

-- -------------------------------------------------------------------- 
-- ur_numberofannualdiversio 
-- -------------------------------------------------------------------- 
CREATE INDEX ur_number_statis_numbe_fkx ON ur_numberofannualdiversio
    USING btree
    (
      statisticalg_numberofannu_id ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

-- -------------------------------------------------------------------- 
-- ur_numberofhouseholds 
-- -------------------------------------------------------------------- 
CREATE INDEX ur_numbe_house_numbe_fkx_1 ON ur_numberofhouseholds
    USING btree
    (
      households_numberofhous_id_1 ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

CREATE INDEX ur_number_househ_numbe_fkx ON ur_numberofhouseholds
    USING btree
    (
      households_numberofhouseh_id ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

-- -------------------------------------------------------------------- 
-- ur_numberofhouseholds_1 
-- -------------------------------------------------------------------- 
CREATE INDEX ur_numbe_censu_numbe_fkx_1 ON ur_numberofhouseholds_1
    USING btree
    (
      censusblock_numberofhou_id_1 ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

CREATE INDEX ur_number_census_numbe_fkx ON ur_numberofhouseholds_1
    USING btree
    (
      censusblock_numberofhouse_id ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

-- -------------------------------------------------------------------- 
-- ur_populationbyageandsex 
-- -------------------------------------------------------------------- 
CREATE INDEX ur_popula_popula_popul_fkx ON ur_populationbyageandsex
    USING btree
    (
      population_populationbyag_id ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

-- -------------------------------------------------------------------- 
-- ur_statisticalgrid 
-- -------------------------------------------------------------------- 
CREATE INDEX ur_statist_lod_1multis_fkx ON ur_statisticalgrid
    USING btree
    (
      lod_1multisurfacegeometry_id ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

CREATE INDEX ur_statist_lod_2multis_fkx ON ur_statisticalgrid
    USING btree
    (
      lod_2multisurfacegeometry_id ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

CREATE INDEX ur_statistic_objectcla_fkx ON ur_statisticalgrid
    USING btree
    (
      objectclass_id ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

-- -------------------------------------------------------------------- 
-- ur_transportation_complex 
-- -------------------------------------------------------------------- 
CREATE INDEX ur_transp_comple_traff_fkx ON ur_transportation_complex
    USING btree
    (
      trafficvolume_id ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

-- -------------------------------------------------------------------- 
-- ur_urbanfunc_to_cityobjec 
-- -------------------------------------------------------------------- 
CREATE INDEX ur_urbanfu_to_cityobj_fk2x ON ur_urbanfunc_to_cityobjec
    USING btree
    (
      cityobject_id ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

CREATE INDEX ur_urbanfu_to_cityobj_fk1x ON ur_urbanfunc_to_cityobjec
    USING btree
    (
      urbanfunction_id ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

-- -------------------------------------------------------------------- 
-- ur_urbanfunction 
-- -------------------------------------------------------------------- 
CREATE INDEX ur_urbanfunction_area_fkx ON ur_urbanfunction
    USING btree
    (
      area_id ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

CREATE INDEX ur_urbanfunct_boundary_spx ON ur_urbanfunction
    USING gist
    (
      boundary
    );

CREATE INDEX ur_urbanfunc_legalgrou_fkx ON ur_urbanfunction
    USING btree
    (
      legalgrounds_id ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

CREATE INDEX ur_urbanfunct_location_spx ON ur_urbanfunction
    USING gist
    (
      location
    );

CREATE INDEX ur_urbanfunc_objectcla_fkx ON ur_urbanfunction
    USING btree
    (
      objectclass_id ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

-- -------------------------------------------------------------------- 
-- ur_zone 
-- -------------------------------------------------------------------- 
CREATE INDEX ur_zone_objectclass_fkx ON ur_zone
    USING btree
    (
      objectclass_id ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 
-- *********************************** Create Sequences *********************************** 
-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 

CREATE SEQUENCE ur_populationbyageand_seq
INCREMENT BY 1
MINVALUE 0
MAXVALUE 2147483647
START WITH 1
CACHE 1
NO CYCLE
OWNED BY NONE;


CREATE SEQUENCE ur_landpriceperlandus_seq
INCREMENT BY 1
MINVALUE 0
MAXVALUE 2147483647
START WITH 1
CACHE 1
NO CYCLE
OWNED BY NONE;


CREATE SEQUENCE ur_numberofannualdive_seq
INCREMENT BY 1
MINVALUE 0
MAXVALUE 2147483647
START WITH 1
CACHE 1
NO CYCLE
OWNED BY NONE;


CREATE SEQUENCE ur_numberofhouseholds_seq
INCREMENT BY 1
MINVALUE 0
MAXVALUE 2147483647
START WITH 1
CACHE 1
NO CYCLE
OWNED BY NONE;


CREATE SEQUENCE ur_buildingdetails_seq
INCREMENT BY 1
MINVALUE 0
MAXVALUE 2147483647
START WITH 1
CACHE 1
NO CYCLE
OWNED BY NONE;


CREATE SEQUENCE ur_largecustomerfacil_seq
INCREMENT BY 1
MINVALUE 0
MAXVALUE 2147483647
START WITH 1
CACHE 1
NO CYCLE
OWNED BY NONE;


CREATE SEQUENCE ur_trafficvolume_seq
INCREMENT BY 1
MINVALUE 0
MAXVALUE 2147483647
START WITH 1
CACHE 1
NO CYCLE
OWNED BY NONE;


CREATE SEQUENCE ur_legalgrounds_seq
INCREMENT BY 1
MINVALUE 0
MAXVALUE 2147483647
START WITH 1
CACHE 1
NO CYCLE
OWNED BY NONE;


CREATE SEQUENCE ur_numberofhousehol_seq_1
INCREMENT BY 1
MINVALUE 0
MAXVALUE 2147483647
START WITH 1
CACHE 1
NO CYCLE
OWNED BY NONE;


