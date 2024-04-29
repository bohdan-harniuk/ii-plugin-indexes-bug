/*
 * @author Atwix Team
 * @copyright Copyright (c) Atwix (https://www.atwix.com/)
 */

package com.my.module.indexing

import com.intellij.util.indexing.FileBasedIndex
import com.intellij.util.indexing.ID

object IndexManager {

    private val indexes = arrayOf<ID<*, *>>(
        ModuleIndex.Data.KEY
    )

    fun reindex() {
        for (id in indexes) {
            try {
                FileBasedIndex.getInstance().requestRebuild(id)
            } catch (exception: Throwable) {
                // index is not present yet
            }
        }
    }
}
