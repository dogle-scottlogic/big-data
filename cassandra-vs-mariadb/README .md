# Cassandra vs MariaDB

### Data Generator

The data generator package is responsible for creating Events. The package is dependant on two external files:
 - data.json
 - application.conf
 
These are expected in the folder:
```sh
sr/main/resources
```

#### data.json
The data.json file contains mock data that the system uses to generate events. By default the file contains data to generate Clients and Hats. 

#### application.conf
The application.conf file is the configuration file for the system and has the following options:

- SEED = The int value for seeding the Java random class
- NUM_CLIENTS = The number of mock clients the system should generate
- ORDER_CACHE_SIZE = The total number of created orders to cache, the system uses these orders to apply update and delete events.
- SLEEP = The time to sleep between each event in milliseconds
- MAX_PRODUCTS = The maximum quantity of products to assign to a line item
- MAX_LINE_ITEMS = The max number of line items to assign to an order
- DATA_FILE_PATH = Where the system expects to find the data.json file
- EVENT_GEN_MODE = The mode to run the event generator in. 'fixed' runs a set number of events one after the other, 'random' creates random events until the 'stop' command is run
- NUM_FIXED_EVENTS = the number of each event type to generate when running in 'fixed' mode
- DELETE_CHANCE =  Deletes can be weighted to occure less frequently. This var sets the chance that the system will generate a DELETE event. e.g. 5 = 1 in 5 chance.
- QUEUE_NAME = Name of the Rabbit MQ queue to connect to*
- HOST_NAME = The host the queue should use*
- DURABLE = Dont forget the queue if rabbitMQ crashes*
- EXCLUSIVE = Queue can only be accessed by current connection*
- AUTO_DELETE = exchange is deleted when all queues have finished using it*
- EXCHANGE = exchange where the message is sent: default (Empty string) and amq.direct
- MAX_PRICE = The maximum price of a hat
- MIN_PRICE = The minimum price of a hat
- MAX_WEIGHT = The maximim weight of a hat
- MIN_WEIGHT = The minimim weight of a hat
**\* Only applicable if using RabbitMQ**

To run the data generator from the command line interface run: 
```sh
/src/main/java/dataGenerator/Main.java
```

The command options available are:
- Run = Start generating Events
- Stop = Stop generating Events
- Exit = Close the program
\*Note - Running the program via command line expects to use an instance of RabbitMQ to send events to.

To run the package programatically create an instance of:
```sh
/src/main/java/dataGenerator/generators/EventGenerator.java
```

and call the *getNextEvent()* method. This will return an event in the form:
```json
{
    "type":"<type>",
    "data":{<data>}
}
```

**type** options are:
*CREATE
*READ
*UPDATE
*UPDATE_STATUS
*DELETE

**data** will either be the OrderID in the case of DELETE, UPDATE_STATUS and READ events or a complete order object in the following format:
```json
{
   "data":{  
      "id":"4612d1c0-212d-40a0-813d-a17543296896",
      "lineItems":[  
         {  
            "id":"561cea89-1fd8-4e94-800b-626ca27149f9",
            "product":{  
               "id":"414dfe60-3d7f-445c-8b82-7620f6cea367",
               "productType":"HAT",
               "name":"Beanie",
               "weight":594.81,
               "price":6.29,
               "colour":"Burnt Sienna",
               "size":"XXL"
            },
            "quantity":9,
            "linePrice":56.61
         },
         {  
            "id":"cde58d2b-0f60-45d4-b46a-94526f001ec4",
            "product":{  
               "id":"213a29b3-e411-4684-8925-4aca4a1d1e22",
               "productType":"HAT",
               "name":"Flat",
               "weight":168.9,
               "price":64.09,
               "colour":"Puce",
               "size":"S"
            },
            "quantity":8,
            "linePrice":512.72
         }
      ],
      "client":{  
         "id":"4612c308-9158-4bbe-bc41-52b25647a2e6",
         "name":"Katherine Sims",
         "address":"240, frantic Drive, Worcester",
         "email":"KatherineSims@fakemail.com"
      },
      "status":"ORDERED",
      "subTotal":2779.2299999999996,
      "date":1481537772610
    }
}
```

Event types can be ammended and added to via:
```sh
/src/main/java/dataGenerator/enums/Enums.java
```

### Storers

The Storers package is responsible for handling events and passing them to the databases. Storers is split into two parts
- consumers
- storers

**consumers** holds classes for pulling data from RabbitMQ, this can be extended to inculde other data sources.
**storers** holds storers for handling events with database types and also a timer class for timing events.
The Class *Storer* is an interface only and requires that all implentations contain the method *messageHandler()*. This method takes an event as a JSONObject and executes that event against a specific database.



































