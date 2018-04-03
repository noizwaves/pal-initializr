# PAL Initializr

Spring Initializr based project generation observing PAL opinions.

## Dependencies

- This is built using a fork of [Spring Initializr](https://github.com/noizwaves/initializr)

1. `cd ~/workspace`
1. Install `initializr` fork into local Maven cache via:
    1. `git clone git@github.com:noizwaves/initializr.git`
    1. `cd initializr`
    1. `git checkout enhanced-customization`
    1. `./mvnw clean install`
    1. `.cd`

## Building & running locally

1. Install above dependencies
1. `cd ~/workspace`
1. `git clone git@github.com:noizwaves/pal-initializr.git`
1. `cd pal-initializr`
1. `./gradlew bootRun`
