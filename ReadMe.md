### Movie Rating and Recommendation Service

#### Entails:
1. CRUD methods for movies
2. Rating methods for movies
3. Movie recommendation methods

#### Tech Stack
- SpringBoot
- MySQL DB
- FlyWay Migrations - to upload sample data
- Mockito, Junit - for unit testing

#### How to run
1. Clone the repo
2. Add/amend the following properties to `resources > application.yml` file
    - `spring.datasource.url`
    - `spring.datasource.username`
    - `spring.datasource.password`
3. Run the application
4. Runs on 8081 port by default, can be amended in `application.yml` file
5. Sample data is uploaded using FlyWay migrations, can be amended in `V1__Create_Table.sql` file