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

import org.citydb.ade.exporter.CityGMLExportHelper;
import org.citydb.ade.iur.exporter.ExportManager;
import org.citydb.ade.iur.schema.ADETable;
import org.citydb.citygml.exporter.CityGMLExportException;
import org.citydb.citygml.exporter.database.content.GMLConverter;
import org.citydb.citygml.exporter.database.content.SurfaceGeometryExporter;
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
import org.citygml4j.ade.iur.model.module.UrbanFunctionModule;
import org.citygml4j.ade.iur.model.urf.LegalGrounds;
import org.citygml4j.ade.iur.model.urf.LegalGroundsProperty;
import org.citygml4j.ade.iur.model.urf.TargetProperty;
import org.citygml4j.ade.iur.model.urf.UrbanFunction;
import org.citygml4j.model.gml.basicTypes.Code;
import org.citygml4j.model.gml.basicTypes.Measure;
import org.citygml4j.model.gml.geometry.aggregates.MultiCurveProperty;
import org.citygml4j.model.gml.geometry.aggregates.MultiPointProperty;

import java.sql.*;
import java.time.Year;

public class UrbanFunctionExporter implements UrbanFunctionModuleExporter {
    private final CityGMLExportHelper helper;
    private final ExportManager manager;
    private final SurfaceGeometryExporter surfaceGeometryExporter;
    private final PreparedStatement ps;
    private final String module;

    private final GMLConverter gmlConverter;
    private final AttributeValueSplitter valueSplitter;

    public UrbanFunctionExporter(Connection connection, CityGMLExportHelper helper, ExportManager manager) throws CityGMLExportException, SQLException {
        this.helper = helper;
        this.manager = manager;

        String tableName = manager.getSchemaMapper().getTableName(ADETable.URBANFUNCTION);
        CombinedProjectionFilter projectionFilter = helper.getCombinedProjectionFilter(tableName);
        module = UrbanFunctionModule.v1_4.getNamespaceURI();

        Table table = new Table(helper.getTableNameWithSchema(tableName));
        Select select = addProjection(new Select(), table, projectionFilter, "")
                .addSelection(ComparisonFactory.equalTo(table.getColumn("id"), new PlaceHolder<>()));
        if (projectionFilter.containsProperty("target", module)) {
            Table targets = new Table(helper.getTableNameWithSchema(manager.getSchemaMapper().getTableName(ADETable.URBANFUNC_TO_CITYOBJEC)));
            Table cityObject = new Table(helper.getTableNameWithSchema(MappingConstants.CITYOBJECT));
            select.addProjection(cityObject.getColumn("id", "tid"), cityObject.getColumn("gmlid"))
                    .addJoin(JoinFactory.left(targets, "urbanfunction_id", ComparisonName.EQUAL_TO, table.getColumn("id")))
                    .addJoin(JoinFactory.left(cityObject, "id", ComparisonName.EQUAL_TO, targets.getColumn("cityobject_id")));
        }
        ps = connection.prepareStatement(select.toString());

        surfaceGeometryExporter = helper.getSurfaceGeometryExporter();
        gmlConverter = helper.getGMLConverter();
        valueSplitter = helper.getAttributeValueSplitter();
    }

