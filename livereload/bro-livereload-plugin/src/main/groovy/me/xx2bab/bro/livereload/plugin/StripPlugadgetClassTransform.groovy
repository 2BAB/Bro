package me.xx2bab.bro.livereload.plugin

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import groovy.io.FileType
import org.apache.commons.io.FileUtils
import org.gradle.api.Project

class StripPlugadgetClassTransform extends Transform {

    private Project project

    StripPlugadgetClassTransform(Project project) {
        this.project = project
    }

    @Override
    String getName() {
        return 'stripPlugadgetClass'
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_JARS
    }

    @Override
    Set<QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(final TransformInvocation invocation) throws TransformException, InterruptedException, IOException {

        def packageName = extension.packageName.replaceAll("\\.", "/")

        if (!isIncremental()) {
            invocation.outputProvider.deleteAll()
        }

        invocation.inputs.each {
            it.directoryInputs.each { directoryInput ->
                directoryInput.file.traverse(type: FileType.FILES) {
                    def entryName = it.path.substring(directoryInput.file.path.length() + 1)
                    def destName = directoryInput.name + '/' + entryName
                    def dest = invocation.outputProvider.getContentLocation(
                            destName, directoryInput.contentTypes, directoryInput.scopes, Format.DIRECTORY)
                    // check whether it is a bundle-file
                    if (entryName.contains(packageName)) {
                        FileUtils.copyFile(it, dest)
                    }
                }
            }
            it.jarInputs.each { jarInput ->
                //
            }
        }
    }
}
