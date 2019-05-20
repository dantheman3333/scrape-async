package com.kramer425.scrapeasync.util

import com.kramer425.scrapeasync.{WorkAttempt, WorkFailed, WorkFinished}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Success

class FutureWorkAttemptPrinter(workAttempts: Seq[Future[WorkAttempt]])(private implicit val executionContext: ExecutionContext) {
  val total = workAttempts.size
  var successes = 0
  var failures = 0

  workAttempts.foreach(future => {
    future.onComplete {
      case Success(workAttempt) => workAttempt match {
        case _: WorkFinished => successes += 1; printStatus()
        case _: WorkFailed => failures += 1; printStatus()
      }
      case e => println(e); Console.flush()
    }
  })

  def printStatus(): Unit = {
    val iteration = successes + failures
    val str = "%d/%d, failures: %d".format(iteration, total, failures)
    println(str)

    Console.flush()
  }
}

object FutureWorkAttemptPrinter {
  def apply(workAttempts: Seq[Future[WorkAttempt]])(implicit executionContext: ExecutionContext) = new FutureWorkAttemptPrinter(workAttempts)(executionContext)
}
