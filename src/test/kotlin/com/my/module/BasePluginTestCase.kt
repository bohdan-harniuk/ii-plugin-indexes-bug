/*
 * @author Atwix Team
 * @copyright Copyright (c) Atwix (https://www.atwix.com/)
 */

package com.my.module

import com.my.module.indexing.IndexManagerTestFixture
import com.intellij.testFramework.fixtures.BasePlatformTestCase

abstract class BasePluginTestCase : BasePlatformTestCase() {

    private val baseTestDataPath = "src/test/testData"

    override fun getTestDataPath(): String {
        return baseTestDataPath
    }

    override fun setUp() {
        super.setUp()
        myFixture.copyDirectoryToProject("project", "project")
        IndexManagerTestFixture.reindex()
    }
}
