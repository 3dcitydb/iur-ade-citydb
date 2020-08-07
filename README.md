# i-UR ADE extension for the 3D City Database

This is a 3DCityDB extension for the **i-Urban Revitalization (i-UR)** Application Domain Extension for CityGML.
The "i-UR" is an information infrastructure for urban revitalization and planning.

This extension adds support for managing i-UR ADE data inside the 3DCityDB and enables the
[Importer/Exporter tool](https://github.com/3dcitydb/importer-exporter) to load and export i-UR ADE enriched datasets.

* **i-UR specification and material: https://www.kantei.go.jp/jp/singi/tiiki/toshisaisei/itoshisaisei/iur**
* **Where to file issues: https://github.com/3dcitydb/iur-ade-citydb/issues**

## How to use this extension
The i-UR ADE extension consists of two main parts:

- A relational schema mapping the i-UR ADE data model to a set of database tables and objects. The relational schema
is build upon and seamlessly integrates with the official 3DCityDB relational schema.
- Java modules that are automatically loaded by the Importer/Exporter and enable the tool to read/write i-UR
ADE enriched datasets and to store and manage i-UR data in the 3DCityDB.

Before using the i-UR ADE extension, you must set up an instance of the 3DCityDB and install the Importer/Eporter tool.
Please follow the installation guidelines provided in the
[3DCityDB online documentation](https://3dcitydb-docs.readthedocs.io/en/latest/intro/index.html). You will need the
[ADE Manager Plugin](https://3dcitydb-docs.readthedocs.io/en/latest/impexp/plugins/ade-manager.html) of the
Importer/Exporter. So, make sure to select this plugin in the setup wizard of the Importer/Exporter.

Afterwards, download a release package of the i-UR ADE extension as ZIP file from the
[releases section](https://github.com/3dcitydb/iur-ade-citydb/releases). Please check the release information to
make sure that the extension can be used with your version of the 3DCityDB and of the Importer/Exporter tool.
Unzip the package into the `ade-extensions` folder inside the installation directory of the Importer/Exporter.

Next, create and register the i-UR ADE relational schema in your 3DCityDB instance. The easiest
way to do this is to use the ADE Manager Plugin and the contents of the unzipped release package.
Follow the steps described in the 3DCityDB online documentation:

* **[How to register the i-UR ADE schema in your 3DCityDB instance](https://3dcitydb-docs.readthedocs.io/en/latest/impexp/plugins/ade-manager.html#user-interface)**

You have to register the schema only once in every 3DCityDB instance that should be able to manage i-UR ADE data.

Finally, use the Importer/Exporter to connect to your i-UR extended 3DCityDB instance and load/extract i-UR data.
If you have correctly unzipped the i-UR ADE extension package inside the `ade-extensions` folder, the tool will
automatically detect the extension and will be able to handle i-UR datasets. Again, the main steps for using an
ADE extension with the Importer/Exporter are described in the 3DCityDB online documentation:

* **[How to the i-UR ADE extension with the Importer/Exporter](https://3dcitydb-docs.readthedocs.io/en/latest/impexp/plugins/ade-manager.html#workflow-of-extending-the-import-export-tool)**

## Technical details
The relational schema for the i-UR ADE has been fully automatically derived from the XML schemas using the ADE Manager
Plugin of the Importer/Exporter. This ADE-to-3DCityDB mapping should work for all CityGML ADE XML schemas and is documented
[here](https://3dcitydb-docs.readthedocs.io/en/latest/impexp/plugins/ade-manager.html#workflow-of-extending-the-import-export-tool).

The Java module for enabling the Importer/Exporter tool to store i-UR ADE data according to the extended relational schema
has been manually implemented against the `ADEExtension` interface and plugin mechanism of the Importer/Exporter.
The `ADEExtension` interface has been introduced with [version 4.0](https://github.com/3dcitydb/3dcitydb/releases/tag/v4.0.0)
of the 3D City Database. The [TestADE repository](https://github.com/3dcitydb/extension-test-ade) demonstrates the
implementation of an artifical ADE and may serve as template for implementing extensions for your own ADEs.

The Java module for parsing and writing i-UR ADE enriched datasets is implemented as extension for the open source
CityGML library citygml4j. More information can be found [here](https://github.com/citygml4j/iur-ade-citygml4j).

## Building from source
The i-UR ADE 3DCityDB extension uses [Gradle](https://gradle.org/) as build system. To build the extension from source,
clone the repository to your local machine and run the following command from the root of the repository. 

    > gradlew installDist

The script automatically downloads all required dependencies for building the module. So make sure you are connected
to the internet. The build process runs on all major operating systems and only requires a Java 8 JDK or higher to run.

If the build was successful, you will find the extension package of the i-UR ADE module under `iur-ade-citydb/build/install`.

## About i-UR
The "i-UR" information infrastructure allows people to analyse and to visualize the situation and problems of urban areas
according to the future vision of each area using geospatial information and virtual reality technologies. The
quantitative analysis and visualization clearly show the cash-flow and spatial plan of the city and promotes
understanding and encourages consensus building among relevant players, e.g. investors, citizens, and developers.

The i-UR ADE is an Application Domain Extension for [OGC CityGML 2.0](http://www.opengeospatial.org/standards/citygml).

## License

The i-UR ADE 3DCityDB extension is licensed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).
See the `LICENSE` file for more details.
