package org.citydb.ade.iur.importer.urf;

import org.citydb.ade.importer.ADEImporter;
import org.citydb.ade.importer.CityGMLImportHelper;
import org.citydb.ade.importer.ForeignKeys;
import org.citydb.citygml.importer.CityGMLImportException;
import org.citydb.citygml.importer.database.content.GeometryConverter;
import org.citydb.citygml.importer.util.AttributeValueJoiner;
import org.citydb.config.geometry.GeometryObject;
import org.citydb.database.schema.mapping.AbstractObjectType;
import org.citygml4j.model.citygml.core.AbstractCityObject;
import org.citygml4j.model.gml.basicTypes.Code;
import org.citygml4j.model.gml.geometry.aggregates.MultiSurfaceProperty;
import org.citydb.ade.iur.importer.ImportManager;
import org.citydb.ade.iur.schema.ADETable;
import org.citydb.ade.iur.schema.SchemaMapper;
import org.citygml4j.ade.iur.model.urf.DisasterPreventionBase;
import org.citygml4j.ade.iur.model.urf.TargetProperty;
import org.citygml4j.ade.iur.model.urf.UrbanFunction;
import org.citygml4j.ade.iur.model.urf.Zone;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;

public class UrbanFunctionImporter implements ADEImporter {
    private final Connection connection;
    private final CityGMLImportHelper helper;
    private final SchemaMapper schemaMapper;
    private final PreparedStatement psUrbanFunction;
    private final PreparedStatement psZone;

    private final LegalGroundsImporter legalGroundsImporter;
    private final UrbanFunctionToCityObjectImporter urbanFunctionToCityObjectImporter;
    private final GeometryConverter geometryConverter;
    private final AttributeValueJoiner valueJoiner;

    private int batchCounter;

