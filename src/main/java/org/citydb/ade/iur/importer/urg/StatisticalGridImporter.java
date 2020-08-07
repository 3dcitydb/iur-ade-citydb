package org.citydb.ade.iur.importer.urg;

import org.citydb.ade.importer.ADEImporter;
import org.citydb.ade.importer.CityGMLImportHelper;
import org.citydb.ade.importer.ForeignKeys;
import org.citydb.citygml.importer.CityGMLImportException;
import org.citydb.citygml.importer.util.AttributeValueJoiner;
import org.citydb.database.schema.mapping.AbstractObjectType;
import org.citygml4j.model.gml.geometry.aggregates.MultiSurfaceProperty;
import org.w3c.dom.Element;
import org.citydb.ade.iur.importer.ImportManager;
import org.citydb.ade.iur.schema.ADETable;
import org.citygml4j.ade.iur.model.urg.LandPrice;
import org.citygml4j.ade.iur.model.urg.LandPricePerLandUseProperty;
import org.citygml4j.ade.iur.model.urg.LandUseDiversion;
import org.citygml4j.ade.iur.model.urg.NumberOfAnnualDiversionsProperty;
import org.citygml4j.ade.iur.model.urg.PublicTransportationAccessibility;
import org.citygml4j.ade.iur.model.urg.StatisticalGrid;

