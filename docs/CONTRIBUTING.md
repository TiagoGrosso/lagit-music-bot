# Contributing

Please run `mvn clean install`/`./mvnw clean install` (or `quarkus build` if you have the Quarkus CLI installed) before
opening a (non-draft) PR. It will run all the validations and tests that the CI requires.

## Checkstyle

This project uses [Checkstyle](https://checkstyle.sourceforge.io/) with a local configuration file for consistent
formatting.

Builds will fail if your code format doesn't adhere to the standard. Therefore, it is *highly encouraged* that you:

* Install the Checkstyle plugin in your IDE of choice
* Configure the plugin to use the local `config/checkstyle.xml` file as the active ruleset
* Import the checkstyle file to your IDE's code style settings

[This page](https://wiki.lyrasis.org/display/DSPACE/Code+Style+Guide#CodeStyleGuide-IDESupport) covers the steps above
for the most used IDEs.

## Spotbugs

This project uses [Spotbugs](https://spotbugs.github.io/) to detect some known code issues and vulnerabilities.

Builds will fail if your code has these issues. Therefore, to help you identify them proactively, you can:

* Install the Spotbugs plugin in your IDE of choice
* Configure the plugin to use the local exclusion file `config/spotbugs-exclude-filter.xml` to avoid some false
  positives due to Quarkus' dependency injection

Otherwise, the build pipeline (`mvn clean install`) will warn you of what you need to fix

## Error Prone

This project uses [Error Prone](https://errorprone.info/)  to detect common bugs.

If you are used to compiling the project in your IDE, you can install and configure the Error Prone plugin in Intellij
or Eclipse. Check their respective section in
the [Error Prone Installation Guide](https://errorprone.info/docs/installation).

## NullAway

This project uses [NullAway](https://github.com/uber/NullAway) to detect potential Null Pointer Exceptions. By default,
every field and method is non-nullable. If you wish it to be nullable, you have to annotate it with `@Nullable`. When
building the project through Maven, you will get errors if you are trying to pass `null` to non-nullable fields.