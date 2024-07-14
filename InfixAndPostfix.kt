package per.seungbin.study

import kotlin.math.pow

class Stack<T>(val size : Int = 100) {
    private var top = 0
    private val stackArray = arrayOfNulls<Any>(size)

    fun push(t : T) : Boolean {
        if(isFull()) return false
        stackArray[top++] =  t
        return true
    }

    @Suppress("UNCHECKED_CAST")
    fun pop() : T? = if(isEmpty()) null else stackArray[top-- -1 ] as T

    @Suppress("UNCHECKED_CAST")
    fun getTop() : T? = if(isEmpty()) null else stackArray[top-1 ] as T

    private fun isFull() = top == size
    fun isEmpty() = top == 0

    fun empty() = { top = 0 }

    override fun toString() : String {
        return stackArray.slice(0 until top).joinToString()
    }

}

class Queue<T>(val size : Int = 100) {
    private var front = -1
    private var rear  = -1
    val queueArray = arrayOfNulls<Any>(size)

    fun offer(t : T)= if(isFull()) false else {
        if(isEmpty()) {
            front = 0
        }
        rear = ++rear % size
        queueArray[rear] = t
        true
    }

    fun poll() : T? =if(isEmpty()) null else {
            val t =  queueArray[front] as T
            if( isLast()) {
                front = -1
                rear = -1
            }
            else {
                front = ++front % size
            }
            t
    }

    fun display(typeOperation : String ) {
        print("${typeOperation} => front=${front}, rear=${rear},")
        if( front !=-1) {
            print("frontValue=${queueArray[front]}, rearValue=${queueArray[rear]}")
        }
        println()
    }

    private fun isFull() =  front==(rear+1)%size

    private fun isEmpty() = front==-1 && rear==-1

    private fun isLast() = front!=-1 && front==rear
}

class NotationConverter {

    private val operationStr = "+-*/^()"
    private val leftAssociateive = "+-*/"
    private val postfixNotation = mutableListOf<Any>()

    fun convertFrom(infixNotation :String) : List<Any> {
        convertStack.empty()
        postfixNotation.clear()

        for(ch in infixNotation ) {
           displayBefore(ch)
            if( ch !in operationStr ) {
                postfixNotation += ch
            } else if ( convertStack.isEmpty() || convertStack.getTop() == '(') {
                convertStack.push(ch)
            } else if( ch == '(') {
                convertStack.push(ch)
            } else if( ch == ')') {
                while( '(' != convertStack.getTop() ) {
                    postfixNotation += convertStack.pop()!!
                }
                convertStack.pop()
            }
            else {
                displayBefore(ch)
                var precedenceResult = comparePrecedence(ch, convertStack.getTop() )
                do {
                    when( precedenceResult) {
                        Precedence.HIGHER.precedenceResult -> {
                            convertStack.push(ch)
                            break
                        }
                        0 -> {
                            if (ch in leftAssociateive && !convertStack.isEmpty()) {
                                postfixNotation += convertStack.pop()!!
                            }
                            convertStack.push(ch) // 3^2^2
                            break
                        }
                        -1 -> {
                            postfixNotation += convertStack.pop()!!
                            precedenceResult = comparePrecedence(ch, convertStack.getTop())
                        }
                    }
                }while(true)

                displayAfter(ch)
            }
          displayAfter(ch)
        }
        while( !convertStack.isEmpty() ) {
            postfixNotation += convertStack.pop()!!
        }
        displayAfter('$')

        return postfixNotation.toList()
    }

    companion object : Comparator<Any> {
        val convertStack = Stack<Any>(30)
        override fun compare(operatorTmp: Any?, stackOperator: Any?): Int {
            if( operatorTmp == null || stackOperator == null ) return -2
            val operator = operatorTmp as Char
            val stackOperatorTmp = stackOperator as Char
            val result = when(operator) {
                '^' ->  if( stackOperatorTmp == '(' ) -1 else if(stackOperatorTmp == '^') 0 else 1
                in "*/"-> if( stackOperatorTmp in "(^" ) -1 else if(stackOperatorTmp in "*/" ) 0 else 1
                else-> if( stackOperatorTmp in "(^*/" ) -1  else 0
            }
           // println("operator=>$operator, stackOperator=$stackOperator, result=$result, stack=$convertStack" )
            return result
        }

        fun comparePrecedence(operatorTmp: Any?, stackOperator: Any?): Int = compare(operatorTmp, stackOperator)
    }

    private fun displayBefore(ch:Char) {
     //  println("Before $ch  stack=$convertStack postfixNotation=$postfixNotation")
    }

    private fun displayAfter(ch:Char) {
     //  println("After char=$ch stack=$convertStack postfixNotation=$postfixNotation")
    }

    enum class Precedence(val precedenceResult: Int ) {
        HIGHER(1), EQUAL(0), LOWER(-1), UNCOMPARABLE(-2)

    }

    fun executeArith(postfixNotation : List<Any> ) : Int {
        println(postfixNotation)
        convertStack.empty()
        var firstOperland = 0
        var secondOperland = 0
        for(temp in postfixNotation) {
            if( temp as Char in operationStr) {
                secondOperland = convertStack.pop().toString().toInt()!! as Int
                firstOperland = convertStack.pop().toString().toInt()!! as Int
                when( temp )  {
                    '+'->firstOperland += secondOperland
                    '-'->firstOperland -= secondOperland
                    '*'->firstOperland *= secondOperland
                    '/'->firstOperland /= secondOperland
                    '^'->firstOperland = firstOperland.toDouble().pow(secondOperland).toInt()
                }
                convertStack.push(firstOperland)
            }
            else {
                convertStack.push(temp)
            }
            println("after operator=$temp, firstOperland=$firstOperland, secondOperland=$secondOperland , convertStack=$convertStack")
        }
        return convertStack.pop()!! as Int
    }
}