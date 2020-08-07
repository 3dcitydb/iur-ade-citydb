package org.citydb.ade.iur.importer.urf;

import org.citydb.ade.importer.ADEImporter;
import org.citydb.ade.importer.CityGMLImportHelper;
import org.citydb.ade.importer.ForeignKeys;
import org.citydb.ade.iur.importer.ImportManager;
import org.citydb.ade.iur.schema.ADETable;
import org.citydb.citygml.importer.CityGMLImportException;
import org.citydb.database.schema.mapping.AbstractObjectType;
import org.citygml4j.ade.iur.model.urf.DevelopmentProject;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class DevelopmentProjectImporter implements ADEImporter {
    private final CityGMLImportHelper helper;
    private final PreparedStatement ps;
    private final UrbanFunctionImporter urbanFunctionImporter;

    private int batchCounter;

    public DevelopmentProjectImporter(Connection connection, CityGMLImportHelper helper, ImportManager manager) throws CityGMLImportException, SQLException {
        this.helper = helper;

        ps = connection.prepareStatement("insert into " +
                helper.getTableNameWithSchema(manager.getSchemaMapper().getTableName(ADETable.DEVELOPMENTPROJECT)) + " " +
                "(id, benefitarea, benefitarea_uom, benefitperiod, completedarea, completedarea_uom, cost, dateofdecision, " +
                "dateofdesignationfortemporar, mainpurpose, mainpurpose_codespace, ongoingarea, ongoingarea_uom, " +
                "plannedarea, plannedarea_uom, status, status_codespace) " +
                "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

        urbanFunctionImporter = manager.getImporter(UrbanFunctionImporter.class);
    }

    public void doImport(DevelopmentProject developmentProject, long objectId, AbstractObjectType<?> objectType, ForeignKeys foreignKeys) throws CityGMLImportException, SQLException {
        urbanFunctionImporter.doImport(developmentProject, objectId, objectType, foreignKeys);
        ps.setLong(1, objectId);

        if (developmentProject.getBenefitArea() != null && developmentProject.getBenefitArea().isSetValue()) {
            ps.setDouble(2, developmentProject.getBenefitArea().getValue());
            ps.setString(3, developmentProject.getBenefitArea().getUom());
        } else {
            ps.setNull(2, Types.DOUBLE);
            ps.setNull(3, Types.VARCHAR);
        }

        ps.setString(4, developmentProject.getBenefitPeriod());

        if (developmentProject.getCompletedArea() != null && developmentProject.getCompletedArea().isSetValue()) {
            ps.setDouble(5, developmentProject.getCompletedArea().getValue());
            ps.setString(6, developmentProject.getCompletedArea().getUom());
        } else {
            ps.setNull(5, Types.DOUBLE);
            ps.setNull(6, Types.VARCHAR);
        }

        if (developmentProject.getCost() != null)
            ps.setInt(7, developmentProject.getCost());
        else
            ps.setNull(7, Types.INTEGER);

        if (developmentProject.getDateOfDecision() != null)
            ps.setDate(8, Date.valueOf(developmentProject.getDateOfDecision()));
        else
            ps.setNull(8, Types.DATE);

        if (developmentProject.getDateOfDesignationForTemporaryReplotting() != null)
            ps.setDate(9, Date.valueOf(developmentProject.getDateOfDesignationForTemporaryReplotting()));
        else
            ps.setNull(9, Types.DATE);

        if (developmentProject.getMainPurpose() != null && developmentProject.getMainPurpose().isSetValue()) {
            ps.setString(10, developmentProject.getMainPurpose().getValue());
            ps.setString(11, developmentProject.getMainPurpose().getCodeSpace());
        } else {
            ps.setNull(10, Types.VARCHAR);
            ps.setNull(11, Types.VARCHAR);
        }

        if (developmentProject.getOngoingArea() != null && developmentProject.getOngoingArea().isSetValue()) {
            ps.setDouble(12, developmentProject.getOngoingArea().getValue());
            ps.setString(13, developmentProject.getOngoingArea().getUom());
        } else {
            ps.setNull(12, Types.DOUBLE);
            ps.setNull(13, Types.VARCHAR);
        }

        if (developmentProject.getPlannedArea() != null && developmentProject.getPlannedArea().isSetValue()) {
            ps.setDouble(14, developmentProject.getPlannedArea().getValue());
            ps.setString(15, developmentProject.getPlannedArea().getUom());
        } else {
            ps.setNull(14, Types.DOUBLE);
            ps.setNull(15, Types.VARCHAR);
        }

        if (developmentProject.getStatus() != null && developmentProject.getStatus().isSetValue()) {
            ps.setString(16, developmentProject.getStatus().getValue());
            ps.setString(17, developmentProject.getStatus().getCodeSpace());
        } else {
            ps.setNull(16, Types.VARCHAR);
            ps.setNull(17, Types.VARCHAR);
        }

        ps.addBatch();
        if (++batchCounter == helper.getDatabaseAdapter().getMaxBatchSize())
            helper.executeBatch(objectType);
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
