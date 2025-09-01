# MovieCatalog

A simple movie catalog with a REST API: **upload** video files, **store** metadata, **compute a ranking** (based on DigiKat data), **download** files, and **search with pagination and sorting**.

---

## Features

* **Create/Update movie (multipart)**
  Stores the DB record and the physical file. File extension is inferred from content type (Apache Tika-like detection).
* **Delete movie**
  Removes the DB record and the underlying file.
* **Download movie**
  Streams bytes with `Content-Disposition: attachment` and `X-Content-Type-Options: nosniff`.
* **Search**
  Pagination + sorting by `fileSize` or `rankScore` (ASC/DESC).
* **Recalculate ranking**
  Recomputes `rankScore` for all movies.
* **Validation**
  File size must be **≤ 1 GB**; title must be unique, etc.

---

## Architecture & Profiles

* **REST layer:** `MovieResource` (`/api/movies`) – request mapping only; delegates to the service.
* **Domain service:** `MovieService` – business logic: storing/deleting/streaming files, ranking, pagination.
* **File storage:** `FilesStorage` (SPI) with `LocalFileStorage` (profiles **local/test**), using `storage.local.root` as the root folder.
* **DigiKat clients:**

  * `DigiKatClientMock` (profiles **local/test**) – loads JSON files from `classpath*:/mocks/**/*.json`.
  * `DigiKatClientHTTP` (profile **prod**) – calls `${digikat.base-url}/ranking?film={title}` using `WebClient`.
* **Ranking:** `MovieRankScoreCalculator` – example rules:

  * file size **≤ 200 MB** → **+100**
  * `productionType` in `{0, 2}` → **+200**
  * available at **Netflix** → **−50**
  * `usersScore == wybitny` (Polish “outstanding”) → **+100**
* **Validation:** `MovieValidator` – checks 1 GB limit, duplicate titles, etc.

> **Note on DigiKat enums:** `usersScore` values are Polish (`mierny`, `dobry`, `wybitny`), and `availableAtVODs` includes `netflix`, `youtube`, `disney`, `hbo`.

---

## Requirements

* **Java 17+**
* **Maven 3.9+** (the repo includes `mvnw` wrapper)
* For **prod**: configure `digikat.base-url` (environment variable or property)

---

## Configuration

### Profiles

* **local / test**

  * Storage: `LocalFileStorage` (writes under `storage.local.root`, auto-creates the folder)
  * DigiKat: `DigiKatClientMock` (reads JSON from `resources/mocks`)
* **prod**

  * DigiKat: `DigiKatClientHTTP` (uses `digikat.base-url`)

### Key properties


# Local/Test — where to save uploaded files
storage.local.root=/absolute/path/to/storage

# Prod — external DigiKat API base URL
digikat.base-url=https://example.com/digikat


### Suggested `application-test.yml`


spring:
  datasource:
    url: jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL
    username: sa
    password:
  jpa:
    hibernate:
      ddl-auto: create-drop
  flyway:
    enabled: false


---

## Run

### Local (profile `local`)


./mvnw spring-boot:run \
  -Dspring-boot.run.profiles=local \
  -Dspring-boot.run.jvmArguments="-Dstorage.local.root=/tmp/movies"


`LocalFileStorage` will create the folder if it doesn’t exist.

### Tests


# unit tests
./mvnw test

# unit + integration (if you add Failsafe for IT)
./mvnw verify


> For code coverage, add **JaCoCo** and generate reports via `mvn verify` or IntelliJ “Run with Coverage”.

---

## API

**Base path:** `/api/movies`

### 1) Create/Update (multipart)

`POST /api/movies`

**Form fields**

* `file` – required, binary
* `title` – required (1..300 chars)
* `director` – required
* `year` – required (`YYYY`)

**Response:** `MovieDetailsDTO`


curl -X POST http://localhost:8080/api/movies \
  -F "file=@/path/to/video.mp4" \
  -F "title=The Matrix" \
  -F "director=The Wachowskis" \
  -F "year=1999"


### 2) Search (paged & sorted)

`GET /api/movies?sortBy={fileSize|rankScore}&dir={asc|desc}&page=0&size=10`

**Response:** `Page<MovieDetailsDTO>`

Examples:


# by size descending
curl "http://localhost:8080/api/movies?sortBy=fileSize&dir=desc&size=50"

# by rank ascending, page 1
curl "http://localhost:8080/api/movies?sortBy=rankScore&dir=asc&page=1&size=10"


### 3) Download

`GET /api/movies/download?title={title}`

Returns `200 OK` + `attachment` + bytes stream.


curl -OJ "http://localhost:8080/api/movies/download?title=The%20Matrix"


### 4) Delete

`DELETE /api/movies/{title}`

Removes the DB record and the file.

### 5) Recalculate ranking

`POST /api/movies/recalculate`

Recomputes rank for all movies, save and returns a list of updated DTOs.

---

## Data model

`MovieEntity`

* `title` *(PK)*
* `productionYear`
* `director`
* `rankScore`
* `fileSize`
* `fileId`

`MovieDetailsDTO` mirrors the entity fields (used in API responses).

`DigiKatResponse`

* `title`
* `productionType` *(int)*
* `availableAtVODs` *(list of enums)*
* `usersScore` *(enum)*
* `lastUsersScoreUpdate` *(ISO date string)*

---

## Validation & Errors

* **File too large (> 1 GB):** validation error (custom `GlobalValidationException`)
* **Movie already exists / not found:** validation error
* **Invalid sort field:** `IllegalArgumentException` (only `fileSize` or `rankScore` are allowed)

---

## Local DigiKat mock data

For **local/test** profiles, place JSON files under `src/main/resources/mocks/**`. The mock client loads:
