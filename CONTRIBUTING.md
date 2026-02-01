# Contributing

Please run `mvn clean install`/`./mvnw clean install` (or `quarkus build` if you have the Quarkus CLI installed) before
opening a (non-draft) PR. It will run all the validations and tests that the CI requires.

## Conventional Commits

This project follows the [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0) spec for commit messages.
This is mandatory and enforced by a check that runs during Pull Requests.

## Dev Mode

> ![IMPORTANT]
> Running the bot, even locally, requires a Discord Token. The simplest way to provide it is via a `.env` file at the
> root of the project. Check the [Running the Bot](./docs/Running.md) instructions for more details

You can use the [Quarkus Dev Mode](https://quarkus.io/guides/dev-mode-differences) during development using one of these
commands:

* `quarkus dev` - if you have the [Quarkus Cli](https://quarkus.io/guides/cli-tooling) installed
* `mvn quarkus:dev` - if you have Maven install
* `./mvwn quarkus:dev`

If you wish to skip checkstyle and spotbugs checks during your development/prototyping phase, you can use the
`skip-check` maven profile. You cannot do so using the Quarkus CLI, so your options would be:

* `mvn -Pskip-checks quarkus:dev`
* `./mvwn -Pskip-checks quarkus:dev`

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

## Releases

This project runs [Semantic Release](https://github.com/semantic-release/semantic-release) on an hourly basis. This
means that, if there is any change that warrants a release (pretty much anything except docs, build updates and such),
it will be picked up and released at the start of the next hour (UTC).