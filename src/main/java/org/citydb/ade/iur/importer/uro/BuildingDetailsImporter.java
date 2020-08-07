package org.citydb.ade.iur.importer.uro;

import org.citydb.ade.importer.ADEImporter;
import org.citydb.ade.importer.CityGMLImportHelper;
import org.citydb.citygml.importer.CityGMLImportException;
import org.citydb.ade.iur.importer.ImportManager;
import org.citydb.ade.iur.schema.ADESequence;
import org.citydb.ade.iur.schema.ADETable;
import org.citydb.ade.iur.schema.SchemaMapper;
import org.citygml4j.ade.iur.model.uro.BuildingDetails;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;

public class BuildingDetailsImporter implements ADEImporter {
    private final CityGMLImportHelper helper;
    private final SchemaMapper schemaMapper;
    private final PreparedStatement ps;

    private int batchCounter;

    public BuildingDetailsImporter(Connection connection, CityGMLImportHelper helper, ImportManager manager) throws CityGMLImportException, SQLException {
        this.helper = helper;
        this.schemaMapper = manager.getSchemaMapper();

        ps = connection.prepareStatement("insert into " +
                helper.getTableNameWithSchema(schemaMapper.getTableName(ADETable.BUILDINGDETAILS)) + " " +
                "(id, areaclassificationtype, areaclassification_codespace, buildingfootprintarea, buildingfootprintarea_uom, " +
                "buildingroofedgearea, buildingroofedgearea_uom, buildingstructuretype, buildingstructuret_codespace, " +
                "city, city_codespace, developmentarea, developmentarea_uom, districtsandzonestype, districtsandzonest_codespace, " +
                "fireproofstructuretype, fireproofstructure_codespace, implementingbody, landuseplantype, " +
                "landuseplantype_codespace, note, prefecture, prefecture_codespace, reference, serialnumberofbuildingcertif, " +
                "sitearea, sitearea_uom, surveyyear, totalfloorarea, totalfloorarea_uom, urbanplantype, urbanplantype_codespace) " +
                "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
    }

    public long doImport(BuildingDetails buildingDetails) throws CityGMLImportException, SQLException {
        long objectId = helper.getNextSequenceValue(schemaMapper.getSequenceName(ADESequence.BUILDINGDETAILS_SEQ));
        ps.setLong(1, objectId);

        if (buildingDetails.getAreaClassificationType() != null && buildingDetails.getAreaClassificationType().isSetValue()) {
            ps.setString(2, buildingDetails.getAreaClassificationType().getValue());
            ps.setString(3, buildingDetails.getAreaClassificationType().getCodeSpace());
        } else {
            ps.setNull(2, Types.VARCHAR);
            ps.setNull(3, Types.VARCHAR);
        }

        if (buildingDetails.getBuildingFootprintArea() != null && buildingDetails.getBuildingFootprintArea().isSetValue()) {
            ps.setDouble(4, buildingDetails.getBuildingFootprintArea().getValue());
            ps.setString(5, buildingDetails.getBuildingFootprintArea().getUom());
        } else {
            ps.setNull(4, Types.DOUBLE);
            ps.setNull(5, Types.VARCHAR);
        }

        if (buildingDetails.getBuildingRoofEdgeArea() != null && buildingDetails.getBuildingRoofEdgeArea().isSetValue()) {
            ps.setDouble(6, buildingDetails.getBuildingRoofEdgeArea().getValue());
            ps.setString(7, buildingDetails.getBuildingRoofEdgeArea().getUom());
        } else {
            ps.setNull(6, Types.DOUBLE);
            ps.setNull(7, Types.VARCHAR);
        }

        if (buildingDetails.getBuildingStructureType() != null && buildingDetails.getBuildingStructureType().isSetValue()) {
            ps.setString(8, buildingDetails.getBuildingStructureType().getValue());
            ps.setString(9, buildingDetails.getBuildingStructureType().getCodeSpace());
        } else {
            ps.setNull(8, Types.VARCHAR);
            ps.setNull(9, Types.VARCHAR);
        }

        if (buildingDetails.getCity() != null && buildingDetails.getCity().isSetValue()) {
            ps.setString(10, buildingDetails.getCity().getValue());
            ps.setString(11, buildingDetails.getCity().getCodeSpace());
        } else {
            ps.setNull(10, Types.VARCHAR);
            ps.setNull(11, Types.VARCHAR);
        }

        if (buildingDetails.getDevelopmentArea() != null && buildingDetails.getDevelopmentArea().isSetValue()) {
            ps.setDouble(12, buildingDetails.getDevelopmentArea().getValue());
            ps.setString(13, buildingDetails.getDevelopmentArea().getUom());
        } else {
            ps.setNull(12, Types.DOUBLE);
            ps.setNull(13, Types.VARCHAR);
        }

        if (buildingDetails.getDistrictsAndZonesType() != null && buildingDetails.getDistrictsAndZonesType().isSetValue()) {
            ps.setString(14, buildingDetails.getDistrictsAndZonesType().getValue());
            ps.setString(15, buildingDetails.getDistrictsAndZonesType().getCodeSpace());
        } else {
            ps.setNull(14, Types.VARCHAR);
            ps.setNull(15, Types.VARCHAR);
        }

        if (buildingDetails.getFireproofStructureType() != null && buildingDetails.getFireproofStructureType().isSetValue()) {
            ps.setString(16, buildingDetails.getFireproofStructureType().getValue());
            ps.setString(17, buildingDetails.getFireproofStructureType().getCodeSpace());
        } else {
            ps.setNull(16, Types.VARCHAR);
            ps.setNull(17, Types.VARCHAR);
        }

        ps.setString(18, buildingDetails.getImplementingBody());

        if (buildingDetails.getLandUsePlanType() != null && buildingDetails.getLandUsePlanType().isSetValue()) {
            ps.setString(19, buildingDetails.getLandUsePlanType().getValue());
            ps.setString(20, buildingDetails.getLandUsePlanType().getCodeSpace());
        } else {
            ps.setNull(19, Types.VARCHAR);
            ps.setNull(20, Types.VARCHAR);
        }

        ps.setString(21, buildingDetails.getNote());

        if (buildingDetails.getPrefecture() != null && buildingDetails.getPrefecture().isSetValue()) {
            ps.setString(22, buildingDetails.getPrefecture().getValue());
            ps.setString(23, buildingDetails.getPrefecture().getCodeSpace());
        } else {
            ps.setNull(22, Types.VARCHAR);
            ps.setNull(23, Types.VARCHAR);
        }

        ps.setString(24, buildingDetails.getReference());
        ps.setString(25, buildingDetails.getSerialNumberOfBuildingCertification());

        if (buildingDetails.getSiteArea() != null && buildingDetails.getSiteArea().isSetValue()) {
            ps.setDouble(26, buildingDetails.getSiteArea().getValue());
            ps.setString(27, buildingDetails.getSiteArea().getUom());
        } else {
            ps.setNull(26, Types.DOUBLE);
            ps.setNull(27, Types.VARCHAR);
        }

        if (buildingDetails.getSurveyYear() != null)
            ps.setDate(28, Date.valueOf(LocalDate.of(buildingDetails.getSurveyYear().getValue(), 1, 1)));
        else
            ps.setNull(28, Types.DATE);

        if (buildingDetails.getTotalFloorArea() != null && buildingDetails.getTotalFloorArea().isSetValue()) {
            ps.setDouble(29, buildingDetails.getTotalFloorArea().getValue());
            ps.setString(30, buildingDetails.getTotalFloorArea().getUom());
        } else {
            ps.setNull(29, Types.DOUBLE);
            ps.setNull(30, Types.VARCHAR);
        }

        if (buildingDetails.getUrbanPlanType() != null && buildingDetails.getUrbanPlanType().isSetValue()) {
            ps.setString(31, buildingDetails.getUrbanPlanType().getValue());
            ps.setString(32, buildingDetails.getUrbanPlanType().getCodeSpace());
        } else {
            ps.setNull(31, Types.VARCHAR);
            ps.setNull(32, Types.VARCHAR);
        }

        ps.addBatch();
        if (++batchCounter == helper.getDatabaseAdapter().getMaxBatchSize())
            helper.executeBatch(schemaMapper.getTableName(ADETable.BUILDINGDETAILS));

        return objectId;
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
