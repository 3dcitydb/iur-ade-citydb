/*
 * 3D City Database - The Open Source CityGML Database
 * https://www.3dcitydb.org/
 *
 * Copyright 2013 - 2024
 * Chair of Geoinformatics
 * Technical University of Munich, Germany
 * https://www.lrg.tum.de/gis/
 *
 * The 3D City Database is jointly developed with the following
 * cooperation partners:
 *
 * Virtual City Systems, Berlin <https://vc.systems/>
 * M.O.S.S. Computer Grafik Systeme GmbH, Taufkirchen <http://www.moss.de/>
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

package org.citydb.ade.iur.importer.urf;

import org.citydb.ade.iur.importer.ImportManager;
import org.citydb.ade.iur.schema.ADETable;
import org.citydb.ade.iur.schema.SchemaMapper;
import org.citydb.config.geometry.GeometryObject;
import org.citydb.core.ade.importer.CityGMLImportHelper;
import org.citydb.core.ade.importer.ForeignKeys;
import org.citydb.core.database.schema.mapping.AbstractObjectType;
import org.citydb.core.operation.importer.CityGMLImportException;
import org.citydb.core.operation.importer.util.AttributeValueJoiner;
import org.citydb.core.operation.importer.util.GeometryConverter;
import org.citygml4j.ade.iur.model.urf.DisasterPreventionBase;
import org.citygml4j.ade.iur.model.urf.TargetProperty;
import org.citygml4j.ade.iur.model.urf.UrbanFunction;
import org.citygml4j.model.citygml.core.AbstractCityObject;
import org.citygml4j.model.gml.basicTypes.Code;
import org.citygml4j.model.gml.geometry.aggregates.MultiCurveProperty;
import org.citygml4j.model.gml.geometry.aggregates.MultiPointProperty;
import org.citygml4j.model.gml.geometry.aggregates.MultiSurfaceProperty;

import java.sql.*;
import java.time.LocalDate;

public class UrbanFunctionImporter implements UrbanFunctionModuleImporter {
    private final Connection connection;
    private final CityGMLImportHelper helper;
    private final SchemaMapper schemaMapper;
    private final PreparedStatement ps;

    private final LegalGroundsImporter legalGroundsImporter;
    private final UrbanFunctionToCityObjectImporter urbanFunctionToCityObjectImporter;
    private final GeometryConverter geometryConverter;
    private final AttributeValueJoiner valueJoiner;

    private int batchCounter;

    public UrbanFunctionImporter(Connection connection, CityGMLImportHelper helper, ImportManager manager) throws CityGMLImportException, SQLException {
        this.connection = connection;
        this.helper = helper;
        this.schemaMapper = manager.getSchemaMapper();

        ps = connection.prepareStatement("insert into " +
                helper.getTableNameWithSchema(schemaMapper.getTableName(ADETable.URBANFUNCTION)) + " " +
                "(id, objectclass_id, abstract, areaclassificationtype, areaclassification_codespace, city, city_codespace, " +
                "class, class_codespace, custodian, enactmentdate, enactmentfiscalyear, expirationdate, expirationfiscalyear, " +
                "function, function_codespace, legalgrounds_id, " +
                "lod0multipoint, lod_1multipoint, lod_2multipoint, " +
                "lod0multicurve, lod_1multicurve, lod_2multicurve, " +
                "lod0multisurface_id, lod_1multisurface_id, lod_2multisurface_id, " +
                "nominalarea, nominalarea_uom, note, prefecture, prefecture_codespace, reference, surveyyear, urbanplantype, " +
                "urbanplantype_codespace, validity, capacity) " +
                "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

        legalGroundsImporter = manager.getImporter(LegalGroundsImporter.class);
        urbanFunctionToCityObjectImporter = manager.getImporter(UrbanFunctionToCityObjectImporter.class);
        geometryConverter = helper.getGeometryConverter();
        valueJoiner = helper.getAttributeValueJoiner();
    }

    public void doImport(UrbanFunction urbanFunction, long objectId, AbstractObjectType<?> objectType, ForeignKeys foreignKeys) throws CityGMLImportException, SQLException {
        ps.setLong(1, objectId);
        ps.setInt(2, objectType.getObjectClassId());

        ps.setString(3, urbanFunction.getAbstract());

        if (urbanFunction.getAreaClassificationType() != null && urbanFunction.getAreaClassificationType().isSetValue()) {
            ps.setString(4, urbanFunction.getAreaClassificationType().getValue());
            ps.setString(5, urbanFunction.getAreaClassificationType().getCodeSpace());
        } else {
            ps.setNull(4, Types.VARCHAR);
            ps.setNull(5, Types.VARCHAR);
        }

        if (urbanFunction.getCity() != null && urbanFunction.getCity().isSetValue()) {
            ps.setString(6, urbanFunction.getCity().getValue());
            ps.setString(7, urbanFunction.getCity().getCodeSpace());
        } else {
            ps.setNull(6, Types.VARCHAR);
            ps.setNull(7, Types.VARCHAR);
        }

        if (urbanFunction.getClassifier() != null && urbanFunction.getClassifier().isSetValue()) {
            ps.setString(8, urbanFunction.getClassifier().getValue());
            ps.setString(9, urbanFunction.getClassifier().getCodeSpace());
        } else {
            ps.setNull(8, Types.VARCHAR);
            ps.setNull(9, Types.VARCHAR);
        }

        ps.setString(10, urbanFunction.getCustodian());

        if (urbanFunction.getEnactmentDate() != null)
            ps.setDate(11, Date.valueOf(urbanFunction.getEnactmentDate()));
        else
            ps.setNull(11, Types.DATE);

        if (urbanFunction.getEnactmentFiscalYear() != null)
            ps.setDate(12, Date.valueOf(LocalDate.of(urbanFunction.getEnactmentFiscalYear().getValue(), 1, 1)));
        else
            ps.setNull(12, Types.DATE);

        if (urbanFunction.getExpirationDate() != null)
            ps.setDate(13, Date.valueOf(urbanFunction.getExpirationDate()));
        else
            ps.setNull(13, Types.DATE);

        if (urbanFunction.getExpirationFiscalYear() != null)
            ps.setDate(14, Date.valueOf(LocalDate.of(urbanFunction.getExpirationFiscalYear().getValue(), 1, 1)));
        else
            ps.setNull(14, Types.DATE);

        if (!urbanFunction.getFunctions().isEmpty()) {
            valueJoiner.join(urbanFunction.getFunctions(), Code::getValue, Code::getCodeSpace);
            ps.setString(15, valueJoiner.result(0));
            ps.setString(16, valueJoiner.result(1));
        } else {
            ps.setNull(15, Types.VARCHAR);
            ps.setNull(16, Types.VARCHAR);
        }

        long legalGroundsId = 0;
        if (urbanFunction.getLegalGrounds() != null && urbanFunction.getLegalGrounds().isSetObject())
            legalGroundsId = legalGroundsImporter.doImport(urbanFunction.getLegalGrounds().getObject());

        if (legalGroundsId != 0)
            ps.setLong(17, legalGroundsId);
        else
            ps.setNull(17, Types.NULL);

        for (int i = 0; i < 3; i++) {
            MultiPointProperty property = null;
            GeometryObject multiPoint = null;

            switch (i) {
                case 0:
                    property = urbanFunction.getLod0MultiPoint();
                    break;
                case 1:
                    property = urbanFunction.getLod1MultiPoint();
                    break;
                case 2:
                    property = urbanFunction.getLod2MultiPoint();
                    break;
            }

            if (property != null) {
                multiPoint = geometryConverter.getMultiPoint(property);
                property.unsetMultiPoint();
            }

            if (multiPoint != null)
                ps.setObject(18 + i, helper.getDatabaseAdapter().getGeometryConverter().getDatabaseObject(multiPoint, connection));
            else
                ps.setNull(18 + i, helper.getDatabaseAdapter().getGeometryConverter().getNullGeometryType(),
                        helper.getDatabaseAdapter().getGeometryConverter().getNullGeometryTypeName());
        }

        for (int i = 0; i < 3; i++) {
            MultiCurveProperty property = null;
            GeometryObject multiCurve = null;

            switch (i) {
                case 0:
                    property = urbanFunction.getLod0MultiCurve();
                    break;
                case 1:
                    property = urbanFunction.getLod1MultiCurve();
                    break;
                case 2:
                    property = urbanFunction.getLod2MultiCurve();
                    break;
            }

            if (property != null) {
                multiCurve = geometryConverter.getMultiCurve(property);
                property.unsetMultiCurve();
            }

            if (multiCurve != null)
                ps.setObject(21 + i, helper.getDatabaseAdapter().getGeometryConverter().getDatabaseObject(multiCurve, connection));
            else
                ps.setNull(21 + i, helper.getDatabaseAdapter().getGeometryConverter().getNullGeometryType(),
                        helper.getDatabaseAdapter().getGeometryConverter().getNullGeometryTypeName());
        }

        for (int i = 0; i < 3; i++) {
            MultiSurfaceProperty property = null;
            long surfaceGeometryId = 0;

            switch (i) {
                case 0:
                    property = urbanFunction.getLod0MultiSurface();
                    break;
                case 1:
                    property = urbanFunction.getLod1MultiSurface();
                    break;
                case 2:
                    property = urbanFunction.getLod2MultiSurface();
                    break;
            }

            if (property != null) {
                if (property.isSetMultiSurface()) {
                    surfaceGeometryId = helper.importSurfaceGeometry(property.getMultiSurface(), objectId);
                    property.unsetMultiSurface();
                } else {
                    String href = property.getHref();
                    if (href != null && !href.isEmpty()) {
                        String propertyColumn = i == 0 ?
                                "lod0multisurface_id" :
                                "lod_" + i + "multisurface_id";
                        helper.propagateSurfaceGeometryXlink(href, schemaMapper.getTableName(ADETable.URBANFUNCTION), objectId, propertyColumn);
                    }
                }
            }

            if (surfaceGeometryId != 0)
                ps.setLong(24 + i, surfaceGeometryId);
            else
                ps.setNull(24 + i, Types.NULL);
        }

        if (urbanFunction.getNominalArea() != null && urbanFunction.getNominalArea().isSetValue()) {
            ps.setDouble(27, urbanFunction.getNominalArea().getValue());
            ps.setString(28, urbanFunction.getNominalArea().getUom());
        } else {
            ps.setNull(27, Types.DOUBLE);
            ps.setNull(28, Types.VARCHAR);
        }

        ps.setString(29, urbanFunction.getNote());

        if (urbanFunction.getPrefecture() != null && urbanFunction.getPrefecture().isSetValue()) {
            ps.setString(30, urbanFunction.getPrefecture().getValue());
            ps.setString(31, urbanFunction.getPrefecture().getCodeSpace());
        } else {
            ps.setNull(30, Types.VARCHAR);
            ps.setNull(31, Types.VARCHAR);
        }

        ps.setString(32, urbanFunction.getReference());

        if (urbanFunction.getSurveyYear() != null)
            ps.setDate(33, Date.valueOf(LocalDate.of(urbanFunction.getSurveyYear().getValue(), 1, 1)));
        else
            ps.setNull(33, Types.DATE);

        if (urbanFunction.getUrbanPlanType() != null && urbanFunction.getUrbanPlanType().isSetValue()) {
            ps.setString(34, urbanFunction.getUrbanPlanType().getValue());
            ps.setString(35, urbanFunction.getUrbanPlanType().getCodeSpace());
        } else {
            ps.setNull(34, Types.VARCHAR);
            ps.setNull(35, Types.VARCHAR);
        }

        if (urbanFunction.getValidity() != null)
            ps.setInt(36, urbanFunction.getValidity() ? 1 : 0);
        else
            ps.setNull(36, Types.INTEGER);

        if (urbanFunction instanceof DisasterPreventionBase && ((DisasterPreventionBase) urbanFunction).getCapacity() != null)
            ps.setInt(37, ((DisasterPreventionBase) urbanFunction).getCapacity());
        else
            ps.setNull(37, Types.INTEGER);

        ps.addBatch();
        if (++batchCounter == helper.getDatabaseAdapter().getMaxBatchSize())
            helper.executeBatch(objectType);

        for (TargetProperty property : urbanFunction.getTargets()) {
            AbstractCityObject cityObject = property.getObject();
            if (cityObject != null) {
                long cityObjectId = helper.importObject(cityObject);
                property.unsetObject();
                urbanFunctionToCityObjectImporter.doImport(cityObjectId, objectId);
            } else {
                String href = property.getHref();
                if (href != null && !href.isEmpty()) {
                    helper.propagateObjectXlink(
                            schemaMapper.getTableName(ADETable.URBANFUNC_TO_CITYOBJEC),
                            objectId, "urbanfunction_id",
                            href, "cityobject_id");
                }
            }
        }
    }

    @Override
    public void executeBatch() throws CityGMLImportException, SQLException {
        if (batchCounter > 0) {
            ps.executeBatch();
            batchCounter = 0;
        }
    }

    @Override
    public void close() throws CityGMLImportException, SQLException {
        ps.close();
    }
}
