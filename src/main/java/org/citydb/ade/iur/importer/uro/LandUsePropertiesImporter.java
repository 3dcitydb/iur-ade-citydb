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

import org.citydb.ade.importer.ADEImporter;
import org.citydb.ade.importer.ADEPropertyCollection;
import org.citydb.ade.importer.CityGMLImportHelper;
import org.citydb.ade.iur.importer.ImportManager;
import org.citydb.ade.iur.schema.ADETable;
import org.citydb.ade.iur.schema.SchemaMapper;
import org.citydb.citygml.importer.CityGMLImportException;
import org.citydb.database.schema.mapping.FeatureType;
import org.citygml4j.ade.iur.model.uro.AreaClassificationTypeProperty;
import org.citygml4j.ade.iur.model.uro.AreaInHaProperty;
import org.citygml4j.ade.iur.model.uro.AreaInSquareMeterProperty;
import org.citygml4j.ade.iur.model.uro.CityProperty;
import org.citygml4j.ade.iur.model.uro.DistrictsAndZonesTypeProperty;
import org.citygml4j.ade.iur.model.uro.LandUsePlanTypeProperty;
import org.citygml4j.ade.iur.model.uro.NominalAreaProperty;
import org.citygml4j.ade.iur.model.uro.NoteProperty;
import org.citygml4j.ade.iur.model.uro.OwnerProperty;
import org.citygml4j.ade.iur.model.uro.OwnerTypeProperty;
import org.citygml4j.ade.iur.model.uro.PrefectureProperty;
import org.citygml4j.ade.iur.model.uro.ReferenceProperty;
import org.citygml4j.ade.iur.model.uro.SurveyYearProperty;
import org.citygml4j.ade.iur.model.uro.UrbanPlanTypeProperty;
import org.citygml4j.model.citygml.landuse.LandUse;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;

public class LandUsePropertiesImporter implements ADEImporter {
    private final CityGMLImportHelper helper;
    private final SchemaMapper schemaMapper;
    private final PreparedStatement ps;

    private int batchCounter;

