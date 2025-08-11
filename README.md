# Spribe API Test Suite
This is test suite for Spribe test application.

## Technologies
- Java v11
- RestAssured 
- Gradle
- TestNG
- Allure

### Other libraries
- Jackson - JSON data-binding library

## Features
- Allure report
- Parallel testing in 3 threads
- CI/CD with GitHub Actions

## How to run
- `./gradlew clean test`
- `./gradlew allureReport`

## Known issues
- BUG-1 Nulls as Password, ScreenName, Gender, Age, Role values in Create Player response
- BUG-2 Player Update request doesn't update role value
- BUG-3 200 instead of 404 on missing player id in GetPlayer request
- BUG-4 Login field not unique for a Player
- BUG-5 403 instead of 404 on wrong id in Player delete request
- BUG-6 Delete Player request ignores Editor value
- BUG-7 Access control issue - user able to update admin
- BUG-8 Access control issue - user able to delete admin
- BUG-9 Missing input values validations on update request
- BUG-10 Allowed update of non existing player
- BUG-11 Gender could be any value - no input validation
- BUG-12 Password min-max length validation not applied
- BUG-13 User Player able to delete itself
- BUG-14 Duplicate screenNames allowed in update request
- BUG-15 Security - login and password in url parameters
- BUG-16 Security - secrets in clear text in responses
- BUG-17 Security - missing authentication
- BUG-18 Security http instead of https
- BUG-19 No error messages acros application
- BUG-20 Swagger - Wrong status 200 on delete Player endpoint
- BUG-21 Swagger - Wrong status 201 "Created" on get Player endpoint
- BUG-22 Swagger - ID field in the Edit Player request body but not in the PlayerUpdateRequestDto model

