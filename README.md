# Ig-Lazy-Module-Loader

[![Build Status][build-status-svg]][build-status-link]
[![Maven Central][maven-svg]][maven-link]
[![License][license-svg]][license-link]


This library helps with loading modules (features) in Android apps on demand, whenever needed. Before this library can be used a module needs to be compiled to a separate jar/dex or apk file.

Right now, the library supports java libraries and android libraries which don't rely on android resources. Support for lazy loading resources may be added later.

In the demo app there's two examples of lazy loading:
- java library - compiled first to jar and then to dex file
- android library - compiled to apk file (in this example I use Android service)

## Benefits
Lazy loading of a feature vs having it in the main main executable file brings these benefits:
- feature is loaded in memory only when really needed. It offloads code from the main executable file which remains smaller which guarantees better cold start time. On Dalvik it offloads methods off the main dex file decreasing performance penalty of multi dex
- feature code is clustered together in memory as it lives in one file and it provides most optimal execution in terms of memory access
- less disk space is used if some features remain unused because code is not uncompressed
- it may improve developer velocity where many developers concurrently work on the same codebase by providing very rigid isolation between features
- module hotswapping may be implemented allowing for faster development without restarting the app

## Tactics around lazy loading
Not every module or a feature is a good candidate to be lazily loaded as lazy loading may incur small latency on the first load.

Features used during cold start would ideally live in the main execubtale file (classes.dex) as this would ensure the most efficient app start. Anything that is outside cold start or, in general, is a less core to the app could be lazily loaded.

There are some tactics around when to trigger lazy loading. Generally speaking a module should be loaded when it is expected to be used in the near future. To make sure the loading latency does not worsen user experience these tactics can be applied:
- loading a module in the background when user is one click away from the module - it may mean that module remains unused if user doesn't decide to click into that feature or navigates back. But if there's high probability of clicking into that module then it's a well working solution
- loading a module once user navigates into the module. If loading latency is small (below 50ms) for majority of cases (e.g. p99) then code can block on loading and once it's done navigate into the feature. Otherwise a simple spinner or a progress bar can be displayed so that app does not appear as frozen.
- some modules are by nature asynchronous and this eases lazy loading as it will be the part of the asynchronous loading. An example of such module from Instagram domain is video player which runs in a secondary process. Instagram initially shows a screenshot of the video while video loads in the background (often times is fetched from the network). Lazy loading would happen in that secondary process and be completely transparent to users.

Also, the first time module is loaded after app install or app upgrade the loading latency will be higher because of dexopt or dex2oat being run (which is compiling and optimizing code for faster execution). This case, although it happens rarely compared to every cold start (e.g. once a week - after app upgade) it usually forces to apply more conservative approach (e.g. loading one click away) especially for modules of significate size (at least couple of hundred kilobytes).

## Getting started
The easiest way is to look at the demoapp. It's an Android app that shows a screen with two buttons to lazy load a java library and android library:

Each feature has a Proxy object which lives in the main app. Proxy handles lazy loading of the actual module and delegates calls to it:
```java
class ServiceProxy extends Service {

    private ServiceLike mLazyLoadedService;

    void onCreate() {
        try {
          mLazyLoadedService = LazyModuleLoaderHelper
            .createLoaderWithoutNativeLibrariesSupport(this, new ManifestReader(), mLazyLoadListener)
            .loadServiceModule(ManifestReader.LazyLoadedService, CLASS_NAME);
        } catch (LazyLoadingException e) {
          Log.e(TAG, "Failed to lazy loaded a service", e);
        }
        // delegate calls once lazy loading finished
        mLazyLoadedService.onCreate();
      }
      
      public IBinder onBind(Intent intent) {
        // keep delegating calls
        return mLazyLoadedService.onBind(intent);
      }
      // ...
  }
```
***
In the main app an implementation for ModuleManifestReader interface must be added which provides basic metadata about lazy loaded module
```java
class ManifestReader implements ModuleManifestReader {

  public final static String LazyLoadedService = "LazyLoadedService";

  // Those file need to be placed in the assets/ folder
  private final static String ServiceFileName = "lazyservice.apk";

  // Hash of the module may be useful when versioning of modules needs to be added
  private final static String ModuleHash = "null";

  @Override
  public ModuleManifest readModuleManifest(String moduleName) throws IOException {
    if (moduleName.equals(LazyLoadedService)) {
      return new ModuleManifest.Builder(moduleName).setModuleHash(ModuleHash).setDexFileName
          (ServiceFileName).build();
    } else  {
      // ...
    }
  }
}
```
***
Each compiled lazy loaded module must be placed in the assets folder. It could either be a dex file (for a java library) or apk (for android library). Right now there's no support for aar files. In the demoapp _lazylibrary.dex_ is compiled file  of _lazyloadedlibrary_ and _lazyservice.apk_ is compiled file of _lazyloadedservice_ - both included in this repo:
```
$ ls LazyLoader/demoapp/src/main/assets/*
src/main/assets//LazyLoadedModule/lazylibrary.dex
src/main/assets//LazyLoadedService/lazyservice.apk
```
For java library you need to compile the library to a jar file and and then to dex file using dx tool. You also need to add a **provided** dependency in the gradle build file: 
```
dx --dex --output=lazyloadedlibrary.dex lazyloadedlibrary.jar
```
```groovy
dependencies {
    provided project(':lazyloadedlibrary')
}
```

## Compile a AAR

```
./gradlew clean assembleRelease
```
Outputs can be found in ig-lazy-module-loader/build/outputs/

## Run the Tests
```
./gradlew clean test
```

## Gradle

Add this to your build.gradle file:
``` groovy
allprojects {
  repositories {
    maven { url 'https://jitpack.io' }
  }
}

dependencies {
    compile 'com.github.instagram:ig-lazy-module-loader:master-SNAPSHOT'
}
```

## Other Instagram Android Projects
- [ig-json-parser][ig-json-parser-link]
- [ig-disk-cache][ig-disk-cache-link]

## Instagram Engineering Blog
- [Engineering Blog][eng-blog]

## License

```
Copyright (c) 2017-present, Facebook, Inc.
All rights reserved.

This source code is licensed under the BSD-style license found in the
LICENSE file in the root directory of this source tree. An additional grant
of patent rights can be found in the PATENTS file in the same directory.
```

[eng-blog]: http://engineering.instagram.com/

[build-status-svg]: https://travis-ci.org/Instagram/ig-disk-cache.svg
[build-status-link]: https://travis-ci.org/Instagram/ig-disk-cache
[maven-svg]: https://maven-badges.herokuapp.com/maven-central/com.instagram.igdiskcache/ig-disk-cache/badge.svg?style=flat
[maven-link]: https://maven-badges.herokuapp.com/maven-central/com.instagram.igdiskcache/ig-disk-cache

[ig-json-parser-link]: https://github.com/Instagram/ig-json-parser
[ig-disk-cache-link]: https://github.com/Instagram/ig-disk-cache

[license-svg]: https://img.shields.io/badge/license-BSD-lightgrey.svg
[license-link]: https://github.com/Instagram/ig-disk-cache/blob/master/LICENSE