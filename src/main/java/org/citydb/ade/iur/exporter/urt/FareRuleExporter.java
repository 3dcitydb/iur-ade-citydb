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

package org.citydb.ade.iur.exporter.urt;

import org.citydb.ade.iur.exporter.ExportManager;
import org.citydb.ade.iur.schema.ADETable;
import org.citydb.core.ade.exporter.CityGMLExportHelper;
import org.citydb.core.database.schema.mapping.MappingConstants;
import org.citydb.core.operation.exporter.CityGMLExportException;
import org.citydb.sqlbuilder.expression.PlaceHolder;
import org.citydb.sqlbuilder.schema.Table;
import org.citydb.sqlbuilder.select.Select;
import org.citydb.sqlbuilder.select.join.JoinFactory;
import org.citydb.sqlbuilder.select.operator.comparison.ComparisonFactory;
import org.citydb.sqlbuilder.select.operator.comparison.ComparisonName;
import org.citygml4j.ade.iur.model.urt.FareAttributeProperty;
import org.citygml4j.ade.iur.model.urt.FareRule;
import org.citygml4j.ade.iur.model.urt.RouteProperty;
import org.citygml4j.model.gml.basicTypes.Code;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FareRuleExporter implements PublicTransitModuleExporter {
    private final PreparedStatement ps;

    public FareRuleExporter(Connection connection, CityGMLExportHelper helper, ExportManager manager) throws CityGMLExportException, SQLException {
        String tableName = manager.getSchemaMapper().getTableName(ADETable.FARERULE);

        Table table = new Table(helper.getTableNameWithSchema(tableName));
        Table fare = new Table(helper.getTableNameWithSchema(MappingConstants.CITYOBJECT));
        Table route = new Table(helper.getTableNameWithSchema(MappingConstants.CITYOBJECT));

        Select select = new Select().addProjection(table.getColumns("containsid", "containsid_codespace", "destinationid",
                        "destinationid_codespace", "originid", "originid_codespace"))
                .addProjection(fare.getColumn("gmlid", "fgmlid"), route.getColumn("gmlid", "rgmlid"))
                .addJoin(JoinFactory.left(fare, "id", ComparisonName.EQUAL_TO, table.getColumn("fare_id")))
                .addJoin(JoinFactory.left(route, "id", ComparisonName.EQUAL_TO, table.getColumn("route_id")))
                .addSelection(ComparisonFactory.equalTo(table.getColumn("id"), new PlaceHolder<>()));
        ps = connection.prepareStatement(select.toString());
    }

    public void doExport(FareRule fareRule, long objectId) throws CityGMLExportException, SQLException {
        ps.setLong(1, objectId);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                String originId = rs.getString("originid");
                if (!rs.wasNull()) {
                    Code code = new Code(originId);
                    code.setCodeSpace(rs.getString("originid_codespace"));
                    fareRule.setOriginId(code);
                }

                String destinationId = rs.getString("destinationid");
                if (!rs.wasNull()) {
                    Code code = new Code(destinationId);
                    code.setCodeSpace(rs.getString("destinationid_codespace"));
                    fareRule.setDestinationId(code);
                }

                String containsId = rs.getString("containsid");
                if (!rs.wasNull()) {
                    Code code = new Code(containsId);
                    code.setCodeSpace(rs.getString("containsid_codespace"));
                    fareRule.setContainsId(code);
                }

                String fareGmlId = rs.getString("fgmlid");
                if (fareGmlId != null)
                    fareRule.setFare(new FareAttributeProperty("#" + fareGmlId));

                String routeGmlId = rs.getString("rgmlid");
                if (routeGmlId != null)
                    fareRule.setRoute(new RouteProperty("#" + routeGmlId));
            }
        }
    }

    @Override
    public void close() throws CityGMLExportException, SQLException {
        ps.close();
    }
}
