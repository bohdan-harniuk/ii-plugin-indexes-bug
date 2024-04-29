/*
 * @author Atwix Team
 * @copyright Copyright (c) Atwix (https://www.atwix.com/)
 */

package com.my.module.indexing

import com.intellij.testFramework.PlatformTestUtil
import java.util.concurrent.atomic.AtomicBoolean

object IndexManagerTestFixture {

    private val isReady = AtomicBoolean(false)

    fun reindex() {
        if (!isReady.get()) {
            IndexManager.reindex()
            PlatformTestUtil.dispatchAllEventsInIdeEventQueue()
            isReady.set(true)
        }
    }
}
