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

import org.citydb.ade.iur.exporter.ExportManager;
import org.citydb.ade.iur.schema.ADETable;
import org.citydb.core.ade.exporter.CityGMLExportHelper;
import org.citydb.core.database.schema.mapping.AbstractType;
import org.citydb.core.database.schema.mapping.MappingConstants;
import org.citydb.core.operation.exporter.CityGMLExportException;
import org.citydb.core.query.filter.projection.CombinedProjectionFilter;
import org.citydb.core.query.filter.projection.ProjectionFilter;
import org.citydb.sqlbuilder.expression.PlaceHolder;
import org.citydb.sqlbuilder.schema.Table;
import org.citydb.sqlbuilder.select.Select;
import org.citydb.sqlbuilder.select.join.JoinFactory;
import org.citydb.sqlbuilder.select.operator.comparison.ComparisonFactory;
import org.citydb.sqlbuilder.select.operator.comparison.ComparisonName;
import org.citygml4j.ade.iur.model.module.StatisticalGridModule;
import org.citygml4j.ade.iur.model.urt.AgencyProperty;
import org.citygml4j.ade.iur.model.urt.FareAttribute;
import org.citygml4j.model.gml.basicTypes.Code;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FareAttributeExporter implements PublicTransitModuleExporter {
    private final PreparedStatement ps;
    private final String module;
    private final PublicTransitExporter publicTransitExporter;

    public FareAttributeExporter(Connection connection, CityGMLExportHelper helper, ExportManager manager) throws CityGMLExportException, SQLException {
        String tableName = manager.getSchemaMapper().getTableName(ADETable.FAREATTRIBUTE);
        CombinedProjectionFilter projectionFilter = helper.getCombinedProjectionFilter(tableName);
        module = StatisticalGridModule.v1_4.getNamespaceURI();

        publicTransitExporter = manager.getExporter(PublicTransitExporter.class);

        Table table = new Table(helper.getTableNameWithSchema(tableName));
        Table publicTransit = new Table(helper.getTableNameWithSchema(manager.getSchemaMapper().getTableName(ADETable.PUBLICTRANSIT)));

        Select select = new Select().addProjection(table.getColumns("price", "currencytype", "currencytype_codespace",
                "paymentmethod", "paymentmethod_codespace", "transfers", "transfers_codespace"))
                .addJoin(JoinFactory.inner(publicTransit, "id", ComparisonName.EQUAL_TO, table.getColumn("id")))
                .addSelection(ComparisonFactory.equalTo(table.getColumn("id"), new PlaceHolder<>()));
        publicTransitExporter.addProjection(select, publicTransit, projectionFilter, "pt");
        if (projectionFilter.containsProperty("transferDuration", module))
            select.addProjection(table.getColumn("transferduration"));
        if (projectionFilter.containsProperty("agency", module)) {
            Table cityObject = new Table(helper.getTableNameWithSchema(MappingConstants.CITYOBJECT));
            select.addProjection(cityObject.getColumn("gmlid", "agmlid"))
                    .addJoin(JoinFactory.left(cityObject, "id", ComparisonName.EQUAL_TO, table.getColumn("agency_id")));
        }
        ps = connection.prepareStatement(select.toString());
    }

    public void doExport(FareAttribute fareAttribute, long objectId, AbstractType<?> objectType, ProjectionFilter projectionFilter) throws CityGMLExportException, SQLException {
        ps.setLong(1, objectId);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                publicTransitExporter.doExport(fareAttribute, projectionFilter, "pt", rs);

                double price = rs.getDouble("price");
                if (!rs.wasNull())
                    fareAttribute.setPrice(price);

                String currencyType = rs.getString("currencytype");
                if (!rs.wasNull()) {
                    Code code = new Code(currencyType);
                    code.setCodeSpace(rs.getString("currencytype_codespace"));
                    fareAttribute.setCurrencyType(code);
                }

                String paymentMethod = rs.getString("paymentmethod");
                if (!rs.wasNull()) {
                    Code code = new Code(paymentMethod);
                    code.setCodeSpace(rs.getString("paymentmethod_codespace"));
                    fareAttribute.setPaymentMethod(code);
                }

                String transfers = rs.getString("transfers");
                if (!rs.wasNull()) {
                    Code code = new Code(transfers);
                    code.setCodeSpace(rs.getString("transfers_codespace"));
                    fareAttribute.setTransfers(code);
                }

                if (projectionFilter.containsProperty("transferDuration", module)) {
                    int transferDuration = rs.getInt("transferduration");
                    if (!rs.wasNull())
                        fareAttribute.setTransferDuration(transferDuration);
                }

                if (projectionFilter.containsProperty("agency", module)) {
                    String gmlId = rs.getString("agmlid");
                    if (gmlId != null)
                        fareAttribute.setAgency(new AgencyProperty("#" + gmlId));
                }
            }
        }
    }

    @Override
    public void close() throws CityGMLExportException, SQLException {
        ps.close();
    }
}
