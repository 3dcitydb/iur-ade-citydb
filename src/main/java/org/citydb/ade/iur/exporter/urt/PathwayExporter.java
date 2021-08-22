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
import org.citygml4j.ade.iur.model.urt.Pathway;
import org.citygml4j.ade.iur.model.urt.StopProperty;
import org.citygml4j.model.gml.basicTypes.Code;
import org.citygml4j.model.gml.measures.Length;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PathwayExporter implements PublicTransitModuleExporter {
    private final PreparedStatement ps;
    private final String module;
    private final PublicTransitExporter publicTransitExporter;

    public PathwayExporter(Connection connection, CityGMLExportHelper helper, ExportManager manager) throws CityGMLExportException, SQLException {
        String tableName = manager.getSchemaMapper().getTableName(ADETable.PATHWAY);
        CombinedProjectionFilter projectionFilter = helper.getCombinedProjectionFilter(tableName);
        module = StatisticalGridModule.v1_4.getNamespaceURI();

        publicTransitExporter = manager.getExporter(PublicTransitExporter.class);

        Table table = new Table(helper.getTableNameWithSchema(tableName));
        Table publicTransit = new Table(helper.getTableNameWithSchema(manager.getSchemaMapper().getTableName(ADETable.PUBLICTRANSIT)));
        Table from = new Table(helper.getTableNameWithSchema(MappingConstants.CITYOBJECT));
        Table to = new Table(helper.getTableNameWithSchema(MappingConstants.CITYOBJECT));

        Select select = new Select().addProjection(table.getColumns("isbidirectional", "isbidirectional_codespace"))
                .addProjection(from.getColumn("gmlid", "fgmlid"), to.getColumn("gmlid", "tgmlid"))
                .addJoin(JoinFactory.inner(publicTransit, "id", ComparisonName.EQUAL_TO, table.getColumn("id")))
                .addJoin(JoinFactory.left(from, "id", ComparisonName.EQUAL_TO, table.getColumn("from_id")))
                .addJoin(JoinFactory.left(to, "id", ComparisonName.EQUAL_TO, table.getColumn("to_id")))
                .addSelection(ComparisonFactory.equalTo(table.getColumn("id"), new PlaceHolder<>()));
        publicTransitExporter.addProjection(select, publicTransit, projectionFilter, "pt");
        if (projectionFilter.containsProperty("mode", module))
            select.addProjection(table.getColumns("mode_", "mode_codespace"));
        if (projectionFilter.containsProperty("length", module))
            select.addProjection(table.getColumns("length", "length_uom"));
        if (projectionFilter.containsProperty("traversalTime", module))
            select.addProjection(table.getColumn("traversaltime"));
        if (projectionFilter.containsProperty("stairCount", module))
            select.addProjection(table.getColumn("staircount"));
        if (projectionFilter.containsProperty("maxSlope", module))
            select.addProjection(table.getColumn("maxslope"));
        if (projectionFilter.containsProperty("minWidth", module))
            select.addProjection(table.getColumn("minwidth"));
        if (projectionFilter.containsProperty("signpostedAs", module))
            select.addProjection(table.getColumn("signpostedas"));
        if (projectionFilter.containsProperty("reversedSignpostedAs", module))
            select.addProjection(table.getColumn("reversedsignpostedas"));
        ps = connection.prepareStatement(select.toString());
    }

    public void doExport(Pathway pathway, long objectId, AbstractType<?> objectType, ProjectionFilter projectionFilter) throws CityGMLExportException, SQLException {
        ps.setLong(1, objectId);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                publicTransitExporter.doExport(pathway, projectionFilter, "pt", rs);

                String isBidirectional = rs.getString("isbidirectional");
                if (!rs.wasNull()) {
                    Code code = new Code(isBidirectional);
                    code.setCodeSpace(rs.getString("isbidirectional_codespace"));
                    pathway.setIsBidirectional(code);
                }

                String fromGmlId = rs.getString("fgmlid");
                if (fromGmlId != null)
                    pathway.setFrom(new StopProperty("#" + fromGmlId));

                String toGmlId = rs.getString("tgmlid");
                if (toGmlId != null)
                    pathway.setTo(new StopProperty("#" + toGmlId));

                if (projectionFilter.containsProperty("mode", module)) {
                    String mode = rs.getString("mode_");
                    if (!rs.wasNull()) {
                        Code code = new Code(mode);
                        code.setCodeSpace(rs.getString("mode_codespace"));
                        pathway.setMode(code);
                    }
                }

                if (projectionFilter.containsProperty("length", module)) {
                    double lengthValue = rs.getDouble("length");
                    if (!rs.wasNull()) {
                        Length length = new Length(lengthValue);
                        length.setUom(rs.getString("length_uom"));
                        pathway.setLength(length);
                    }
                }

                if (projectionFilter.containsProperty("traversalTime", module)) {
                    int traversalTime = rs.getInt("traversaltime");
                    if (!rs.wasNull())
                        pathway.setTraversalTime(traversalTime);
                }

                if (projectionFilter.containsProperty("stairCount", module)) {
                    int stairCount = rs.getInt("staircount");
                    if (!rs.wasNull())
                        pathway.setStairCount(stairCount);
                }

                if (projectionFilter.containsProperty("maxSlope", module)) {
                    double maxSlope = rs.getDouble("maxslope");
                    if (!rs.wasNull())
                        pathway.setMaxSlope(maxSlope);
                }

                if (projectionFilter.containsProperty("minWidth", module)) {
                    double minWidth = rs.getDouble("minwidth");
                    if (!rs.wasNull())
                        pathway.setMinWidth(minWidth);
                }

                if (projectionFilter.containsProperty("signpostedAs", module))
                    pathway.setSignpostedAs(rs.getString("signpostedas"));

                if (projectionFilter.containsProperty("reversedSignpostedAs", module))
                    pathway.setSignpostedAs(rs.getString("reversedsignpostedas"));
            }
        }
    }

    @Override
    public void close() throws CityGMLExportException, SQLException {
        ps.close();
    }
}
