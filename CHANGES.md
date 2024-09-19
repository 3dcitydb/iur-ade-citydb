Change Log
==========

### 2.1.0 - 2024-09-19

#### Changes
* **Breaking:** Java 11 is now the minimum required version for using the i-UR ADE extension.
* Updated impexp-client-gui to 5.5.0.
* Updated energy-ade-citygml4j to 1.5.0.

### 2.0.0 - 2021-10-08

#### Changes
* Updated PostgreSQL database schema to use 64-bit `bigint` as data type instead of 32-bit `integer` for all
  primary key and foreign key columns. Hence, this release can only be used with version 4.2.x of the 3DCityDB.
  It is not compatible with previous versions of the 3DCityDB anymore.
* Updated impexp-client-gui to 5.0.0
* Updated iur-ade-citygml4j to 1.4.2.

### 1.5.0 - 2021-04-30

#### Changes
* Updated impexp-client to 4.3.0
* Updated iur-ade-citygml4j to 1.4.1.