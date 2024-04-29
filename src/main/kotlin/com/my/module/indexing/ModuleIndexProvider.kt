package com.my.module.indexing

import com.intellij.openapi.project.IndexNotReadyException
import com.intellij.openapi.project.Project
import com.intellij.util.indexing.FileBasedIndex

object ModuleIndexProvider {

    fun getAllModules(project: Project): List<String> {
        return try {
            FileBasedIndex.getInstance().getAllKeys(
                ModuleIndex.Data.KEY,
                project
            ).stream().toList()
        } catch (exception: IndexNotReadyException) {
            emptyList()
        }
    }
}
