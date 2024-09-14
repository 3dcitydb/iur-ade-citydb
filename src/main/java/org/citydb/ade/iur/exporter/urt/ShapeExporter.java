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
import org.citydb.config.geometry.GeometryObject;
import org.citydb.core.ade.exporter.CityGMLExportHelper;
import org.citydb.core.database.schema.mapping.AbstractType;
import org.citydb.core.operation.exporter.CityGMLExportException;
import org.citydb.core.operation.exporter.database.content.GMLConverter;
import org.citydb.core.query.filter.projection.CombinedProjectionFilter;
import org.citydb.core.query.filter.projection.ProjectionFilter;
import org.citydb.sqlbuilder.expression.PlaceHolder;
import org.citydb.sqlbuilder.schema.Table;
import org.citydb.sqlbuilder.select.Select;
import org.citydb.sqlbuilder.select.join.JoinFactory;
import org.citydb.sqlbuilder.select.operator.comparison.ComparisonFactory;
import org.citydb.sqlbuilder.select.operator.comparison.ComparisonName;
import org.citygml4j.ade.iur.model.module.StatisticalGridModule;
import org.citygml4j.ade.iur.model.urt.Point;
import org.citygml4j.ade.iur.model.urt.PointProperty;
import org.citygml4j.ade.iur.model.urt.Shape;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ShapeExporter implements PublicTransitModuleExporter {
    private final CityGMLExportHelper helper;
    private final PreparedStatement ps;
    private final String module;

    private final PublicTransitExporter publicTransitExporter;
    private final GMLConverter gmlConverter;

    public ShapeExporter(Connection connection, CityGMLExportHelper helper, ExportManager manager) throws CityGMLExportException, SQLException {
        this.helper = helper;

        String tableName = manager.getSchemaMapper().getTableName(ADETable.PUBLICTRANSIT);
        CombinedProjectionFilter projectionFilter = helper.getCombinedProjectionFilter(tableName);
        module = StatisticalGridModule.v1_4.getNamespaceURI();

        publicTransitExporter = manager.getExporter(PublicTransitExporter.class);
        gmlConverter = helper.getGMLConverter();

        Table table = new Table(helper.getTableNameWithSchema(tableName));
        Select select = publicTransitExporter.addProjection(new Select(), table, projectionFilter, "pt")
                .addSelection(ComparisonFactory.equalTo(table.getColumn("id"), new PlaceHolder<>()));
        if (projectionFilter.containsProperty("point", module)) {
            Table point = new Table(helper.getTableNameWithSchema(manager.getSchemaMapper().getTableName(ADETable.POINT)));
            select.addProjection(point.getColumns("latitude", "longitude", "point", "pointdistancetraveled", "pointsequence"))
                    .addJoin(JoinFactory.left(point, "publictransit_point_id", ComparisonName.EQUAL_TO, table.getColumn("id")));
        }
        ps = connection.prepareStatement(select.toString());
    }

    public void doExport(Shape shape, long objectId, AbstractType<?> objectType, ProjectionFilter projectionFilter) throws CityGMLExportException, SQLException {
        ps.setLong(1, objectId);

        try (ResultSet rs = ps.executeQuery()) {
            boolean isInitialized = false;

            while (rs.next()) {
                if (!isInitialized) {
                    publicTransitExporter.doExport(shape, projectionFilter, "pt", rs);
                    isInitialized = true;
                }

                Point point = new Point();

                double latitude = rs.getDouble("latitude");
                if (!rs.wasNull())
                    point.setLatitude(latitude);

                double longitude = rs.getDouble("longitude");
                if (!rs.wasNull())
                    point.setLongitude(longitude);

                Object geometry = rs.getObject("point");
                if (!rs.wasNull()) {
                    GeometryObject pointGeometry = helper.getDatabaseAdapter().getGeometryConverter().getPoint(geometry);
                    point.setPoint(gmlConverter.getPointProperty(pointGeometry));
                }

                int pointSequence = rs.getInt("pointsequence");
                if (!rs.wasNull())
                    point.setPointSequence(pointSequence);

                double pointDistanceTraveled = rs.getDouble("pointdistancetraveled");
                if (!rs.wasNull())
                    point.setPointDistanceTraveled(pointDistanceTraveled);

                shape.getPoints().add(new PointProperty(point));
            }
        }
    }

    @Override
    public void close() throws CityGMLExportException, SQLException {
        ps.close();
    }
}
