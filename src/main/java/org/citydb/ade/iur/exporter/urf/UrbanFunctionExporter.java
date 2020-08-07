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

package org.citydb.ade.iur.exporter.urf;

import org.citydb.ade.exporter.ADEExporter;
import org.citydb.ade.exporter.CityGMLExportHelper;
import org.citydb.ade.iur.exporter.ExportManager;
import org.citydb.ade.iur.schema.ADETable;
import org.citydb.citygml.exporter.CityGMLExportException;
import org.citydb.citygml.exporter.database.content.GMLConverter;
import org.citydb.citygml.exporter.database.content.SurfaceGeometry;
import org.citydb.citygml.exporter.util.AttributeValueSplitter;
import org.citydb.config.geometry.GeometryObject;
import org.citydb.database.schema.mapping.AbstractType;
import org.citydb.database.schema.mapping.MappingConstants;
import org.citydb.query.filter.projection.CombinedProjectionFilter;
import org.citydb.query.filter.projection.ProjectionFilter;
import org.citydb.sqlbuilder.expression.PlaceHolder;
import org.citydb.sqlbuilder.schema.Table;
import org.citydb.sqlbuilder.select.Select;
import org.citydb.sqlbuilder.select.join.JoinFactory;
import org.citydb.sqlbuilder.select.operator.comparison.ComparisonFactory;
import org.citydb.sqlbuilder.select.operator.comparison.ComparisonName;
import org.citygml4j.model.gml.GMLClass;
import org.citygml4j.model.gml.basicTypes.Code;
import org.citygml4j.model.gml.basicTypes.Measure;
import org.citygml4j.model.gml.geometry.aggregates.MultiCurveProperty;
import org.citygml4j.model.gml.geometry.aggregates.MultiPointProperty;
import org.citygml4j.model.gml.geometry.aggregates.MultiSurface;
import org.citygml4j.model.gml.geometry.aggregates.MultiSurfaceProperty;
import org.citygml4j.ade.iur.model.module.UrbanFunctionModule;
import org.citygml4j.ade.iur.model.urf.DisasterPreventionBase;
import org.citygml4j.ade.iur.model.urf.LegalGrounds;
import org.citygml4j.ade.iur.model.urf.LegalGroundsProperty;
import org.citygml4j.ade.iur.model.urf.TargetProperty;
import org.citygml4j.ade.iur.model.urf.UrbanFunction;
import org.citygml4j.ade.iur.model.urf.Zone;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Year;

public class UrbanFunctionExporter implements ADEExporter {
    private final CityGMLExportHelper helper;
    private final PreparedStatement ps;
    private final String module;

    private final GMLConverter gmlConverter;
    private final AttributeValueSplitter valueSplitter;

    public UrbanFunctionExporter(Connection connection, CityGMLExportHelper helper, ExportManager manager) throws CityGMLExportException, SQLException {
        this.helper = helper;

        String tableName = manager.getSchemaMapper().getTableName(ADETable.URBANFUNCTION);
        CombinedProjectionFilter projectionFilter = helper.getCombinedProjectionFilter(tableName);
        module = UrbanFunctionModule.v1_3.getNamespaceURI();

        Table table = new Table(helper.getTableNameWithSchema(tableName));
        Table zone = new Table(manager.getSchemaMapper().getTableName(ADETable.ZONE));
        Table legalGrounds = new Table(manager.getSchemaMapper().getTableName(ADETable.LEGALGROUNDS));
        Table targets = new Table(manager.getSchemaMapper().getTableName(ADETable.URBANFUNC_TO_CITYOBJEC));
        Table cityObject = new Table(helper.getTableNameWithSchema(MappingConstants.CITYOBJECT));

        Select select = new Select().addProjection(table.getColumns("legalgrounds_id", "abstract", "area_id",
                "areaclassificationtype", "areaclassification_codespace", "city", "city_codespace", "class",
                "class_codespace", "custodian", "enactmentdate", "enactmentfiscalyear", "expirationdate",
                "expirationfiscalyear", "function", "function_codespace", "nominalarea", "nominalarea_uom", "note",
                "prefecture", "prefecture_codespace", "reference", "surveyyear", "urbanplantype", "urbanplantype_codespace",
                "validity", "capacity"))
                .addProjection(zone.getColumns("areaapplied", "finalpublicationdate"))
                .addJoin(JoinFactory.left(zone, "id", ComparisonName.EQUAL_TO, table.getColumn("id")));
        if (projectionFilter.containsProperty("boundary", module))
            select.addProjection(helper.getGeometryColumn(table.getColumn("boundary")));
        if (projectionFilter.containsProperty("location", module))
            select.addProjection(helper.getGeometryColumn(table.getColumn("location")));
        if (projectionFilter.containsProperty("legalGrounds", module)) {
            select.addProjection(legalGrounds.getColumns("articlesofregulation", "articlesofregulati_codespace", "date_",
                    "nameofregulation", "nameofregulation_codespace"))
                    .addJoin(JoinFactory.left(legalGrounds, "id", ComparisonName.EQUAL_TO, table.getColumn("legalgrounds_id")));
        }
        if (projectionFilter.containsProperty("target", module)) {
            select.addProjection(cityObject.getColumn(MappingConstants.GMLID))
                    .addJoin(JoinFactory.left(targets, "urbanfunction_id", ComparisonName.EQUAL_TO, table.getColumn("id")))
                    .addJoin(JoinFactory.left(cityObject, "id", ComparisonName.EQUAL_TO, targets.getColumn("cityobject_id")));
        }
        select.addSelection(ComparisonFactory.equalTo(table.getColumn("id"), new PlaceHolder<>()));
        ps = connection.prepareStatement(select.toString());

        gmlConverter = helper.getGMLConverter();
        valueSplitter = helper.getAttributeValueSplitter();
    }

