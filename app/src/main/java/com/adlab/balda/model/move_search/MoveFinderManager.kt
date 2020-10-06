package com.adlab.balda.model.move_search

import com.adlab.balda.model.field.AbstractField
import com.adlab.balda.model.move_search.tree.parser.ReverseTreeParser
import com.adlab.balda.model.move_search.tree.parser.StraightTreeParser
import com.adlab.balda.utils.log
import java.lang.IllegalStateException
import kotlin.random.Random

object MoveFinderManager {

    private var moveFinder: MoveFinder? = null
    var inited = false

    fun init(words: List<String>) {
        val reverseTree = ReverseTreeParser().parse(words)
        val straightTree = StraightTreeParser().parse(words)
        moveFinder = MoveFinder(straightTree, reverseTree)
        inited = true
    }

    fun findRandomMove(field: AbstractField, excludedWords: List<String>): Move {
        if (moveFinder == null) throw IllegalStateException("variable moveFinder isn't initialized")

        val moves = moveFinder!!.findPossibleMovesForField(field.items, field.fieldSize, field.fieldType)
                .filter { it.word.length > 1 }
                .filter { !excludedWords.contains(it.word) }
        log("${moves.size}")
        log(moves.map { it.word }, ", ")
        val randomIndex = Random.nextInt(0, moves.size)
        return moves[randomIndex]
    }

}