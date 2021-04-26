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

package org.citydb.ade.iur.exporter.urt;

import org.citydb.ade.exporter.CityGMLExportHelper;
import org.citydb.ade.iur.exporter.ExportManager;
import org.citydb.ade.iur.schema.ADETable;
import org.citydb.ade.iur.schema.ObjectMapper;
import org.citydb.citygml.exporter.CityGMLExportException;
import org.citydb.database.schema.mapping.FeatureType;
import org.citydb.query.filter.projection.ProjectionFilter;
import org.citydb.sqlbuilder.expression.PlaceHolder;
import org.citydb.sqlbuilder.schema.Table;
import org.citydb.sqlbuilder.select.Select;
import org.citydb.sqlbuilder.select.operator.comparison.ComparisonFactory;
import org.citygml4j.ade.iur.model.module.PublicTransitModule;
import org.citygml4j.ade.iur.model.urt.DataTypeProperty;
import org.citygml4j.ade.iur.model.urt.FareRule;
import org.citygml4j.ade.iur.model.urt.FeedInfo;
import org.citygml4j.ade.iur.model.urt.Frequency;
import org.citygml4j.ade.iur.model.urt.PublicTransitDataType;
import org.citygml4j.ade.iur.model.urt.PublicTransitDataTypeProperty;
import org.citygml4j.ade.iur.model.urt.StopTime;
import org.citygml4j.ade.iur.model.urt.Transfer;
import org.citygml4j.ade.iur.model.urt.Translation;
import org.citygml4j.model.citygml.cityobjectgroup.CityObjectGroup;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CityObjectGroupPropertiesExporter implements PublicTransitModuleExporter {
    private final CityGMLExportHelper helper;
    private final ObjectMapper objectMapper;
    private final PreparedStatement ps;
    private final String module;

    private final FareRuleExporter fareRuleExporter;
    private final FeedInfoExporter feedInfoExporter;
    private final FrequencyExporter frequencyExporter;
    private final StopTimeExporter stopTimeExporter;
    private final TransferExporter transferExporter;
    private final TranslationExporter translationExporter;

    public CityObjectGroupPropertiesExporter(Connection connection, CityGMLExportHelper helper, ExportManager manager) throws CityGMLExportException, SQLException {
        this.helper = helper;
        this.objectMapper = manager.getObjectMapper();

        String tableName = manager.getSchemaMapper().getTableName(ADETable.PUBLICTRANSITDATATYPE);
        module = PublicTransitModule.v1_4.getNamespaceURI();

        Table table = new Table(helper.getTableNameWithSchema(tableName));
        Select select = new Select().addProjection(table.getColumns("id", "objectclass_id"))
                .addSelection(ComparisonFactory.equalTo(table.getColumn("cityobjectgroup_datatype_id"), new PlaceHolder<>()));

        ps = connection.prepareStatement(select.toString());

        fareRuleExporter = manager.getExporter(FareRuleExporter.class);
        feedInfoExporter = manager.getExporter(FeedInfoExporter.class);
        frequencyExporter = manager.getExporter(FrequencyExporter.class);
        stopTimeExporter = manager.getExporter(StopTimeExporter.class);
        transferExporter = manager.getExporter(TransferExporter.class);
        translationExporter = manager.getExporter(TranslationExporter.class);
    }

    public void doExport(CityObjectGroup parent, long parentId, FeatureType parentType, ProjectionFilter projectionFilter) throws CityGMLExportException, SQLException {
        if (projectionFilter.containsProperty("dataType", module)) {
            ps.setLong(1, parentId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    long objectId = rs.getLong(1);
                    int objectClassId = rs.getInt(2);
                    if (!rs.wasNull()) {
                        PublicTransitDataType dataType = objectMapper.createType(objectClassId, PublicTransitDataType.class);
                        if (dataType == null) {
                            helper.logOrThrowErrorMessage("Failed to instantiate public transit data type (id: " + objectId + ").");
                            continue;
                        }

                        if (dataType instanceof FareRule)
                            fareRuleExporter.doExport((FareRule) dataType, objectId);
                        else if (dataType instanceof FeedInfo)
                            feedInfoExporter.doExport((FeedInfo) dataType, objectId);
                        else if (dataType instanceof Frequency)
                            frequencyExporter.doExport((Frequency) dataType, objectId);
                        else if (dataType instanceof StopTime)
                            stopTimeExporter.doExport((StopTime) dataType, objectId);
                        else if (dataType instanceof Transfer)
                            transferExporter.doExport((Transfer) dataType, objectId);
                        else if (dataType instanceof Translation)
                            translationExporter.doExport((Translation) dataType, objectId);

                        DataTypeProperty property = new DataTypeProperty(new PublicTransitDataTypeProperty(dataType));
                        parent.addGenericApplicationPropertyOfCityObjectGroup(property);
                    }
                }
            }
        }
    }

    @Override
    public void close() throws CityGMLExportException, SQLException {
        ps.close();
    }
}
