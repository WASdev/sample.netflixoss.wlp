# Liberty Feature for Netflix OSS

This project creates a Liberty feature that packages a sub-set of the [Netflix OSS] projects. In doing so, it aims to demonstrate compatibility between those projects and Liberty, make them more consumable to Liberty users, and provide additional value drawing on the strengths of the Liberty platform.

## Building the Liberty feature

1. Clone this repository.
2. Modify the `wlpDir` property in `gradle.properties` to point to the root directory of a WebSphere Application Server Liberty Profile V8.5.5.2 installation. This can be obtained from http://wasdev.net.
3. Ensure that you have a Java runtime on your path and then run the following Gradle command:

    ```bash
    gradlew :ws-netflixoss:buildEsa
    ```

This will result in the creation of a sub-system archive `ws-netflixoss/build/libs/ws-netflixoss_1.0.0.esa`. You can optionally use the Gradle task `:ws-netflixoss:install` to install the feature in to a local Maven repository.

## Using the Liberty feature

The Liberty feature can be added to a Liberty profile installation using the `featureManager` command as follows:

```bash
wlp\bin\featureManager install ws-netflixoss_1.0.0.esa
```

A server instance wishing to use the feature should add the `usr:ws-netflixoss` feature to the `featureManager` stanza in `server.xml`. The server must be using Java 7.

## Netflix OSS projects supported

The following sections provides details of the Netflix OSS projects currently supported by the Liberty feature.

### Archaius

The inclusion of the [Archaius] project enables a Liberty application to make use of the Archaius API to access dynamic properties. This is particularly important as Archaius is typically the mechanism used to configure other Netflix OSS projects. The feature also adds the ability to specify Archaius properties as part of the Liberty `server.xml`. This is achieved by adding an `archaius` stanza to the server.xml where the nested elements within that correspond to Archaius properties with a dotted notation. For example the following stanza defines two properties `hystrix.threadpool.CustomerServiceClient.coreSize` and `hystrix.threadpool.CustomerServiceClient.maxQueueSize` with values of 2 and 10 respectively. 

```xml
<archaius>
  <hystrix>
    <threadpool>
      <CustomerServiceClient>
        <coreSize>2</coreSize>
        <maxQueueSize>10</maxQueueSize>
      </CustomerServiceClient>
    </threadpool>
  </hystrix>
<archaius>
```

Updates to the `server.xml` are made available dynamically to Archaius.

### Hystrix

The inclusion of the [Hystrix] project enables a Liberty application to make use of the Hystrix command pattern API for providing isolation from latency and failures in downstream services. The feature also introduces a `HystrixConcurrencyStrategy` which means that any JEE context from the initial thread is also available on the thread on which the Hystrix command executes. As per the example above, Hystrix properties can be configured in `server.xml`.

The following command can be used to build a WAR file that exposes a Hystrix event stream:

```bash
gradlew :ws-hystrix-event-stream:war
```

Deploying the WAR file `build\libs\ws-hystrix-event-stream-1.0.0.war` to a server with the Netflix OSS feature exposes a Hystrix event stream at `/hystix.stream`. This allows Hystrix metrics to be displayed in a [Hystrix Dashboard]. The Hystrix Dashboard WAR file available from the Netflix OSS Hystrix project can be deployed to WebSphere Liberty Profile without modification.

## Example usage

The sub-directory `ws-netflixoss-examples` contains some simple technology examples that demonstrate usage of each of the projects. To run the examples:

1. Install the Liberty feature in to a local Maven repository as documented above.
2. Modify the `wlpDir` property in `ws-netflixoss-examples\gradle.properties` to point to the root directory of a WebSphere Application Server Liberty Profile V8.5.5.2 installation using Java 7.
2. Execute the following Gradle command in the `ws-netflixoss-examples` directory:

    ```bash
    gradlew build libertyServer deployWar startServer
    ```

### Archaius

Review the file `ws-netflixoss-examples/src/net/wasdev/wlp/netflixoss/examples/Archaius.java`. This defines a servlet which outputs the value of an Archaius dynamic property called `archaiusServlet.string`. This property has a default value of `Default Value`. If you browse to http://localhost:9090/ws-netflixoss-examples/archaius and enter the credentials `demouser` and `demopassword`, you will see that the value retrieved for the property is actually `From server.xml`. If you open the file `ws-netflixoss-examples/build/wlp/servers/defaultServer/server.xml` you will find that this value comes from the following stanza:

```xml
<archaius>
  <archaiusServlet>
    <string>From server.xml</string>
  </archaiusServlet>
<archaius>
```

If you edit this value you will see the servlet return the new value without having to restart the server.

### Hystrix

Review the file `ws-netflixoss-examples/src/net/wasdev/wlp/netflixoss/examples/Hystrix.java`. This servlet contains three example usages of `HystrixCommand` returning a string. In `checkThreadContextPropagation` the `run` method of the command returns the principals associated with the current thread. If you browse to http://localhost:9090/ws-netflixoss-examples/hystrix you will see that the principal associated with the original servlet thread is successfully propagated to the thread on which the command is executed. In `checkCommandTimeout` you will see the use of a fallback method when the invocation of the `run` method times out. Lastly, in `checkCommandError` you will see the use of a fallback method when an error is propagated from the `run` method.

[ci.gradle]: https://github.com/WASdev/ci.gradle
[Netflix OSS]: http://netflix.github.io/
[Archaius]: https://github.com/Netflix/archaius/wiki
[Hystrix]: https://github.com/Netflix/hystrix/wiki
[Hystrix Dashboard]: https://github.com/Netflix/Hystrix/wiki/Dashboard
