package lispy.test

import lispy.internal.Source

fun main () {
    val source = Source ("""
        Lorem ipsum dolor sit amet, consectetur adipiscing elit. Fusce rhoncus molestie ipsum quis vulputate. 
        Vivamus sit amet libero tellus. Etiam lacinia eros ante, in finibus mi lobortis tincidunt. Integer eget 
        lorem eu tellus faucibus placerat. Sed ornare facilisis risus eu egestas. In nisl tortor, porta eu iaculis id, 
        venenatis sit amet mi. Aenean quis interdum purus. Integer feugiat pulvinar nisl vestibulum viverra. 
        Aliquam vel orci vitae est finibus placerat. Proin lectus nunc, fringilla in fermentum nec, consequat vitae nisi. 
        Praesent faucibus dui nec fermentum feugiat. Donec semper tortor viverra nisi elementum, sed tincidunt dolor convallis. 
        Suspendisse consectetur eget urna quis posuere. Donec tincidunt blandit mauris at luctus. Curabitur consectetur 
        venenatis augue, eget aliquet mi consectetur at. Etiam ut egestas ex.
    """.trimIndent())

    source.forEach { c ->
        when {
            c.isUpperCase() -> println("Found an $c at ${source.location}.")
            else -> Unit
        }
    }

    return
}

// EOF