package me.xx2bab.bro.gradle.processor

import com.alibaba.fastjson.JSON
import com.squareup.javapoet.*
import me.xx2bab.bro.annotations.BroActivity
import me.xx2bab.bro.annotations.BroApi
import me.xx2bab.bro.annotations.BroModule
import me.xx2bab.bro.common.BroProperties
import me.xx2bab.bro.common.Constants
import me.xx2bab.bro.common.gen.GenOutputs
import me.xx2bab.bro.common.gen.anno.AnnotatedElement
import me.xx2bab.bro.common.gen.anno.IBroAliasRoutingTable
import me.xx2bab.bro.common.gen.anno.IBroAnnoProcessor
import me.xx2bab.bro.common.util.FileUtils
import net.steppschuh.markdowngenerator.table.Table
import net.steppschuh.markdowngenerator.text.heading.Heading
import java.lang.annotation.ElementType
import java.util.*
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.Modifier

/**
 * A generator to generate the implementation of "IBroAliasRoutingTable"
 * which will be used by other Bro components.
 *
 * @see IBroAliasRoutingTable
 */
class BroRoutingTableAnnoProcessor : IBroAnnoProcessor {

    companion object {
        private val STRING_CLASSNAME = ClassName.get("java.lang", "String")
        private val MAP_CLASSNAME = ClassName.get("java.util", "Map")
        private val HASHMAP_CLASSNAME = ClassName.get("java.util", "HashMap")
        private val WILDCARD_ANNOTATION: TypeName = WildcardTypeName.subtypeOf(Annotation::class.java)
        private val TYPE_GENERIC_ANNOTATION_CLASS: TypeName = ParameterizedTypeName.get(ClassName.get(Class::class.java),
                WILDCARD_ANNOTATION)
        private val TYPE_STRING_AND_BRO_PROPERTIES_MAP: TypeName = ParameterizedTypeName.get(
                MutableMap::class.java, String::class.java, BroProperties::class.java)
        private val TYPE_CLASS_AND_BRO_PROPERTIES_MAP_NESTED_MAP: TypeName = ParameterizedTypeName.get(
                MAP_CLASSNAME, TYPE_GENERIC_ANNOTATION_CLASS,
                TYPE_STRING_AND_BRO_PROPERTIES_MAP)
        private val TYPE_CLASS_AND_BRO_PROPERTIES_MAP_NESTED_HASHMAP: TypeName = ParameterizedTypeName.get(
                HASHMAP_CLASSNAME, TYPE_GENERIC_ANNOTATION_CLASS,
                TYPE_STRING_AND_BRO_PROPERTIES_MAP)
        private val TYPE_STRING_AND_STRING_MAP: TypeName = ParameterizedTypeName.get(
                MutableMap::class.java, String::class.java, String::class.java)
        private val TYPE_STRING_AND_STRING_STRING_HASH_MAP: TypeName = ParameterizedTypeName.get(
                MAP_CLASSNAME, STRING_CLASSNAME, TYPE_STRING_AND_STRING_MAP)

        private const val TEMP_FIELD_TABLE = "table"
        private const val TEMP_FIELD_BRO_MAP = "broMap"
        private const val TEMP_FIELD_EXTRA_ANNO = "extraAnnotations"
        private const val TEMP_FIELD_ANNO_VALUE = "annotationValues"
        private const val TEMP_FIELD_BRO_PROP = "broProp"
    }

    private val supportedAnnotations: HashMap<String, Class<out Annotation?>> = HashMap()
    private val aliases: HashSet<String>
    private val comparator = Comparator<me.xx2bab.bro.common.gen.anno.Annotation> { a1, a2 ->
        a1!!.name.compareTo(a2!!.name)
    }

    init {
        supportedAnnotations[BroActivity::class.java.canonicalName] = BroActivity::class.java
        supportedAnnotations[BroApi::class.java.canonicalName] = BroApi::class.java
        supportedAnnotations[BroModule::class.java.canonicalName] = BroModule::class.java
        aliases = HashSet()
    }

    override fun getSupportedAnnotationTypes(): Collection<Class<out Annotation>> {
        return supportedAnnotations.values
    }

    override fun onCollect(element: Element, processingEnvironment: ProcessingEnvironment): String {
        val list = element.annotationMirrors
        val elementName = element.asType().toString()
        val annotations = TreeSet(comparator)
        for (i in list.indices) {
            val name = list[i]!!.annotationType.toString()
            // TODO: check if there is any other auto generated annotation
            if ("kotlin.Metadata".equals(name, ignoreCase = true)) {
                continue
            }

            val annotationProps = TreeMap<String, String>()
            val map = list[i]!!.elementValues
            for ((key, value) in map) {
                val safeKey = key.toString().replace("\\(\\)".toRegex(), "")
                val safeVal = value.toString().replace("([\"'])".toRegex(), "")
                annotationProps[safeKey] = safeVal
            }
            val annotation = me.xx2bab.bro.common.gen.anno.Annotation(name, annotationProps)
            annotations.add(annotation)
        }
        // TODO: support more element type
        // Parse interface info for class
//        if (element is TypeElement) {
//            annotatedElement.type = ElementType.TYPE
//            annotatedElement.clazz = annotatedElement.name
//            TypeElement typeElement = (TypeElement) element;
//            String packageName = typeElement.getQualifiedName().toString();
//        }
        val annotatedElement = AnnotatedElement(elementName, ElementType.TYPE, elementName, annotations)
        return JSON.toJSONString(annotatedElement)
    }