    public LandUsePropertiesImporter(Connection connection, CityGMLImportHelper helper, ImportManager manager) throws CityGMLImportException, SQLException {
        this.helper = helper;
        this.schemaMapper = manager.getSchemaMapper();

        ps = connection.prepareStatement("insert into " +
                helper.getTableNameWithSchema(schemaMapper.getTableName(ADETable.LAND_USE)) + " " +
                "(id, areaclassificationtype, areaclassification_codespace, areainha, areainha_uom, areainsquaremeter, " +
                "areainsquaremeter_uom, city, city_codespace, districtsandzonestype, districtsandzonest_codespace, " +
                "landuseplantype, landuseplantype_codespace, nominalarea, nominalarea_uom, note, owner, ownertype, " +
                "ownertype_codespace, prefecture, prefecture_codespace, reference, surveyyear, urbanplantype, urbanplantype_codespace) " +
                "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
    }

    public void doImport(ADEPropertyCollection properties, LandUse parent, long parentId, FeatureType parentType) throws CityGMLImportException, SQLException {
        ps.setLong(1, parentId);

        AreaClassificationTypeProperty areaClassificationType = properties.getFirst(AreaClassificationTypeProperty.class);
        if (areaClassificationType != null && areaClassificationType.isSetValue()) {
            ps.setString(2, areaClassificationType.getValue().getValue());
            ps.setString(3, areaClassificationType.getValue().getCodeSpace());
        } else {
            ps.setNull(2, Types.VARCHAR);
            ps.setNull(3, Types.VARCHAR);
        }

        AreaInHaProperty areaInHa = properties.getFirst(AreaInHaProperty.class);
        if (areaInHa != null && areaInHa.isSetValue() && areaInHa.getValue().isSetValue()) {
            ps.setDouble(4, areaInHa.getValue().getValue());
            ps.setString(5, areaInHa.getValue().getUom());
        } else {
            ps.setNull(4, Types.DOUBLE);
            ps.setNull(5, Types.VARCHAR);
        }

        AreaInSquareMeterProperty areaInSquareMeter = properties.getFirst(AreaInSquareMeterProperty.class);
        if (areaInSquareMeter != null && areaInSquareMeter.isSetValue() && areaInSquareMeter.getValue().isSetValue()) {
            ps.setDouble(6, areaInSquareMeter.getValue().getValue());
            ps.setString(7, areaInSquareMeter.getValue().getUom());
        } else {
            ps.setNull(6, Types.DOUBLE);
            ps.setNull(7, Types.VARCHAR);
        }

        CityProperty city = properties.getFirst(CityProperty.class);
        if (city != null && city.isSetValue()) {
            ps.setString(8, city.getValue().getValue());
            ps.setString(9, city.getValue().getCodeSpace());
        } else {
            ps.setNull(8, Types.VARCHAR);
            ps.setNull(9, Types.VARCHAR);
        }

        DistrictsAndZonesTypeProperty districtsAndZonesType = properties.getFirst(DistrictsAndZonesTypeProperty.class);
        if (districtsAndZonesType != null && districtsAndZonesType.isSetValue()) {
            ps.setString(10, districtsAndZonesType.getValue().getValue());
            ps.setString(11, districtsAndZonesType.getValue().getCodeSpace());
        } else {
            ps.setNull(10, Types.VARCHAR);
            ps.setNull(11, Types.VARCHAR);
        }

        LandUsePlanTypeProperty landUsePlanType = properties.getFirst(LandUsePlanTypeProperty.class);
        if (landUsePlanType != null && landUsePlanType.isSetValue()) {
            ps.setString(12, landUsePlanType.getValue().getValue());
            ps.setString(13, landUsePlanType.getValue().getCodeSpace());
        } else {
            ps.setNull(12, Types.VARCHAR);
            ps.setNull(13, Types.VARCHAR);
        }

        NominalAreaProperty nominalArea = properties.getFirst(NominalAreaProperty.class);
        if (nominalArea != null && nominalArea.isSetValue() && nominalArea.getValue().isSetValue()) {
            ps.setDouble(14, nominalArea.getValue().getValue());
            ps.setString(15, nominalArea.getValue().getUom());
        } else {
            ps.setNull(14, Types.DOUBLE);
            ps.setNull(15, Types.VARCHAR);
        }

        NoteProperty note = properties.getFirst(NoteProperty.class);
        ps.setString(16, note != null ? note.getValue() : null);

        OwnerProperty owner = properties.getFirst(OwnerProperty.class);
        ps.setString(17, owner != null ? owner.getValue() : null);

        OwnerTypeProperty ownerType = properties.getFirst(OwnerTypeProperty.class);
        if (ownerType != null && ownerType.isSetValue()) {
            ps.setString(18, ownerType.getValue().getValue());
            ps.setString(19, ownerType.getValue().getCodeSpace());
        } else {
            ps.setNull(18, Types.VARCHAR);
            ps.setNull(19, Types.VARCHAR);
        }

        PrefectureProperty prefecture = properties.getFirst(PrefectureProperty.class);
        if (prefecture != null && prefecture.isSetValue()) {
            ps.setString(20, prefecture.getValue().getValue());
            ps.setString(21, prefecture.getValue().getCodeSpace());
        } else {
            ps.setNull(20, Types.VARCHAR);
            ps.setNull(21, Types.VARCHAR);
        }

        ReferenceProperty reference = properties.getFirst(ReferenceProperty.class);
        ps.setString(22, reference != null ? reference.getValue() : null);

        SurveyYearProperty surveyYear = properties.getFirst(SurveyYearProperty.class);
        if (surveyYear != null && surveyYear.isSetValue())
            ps.setDate(23, Date.valueOf(LocalDate.of(surveyYear.getValue().getValue(), 1, 1)));
        else
            ps.setNull(23, Types.DATE);

        UrbanPlanTypeProperty urbanPlanType = properties.getFirst(UrbanPlanTypeProperty.class);
        if (urbanPlanType != null && urbanPlanType.isSetValue()) {
            ps.setString(24, urbanPlanType.getValue().getValue());
            ps.setString(25, urbanPlanType.getValue().getCodeSpace());
        } else {
            ps.setNull(24, Types.VARCHAR);
            ps.setNull(25, Types.VARCHAR);
        }

        ps.addBatch();
        if (++batchCounter == helper.getDatabaseAdapter().getMaxBatchSize())
            helper.executeBatch(schemaMapper.getTableName(ADETable.LAND_USE));
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
