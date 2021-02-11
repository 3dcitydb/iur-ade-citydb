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

package org.citydb.ade.iur.importer.uro;

import org.citydb.ade.importer.CityGMLImportHelper;
import org.citydb.ade.iur.importer.ImportManager;
import org.citydb.ade.iur.schema.ADESequence;
import org.citydb.ade.iur.schema.ADETable;
import org.citydb.ade.iur.schema.SchemaMapper;
import org.citydb.citygml.importer.CityGMLImportException;
import org.citygml4j.ade.iur.model.uro.TrafficVolume;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;

public class TrafficVolumeImporter implements UrbanObjectModuleImporter {
    private final CityGMLImportHelper helper;
    private final SchemaMapper schemaMapper;
    private final PreparedStatement ps;

    private int batchCounter;

    public TrafficVolumeImporter(Connection connection, CityGMLImportHelper helper, ImportManager manager) throws CityGMLImportException, SQLException {
        this.helper = helper;
        this.schemaMapper = manager.getSchemaMapper();

        ps = connection.prepareStatement("insert into " +
                helper.getTableNameWithSchema(schemaMapper.getTableName(ADETable.TRAFFICVOLUME)) + " " +
                "(id, areaclassificationtype, areaclassification_codespace, averagetravelspeedincongesti, city, " +
                "city_codespace, congestionrate, largevehiclerate, note, observationpointname, prefecture, " +
                "prefecture_codespace, reference, surveyyear, urbanplantype, urbanplantype_codespace, " +
                "weekday12hourtrafficvolume, weekday24hourtrafficvolume) " +
                "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
    }

    public long doImport(TrafficVolume trafficVolume) throws CityGMLImportException, SQLException {
        long objectId = helper.getNextSequenceValue(schemaMapper.getSequenceName(ADESequence.TRAFFICVOLUME_SEQ));
        ps.setLong(1, objectId);

        if (trafficVolume.getAreaClassificationType() != null && trafficVolume.getAreaClassificationType().isSetValue()) {
            ps.setString(2, trafficVolume.getAreaClassificationType().getValue());
            ps.setString(3, trafficVolume.getAreaClassificationType().getCodeSpace());
        } else {
            ps.setNull(2, Types.VARCHAR);
            ps.setNull(3, Types.VARCHAR);
        }

        if (trafficVolume.getAverageTravelSpeedInCongestion() != null)
            ps.setDouble(4, trafficVolume.getAverageTravelSpeedInCongestion());
        else
            ps.setNull(4, Types.DOUBLE);

        if (trafficVolume.getCity() != null && trafficVolume.getCity().isSetValue()) {
            ps.setString(5, trafficVolume.getCity().getValue());
            ps.setString(6, trafficVolume.getCity().getCodeSpace());
        } else {
            ps.setNull(5, Types.VARCHAR);
            ps.setNull(6, Types.VARCHAR);
        }

        if (trafficVolume.getCongestionRate() != null)
            ps.setDouble(7, trafficVolume.getCongestionRate());
        else
            ps.setNull(7, Types.DOUBLE);

        if (trafficVolume.getLargeVehicleRate() != null)
            ps.setDouble(8, trafficVolume.getLargeVehicleRate());
        else
            ps.setNull(8, Types.DOUBLE);

        ps.setString(9, trafficVolume.getNote());
        ps.setString(10, trafficVolume.getObservationPointName());

        if (trafficVolume.getPrefecture() != null && trafficVolume.getPrefecture().isSetValue()) {
            ps.setString(11, trafficVolume.getPrefecture().getValue());
            ps.setString(12, trafficVolume.getPrefecture().getCodeSpace());
        } else {
            ps.setNull(11, Types.VARCHAR);
            ps.setNull(12, Types.VARCHAR);
        }

        ps.setString(13, trafficVolume.getReference());

        if (trafficVolume.getSurveyYear() != null)
            ps.setDate(14, Date.valueOf(LocalDate.of(trafficVolume.getSurveyYear().getValue(), 1, 1)));
        else
            ps.setNull(14, Types.DATE);

        if (trafficVolume.getUrbanPlanType() != null && trafficVolume.getUrbanPlanType().isSetValue()) {
            ps.setString(15, trafficVolume.getUrbanPlanType().getValue());
            ps.setString(16, trafficVolume.getUrbanPlanType().getCodeSpace());
        } else {
            ps.setNull(15, Types.VARCHAR);
            ps.setNull(16, Types.VARCHAR);
        }

        if (trafficVolume.getWeekday12hourTrafficVolume() != null)
            ps.setInt(17, trafficVolume.getWeekday12hourTrafficVolume());
        else
            ps.setNull(17, Types.INTEGER);

        if (trafficVolume.getWeekday24hourTrafficVolume() != null)
            ps.setInt(18, trafficVolume.getWeekday24hourTrafficVolume());
        else
            ps.setNull(18, Types.INTEGER);

        ps.addBatch();
        if (++batchCounter == helper.getDatabaseAdapter().getMaxBatchSize())
            helper.executeBatch(schemaMapper.getTableName(ADETable.TRAFFICVOLUME));

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