    override fun onGenerate(inputMetaData: List<String>,
                            genOutputs: GenOutputs,
                            processingEnvironment: ProcessingEnvironment) { // Prepare params
        val elements: MutableList<AnnotatedElement> = ArrayList()
        for (json in inputMetaData) {
            elements.add(JSON.parseObject(json, AnnotatedElement::class.java))
        }
        aliases.clear()
        // Convert the data
        val table = breakdownMetaData(elements)
        // Generate the Java class
        val className = GenOutputs.generateClassNameForImplementation(
                IBroAliasRoutingTable::class.java)
        val builder = TypeSpec.classBuilder(className)
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(IBroAliasRoutingTable::class.java)
        // A field to store routing table
        builder.addField(TYPE_CLASS_AND_BRO_PROPERTIES_MAP_NESTED_MAP, TEMP_FIELD_TABLE, Modifier.PRIVATE,
                Modifier.FINAL)
        builder.addField(TYPE_STRING_AND_BRO_PROPERTIES_MAP, TEMP_FIELD_BRO_MAP, Modifier.PRIVATE)
        builder.addField(TYPE_STRING_AND_STRING_STRING_HASH_MAP, TEMP_FIELD_EXTRA_ANNO, Modifier.PRIVATE)
        builder.addField(TYPE_STRING_AND_STRING_MAP, TEMP_FIELD_ANNO_VALUE, Modifier.PRIVATE)
        builder.addField(BroProperties::class.java, TEMP_FIELD_BRO_PROP, Modifier.PRIVATE)
        // The constructor method to init all
        builder.addMethod(generateConstructor(table))
        // The getter method for each Annotation
        builder.addMethod(generateMapGetterMethod())
        val file = JavaFile.builder(Constants.GEN_PACKAGE_NAME, builder.build())
                .indent("    ") // with 4 spaces
                .addFileComment("Generated by BroRoutingTableAnnoProcessor.").build()
        try {
            file.writeTo(processingEnvironment.filer)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Generate the doc
        generateDoc(table, genOutputs)
    }

    /**
     * Converts the AnnotatedElement list to the data structure (routing-table) that Bro needs.
     *
     * @param metaDataList Get from the annotation collectors.
     * @return The map with the key (eg. BroActivity.class) and value (class & other annotations).
     * @see BroProperties
     */
    private fun breakdownMetaData( // eg. BroActivity.class -> [{Alias, BroProperties}]
            metaDataList: List<AnnotatedElement>): Map<Class<out Annotation?>, MutableMap<String, BroProperties>> {

        val res: MutableMap<Class<out Annotation?>, MutableMap<String, BroProperties>> = HashMap()
        for (broAnno in supportedAnnotations.values) {
            res[broAnno] = HashMap()
        }
        for (ae in metaDataList) { // eg. BroActivity.class
            val key = getBroAnnotation(ae) ?: continue
            // [{Alias, BroProperties}]
            var alias = ""
            var module = ""
            val extraAnnotations: MutableMap<String, Map<String, String>> = HashMap()
            for (anno in ae.annotations) {
                if (anno.name == key.canonicalName) { // Bro annotation only has one field which is "value()"
                    alias = if (!anno.values.containsKey("alias") || anno.values["alias"]!!.isEmpty()) {
                        ae.clazz
                        // module = "";
                    } else {
                        anno.values["alias"]!!
                    }
                    if (anno.values.containsKey("module") && anno.values["module"]!!.isNotEmpty()) {
                        module = anno.values["module"]!!
                        module = module.substring(0, module.length - ".class".length)
                    }
                } else {
                    extraAnnotations[anno.name] = anno.values
                }
            }

            checkDuplicatedAlias(alias)
            val broProperties = BroProperties(ae.clazz, module, extraAnnotations)
            res[key]!![alias] = broProperties
        }
        return res
    }

    private fun getBroAnnotation(ae: AnnotatedElement): Class<out Annotation?>? {
        for (annotation in ae.annotations) {
            if (supportedAnnotations.containsKey(annotation.name)) {
                return supportedAnnotations[annotation.name]
            }
        }
        return null
    }

    /**
     * The constructor of IBroAliasRoutingTable implementation class.
     *
     * @return constructor method
     */
    private fun generateConstructor(
            table: Map<Class<out Annotation?>, MutableMap<String, BroProperties>>): MethodSpec {
        val builder = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addCode(CodeBlock.builder().addStatement("table = new \$T()",
                        TYPE_CLASS_AND_BRO_PROPERTIES_MAP_NESTED_HASHMAP).build())
        for (broAnno in table.keys) {
            builder.addCode("\n")
                    .addStatement("$TEMP_FIELD_BRO_MAP = new HashMap<>()")
            val map: Map<String, BroProperties>? = table[broAnno]
            for (alias in map!!.keys) {
                builder.addCode(generateBroProperties(map[alias]))
                        .addStatement(TEMP_FIELD_BRO_MAP + ".put(\"" + alias + "\", "
                                + TEMP_FIELD_BRO_PROP + ")")
            }
            builder.addStatement(TEMP_FIELD_TABLE + ".put(" + broAnno.canonicalName + ".class, "
                    + TEMP_FIELD_BRO_MAP + ")")
        }
        builder.addCode("\n")
                .addStatement("$TEMP_FIELD_BRO_MAP = null")
                .addStatement("$TEMP_FIELD_EXTRA_ANNO = null")
                .addStatement("$TEMP_FIELD_ANNO_VALUE = null")
                .addStatement("$TEMP_FIELD_BRO_PROP = null")
        return builder.build()
    }

    /**
     * Generates the function getRoutingMapByAnnotation(Class).
     *
     * @return The MethodSpec object used by JavaPoet.
     */
    private fun generateMapGetterMethod(): MethodSpec {
        return MethodSpec.methodBuilder("getRoutingMapByAnnotation")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override::class.java)
                .addParameter(TYPE_GENERIC_ANNOTATION_CLASS, "annotation")
                .returns(TYPE_STRING_AND_BRO_PROPERTIES_MAP)
                .addStatement("return table.get(annotation)")
                .build()
    }