import javax.xml.transform.TransformerException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class StatisticalGridImporter implements ADEImporter {
    private final CityGMLImportHelper helper;
    private final ImportManager manager;
    private final PreparedStatement ps;

    private final LandPricePerLandUseImporter landPricePerLandUseImporter;
    private final NumberOfAnnualDiversionsImporter numberOfAnnualDiversionsImporter;
    private final AttributeValueJoiner valueJoiner;

    private int batchCounter;

    public StatisticalGridImporter(Connection connection, CityGMLImportHelper helper, ImportManager manager) throws CityGMLImportException, SQLException {
        this.helper = helper;
        this.manager = manager;

        ps = connection.prepareStatement("insert into " +
                helper.getTableNameWithSchema(manager.getSchemaMapper().getTableName(ADETable.STATISTICALGRID)) + " " +
                "(id, objectclass_id, areaclassificationtype, areaclassification_codespace, city, city_codespace, " +
                "class, class_codespace, lod_1multisurfacegeometry_id, lod_2multisurfacegeometry_id, " +
                "prefecture, prefecture_codespace, surveyyear, urbanplantype, urbanplantype_codespace, value, availability) " +
                "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

        landPricePerLandUseImporter = manager.getImporter(LandPricePerLandUseImporter.class);
        numberOfAnnualDiversionsImporter = manager.getImporter(NumberOfAnnualDiversionsImporter.class);
        valueJoiner = helper.getAttributeValueJoiner();
    }

    public void doImport(StatisticalGrid statisticalGrid, long objectId, AbstractObjectType<?> objectType, ForeignKeys foreignKeys) throws CityGMLImportException, SQLException {
        ps.setLong(1, objectId);
        ps.setInt(2, objectType.getObjectClassId());

        if (statisticalGrid.getAreaClassificationType() != null && statisticalGrid.getAreaClassificationType().isSetValue()) {
            ps.setString(3, statisticalGrid.getAreaClassificationType().getValue());
            ps.setString(4, statisticalGrid.getAreaClassificationType().getCodeSpace());
        } else {
            ps.setNull(3, Types.VARCHAR);
            ps.setNull(4, Types.VARCHAR);
        }

        if (statisticalGrid.getCity() != null && statisticalGrid.getCity().isSetValue()) {
            ps.setString(5, statisticalGrid.getCity().getValue());
            ps.setString(6, statisticalGrid.getCity().getCodeSpace());
        } else {
            ps.setNull(5, Types.VARCHAR);
            ps.setNull(6, Types.VARCHAR);
        }

        if (statisticalGrid.getClassifier() != null && statisticalGrid.getClassifier().isSetValue()) {
            ps.setString(7, statisticalGrid.getClassifier().getValue());
            ps.setString(8, statisticalGrid.getClassifier().getCodeSpace());
        } else {
            ps.setNull(7, Types.VARCHAR);
            ps.setNull(8, Types.VARCHAR);
        }

        long lod1MultiSurfaceId = 0;
        if (statisticalGrid.getLod1MultiSurfaceGeometry() != null) {
            MultiSurfaceProperty property = statisticalGrid.getLod1MultiSurfaceGeometry();
            if (property.isSetMultiSurface()) {
                lod1MultiSurfaceId = helper.importSurfaceGeometry(property.getMultiSurface(), objectId);
                property.unsetMultiSurface();
            } else {
                String href = property.getHref();
                if (href != null && !href.isEmpty())
                    helper.propagateSurfaceGeometryXlink(href, manager.getSchemaMapper().getTableName(ADETable.STATISTICALGRID), objectId, "lod_1multisurfacegeometry_id");
            }
        }

        if (lod1MultiSurfaceId != 0)
            ps.setLong(9, lod1MultiSurfaceId);
        else
            ps.setNull(9, Types.NULL);

        long lod2MultiSurfaceId = 0;
        if (statisticalGrid.getLod2MultiSurfaceGeometry() != null) {
            MultiSurfaceProperty property = statisticalGrid.getLod2MultiSurfaceGeometry();
            if (property.isSetMultiSurface()) {
                lod2MultiSurfaceId = helper.importSurfaceGeometry(property.getMultiSurface(), objectId);
                property.unsetMultiSurface();
            } else {
                String href = property.getHref();
                if (href != null && !href.isEmpty())
                    helper.propagateSurfaceGeometryXlink(href, manager.getSchemaMapper().getTableName(ADETable.STATISTICALGRID), objectId, "lod_2multisurfacegeometry_id");
            }
        }

        if (lod2MultiSurfaceId != 0)
            ps.setLong(10, lod2MultiSurfaceId);
        else
            ps.setNull(10, Types.NULL);

        if (statisticalGrid.getPrefecture() != null && statisticalGrid.getPrefecture().isSetValue()) {
            ps.setString(11, statisticalGrid.getPrefecture().getValue());
            ps.setString(12, statisticalGrid.getPrefecture().getCodeSpace());
        } else {
            ps.setNull(11, Types.VARCHAR);
            ps.setNull(12, Types.VARCHAR);
        }

        if (statisticalGrid.getSurveyYear() != null)
            ps.setDate(13, Date.valueOf(LocalDate.of(statisticalGrid.getSurveyYear().getValue(), 1, 1)));
        else
            ps.setNull(13, Types.DATE);

        if (statisticalGrid.getUrbanPlanType() != null && statisticalGrid.getUrbanPlanType().isSetValue()) {
            ps.setString(14, statisticalGrid.getUrbanPlanType().getValue());
            ps.setString(15, statisticalGrid.getUrbanPlanType().getCodeSpace());
        } else {
            ps.setNull(14, Types.VARCHAR);
            ps.setNull(15, Types.VARCHAR);
        }

        List<String> values = new ArrayList<>();
        try {
            for (Iterator<Element> iter = statisticalGrid.getValues().iterator(); iter.hasNext(); ) {
                Element element = iter.next();
                if (element.hasChildNodes())
                    values.add(manager.marshal(element.getFirstChild()));

                iter.remove();
            }
        } catch (TransformerException e) {
            throw new CityGMLImportException("Failed to parse generic xs:anyType value.", e);
        }

        if (!values.isEmpty())
            ps.setString(16, valueJoiner.join(values));
        else
            ps.setNull(16, Types.VARCHAR);

        if (statisticalGrid instanceof PublicTransportationAccessibility && ((PublicTransportationAccessibility) statisticalGrid).getAvailability() != null)
            ps.setInt(17, ((PublicTransportationAccessibility) statisticalGrid).getAvailability() ? 1 : 0);
        else
            ps.setNull(17, Types.INTEGER);

        ps.addBatch();
        if (++batchCounter == helper.getDatabaseAdapter().getMaxBatchSize())
            helper.executeBatch(objectType);

        if (statisticalGrid instanceof LandPrice) {
            LandPrice landPrice = (LandPrice) statisticalGrid;
            for (LandPricePerLandUseProperty property : landPrice.getLandPrices()) {
                if (property.isSetObject())
                    landPricePerLandUseImporter.doImport(property.getObject(), objectId);
            }
        } else if (statisticalGrid instanceof LandUseDiversion) {
            LandUseDiversion landUseDiversion = (LandUseDiversion) statisticalGrid;
            for (NumberOfAnnualDiversionsProperty property : landUseDiversion.getNumberOfAnnualDiversions()) {
                if (property.isSetObject())
                    numberOfAnnualDiversionsImporter.doImport(property.getObject(), objectId);
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
