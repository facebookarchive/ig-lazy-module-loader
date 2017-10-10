/**
 * Copyright (c) 2017-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */

package com.instagram.lazyload.base;

import android.app.Activity;
import android.content.Context;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/** Unit tests for LazyModuleLoader class. */
@RunWith(PowerMockRunner.class)
@PrepareForTest({CustomClassLoader.class, SystemClock.class, Class.class})
public class LazyModuleLoaderTest {

  private final String MODULE_NAME_NO1 = "java.com.instagram.module_a";
  private final String MODULE_NAME_NO2 = "java.com.instagram.module_b";

  @Mock Context mContextMock;
  @Mock ModuleStore mModuleStoreMock;
  @Mock ModuleManifestReader mModuleManifestReaderMock;
  @Mock LazyLoadListener mLazyLoadListenerMock;
  @Mock NativeModuleLoader mNativeModuleLoaderMock;

  @Mock CustomClassLoader mCustomClassLoaderMock;
  @Mock ModulePathsAndDependencies mModulePathsNo1Mock;
  @Mock ModulePathsAndDependencies mModulePathsNo2Mock;
  @Mock File mDexFileNo1Mock;
  @Mock File mOptDexFileNo1Mock;
  @Mock File mDexFileNo2Mock;
  @Mock File mOptDexFileNo2Mock;
  @Mock File mNativeLibsDirectoryMock;
  @Mock ClassLoader mClassLoaderMock;
  @Mock Activity mActivityMock;
  @Mock Fragment mSupportFragmentMock;
  @Mock android.app.Fragment mAppFragmentMock;

  // Class that represents an interface to lazy loaded module
  private static final class LazyModule {
    public LazyModule() {
      // empty
    }
  }

  // Class that represents a module that is a service
  private static final class ServiceModule extends ServiceLike {
    public ServiceModule(Context context) {
      super(context);
    }
  }

  // Class that represents a module that is a fragment
  private static final class SupportFragmentModule extends SupportFragmentLike {
    public SupportFragmentModule(Fragment fragment) {
      super(fragment);
    }
  }

  // Class that represents a module that is a fragment
  private static final class AppFragmentModule extends FragmentLike {
    public AppFragmentModule(android.app.Fragment fragment) {
      super(fragment);
    }
  }

  // Class that represents a module that is an activity
  private static final class ActivityModule extends ActivityLike {
    public ActivityModule(Activity activity) {
      super(activity);
    }
  }

  private LazyModuleLoader mObjectUnderTest = null;

  @Before
  public void setUp() throws IOException, ClassNotFoundException {
    MockitoAnnotations.initMocks(this);

    PowerMockito.mockStatic(CustomClassLoader.class);
    PowerMockito.mockStatic(SystemClock.class);

    // This object should be re-created for every test so getInstance cannot be called because
    // it would reuse existing object.
    mObjectUnderTest =
        new LazyModuleLoader(
            mContextMock,
            new DefautlLoaderAlgorithm(
                mContextMock,
                mModuleStoreMock,
                mModuleManifestReaderMock,
                mLazyLoadListenerMock,
                mNativeModuleLoaderMock,
                mCustomClassLoaderMock,
                true));

    Mockito.when(mModulePathsNo1Mock.getModuleName()).thenReturn(MODULE_NAME_NO1);

    Mockito.when(mModulePathsNo2Mock.getModuleName()).thenReturn(MODULE_NAME_NO2);

    Mockito.when(mModuleStoreMock.resolveModulePaths(mModuleManifestReaderMock, MODULE_NAME_NO1))
        .thenReturn(mModulePathsNo1Mock);

    Mockito.when(mModuleStoreMock.resolveModulePaths(mModuleManifestReaderMock, MODULE_NAME_NO2))
        .thenReturn(mModulePathsNo2Mock);

    Mockito.when(mContextMock.getClassLoader()).thenReturn(mClassLoaderMock);

    PowerMockito.when(CustomClassLoader.getInstance()).thenReturn(mCustomClassLoaderMock);

    Mockito.when(mClassLoaderMock.loadClass(LazyModule.class.getName()))
        .thenReturn((Class) LazyModule.class);
    Mockito.when(mClassLoaderMock.loadClass(ServiceModule.class.getName()))
        .thenReturn((Class) ServiceModule.class);
    Mockito.when(mClassLoaderMock.loadClass(ActivityModule.class.getName()))
        .thenReturn((Class) ActivityModule.class);
    Mockito.when(mClassLoaderMock.loadClass(SupportFragmentModule.class.getName()))
        .thenReturn((Class) SupportFragmentModule.class);
    Mockito.when(mClassLoaderMock.loadClass(AppFragmentModule.class.getName()))
        .thenReturn((Class) AppFragmentModule.class);
  }

