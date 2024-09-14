-- This document was automatically created by the ADE-Manager tool of 3DCityDB (https://www.3dcitydb.org) on 2024-09-14 20:02:27 
-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 
-- *********************************** Create Sequences *********************************** 
-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 

CREATE SEQUENCE ur_buildingdetails_seq
INCREMENT BY 1
MINVALUE 0
MAXVALUE 9223372036854775807
START WITH 1
CACHE 1
NO CYCLE
OWNED BY NONE;


CREATE SEQUENCE ur_areaofannualdivers_seq
INCREMENT BY 1
MINVALUE 0
MAXVALUE 9223372036854775807
START WITH 1
CACHE 1
NO CYCLE
OWNED BY NONE;


CREATE SEQUENCE ur_keyvaluepair_seq_1
INCREMENT BY 1
MINVALUE 0
MAXVALUE 9223372036854775807
START WITH 1
CACHE 1
NO CYCLE
OWNED BY NONE;


CREATE SEQUENCE ur_point_seq
INCREMENT BY 1
MINVALUE 0
MAXVALUE 9223372036854775807
START WITH 1
CACHE 1
NO CYCLE
OWNED BY NONE;


CREATE SEQUENCE ur_numberofhouseholds_seq
INCREMENT BY 1
MINVALUE 0
MAXVALUE 9223372036854775807
START WITH 1
CACHE 1
NO CYCLE
OWNED BY NONE;


CREATE SEQUENCE ur_keyvaluepair_seq
INCREMENT BY 1
MINVALUE 0
MAXVALUE 9223372036854775807
START WITH 1
CACHE 1
NO CYCLE
OWNED BY NONE;


CREATE SEQUENCE ur_largecustomerfacil_seq
INCREMENT BY 1
MINVALUE 0
MAXVALUE 9223372036854775807
START WITH 1
CACHE 1
NO CYCLE
OWNED BY NONE;


CREATE SEQUENCE ur_description_seq
INCREMENT BY 1
MINVALUE 0
MAXVALUE 9223372036854775807
START WITH 1
CACHE 1
NO CYCLE
OWNED BY NONE;


CREATE SEQUENCE ur_publictransitdatat_seq
INCREMENT BY 1
MINVALUE 0
MAXVALUE 9223372036854775807
START WITH 1
CACHE 1
NO CYCLE
OWNED BY NONE;


CREATE SEQUENCE ur_numberofannualdive_seq
INCREMENT BY 1
MINVALUE 0
MAXVALUE 9223372036854775807
START WITH 1
CACHE 1
NO CYCLE
OWNED BY NONE;


CREATE SEQUENCE ur_trafficvolume_seq
INCREMENT BY 1
MINVALUE 0
MAXVALUE 9223372036854775807
START WITH 1
CACHE 1
NO CYCLE
OWNED BY NONE;


CREATE SEQUENCE ur_landpriceperlandus_seq
INCREMENT BY 1
MINVALUE 0
MAXVALUE 9223372036854775807
START WITH 1
CACHE 1
NO CYCLE
OWNED BY NONE;


CREATE SEQUENCE ur_legalgrounds_seq
INCREMENT BY 1
MINVALUE 0
MAXVALUE 9223372036854775807
START WITH 1
CACHE 1
NO CYCLE
OWNED BY NONE;


CREATE SEQUENCE ur_populationbyageand_seq
INCREMENT BY 1
MINVALUE 0
MAXVALUE 9223372036854775807
START WITH 1
CACHE 1
NO CYCLE
OWNED BY NONE;


CREATE SEQUENCE ur_numberofhousehol_seq_1
INCREMENT BY 1
MINVALUE 0
MAXVALUE 9223372036854775807
START WITH 1
CACHE 1
NO CYCLE
OWNED BY NONE;


-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 
-- *********************************** Create tables ************************************** 
-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 
-- -------------------------------------------------------------------- 
-- ur_agency 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_agency
(
    id BIGINT NOT NULL,
    address VARCHAR(1000),
    email VARCHAR(1000),
    fareurl VARCHAR(1000),
    language VARCHAR(1000),
    language_codespace VARCHAR(1000),
    name VARCHAR(1000),
    officialname VARCHAR(1000),
    phone VARCHAR(1000),
    presidentname VARCHAR(1000),
    presidentposition VARCHAR(1000),
    timezone VARCHAR(1000),
    timezone_codespace VARCHAR(1000),
    url VARCHAR(1000),
    zipnumber VARCHAR(1000),
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_areaofannualdiversions 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_areaofannualdiversions
(
    id BIGINT NOT NULL DEFAULT nextval('ur_areaofannualdivers_seq'::regclass) NOT NULL,
    area NUMERIC,
    area_uom VARCHAR(1000),
    landusediver_areaofannual_id BIGINT,
    year DATE,
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_attribution 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_attribution
(
    id BIGINT NOT NULL,
    agency_id BIGINT,
    email VARCHAR(1000),
    isauthority NUMERIC,
    isoperator NUMERIC,
    isproducer NUMERIC,
    organizationname VARCHAR(1000),
    phonenumber VARCHAR(1000),
    route_id BIGINT,
    trip_id BIGINT,
    url VARCHAR(1000),
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_building 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_building
(
    id BIGINT NOT NULL,
    buildingdetails_id BIGINT,
    largecustomerfacilities_id BIGINT,
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_buildingdetails 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_buildingdetails
(
    id BIGINT NOT NULL DEFAULT nextval('ur_buildingdetails_seq'::regclass) NOT NULL,
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
-- ur_calendar 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_calendar
(
    id BIGINT NOT NULL,
    enddate TIMESTAMP WITH TIME ZONE,
    friday NUMERIC,
    monday NUMERIC,
    saturday NUMERIC,
    startdate TIMESTAMP WITH TIME ZONE,
    sunday NUMERIC,
    thursday NUMERIC,
    tuesday NUMERIC,
    wednesday NUMERIC,
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_calendardate 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_calendardate
(
    id BIGINT NOT NULL,
    calendar_id BIGINT,
    date_ TIMESTAMP WITH TIME ZONE,
    exceptiontype VARCHAR(1000),
    exceptiontype_codespace VARCHAR(1000),
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_censusblock 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_censusblock
(
    id BIGINT NOT NULL,
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
    id BIGINT NOT NULL,
    fiscalyearofpublication DATE,
    language VARCHAR(1000),
    language_codespace VARCHAR(1000),
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_cityobjectgroup_1 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_cityobjectgroup_1
(
    id BIGINT NOT NULL,
    fiscalyearofpublication DATE,
    language VARCHAR(1000),
    language_codespace VARCHAR(1000),
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_cityobjectgroup_2 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_cityobjectgroup_2
(
    id BIGINT NOT NULL,
    fiscalyearofpublication DATE,
    language VARCHAR(1000),
    language_codespace VARCHAR(1000),
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_cityobjectgroup_3 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_cityobjectgroup_3
(
    id BIGINT NOT NULL,
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_description 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_description
(
    id BIGINT NOT NULL DEFAULT nextval('ur_description_seq'::regclass) NOT NULL,
    description VARCHAR(1000),
    frequencyofservice INTEGER,
    numberofcustomers INTEGER,
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_developmentproject 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_developmentproject
(
    id BIGINT NOT NULL,
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
    id BIGINT NOT NULL,
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
-- ur_fareattribute 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_fareattribute
(
    id BIGINT NOT NULL,
    agency_id BIGINT,
    currencytype VARCHAR(1000),
    currencytype_codespace VARCHAR(1000),
    paymentmethod VARCHAR(1000),
    paymentmethod_codespace VARCHAR(1000),
    price NUMERIC,
    transferduration INTEGER,
    transfers VARCHAR(1000),
    transfers_codespace VARCHAR(1000),
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_farerule 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_farerule
(
    id BIGINT NOT NULL,
    containsid VARCHAR(1000),
    containsid_codespace VARCHAR(1000),
    destinationid VARCHAR(1000),
    destinationid_codespace VARCHAR(1000),
    fare_id BIGINT,
    originid VARCHAR(1000),
    originid_codespace VARCHAR(1000),
    route_id BIGINT,
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_feedinfo 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_feedinfo
(
    id BIGINT NOT NULL,
    contactemail VARCHAR(1000),
    contacturl VARCHAR(1000),
    defaultlanguage VARCHAR(1000),
    defaultlanguage_codespace VARCHAR(1000),
    detailedinfo VARCHAR(1000),
    enddate TIMESTAMP WITH TIME ZONE,
    language VARCHAR(1000),
    language_codespace VARCHAR(1000),
    publishername VARCHAR(1000),
    publisherurl VARCHAR(1000),
    startdate TIMESTAMP WITH TIME ZONE,
    version VARCHAR(1000),
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_frequency 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_frequency
(
    id BIGINT NOT NULL,
    endtime TIMESTAMP WITH TIME ZONE,
    exacttimes VARCHAR(1000),
    exacttimes_codespace VARCHAR(1000),
    headwaysecs INTEGER,
    starttime TIMESTAMP WITH TIME ZONE,
    trip_id BIGINT,
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_households 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_households
(
    id BIGINT NOT NULL,
    numberofmainhousehold INTEGER,
    numberofordinaryhousehold INTEGER,
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_keyvaluepair 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_keyvaluepair
(
    id BIGINT NOT NULL DEFAULT nextval('ur_keyvaluepair_seq'::regclass) NOT NULL,
    codevalue VARCHAR(1000),
    codevalue_codespace VARCHAR(1000),
    datevalue TIMESTAMP WITH TIME ZONE,
    doublevalue NUMERIC,
    intvalue INTEGER,
    key VARCHAR(1000),
    key_codespace VARCHAR(1000),
    measuredvalue NUMERIC,
    measuredvalue_uom VARCHAR(1000),
    statisticalg_genericvalue_id BIGINT,
    stringvalue VARCHAR(1000),
    urivalue VARCHAR(1000),
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_keyvaluepair_1 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_keyvaluepair_1
(
    id BIGINT NOT NULL DEFAULT nextval('ur_keyvaluepair_seq_1'::regclass) NOT NULL,
    building_extendedattribut_id BIGINT,
    codevalue VARCHAR(1000),
    codevalue_codespace VARCHAR(1000),
    datevalue TIMESTAMP WITH TIME ZONE,
    doublevalue NUMERIC,
    intvalue INTEGER,
    key VARCHAR(1000),
    key_codespace VARCHAR(1000),
    measuredvalue NUMERIC,
    measuredvalue_uom VARCHAR(1000),
    stringvalue VARCHAR(1000),
    urivalue VARCHAR(1000),
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_land_use 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_land_use
(
    id BIGINT NOT NULL,
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
-- ur_landprice 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_landprice
(
    id BIGINT NOT NULL,
    currencyunit VARCHAR(1000),
    currencyunit_codespace VARCHAR(1000),
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_landpriceperlanduse 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_landpriceperlanduse
(
    id BIGINT NOT NULL DEFAULT nextval('ur_landpriceperlandus_seq'::regclass) NOT NULL,
    landprice INTEGER,
    landprice_landprice_id BIGINT,
    landuse VARCHAR(1000),
    landuse_codespace VARCHAR(1000),
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_landusediversion 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_landusediversion
(
    id BIGINT NOT NULL,
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_largecustomerfacilitie 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_largecustomerfacilitie
(
    id BIGINT NOT NULL DEFAULT nextval('ur_largecustomerfacil_seq'::regclass) NOT NULL,
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
    id BIGINT NOT NULL DEFAULT nextval('ur_legalgrounds_seq'::regclass) NOT NULL,
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
    id BIGINT NOT NULL DEFAULT nextval('ur_numberofannualdive_seq'::regclass) NOT NULL,
    count INTEGER,
    landusediver_numberofannu_id BIGINT,
    year DATE,
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_numberofhouseholds 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_numberofhouseholds
(
    id BIGINT NOT NULL DEFAULT nextval('ur_numberofhouseholds_seq'::regclass) NOT NULL,
    class VARCHAR(1000),
    class_codespace VARCHAR(1000),
    households_numberofhous_id_1 BIGINT,
    households_numberofhouseh_id BIGINT,
    number_ INTEGER,
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_numberofhouseholds_1 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_numberofhouseholds_1
(
    id BIGINT NOT NULL DEFAULT nextval('ur_numberofhousehol_seq_1'::regclass) NOT NULL,
    censusblock_numberofhou_id_1 BIGINT,
    censusblock_numberofhouse_id BIGINT,
    class VARCHAR(1000),
    class_codespace VARCHAR(1000),
    number_ INTEGER,
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_office 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_office
(
    id BIGINT NOT NULL,
    name VARCHAR(1000),
    phone VARCHAR(1000),
    url VARCHAR(1000),
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_officesandemployees 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_officesandemployees
(
    id BIGINT NOT NULL,
    numberofemployees INTEGER,
    numberofoffices INTEGER,
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_pathway 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_pathway
(
    id BIGINT NOT NULL,
    from_id BIGINT,
    isbidirectional VARCHAR(1000),
    isbidirectional_codespace VARCHAR(1000),
    length NUMERIC,
    length_uom VARCHAR(1000),
    maxslope NUMERIC,
    minwidth NUMERIC,
    mode_ VARCHAR(1000),
    mode_codespace VARCHAR(1000),
    reversedsignpostedas VARCHAR(1000),
    signpostedas VARCHAR(1000),
    staircount INTEGER,
    to_id BIGINT,
    traversaltime INTEGER,
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_point 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_point
(
    id BIGINT NOT NULL DEFAULT nextval('ur_point_seq'::regclass) NOT NULL,
    latitude NUMERIC,
    longitude NUMERIC,
    point geometry(GEOMETRYZ),
    pointdistancetraveled NUMERIC,
    pointsequence INTEGER,
    publictransit_point_id BIGINT,
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_pollution 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_pollution
(
    id BIGINT NOT NULL,
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
    id BIGINT NOT NULL,
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
    id BIGINT NOT NULL DEFAULT nextval('ur_populationbyageand_seq'::regclass) NOT NULL,
    ageandsex VARCHAR(1000),
    ageandsex_codespace VARCHAR(1000),
    number_ INTEGER,
    population_populationbyag_id BIGINT,
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_publictransit 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_publictransit
(
    id BIGINT NOT NULL,
    index_ NUMERIC,
    objectclass_id INTEGER,
    orgid VARCHAR(1000),
    target_id BIGINT,
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_publictransitdatatype 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_publictransitdatatype
(
    id BIGINT NOT NULL DEFAULT nextval('ur_publictransitdatat_seq'::regclass) NOT NULL,
    cityobjectgroup_datatype_id BIGINT,
    objectclass_id INTEGER,
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_publictransportationfa 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_publictransportationfa
(
    id BIGINT NOT NULL,
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
    id BIGINT NOT NULL,
    capacity INTEGER,
    numberofusers INTEGER,
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_route 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_route
(
    id BIGINT NOT NULL,
    agency_id BIGINT,
    color VARCHAR(1000),
    continuousdropoff VARCHAR(1000),
    continuousdropoff_codespace VARCHAR(1000),
    continuouspickup VARCHAR(1000),
    continuouspickup_codespace VARCHAR(1000),
    description_id BIGINT,
    destinationstop VARCHAR(1000),
    lod0multicurve geometry(GEOMETRYZ),
    longname VARCHAR(1000),
    originstop VARCHAR(1000),
    parentroute_id BIGINT,
    routesortorder INTEGER,
    shortname VARCHAR(1000),
    textcolor VARCHAR(1000),
    type VARCHAR(1000),
    type_codespace VARCHAR(1000),
    updatedate TIMESTAMP WITH TIME ZONE,
    url VARCHAR(1000),
    viastop VARCHAR(1000),
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_statisticalgrid 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_statisticalgrid
(
    id BIGINT NOT NULL,
    areaclassification_codespace VARCHAR(1000),
    areaclassificationtype VARCHAR(1000),
    availability NUMERIC,
    city VARCHAR(1000),
    city_codespace VARCHAR(1000),
    class VARCHAR(1000),
    class_codespace VARCHAR(1000),
    lod_1multisurface_id BIGINT,
    lod_2multisurface_id BIGINT,
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
-- ur_stop 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_stop
(
    id BIGINT NOT NULL,
    code VARCHAR(1000),
    code_codespace VARCHAR(1000),
    latitude NUMERIC,
    level_id BIGINT,
    locationtype VARCHAR(1000),
    locationtype_codespace VARCHAR(1000),
    longitude NUMERIC,
    parentstation_id BIGINT,
    platformcode VARCHAR(1000),
    point geometry(GEOMETRYZ),
    timezone VARCHAR(1000),
    timezone_codespace VARCHAR(1000),
    ttsname VARCHAR(1000),
    url VARCHAR(1000),
    wheelchairboarding VARCHAR(1000),
    wheelchairboarding_codespace VARCHAR(1000),
    zoneid VARCHAR(1000),
    zoneid_codespace VARCHAR(1000),
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_stoptime 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_stoptime
(
    id BIGINT NOT NULL,
    arrivaltime TIMESTAMP WITH TIME ZONE,
    continuousdropofft_codespace VARCHAR(1000),
    continuousdropofftype VARCHAR(1000),
    continuouspickupty_codespace VARCHAR(1000),
    continuouspickuptype VARCHAR(1000),
    departuretime TIMESTAMP WITH TIME ZONE,
    dropofftype VARCHAR(1000),
    dropofftype_codespace VARCHAR(1000),
    headsign VARCHAR(1000),
    pickuptype VARCHAR(1000),
    pickuptype_codespace VARCHAR(1000),
    shapedistancetraveled NUMERIC,
    stop_id BIGINT,
    stopsequence INTEGER,
    timepoint VARCHAR(1000),
    timepoint_codespace VARCHAR(1000),
    trip_id BIGINT,
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_trafficvolume 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_trafficvolume
(
    id BIGINT NOT NULL DEFAULT nextval('ur_trafficvolume_seq'::regclass) NOT NULL,
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
-- ur_transfer 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_transfer
(
    id BIGINT NOT NULL,
    from_id BIGINT,
    mintransfertime INTEGER,
    to_id BIGINT,
    transfertype VARCHAR(1000),
    transfertype_codespace VARCHAR(1000),
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_translation 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_translation
(
    id BIGINT NOT NULL,
    fieldname VARCHAR(1000),
    fieldvalue VARCHAR(1000),
    language VARCHAR(1000),
    language_codespace VARCHAR(1000),
    recordid_id BIGINT,
    recordsubid VARCHAR(1000),
    tablename VARCHAR(1000),
    tablename_codespace VARCHAR(1000),
    translation VARCHAR(1000),
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_translationjp 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_translationjp
(
    id BIGINT NOT NULL,
    language VARCHAR(1000),
    language_codespace VARCHAR(1000),
    translation VARCHAR(1000),
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_transportation_complex 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_transportation_complex
(
    id BIGINT NOT NULL,
    trafficvolume_id BIGINT,
    width NUMERIC,
    width_uom VARCHAR(1000),
    widthtype VARCHAR(1000),
    widthtype_codespace VARCHAR(1000),
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_trip 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_trip
(
    id BIGINT NOT NULL,
    bikeallowed VARCHAR(1000),
    bikeallowed_codespace VARCHAR(1000),
    blockid VARCHAR(1000),
    calendar_id BIGINT,
    calendardate_id BIGINT,
    directionid VARCHAR(1000),
    directionid_codespace VARCHAR(1000),
    headsign VARCHAR(1000),
    lod0multicurve geometry(GEOMETRYZ),
    office_id BIGINT,
    route_id BIGINT,
    shape_id BIGINT,
    shortname VARCHAR(1000),
    symbol VARCHAR(1000),
    wheelchairaccessib_codespace VARCHAR(1000),
    wheelchairaccessible VARCHAR(1000),
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_urbanfunc_to_cityobjec 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_urbanfunc_to_cityobjec
(
    cityobject_id BIGINT NOT NULL,
    urbanfunction_id BIGINT NOT NULL,
    PRIMARY KEY (cityobject_id, urbanfunction_id)
);

-- -------------------------------------------------------------------- 
-- ur_urbanfunction 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_urbanfunction
(
    id BIGINT NOT NULL,
    abstract VARCHAR(1000),
    areaclassification_codespace VARCHAR(1000),
    areaclassificationtype VARCHAR(1000),
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
    legalgrounds_id BIGINT,
    lod0multicurve geometry(GEOMETRYZ),
    lod0multipoint geometry(GEOMETRYZ),
    lod0multisurface_id BIGINT,
    lod_1multicurve geometry(GEOMETRYZ),
    lod_1multipoint geometry(GEOMETRYZ),
    lod_1multisurface_id BIGINT,
    lod_2multicurve geometry(GEOMETRYZ),
    lod_2multipoint geometry(GEOMETRYZ),
    lod_2multisurface_id BIGINT,
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
    id BIGINT NOT NULL,
    period VARCHAR(1000),
    resources VARCHAR(1000),
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_zone 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_zone
(
    id BIGINT NOT NULL,
    areaapplied VARCHAR(1000),
    finalpublicationdate TIMESTAMP WITH TIME ZONE,
    objectclass_id INTEGER,
    PRIMARY KEY (id)
);

-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 
-- *********************************** Create foreign keys ******************************** 
-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 
-- -------------------------------------------------------------------- 
-- ur_agency 
-- -------------------------------------------------------------------- 
ALTER TABLE ur_agency ADD CONSTRAINT ur_agency_fk FOREIGN KEY (id)
REFERENCES ur_publictransit (id);

-- -------------------------------------------------------------------- 
-- ur_areaofannualdiversions 
-- -------------------------------------------------------------------- 
ALTER TABLE ur_areaofannualdiversions ADD CONSTRAINT ur_areaof_landus_areaof_fk FOREIGN KEY (landusediver_areaofannual_id)
REFERENCES ur_landusediversion (id);

-- -------------------------------------------------------------------- 
-- ur_attribution 
-- -------------------------------------------------------------------- 
ALTER TABLE ur_attribution ADD CONSTRAINT ur_attribution_fk FOREIGN KEY (id)
REFERENCES ur_publictransit (id);

ALTER TABLE ur_attribution ADD CONSTRAINT ur_attribution_route_fk FOREIGN KEY (route_id)
REFERENCES ur_route (id)
ON DELETE SET NULL;

ALTER TABLE ur_attribution ADD CONSTRAINT ur_attribution_agency_fk FOREIGN KEY (agency_id)
REFERENCES ur_agency (id)
ON DELETE SET NULL;

ALTER TABLE ur_attribution ADD CONSTRAINT ur_attribution_trip_fk FOREIGN KEY (trip_id)
REFERENCES ur_trip (id)
ON DELETE SET NULL;

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
-- ur_calendar 
-- -------------------------------------------------------------------- 
ALTER TABLE ur_calendar ADD CONSTRAINT ur_calendar_fk FOREIGN KEY (id)
REFERENCES ur_publictransit (id);

-- -------------------------------------------------------------------- 
-- ur_calendardate 
-- -------------------------------------------------------------------- 
ALTER TABLE ur_calendardate ADD CONSTRAINT ur_calendardate_fk FOREIGN KEY (id)
REFERENCES ur_publictransit (id);

ALTER TABLE ur_calendardate ADD CONSTRAINT ur_calendardat_calendar_fk FOREIGN KEY (calendar_id)
REFERENCES ur_calendar (id)
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
-- ur_cityobjectgroup_1 
-- -------------------------------------------------------------------- 
ALTER TABLE ur_cityobjectgroup_1 ADD CONSTRAINT ur_cityobjectgroup_fk_1 FOREIGN KEY (id)
REFERENCES cityobjectgroup (id);

-- -------------------------------------------------------------------- 
-- ur_cityobjectgroup_2 
-- -------------------------------------------------------------------- 
ALTER TABLE ur_cityobjectgroup_2 ADD CONSTRAINT ur_cityobjectgroup_fk_2 FOREIGN KEY (id)
REFERENCES cityobjectgroup (id);

-- -------------------------------------------------------------------- 
-- ur_cityobjectgroup_3 
-- -------------------------------------------------------------------- 
ALTER TABLE ur_cityobjectgroup_3 ADD CONSTRAINT ur_cityobjectgroup_fk_3 FOREIGN KEY (id)
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
-- ur_fareattribute 
-- -------------------------------------------------------------------- 
ALTER TABLE ur_fareattribute ADD CONSTRAINT ur_fareattribute_fk FOREIGN KEY (id)
REFERENCES ur_publictransit (id);

ALTER TABLE ur_fareattribute ADD CONSTRAINT ur_fareattribute_agency_fk FOREIGN KEY (agency_id)
REFERENCES ur_agency (id)
ON DELETE SET NULL;

-- -------------------------------------------------------------------- 
-- ur_farerule 
-- -------------------------------------------------------------------- 
ALTER TABLE ur_farerule ADD CONSTRAINT ur_farerule_fk FOREIGN KEY (id)
REFERENCES ur_publictransitdatatype (id);

ALTER TABLE ur_farerule ADD CONSTRAINT ur_farerule_route_fk FOREIGN KEY (route_id)
REFERENCES ur_route (id)
ON DELETE SET NULL;

ALTER TABLE ur_farerule ADD CONSTRAINT ur_farerule_fare_fk FOREIGN KEY (fare_id)
REFERENCES ur_fareattribute (id)
ON DELETE SET NULL;

-- -------------------------------------------------------------------- 
-- ur_feedinfo 
-- -------------------------------------------------------------------- 
ALTER TABLE ur_feedinfo ADD CONSTRAINT ur_feedinfo_fk FOREIGN KEY (id)
REFERENCES ur_publictransitdatatype (id);

-- -------------------------------------------------------------------- 
-- ur_frequency 
-- -------------------------------------------------------------------- 
ALTER TABLE ur_frequency ADD CONSTRAINT ur_frequency_fk FOREIGN KEY (id)
REFERENCES ur_publictransitdatatype (id);

ALTER TABLE ur_frequency ADD CONSTRAINT ur_frequency_trip_fk FOREIGN KEY (trip_id)
REFERENCES ur_trip (id)
ON DELETE SET NULL;

-- -------------------------------------------------------------------- 
-- ur_households 
-- -------------------------------------------------------------------- 
ALTER TABLE ur_households ADD CONSTRAINT ur_households_fk FOREIGN KEY (id)
REFERENCES ur_statisticalgrid (id);

-- -------------------------------------------------------------------- 
-- ur_keyvaluepair 
-- -------------------------------------------------------------------- 
ALTER TABLE ur_keyvaluepair ADD CONSTRAINT ur_keyval_statis_generi_fk FOREIGN KEY (statisticalg_genericvalue_id)
REFERENCES ur_statisticalgrid (id);

-- -------------------------------------------------------------------- 
-- ur_keyvaluepair_1 
-- -------------------------------------------------------------------- 
ALTER TABLE ur_keyvaluepair_1 ADD CONSTRAINT ur_keyval_buildi_extend_fk FOREIGN KEY (building_extendedattribut_id)
REFERENCES ur_building (id);

-- -------------------------------------------------------------------- 
-- ur_land_use 
-- -------------------------------------------------------------------- 
ALTER TABLE ur_land_use ADD CONSTRAINT ur_land_use_fk FOREIGN KEY (id)
REFERENCES land_use (id);

-- -------------------------------------------------------------------- 
-- ur_landprice 
-- -------------------------------------------------------------------- 
ALTER TABLE ur_landprice ADD CONSTRAINT ur_landprice_fk FOREIGN KEY (id)
REFERENCES ur_statisticalgrid (id);

-- -------------------------------------------------------------------- 
-- ur_landpriceperlanduse 
-- -------------------------------------------------------------------- 
ALTER TABLE ur_landpriceperlanduse ADD CONSTRAINT ur_landpr_landpr_landpr_fk FOREIGN KEY (landprice_landprice_id)
REFERENCES ur_landprice (id);

-- -------------------------------------------------------------------- 
-- ur_landusediversion 
-- -------------------------------------------------------------------- 
ALTER TABLE ur_landusediversion ADD CONSTRAINT ur_landusediversion_fk FOREIGN KEY (id)
REFERENCES ur_statisticalgrid (id);

-- -------------------------------------------------------------------- 
-- ur_numberofannualdiversio 
-- -------------------------------------------------------------------- 
ALTER TABLE ur_numberofannualdiversio ADD CONSTRAINT ur_number_landus_number_fk FOREIGN KEY (landusediver_numberofannu_id)
REFERENCES ur_landusediversion (id);

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
-- ur_office 
-- -------------------------------------------------------------------- 
ALTER TABLE ur_office ADD CONSTRAINT ur_office_fk FOREIGN KEY (id)
REFERENCES ur_publictransit (id);

-- -------------------------------------------------------------------- 
-- ur_officesandemployees 
-- -------------------------------------------------------------------- 
ALTER TABLE ur_officesandemployees ADD CONSTRAINT ur_officesandemployees_fk FOREIGN KEY (id)
REFERENCES ur_statisticalgrid (id);

-- -------------------------------------------------------------------- 
-- ur_pathway 
-- -------------------------------------------------------------------- 
ALTER TABLE ur_pathway ADD CONSTRAINT ur_pathway_fk FOREIGN KEY (id)
REFERENCES ur_publictransit (id);

ALTER TABLE ur_pathway ADD CONSTRAINT ur_pathway_from_fk FOREIGN KEY (from_id)
REFERENCES ur_stop (id)
ON DELETE SET NULL;

ALTER TABLE ur_pathway ADD CONSTRAINT ur_pathway_to_fk FOREIGN KEY (to_id)
REFERENCES ur_stop (id)
ON DELETE SET NULL;

-- -------------------------------------------------------------------- 
-- ur_point 
-- -------------------------------------------------------------------- 
ALTER TABLE ur_point ADD CONSTRAINT ur_point_publictr_point_fk FOREIGN KEY (publictransit_point_id)
REFERENCES ur_publictransit (id);

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
ALTER TABLE ur_publictransit ADD CONSTRAINT ur_publictran_objectcla_fk FOREIGN KEY (objectclass_id)
REFERENCES objectclass (id);

ALTER TABLE ur_publictransit ADD CONSTRAINT ur_publictransit_fk FOREIGN KEY (id)
REFERENCES cityobject (id);

ALTER TABLE ur_publictransit ADD CONSTRAINT ur_publictransit_target_fk FOREIGN KEY (target_id)
REFERENCES cityobject (id)
ON DELETE SET NULL;

-- -------------------------------------------------------------------- 
-- ur_publictransitdatatype 
-- -------------------------------------------------------------------- 
ALTER TABLE ur_publictransitdatatype ADD CONSTRAINT ur_publictra_objectcl_fk_1 FOREIGN KEY (objectclass_id)
REFERENCES objectclass (id);

ALTER TABLE ur_publictransitdatatype ADD CONSTRAINT ur_public_cityob_dataty_fk FOREIGN KEY (cityobjectgroup_datatype_id)
REFERENCES ur_cityobjectgroup_3 (id);

-- -------------------------------------------------------------------- 
-- ur_publictransportationfa 
-- -------------------------------------------------------------------- 
ALTER TABLE ur_publictransportationfa ADD CONSTRAINT ur_publictransportation_fk FOREIGN KEY (id)
REFERENCES ur_urbanfunction (id);

-- -------------------------------------------------------------------- 
-- ur_recreations 
-- -------------------------------------------------------------------- 
ALTER TABLE ur_recreations ADD CONSTRAINT ur_recreations_fk FOREIGN KEY (id)
REFERENCES ur_urbanfunction (id);

-- -------------------------------------------------------------------- 
-- ur_route 
-- -------------------------------------------------------------------- 
ALTER TABLE ur_route ADD CONSTRAINT ur_route_fk FOREIGN KEY (id)
REFERENCES ur_publictransit (id);

ALTER TABLE ur_route ADD CONSTRAINT ur_route_parentroute_fk FOREIGN KEY (parentroute_id)
REFERENCES ur_route (id)
ON DELETE SET NULL;

ALTER TABLE ur_route ADD CONSTRAINT ur_route_description_fk FOREIGN KEY (description_id)
REFERENCES ur_description (id)
ON DELETE SET NULL;

ALTER TABLE ur_route ADD CONSTRAINT ur_route_agency_fk FOREIGN KEY (agency_id)
REFERENCES ur_agency (id)
ON DELETE SET NULL;

-- -------------------------------------------------------------------- 
-- ur_statisticalgrid 
-- -------------------------------------------------------------------- 
ALTER TABLE ur_statisticalgrid ADD CONSTRAINT ur_statistica_objectcla_fk FOREIGN KEY (objectclass_id)
REFERENCES objectclass (id);

ALTER TABLE ur_statisticalgrid ADD CONSTRAINT ur_statisticalgrid_fk FOREIGN KEY (id)
REFERENCES cityobject (id);

ALTER TABLE ur_statisticalgrid ADD CONSTRAINT ur_statisti_lod_1multis_fk FOREIGN KEY (lod_1multisurface_id)
REFERENCES surface_geometry (id);

ALTER TABLE ur_statisticalgrid ADD CONSTRAINT ur_statisti_lod_2multis_fk FOREIGN KEY (lod_2multisurface_id)
REFERENCES surface_geometry (id);

-- -------------------------------------------------------------------- 
-- ur_stop 
-- -------------------------------------------------------------------- 
ALTER TABLE ur_stop ADD CONSTRAINT ur_stop_fk FOREIGN KEY (id)
REFERENCES ur_publictransit (id);

ALTER TABLE ur_stop ADD CONSTRAINT ur_stop_parentstation_fk FOREIGN KEY (parentstation_id)
REFERENCES ur_stop (id)
ON DELETE SET NULL;

ALTER TABLE ur_stop ADD CONSTRAINT ur_stop_level_fk FOREIGN KEY (level_id)
REFERENCES ur_publictransit (id)
ON DELETE SET NULL;

-- -------------------------------------------------------------------- 
-- ur_stoptime 
-- -------------------------------------------------------------------- 
ALTER TABLE ur_stoptime ADD CONSTRAINT ur_stoptime_fk FOREIGN KEY (id)
REFERENCES ur_publictransitdatatype (id);

ALTER TABLE ur_stoptime ADD CONSTRAINT ur_stoptime_stop_fk FOREIGN KEY (stop_id)
REFERENCES ur_stop (id)
ON DELETE SET NULL;

ALTER TABLE ur_stoptime ADD CONSTRAINT ur_stoptime_trip_fk FOREIGN KEY (trip_id)
REFERENCES ur_trip (id)
ON DELETE SET NULL;

-- -------------------------------------------------------------------- 
-- ur_transfer 
-- -------------------------------------------------------------------- 
ALTER TABLE ur_transfer ADD CONSTRAINT ur_transfer_fk FOREIGN KEY (id)
REFERENCES ur_publictransitdatatype (id);

ALTER TABLE ur_transfer ADD CONSTRAINT ur_transfer_from_fk FOREIGN KEY (from_id)
REFERENCES ur_stop (id)
ON DELETE SET NULL;

ALTER TABLE ur_transfer ADD CONSTRAINT ur_transfer_to_fk FOREIGN KEY (to_id)
REFERENCES ur_stop (id)
ON DELETE SET NULL;

-- -------------------------------------------------------------------- 
-- ur_translation 
-- -------------------------------------------------------------------- 
ALTER TABLE ur_translation ADD CONSTRAINT ur_translation_fk FOREIGN KEY (id)
REFERENCES ur_publictransitdatatype (id);

ALTER TABLE ur_translation ADD CONSTRAINT ur_translation_recordid_fk FOREIGN KEY (recordid_id)
REFERENCES ur_publictransit (id)
ON DELETE SET NULL;

-- -------------------------------------------------------------------- 
-- ur_translationjp 
-- -------------------------------------------------------------------- 
ALTER TABLE ur_translationjp ADD CONSTRAINT ur_translationjp_fk FOREIGN KEY (id)
REFERENCES ur_publictransit (id);

-- -------------------------------------------------------------------- 
-- ur_transportation_complex 
-- -------------------------------------------------------------------- 
ALTER TABLE ur_transportation_complex ADD CONSTRAINT ur_transportati_complex_fk FOREIGN KEY (id)
REFERENCES transportation_complex (id);

ALTER TABLE ur_transportation_complex ADD CONSTRAINT ur_transp_comple_traffi_fk FOREIGN KEY (trafficvolume_id)
REFERENCES ur_trafficvolume (id)
ON DELETE SET NULL;

-- -------------------------------------------------------------------- 
-- ur_trip 
-- -------------------------------------------------------------------- 
ALTER TABLE ur_trip ADD CONSTRAINT ur_trip_fk FOREIGN KEY (id)
REFERENCES ur_publictransit (id);

ALTER TABLE ur_trip ADD CONSTRAINT ur_trip_shape_fk FOREIGN KEY (shape_id)
REFERENCES ur_publictransit (id)
ON DELETE SET NULL;

ALTER TABLE ur_trip ADD CONSTRAINT ur_trip_route_fk FOREIGN KEY (route_id)
REFERENCES ur_route (id)
ON DELETE SET NULL;

ALTER TABLE ur_trip ADD CONSTRAINT ur_trip_calendar_fk FOREIGN KEY (calendar_id)
REFERENCES ur_calendar (id)
ON DELETE SET NULL;

ALTER TABLE ur_trip ADD CONSTRAINT ur_trip_calendardate_fk FOREIGN KEY (calendardate_id)
REFERENCES ur_calendardate (id)
ON DELETE SET NULL;

ALTER TABLE ur_trip ADD CONSTRAINT ur_trip_office_fk FOREIGN KEY (office_id)
REFERENCES ur_office (id)
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

ALTER TABLE ur_urbanfunction ADD CONSTRAINT ur_urbanfunct_lod0multi_fk FOREIGN KEY (lod0multisurface_id)
REFERENCES surface_geometry (id);

ALTER TABLE ur_urbanfunction ADD CONSTRAINT ur_urbanfun_lod_1multis_fk FOREIGN KEY (lod_1multisurface_id)
REFERENCES surface_geometry (id);

ALTER TABLE ur_urbanfunction ADD CONSTRAINT ur_urbanfun_lod_2multis_fk FOREIGN KEY (lod_2multisurface_id)
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
-- ur_areaofannualdiversions 
-- -------------------------------------------------------------------- 
CREATE INDEX ur_areaof_landus_areao_fkx ON ur_areaofannualdiversions
    USING btree
    (
      landusediver_areaofannual_id ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

-- -------------------------------------------------------------------- 
-- ur_attribution 
-- -------------------------------------------------------------------- 
CREATE INDEX ur_attribution_agency_fkx ON ur_attribution
    USING btree
    (
      agency_id ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

CREATE INDEX ur_attribution_route_fkx ON ur_attribution
    USING btree
    (
      route_id ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

CREATE INDEX ur_attribution_trip_fkx ON ur_attribution
    USING btree
    (
      trip_id ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

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
-- ur_calendardate 
-- -------------------------------------------------------------------- 
CREATE INDEX ur_calendarda_calendar_fkx ON ur_calendardate
    USING btree
    (
      calendar_id ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

-- -------------------------------------------------------------------- 
-- ur_fareattribute 
-- -------------------------------------------------------------------- 
CREATE INDEX ur_fareattribut_agency_fkx ON ur_fareattribute
    USING btree
    (
      agency_id ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

-- -------------------------------------------------------------------- 
-- ur_farerule 
-- -------------------------------------------------------------------- 
CREATE INDEX ur_farerule_fare_fkx ON ur_farerule
    USING btree
    (
      fare_id ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

CREATE INDEX ur_farerule_route_fkx ON ur_farerule
    USING btree
    (
      route_id ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

-- -------------------------------------------------------------------- 
-- ur_frequency 
-- -------------------------------------------------------------------- 
CREATE INDEX ur_frequency_trip_fkx ON ur_frequency
    USING btree
    (
      trip_id ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

-- -------------------------------------------------------------------- 
-- ur_keyvaluepair 
-- -------------------------------------------------------------------- 
CREATE INDEX ur_keyval_statis_gener_fkx ON ur_keyvaluepair
    USING btree
    (
      statisticalg_genericvalue_id ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

-- -------------------------------------------------------------------- 
-- ur_keyvaluepair_1 
-- -------------------------------------------------------------------- 
CREATE INDEX ur_keyval_buildi_exten_fkx ON ur_keyvaluepair_1
    USING btree
    (
      building_extendedattribut_id ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

-- -------------------------------------------------------------------- 
-- ur_landpriceperlanduse 
-- -------------------------------------------------------------------- 
CREATE INDEX ur_landpr_landpr_landp_fkx ON ur_landpriceperlanduse
    USING btree
    (
      landprice_landprice_id ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

-- -------------------------------------------------------------------- 
-- ur_numberofannualdiversio 
-- -------------------------------------------------------------------- 
CREATE INDEX ur_number_landus_numbe_fkx ON ur_numberofannualdiversio
    USING btree
    (
      landusediver_numberofannu_id ASC NULLS LAST
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
-- ur_pathway 
-- -------------------------------------------------------------------- 
CREATE INDEX ur_pathway_from_fkx ON ur_pathway
    USING btree
    (
      from_id ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

CREATE INDEX ur_pathway_to_fkx ON ur_pathway
    USING btree
    (
      to_id ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

-- -------------------------------------------------------------------- 
-- ur_point 
-- -------------------------------------------------------------------- 
CREATE INDEX ur_point_point_spx ON ur_point
    USING gist
    (
      point
    );

CREATE INDEX ur_point_publict_point_fkx ON ur_point
    USING btree
    (
      publictransit_point_id ASC NULLS LAST
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
-- ur_publictransit 
-- -------------------------------------------------------------------- 
CREATE INDEX ur_publictra_objectcla_fkx ON ur_publictransit
    USING btree
    (
      objectclass_id ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

CREATE INDEX ur_publictransi_target_fkx ON ur_publictransit
    USING btree
    (
      target_id ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

-- -------------------------------------------------------------------- 
-- ur_publictransitdatatype 
-- -------------------------------------------------------------------- 
CREATE INDEX ur_public_cityob_datat_fkx ON ur_publictransitdatatype
    USING btree
    (
      cityobjectgroup_datatype_id ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

CREATE INDEX ur_publictr_objectcl_fkx_1 ON ur_publictransitdatatype
    USING btree
    (
      objectclass_id ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

-- -------------------------------------------------------------------- 
-- ur_route 
-- -------------------------------------------------------------------- 
CREATE INDEX ur_route_agency_fkx ON ur_route
    USING btree
    (
      agency_id ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

CREATE INDEX ur_route_description_fkx ON ur_route
    USING btree
    (
      description_id ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

CREATE INDEX ur_route_lod0multicurv_spx ON ur_route
    USING gist
    (
      lod0multicurve
    );

CREATE INDEX ur_route_parentroute_fkx ON ur_route
    USING btree
    (
      parentroute_id ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

-- -------------------------------------------------------------------- 
-- ur_statisticalgrid 
-- -------------------------------------------------------------------- 
CREATE INDEX ur_statist_lod_1multis_fkx ON ur_statisticalgrid
    USING btree
    (
      lod_1multisurface_id ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

CREATE INDEX ur_statist_lod_2multis_fkx ON ur_statisticalgrid
    USING btree
    (
      lod_2multisurface_id ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

CREATE INDEX ur_statistic_objectcla_fkx ON ur_statisticalgrid
    USING btree
    (
      objectclass_id ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

-- -------------------------------------------------------------------- 
-- ur_stop 
-- -------------------------------------------------------------------- 
CREATE INDEX ur_stop_level_fkx ON ur_stop
    USING btree
    (
      level_id ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

CREATE INDEX ur_stop_parentstation_fkx ON ur_stop
    USING btree
    (
      parentstation_id ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

CREATE INDEX ur_stop_point_spx ON ur_stop
    USING gist
    (
      point
    );

-- -------------------------------------------------------------------- 
-- ur_stoptime 
-- -------------------------------------------------------------------- 
CREATE INDEX ur_stoptime_stop_fkx ON ur_stoptime
    USING btree
    (
      stop_id ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

CREATE INDEX ur_stoptime_trip_fkx ON ur_stoptime
    USING btree
    (
      trip_id ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

-- -------------------------------------------------------------------- 
-- ur_transfer 
-- -------------------------------------------------------------------- 
CREATE INDEX ur_transfer_from_fkx ON ur_transfer
    USING btree
    (
      from_id ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

CREATE INDEX ur_transfer_to_fkx ON ur_transfer
    USING btree
    (
      to_id ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

-- -------------------------------------------------------------------- 
-- ur_translation 
-- -------------------------------------------------------------------- 
CREATE INDEX ur_translatio_recordid_fkx ON ur_translation
    USING btree
    (
      recordid_id ASC NULLS LAST
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
-- ur_trip 
-- -------------------------------------------------------------------- 
CREATE INDEX ur_trip_calendar_fkx ON ur_trip
    USING btree
    (
      calendar_id ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

CREATE INDEX ur_trip_calendardate_fkx ON ur_trip
    USING btree
    (
      calendardate_id ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

CREATE INDEX ur_trip_lod0multicurve_spx ON ur_trip
    USING gist
    (
      lod0multicurve
    );

CREATE INDEX ur_trip_office_fkx ON ur_trip
    USING btree
    (
      office_id ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

CREATE INDEX ur_trip_route_fkx ON ur_trip
    USING btree
    (
      route_id ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

CREATE INDEX ur_trip_shape_fkx ON ur_trip
    USING btree
    (
      shape_id ASC NULLS LAST
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
CREATE INDEX ur_urbanfunc_legalgrou_fkx ON ur_urbanfunction
    USING btree
    (
      legalgrounds_id ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

CREATE INDEX ur_urbanfunc_lod0multi_spx ON ur_urbanfunction
    USING gist
    (
      lod0multicurve
    );

CREATE INDEX ur_urbanfun_lod0mult_spx_1 ON ur_urbanfunction
    USING gist
    (
      lod0multipoint
    );

CREATE INDEX ur_urbanfunc_lod0multi_fkx ON ur_urbanfunction
    USING btree
    (
      lod0multisurface_id ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

CREATE INDEX ur_urbanfu_lod_1multic_spx ON ur_urbanfunction
    USING gist
    (
      lod_1multicurve
    );

CREATE INDEX ur_urbanfu_lod_1multip_spx ON ur_urbanfunction
    USING gist
    (
      lod_1multipoint
    );

CREATE INDEX ur_urbanfu_lod_1multis_fkx ON ur_urbanfunction
    USING btree
    (
      lod_1multisurface_id ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

CREATE INDEX ur_urbanfu_lod_2multic_spx ON ur_urbanfunction
    USING gist
    (
      lod_2multicurve
    );

CREATE INDEX ur_urbanfu_lod_2multip_spx ON ur_urbanfunction
    USING gist
    (
      lod_2multipoint
    );

CREATE INDEX ur_urbanfu_lod_2multis_fkx ON ur_urbanfunction
    USING btree
    (
      lod_2multisurface_id ASC NULLS LAST
    )   WITH (FILLFACTOR = 90);

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