    public Select addProjection(Select select, Table table, CombinedProjectionFilter projectionFilter, String prefix) {
        select.addProjection(table.getColumn("id", prefix + "id"));
        if (projectionFilter.containsProperty("class", module))
            select.addProjection(table.getColumn("class", prefix + "class"), table.getColumn("class_codespace", prefix + "class_codespace"));
        if (projectionFilter.containsProperty("function", module))
            select.addProjection(table.getColumn("function", prefix + "function"), table.getColumn("function_codespace", prefix + "function_codespace"));
        if (projectionFilter.containsProperty("enactmentDate", module))
            select.addProjection(table.getColumn("enactmentdate", prefix + "enactmentdate"));
        if (projectionFilter.containsProperty("expirationFiscalYear", module))
            select.addProjection(table.getColumn("enactmentfiscalyear", prefix + "enactmentfiscalyear"));
        if (projectionFilter.containsProperty("expirationDate", module))
            select.addProjection(table.getColumn("expirationdate", prefix + "expirationdate"));
        if (projectionFilter.containsProperty("expirationFiscalYear", module))
            select.addProjection(table.getColumn("expirationfiscalyear", prefix + "expirationfiscalyear"));
        if (projectionFilter.containsProperty("custodian", module))
            select.addProjection(table.getColumn("custodian", prefix + "custodian"));
        if (projectionFilter.containsProperty("nominalArea", module))
            select.addProjection(table.getColumn("nominalarea", prefix + "nominalarea"), table.getColumn("nominalarea_uom", prefix + "nominalarea_uom"));
        if (projectionFilter.containsProperty("abstract", module))
            select.addProjection(table.getColumn("abstract", prefix + "abstract"));
        if (projectionFilter.containsProperty("validity", module))
            select.addProjection(table.getColumn("validity", prefix + "validity"));
        if (projectionFilter.containsProperty("urbanPlanType", module))
            select.addProjection(table.getColumn("urbanplantype", prefix + "urbanplantype"), table.getColumn("urbanplantype_codespace", prefix + "urbanplantype_codespace"));
        if (projectionFilter.containsProperty("areaClassificationType", module))
            select.addProjection(table.getColumn("areaclassificationtype", prefix + "areaclassificationtype"), table.getColumn("areaclassification_codespace", prefix + "areaclassification_codespace"));
        if (projectionFilter.containsProperty("prefecture", module))
            select.addProjection(table.getColumn("prefecture", prefix + "prefecture"), table.getColumn("prefecture_codespace", prefix + "prefecture_codespace"));
        if (projectionFilter.containsProperty("city", module))
            select.addProjection(table.getColumn("city", prefix + "city"), table.getColumn("city_codespace", prefix + "city_codespace"));
        if (projectionFilter.containsProperty("reference", module))
            select.addProjection(table.getColumn("reference", prefix + "reference"));
        if (projectionFilter.containsProperty("note", module))
            select.addProjection(table.getColumn("note", prefix + "note"));
        if (projectionFilter.containsProperty("surveyYear", module))
            select.addProjection(table.getColumn("surveyyear", prefix + "surveyyear"));
        if (helper.getLodFilter().isEnabled(0)) {
            if (projectionFilter.containsProperty("lod0MultiPoint", module))
                select.addProjection(helper.getGeometryColumn(table.getColumn("lod0multipoint"), prefix + "lod0multipoint"));
            if (projectionFilter.containsProperty("lod0MultiCurve", module))
                select.addProjection(helper.getGeometryColumn(table.getColumn("lod0multicurve"), prefix + "lod0multicurve"));
            if (projectionFilter.containsProperty("lod0MultiSurface", module))
                select.addProjection(table.getColumn("lod0multisurface_id", prefix + "lod0multisurface_id"));
        }
        if (projectionFilter.containsProperty("lod-1MultiPoint", module))
            select.addProjection(helper.getGeometryColumn(table.getColumn("lod_1multipoint"), prefix + "lod_1multipoint"));
        if (projectionFilter.containsProperty("lod-2MultiPoint", module))
            select.addProjection(helper.getGeometryColumn(table.getColumn("lod_2multipoint"), prefix + "lod_2multipoint"));
        if (projectionFilter.containsProperty("lod-1MultiCurve", module))
            select.addProjection(helper.getGeometryColumn(table.getColumn("lod_1multicurve"), prefix + "lod_1multicurve"));
        if (projectionFilter.containsProperty("lod-2MultiCurve", module))
            select.addProjection(helper.getGeometryColumn(table.getColumn("lod_2multicurve"), prefix + "lod_2multicurve"));
        if (projectionFilter.containsProperty("lod-1MultiSurface", module))
            select.addProjection(table.getColumn("lod_1multisurface_id", prefix + "lod_1multisurface_id"));
        if (projectionFilter.containsProperty("lod-2MultiSurface", module))
            select.addProjection(table.getColumn("lod_2multisurface_id", prefix + "lod_2multisurface_id"));
        if (projectionFilter.containsProperty("legalGrounds", module)) {
            Table legalGrounds = new Table(helper.getTableNameWithSchema(manager.getSchemaMapper().getTableName(ADETable.LEGALGROUNDS)));
            select.addProjection(table.getColumn("legalgrounds_id", prefix + "legalgrounds_id"),
                    legalGrounds.getColumn("articlesofregulation", prefix + "articlesofregulation"),
                    legalGrounds.getColumn("articlesofregulati_codespace", prefix + "articlesofregulati_codespace"),
                    legalGrounds.getColumn("date_", prefix + "date_"),
                    legalGrounds.getColumn("nameofregulation", prefix + "nameofregulation"),
                    legalGrounds.getColumn("nameofregulation_codespace", prefix + "nameofregulation_codespace"))
                    .addJoin(JoinFactory.left(legalGrounds, "id", ComparisonName.EQUAL_TO, table.getColumn("legalgrounds_id")));
        }

        return select;
    }

