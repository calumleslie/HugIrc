package com.github.calumleslie.hugs.irc

import io.netty.bootstrap.Bootstrap
import io.netty.channel.ChannelInitializer
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.handler.codec.LineBasedFrameDecoder
import io.netty.handler.codec.string.StringDecoder
import io.netty.handler.codec.string.StringEncoder
import io.netty.util.CharsetUtil
import rx.lang.scala.Observable
import rx.lang.scala.Observer
import rx.lang.scala.subjects.PublishSubject
import java.util.UUID
import scala.collection.concurrent
import io.netty.channel.Channel
import io.netty.util.concurrent.GenericFutureListener
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelFutureListener
import rx.lang.scala.Subject
import com.typesafe.scalalogging.slf4j.Logging
import io.netty.channel.ChannelInboundHandler
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.channel.ChannelHandlerContext
import com.github.calumleslie.hugs.irc.messages.NICK
import com.github.calumleslie.hugs.irc.messages.USER
import com.github.calumleslie.hugs.irc.messages.JOIN
import rx.lang.scala.Subject
import rx.lang.scala.schedulers.IOScheduler
import rx.lang.scala.observables.ConnectableObservable

class SimpleBot extends Observer[Command] with Observable[Event] with Logging {
  private val eventSubject = Subject[Event]()
  private val bootstrap = new Bootstrap()
  private val parser = new Parser()

  private val connections = new concurrent.TrieMap[ConnectionId, Channel]()

  bootstrap.group(new NioEventLoopGroup).
    channel(classOf[NioSocketChannel]).
    handler(new ChannelInitializer[SocketChannel]() {
      override def initChannel(ch: SocketChannel) {
        ch.pipeline().addLast(
          new LineBasedFrameDecoder(16 * 1024),
          new StringDecoder(CharsetUtil.UTF_8),
          new StringEncoder(CharsetUtil.UTF_8))
      }
    })

  override val asJavaObservable = eventSubject.asJavaObservable

  override def onNext(command: Command) = command match {
    case Connect(id) => {
      val channelFuture = bootstrap.connect(id.host, id.port)

      channelFuture.addListener(new ChannelFutureListener {
        override def operationComplete(future: ChannelFuture) {
          if (future.isSuccess) {
            logger.debug(s"CONNECTED: $id")
            connections(id) = future.channel()
            future.channel().pipeline().addLast("Handler for $id", new Handler(id))
          }
        }
      })
    }
    case SendMessage(id, message) => {
      connections.get(id) match {
        case Some(channel) => {
          logger.debug(s"$id > ${message.toLine}")
          channel.writeAndFlush(s"${message.toLine}\r\n")
        }
        case None => logger.warn(s"Discarding $message because channel $id not known")
      }
    }
  }

  private class Handler(id: ConnectionId) extends ChannelInboundHandlerAdapter {

    override def channelActive(ctx: ChannelHandlerContext) = {
      logger.debug(s"ACTIVE: $id")
      eventSubject.onNext(Connected(id))
    }

    override def channelRead(ctx: ChannelHandlerContext, msg: Any) = {
      val line = msg.asInstanceOf[String]

      logger.debug(s"$id < $line")

      eventSubject.onNext(MessageReceived(id, parser.parse(line)))
    }

    override def channelInactive(ctx: ChannelHandlerContext) = {
      logger.debug(s"INACTIVE: $id")

      connections.remove(id, ctx.channel())
      eventSubject.onNext(Disconnected(id))
    }
  }

  def maintainConnectionTo(host: String, port: Int, identity: Identity) = {
    val id = ConnectionId(host, port)

    onNext(Connect(id))

    subscribe { event: Event =>
      event match {
        case Connected(`id`) => {
          onNext(SendMessage(id, NICK(identity.nick)))
          onNext(SendMessage(id, USER(identity.username, identity.realname)))
        }
        case Disconnected(`id`) => {
          onNext(Connect(id))
        }
        case _ => ()
      }
    }

    id
  }
}

