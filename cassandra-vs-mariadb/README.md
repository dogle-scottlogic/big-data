# Cassandra vs MariaDB

### Getting Started

These instructions are only suppored in git bash.

#### Install packages

Ensure [Terraform](https://www.terraform.io/downloads.html) is installed and on your `PATH`. 

#### Setup AWS access

1. Create an AWS account and sign in.
1. [Create a new user](https://console.aws.amazon.com/iam/home?#users) with programmatic access. Create a new user group with AmazonEC2FullAccess policy. You will be displayed the 'Access Key ID' and the 'Secret Access Key'. Create `terraform/terraform.tfvars` with these values:
```
access_key=<ACCESS_KEY>
secret_key=<SECRET_KEY>
```
1. In git bash, ensure that an `id_rsa` / `id_rsa.pub` pair exist at `~/.ssh`.

#### Define your clusters

* In `terraform/main.tf` a 3 node cluster, `cluster_3`, is defined for you. This cluster will contain 3 Cassandra nodes, 3 MariaDB nodes, and a test client.
* If you want additional clusters, copy and paste the `module` and 3 `output` blocks. Change the `num_nodes` parameter to suit.

#### Spinning up your clusters

From within the `terraform/` directory, run `terraform plan`. This will show you what AWS resources will be created and deleted if you choose to apply this config. If everything is ok, run `terraform apply`. Once this completes, you can see the public IPs you can use to access the cluster with `terraform output`. To log in, run `ssh ubuntu@<PUBLIC_IP>`. No password will be required.

#### Deleting a cluster

Delete its block from `main.tf`. Run `terraform plan` / `terraform apply`.

To delete all terraform resources run `terraform destroy`. *BE VERY CAUTIOUS ABOUT DOING THIS!*

#### Running Analysis tests

After running `terraform apply` as described above then the analysis test script can be run.

To run tests on a particular cluster
```
./run-tests.sh <CLUSTER_NAME> <TESTS_TO_RUN>
```

e.g.

```
./run-tests.sh cluster_3 showdown.RandomEventTest
```

This job will run the tests on the cluster and then copy the results into a timestamped directory in `out/`.

### Data Generator

The data generator package is responsible for creating Events. The package is dependant on two external files:
- data.json
- application.conf
 
These are expected in the folder:
```sh
src/main/resources
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
- DELETE_CHANCE =  Deletes can be weighted to occur less frequently. This var sets the chance that the system will generate a DELETE event. e.g. 5 = 1 in 5 chance.
- QUEUE_NAME = Name of the Rabbit MQ queue to connect to*
- HOST_NAME = The host the queue should use*
- DURABLE = Don't forget the queue if rabbitMQ crashes*
- EXCLUSIVE = Queue can only be accessed by current connection*
- AUTO_DELETE = exchange is deleted when all queues have finished using it*
- EXCHANGE = exchange where the message is sent: default (Empty string) and amq.direct
- MAX_PRICE = The maximum price of a hat
- MIN_PRICE = The minimum price of a hat
- MAX_WEIGHT = The maximum weight of a hat
- MIN_WEIGHT = The minimum weight of a hat
**\* Only applicable if using RabbitMQ**

Settings in application.conf can be set programmatically via the *Settings* class.

#### Running
To run the data generator from the command line interface run: 
```sh
/src/main/java/dataGenerator/Main.java
```

The command options available are:
- Run = Start generating Events
- Stop = Stop generating Events
- Exit = Close the program
\*Note - Running the program via command line expects to use an instance of RabbitMQ to send events to.

To run the package programmatically create an instance of:
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

Event types can be amended and added to via:
```sh
/src/main/java/dataGenerator/enums/Enums.java
```

### Storers

The Storers package is responsible for handling events and passing them to the databases. Storers is split into two parts
- consumers
- storers

**consumers** holds classes for pulling data from RabbitMQ, this can be extended to include other data sources.
**storers** holds storers for handling events with database types and also a timer class for timing events.
The Class *Storer* is an interface only and requires that all implementations contain the method *messageHandler()*. This method takes an event as a JSONObject and executes that event against a specific database.
There are currently three storers in use: Cassandra, Maria and Combo. Cassandra and Maria are each responsible for an individual database. Combo will run against either Cassandra or MariaDB dependant on the **DBType** enum passed in on creation of the class. 
This Class has been created in an attempt to keep the code the same as much as possible for both databases. Combo implements threaded execution of queries with a connection pool for MariaDB which can be given a max connections value.

### Conveyor

The last package is the Conveyor. This contains the Conveyor class which is responsible for getting events from the data generator and passing them to a storer. The conveyor has one function **processEvents** which takes a storer, a event generator and a number of events to process.

### CSVLogger

An instance of the CSVLogger is given to a storer on construction. The parameter is manditory however if you do not wish to log results then creating the CSVLogger with **doNotLog** parameter set to true will ensure nothing is logged. 
To log results create the CSVLogger with a folder name and file name. The folder must exist however the file does not need to. The CSVLogger can be created with an optional Test name to be used in the log, if this is not set the file name will be used instead.
The test name (testID) can be set after construction for logging a second set of tests to the same file. 

## Test Example

Run a thousand random events with a database containing 1000 orders:

First task is to set the order cache size (see **Things to watch out for**) and create a new logger with the doNotLog flag set.
```java 
Settings.setIntSetting("ORDER_CACHE_SIZE", 1000);
CSVLogger log = new CSVLogger(true);
```
Next we create our two storers, one for Cassandra and one for MariaDB. In this example we will user the combo storer for both.
```java
Combo cs = new Combo(log, DBType.CASSANDRA);
Combo ms = new Combo(log, DBType.MARIA_DB);
```
now run up a new event generator for each database and with just CREATE events and process 1000 events. 
```java
eventGenerator = Conveyor.initialiseEventsGenerator(new Enums.EventType[]{ Enums.EventType.CREATE});
Conveyor.processEvents(1000, cs, eventGenerator);
eventGenerator = Conveyor.initialiseEventsGenerator(new Enums.EventType[]{ Enums.EventType.CREATE});
Conveyor.processEvents(1000, ms, eventGenerator);
```
That's both databases seeded with the same 1000 events. Now some more setup. 
- We create a new logger, this time with a file path for where we want the log saved
- We set the generator mode to random
- We reinitialize the thread pool for each storer
- We set the list of Events in the event generator to include all events this time.

```java
log = new CSVLogger(absPath, "OneThousandRandomEvents");
Settings.setStringSetting("EVENT_GEN_MODE", "random");
cs.setLogger(log);
cs.reinitThreadPool();
ms.setLogger(log);
ms.reinitThreadPool();
eventGenerator.setEvents(new Enums.EventType[]{ });
```
lastly we run the conveyor again for each database type.
```java
Conveyor.processEvents(1000, cs, eventGenerator);
Conveyor.processEvents(1000, ms, eventGenerator);
```

## Things to watch out for

##### Event Generator Modes
The event generator expects a list of at least two events if running in "Random" mode. If you wish to pass in only a single event (as in the above example) ensue that the **EVENT_GEN_MODE** flag in the application.conf file is set to "fixed" or do so programitaclly before running the Conveyor:
```java 
Settings.setStringSetting("EVENT_GEN_MODE", "fixed");
``` 
##### Order Cache Size
The Event Generator maintains a cache of orders that is used to generate other events. When pre-populating a database with orders be aware that the Event Generator will only reference the orders stored in the cache. This means that if the cache is set to say twenty, the Event Generator will only generate *update* events for, at most, the twenty events stored in the cache. To change this setting alter the **ORDER_CACHE_SIZE** value in the application.conf or do so programitaclly before running the Conveyor:
```java 
Settings.setIntSetting("ORDER_CACHE_SIZE", 5000);
``` 

##### Thread Pool Re-initialization
When processing events using the combo storer the thread pool is shutdown at the end of each run in order to ensure that all threads have executed before exiting the program. If running the same combo storer twice (as in the above example) then ensure that
```java
comboStorer.reinitThreadPool();
```
is called before re-running the Conveyor.  

