    public void doExport(UrbanFunction urbanFunction, long objectId, AbstractType<?> objectType, ProjectionFilter projectionFilter) throws CityGMLExportException, SQLException {
        ps.setLong(1, objectId);

        try (ResultSet rs = ps.executeQuery()) {
            boolean isInitialized = false;

            while (rs.next()) {
                if (!isInitialized) {
                    doExport(urbanFunction, projectionFilter, "", rs);
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

    public void doExport(UrbanFunction urbanFunction, ProjectionFilter projectionFilter, String prefix, ResultSet rs) throws CityGMLExportException, SQLException {
        if (projectionFilter.containsProperty("class", module)) {
            String classifier = rs.getString(prefix + "class");
            if (!rs.wasNull()) {
                Code code = new Code(classifier);
                code.setCodeSpace(rs.getString(prefix + "class_codespace"));
                urbanFunction.setClassifier(code);
            }
        }

        if (projectionFilter.containsProperty("function", module)) {
            for (AttributeValueSplitter.SplitValue splitValue : valueSplitter.split(rs.getString(prefix + "function"), rs.getString(prefix + "function_codespace"))) {
                Code function = new Code(splitValue.result(0));
                function.setCodeSpace(splitValue.result(1));
                urbanFunction.getFunctions().add(function);
            }
        }

        if (projectionFilter.containsProperty("enactmentDate", module)) {
            Date enactmentDate = rs.getDate(prefix + "enactmentdate");
            if (!rs.wasNull())
                urbanFunction.setEnactmentDate(enactmentDate.toLocalDate());
        }

        if (projectionFilter.containsProperty("enactmentFiscalYear", module)) {
            Date enactmentFiscalYear = rs.getDate(prefix + "enactmentfiscalyear");
            if (!rs.wasNull())
                urbanFunction.setEnactmentFiscalYear(Year.of(enactmentFiscalYear.toLocalDate().getYear()));
        }

        if (projectionFilter.containsProperty("expirationDate", module)) {
            Date expirationDate = rs.getDate(prefix + "expirationdate");
            if (!rs.wasNull())
                urbanFunction.setExpirationDate(expirationDate.toLocalDate());
        }

        if (projectionFilter.containsProperty("expirationFiscalYear", module)) {
            Date expirationFiscalYear = rs.getDate(prefix + "expirationfiscalyear");
            if (!rs.wasNull())
                urbanFunction.setExpirationFiscalYear(Year.of(expirationFiscalYear.toLocalDate().getYear()));
        }

        if (projectionFilter.containsProperty("custodian", module))
            urbanFunction.setCustodian(rs.getString(prefix + "custodian"));

        if (projectionFilter.containsProperty("nominalArea", module)) {
            double nominalArea = rs.getDouble(prefix + "nominalarea");
            if (!rs.wasNull()) {
                Measure measure = new Measure(nominalArea);
                measure.setUom(rs.getString(prefix + "nominalarea_uom"));
                urbanFunction.setNominalArea(measure);
            }
        }

        if (projectionFilter.containsProperty("abstract", module))
            urbanFunction.setAbstract(rs.getString(prefix + "abstract"));

        if (projectionFilter.containsProperty("validity", module)) {
            boolean validity = rs.getBoolean(prefix + "validity");
            if (!rs.wasNull())
                urbanFunction.setValidity(validity);
        }

        if (projectionFilter.containsProperty("urbanPlanType", module)) {
            String urbanPlanType = rs.getString(prefix + "urbanplantype");
            if (!rs.wasNull()) {
                Code code = new Code(urbanPlanType);
                code.setCodeSpace(rs.getString(prefix + "urbanplantype_codespace"));
                urbanFunction.setUrbanPlanType(code);
            }
        }

        if (projectionFilter.containsProperty("areaClassificationType", module)) {
            String areaClassificationType = rs.getString(prefix + "areaclassificationtype");
            if (!rs.wasNull()) {
                Code code = new Code(areaClassificationType);
                code.setCodeSpace(rs.getString(prefix + "areaclassification_codespace"));
                urbanFunction.setAreaClassificationType(code);
            }
        }

        if (projectionFilter.containsProperty("prefecture", module)) {
            String prefecture = rs.getString(prefix + "prefecture");
            if (!rs.wasNull()) {
                Code code = new Code(prefecture);
                code.setCodeSpace(rs.getString(prefix + "prefecture_codespace"));
                urbanFunction.setPrefecture(code);
            }
        }

        if (projectionFilter.containsProperty("city", module)) {
            String city = rs.getString(prefix + "city");
            if (!rs.wasNull()) {
                Code code = new Code(city);
                code.setCodeSpace(rs.getString(prefix + "city_codespace"));
                urbanFunction.setCity(code);
            }
        }

        if (projectionFilter.containsProperty("reference", module))
            urbanFunction.setReference(rs.getString(prefix + "reference"));

        if (projectionFilter.containsProperty("note", module))
            urbanFunction.setNote(rs.getString(prefix + "note"));

        if (projectionFilter.containsProperty("surveyYear", module)) {
            Date surveyYear = rs.getDate(prefix + "surveyyear");
            if (!rs.wasNull())
                urbanFunction.setSurveyYear(Year.of(surveyYear.toLocalDate().getYear()));
        }

        for (int i = 0; i < 3; i++) {
            if (!projectionFilter.containsProperty("lod" + (i == 0 ? i : "-" + i) + "MultiPoint"))
                continue;

            if (i == 0 && !helper.getLodFilter().isEnabled(0))
                continue;

            Object geometry = rs.getObject(prefix + "lod" + (i == 0 ? i : "_" + i) + "multipoint");
            if (rs.wasNull())
                continue;

            GeometryObject multiPoint = helper.getDatabaseAdapter().getGeometryConverter().getMultiPoint(geometry);
            if (multiPoint != null) {
                MultiPointProperty property = gmlConverter.getMultiPointProperty(multiPoint);
                if (property != null) {
                    switch (i) {
                        case 0:
                            urbanFunction.setLod0MultiPoint(property);
                            break;
                        case 1:
                            urbanFunction.setLod1MultiPoint(property);
                            break;
                        case 2:
                            urbanFunction.setLod2MultiPoint(property);
                            break;
                    }
                }
            }
        }

        for (int i = 0; i < 3; i++) {
            if (!projectionFilter.containsProperty("lod" + (i == 0 ? i : "-" + i) + "MultiCurve"))
                continue;

            if (i == 0 && !helper.getLodFilter().isEnabled(0))
                continue;

            Object geometry = rs.getObject(prefix + "lod" + (i == 0 ? i : "_" + i) + "multicurve");
            if (rs.wasNull())
                continue;

            GeometryObject multiCurve = helper.getDatabaseAdapter().getGeometryConverter().getMultiCurve(geometry);
            if (multiCurve != null) {
                MultiCurveProperty property = gmlConverter.getMultiCurveProperty(multiCurve);
                if (property != null) {
                    switch (i) {
                        case 0:
                            urbanFunction.setLod0MultiCurve(property);
                            break;
                        case 1:
                            urbanFunction.setLod1MultiCurve(property);
                            break;
                        case 2:
                            urbanFunction.setLod2MultiCurve(property);
                            break;
                    }
                }
            }
        }

        for (int i = 0; i < 3; i++) {
            if (!projectionFilter.containsProperty("lod" + (i == 0 ? i : "-" + i) + "MultiSurface"))
                continue;

            if (i == 0 && !helper.getLodFilter().isEnabled(0))
                continue;

            long surfaceGeometryId = rs.getLong(prefix + "lod" + (i == 0 ? i : "_" + i) + "multisurface_id");
            if (rs.wasNull())
                continue;

            switch (i) {
                case 0:
                    surfaceGeometryExporter.addBatch(surfaceGeometryId, urbanFunction::setLod0MultiSurface);
                    break;
                case 1:
                    surfaceGeometryExporter.addBatch(surfaceGeometryId, urbanFunction::setLod1MultiSurface);
                    break;
                case 2:
                    surfaceGeometryExporter.addBatch(surfaceGeometryId, urbanFunction::setLod2MultiSurface);
                    break;
            }
        }

        long legalGroundsId = rs.getLong(prefix + "legalgrounds_id");
        if (legalGroundsId != 0 && projectionFilter.containsProperty("legalGrounds", module)) {
            LegalGrounds legalGrounds = new LegalGrounds();

            String articlesOfRegulation = rs.getString(prefix + "articlesofregulation");
            if (!rs.wasNull()) {
                Code code = new Code(articlesOfRegulation);
                code.setCodeSpace(rs.getString(prefix + "articlesofregulati_codespace"));
                legalGrounds.setArticlesOfRegulation(code);
            }

            Date date = rs.getDate(prefix + "date_");
            if (!rs.wasNull())
                legalGrounds.setDate(date.toLocalDate());

            String nameOfRegulation = rs.getString(prefix + "nameofregulation");
            if (!rs.wasNull()) {
                Code code = new Code(nameOfRegulation);
                code.setCodeSpace(rs.getString(prefix + "nameofregulation_codespace"));
                legalGrounds.setNameOfRegulation(code);
            }

            urbanFunction.setLegalGrounds(new LegalGroundsProperty(legalGrounds));
        }
    }

    @Override
    public void close() throws CityGMLExportException, SQLException {
        ps.close();
    }
}
