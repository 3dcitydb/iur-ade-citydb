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
import org.citygml4j.ade.iur.model.module.UrbanFunctionModule;
import org.citygml4j.ade.iur.model.urt.PublicTransit;
import org.citygml4j.ade.iur.model.urt.TargetProperty;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PublicTransitExporter implements PublicTransitModuleExporter {
    private final CityGMLExportHelper helper;
    private final PreparedStatement ps;
    private final String module;

    public PublicTransitExporter(Connection connection, CityGMLExportHelper helper, ExportManager manager) throws CityGMLExportException, SQLException {
        this.helper = helper;

        String tableName = manager.getSchemaMapper().getTableName(ADETable.PUBLICTRANSIT);
        CombinedProjectionFilter projectionFilter = helper.getCombinedProjectionFilter(tableName);
        module = UrbanFunctionModule.v1_4.getNamespaceURI();

        Table table = new Table(helper.getTableNameWithSchema(tableName));
        Select select = addProjection(new Select(), table, projectionFilter, "")
                .addSelection(ComparisonFactory.equalTo(table.getColumn("id"), new PlaceHolder<>()));
        ps = connection.prepareStatement(select.toString());
    }

    public Select addProjection(Select select, Table table, CombinedProjectionFilter projectionFilter, String prefix) {
        select.addProjection(table.getColumn("id", prefix + "id"), table.getColumn("orgid", prefix + "orgid"));
        if (projectionFilter.containsProperty("target", module)) {
            Table cityObject = new Table(helper.getTableNameWithSchema(MappingConstants.CITYOBJECT));
            select.addProjection(table.getColumn("target_id", prefix + "target_id"),
                    cityObject.getColumn("gmlid", prefix + "gmlid"))
                    .addJoin(JoinFactory.left(cityObject, "id", ComparisonName.EQUAL_TO, table.getColumn("target_id")));
        }

        return select;
    }

    public void doExport(PublicTransit publicTransit, ProjectionFilter projectionFilter, String prefix, ResultSet rs) throws CityGMLExportException, SQLException {
        publicTransit.setOrgId(rs.getString(prefix + "orgid"));

        if (projectionFilter.containsProperty("target", module)) {
            String gmlId = rs.getString(prefix + "gmlid");
            if (gmlId != null)
                publicTransit.setTarget(new TargetProperty("#" + gmlId));
        }
    }

    @Override
    public void close() throws CityGMLExportException, SQLException {
        ps.close();
    }
}