  @Test
  public void testThatInstallSucceedsForDexModule() throws IOException {
    // given
    Mockito.when(mModulePathsNo1Mock.containsDexFile()).thenReturn(true);
    Mockito.when(mModulePathsNo1Mock.getDexFile()).thenReturn(mDexFileNo1Mock);
    Mockito.when(mModulePathsNo1Mock.getOptimizedDexFile()).thenReturn(mOptDexFileNo1Mock);

    // when
    mObjectUnderTest.installModule(MODULE_NAME_NO1);

    // then
    Mockito.verify(mCustomClassLoaderMock)
        .addDex(Mockito.eq(mDexFileNo1Mock), Mockito.eq(mOptDexFileNo1Mock));
    Mockito.verify(mLazyLoadListenerMock)
        .moduleLazilyInstalled(Mockito.eq(MODULE_NAME_NO1), Mockito.anyLong());
    Mockito.verifyZeroInteractions(mNativeModuleLoaderMock);
  }

  @Test
  public void testThatInstallSucceedsForNativeModule() throws IOException {
    // given
    Mockito.when(mModulePathsNo1Mock.containsNativeLib()).thenReturn(true);
    Mockito.when(mModulePathsNo1Mock.getNativeLibsDirectory()).thenReturn(mNativeLibsDirectoryMock);

    // when
    mObjectUnderTest.installModule(MODULE_NAME_NO1);

    // then
    Mockito.verify(mLazyLoadListenerMock)
        .moduleLazilyInstalled(Mockito.eq(MODULE_NAME_NO1), Mockito.anyLong());
    Mockito.verify(mNativeModuleLoaderMock).load(Mockito.eq(mModulePathsNo1Mock));
    Mockito.verifyZeroInteractions(mCustomClassLoaderMock);
  }

  @Test
  public void testThatModuleIsInstalledOnlyOnce() throws IOException {
    // given
    Mockito.when(mModulePathsNo1Mock.containsDexFile()).thenReturn(true);
    Mockito.when(mModulePathsNo1Mock.getDexFile()).thenReturn(mDexFileNo1Mock);
    Mockito.when(mModulePathsNo1Mock.getOptimizedDexFile()).thenReturn(mOptDexFileNo1Mock);

    // when
    mObjectUnderTest.installModule(MODULE_NAME_NO1);
    mObjectUnderTest.installModule(MODULE_NAME_NO1);

    // then
    Mockito.verify(mLazyLoadListenerMock, Mockito.times(1))
        .moduleLazilyInstalled(Mockito.eq(MODULE_NAME_NO1), Mockito.anyLong());
  }

  @Test
  public void testThatTwoModulesCanBeInstalled() throws IOException {
    // given
    Mockito.when(mModulePathsNo1Mock.containsDexFile()).thenReturn(true);
    Mockito.when(mModulePathsNo1Mock.getDexFile()).thenReturn(mDexFileNo1Mock);
    Mockito.when(mModulePathsNo1Mock.getOptimizedDexFile()).thenReturn(mOptDexFileNo1Mock);
    Mockito.when(mModulePathsNo2Mock.containsDexFile()).thenReturn(true);
    Mockito.when(mModulePathsNo2Mock.getDexFile()).thenReturn(mDexFileNo2Mock);
    Mockito.when(mModulePathsNo2Mock.getOptimizedDexFile()).thenReturn(mOptDexFileNo2Mock);

    // when
    mObjectUnderTest.installModule(MODULE_NAME_NO1);
    mObjectUnderTest.installModule(MODULE_NAME_NO2);

    // then
    Mockito.verify(mCustomClassLoaderMock)
        .addDex(Mockito.eq(mDexFileNo1Mock), Mockito.eq(mOptDexFileNo1Mock));
    Mockito.verify(mCustomClassLoaderMock)
        .addDex(Mockito.eq(mDexFileNo2Mock), Mockito.eq(mOptDexFileNo2Mock));
    Mockito.verify(mLazyLoadListenerMock, Mockito.times(1))
        .moduleLazilyInstalled(Mockito.eq(MODULE_NAME_NO1), Mockito.anyLong());
    Mockito.verify(mLazyLoadListenerMock, Mockito.times(1))
        .moduleLazilyInstalled(Mockito.eq(MODULE_NAME_NO2), Mockito.anyLong());
  }

