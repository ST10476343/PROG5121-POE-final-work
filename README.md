# QuickChat – PROG5121 POE (Parts 1, 2 and 3)

QuickChat is a Java console application that registers and logs in a user, sends messages, stores messages in a JSON file, and provides message searching and reporting features.

## Requirements
- JDK 21 or later
- Apache Maven 3.8 or later
- Internet access the first time Maven downloads the JUnit test dependency

## Open and run
Open this folder as a Maven project in NetBeans or IntelliJ.

From a terminal in the project folder, run:

```bash
mvn clean test
mvn exec:java
```

The application asks the user to register and log in before showing the main menu.

## Main menu
1. Send Messages
2. Show recently sent messages
3. Quit
4. Stored Messages

The Stored Messages menu provides options to display sender/recipient details, find the longest stored message, search by message ID, search by recipient, delete by message hash, and display a report.

## Message storage
Stored messages are written to `messages.json` in the folder from which the application is run. The file is created automatically when the first message is stored. The application reads this JSON file when it starts the message manager.

## Tests
The test suite includes the registration/login tests from Part 1, the message validation and JSON tests from Part 2, and the array/search/delete/report tests for Part 3.

## Notes
- User registration requires a username containing an underscore and no more than five characters, a password of at least eight characters containing an uppercase letter, a digit and a special character, and a South African cellphone number in `+27` format.
- Message recipients must use `+27` followed by nine digits.
- Messages may contain up to 250 characters.
- Message arrays are populated as users send, disregard or store messages; the application does not pre-populate production arrays with lecturer test data.
