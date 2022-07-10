# MUMTRAN Android app

# Index
1. [Guidelines](#guidelines)
    1. [Issue reporting](#issue-reporting)
    1. [Labels](#labels)
        1. [Pull request](#pull-request)
        1. [Issue](#issue)
        1. [Bug workflow](#bug-workflow)
1. [Contributing to Source Code](#contributing-to-source-code)
    1. [Developing process](#developing-process)
        1. [Branching model](#branching-model)
        1. [Android Studio formatter setup](#android-studio-formatter-setup)
        1. [Build variants](#build-variants)
        1. [Git hooks](#git-hooks)
    1. [Contribution process](#contribution-process)
        1. [Fork and download android repository](#fork-and-download-android-repository)
        1. [Create pull request](#create-pull-request)
        1. [Create another pull request](#create-another-pull-request)
        1. [Backport pull request](#backport-pull-request)
        1. [Pull requests that also need changes on library](#pull-requests-that-also-need-changes-on-library)
        1. [Adding new files](#adding-new-files)
        1. [Testing](#testing)
	1. [File naming](#file-naming)
        1. [Menu files](#menu-files)
    1. [Translations](#translations)
    1. [Engineering practices](#engineering-practices)
        1. [Approach to technical debt](#approach-to-technical-debt)
        1. [Dependency injection](#dependency-injection) 
        1. [Custom platform APIs](#custom-platform-apis)
        1. [Testing](#testing)
1. [Releases](#releases)
    1. [Types](#types)
        1. [Stable](#stable)
        1. [Release Candidate](#release-candidate)
        1. [Dev](#dev)
    1. [Version Name and number](#version-name-and-number)
        1. [Stable / Release candidate](#stable-release-candidate)
        1. [Dev](#dev)
    1. [Release cycle](#release-cycle)
    1. [Release Process](#release-process)
        1. [Stable Release](#stable-release)
        1. [Release Candidate Release](#release-candidate-release)
        1. [Development Dev](#development-dev)


# Guidelines

## Issue reporting
* [Report the issue](https://github.com/SegFault03/MAATRAN/issues/new) and choose bug report or feature request. The template includes all the information we need to track down the issue.
* This repository is *only* for issues within the MUMTRAN Android app code. Issues in other components should be reported in their own repositories. 
* Search the [existing issues](https://github.com/SegFault03/MAATRAN/issues) first, it's likely that your issue was already reported.
If your issue appears to be a bug, and hasn't been reported, open a new issue.


## Labels


### Pull request
* 1 developing
* 2 to review
* 3 to release


### Issue
* nothing
* approved
* PR exists (and then the PR# should be shown in first post)


### Bug workflow
Every bug should be triaged in approved/needs info in a given time.
* approved: at least one other is able to reproduce it
* needs info: something unclear, or not able to reproduce
  * if no response within 1 months, bug will be closed
* pr exists: if bug is fixed, link to pr


# Contributing to Source Code
Thanks for wanting to contribute source code to MUMTRAN. That's great!

New contributions are added under AGPL version 3.

## Developing process
We are all about quality while not sacrificing speed so we use a very pragmatic workflow.

* create an issue with feature request
    * discuss it with other developers
    * create mockup if necessary
    * must be approved --> label approved
    * after that no conceptual changes!
* develop code
* create [pull request](https://github.com/nextcloud/android/pulls)
* to assure the quality of the app, any PR gets reviewed, approved and tested by [two developers](https://github.com/nextcloud/android/blob/master/.pullapprove.yml#L29) before it will be merged to master


### Branching model
![branching model](/doc/branching.png "Branching Model")
* All contributions bug fix or feature PRs target the ```master``` branch
* Feature releases will always be based on ```master```
* Bug fix releases will always be based on their respective feature-release-bug-fix-branches
* Bug fixes relevant for the most recent _and_ released feature (e.g. ```2.0.0```) or bugfix (e.g. ```2.0.1```) release will be backported to the respective bugfix branch (e.g. ```2.0.x``` or ```2.1.x```)
* Hot fixes not relevant for an upcoming feature release but the latest release can target the bug fix branch directly


### Android Studio formatter setup
Our formatter setup is rather simple:
* Standard Android Studio
* Line length 120 characters (Settings->Editor->Code Style->Right margin(columns): 120)
* Auto optimize imports (Settings->Editor->Auto Import->Optimize imports on the fly)


### Build variants
There are three build variants
* generic: no Google Stuff, used for FDroid
* gplay: with Google Stuff (Push notification), used for Google Play Store
* versionDev: based on master and library master, available as direct download and FDroid

### Git hooks
We provide git hooks to make development process easier for both the developer and the reviewers.
To install them, just run:

```bash
./gradlew installGitHooks
```

## Contribution process
* If you're working on a new feature/bug, make your own branch.
* For your first contribution start a pull request on master.


### Fork and download android repository:
* See README.md for further instructions.


### Create pull request:
* Commit your changes locally. Remember to sign off your commits (`git commit -sm 'Your commit message'`).
* Push your changes to your GitHub repo: ```git push```
* Browse to <https://github.com/YOURGITHUBNAME/android/pulls> and issue pull request
* Enter description and send pull request.

### Create another pull request:
To make sure your new pull request does not contain commits which are already contained in previous PRs, create a new branch which is a clone of upstream/master.

* ```git fetch upstream```
* ```git checkout -b my_new_master_branch upstream/master```
* If you want to rename that branch later: ```git checkout -b my_new_master_branch_with_new_name```
* Push branch to server: ```git push -u origin name_of_local_master_branch```
* Use GitHub to issue PR

### Backport pull request:
Use backport-bot via "/backport to stable-version", e.g. "/backport to stable-3.7".
This will automatically add "backport-request" label to PR and bot will create a new PR to targeted branch once the base PR is merged.
If automatic backport fails, it will create a comment.



### Adding new files
If you create a new file it needs to contain a license header. We encourage you to use the same license (AGPL3+) as we do.


Source code of library:
```java
 /* MUMTRAN Android Library is available under MIT license
 *
 *   @author Your Name
 *   Copyright (C) 2019 Your Name
 *   Copyright (C) 2019 Nextcloud GmbH
 *   
 *   Permission is hereby granted, free of charge, to any person obtaining a copy
 *   of this software and associated documentation files (the "Software"), to deal
 *   in the Software without restriction, including without limitation the rights
 *   to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *   copies of the Software, and to permit persons to whom the Software is
 *   furnished to do so, subject to the following conditions:
 *   
 *   The above copyright notice and this permission notice shall be included in
 *   all copies or substantial portions of the Software.
 *   
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, 
 *   EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *   MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND 
 *   NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS 
 *   BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN 
 *   ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN 
 *   CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *   THE SOFTWARE.
 *
 */
 ```
Source code of app:
```java
/*
 * MUMTRAN Android client application
 *
 * @author Your Name
 * Copyright (C) 2019 Your Name
 * Copyright (C) 2019 Nextcloud GmbH
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
 ```

 
 XML (layout) file:
 ```xml
<!--
  MUMTRAN Android client application

  @author Your Name
  Copyright (C) 2019 Your Name
  Copyright (C) 2019 Nextcloud GmbH
 
  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU Affero General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.
 
  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
  GNU Affero General Public License for more details.
 
  You should have received a copy of the GNU Affero General Public License
  along with this program. If not, see <https://www.gnu.org/licenses/>.
-->
```


## File naming

The file naming patterns are inspired and based on [Ribot's Android Project And Code Guidelines](https://github.com/ribot/android-guidelines/blob/c1d8c9c904eb31bf01fe24aadb963b74281fe79a/project_and_code_guidelines.md).

### Menu files

Similar to layout files, menu files should match the name of the component. For example, if we are defining a menu file that is going to be used in the `UserProfileActivity`, then the name of the file should be `activity_user_profile.xml`. Same pattern applies for menus used in adapter view items, dialogs, etc.

| Component        | Class Name             | Menu Name                   |
| ---------------- | ---------------------- | ----------------------------- |
| Activity         | `UserProfileActivity`  | `activity_user_profile.xml`   |
| Fragment         | `SignUpFragment`       | `fragment_sign_up.xml`        |
| Dialog           | `ChangePasswordDialog` | `dialog_change_password.xml`  |
| AdapterView item | ---                    | `item_person.xml`             |
| Partial layout   | ---                    | `partial_stats_bar.xml`       | 

A good practice is to not include the word `menu` as part of the name because these files are already located in the `menu` directory. In case a component uses several menus in different places (via popup menus) then the resource name would be extended. For example, if the user profile activity has two popup menus for configuring the users settings and one for the handling group assignments then the file names for the menus would be: `activity_user_profile_user_settings.xml` and `activity_user_profile_group_assignments.xml`.
 

## Engineering practices

This section contains some general guidelines for new contributors, based on common issues flagged during code review.

### Approach to technical debt

TL;DR Non-Stop Litter Picking Party!

We recognize the importance of technical debt that can slow down development, make bug fixing difficult and
discourage future contributors.

We are mindful of the [Broken Windows Theory](https://en.wikipedia.org/wiki/Broken_windows_theory) and we'd like
actively promote and encourage contributors to apply The Scout's Rule: *"Always leave the campground cleaner than 
you found it"*. Simple, little improvements will sum up and will be very appreciated by Nextcloud team.

We also promise to actively support and mentor contributors that help us to improve code quality, as we understand
that this process is challenging and requires deep understanding of the application codebase.

### Dependency injection

TL;DR Avoid calling constructors inside constructors.

In effort to modernize the codebase we are applying [Dependency Injection](https://en.wikipedia.org/wiki/Dependency_injection)
whenever possible. We use 2 approaches: automatic and manual.

We are using [Dagger 2](https://dagger.dev/) to inject dependencies into major Android components only:

 * `Activity`
 * `Fragment`
 * `Service`
 * `BroadcastReceiver`
 * `ContentProvider`

This process is fairly automatic, with `@Inject` annotation being sufficient to supply properly initialized
objects. Android lifecycle callbacks allow us to do most of the work without effort.

For other application sub-components we prefer to use constructor injection and manually provide required dependencies.

This combination allows us to benefit from automation when it provides most value, does not tie rest of the code
to any specific framework and stimulates continuous code modernization through iterative refactoring of all minor
elements.

### Testing
 
TL;DR If we can't write a test for it, it's not good.
 
Test automation is challenging in mobile applications in general. We try to improve in this area
and thereof we'd ask contributors to be mindful of their code testability:

1. new code submitted to Nextcloud project should be provided with automatic tests
2. contributions to existing code that is currently not covered by automatic tests
   should at least not make future efforts more challenging
3. whenever possible, testability should be improved even if the code is not covered by tests


