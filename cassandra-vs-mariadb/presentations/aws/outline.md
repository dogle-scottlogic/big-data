# Outline

* Start. Following on from single node comparison of MariaDB and Cassandra.
    * Had shell script spinning up Docker images in an Ubuntu VM
    * Wanted to reproduce, but no documentation on Ubuntu VM
* Reproducing. Decided to automate VM setup
    * Self-documenting
    * Easily reproducible - therefore greater confidence in test environment
    * Selected [Vagrant](https://www.vagrantup.com/).
        * Familiarity
        * Ease of use in development environments
        * Can use against Virtualbox and AWS to spin up multiple VMs in a network - useful for future...
* Jettisoned Docker images.
    * Each database node on its own VM - no need for extra Docker layer.
    * Learning to install and configure the databases was a good exercise in gaining familiarity.

* Vagrant-provisioned cluster in AWS.
    * Lots of faffy manual setup (see old README)
    * Felt messy and unmodularised. Awkward to isolate clusters.
    * At the time we were thinking of spinning up multiple clusters to parallelise testing.
* Terraform.
    * Same people as Vagrant
    * Declarative language was attractive
    * Client-based. Seemed easier at a quick glance to get up and running than with something like Chef (probably a mistake!)
* Downside
    * Immature. Weird missing holes, e.g. lack of `count` support in modules. Multiple copy-pasting of variable definitions. Felt almost there.
    * Not suitable for provisioning, but fine for basic installs and AMI creation (though immature - instances not terminated after AMI creation)
    * Poor error diagnostics. 
        * "Script failed with error code 1" - what script? what nodes?!
    * Few provisioning integrations - immature. Major ones are Chef and shell? But Chef does the same thing?
    * Spaghettified shell scripts for provisioning. Hacky.
* Advantages
    * Active development
    * Very straightforward to setup multiple upstream resources
        * Security groups
        * Keys
        * Simpler README...
* Future
    * Chef? Seems to be lots of 3rd party recipes for database clusters.

    
