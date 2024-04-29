package com.my.module.indexing

import com.my.module.BasePluginTestCase
import kotlin.test.assertContains

class ModuleIndexTest : BasePluginTestCase() {

    fun testIndexData() {
        assertContains(ModuleIndexProvider.getAllModules(myFixture.project), "Awesome_Module")
    }
}
