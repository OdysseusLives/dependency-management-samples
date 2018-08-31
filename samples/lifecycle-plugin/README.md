## Module Lifecycle plugin POC
### Handling module lifecycle

This test project demonstrates a plugin enabling a custom lifecycle for modules. A module may be in 3 different
states:

* alive, meaning that the module is still relevant and can be used without problem
* deprecated, meaning that the module is no longer supported, and an upgrade would probably make sense
* blacklisted, meaning that the module should not be used, because it has been blacklisted, for whatever reason (can be
because of a vulnerability, a critical bug, ...)

It's perfectly valid to have multiple versions of a module to be alive at the same time, it only depends on the
maintained branches. So this plugin will:

* register an attribute called `org.example.gradle.lifecycle`, of type `Lifecycle`, which can have 3 different values: `ALIVE`,
`DEPRECATED` or `BLACKLISTED`.
* doesn't set any preferred lifecycle for configurations by default, so the standard behavior is to resolve like usual
* except for blacklisted modules, see below
* whenever the attribute is set on a consumer configuration, it tries to honor it. In particular:
  * if requesting `ALIVE` modules, only `ALIVE` modules will be returned. `DEPRECATED` and `BLACKLISTED` modules are no
longer allowed in the graph
  * if requesting `DEPRECATED` modules, we may return either `ALIVE` or `DEPRECATED` modules
  * if requesting `BLACKLISTED` modules, we will always fail (see below)

In short, the lifecycle attribute allows us to set the minimal accepted state. Saying `DEPRECATED` doesn't mean we want
deprecated modules, but that `DEPRECATED` is acceptable.

### Handling blacklisted modules

By default, this plugin _breaks the build_ whenever a blacklisted module is found in the graph. This is done to make
sure we are _reproducible by default_ and realize that the resulting graph contains blacklisted modules. It forces the
user to change its build script to avoid the problem (we could implement a variation of this to only issue warnings, but
it would weaken the semantics of blacklisting).

### Deprecated modules

The plugin will also warn the user about deprecated modules found in the graph:

> Configuration compileClasspath resolved a deprecated module: com.acme:testB:3

This is a call for action, consistent with the semantics of deprecated modules.

### Implementation notes

Failing blacklisted modules happens in an `afterResolve` hook.

### Usage of the demo

- `gradle help` : lists all versions of the modules with their attributes
- `gradle` : resolves the `compileClasspath`, and displays the `dependencyInsight` report