  @Test
  public void testThatInstallSucceedsForDexModuleWithDependency() throws IOException {
    // given
    List<String> dependentModules = new ArrayList<>();
    dependentModules.add(MODULE_NAME_NO2);
    Mockito.when(mModulePathsNo1Mock.getModuleDependencies()).thenReturn(dependentModules);
    Mockito.when(mModulePathsNo1Mock.containsDexFile()).thenReturn(true);
    Mockito.when(mModulePathsNo1Mock.getDexFile()).thenReturn(mDexFileNo1Mock);
    Mockito.when(mModulePathsNo1Mock.getOptimizedDexFile()).thenReturn(mOptDexFileNo1Mock);
    Mockito.when(mModulePathsNo2Mock.containsDexFile()).thenReturn(true);
    Mockito.when(mModulePathsNo2Mock.getDexFile()).thenReturn(mDexFileNo2Mock);
    Mockito.when(mModulePathsNo2Mock.getOptimizedDexFile()).thenReturn(mOptDexFileNo2Mock);

    // when
    mObjectUnderTest.installModule(MODULE_NAME_NO1);

    // then
    Mockito.verify(mCustomClassLoaderMock)
        .addDex(Mockito.eq(mDexFileNo1Mock), Mockito.eq(mOptDexFileNo1Mock));
    Mockito.verify(mCustomClassLoaderMock)
        .addDex(Mockito.eq(mDexFileNo2Mock), Mockito.eq(mOptDexFileNo2Mock));
    Mockito.verify(mLazyLoadListenerMock)
        .moduleLazilyInstalled(Mockito.eq(MODULE_NAME_NO1), Mockito.anyLong());
    Mockito.verify(mLazyLoadListenerMock)
        .moduleLazilyInstalled(Mockito.eq(MODULE_NAME_NO2), Mockito.anyLong());
    Mockito.verifyZeroInteractions(mNativeModuleLoaderMock);
  }

  @Test
  public void testThatLoadSucceedsForDexModule()
      throws IOException, LazyLoadingException, ClassNotFoundException {
    // given
    Mockito.when(mModulePathsNo1Mock.containsDexFile()).thenReturn(true);
    Mockito.when(mModulePathsNo1Mock.getDexFile()).thenReturn(mDexFileNo1Mock);
    Mockito.when(mModulePathsNo1Mock.getOptimizedDexFile()).thenReturn(mOptDexFileNo1Mock);

    // when
    Class returnedClass = mObjectUnderTest.loadModule(MODULE_NAME_NO1, LazyModule.class.getName());

    // then
    Assert.assertEquals(returnedClass, LazyModule.class);

    Mockito.verify(mCustomClassLoaderMock)
        .addDex(Mockito.eq(mDexFileNo1Mock), Mockito.eq(mOptDexFileNo1Mock));
    Mockito.verify(mLazyLoadListenerMock)
        .moduleLazilyLoaded(Mockito.eq(MODULE_NAME_NO1), Mockito.anyLong());
    Mockito.verify(mClassLoaderMock).loadClass(LazyModule.class.getName());
    Mockito.verifyZeroInteractions(mNativeModuleLoaderMock);
  }

