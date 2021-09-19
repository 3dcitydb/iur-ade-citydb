/*
 * 3D City Database - The Open Source CityGML Database
 * https://www.3dcitydb.org/
 *
 * Copyright 2013 - 2021
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
import org.citydb.ade.iur.schema.ADETable;
import org.citydb.ade.iur.schema.SchemaMapper;
import org.citydb.core.ade.importer.ADEPropertyCollection;
import org.citydb.core.ade.importer.CityGMLImportHelper;
import org.citydb.core.database.schema.mapping.FeatureType;
import org.citydb.core.operation.importer.CityGMLImportException;
import org.citygml4j.ade.iur.model.uro.*;
import org.citygml4j.model.citygml.landuse.LandUse;

import java.sql.*;
import java.time.LocalDate;

public class LandUsePropertiesImporter implements UrbanObjectModuleImporter {
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
