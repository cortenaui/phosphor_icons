# Publishing

This document covers what a maintainer needs to do to ship a release. End users do not need to read this — they only need [INSTALLATION.md](INSTALLATION.md).

The library publishes the `io.github.cortenaui:phosphor_icons` artifact to **Maven Central**.

Publishing is automated through the `Publish` GitHub Actions workflow. Pushing a tag of the form `v<version>` (for example `v1.0.0`) triggers a build, signs the artifacts, uploads them to Maven Central, and attaches the AAR to a GitHub Release.

## One-time setup

The setup requires a Sonatype Central Portal account, a GPG key, and four GitHub Actions secrets, exactly the same as the main CortenaUI repository.

Open `https://github.com/cortenaui/cortenaui-phosphor-icons/settings/secrets/actions` and add the following four secrets, then create an environment named `maven-central` and assign the secrets to it (the publish workflow runs in that environment):

| Secret                           | Value                                                     |
| -------------------------------- | --------------------------------------------------------- |
| `MAVEN_CENTRAL_USERNAME`         | Username from the Sonatype publishing token.              |
| `MAVEN_CENTRAL_PASSWORD`         | Password from the Sonatype publishing token.              |
| `SIGNING_IN_MEMORY_KEY`          | Contents of `signing.key` (the full ASCII-armored block). |
| `SIGNING_IN_MEMORY_KEY_PASSWORD` | Passphrase you used when generating the GPG key.          |

## Cutting a release

Each release is a signed Git tag of the form `v<version>`.

```bash
# 1. Create a release branch off master
git checkout master
git pull
git checkout -b release/1.0.0

# 2. Bump the version. The version is controlled in the project root `build.gradle.kts`.
${EDITOR:-vim} build.gradle.kts

# 3. Commit and push the branch.
git commit -am "release: 1.0.0"
git push origin release/1.0.0

# 4. Open a PR, wait for the Build workflow to pass, merge it.

# 5. Pull the merged commit and tag it.
git checkout master
git pull
git tag v1.0.0
git push origin v1.0.0
```

The tag push fires the Publish workflow. When the workflow finishes, head to [central.sonatype.com](https://central.sonatype.com) → **Deployments** and confirm a draft deployment is staged. Review the artifacts, then click **Publish** to release to Maven Central.

A GitHub Release is also created automatically, with the AAR attached as an asset.

## Local testing

Before pushing a tag, smoke-test locally:

```bash
./gradlew publishToMavenLocal
```

This publishes the artifact to your local `~/.m2/repository`. You can then test consuming it from another local project.
