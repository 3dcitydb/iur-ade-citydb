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

package org.citydb.ade.iur.importer.urt;

import org.citydb.ade.iur.importer.ImportManager;
import org.citydb.ade.iur.schema.ADETable;
import org.citydb.ade.iur.schema.SchemaMapper;
import org.citydb.core.ade.importer.CityGMLImportHelper;
import org.citydb.core.ade.importer.ForeignKeys;
import org.citydb.core.database.schema.mapping.AbstractObjectType;
import org.citydb.core.operation.importer.CityGMLImportException;
import org.citygml4j.ade.iur.model.urt.Agency;
import org.citygml4j.ade.iur.model.urt.Attribution;
import org.citygml4j.ade.iur.model.urt.Route;
import org.citygml4j.ade.iur.model.urt.Trip;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class AttributionImporter implements PublicTransitModuleImporter {
    private final CityGMLImportHelper helper;
    private final SchemaMapper schemaMapper;
    private final PreparedStatement ps;
    private final PublicTransitImporter publicTransitImporter;

    private int batchCounter;

    public AttributionImporter(Connection connection, CityGMLImportHelper helper, ImportManager manager) throws CityGMLImportException, SQLException {
        this.helper = helper;
        this.schemaMapper = manager.getSchemaMapper();

        ps = connection.prepareStatement("insert into " +
                helper.getTableNameWithSchema(manager.getSchemaMapper().getTableName(ADETable.ATTRIBUTION)) + " " +
                "(id, email, isauthority, isoperator, isproducer, organizationname, phonenumber, url, " +
                "agency_id, route_id, trip_id) " +
                "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

        publicTransitImporter = manager.getImporter(PublicTransitImporter.class);
    }

    public void doImport(Attribution attribution, long objectId, AbstractObjectType<?> objectType, ForeignKeys foreignKeys) throws CityGMLImportException, SQLException {
        publicTransitImporter.doImport(attribution, objectId, objectType, foreignKeys);
        ps.setLong(1, objectId);

        ps.setString(2, attribution.getEmail());

        if (attribution.getIsAuthority() != null)
            ps.setInt(3, attribution.getIsAuthority() ? 1 : 0);
        else
            ps.setNull(3, Types.INTEGER);

        if (attribution.getIsOperator() != null)
            ps.setInt(4, attribution.getIsOperator() ? 1 : 0);
        else
            ps.setNull(4, Types.INTEGER);

        if (attribution.getIsProducer() != null)
            ps.setInt(5, attribution.getIsProducer() ? 1 : 0);
        else
            ps.setNull(5, Types.INTEGER);

        ps.setString(6, attribution.getOrganizationName());
        ps.setString(7, attribution.getPhoneNumber());
        ps.setString(8, attribution.getUrl());

        long agencyId = 0;
        if (attribution.getAgency() != null) {
            Agency agency = attribution.getAgency().getObject();
            if (agency != null) {
                agencyId = helper.importObject(agency);
                attribution.getAgency().unsetObject();
            } else {
                String href = attribution.getAgency().getHref();
                if (href != null && !href.isEmpty()) {
                    helper.propagateObjectXlink(
                            schemaMapper.getTableName(ADETable.ATTRIBUTION),
                            objectId, href, "agency_id");
                }
            }
        }

        if (agencyId != 0)
            ps.setLong(9, agencyId);
        else
            ps.setNull(9, Types.NULL);

        long routeId = 0;
        if (attribution.getRoute() != null) {
            Route route = attribution.getRoute().getObject();
            if (route != null) {
                routeId = helper.importObject(route);
                attribution.getRoute().unsetObject();
            } else {
                String href = attribution.getRoute().getHref();
                if (href != null && !href.isEmpty()) {
                    helper.propagateObjectXlink(
                            schemaMapper.getTableName(ADETable.ATTRIBUTION),
                            objectId, href, "route_id");
                }
            }
        }

        if (routeId != 0)
            ps.setLong(10, routeId);
        else
            ps.setNull(10, Types.NULL);

        long tripId = 0;
        if (attribution.getTrip() != null) {
            Trip trip = attribution.getTrip().getObject();
            if (trip != null) {
                tripId = helper.importObject(trip);
                attribution.getTrip().unsetObject();
            } else {
                String href = attribution.getTrip().getHref();
                if (href != null && !href.isEmpty()) {
                    helper.propagateObjectXlink(
                            schemaMapper.getTableName(ADETable.TRIP),
                            objectId, href, "trip_id");
                }
            }
        }

        if (tripId != 0)
            ps.setLong(11, tripId);
        else
            ps.setNull(11, Types.NULL);

        ps.addBatch();
        if (++batchCounter == helper.getDatabaseAdapter().getMaxBatchSize())
            helper.executeBatch(objectType);
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
