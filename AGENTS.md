# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Low
* IDE and level of expertise: IntelliJ IDEA, Low

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Java coding standard

All Java code in this project must follow the `cs2103t-java-coding-standard` skill, including its SE-EDU Basic and Intermediate rules. Before writing, editing, or reviewing Java code, agents must read and apply that skill's current `SKILL.md` and `references/rules.md`. For topics that the SE-EDU rules do not cover, follow the Google Java Style Guide; the SE-EDU rules take precedence if the guides differ.

Preserve program behavior during style-only changes, avoid unrelated cleanup, and do not rename public APIs or externally consumed identifiers solely for style without checking compatibility. Run the project's available formatter and style checks, then run the relevant Gradle tests after Java changes. Report any remaining violations with file and line locations, distinguishing verified violations from judgment calls.

## Testing

Maintain JUnit tests for approximately the top 50% highest-value methods, prioritizing complex, core, and critical business logic. After every code change, update the JUnit tests as needed and run the Gradle test task to ensure the project continues to meet this 50% test coverage target.

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
