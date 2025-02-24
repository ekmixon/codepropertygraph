package io.shiftleft

import org.slf4j.{Logger, LoggerFactory}

object Implicits {

  private val logger: Logger = LoggerFactory.getLogger(Implicits.getClass)

  implicit class IterableOnceDeco[T](val iterable: IterableOnce[T]) extends AnyVal {
    def onlyChecked: T = {
      if (iterable.iterator.hasNext) {
        val res = iterable.iterator.next()
        if (iterable.iterator.hasNext) {
          logger.warn("iterator was expected to have exactly one element, but it actually has more")
        }
        res
      } else { throw new NoSuchElementException() }
    }
  }

  /**
    * A wrapper around a Java iterator that throws a proper NoSuchElementException.
    *
    * Proper in this case means an exception with a stack trace.
    * This is intended to be used as a replacement for next() on the iterators
    * returned from TinkerPop since those are missing stack traces.
    */
  implicit class JavaIteratorDeco[T](val iterator: java.util.Iterator[T]) extends AnyVal {
    def nextChecked: T = {
      try {
        iterator.next
      } catch {
        case _: NoSuchElementException =>
          throw new NoSuchElementException()
      }
    }

    def onlyChecked: T = {
      if (iterator.hasNext) {
        val res = iterator.next
        if (iterator.hasNext) {
          logger.warn("iterator was expected to have exactly one element, but it actually has more")
        }
        res
      } else { throw new NoSuchElementException() }
    }
  }

}
