# Getting Started

Projects created by start.spring.io contain Spring Boot, a framework that makes Spring ready to work inside your app, but without much code or configuration required. Spring Boot is the quickest and most popular way to start Spring projects. If you have any questions, please contact zdu.strong@gmail.com.

## Development environment setup
1. From https://adoptium.net install java v17, and choose Entire feature.<br/>
2. From https://code.visualstudio.com install Visual Studio Code. Next, install extension "Extension Pack for Java" and "XML".<br/>
3. From https://dev.mysql.com/downloads/installer install MySQL, the password of the root user is set to 123456.

## Available Scripts

In the project directory, you can run:

### `./mvn clean compile spring-boot:run`

Let’s build and run the program. Open a command line (or terminal) and navigate to the folder where you have the project files. We can build and run the application by issuing the command.

The last couple of lines here tell us that Spring has started. Spring Boot’s embedded Apache Tomcat server is acting as a webserver and is listening for requests on localhost port 8080. Open your browser and in the address bar at the top enter http://localhost:8080. You should get a nice friendly response like this:
"Hello, World!"

### `./mvn clean package`

To generate executable jar package

### `./mvn clean test`

Run unit tests

### `./diff`

Generate database version upgrade sql.<br/>
For now, only windows x64 system and macos x64 system are supported. If you need to support others, please compile the source code yourself.

Its source code is in the ".mvn/diff" folder.<br/>

### `./mvn clean compile sql:execute`

Delete all tables in the development database

### `./mvn versions:display-dependency-updates`

Check that a new version of the dependency is available<br/>

The following dependencies are currently unable to continue to be upgraded:<br/>

    <dependency>
        <groupId>org.liquibase</groupId>
        <artifactId>liquibase-core</artifactId>
    </dependency>

    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
    </dependency>

## Notes - How to use vscode

1. Press "F5" to start.<br/>
2. In debug mode, "F10" Step over, "F11" Step into, "Shipt+F11" Step out, "F5" Skip breakpoint.<br/>

## Notes - jinq - Things to note

Some experience in use, if you already know it, you can skip it.
1. Before the new JPA entity, query all the required data and save the previous entity.
2. Never use getList of jpa entity, always query data from the database, avoid memory overflow and data expiration.
3. Jinq does not support nested select statements(select * from (select * from user)). This doesn't matter because it doesn't need to be used.
4. Jinq does not support union and union all. It doesn't matter, we can add database association table for query.
5. Jinq doest not support right join. This does not matter, we can use left join.
6. Jinq doest not support outer join. It doesn't matter, we can add database association table for query.

## Notes - jpa - create entity

    this.persist(userEntity);

## Notes - jpa - update entity

    this.merge(userEntity);

## Notes - jpa - delete entity

    this.remove(userEntity);

## Notes - jinq - getOnlyValue

get only one element, like this:

    this.UserEmailEntity().getOnlyValue();

## Notes - jinq - findFirst

get the first element, like this:

    this.UserEntity().findFirst();

## Notes - jinq - toList

get array

    this.UserEntity().toList();

get model array

    this.UserEntity().map(s -> this.userFormatter.format(s)).toList();

## Notes - jinq - pagination

    JPAJinqStream<UserEntity> stream = this.UserEntity();
    return new PaginationModel<>(1, 10, stream, (s) -> s.getUsername());

## Notes - jinq - exists

    this.UserEntity().exists();

## Notes - jinq - where

    this.UserEntity().where(s -> s.getUsername().equals("tom"));

## Notes - jinq - and

    this.UserEntity().where(s -> s.getUsername().contains("jerry") && s.getUsername().contains("tom"));

    this.UserEntity().where(s -> s.getUsername().contains("jerry")).where(s -> s.getUsername().contains("tom"));

## Notes - jinq - or

    this.UserEntity().where(s -> s.getUsername().contains("tom") || s.getUsername().contains("jerry"));

## Notes - jinq - or of array

    public long getUsers(List<String> names) {
        JPAJinqStream<UserEntity> stream = this.UserEntity();
        JPAJinqStream<UserEntity> streamOne = stream;
        for (String name : names) {
            JPAJinqStream<UserEntity> streamTwo = streamOne.where(s -> s.getUsername().contains(name));
            stream = stream == streamOne ? streamTwo : stream.orUnion(streamTwo);
        }
        return stream.count();
    }

## Notes - jinq - inner join

    this.UserEmailEntity().where(s -> s.getUser().getUsername().equals("tom"));

    this.UserEntity().joinList(t -> t.getUserEmailList());

    this.UserEmailEntity().join(s -> JinqStream.of(s.getUser()));

    this.UserEntity().join((s, t) -> t.stream(UserEmailEntity.class));

