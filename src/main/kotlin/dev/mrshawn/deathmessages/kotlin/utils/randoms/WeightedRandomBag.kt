package dev.mrshawn.deathmessages.kotlin.utils.randoms

import kotlin.random.Random


class WeightedRandomBag<T: Any?> {

	private inner class Entry(val entryObject: T? = null, var accumulatedWeight: Double = 0.0)

	private val entries: MutableList<Entry> = ArrayList()
	private var accumulatedWeight = 0.0
	private val rand = Random

	fun addEntry(entryObject: T, weight: Double) {
		accumulatedWeight += weight
		entries.add(Entry(entryObject, accumulatedWeight))
	}

	fun getRandom(): T? {
		val randomWeight = rand.nextDouble() * accumulatedWeight

		for (entry in entries) {
			if (entry.accumulatedWeight >= randomWeight) {
				return entry.entryObject
			}
		}
		return null // should only happen when there are no entries
	}
}