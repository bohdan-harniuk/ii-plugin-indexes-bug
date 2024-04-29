package com.my.module.indexing

import com.intellij.ide.highlighter.XmlFileType
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import com.intellij.psi.xml.XmlFile
import com.intellij.util.indexing.DataIndexer
import com.intellij.util.indexing.FileBasedIndex
import com.intellij.util.indexing.FileContent
import com.intellij.util.indexing.ID
import com.intellij.util.indexing.ScalarIndexExtension
import com.intellij.util.io.EnumeratorStringDescriptor
import com.intellij.util.io.KeyDescriptor

class ModuleIndex : ScalarIndexExtension<String>() {

    object Data {
        @JvmStatic val KEY = ID.create<String, Void>("com.my.module.ModuleIndex")
    }

    override fun getName(): ID<String, Void> = Data.KEY

    override fun getIndexer(): DataIndexer<String, Void, FileContent> = DataIndexer { inputData ->
        val project = inputData.project
        val file = PsiManager.getInstance(project).findFile(inputData.file)

        if (file !is XmlFile) {
            return@DataIndexer emptyMap()
        }
        val result: MutableMap<String, Void?> = hashMapOf()
        val rootTag = file.rootTag ?: return@DataIndexer emptyMap()
        val modulesTag = rootTag.findFirstSubTag("modules") ?: return@DataIndexer emptyMap()

        for (moduleTag in modulesTag.findSubTags("module")) {
            val moduleName = moduleTag.getAttributeValue("name") ?: continue
            result[moduleName] = null
        }

        result
    }

    override fun getInputFilter(): FileBasedIndex.InputFilter = FileBasedIndex.InputFilter { vf: VirtualFile ->
        vf.fileType == XmlFileType.INSTANCE && "test.xml" == vf.name
    }

    override fun getKeyDescriptor(): KeyDescriptor<String> = EnumeratorStringDescriptor.INSTANCE

    override fun getVersion(): Int = 1

    override fun dependsOnFileContent(): Boolean = true
}
