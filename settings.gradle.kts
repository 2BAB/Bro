val broPubModule = settings.startParameter.projectProperties["broPublish"]

if (broPubModule != null) {
    include(":$broPubModule")
}

if (broPubModule == null) {
    include(":bro")
    include(":bro-compiler")
    include(":bro-annotations")
    include(":bro-common")
    include(":bro-gradle-plugin")
}

rootProject.name = "bro-parent"