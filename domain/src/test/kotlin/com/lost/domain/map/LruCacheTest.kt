package com.lost.domain.map

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class LruCacheTest {
    @Test
    fun `al pasarse del tope olvida el menos usado`() {
        val cache = LruCache<String, Int>(maxEntries = 2)
        cache["a"] = 1
        cache["b"] = 2
        cache["a"]
        cache["c"] = 3
        assertNull(cache["b"])
        assertEquals(1, cache["a"])
        assertEquals(3, cache["c"])
        assertEquals(2, cache.size)
    }
}