    public void doExport(UrbanFunction urbanFunction, long objectId, AbstractType<?> objectType, ProjectionFilter projectionFilter) throws CityGMLExportException, SQLException {
        ps.setLong(1, objectId);

        try (ResultSet rs = ps.executeQuery()) {
            boolean isInitialized = false;

            while (rs.next()) {
                if (!isInitialized) {
                    if (projectionFilter.containsProperty("class", module)) {
                        String classifier = rs.getString("class");
                        if (!rs.wasNull()) {
                            Code code = new Code(classifier);
                            code.setCodeSpace(rs.getString("class_codespace"));
                            urbanFunction.setClassifier(code);
                        }
                    }

                    if (projectionFilter.containsProperty("function", module)) {
                        for (AttributeValueSplitter.SplitValue splitValue : valueSplitter.split(rs.getString("function"), rs.getString("function_codespace"))) {
                            Code function = new Code(splitValue.result(0));
                            function.setCodeSpace(splitValue.result(1));
                            urbanFunction.getFunctions().add(function);
                        }
                    }

                    if (projectionFilter.containsProperty("enactmentDate", module)) {
                        Date enactmentDate = rs.getDate("enactmentdate");
                        if (!rs.wasNull())
                            urbanFunction.setEnactmentDate(enactmentDate.toLocalDate());
                    }

                    if (projectionFilter.containsProperty("enactmentFiscalYear", module)) {
                        Date enactmentFiscalYear = rs.getDate("enactmentfiscalyear");
                        if (!rs.wasNull())
                            urbanFunction.setEnactmentFiscalYear(Year.of(enactmentFiscalYear.toLocalDate().getYear()));
                    }

                    if (projectionFilter.containsProperty("expirationDate", module)) {
                        Date expirationDate = rs.getDate("expirationdate");
                        if (!rs.wasNull())
                            urbanFunction.setExpirationDate(expirationDate.toLocalDate());
                    }

                    if (projectionFilter.containsProperty("expirationFiscalYear", module)) {
                        Date expirationFiscalYear = rs.getDate("expirationfiscalyear");
                        if (!rs.wasNull())
                            urbanFunction.setExpirationFiscalYear(Year.of(expirationFiscalYear.toLocalDate().getYear()));
                    }

                    if (projectionFilter.containsProperty("custodian", module))
                        urbanFunction.setCustodian(rs.getString("custodian"));

                    if (projectionFilter.containsProperty("nominalArea", module)) {
                        double nominalArea = rs.getDouble("nominalarea");
                        if (!rs.wasNull()) {
                            Measure measure = new Measure(nominalArea);
                            measure.setUom(rs.getString("nominalarea_uom"));
                            urbanFunction.setNominalArea(measure);
                        }
                    }

                    if (projectionFilter.containsProperty("abstract", module))
                        urbanFunction.setAbstract(rs.getString("abstract"));

                    if (projectionFilter.containsProperty("validity", module)) {
                        boolean validity = rs.getBoolean("validity");
                        if (!rs.wasNull())
                            urbanFunction.setValidity(validity);
                    }

                    if (projectionFilter.containsProperty("urbanPlanType", module)) {
                        String urbanPlanType = rs.getString("urbanplantype");
                        if (!rs.wasNull()) {
                            Code code = new Code(urbanPlanType);
                            code.setCodeSpace(rs.getString("urbanplantype_codespace"));
                            urbanFunction.setUrbanPlanType(code);
                        }
                    }

                    if (projectionFilter.containsProperty("areaClassificationType", module)) {
                        String areaClassificationType = rs.getString("areaclassificationtype");
                        if (!rs.wasNull()) {
                            Code code = new Code(areaClassificationType);
                            code.setCodeSpace(rs.getString("areaclassification_codespace"));
                            urbanFunction.setAreaClassificationType(code);
                        }
                    }

                    if (projectionFilter.containsProperty("prefecture", module)) {
                        String prefecture = rs.getString("prefecture");
                        if (!rs.wasNull()) {
                            Code code = new Code(prefecture);
                            code.setCodeSpace(rs.getString("prefecture_codespace"));
                            urbanFunction.setPrefecture(code);
                        }
                    }

                    if (projectionFilter.containsProperty("city", module)) {
                        String city = rs.getString("city");
                        if (!rs.wasNull()) {
                            Code code = new Code(city);
                            code.setCodeSpace(rs.getString("city_codespace"));
                            urbanFunction.setCity(code);
                        }
                    }

                    if (projectionFilter.containsProperty("reference", module))
                        urbanFunction.setReference(rs.getString("reference"));

                    if (projectionFilter.containsProperty("note", module))
                        urbanFunction.setNote(rs.getString("note"));

                    if (projectionFilter.containsProperty("surveyYear", module)) {
                        Date surveyYear = rs.getDate("surveyyear");
                        if (!rs.wasNull())
                            urbanFunction.setSurveyYear(Year.of(surveyYear.toLocalDate().getYear()));
                    }

                    if (projectionFilter.containsProperty("area", module)) {
                        long areaId = rs.getLong("area_id");
                        if (!rs.wasNull()) {
                            SurfaceGeometry geometry = helper.exportSurfaceGeometry(areaId);
                            if (geometry != null && geometry.getType() == GMLClass.MULTI_SURFACE) {
                                MultiSurfaceProperty multiSurfaceProperty = new MultiSurfaceProperty();
                                if (geometry.isSetGeometry())
                                    multiSurfaceProperty.setMultiSurface((MultiSurface) geometry.getGeometry());
                                else
                                    multiSurfaceProperty.setHref(geometry.getReference());

                                urbanFunction.setArea(multiSurfaceProperty);
                            }
                        }
                    }

                    if (projectionFilter.containsProperty("boundary", module)) {
                        Object object = rs.getObject("boundary");
                        if (object != null) {
                            GeometryObject multiCurve = helper.getDatabaseAdapter().getGeometryConverter().getMultiCurve(object);
                            if (multiCurve != null) {
                                MultiCurveProperty property = gmlConverter.getMultiCurveProperty(multiCurve, false);
                                urbanFunction.setBoundary(property);
                            }
                        }
                    }

                    if (projectionFilter.containsProperty("location", module)) {
                        Object object = rs.getObject("location");
                        if (object != null) {
                            GeometryObject multiPoint = helper.getDatabaseAdapter().getGeometryConverter().getMultiPoint(object);
                            if (multiPoint != null) {
                                MultiPointProperty property = gmlConverter.getMultiPointProperty(multiPoint, false);
                                urbanFunction.setPointLocation(property);
                            }
                        }
                    }

                    long legalGroundsId = rs.getLong("legalgrounds_id");
                    if (legalGroundsId != 0 && projectionFilter.containsProperty("legalGrounds", module)) {
                        LegalGrounds legalGrounds = new LegalGrounds();

                        String articlesOfRegulation = rs.getString("articlesofregulation");
                        if (!rs.wasNull()) {
                            Code code = new Code(articlesOfRegulation);
                            code.setCodeSpace(rs.getString("articlesofregulati_codespace"));
                            legalGrounds.setArticlesOfRegulation(code);
                        }

                        Date date = rs.getDate("date_");
                        if (!rs.wasNull())
                            legalGrounds.setDate(date.toLocalDate());

                        String nameOfRegulation = rs.getString("nameofregulation");
                        if (!rs.wasNull()) {
                            Code code = new Code(nameOfRegulation);
                            code.setCodeSpace(rs.getString("nameofregulation_codespace"));
                            legalGrounds.setNameOfRegulation(code);
                        }

                        urbanFunction.setLegalGrounds(new LegalGroundsProperty(legalGrounds));
                    }

                    if (urbanFunction instanceof Zone) {
                        Zone zone = (Zone) urbanFunction;

                        if (projectionFilter.containsProperty("finalPublicationDate", module)) {
                            Date finalPublicationDate = rs.getDate("finalpublicationdate");
                            if (!rs.wasNull())
                                zone.setFinalPublicationDate(finalPublicationDate.toLocalDate());
                        }

                        if (projectionFilter.containsProperty("areaApplied", module))
                            zone.setAreaApplied(rs.getString("areaapplied"));
                    }

                    if (urbanFunction instanceof DisasterPreventionBase) {
                        DisasterPreventionBase disasterPreventionBase = (DisasterPreventionBase) urbanFunction;

                        if (projectionFilter.containsProperty("capacity", module)) {
                            int capacity = rs.getInt("capacity");
                            if (!rs.wasNull())
                                disasterPreventionBase.setCapacity(capacity);
                        }
                    }

                    isInitialized = true;
                }

                if (projectionFilter.containsProperty("target", module)) {
                    String gmlId = rs.getString(MappingConstants.GMLID);
                    if (gmlId != null)
                        urbanFunction.getTargets().add(new TargetProperty("#" + gmlId));
                }
            }
        }
    }

    @Override
    public void close() throws CityGMLExportException, SQLException {
        ps.close();
    }
}
