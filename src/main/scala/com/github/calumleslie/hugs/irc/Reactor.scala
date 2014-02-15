package com.github.calumleslie.hugs.irc

import rx.lang.scala.Observable
import rx.lang.scala.Observer
import rx.lang.scala.subjects.PublishSubject
import rx.lang.scala.concurrency.Schedulers

// TODO: This is basically the same as "subject" except that I'm too lazy to provide
// a Java subject implementation. This should be changed into a subject when laziness
// subsides
// TODO: Both normal reactors and the bot could be based on this
class Reactor[E, C] extends Observable[C] with Observer[E] {
  private val commands = PublishSubject[C]
  private val events = PublishSubject[E]

  override def asJavaObservable = commands.subscribeOn(Schedulers.threadPoolForComputation).asJavaObservable
  override def asJavaObserver = events.asJavaObserver
}

object Reactor {
  def apply[E,C]( handler: (E, Observer[C]) => Unit ): Reactor[E, C] = {
    val reactor = new Reactor[E,C]
    reactor.events.subscribe { e: E => handler( e, reactor.commands ) }
    reactor
  }
}