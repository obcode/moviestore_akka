/**
 * Copyright (c) 2010 Oliver Braun
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the author nor the names of his contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHORS ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE AUTHORS OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package org.obraun.moviestore

import akka.actor.{Actor,ActorRef}
import akka.actor.Actor.actorOf

trait SessionManagement extends Actor {

  protected var users: Map[Int,User]
  protected var sessions = Map[Int,ActorRef]()

  abstract override def receive =
    sessionManagement orElse super.receive

  def newMovieStore: ActorRef

  def sessionManagement: Receive = {
    case Login(id) =>
      val user = users.get(id)
      user match {
        case None =>
          self.reply(Error("User id "+id+" not known!"))
        case Some(user) =>
          sessions.get(id) match {
            case None =>
              // log.info("User [%s] has logged in",
              //         user.toString)
              val session = actorOf(
                new Session(user,self)
              )
              session.start
              sessions += (id -> session)
            case _ =>
          }
      }
    case Logout(id) =>
      // log.info("User [%d] has logged out",id)
      val session = sessions(id)
      session.stop
      sessions -= id
    case msg @ ShowAvailable(userID) =>
      sessions(userID) forward msg
    case msg @ Rent(userID, _) =>
      sessions(userID) forward msg
  }

  override def postStop() = {
    // log.info("Sessionmanagement is shutting down...")
    sessions foreach { case (_,session) => session.stop }
  }
}
// vim: set ts=2 sw=4 et:
