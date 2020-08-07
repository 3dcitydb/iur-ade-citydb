package org.citydb.ade.iur;

import org.citydb.ImpExp;
import org.citydb.ade.ADEExtension;
import org.citydb.ade.ADEExtensionException;
import org.citydb.ade.ADEObjectMapper;
import org.citydb.ade.exporter.ADEExportManager;
import org.citydb.ade.importer.ADEImportManager;
import org.citydb.ade.iur.kmlExporter.KMLExportManager;
import org.citydb.database.schema.mapping.SchemaMapping;
import org.citydb.modules.kml.ade.ADEBalloonManager;
import org.citydb.modules.kml.ade.ADEKmlExportExtension;
import org.citydb.modules.kml.ade.ADEKmlExportManager;
import org.citygml4j.model.citygml.ade.binding.ADEContext;
import org.citydb.ade.iur.balloon.BalloonManager;
import org.citydb.ade.iur.exporter.ExportManager;
import org.citydb.ade.iur.importer.ImportManager;
import org.citydb.ade.iur.schema.ADETableMapper;
import org.citydb.ade.iur.schema.ObjectMapper;
import org.citydb.ade.iur.schema.SchemaMapper;
import org.citygml4j.ade.iur.UrbanRevitalizationADEContext;

import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

public class UrbanRevitalizationADE extends ADEExtension implements ADEKmlExportExtension {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final SchemaMapper schemaMapper = new SchemaMapper();
    private final UrbanRevitalizationADEContext context = new UrbanRevitalizationADEContext();
    private final ADETableMapper tableMapper = new ADETableMapper();

    public static void main(String[] args) {
        UrbanRevitalizationADE adeExtension = new UrbanRevitalizationADE();
        adeExtension.setBasePath(Paths.get("resources", "extension").toAbsolutePath());
        new ImpExp().doMain(args, adeExtension);
    }

    @Override
    public void init(SchemaMapping schemaMapping) throws ADEExtensionException {
        objectMapper.populateObjectClassIds(schemaMapping);
        schemaMapper.populateSchemaNames(schemaMapping.getMetadata().getDBPrefix().toLowerCase());
        tableMapper.populateTableColumns(schemaMapper);
    }

    @Override
    public List<ADEContext> getADEContexts() {
        return Collections.singletonList(context);
    }

    @Override
    public ADEObjectMapper getADEObjectMapper() {
        return objectMapper;
    }

    @Override
    public ADEImportManager createADEImportManager() {
        return new ImportManager(this, schemaMapper);
    }

    @Override
    public ADEExportManager createADEExportManager() {
        return new ExportManager(objectMapper, schemaMapper);
    }

    @Override
    public ADEKmlExportManager createADEKmlExportManager() {
        return new KMLExportManager(schemaMapper);
    }

    @Override
    public ADEBalloonManager createBalloonManager() {
        return new BalloonManager(tableMapper, schemaMapper);
    }
}
