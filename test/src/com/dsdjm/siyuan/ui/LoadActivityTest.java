package com.dsdjm.siyuan.ui;

import android.test.ActivityInstrumentationTestCase2;

/**
 * This is a simple framework for a test of an Application.  See
 * {@link android.test.ApplicationTestCase ApplicationTestCase} for more information on
 * how to write and extend Application tests.
 * <p/>
 * To run this test, you can type:
 * adb shell am instrument -w \
 * -e class com.dsdjm.siyuan.ui.LoadActivityTest \
 * com.dsdjm.siyuan.tests/android.test.InstrumentationTestRunner
 */
public class LoadActivityTest extends ActivityInstrumentationTestCase2<LoadActivity> {

    public LoadActivityTest() {
        super("com.dsdjm.siyuan", LoadActivity.class);
    }

}
