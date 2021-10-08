-- This document was automatically created by the ADE-Manager tool of 3DCityDB (https://www.3dcitydb.org) on 2021-10-08 13:34:39 
-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 
-- *********************************** Create tables ************************************** 
-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 
-- -------------------------------------------------------------------- 
-- ur_agency 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_agency
(
    id NUMBER(38) NOT NULL,
    address VARCHAR2(1000),
    email VARCHAR2(1000),
    fareurl VARCHAR2(1000),
    language VARCHAR2(1000),
    language_codespace VARCHAR2(1000),
    name VARCHAR2(1000),
    officialname VARCHAR2(1000),
    phone VARCHAR2(1000),
    presidentname VARCHAR2(1000),
    presidentposition VARCHAR2(1000),
    timezone VARCHAR2(1000),
    timezone_codespace VARCHAR2(1000),
    url VARCHAR2(1000),
    zipnumber VARCHAR2(1000),
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_areaofannualdiversions 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_areaofannualdiversions
(
    id NUMBER(38) NOT NULL,
    area NUMBER,
    area_uom VARCHAR2(1000),
    landusediver_areaofannual_id NUMBER(38),
    year DATE,
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_attribution 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_attribution
(
    id NUMBER(38) NOT NULL,
    agency_id NUMBER(38),
    email VARCHAR2(1000),
    isauthority NUMBER,
    isoperator NUMBER,
    isproducer NUMBER,
    organizationname VARCHAR2(1000),
    phonenumber VARCHAR2(1000),
    route_id NUMBER(38),
    trip_id NUMBER(38),
    url VARCHAR2(1000),
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_building 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_building
(
    id NUMBER(38) NOT NULL,
    buildingdetails_id NUMBER(38),
    largecustomerfacilities_id NUMBER(38),
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_buildingdetails 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_buildingdetails
(
    id NUMBER(38) NOT NULL,
    areaclassification_codespace VARCHAR2(1000),
    areaclassificationtype VARCHAR2(1000),
    buildingfootprintarea NUMBER,
    buildingfootprintarea_uom VARCHAR2(1000),
    buildingroofedgearea NUMBER,
    buildingroofedgearea_uom VARCHAR2(1000),
    buildingstructuret_codespace VARCHAR2(1000),
    buildingstructuretype VARCHAR2(1000),
    city VARCHAR2(1000),
    city_codespace VARCHAR2(1000),
    developmentarea NUMBER,
    developmentarea_uom VARCHAR2(1000),
    districtsandzonest_codespace VARCHAR2(1000),
    districtsandzonestype VARCHAR2(1000),
    fireproofstructure_codespace VARCHAR2(1000),
    fireproofstructuretype VARCHAR2(1000),
    implementingbody VARCHAR2(1000),
    landuseplantype VARCHAR2(1000),
    landuseplantype_codespace VARCHAR2(1000),
    note VARCHAR2(1000),
    prefecture VARCHAR2(1000),
    prefecture_codespace VARCHAR2(1000),
    reference VARCHAR2(1000),
    serialnumberofbuildingcertif VARCHAR2(1000),
    sitearea NUMBER,
    sitearea_uom VARCHAR2(1000),
    surveyyear DATE,
    totalfloorarea NUMBER,
    totalfloorarea_uom VARCHAR2(1000),
    urbanplantype VARCHAR2(1000),
    urbanplantype_codespace VARCHAR2(1000),
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_calendar 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_calendar
(
    id NUMBER(38) NOT NULL,
    enddate TIMESTAMP,
    friday NUMBER,
    monday NUMBER,
    saturday NUMBER,
    startdate TIMESTAMP,
    sunday NUMBER,
    thursday NUMBER,
    tuesday NUMBER,
    wednesday NUMBER,
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_calendardate 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_calendardate
(
    id NUMBER(38) NOT NULL,
    calendar_id NUMBER(38),
    date_ TIMESTAMP,
    exceptiontype VARCHAR2(1000),
    exceptiontype_codespace VARCHAR2(1000),
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_censusblock 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_censusblock
(
    id NUMBER(38) NOT NULL,
    daytimepopulation INTEGER,
    daytimepopulationdensity NUMBER,
    numberofmainhouseholds INTEGER,
    numberofordinaryhouseholds INTEGER,
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_cityobjectgroup 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_cityobjectgroup
(
    id NUMBER(38) NOT NULL,
    fiscalyearofpublication DATE,
    language VARCHAR2(1000),
    language_codespace VARCHAR2(1000),
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_cityobjectgroup_1 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_cityobjectgroup_1
(
    id NUMBER(38) NOT NULL,
    fiscalyearofpublication DATE,
    language VARCHAR2(1000),
    language_codespace VARCHAR2(1000),
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_cityobjectgroup_2 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_cityobjectgroup_2
(
    id NUMBER(38) NOT NULL,
    fiscalyearofpublication DATE,
    language VARCHAR2(1000),
    language_codespace VARCHAR2(1000),
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_cityobjectgroup_3 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_cityobjectgroup_3
(
    id NUMBER(38) NOT NULL,
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_description 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_description
(
    id NUMBER(38) NOT NULL,
    description VARCHAR2(1000),
    frequencyofservice INTEGER,
    numberofcustomers INTEGER,
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_developmentproject 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_developmentproject
(
    id NUMBER(38) NOT NULL,
    benefitarea NUMBER,
    benefitarea_uom VARCHAR2(1000),
    benefitperiod VARCHAR2(1000),
    completedarea NUMBER,
    completedarea_uom VARCHAR2(1000),
    cost INTEGER,
    dateofdecision TIMESTAMP,
    dateofdesignationfortemporar TIMESTAMP,
    mainpurpose VARCHAR2(1000),
    mainpurpose_codespace VARCHAR2(1000),
    ongoingarea NUMBER,
    ongoingarea_uom VARCHAR2(1000),
    plannedarea NUMBER,
    plannedarea_uom VARCHAR2(1000),
    status VARCHAR2(1000),
    status_codespace VARCHAR2(1000),
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_disasterdamage 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_disasterdamage
(
    id NUMBER(38) NOT NULL,
    damagedarea NUMBER,
    damagedarea_uom VARCHAR2(1000),
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
    id NUMBER(38) NOT NULL,
    agency_id NUMBER(38),
    currencytype VARCHAR2(1000),
    currencytype_codespace VARCHAR2(1000),
    paymentmethod VARCHAR2(1000),
    paymentmethod_codespace VARCHAR2(1000),
    price NUMBER,
    transferduration INTEGER,
    transfers VARCHAR2(1000),
    transfers_codespace VARCHAR2(1000),
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_farerule 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_farerule
(
    id NUMBER(38) NOT NULL,
    containsid VARCHAR2(1000),
    containsid_codespace VARCHAR2(1000),
    destinationid VARCHAR2(1000),
    destinationid_codespace VARCHAR2(1000),
    fare_id NUMBER(38),
    originid VARCHAR2(1000),
    originid_codespace VARCHAR2(1000),
    route_id NUMBER(38),
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_feedinfo 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_feedinfo
(
    id NUMBER(38) NOT NULL,
    contactemail VARCHAR2(1000),
    contacturl VARCHAR2(1000),
    defaultlanguage VARCHAR2(1000),
    defaultlanguage_codespace VARCHAR2(1000),
    detailedinfo VARCHAR2(1000),
    enddate TIMESTAMP,
    language VARCHAR2(1000),
    language_codespace VARCHAR2(1000),
    publishername VARCHAR2(1000),
    publisherurl VARCHAR2(1000),
    startdate TIMESTAMP,
    version VARCHAR2(1000),
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_frequency 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_frequency
(
    id NUMBER(38) NOT NULL,
    endtime TIMESTAMP,
    exacttimes VARCHAR2(1000),
    exacttimes_codespace VARCHAR2(1000),
    headwaysecs INTEGER,
    starttime TIMESTAMP,
    trip_id NUMBER(38),
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_households 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_households
(
    id NUMBER(38) NOT NULL,
    numberofmainhousehold INTEGER,
    numberofordinaryhousehold INTEGER,
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_keyvaluepair 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_keyvaluepair
(
    id NUMBER(38) NOT NULL,
    codevalue VARCHAR2(1000),
    codevalue_codespace VARCHAR2(1000),
    datevalue TIMESTAMP,
    doublevalue NUMBER,
    intvalue INTEGER,
    key VARCHAR2(1000),
    key_codespace VARCHAR2(1000),
    measuredvalue NUMBER,
    measuredvalue_uom VARCHAR2(1000),
    statisticalg_genericvalue_id NUMBER(38),
    stringvalue VARCHAR2(1000),
    urivalue VARCHAR2(1000),
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_keyvaluepair_1 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_keyvaluepair_1
(
    id NUMBER(38) NOT NULL,
    building_extendedattribut_id NUMBER(38),
    codevalue VARCHAR2(1000),
    codevalue_codespace VARCHAR2(1000),
    datevalue TIMESTAMP,
    doublevalue NUMBER,
    intvalue INTEGER,
    key VARCHAR2(1000),
    key_codespace VARCHAR2(1000),
    measuredvalue NUMBER,
    measuredvalue_uom VARCHAR2(1000),
    stringvalue VARCHAR2(1000),
    urivalue VARCHAR2(1000),
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_land_use 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_land_use
(
    id NUMBER(38) NOT NULL,
    areaclassification_codespace VARCHAR2(1000),
    areaclassificationtype VARCHAR2(1000),
    areainha NUMBER,
    areainha_uom VARCHAR2(1000),
    areainsquaremeter NUMBER,
    areainsquaremeter_uom VARCHAR2(1000),
    city VARCHAR2(1000),
    city_codespace VARCHAR2(1000),
    districtsandzonest_codespace VARCHAR2(1000),
    districtsandzonestype VARCHAR2(1000),
    landuseplantype VARCHAR2(1000),
    landuseplantype_codespace VARCHAR2(1000),
    nominalarea NUMBER,
    nominalarea_uom VARCHAR2(1000),
    note VARCHAR2(1000),
    owner VARCHAR2(1000),
    ownertype VARCHAR2(1000),
    ownertype_codespace VARCHAR2(1000),
    prefecture VARCHAR2(1000),
    prefecture_codespace VARCHAR2(1000),
    reference VARCHAR2(1000),
    surveyyear DATE,
    urbanplantype VARCHAR2(1000),
    urbanplantype_codespace VARCHAR2(1000),
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_landprice 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_landprice
(
    id NUMBER(38) NOT NULL,
    currencyunit VARCHAR2(1000),
    currencyunit_codespace VARCHAR2(1000),
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_landpriceperlanduse 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_landpriceperlanduse
(
    id NUMBER(38) NOT NULL,
    landprice INTEGER,
    landprice_landprice_id NUMBER(38),
    landuse VARCHAR2(1000),
    landuse_codespace VARCHAR2(1000),
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_landusediversion 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_landusediversion
(
    id NUMBER(38) NOT NULL,
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_largecustomerfacilitie 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_largecustomerfacilitie
(
    id NUMBER(38) NOT NULL,
    areaclassification_codespace VARCHAR2(1000),
    areaclassificationtype VARCHAR2(1000),
    availability NUMBER,
    capacity INTEGER,
    city VARCHAR2(1000),
    city_codespace VARCHAR2(1000),
    class VARCHAR2(1000),
    class_codespace VARCHAR2(1000),
    districtsandzonest_codespace VARCHAR2(1000),
    districtsandzonestype VARCHAR2(1000),
    inauguraldate TIMESTAMP,
    keytenants VARCHAR2(1000),
    landuseplantype VARCHAR2(1000),
    landuseplantype_codespace VARCHAR2(1000),
    name VARCHAR2(1000),
    note VARCHAR2(1000),
    owner VARCHAR2(1000),
    prefecture VARCHAR2(1000),
    prefecture_codespace VARCHAR2(1000),
    reference VARCHAR2(1000),
    surveyyear DATE,
    totalfloorarea NUMBER,
    totalfloorarea_uom VARCHAR2(1000),
    totalstorefloorarea NUMBER,
    totalstorefloorarea_uom VARCHAR2(1000),
    urbanplantype VARCHAR2(1000),
    urbanplantype_codespace VARCHAR2(1000),
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_legalgrounds 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_legalgrounds
(
    id NUMBER(38) NOT NULL,
    articlesofregulati_codespace VARCHAR2(1000),
    articlesofregulation VARCHAR2(1000),
    date_ TIMESTAMP,
    nameofregulation VARCHAR2(1000),
    nameofregulation_codespace VARCHAR2(1000),
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_numberofannualdiversio 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_numberofannualdiversio
(
    id NUMBER(38) NOT NULL,
    count INTEGER,
    landusediver_numberofannu_id NUMBER(38),
    year DATE,
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_numberofhouseholds 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_numberofhouseholds
(
    id NUMBER(38) NOT NULL,
    class VARCHAR2(1000),
    class_codespace VARCHAR2(1000),
    households_numberofhous_id_1 NUMBER(38),
    households_numberofhouseh_id NUMBER(38),
    number_ INTEGER,
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_numberofhouseholds_1 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_numberofhouseholds_1
(
    id NUMBER(38) NOT NULL,
    censusblock_numberofhou_id_1 NUMBER(38),
    censusblock_numberofhouse_id NUMBER(38),
    class VARCHAR2(1000),
    class_codespace VARCHAR2(1000),
    number_ INTEGER,
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_office 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_office
(
    id NUMBER(38) NOT NULL,
    name VARCHAR2(1000),
    phone VARCHAR2(1000),
    url VARCHAR2(1000),
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_officesandemployees 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_officesandemployees
(
    id NUMBER(38) NOT NULL,
    numberofemployees INTEGER,
    numberofoffices INTEGER,
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_pathway 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_pathway
(
    id NUMBER(38) NOT NULL,
    from_id NUMBER(38),
    isbidirectional VARCHAR2(1000),
    isbidirectional_codespace VARCHAR2(1000),
    length NUMBER,
    length_uom VARCHAR2(1000),
    maxslope NUMBER,
    minwidth NUMBER,
    mode_ VARCHAR2(1000),
    mode_codespace VARCHAR2(1000),
    reversedsignpostedas VARCHAR2(1000),
    signpostedas VARCHAR2(1000),
    staircount INTEGER,
    to_id NUMBER(38),
    traversaltime INTEGER,
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_point 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_point
(
    id NUMBER(38) NOT NULL,
    latitude NUMBER,
    longitude NUMBER,
    point MDSYS.SDO_GEOMETRY,
    pointdistancetraveled NUMBER,
    pointsequence INTEGER,
    publictransit_point_id NUMBER(38),
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_pollution 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_pollution
(
    id NUMBER(38) NOT NULL,
    cause VARCHAR2(1000),
    damagedarea NUMBER,
    damagedarea_uom VARCHAR2(1000),
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_population 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_population
(
    id NUMBER(38) NOT NULL,
    births INTEGER,
    daytimepopulation INTEGER,
    daytimepopulationdensity NUMBER,
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
    id NUMBER(38) NOT NULL,
    ageandsex VARCHAR2(1000),
    ageandsex_codespace VARCHAR2(1000),
    number_ INTEGER,
    population_populationbyag_id NUMBER(38),
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_publictransit 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_publictransit
(
    id NUMBER(38) NOT NULL,
    index_ NUMBER,
    objectclass_id INTEGER,
    orgid VARCHAR2(1000),
    target_id NUMBER(38),
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_publictransitdatatype 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_publictransitdatatype
(
    id NUMBER(38) NOT NULL,
    cityobjectgroup_datatype_id NUMBER(38),
    objectclass_id INTEGER,
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_publictransportationfa 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_publictransportationfa
(
    id NUMBER(38) NOT NULL,
    companyname VARCHAR2(1000),
    frequencyofservice INTEGER,
    numberofcustomers NUMBER,
    routename VARCHAR2(1000),
    sectionname VARCHAR2(1000),
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_recreations 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_recreations
(
    id NUMBER(38) NOT NULL,
    capacity INTEGER,
    numberofusers INTEGER,
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_route 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_route
(
    id NUMBER(38) NOT NULL,
    agency_id NUMBER(38),
    color VARCHAR2(1000),
    continuousdropoff VARCHAR2(1000),
    continuousdropoff_codespace VARCHAR2(1000),
    continuouspickup VARCHAR2(1000),
    continuouspickup_codespace VARCHAR2(1000),
    description_id NUMBER(38),
    destinationstop VARCHAR2(1000),
    lod0multicurve MDSYS.SDO_GEOMETRY,
    longname VARCHAR2(1000),
    originstop VARCHAR2(1000),
    parentroute_id NUMBER(38),
    routesortorder INTEGER,
    shortname VARCHAR2(1000),
    textcolor VARCHAR2(1000),
    type VARCHAR2(1000),
    type_codespace VARCHAR2(1000),
    updatedate TIMESTAMP,
    url VARCHAR2(1000),
    viastop VARCHAR2(1000),
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_statisticalgrid 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_statisticalgrid
(
    id NUMBER(38) NOT NULL,
    areaclassification_codespace VARCHAR2(1000),
    areaclassificationtype VARCHAR2(1000),
    availability NUMBER,
    city VARCHAR2(1000),
    city_codespace VARCHAR2(1000),
    class VARCHAR2(1000),
    class_codespace VARCHAR2(1000),
    lod_1multisurface_id NUMBER(38),
    lod_2multisurface_id NUMBER(38),
    objectclass_id INTEGER,
    prefecture VARCHAR2(1000),
    prefecture_codespace VARCHAR2(1000),
    surveyyear DATE,
    urbanplantype VARCHAR2(1000),
    urbanplantype_codespace VARCHAR2(1000),
    value CLOB,
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_stop 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_stop
(
    id NUMBER(38) NOT NULL,
    code VARCHAR2(1000),
    code_codespace VARCHAR2(1000),
    latitude NUMBER,
    level_id NUMBER(38),
    locationtype VARCHAR2(1000),
    locationtype_codespace VARCHAR2(1000),
    longitude NUMBER,
    parentstation_id NUMBER(38),
    platformcode VARCHAR2(1000),
    point MDSYS.SDO_GEOMETRY,
    timezone VARCHAR2(1000),
    timezone_codespace VARCHAR2(1000),
    ttsname VARCHAR2(1000),
    url VARCHAR2(1000),
    wheelchairboarding VARCHAR2(1000),
    wheelchairboarding_codespace VARCHAR2(1000),
    zoneid VARCHAR2(1000),
    zoneid_codespace VARCHAR2(1000),
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_stoptime 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_stoptime
(
    id NUMBER(38) NOT NULL,
    arrivaltime TIMESTAMP,
    continuousdropofft_codespace VARCHAR2(1000),
    continuousdropofftype VARCHAR2(1000),
    continuouspickupty_codespace VARCHAR2(1000),
    continuouspickuptype VARCHAR2(1000),
    departuretime TIMESTAMP,
    dropofftype VARCHAR2(1000),
    dropofftype_codespace VARCHAR2(1000),
    headsign VARCHAR2(1000),
    pickuptype VARCHAR2(1000),
    pickuptype_codespace VARCHAR2(1000),
    shapedistancetraveled NUMBER,
    stop_id NUMBER(38),
    stopsequence INTEGER,
    timepoint VARCHAR2(1000),
    timepoint_codespace VARCHAR2(1000),
    trip_id NUMBER(38),
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_trafficvolume 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_trafficvolume
(
    id NUMBER(38) NOT NULL,
    areaclassification_codespace VARCHAR2(1000),
    areaclassificationtype VARCHAR2(1000),
    averagetravelspeedincongesti NUMBER,
    city VARCHAR2(1000),
    city_codespace VARCHAR2(1000),
    congestionrate NUMBER,
    largevehiclerate NUMBER,
    note VARCHAR2(1000),
    observationpointname VARCHAR2(1000),
    prefecture VARCHAR2(1000),
    prefecture_codespace VARCHAR2(1000),
    reference VARCHAR2(1000),
    surveyyear DATE,
    urbanplantype VARCHAR2(1000),
    urbanplantype_codespace VARCHAR2(1000),
    weekday12hourtrafficvolume INTEGER,
    weekday24hourtrafficvolume INTEGER,
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_transfer 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_transfer
(
    id NUMBER(38) NOT NULL,
    from_id NUMBER(38),
    mintransfertime INTEGER,
    to_id NUMBER(38),
    transfertype VARCHAR2(1000),
    transfertype_codespace VARCHAR2(1000),
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_translation 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_translation
(
    id NUMBER(38) NOT NULL,
    fieldname VARCHAR2(1000),
    fieldvalue VARCHAR2(1000),
    language VARCHAR2(1000),
    language_codespace VARCHAR2(1000),
    recordid_id NUMBER(38),
    recordsubid VARCHAR2(1000),
    tablename VARCHAR2(1000),
    tablename_codespace VARCHAR2(1000),
    translation VARCHAR2(1000),
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_translationjp 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_translationjp
(
    id NUMBER(38) NOT NULL,
    language VARCHAR2(1000),
    language_codespace VARCHAR2(1000),
    translation VARCHAR2(1000),
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_transportation_complex 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_transportation_complex
(
    id NUMBER(38) NOT NULL,
    trafficvolume_id NUMBER(38),
    width NUMBER,
    width_uom VARCHAR2(1000),
    widthtype VARCHAR2(1000),
    widthtype_codespace VARCHAR2(1000),
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_trip 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_trip
(
    id NUMBER(38) NOT NULL,
    bikeallowed VARCHAR2(1000),
    bikeallowed_codespace VARCHAR2(1000),
    blockid VARCHAR2(1000),
    calendar_id NUMBER(38),
    calendardate_id NUMBER(38),
    directionid VARCHAR2(1000),
    directionid_codespace VARCHAR2(1000),
    headsign VARCHAR2(1000),
    lod0multicurve MDSYS.SDO_GEOMETRY,
    office_id NUMBER(38),
    route_id NUMBER(38),
    shape_id NUMBER(38),
    shortname VARCHAR2(1000),
    symbol VARCHAR2(1000),
    wheelchairaccessib_codespace VARCHAR2(1000),
    wheelchairaccessible VARCHAR2(1000),
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_urbanfunc_to_cityobjec 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_urbanfunc_to_cityobjec
(
    cityobject_id NUMBER(38) NOT NULL,
    urbanfunction_id NUMBER(38) NOT NULL,
    PRIMARY KEY (cityobject_id, urbanfunction_id)
);

-- -------------------------------------------------------------------- 
-- ur_urbanfunction 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_urbanfunction
(
    id NUMBER(38) NOT NULL,
    abstract VARCHAR2(1000),
    areaclassification_codespace VARCHAR2(1000),
    areaclassificationtype VARCHAR2(1000),
    capacity INTEGER,
    city VARCHAR2(1000),
    city_codespace VARCHAR2(1000),
    class VARCHAR2(1000),
    class_codespace VARCHAR2(1000),
    custodian VARCHAR2(1000),
    enactmentdate TIMESTAMP,
    enactmentfiscalyear DATE,
    expirationdate TIMESTAMP,
    expirationfiscalyear DATE,
    function VARCHAR2(1000),
    function_codespace VARCHAR2(1000),
    legalgrounds_id NUMBER(38),
    lod0multicurve MDSYS.SDO_GEOMETRY,
    lod0multipoint MDSYS.SDO_GEOMETRY,
    lod0multisurface_id NUMBER(38),
    lod_1multicurve MDSYS.SDO_GEOMETRY,
    lod_1multipoint MDSYS.SDO_GEOMETRY,
    lod_1multisurface_id NUMBER(38),
    lod_2multicurve MDSYS.SDO_GEOMETRY,
    lod_2multipoint MDSYS.SDO_GEOMETRY,
    lod_2multisurface_id NUMBER(38),
    nominalarea NUMBER,
    nominalarea_uom VARCHAR2(1000),
    note VARCHAR2(1000),
    objectclass_id INTEGER,
    prefecture VARCHAR2(1000),
    prefecture_codespace VARCHAR2(1000),
    reference VARCHAR2(1000),
    surveyyear DATE,
    urbanplantype VARCHAR2(1000),
    urbanplantype_codespace VARCHAR2(1000),
    validity NUMBER,
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_urbanization 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_urbanization
(
    id NUMBER(38) NOT NULL,
    period VARCHAR2(1000),
    resources VARCHAR2(1000),
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------------- 
-- ur_zone 
-- -------------------------------------------------------------------- 
CREATE TABLE ur_zone
(
    id NUMBER(38) NOT NULL,
    areaapplied VARCHAR2(1000),
    finalpublicationdate TIMESTAMP,
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

ALTER TABLE ur_attribution ADD CONSTRAINT ur_attribution_agency_fk FOREIGN KEY (agency_id)
REFERENCES ur_agency (id)
ON DELETE SET NULL;

ALTER TABLE ur_attribution ADD CONSTRAINT ur_attribution_route_fk FOREIGN KEY (route_id)
REFERENCES ur_route (id)
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

ALTER TABLE ur_farerule ADD CONSTRAINT ur_farerule_fare_fk FOREIGN KEY (fare_id)
REFERENCES ur_fareattribute (id)
ON DELETE SET NULL;

ALTER TABLE ur_farerule ADD CONSTRAINT ur_farerule_route_fk FOREIGN KEY (route_id)
REFERENCES ur_route (id)
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

ALTER TABLE ur_stoptime ADD CONSTRAINT ur_stoptime_trip_fk FOREIGN KEY (trip_id)
REFERENCES ur_trip (id)
ON DELETE SET NULL;

ALTER TABLE ur_stoptime ADD CONSTRAINT ur_stoptime_stop_fk FOREIGN KEY (stop_id)
REFERENCES ur_stop (id)
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

ALTER TABLE ur_trip ADD CONSTRAINT ur_trip_shape_fk FOREIGN KEY (shape_id)
REFERENCES ur_publictransit (id)
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

SET SERVEROUTPUT ON
SET FEEDBACK ON
SET VER OFF
VARIABLE SRID NUMBER;
BEGIN
  SELECT SRID INTO :SRID FROM DATABASE_SRS;
END;
/

column mc new_value SRSNO print
select :SRID mc from dual;

prompt Used SRID for spatial indexes: &SRSNO; 

-- -------------------------------------------------------------------- 
-- ur_areaofannualdiversions 
-- -------------------------------------------------------------------- 
CREATE INDEX ur_areaof_landus_areao_fkx ON ur_areaofannualdiversions (landusediver_areaofannual_id);

-- -------------------------------------------------------------------- 
-- ur_attribution 
-- -------------------------------------------------------------------- 
CREATE INDEX ur_attribution_agency_fkx ON ur_attribution (agency_id);

CREATE INDEX ur_attribution_route_fkx ON ur_attribution (route_id);

CREATE INDEX ur_attribution_trip_fkx ON ur_attribution (trip_id);

-- -------------------------------------------------------------------- 
-- ur_building 
-- -------------------------------------------------------------------- 
CREATE INDEX ur_building_buildingde_fkx ON ur_building (buildingdetails_id);

CREATE INDEX ur_building_largecusto_fkx ON ur_building (largecustomerfacilities_id);

-- -------------------------------------------------------------------- 
-- ur_calendardate 
-- -------------------------------------------------------------------- 
CREATE INDEX ur_calendarda_calendar_fkx ON ur_calendardate (calendar_id);

-- -------------------------------------------------------------------- 
-- ur_fareattribute 
-- -------------------------------------------------------------------- 
CREATE INDEX ur_fareattribut_agency_fkx ON ur_fareattribute (agency_id);

-- -------------------------------------------------------------------- 
-- ur_farerule 
-- -------------------------------------------------------------------- 
CREATE INDEX ur_farerule_fare_fkx ON ur_farerule (fare_id);

CREATE INDEX ur_farerule_route_fkx ON ur_farerule (route_id);

-- -------------------------------------------------------------------- 
-- ur_frequency 
-- -------------------------------------------------------------------- 
CREATE INDEX ur_frequency_trip_fkx ON ur_frequency (trip_id);

-- -------------------------------------------------------------------- 
-- ur_keyvaluepair 
-- -------------------------------------------------------------------- 
CREATE INDEX ur_keyval_statis_gener_fkx ON ur_keyvaluepair (statisticalg_genericvalue_id);

-- -------------------------------------------------------------------- 
-- ur_keyvaluepair_1 
-- -------------------------------------------------------------------- 
CREATE INDEX ur_keyval_buildi_exten_fkx ON ur_keyvaluepair_1 (building_extendedattribut_id);

-- -------------------------------------------------------------------- 
-- ur_landpriceperlanduse 
-- -------------------------------------------------------------------- 
CREATE INDEX ur_landpr_landpr_landp_fkx ON ur_landpriceperlanduse (landprice_landprice_id);

-- -------------------------------------------------------------------- 
-- ur_numberofannualdiversio 
-- -------------------------------------------------------------------- 
CREATE INDEX ur_number_landus_numbe_fkx ON ur_numberofannualdiversio (landusediver_numberofannu_id);

-- -------------------------------------------------------------------- 
-- ur_numberofhouseholds 
-- -------------------------------------------------------------------- 
CREATE INDEX ur_numbe_house_numbe_fkx_1 ON ur_numberofhouseholds (households_numberofhous_id_1);

CREATE INDEX ur_number_househ_numbe_fkx ON ur_numberofhouseholds (households_numberofhouseh_id);

-- -------------------------------------------------------------------- 
-- ur_numberofhouseholds_1 
-- -------------------------------------------------------------------- 
CREATE INDEX ur_numbe_censu_numbe_fkx_1 ON ur_numberofhouseholds_1 (censusblock_numberofhou_id_1);

CREATE INDEX ur_number_census_numbe_fkx ON ur_numberofhouseholds_1 (censusblock_numberofhouse_id);

-- -------------------------------------------------------------------- 
-- ur_pathway 
-- -------------------------------------------------------------------- 
CREATE INDEX ur_pathway_from_fkx ON ur_pathway (from_id);

CREATE INDEX ur_pathway_to_fkx ON ur_pathway (to_id);

-- -------------------------------------------------------------------- 
-- ur_point 
-- -------------------------------------------------------------------- 
DELETE FROM USER_SDO_GEOM_METADATA WHERE TABLE_NAME='UR_POINT' AND COLUMN_NAME='POINT';
INSERT INTO USER_SDO_GEOM_METADATA (TABLE_NAME, COLUMN_NAME, DIMINFO, SRID)
VALUES ('UR_POINT','POINT',
MDSYS.SDO_DIM_ARRAY(MDSYS.SDO_DIM_ELEMENT('X', 0.000, 10000000.000, 0.0005), MDSYS.SDO_DIM_ELEMENT('Y', 0.000, 10000000.000, 0.0005),MDSYS.SDO_DIM_ELEMENT('Z', -1000, 10000, 0.0005)), &SRSNO);
CREATE INDEX ur_point_point_spx ON ur_point (point) INDEXTYPE IS MDSYS.SPATIAL_INDEX;

CREATE INDEX ur_point_publict_point_fkx ON ur_point (publictransit_point_id);

-- -------------------------------------------------------------------- 
-- ur_populationbyageandsex 
-- -------------------------------------------------------------------- 
CREATE INDEX ur_popula_popula_popul_fkx ON ur_populationbyageandsex (population_populationbyag_id);

-- -------------------------------------------------------------------- 
-- ur_publictransit 
-- -------------------------------------------------------------------- 
CREATE INDEX ur_publictra_objectcla_fkx ON ur_publictransit (objectclass_id);

CREATE INDEX ur_publictransi_target_fkx ON ur_publictransit (target_id);

-- -------------------------------------------------------------------- 
-- ur_publictransitdatatype 
-- -------------------------------------------------------------------- 
CREATE INDEX ur_public_cityob_datat_fkx ON ur_publictransitdatatype (cityobjectgroup_datatype_id);

CREATE INDEX ur_publictr_objectcl_fkx_1 ON ur_publictransitdatatype (objectclass_id);

-- -------------------------------------------------------------------- 
-- ur_route 
-- -------------------------------------------------------------------- 
CREATE INDEX ur_route_agency_fkx ON ur_route (agency_id);

CREATE INDEX ur_route_description_fkx ON ur_route (description_id);

DELETE FROM USER_SDO_GEOM_METADATA WHERE TABLE_NAME='UR_ROUTE' AND COLUMN_NAME='LOD0MULTICURVE';
INSERT INTO USER_SDO_GEOM_METADATA (TABLE_NAME, COLUMN_NAME, DIMINFO, SRID)
VALUES ('UR_ROUTE','LOD0MULTICURVE',
MDSYS.SDO_DIM_ARRAY(MDSYS.SDO_DIM_ELEMENT('X', 0.000, 10000000.000, 0.0005), MDSYS.SDO_DIM_ELEMENT('Y', 0.000, 10000000.000, 0.0005),MDSYS.SDO_DIM_ELEMENT('Z', -1000, 10000, 0.0005)), &SRSNO);
CREATE INDEX ur_route_lod0multicurv_spx ON ur_route (lod0multicurve) INDEXTYPE IS MDSYS.SPATIAL_INDEX;

CREATE INDEX ur_route_parentroute_fkx ON ur_route (parentroute_id);

-- -------------------------------------------------------------------- 
-- ur_statisticalgrid 
-- -------------------------------------------------------------------- 
CREATE INDEX ur_statist_lod_1multis_fkx ON ur_statisticalgrid (lod_1multisurface_id);

CREATE INDEX ur_statist_lod_2multis_fkx ON ur_statisticalgrid (lod_2multisurface_id);

CREATE INDEX ur_statistic_objectcla_fkx ON ur_statisticalgrid (objectclass_id);

-- -------------------------------------------------------------------- 
-- ur_stop 
-- -------------------------------------------------------------------- 
CREATE INDEX ur_stop_level_fkx ON ur_stop (level_id);

CREATE INDEX ur_stop_parentstation_fkx ON ur_stop (parentstation_id);

DELETE FROM USER_SDO_GEOM_METADATA WHERE TABLE_NAME='UR_STOP' AND COLUMN_NAME='POINT';
INSERT INTO USER_SDO_GEOM_METADATA (TABLE_NAME, COLUMN_NAME, DIMINFO, SRID)
VALUES ('UR_STOP','POINT',
MDSYS.SDO_DIM_ARRAY(MDSYS.SDO_DIM_ELEMENT('X', 0.000, 10000000.000, 0.0005), MDSYS.SDO_DIM_ELEMENT('Y', 0.000, 10000000.000, 0.0005),MDSYS.SDO_DIM_ELEMENT('Z', -1000, 10000, 0.0005)), &SRSNO);
CREATE INDEX ur_stop_point_spx ON ur_stop (point) INDEXTYPE IS MDSYS.SPATIAL_INDEX;

-- -------------------------------------------------------------------- 
-- ur_stoptime 
-- -------------------------------------------------------------------- 
CREATE INDEX ur_stoptime_stop_fkx ON ur_stoptime (stop_id);

CREATE INDEX ur_stoptime_trip_fkx ON ur_stoptime (trip_id);

-- -------------------------------------------------------------------- 
-- ur_transfer 
-- -------------------------------------------------------------------- 
CREATE INDEX ur_transfer_from_fkx ON ur_transfer (from_id);

CREATE INDEX ur_transfer_to_fkx ON ur_transfer (to_id);

-- -------------------------------------------------------------------- 
-- ur_translation 
-- -------------------------------------------------------------------- 
CREATE INDEX ur_translatio_recordid_fkx ON ur_translation (recordid_id);

-- -------------------------------------------------------------------- 
-- ur_transportation_complex 
-- -------------------------------------------------------------------- 
CREATE INDEX ur_transp_comple_traff_fkx ON ur_transportation_complex (trafficvolume_id);

-- -------------------------------------------------------------------- 
-- ur_trip 
-- -------------------------------------------------------------------- 
CREATE INDEX ur_trip_calendar_fkx ON ur_trip (calendar_id);

CREATE INDEX ur_trip_calendardate_fkx ON ur_trip (calendardate_id);

DELETE FROM USER_SDO_GEOM_METADATA WHERE TABLE_NAME='UR_TRIP' AND COLUMN_NAME='LOD0MULTICURVE';
INSERT INTO USER_SDO_GEOM_METADATA (TABLE_NAME, COLUMN_NAME, DIMINFO, SRID)
VALUES ('UR_TRIP','LOD0MULTICURVE',
MDSYS.SDO_DIM_ARRAY(MDSYS.SDO_DIM_ELEMENT('X', 0.000, 10000000.000, 0.0005), MDSYS.SDO_DIM_ELEMENT('Y', 0.000, 10000000.000, 0.0005),MDSYS.SDO_DIM_ELEMENT('Z', -1000, 10000, 0.0005)), &SRSNO);
CREATE INDEX ur_trip_lod0multicurve_spx ON ur_trip (lod0multicurve) INDEXTYPE IS MDSYS.SPATIAL_INDEX;

CREATE INDEX ur_trip_office_fkx ON ur_trip (office_id);

CREATE INDEX ur_trip_route_fkx ON ur_trip (route_id);

CREATE INDEX ur_trip_shape_fkx ON ur_trip (shape_id);

-- -------------------------------------------------------------------- 
-- ur_urbanfunc_to_cityobjec 
-- -------------------------------------------------------------------- 
CREATE INDEX ur_urbanfu_to_cityobj_fk2x ON ur_urbanfunc_to_cityobjec (cityobject_id);

CREATE INDEX ur_urbanfu_to_cityobj_fk1x ON ur_urbanfunc_to_cityobjec (urbanfunction_id);

-- -------------------------------------------------------------------- 
-- ur_urbanfunction 
-- -------------------------------------------------------------------- 
CREATE INDEX ur_urbanfunc_legalgrou_fkx ON ur_urbanfunction (legalgrounds_id);

DELETE FROM USER_SDO_GEOM_METADATA WHERE TABLE_NAME='UR_URBANFUNCTION' AND COLUMN_NAME='LOD0MULTICURVE';
INSERT INTO USER_SDO_GEOM_METADATA (TABLE_NAME, COLUMN_NAME, DIMINFO, SRID)
VALUES ('UR_URBANFUNCTION','LOD0MULTICURVE',
MDSYS.SDO_DIM_ARRAY(MDSYS.SDO_DIM_ELEMENT('X', 0.000, 10000000.000, 0.0005), MDSYS.SDO_DIM_ELEMENT('Y', 0.000, 10000000.000, 0.0005),MDSYS.SDO_DIM_ELEMENT('Z', -1000, 10000, 0.0005)), &SRSNO);
CREATE INDEX ur_urbanfunc_lod0multi_spx ON ur_urbanfunction (lod0multicurve) INDEXTYPE IS MDSYS.SPATIAL_INDEX;

DELETE FROM USER_SDO_GEOM_METADATA WHERE TABLE_NAME='UR_URBANFUNCTION' AND COLUMN_NAME='LOD0MULTIPOINT';
INSERT INTO USER_SDO_GEOM_METADATA (TABLE_NAME, COLUMN_NAME, DIMINFO, SRID)
VALUES ('UR_URBANFUNCTION','LOD0MULTIPOINT',
MDSYS.SDO_DIM_ARRAY(MDSYS.SDO_DIM_ELEMENT('X', 0.000, 10000000.000, 0.0005), MDSYS.SDO_DIM_ELEMENT('Y', 0.000, 10000000.000, 0.0005),MDSYS.SDO_DIM_ELEMENT('Z', -1000, 10000, 0.0005)), &SRSNO);
CREATE INDEX ur_urbanfun_lod0mult_spx_1 ON ur_urbanfunction (lod0multipoint) INDEXTYPE IS MDSYS.SPATIAL_INDEX;

CREATE INDEX ur_urbanfunc_lod0multi_fkx ON ur_urbanfunction (lod0multisurface_id);

DELETE FROM USER_SDO_GEOM_METADATA WHERE TABLE_NAME='UR_URBANFUNCTION' AND COLUMN_NAME='LOD_1MULTICURVE';
INSERT INTO USER_SDO_GEOM_METADATA (TABLE_NAME, COLUMN_NAME, DIMINFO, SRID)
VALUES ('UR_URBANFUNCTION','LOD_1MULTICURVE',
MDSYS.SDO_DIM_ARRAY(MDSYS.SDO_DIM_ELEMENT('X', 0.000, 10000000.000, 0.0005), MDSYS.SDO_DIM_ELEMENT('Y', 0.000, 10000000.000, 0.0005),MDSYS.SDO_DIM_ELEMENT('Z', -1000, 10000, 0.0005)), &SRSNO);
CREATE INDEX ur_urbanfu_lod_1multic_spx ON ur_urbanfunction (lod_1multicurve) INDEXTYPE IS MDSYS.SPATIAL_INDEX;

DELETE FROM USER_SDO_GEOM_METADATA WHERE TABLE_NAME='UR_URBANFUNCTION' AND COLUMN_NAME='LOD_1MULTIPOINT';
INSERT INTO USER_SDO_GEOM_METADATA (TABLE_NAME, COLUMN_NAME, DIMINFO, SRID)
VALUES ('UR_URBANFUNCTION','LOD_1MULTIPOINT',
MDSYS.SDO_DIM_ARRAY(MDSYS.SDO_DIM_ELEMENT('X', 0.000, 10000000.000, 0.0005), MDSYS.SDO_DIM_ELEMENT('Y', 0.000, 10000000.000, 0.0005),MDSYS.SDO_DIM_ELEMENT('Z', -1000, 10000, 0.0005)), &SRSNO);
CREATE INDEX ur_urbanfu_lod_1multip_spx ON ur_urbanfunction (lod_1multipoint) INDEXTYPE IS MDSYS.SPATIAL_INDEX;

CREATE INDEX ur_urbanfu_lod_1multis_fkx ON ur_urbanfunction (lod_1multisurface_id);

DELETE FROM USER_SDO_GEOM_METADATA WHERE TABLE_NAME='UR_URBANFUNCTION' AND COLUMN_NAME='LOD_2MULTICURVE';
INSERT INTO USER_SDO_GEOM_METADATA (TABLE_NAME, COLUMN_NAME, DIMINFO, SRID)
VALUES ('UR_URBANFUNCTION','LOD_2MULTICURVE',
MDSYS.SDO_DIM_ARRAY(MDSYS.SDO_DIM_ELEMENT('X', 0.000, 10000000.000, 0.0005), MDSYS.SDO_DIM_ELEMENT('Y', 0.000, 10000000.000, 0.0005),MDSYS.SDO_DIM_ELEMENT('Z', -1000, 10000, 0.0005)), &SRSNO);
CREATE INDEX ur_urbanfu_lod_2multic_spx ON ur_urbanfunction (lod_2multicurve) INDEXTYPE IS MDSYS.SPATIAL_INDEX;

DELETE FROM USER_SDO_GEOM_METADATA WHERE TABLE_NAME='UR_URBANFUNCTION' AND COLUMN_NAME='LOD_2MULTIPOINT';
INSERT INTO USER_SDO_GEOM_METADATA (TABLE_NAME, COLUMN_NAME, DIMINFO, SRID)
VALUES ('UR_URBANFUNCTION','LOD_2MULTIPOINT',
MDSYS.SDO_DIM_ARRAY(MDSYS.SDO_DIM_ELEMENT('X', 0.000, 10000000.000, 0.0005), MDSYS.SDO_DIM_ELEMENT('Y', 0.000, 10000000.000, 0.0005),MDSYS.SDO_DIM_ELEMENT('Z', -1000, 10000, 0.0005)), &SRSNO);
CREATE INDEX ur_urbanfu_lod_2multip_spx ON ur_urbanfunction (lod_2multipoint) INDEXTYPE IS MDSYS.SPATIAL_INDEX;

CREATE INDEX ur_urbanfu_lod_2multis_fkx ON ur_urbanfunction (lod_2multisurface_id);

CREATE INDEX ur_urbanfunc_objectcla_fkx ON ur_urbanfunction (objectclass_id);

-- -------------------------------------------------------------------- 
-- ur_zone 
-- -------------------------------------------------------------------- 
CREATE INDEX ur_zone_objectclass_fkx ON ur_zone (objectclass_id);

-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 
-- *********************************** Create Sequences *********************************** 
-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 

CREATE SEQUENCE ur_populationbyageand_seq INCREMENT BY 1 START WITH 1 MINVALUE 1 CACHE 10000;

CREATE SEQUENCE ur_landpriceperlandus_seq INCREMENT BY 1 START WITH 1 MINVALUE 1 CACHE 10000;

CREATE SEQUENCE ur_numberofannualdive_seq INCREMENT BY 1 START WITH 1 MINVALUE 1 CACHE 10000;

CREATE SEQUENCE ur_areaofannualdivers_seq INCREMENT BY 1 START WITH 1 MINVALUE 1 CACHE 10000;

CREATE SEQUENCE ur_numberofhouseholds_seq INCREMENT BY 1 START WITH 1 MINVALUE 1 CACHE 10000;

CREATE SEQUENCE ur_keyvaluepair_seq INCREMENT BY 1 START WITH 1 MINVALUE 1 CACHE 10000;

CREATE SEQUENCE ur_buildingdetails_seq INCREMENT BY 1 START WITH 1 MINVALUE 1 CACHE 10000;

CREATE SEQUENCE ur_largecustomerfacil_seq INCREMENT BY 1 START WITH 1 MINVALUE 1 CACHE 10000;

CREATE SEQUENCE ur_keyvaluepair_seq_1 INCREMENT BY 1 START WITH 1 MINVALUE 1 CACHE 10000;

CREATE SEQUENCE ur_trafficvolume_seq INCREMENT BY 1 START WITH 1 MINVALUE 1 CACHE 10000;

CREATE SEQUENCE ur_legalgrounds_seq INCREMENT BY 1 START WITH 1 MINVALUE 1 CACHE 10000;

CREATE SEQUENCE ur_numberofhousehol_seq_1 INCREMENT BY 1 START WITH 1 MINVALUE 1 CACHE 10000;

CREATE SEQUENCE ur_description_seq INCREMENT BY 1 START WITH 1 MINVALUE 1 CACHE 10000;

CREATE SEQUENCE ur_point_seq INCREMENT BY 1 START WITH 1 MINVALUE 1 CACHE 10000;

CREATE SEQUENCE ur_publictransitdatat_seq INCREMENT BY 1 START WITH 1 MINVALUE 1 CACHE 10000;