    public UrbanFunctionImporter(Connection connection, CityGMLImportHelper helper, ImportManager manager) throws CityGMLImportException, SQLException {
        this.connection = connection;
        this.helper = helper;
        this.schemaMapper = manager.getSchemaMapper();

        psUrbanFunction = connection.prepareStatement("insert into " +
                helper.getTableNameWithSchema(schemaMapper.getTableName(ADETable.URBANFUNCTION)) + " " +
                "(id, objectclass_id, abstract, area_id, areaclassificationtype, areaclassification_codespace, boundary, " +
                "city, city_codespace, class, class_codespace, custodian, enactmentdate, enactmentfiscalyear, " +
                "expirationdate, expirationfiscalyear, function, function_codespace, legalgrounds_id, location, " +
                "nominalarea, nominalarea_uom, note, prefecture, prefecture_codespace, reference, surveyyear, urbanplantype, " +
                "urbanplantype_codespace, validity, capacity) " +
                "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

        psZone = connection.prepareStatement("insert into " +
                helper.getTableNameWithSchema(schemaMapper.getTableName(ADETable.ZONE)) + " " +
                "(id, objectclass_id, areaapplied, finalpublicationdate) " +
                "values (?, ?, ?, ?)");

        legalGroundsImporter = manager.getImporter(LegalGroundsImporter.class);
        urbanFunctionToCityObjectImporter = manager.getImporter(UrbanFunctionToCityObjectImporter.class);
        geometryConverter = helper.getGeometryConverter();
        valueJoiner = helper.getAttributeValueJoiner();
    }

    public void doImport(UrbanFunction urbanFunction, long objectId, AbstractObjectType<?> objectType, ForeignKeys foreignKeys) throws CityGMLImportException, SQLException {
        psUrbanFunction.setLong(1, objectId);
        psUrbanFunction.setInt(2, objectType.getObjectClassId());

        psUrbanFunction.setString(3, urbanFunction.getAbstract());

        long areaId = 0;
        if (urbanFunction.getArea() != null) {
            MultiSurfaceProperty property = urbanFunction.getArea();
            if (property.isSetMultiSurface()) {
                areaId = helper.importSurfaceGeometry(property.getMultiSurface(), objectId);
                property.unsetMultiSurface();
            } else {
                String href = property.getHref();
                if (href != null && !href.isEmpty())
                    helper.propagateSurfaceGeometryXlink(href, schemaMapper.getTableName(ADETable.URBANFUNCTION), objectId, "area_id");
            }
        }

        if (areaId != 0)
            psUrbanFunction.setLong(4, areaId);
        else
            psUrbanFunction.setNull(4, Types.NULL);

        if (urbanFunction.getAreaClassificationType() != null && urbanFunction.getAreaClassificationType().isSetValue()) {
            psUrbanFunction.setString(5, urbanFunction.getAreaClassificationType().getValue());
            psUrbanFunction.setString(6, urbanFunction.getAreaClassificationType().getCodeSpace());
        } else {
            psUrbanFunction.setNull(5, Types.VARCHAR);
            psUrbanFunction.setNull(6, Types.VARCHAR);
        }

        GeometryObject boundary = null;
        if (urbanFunction.getBoundary() != null && urbanFunction.getBoundary().isSetMultiCurve()) {
            boundary = geometryConverter.getMultiCurve(urbanFunction.getBoundary().getMultiCurve());
            urbanFunction.setBoundary(null);
        }

        if (boundary != null)
            psUrbanFunction.setObject(7, helper.getDatabaseAdapter().getGeometryConverter().getDatabaseObject(boundary, connection));
        else
            psUrbanFunction.setNull(7, helper.getDatabaseAdapter().getGeometryConverter().getNullGeometryType(),
                    helper.getDatabaseAdapter().getGeometryConverter().getNullGeometryTypeName());

        if (urbanFunction.getCity() != null && urbanFunction.getCity().isSetValue()) {
            psUrbanFunction.setString(8, urbanFunction.getCity().getValue());
            psUrbanFunction.setString(9, urbanFunction.getCity().getCodeSpace());
        } else {
            psUrbanFunction.setNull(8, Types.VARCHAR);
            psUrbanFunction.setNull(9, Types.VARCHAR);
        }

        if (urbanFunction.getClassifier() != null && urbanFunction.getClassifier().isSetValue()) {
            psUrbanFunction.setString(10, urbanFunction.getClassifier().getValue());
            psUrbanFunction.setString(11, urbanFunction.getClassifier().getCodeSpace());
        } else {
            psUrbanFunction.setNull(10, Types.VARCHAR);
            psUrbanFunction.setNull(11, Types.VARCHAR);
        }

        psUrbanFunction.setString(12, urbanFunction.getCustodian());

        if (urbanFunction.getEnactmentDate() != null)
            psUrbanFunction.setDate(13, Date.valueOf(urbanFunction.getEnactmentDate()));
        else
            psUrbanFunction.setNull(13, Types.DATE);

        if (urbanFunction.getEnactmentFiscalYear() != null)
            psUrbanFunction.setDate(14, Date.valueOf(LocalDate.of(urbanFunction.getEnactmentFiscalYear().getValue(), 1, 1)));
        else
            psUrbanFunction.setNull(14, Types.DATE);

        if (urbanFunction.getExpirationDate() != null)
            psUrbanFunction.setDate(15, Date.valueOf(urbanFunction.getExpirationDate()));
        else
            psUrbanFunction.setNull(15, Types.DATE);

        if (urbanFunction.getExpirationFiscalYear() != null)
            psUrbanFunction.setDate(16, Date.valueOf(LocalDate.of(urbanFunction.getExpirationFiscalYear().getValue(), 1, 1)));
        else
            psUrbanFunction.setNull(16, Types.DATE);

        if (!urbanFunction.getFunctions().isEmpty()) {
            valueJoiner.join(urbanFunction.getFunctions(), Code::getValue, Code::getCodeSpace);
            psUrbanFunction.setString(17, valueJoiner.result(0));
            psUrbanFunction.setString(18, valueJoiner.result(1));
        } else {
            psUrbanFunction.setNull(17, Types.VARCHAR);
            psUrbanFunction.setNull(18, Types.VARCHAR);
        }

        long legalGroundsId = 0;
        if (urbanFunction.getLegalGrounds() != null && urbanFunction.getLegalGrounds().isSetObject())
            legalGroundsId = legalGroundsImporter.doImport(urbanFunction.getLegalGrounds().getObject());

        if (legalGroundsId != 0)
            psUrbanFunction.setLong(19, legalGroundsId);
        else
            psUrbanFunction.setNull(19, Types.NULL);

        GeometryObject pointLocation = null;
        if (urbanFunction.getPointLocation() != null && urbanFunction.getPointLocation().isSetMultiPoint()) {
            pointLocation = geometryConverter.getMultiPoint(urbanFunction.getPointLocation().getMultiPoint());
            urbanFunction.setPointLocation(null);
        }

        if (pointLocation != null)
            psUrbanFunction.setObject(20, helper.getDatabaseAdapter().getGeometryConverter().getDatabaseObject(pointLocation, connection));
        else
            psUrbanFunction.setNull(20, helper.getDatabaseAdapter().getGeometryConverter().getNullGeometryType(),
                    helper.getDatabaseAdapter().getGeometryConverter().getNullGeometryTypeName());

        if (urbanFunction.getNominalArea() != null && urbanFunction.getNominalArea().isSetValue()) {
            psUrbanFunction.setDouble(21, urbanFunction.getNominalArea().getValue());
            psUrbanFunction.setString(22, urbanFunction.getNominalArea().getUom());
        } else {
            psUrbanFunction.setNull(21, Types.DOUBLE);
            psUrbanFunction.setNull(22, Types.VARCHAR);
        }

        psUrbanFunction.setString(23, urbanFunction.getNote());

        if (urbanFunction.getPrefecture() != null && urbanFunction.getPrefecture().isSetValue()) {
            psUrbanFunction.setString(24, urbanFunction.getPrefecture().getValue());
            psUrbanFunction.setString(25, urbanFunction.getPrefecture().getCodeSpace());
        } else {
            psUrbanFunction.setNull(24, Types.VARCHAR);
            psUrbanFunction.setNull(25, Types.VARCHAR);
        }

        psUrbanFunction.setString(26, urbanFunction.getReference());

        if (urbanFunction.getSurveyYear() != null)
            psUrbanFunction.setDate(27, Date.valueOf(LocalDate.of(urbanFunction.getSurveyYear().getValue(), 1, 1)));
        else
            psUrbanFunction.setNull(27, Types.DATE);

        if (urbanFunction.getUrbanPlanType() != null && urbanFunction.getUrbanPlanType().isSetValue()) {
            psUrbanFunction.setString(28, urbanFunction.getUrbanPlanType().getValue());
            psUrbanFunction.setString(29, urbanFunction.getUrbanPlanType().getCodeSpace());
        } else {
            psUrbanFunction.setNull(28, Types.VARCHAR);
            psUrbanFunction.setNull(29, Types.VARCHAR);
        }

        if (urbanFunction.getValidity() != null)
            psUrbanFunction.setInt(30, urbanFunction.getValidity() ? 1 : 0);
        else
            psUrbanFunction.setNull(30, Types.INTEGER);

        if (urbanFunction instanceof DisasterPreventionBase && ((DisasterPreventionBase) urbanFunction).getCapacity() != null)
            psUrbanFunction.setInt(31, ((DisasterPreventionBase) urbanFunction).getCapacity());
        else
            psUrbanFunction.setNull(31, Types.INTEGER);

        psUrbanFunction.addBatch();

        for (TargetProperty property : urbanFunction.getTargets()) {
            AbstractCityObject cityObject = property.getCityObject();
            if (cityObject != null) {
                long cityObjectId = helper.importObject(cityObject);
                property.unsetCityObject();
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

        if (urbanFunction instanceof Zone) {
            Zone zone = (Zone) urbanFunction;

            psZone.setLong(1, objectId);
            psZone.setInt(2, objectType.getObjectClassId());

            psZone.setString(3, zone.getAreaApplied());

            if (zone.getFinalPublicationDate() != null)
                psZone.setDate(4, Date.valueOf(zone.getFinalPublicationDate()));
            else
                psZone.setNull(4, Types.DATE);

            psZone.addBatch();
        }

        if (++batchCounter == helper.getDatabaseAdapter().getMaxBatchSize())
            helper.executeBatch(objectType);
    }

    @Override
    public void executeBatch() throws CityGMLImportException, SQLException {
        if (batchCounter > 0) {
            psUrbanFunction.executeBatch();
            psZone.executeBatch();
            batchCounter = 0;
        }
    }

    @Override
    public void close() throws CityGMLImportException, SQLException {
        psUrbanFunction.close();
        psZone.close();
    }
}