  @Test
  public void testThatLoadSucceedsForNativeModule()
      throws IOException, LazyLoadingException, ClassNotFoundException {
    // given
    Mockito.when(mModulePathsNo1Mock.containsNativeLib()).thenReturn(true);
    Mockito.when(mModulePathsNo1Mock.getNativeLibsDirectory()).thenReturn(mNativeLibsDirectoryMock);

    // when
    Class returnedClass = mObjectUnderTest.loadModule(MODULE_NAME_NO1, LazyModule.class.getName());

    // then
    Assert.assertEquals(returnedClass, LazyModule.class);

    Mockito.verify(mLazyLoadListenerMock)
        .moduleLazilyLoaded(Mockito.eq(MODULE_NAME_NO1), Mockito.anyLong());
    Mockito.verify(mNativeModuleLoaderMock).load(Mockito.eq(mModulePathsNo1Mock));
    Mockito.verifyZeroInteractions(mCustomClassLoaderMock);
  }

  @Test
  public void testThatLoadSucceedsForDexModuleWithDependency()
      throws IOException, LazyLoadingException {
    // given
    List<String> dependentModules = new ArrayList<>();
    dependentModules.add(MODULE_NAME_NO2);
    Mockito.when(mModulePathsNo1Mock.getModuleDependencies()).thenReturn(dependentModules);
    Mockito.when(mModulePathsNo1Mock.containsDexFile()).thenReturn(true);
    Mockito.when(mModulePathsNo1Mock.getDexFile()).thenReturn(mDexFileNo1Mock);
    Mockito.when(mModulePathsNo1Mock.getOptimizedDexFile()).thenReturn(mOptDexFileNo1Mock);
    Mockito.when(mModulePathsNo2Mock.containsDexFile()).thenReturn(true);
    Mockito.when(mModulePathsNo2Mock.getDexFile()).thenReturn(mDexFileNo2Mock);
    Mockito.when(mModulePathsNo2Mock.getOptimizedDexFile()).thenReturn(mOptDexFileNo2Mock);

    // when
    Class returnedClass = mObjectUnderTest.loadModule(MODULE_NAME_NO1, LazyModule.class.getName());

    // then
    Assert.assertEquals(returnedClass, LazyModule.class);

    Mockito.verify(mCustomClassLoaderMock)
        .addDex(Mockito.eq(mDexFileNo1Mock), Mockito.eq(mOptDexFileNo1Mock));
    Mockito.verify(mCustomClassLoaderMock)
        .addDex(Mockito.eq(mDexFileNo2Mock), Mockito.eq(mOptDexFileNo2Mock));
    Mockito.verify(mLazyLoadListenerMock)
        .moduleLazilyLoaded(Mockito.eq(MODULE_NAME_NO1), Mockito.anyLong());
    Mockito.verify(mLazyLoadListenerMock)
        .moduleLazilyInstalled(Mockito.eq(MODULE_NAME_NO2), Mockito.anyLong());
    Mockito.verifyNoMoreInteractions(mLazyLoadListenerMock);
  }

  @Test
  public void testThatLoadSucceedsForServiceModule()
      throws IOException, LazyLoadingException, ClassNotFoundException {
    // given
    Mockito.when(mModulePathsNo1Mock.containsDexFile()).thenReturn(true);
    Mockito.when(mModulePathsNo1Mock.getDexFile()).thenReturn(mDexFileNo1Mock);
    Mockito.when(mModulePathsNo1Mock.getOptimizedDexFile()).thenReturn(mOptDexFileNo1Mock);

    // when
    ServiceLike returnedService =
        mObjectUnderTest.loadServiceModule(MODULE_NAME_NO1, ServiceModule.class.getName());

    // then
    Assert.assertEquals(returnedService.getClass(), ServiceModule.class);

    Mockito.verify(mCustomClassLoaderMock)
        .addDex(Mockito.eq(mDexFileNo1Mock), Mockito.eq(mOptDexFileNo1Mock));
    Mockito.verify(mLazyLoadListenerMock)
        .moduleLazilyLoaded(Mockito.eq(MODULE_NAME_NO1), Mockito.anyLong());
    Mockito.verify(mClassLoaderMock).loadClass(ServiceModule.class.getName());
    Mockito.verifyZeroInteractions(mNativeModuleLoaderMock);
  }

