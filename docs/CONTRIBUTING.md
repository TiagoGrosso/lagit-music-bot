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

Builds will fail if your code has these issues. Therefore, it is *highly encouraged* that you:

* Install the Spotbugs plugin in your IDE of choice
* Configure the plugin to use the local exclusion file `config/spotbugs-exclude-filter.xml` to avoid some false
  positives due to Quarkus' dependency injection

> [!WARNING]
> If you compile your code through Intellij, you will get generated sources with helpers for detecting Nullability.
> This is due to the usage of @NotNull and @Nullable across the repo.
>
> This, however, doesn't play too nicely with some of Spotbugs checks, and you might get some Redundant Null Check
> errors in your Spotbugs plugin. These go away with an `mvn clean install`