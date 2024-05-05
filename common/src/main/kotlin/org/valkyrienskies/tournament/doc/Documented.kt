package org.valkyrienskies.tournament.doc

import com.google.common.reflect.ClassPath
import java.io.File
import java.util.stream.Collectors

data class Doc(
    val kind: Kind,
    val name: String,
    val content: String,
) {
    enum class Kind(val path: String) {
        NONE(""),

        BLOCK("blocks/"),
        ITEM("items/"),
        CC_API("cc/"),
        ADVANCEMENT("advancements/"),
    }
}

interface Documented {
    fun getDoc(): (DocumentationContext.Format) -> List<Doc>
}

class DocumentationContext {
    private val pages = mutableListOf<Page>()

    fun page(title: String): Page {
        val page = Page(vtitle = title)
        pages.add(page)
        return page
    }

    class Page(
        internal var vtitle: String,
        internal var vkind: Doc.Kind = Doc.Kind.NONE,
        internal var vdesc: MutableList<String> = mutableListOf(),
        internal val vsection: MutableList<Section> = mutableListOf()
    ) {
        fun kind(kind: Doc.Kind): Page =
            this.also { it.vkind = kind }

        fun summary(str: String): Page =
            this.also { it.vdesc += str}

        fun section(name: String, fn: Section.() -> Unit): Page =
            this.also {
                val section = Section(name)
                it.vsection.add(section)
                fn(section)
            }
    }

    data class Section(
        internal var vtitle: String,
        internal var vcontent: MutableList<Pair<String,ContentKind>> = mutableListOf(),
    ) {
        enum class ContentKind {
            TEXT,
            CODE,
        }

        fun content(str: String): Section =
            this.also { it.vcontent += str to ContentKind.TEXT }

        fun codeSnippet(str: String): Section =
            this.also { it.vcontent += str to ContentKind.CODE }
    }

    enum class Format {
        GH_MARKDOWN,
        MEDIAWIKI,
    }

    internal fun build(fmt: Format): List<Doc> =
        pages.map {
            val sb = StringBuilder()

            when (fmt) {
                Format.MEDIAWIKI -> {
                    sb.append("== ")
                    sb.append(it.vtitle)
                    sb.appendLine(" ==")
                }
                Format.GH_MARKDOWN -> {
                    sb.append("# ")
                    sb.appendLine(it.vtitle)
                }
            }

            it.vdesc.forEachIndexed { index, s ->
                if (index > 0)
                    sb.appendLine()
                sb.append(s)
                sb.appendLine()
            }

            sb.appendLine()

            it.vsection.forEach { section ->
                when (fmt) {
                    Format.MEDIAWIKI -> {
                        sb.append("=== ")
                        sb.append(section.vtitle)
                        sb.appendLine(" ===")
                    }
                    Format.GH_MARKDOWN -> {
                        sb.append("## ")
                        sb.appendLine(section.vtitle)
                    }
                }

                section.vcontent.forEachIndexed { index, (text,kind) ->
                    if (index > 0)
                        sb.appendLine()

                    when (fmt) {
                        Format.MEDIAWIKI -> when (kind) {
                            Section.ContentKind.TEXT -> sb.appendLine(text)
                            Section.ContentKind.CODE -> {
                                sb.append("<nowiki>")
                                sb.append(text)
                                sb.append("</nowiki>")
                            }
                        }
                        Format.GH_MARKDOWN -> when (kind) {
                            Section.ContentKind.TEXT -> sb.appendLine(text)
                            Section.ContentKind.CODE -> {
                                sb.appendLine("```")
                                sb.appendLine(text)
                                sb.appendLine("```")
                            }
                        }
                    }
                }
            }

            Doc(it.vkind, it.vtitle, sb.toString())
        }
}

fun documentation(fn: DocumentationContext.() -> Unit): (DocumentationContext.Format) -> List<Doc> {
    val ctx = DocumentationContext()
    fn(ctx)
    return ctx::build
}

fun main(args: Array<String>) {
    val outPath = args[0]

    val classes = ClassPath.from(ClassLoader.getSystemClassLoader())
        .getAllClasses()
        .stream()
        .filter { it.packageName.startsWith("org.valkyrienskies.tournament") }
        .map { runCatching { it.load() } }
        .filter { it.isSuccess }
        .map { it.getOrThrow() }
        .filter { Documented::class.java.isAssignableFrom(it) && it != Documented::class.java }
        .map { it.getDeclaredConstructor().newInstance() as Documented }
        .collect(Collectors.toSet())

    fun format(outDir: String, fmt: DocumentationContext.Format) {
        val doc = classes.flatMap { it.getDoc()(fmt) }
        for (d in doc) {
            val f = File(outPath + File.separator + outDir + File.separator + d.kind.path + d.name.replace(" ", "_").lowercase() + ".md ")
            f.parentFile.mkdirs()
            f.writeText(d.content)
        }

        println("Wrote ${doc.size} ${fmt.name.lowercase().replace('_', ' ')} documentation entries into ${outPath + File.separator + outDir}")
    }

    format("doc", DocumentationContext.Format.GH_MARKDOWN)
    format("doc_wiki", DocumentationContext.Format.MEDIAWIKI)
}