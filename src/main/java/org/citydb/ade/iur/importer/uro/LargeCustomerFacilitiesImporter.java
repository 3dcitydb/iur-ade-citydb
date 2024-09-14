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

package org.citydb.ade.iur.importer.uro;

import org.citydb.ade.iur.importer.ImportManager;
import org.citydb.ade.iur.schema.ADESequence;
import org.citydb.ade.iur.schema.ADETable;
import org.citydb.ade.iur.schema.SchemaMapper;
import org.citydb.core.ade.importer.CityGMLImportHelper;
import org.citydb.core.operation.importer.CityGMLImportException;
import org.citygml4j.ade.iur.model.uro.LargeCustomerFacilities;

import java.sql.*;
import java.time.LocalDate;

public class LargeCustomerFacilitiesImporter implements UrbanObjectModuleImporter {
    private final CityGMLImportHelper helper;
    private final SchemaMapper schemaMapper;
    private final PreparedStatement ps;

    private int batchCounter;

    public LargeCustomerFacilitiesImporter(Connection connection, CityGMLImportHelper helper, ImportManager manager) throws CityGMLImportException, SQLException {
        this.helper = helper;
        this.schemaMapper = manager.getSchemaMapper();

        ps = connection.prepareStatement("insert into " +
                helper.getTableNameWithSchema(schemaMapper.getTableName(ADETable.LARGECUSTOMERFACILITIE)) + " " +
                "(id, areaclassificationtype, areaclassification_codespace, availability, capacity, city, city_codespace, " +
                "class, class_codespace, districtsandzonestype, districtsandzonest_codespace, inauguraldate, keytenants, " +
                "landuseplantype, landuseplantype_codespace, name, note, owner, prefecture, prefecture_codespace, " +
                "reference, surveyyear, totalfloorarea, totalfloorarea_uom, totalstorefloorarea, totalstorefloorarea_uom, " +
                "urbanplantype, urbanplantype_codespace) " +
                "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
    }

    public long doImport(LargeCustomerFacilities largeCustomerFacilities) throws CityGMLImportException, SQLException {
        long objectId = helper.getNextSequenceValue(schemaMapper.getSequenceName(ADESequence.LARGECUSTOMERFACIL_SEQ));
        ps.setLong(1, objectId);

        if (largeCustomerFacilities.getAreaClassificationType() != null && largeCustomerFacilities.getAreaClassificationType().isSetValue()) {
            ps.setString(2, largeCustomerFacilities.getAreaClassificationType().getValue());
            ps.setString(3, largeCustomerFacilities.getAreaClassificationType().getCodeSpace());
        } else {
            ps.setNull(2, Types.VARCHAR);
            ps.setNull(3, Types.VARCHAR);
        }

        if (largeCustomerFacilities.getAvailability() != null)
            ps.setInt(4, largeCustomerFacilities.getAvailability() ? 1 : 0);
        else
            ps.setNull(4, Types.INTEGER);

        if (largeCustomerFacilities.getCapacity() != null)
            ps.setInt(5, largeCustomerFacilities.getCapacity());
        else
            ps.setNull(5, Types.INTEGER);

        if (largeCustomerFacilities.getCity() != null && largeCustomerFacilities.getCity().isSetValue()) {
            ps.setString(6, largeCustomerFacilities.getCity().getValue());
            ps.setString(7, largeCustomerFacilities.getCity().getCodeSpace());
        } else {
            ps.setNull(6, Types.VARCHAR);
            ps.setNull(7, Types.VARCHAR);
        }

        if (largeCustomerFacilities.getClassifier() != null && largeCustomerFacilities.getClassifier().isSetValue()) {
            ps.setString(8, largeCustomerFacilities.getClassifier().getValue());
            ps.setString(9, largeCustomerFacilities.getClassifier().getCodeSpace());
        } else {
            ps.setNull(8, Types.VARCHAR);
            ps.setNull(9, Types.VARCHAR);
        }

        if (largeCustomerFacilities.getDistrictsAndZonesType() != null && largeCustomerFacilities.getDistrictsAndZonesType().isSetValue()) {
            ps.setString(10, largeCustomerFacilities.getDistrictsAndZonesType().getValue());
            ps.setString(11, largeCustomerFacilities.getDistrictsAndZonesType().getCodeSpace());
        } else {
            ps.setNull(10, Types.VARCHAR);
            ps.setNull(11, Types.VARCHAR);
        }

        if (largeCustomerFacilities.getInauguralDate() != null)
            ps.setDate(12, Date.valueOf(largeCustomerFacilities.getInauguralDate()));
        else
            ps.setNull(12, Types.DATE);

        ps.setString(13, largeCustomerFacilities.getKeyTenants());

        if (largeCustomerFacilities.getLandUsePlanType() != null && largeCustomerFacilities.getLandUsePlanType().isSetValue()) {
            ps.setString(14, largeCustomerFacilities.getLandUsePlanType().getValue());
            ps.setString(15, largeCustomerFacilities.getLandUsePlanType().getCodeSpace());
        } else {
            ps.setNull(14, Types.VARCHAR);
            ps.setNull(15, Types.VARCHAR);
        }

        ps.setString(16, largeCustomerFacilities.getName());
        ps.setString(17, largeCustomerFacilities.getNote());
        ps.setString(18, largeCustomerFacilities.getOwner());

        if (largeCustomerFacilities.getPrefecture() != null && largeCustomerFacilities.getPrefecture().isSetValue()) {
            ps.setString(19, largeCustomerFacilities.getPrefecture().getValue());
            ps.setString(20, largeCustomerFacilities.getPrefecture().getCodeSpace());
        } else {
            ps.setNull(19, Types.VARCHAR);
            ps.setNull(20, Types.VARCHAR);
        }

        ps.setString(21, largeCustomerFacilities.getReference());

        if (largeCustomerFacilities.getSurveyYear() != null)
            ps.setDate(22, Date.valueOf(LocalDate.of(largeCustomerFacilities.getSurveyYear().getValue(), 1, 1)));
        else
            ps.setNull(22, Types.DATE);

        if (largeCustomerFacilities.getTotalFloorArea() != null && largeCustomerFacilities.getTotalFloorArea().isSetValue()) {
            ps.setDouble(23, largeCustomerFacilities.getTotalFloorArea().getValue());
            ps.setString(24, largeCustomerFacilities.getTotalFloorArea().getUom());
        } else {
            ps.setNull(23, Types.DOUBLE);
            ps.setNull(24, Types.VARCHAR);
        }

        if (largeCustomerFacilities.getTotalStoreFloorArea() != null && largeCustomerFacilities.getTotalStoreFloorArea().isSetValue()) {
            ps.setDouble(25, largeCustomerFacilities.getTotalStoreFloorArea().getValue());
            ps.setString(26, largeCustomerFacilities.getTotalStoreFloorArea().getUom());
        } else {
            ps.setNull(25, Types.DOUBLE);
            ps.setNull(26, Types.VARCHAR);
        }

        if (largeCustomerFacilities.getUrbanPlanType() != null && largeCustomerFacilities.getUrbanPlanType().isSetValue()) {
            ps.setString(27, largeCustomerFacilities.getUrbanPlanType().getValue());
            ps.setString(28, largeCustomerFacilities.getUrbanPlanType().getCodeSpace());
        } else {
            ps.setNull(27, Types.VARCHAR);
            ps.setNull(28, Types.VARCHAR);
        }

        ps.addBatch();
        if (++batchCounter == helper.getDatabaseAdapter().getMaxBatchSize())
            helper.executeBatch(schemaMapper.getTableName(ADETable.LARGECUSTOMERFACILITIE));

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