    private fun generateBroProperties(broProperties: BroProperties?): String {
        val clazz = broProperties!!.clazz
        val builder = StringBuilder()
        builder.append("$TEMP_FIELD_EXTRA_ANNO = new HashMap<>();\n")
        for (anno in broProperties.extraAnnotations.keys) {
            val annoValues = broProperties.extraAnnotations[anno]!!
            builder.append(TEMP_FIELD_ANNO_VALUE).append(" = new HashMap<>();\n")
            for (field in annoValues.keys) {
                builder.append(TEMP_FIELD_ANNO_VALUE).append(".put(\"").append(field).append("\", \"")
                        .append(annoValues[field]).append("\");\n")
            }
            builder.append(TEMP_FIELD_EXTRA_ANNO).append(".put(\"").append(anno).append("\", ")
                    .append(TEMP_FIELD_ANNO_VALUE).append(");\n")
        }
        builder.append(TEMP_FIELD_BRO_PROP).append(" = new BroProperties(\"")
                .append(clazz).append("\", ")
                .append("\"").append(broProperties.module).append("\",")
                .append(TEMP_FIELD_EXTRA_ANNO)
                .append(");\n")
        return builder.toString()
    }

    /**
     * Avoid duplicated alias.
     *
     * @param alias A value in Bro Annotations that can be used to get the real reference of
     * the specific class.
     */
    private fun checkDuplicatedAlias(alias: String) {
        if (aliases.contains(alias)) {
            throw DuplicatedAliasException("BroRoutingTableAnnoProcessor: Alias \"" + alias + "\" is" +
                    " duplicated, please check annotation values!")
        }
        aliases.add(alias)
    }

    private inner class DuplicatedAliasException(s: String) : IllegalArgumentException(s)

    private fun generateDoc(
            annoInfoMap: Map<Class<out Annotation?>, MutableMap<String, BroProperties>>,
            genOutputs: GenOutputs) {
        val builder = StringBuilder()
        builder.append(Heading("RoutingTable by [Alias, BroProperties]", 1))
                .append("\n\n\n")
        for (annoType in annoInfoMap.keys) {
            builder.append(Heading(annoType.simpleName, 2)).append("\n\n")
            val aliasPropMap: Map<String, BroProperties>? = annoInfoMap[annoType]
            val aliasPropTable = Table.Builder()
                    .withAlignments(Table.ALIGN_LEFT, Table.ALIGN_LEFT)
                    .addRow("Alias", "BroProperties")
            for (alias in aliasPropMap!!.keys) {
                val broPropJson = JSON.toJSONString(aliasPropMap[alias])
                aliasPropTable.addRow(alias, broPropJson)
            }
            builder.append(aliasPropTable.build()).append("\n\n")
        }
        FileUtils.default.writeFile(builder.toString(),
                genOutputs.broBuildDirectory.absolutePath, "bro-alias-prop-map.md")
    }


}