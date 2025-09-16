package net.sbo.mod.processor

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import java.io.OutputStreamWriter

class SboEventProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation("net.sbo.mod.utils.events.annotations.SboEvent")
        val generatedObjects = mutableSetOf<String>()

        val classSymbols = symbols.filterIsInstance<KSFunctionDeclaration>()
            .groupBy { it.parentDeclaration as? KSClassDeclaration }

        classSymbols.forEach { (clazz, functions) ->
            if (clazz == null) return@forEach
            val packageName = clazz.packageName.asString()
            val className = clazz.simpleName.asString()
            val fileName = "${className}_SboEventRegister"

            generatedObjects.add("$packageName.$fileName")

            val isObject = clazz.classKind == ClassKind.OBJECT
            if (!isObject) {
                logger.error("@SboEvent can only be used inside objects: ${clazz.simpleName.asString()}")
                return@forEach
            }
            val instanceRef = className

            logger.info("Generating EventRegister for $packageName.$className with functions: ${functions.map { it.simpleName.asString() }}")

            // Nur KSFile-Objekte sammeln, die nicht null sind
            val dependencies = functions.mapNotNull { it.containingFile }.toTypedArray<KSFile>()
            val file = codeGenerator.createNewFile(
                Dependencies(true, *dependencies),
                packageName,
                fileName
            )

            OutputStreamWriter(file).use { writer ->
                val functionCalls = functions.joinToString("\n") { fn ->
                    val paramType = fn.parameters.firstOrNull()?.type?.resolve()?.declaration?.qualifiedName?.asString()
                    if (paramType == null) {
                        "// Cannot resolve type for ${fn.simpleName.asString()}"
                    } else {
                        "EventBus.on($paramType::class) { e -> $instanceRef.${fn.simpleName.asString()}(e) }"
                    }
                }

                writer.write("""
                    package $packageName

                    import net.sbo.mod.utils.events.EventBus

                    object $fileName {
                        fun register() {
                            $functionCalls
                        }
                    }
                """.trimIndent())
            }
        }

        if (generatedObjects.isNotEmpty()) {
            val allDependencies: List<KSFile> = classSymbols.keys.mapNotNull { it?.containingFile }
            val registryFile = codeGenerator.createNewFile(
                Dependencies(true, *allDependencies.toTypedArray()),
                "net.sbo.mod.utils.events",
                "SboEventGeneratedRegistry"
            )

            OutputStreamWriter(registryFile).use { writer ->
                writer.write(
                    """
                    package net.sbo.mod.utils.events
        
                    object SboEventGeneratedRegistry {
                        fun registerAll() {
                            ${generatedObjects.joinToString("\n") { "$it.register()" }}
                        }
                    }
                """.trimIndent()
                )
            }
        }
        return emptyList()
    }
}
