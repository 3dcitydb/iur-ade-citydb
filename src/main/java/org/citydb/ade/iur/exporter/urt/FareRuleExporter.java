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

package org.citydb.ade.iur.exporter.urt;

import org.citydb.ade.exporter.CityGMLExportHelper;
import org.citydb.ade.iur.exporter.ExportManager;
import org.citydb.ade.iur.schema.ADETable;
import org.citydb.citygml.exporter.CityGMLExportException;
import org.citydb.database.schema.mapping.MappingConstants;
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
