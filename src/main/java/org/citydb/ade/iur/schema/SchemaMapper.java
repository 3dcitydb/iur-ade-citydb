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

package org.citydb.ade.iur.schema;

import java.util.EnumMap;
import java.util.Map;

public class SchemaMapper {
    private EnumMap<ADETable, String> tableNames = new EnumMap<>(ADETable.class);
    private EnumMap<ADESequence, String> sequenceNames = new EnumMap<>(ADESequence.class);

    public void populateSchemaNames(String prefix) {
        for (ADETable table : ADETable.values())
            tableNames.put(table, prefix + "_" + table.toString().toLowerCase());

        for (ADESequence sequence : ADESequence.values())
            sequenceNames.put(sequence, prefix + "_" + sequence.toString().toLowerCase());
    }

    public String getTableName(ADETable table) {
        return tableNames.get(table);
    }

    public ADETable fromTableName(String tableName) {
        tableName = tableName.toLowerCase();

        for (Map.Entry<ADETable, String> entry : tableNames.entrySet()) {
            if (entry.getValue().equals(tableName))
                return entry.getKey();
        }

        return null;
    }

    public String getSequenceName(ADESequence sequence) {
        return sequenceNames.get(sequence);
    }
}