## Notes - jinq - left join

    this.UserEmailEntity().leftOuterJoin(s -> JinqStream.of(s.getUser()));

    this.UserEntity().leftOuterJoinList((s) -> s.getUserEmailList());

    this.UserEntity().leftOuterJoin((s, t) -> t.stream(UserEmailEntity.class),
        (s, t) -> s.getId().equals(t.getId()));

## Notes - jinq - group by

Group by id, username

    var stream = this.UserEntity().group(s -> new Pair<>(s.getId(), s.getUsername()), (s, t) -> t.count());
    return new PaginationModel<>(1, 15, stream, (s) -> {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("id", s.getOne().getOne());
        map.put("username", s.getOne().getTwo());
        map.put("countOfThisGroup", s.getTwo());
        return map;
    });

## Notes - jinq - order by

Sort by username first, then by id

    this.UserEntity().sortedBy(s -> s.getId()).sortedBy(s -> s.getUsername());

## Notes - jinq - order by with complex statistical conditions

    this.OrganizeEntity().select((s, t) ->
        new Pair<>(
            s,
            t.stream(OrganizeEntity.class)
                .where(m -> m.getOrganizeShadow().getName().contains(s.getOrganizeShadow().getName()))
                .count()
        )
    )
    .sortedBy(s -> s.getOne().getId())
    .sortedBy(s -> s.getTwo())
    .toList();

## Notes - jinq - Use subqueries in where

    this.UserEntity().where((s, t) ->
        t.stream(UserEmailEntity.class).where(m -> m.getUser().getId().equals(s.getId())).exists()
    );

    this.UserEntity().where( s ->
        JinqStream.from(s.getUserEmailList()).where(m -> m.getEmail().equals("tom@gmail.com")).exists()
    );

## Notes - jinq - Format entity to model

All data can be obtained and set to the model, support any structure

    public UserModel format(UserEntity userEntity) {
        var userId = userEntity.getId();
        var email = this.UserEmailEntity()
                .where(s -> s.getUser().getId().equals(userId))
                .where(s -> !s.getIsDeleted())
                .sortedDescendingBy(s -> s.getId())
                .sortedDescendingBy(s -> s.getUpdateDate())
                .select(s -> s.getEmail())
                .findFirst()
                .orElse("");
        var userModel = new UserModel().setId(userEntity.getId()).setUsername(userEntity.getUsername()).setEmail(email);
        return userModel;
    }

## Notes - Params date

javascript:

    axios.get("/abc", {
        params: {
            date: new Date()
        }
    })

java:

    import org.springframework.format.annotation.DateTimeFormat;
    import org.springframework.format.annotation.DateTimeFormat.ISO;
    @GetMapping("/abc")
    public ResponseEntity<?> abc(@RequestParam @DateTimeFormat(iso = ISO.DATE_TIME) Date date) {
        return ResponseEntity.ok().build();
    }

    URI url = new URIBuilder("/abc").setParameter("date", this.objectMapper.writeValueAsString(new Date()).substring(1, 25)).build();
    ResponseEntity<Object> response = this.testRestTemplate.getForEntity(url, Object.class);
    assertEquals(HttpStatus.OK, response.getStatusCode());

## Notes - id - Generate unique ordered uuid of version 1

    import com.fasterxml.uuid.Generators;

    Generators.timeBasedGenerator().generate().toString()

## Notes - Accept timezone from javascript, then convert to Time Zone from UTC. Time Zone from UTC can be passed as a parameter to database methods.

javascript:

    const { timeZone } = Intl.DateTimeFormat().resolvedOptions()

    Asia/Shanghai

java:

    var timeZone = this.timeZoneUtils.getTimeZone("Asia/Shanghai");

    +08:00

# Notes - Throws an exception with the specified status code

    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The specified task does not exist");

## Notes - multi-process programming

    var command = new ArrayList<String>();
    if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
        command.add("cmd");
        command.add("/c");
    } else {
        command.add("/bin/bash");
        command.add("-c");
    }
    command.add("npm --version");
    var processBuilder = new ProcessBuilder(command)
                .inheritIO()
                .directory(this.storage.createTempFolder());
    processBuilder.environment().put("CUSTOM_ENV", "custom value");
    var exitValue = processBuilder.start()
            .waitFor();
    if (exitValue != 0) {
        throw new RuntimeException("Failed!");
    }

## Learn More

1. Jinq (http://www.jinq.org/docs/queries.html)
2. Learn SQL (https://www.sqlcourse.com)
