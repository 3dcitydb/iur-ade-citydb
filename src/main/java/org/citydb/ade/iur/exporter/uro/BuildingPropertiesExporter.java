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

package org.citydb.ade.iur.exporter.uro;

import org.citydb.ade.exporter.ADEExporter;
import org.citydb.ade.exporter.CityGMLExportHelper;
import org.citydb.ade.iur.exporter.ExportManager;
import org.citydb.ade.iur.schema.ADETable;
import org.citydb.citygml.exporter.CityGMLExportException;
import org.citydb.database.schema.mapping.FeatureType;
import org.citydb.query.filter.projection.CombinedProjectionFilter;
import org.citydb.query.filter.projection.ProjectionFilter;
import org.citydb.sqlbuilder.expression.PlaceHolder;
import org.citydb.sqlbuilder.schema.Table;
import org.citydb.sqlbuilder.select.Select;
import org.citydb.sqlbuilder.select.join.JoinFactory;
import org.citydb.sqlbuilder.select.operator.comparison.ComparisonFactory;
import org.citydb.sqlbuilder.select.operator.comparison.ComparisonName;
import org.citygml4j.model.citygml.building.AbstractBuilding;
import org.citygml4j.model.gml.basicTypes.Code;
import org.citygml4j.model.gml.basicTypes.Measure;
import org.citygml4j.ade.iur.model.module.UrbanObjectModule;
import org.citygml4j.ade.iur.model.uro.BuildingDetails;
import org.citygml4j.ade.iur.model.uro.BuildingDetailsProperty;
import org.citygml4j.ade.iur.model.uro.BuildingDetailsPropertyElement;
import org.citygml4j.ade.iur.model.uro.LargeCustomerFacilities;
import org.citygml4j.ade.iur.model.uro.LargeCustomerFacilitiesProperty;
import org.citygml4j.ade.iur.model.uro.LargeCustomerFacilitiesPropertyElement;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Year;

public class BuildingPropertiesExporter implements ADEExporter {
    private final PreparedStatement ps;
    private final String module;

    public BuildingPropertiesExporter(Connection connection, CityGMLExportHelper helper, ExportManager manager) throws CityGMLExportException, SQLException {
        String tableName = manager.getSchemaMapper().getTableName(ADETable.BUILDING);
        CombinedProjectionFilter projectionFilter = helper.getCombinedProjectionFilter(tableName);
        module = UrbanObjectModule.v1_3.getNamespaceURI();

        Table table = new Table(helper.getTableNameWithSchema(tableName));
        Table details = new Table(helper.getTableNameWithSchema(manager.getSchemaMapper().getTableName(ADETable.BUILDINGDETAILS)));
        Table facilities = new Table(helper.getTableNameWithSchema(manager.getSchemaMapper().getTableName(ADETable.LARGECUSTOMERFACILITIE)));

        Select select = new Select().addProjection(table.getColumns("buildingdetails_id", "largecustomerfacilities_id"));
        if (projectionFilter.containsProperty("buildingDetailsProperty", module)) {
            select.addProjection(details.getColumns("areaclassificationtype", "areaclassification_codespace", "buildingfootprintarea",
                    "buildingfootprintarea_uom", "buildingroofedgearea", "buildingroofedgearea_uom", "buildingstructuretype",
                    "buildingstructuret_codespace", "city", "city_codespace", "developmentarea", "developmentarea_uom",
                    "districtsandzonestype", "districtsandzonest_codespace", "fireproofstructuretype", "fireproofstructure_codespace",
                    "implementingbody", "landuseplantype", "landuseplantype_codespace", "note", "prefecture", "prefecture_codespace",
                    "reference", "serialnumberofbuildingcertif", "sitearea", "sitearea_uom", "surveyyear", "totalfloorarea",
                    "totalfloorarea_uom", "urbanplantype", "urbanplantype_codespace"))
                    .addJoin(JoinFactory.left(details, "id", ComparisonName.EQUAL_TO, table.getColumn("buildingdetails_id")));
        }
        if (projectionFilter.containsProperty("largeCustomerFacilitiesProperty", module)) {
            select.addProjection(facilities.getColumn("areaclassificationtype", "area_2"), facilities.getColumn("areaclassification_codespace", "area_codespace_2"),
                    facilities.getColumn("city", "city_2"), facilities.getColumn("city_codespace", "city_codespace_2"),
                    facilities.getColumn("districtsandzonestype", "districts_2"), facilities.getColumn("districtsandzonest_codespace", "districts_codespace_2"),
                    facilities.getColumn("landuseplantype", "landuse_2"), facilities.getColumn("landuseplantype_codespace", "landuse_codespace_2"),
                    facilities.getColumn("note", "note_2"), facilities.getColumn("prefecture", "prefecture_2"),
                    facilities.getColumn("prefecture_codespace", "prefecture_codespace_2"), facilities.getColumn("reference", "reference_2"),
                    facilities.getColumn("surveyyear", "surveyyear_2"), facilities.getColumn("totalfloorarea", "totalfloorarea_2"),
                    facilities.getColumn("totalfloorarea_uom", "totalfloorarea_uom_2"), facilities.getColumn("urbanplantype", "urban_2"),
                    facilities.getColumn("urbanplantype_codespace", "urban_codespace_2"))
                    .addProjection(facilities.getColumns("availability", "capacity", "class", "class_codespace", "inauguraldate",
                            "keytenants", "name", "owner", "totalstorefloorarea", "totalstorefloorarea_uom"))
                    .addJoin(JoinFactory.left(facilities, "id", ComparisonName.EQUAL_TO, table.getColumn("largecustomerfacilities_id")));
        }
        select.addSelection(ComparisonFactory.equalTo(table.getColumn("id"), new PlaceHolder<>()));
        ps = connection.prepareStatement(select.toString());
    }

