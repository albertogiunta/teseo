# Teseo
The Android client for the [Arianna](https://github.com/albertogiunta/arianna/) system.

## Configuration
In order for the App to be working, you need to change the IP address associated with the machine where the cells are running on.

You can change this information in the ```build.gradle``` file, at line ```22```:

```buildConfigField "String", "IP_ADDRESS", "\"192.168.0.111\""```
