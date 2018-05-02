package me.xx2bab.bro.livereload.plugin

import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformException
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.internal.pipeline.TransformManager
import org.gradle.api.Project

class StripPlugadgetNativeLibsTransform extends Transform {

    private Project project

    StripPlugadgetNativeLibsTransform(Project project) {
        this.project = project
        this.extension = extension
    }

    @Override
    String getName() {
        return 'stripPlugadgetNativeLibs'
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_NATIVE_LIBS
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
        // empty
    }
}