    public void doExport(AbstractBuilding parent, long parentId, FeatureType parentType, ProjectionFilter projectionFilter) throws CityGMLExportException, SQLException {
        ps.setLong(1, parentId);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                long buildingDetailsId = rs.getLong("buildingdetails_id");
                if (buildingDetailsId != 0 && projectionFilter.containsProperty("buildingDetailsProperty", module)) {
                    BuildingDetails buildingDetails = new BuildingDetails();

                    buildingDetails.setImplementingBody(rs.getString("implementingbody"));
                    buildingDetails.setReference(rs.getString("reference"));
                    buildingDetails.setSerialNumberOfBuildingCertification(rs.getString("serialnumberofbuildingcertif"));

                    double buildingFootprintArea = rs.getDouble("buildingfootprintarea");
                    if (!rs.wasNull()) {
                        Measure measure = new Measure(buildingFootprintArea);
                        measure.setUom(rs.getString("buildingfootprintarea_uom"));
                        buildingDetails.setBuildingFootprintArea(measure);
                    }

                    double buildingRoofEdgeArea = rs.getDouble("buildingroofedgearea");
                    if (!rs.wasNull()) {
                        Measure measure = new Measure(buildingRoofEdgeArea);
                        measure.setUom(rs.getString("buildingroofedgearea_uom"));
                        buildingDetails.setBuildingRoofEdgeArea(measure);
                    }

                    double developmentArea = rs.getDouble("developmentarea");
                    if (!rs.wasNull()) {
                        Measure measure = new Measure(developmentArea);
                        measure.setUom(rs.getString("developmentarea_uom"));
                        buildingDetails.setDevelopmentArea(measure);
                    }

                    double siteArea = rs.getDouble("sitearea");
                    if (!rs.wasNull()) {
                        Measure measure = new Measure(siteArea);
                        measure.setUom(rs.getString("sitearea_uom"));
                        buildingDetails.setSiteArea(measure);
                    }

                    Date surveyYear = rs.getDate("surveyyear");
                    if (!rs.wasNull())
                        buildingDetails.setSurveyYear(Year.of(surveyYear.toLocalDate().getYear()));

                    double totalFloorArea = rs.getDouble("totalfloorarea");
                    if (!rs.wasNull()) {
                        Measure measure = new Measure(totalFloorArea);
                        measure.setUom(rs.getString("totalfloorarea_uom"));
                        buildingDetails.setTotalFloorArea(measure);
                    }

                    String areaClassificationType = rs.getString("areaclassificationtype");
                    if (!rs.wasNull()) {
                        Code code = new Code(areaClassificationType);
                        code.setCodeSpace(rs.getString("areaclassification_codespace"));
                        buildingDetails.setAreaClassificationType(code);
                    }

                    String buildingStructureType = rs.getString("buildingstructuretype");
                    if (!rs.wasNull()) {
                        Code code = new Code(buildingStructureType);
                        code.setCodeSpace(rs.getString("buildingstructuret_codespace"));
                        buildingDetails.setBuildingStructureType(code);
                    }

                    String city = rs.getString("city");
                    if (!rs.wasNull()) {
                        Code code = new Code(city);
                        code.setCodeSpace(rs.getString("city_codespace"));
                        buildingDetails.setCity(code);
                    }

                    String districtsAndZonesType = rs.getString("districtsandzonestype");
                    if (!rs.wasNull()) {
                        Code code = new Code(districtsAndZonesType);
                        code.setCodeSpace(rs.getString("districtsandzonest_codespace"));
                        buildingDetails.setDistrictsAndZonesType(code);
                    }

                    String fireproofStructureType = rs.getString("fireproofstructuretype");
                    if (!rs.wasNull()) {
                        Code code = new Code(fireproofStructureType);
                        code.setCodeSpace(rs.getString("fireproofstructure_codespace"));
                        buildingDetails.setFireproofStructureType(code);
                    }

                    String landUsePlanType = rs.getString("landuseplantype");
                    if (!rs.wasNull()) {
                        Code code = new Code(landUsePlanType);
                        code.setCodeSpace(rs.getString("landuseplantype_codespace"));
                        buildingDetails.setLandUsePlanType(code);
                    }

                    buildingDetails.setNote(rs.getString("note"));

                    String prefecture = rs.getString("prefecture");
                    if (!rs.wasNull()) {
                        Code code = new Code(prefecture);
                        code.setCodeSpace(rs.getString("prefecture_codespace"));
                        buildingDetails.setPrefecture(code);
                    }

                    String urbanPlanType = rs.getString("urbanplantype");
                    if (!rs.wasNull()) {
                        Code code = new Code(urbanPlanType);
                        code.setCodeSpace(rs.getString("urbanplantype"));
                        buildingDetails.setUrbanPlanType(code);
                    }

                    BuildingDetailsPropertyElement property = new BuildingDetailsPropertyElement(new BuildingDetailsProperty(buildingDetails));
                    parent.addGenericApplicationPropertyOfAbstractBuilding(property);
                }

                long largeCustomerFacilitiesId = rs.getLong("largecustomerfacilities_id");
                if (largeCustomerFacilitiesId != 0 && projectionFilter.containsProperty("largeCustomerFacilitiesProperty", module)) {
                    LargeCustomerFacilities largeCustomerFacilities = new LargeCustomerFacilities();

                    largeCustomerFacilities.setKeyTenants(rs.getString("keytenants"));
                    largeCustomerFacilities.setName(rs.getString("name"));
                    largeCustomerFacilities.setNote(rs.getString("note_2"));
                    largeCustomerFacilities.setOwner(rs.getString("owner"));
                    largeCustomerFacilities.setReference(rs.getString("reference_2"));

                    boolean availability = rs.getBoolean("availability");
                    if (!rs.wasNull())
                        largeCustomerFacilities.setAvailability(availability);

                    int capacity = rs.getInt("capacity");
                    if (!rs.wasNull())
                        largeCustomerFacilities.setCapacity(capacity);

                    Date inauguralDate = rs.getDate("inauguraldate");
                    if (!rs.wasNull())
                        largeCustomerFacilities.setInauguralDate(inauguralDate.toLocalDate());

                    Date surveyYear = rs.getDate("surveyyear_2");
                    if (!rs.wasNull())
                        largeCustomerFacilities.setSurveyYear(Year.of(surveyYear.toLocalDate().getYear()));

                    double totalFloorArea = rs.getDouble("totalfloorarea_2");
                    if (!rs.wasNull()) {
                        Measure measure = new Measure(totalFloorArea);
                        measure.setUom(rs.getString("totalfloorarea_uom_2"));
                        largeCustomerFacilities.setTotalFloorArea(measure);
                    }

                    double totalStoreFloorArea = rs.getDouble("totalstorefloorarea");
                    if (!rs.wasNull()) {
                        Measure measure = new Measure(totalStoreFloorArea);
                        measure.setUom(rs.getString("totalstorefloorarea_uom"));
                        largeCustomerFacilities.setTotalStoreFloorArea(measure);
                    }

                    String areaClassificationType = rs.getString("area_2");
                    if (!rs.wasNull()) {
                        Code code = new Code(areaClassificationType);
                        code.setCodeSpace(rs.getString("area_codespace_2"));
                        largeCustomerFacilities.setAreaClassificationType(code);
                    }

                    String city = rs.getString("city_2");
                    if (!rs.wasNull()) {
                        Code code = new Code(city);
                        code.setCodeSpace(rs.getString("city_codespace_2"));
                        largeCustomerFacilities.setCity(code);
                    }

                    String classifier = rs.getString("class");
                    if (!rs.wasNull()) {
                        Code code = new Code(classifier);
                        code.setCodeSpace(rs.getString("class_codespace"));
                        largeCustomerFacilities.setClassifier(code);
                    }

                    String districtsAndZonesType = rs.getString("districts_2");
                    if (!rs.wasNull()) {
                        Code code = new Code(districtsAndZonesType);
                        code.setCodeSpace(rs.getString("districts_codespace_2"));
                        largeCustomerFacilities.setDistrictsAndZonesType(code);
                    }

                    String landUsePlanType = rs.getString("landuse_2");
                    if (!rs.wasNull()) {
                        Code code = new Code(landUsePlanType);
                        code.setCodeSpace(rs.getString("landuse_codespace_2"));
                        largeCustomerFacilities.setLandUsePlanType(code);
                    }

                    String prefecture = rs.getString("prefecture_2");
                    if (!rs.wasNull()) {
                        Code code = new Code(prefecture);
                        code.setCodeSpace(rs.getString("prefecture_codespace_2"));
                        largeCustomerFacilities.setPrefecture(code);
                    }

                    String urbanPlanType = rs.getString("urban_2");
                    if (!rs.wasNull()) {
                        Code code = new Code(urbanPlanType);
                        code.setCodeSpace(rs.getString("urban_codespace_2"));
                        largeCustomerFacilities.setUrbanPlanType(code);
                    }

                    LargeCustomerFacilitiesPropertyElement property = new LargeCustomerFacilitiesPropertyElement(new LargeCustomerFacilitiesProperty(largeCustomerFacilities));
                    parent.addGenericApplicationPropertyOfAbstractBuilding(property);
                }
            }
        }
    }

    @Override
    public void close() throws CityGMLExportException, SQLException {
        ps.close();
    }
}
