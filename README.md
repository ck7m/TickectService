# TickectService

Ticketing Service system to book the seats for a venue. Command line interface is provided for the demo of the system. It consists of three operations

1. Find the number of available seats
2. Find and hold the best available seats for a customer
3. Commit seats

###Technologies used:
Java, Spring Boot, JPA/Hibernate,In memory HSQL DB, Junit, Mockito

###Build Instructions:
Checkout the project to local. Run the following Command

`./gradlew  (needed only for first time)`

`gradle clean build`

###Run the Application

`java -jar <TicketService Dir>/build/libs/TicketService.jar`

Default timeout is 60 secs. Non-Booked tickets are  expired after default timeout. The timeout can be overridden using command line parameter.

`java -jar <TicketService Dir>/build/libs/TicketService.jar --ticket.timeoutinseconds=40`

Interactive Command line session will be opened.Follow the instructions.

####About the system
Stage is not provided in the interface, so a default Stage with four levels is created and assigned to the TicketingService class.

The records in the InMemory HSQL Db can be viewed by uncommenting the **@PostConstruct** annotation in the `Application.java`