  @Test
  public void testThatLoadSucceedsForActivityModule()
      throws IOException, LazyLoadingException, ClassNotFoundException {
    // given
    Mockito.when(mModulePathsNo1Mock.containsDexFile()).thenReturn(true);
    Mockito.when(mModulePathsNo1Mock.getDexFile()).thenReturn(mDexFileNo1Mock);
    Mockito.when(mModulePathsNo1Mock.getOptimizedDexFile()).thenReturn(mOptDexFileNo1Mock);

    // when
    ActivityLike returnedActivity =
        mObjectUnderTest.loadActivityModule(
            mActivityMock, MODULE_NAME_NO1, ActivityModule.class.getName());

    // then
    Assert.assertEquals(returnedActivity.getClass(), ActivityModule.class);

    Mockito.verify(mCustomClassLoaderMock)
        .addDex(Mockito.eq(mDexFileNo1Mock), Mockito.eq(mOptDexFileNo1Mock));
    Mockito.verify(mLazyLoadListenerMock)
        .moduleLazilyLoaded(Mockito.eq(MODULE_NAME_NO1), Mockito.anyLong());
    Mockito.verify(mClassLoaderMock).loadClass(ActivityModule.class.getName());
    Mockito.verifyZeroInteractions(mNativeModuleLoaderMock);
  }

  @Test
  public void testThatLoadSucceedsForSupportFragmentModule()
      throws IOException, LazyLoadingException, ClassNotFoundException {
    // given
    Mockito.when(mModulePathsNo1Mock.containsDexFile()).thenReturn(true);
    Mockito.when(mModulePathsNo1Mock.getDexFile()).thenReturn(mDexFileNo1Mock);
    Mockito.when(mModulePathsNo1Mock.getOptimizedDexFile()).thenReturn(mOptDexFileNo1Mock);

    // when
    SupportFragmentLike returnedFragment =
        mObjectUnderTest.loadSupportFragmentModule(
            mSupportFragmentMock, MODULE_NAME_NO1, SupportFragmentModule.class.getName());

    // then
    Assert.assertEquals(returnedFragment.getClass(), SupportFragmentModule.class);

    Mockito.verify(mCustomClassLoaderMock)
        .addDex(Mockito.eq(mDexFileNo1Mock), Mockito.eq(mOptDexFileNo1Mock));
    Mockito.verify(mLazyLoadListenerMock)
        .moduleLazilyLoaded(Mockito.eq(MODULE_NAME_NO1), Mockito.anyLong());
    Mockito.verify(mClassLoaderMock).loadClass(SupportFragmentModule.class.getName());
    Mockito.verifyZeroInteractions(mNativeModuleLoaderMock);
  }

  @Test
  public void testThatLoadSucceedsForFragmentModule()
      throws IOException, LazyLoadingException, ClassNotFoundException {
    // given
    Mockito.when(mModulePathsNo1Mock.containsDexFile()).thenReturn(true);
    Mockito.when(mModulePathsNo1Mock.getDexFile()).thenReturn(mDexFileNo1Mock);
    Mockito.when(mModulePathsNo1Mock.getOptimizedDexFile()).thenReturn(mOptDexFileNo1Mock);

    // when
    FragmentLike returnedFragment =
        mObjectUnderTest.loadFragmentModule(
            mAppFragmentMock, MODULE_NAME_NO1, AppFragmentModule.class.getName());

    // then
    Assert.assertEquals(returnedFragment.getClass(), AppFragmentModule.class);

    Mockito.verify(mCustomClassLoaderMock)
        .addDex(Mockito.eq(mDexFileNo1Mock), Mockito.eq(mOptDexFileNo1Mock));
    Mockito.verify(mLazyLoadListenerMock)
        .moduleLazilyLoaded(Mockito.eq(MODULE_NAME_NO1), Mockito.anyLong());
    Mockito.verify(mClassLoaderMock).loadClass(AppFragmentModule.class.getName());
    Mockito.verifyZeroInteractions(mNativeModuleLoaderMock);
  }
}
