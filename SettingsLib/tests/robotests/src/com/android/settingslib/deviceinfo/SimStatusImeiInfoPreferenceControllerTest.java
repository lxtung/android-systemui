/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settingslib.deviceinfo;

import static com.google.common.truth.Truth.assertThat;

import static org.robolectric.shadow.api.Shadow.extract;

import android.os.UserManager;
import android.telephony.TelephonyManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;

@RunWith(RobolectricTestRunner.class)
@Config(shadows = {SimStatusImeiInfoPreferenceControllerTest.ShadowUserManager.class,
                SimStatusImeiInfoPreferenceControllerTest.ShadowTelephonyManager.class})
public class SimStatusImeiInfoPreferenceControllerTest {

    private AbstractSimStatusImeiInfoPreferenceController mController;

    @Before
    public void setUp() {
        mController = new AbstractSimStatusImeiInfoPreferenceController(
                RuntimeEnvironment.application) {
            @Override
            public String getPreferenceKey() {
                return null;
            }
        };
    }

    @Test
    public void testIsAvailable_isAdminAndHasMobile_shouldReturnTrue() {
        ShadowUserManager userManager =
                extract(RuntimeEnvironment.application.getSystemService(UserManager.class));
        userManager.setIsAdminUser(true);
        ShadowTelephonyManager telephonyManager =
                extract(RuntimeEnvironment.application.getSystemService(TelephonyManager.class));
        telephonyManager.setDataCapable(true);

        assertThat(mController.isAvailable()).isTrue();
    }

    @Test
    public void testIsAvailable_isAdminButNoMobile_shouldReturnFalse() {
        ShadowUserManager userManager =
                extract(RuntimeEnvironment.application.getSystemService(UserManager.class));
        userManager.setIsAdminUser(true);
        ShadowTelephonyManager telephonyManager =
                extract(RuntimeEnvironment.application.getSystemService(TelephonyManager.class));
        telephonyManager.setDataCapable(false);

        assertThat(mController.isAvailable()).isFalse();
    }

    @Test
    public void testIsAvailable_isNotAdmin_shouldReturnFalse() {
        ShadowUserManager userManager =
                extract(RuntimeEnvironment.application.getSystemService(UserManager.class));
        userManager.setIsAdminUser(false);

        assertThat(mController.isAvailable()).isFalse();
    }

    @Implements(UserManager.class)
    public static class ShadowUserManager extends org.robolectric.shadows.ShadowUserManager {

        private boolean mAdminUser;

        public void setIsAdminUser(boolean isAdminUser) {
            mAdminUser = isAdminUser;
        }

        @Implementation
        public boolean isAdminUser() {
            return mAdminUser;
        }
    }

    @Implements(TelephonyManager.class)
    public static class ShadowTelephonyManager
            extends org.robolectric.shadows.ShadowTelephonyManager {
        private boolean mDataCapable = false;
        private void setDataCapable(boolean capable) {
            mDataCapable = capable;
        }

        @Implementation
        public boolean isDataCapable() {
            return mDataCapable;
        }
    }
}