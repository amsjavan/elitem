package ir.sndu.server

import akka.actor.ActorSystem
import akka.cluster.Cluster
import com.typesafe.config.Config
import io.grpc.ServerServiceDefinition
import ir.sndu.server.frontend.Frontend
import ir.sndu.server.rpc.auth.AuthServiceImpl
import ir.sndu.server.rpc.contacts.ContactServiceImpl
import ir.sndu.server.rpc.group.GroupServiceImpl
import ir.sndu.server.rpc.messaging.MessagingServiceImpl
import ir.sndu.server.rpc.user.UsersServiceImpl
import ir.sndu.rpc.auth.AuthServiceGrpc
import ir.sndu.rpc.contact.ContactServiceGrpc
import ir.sndu.rpc.group.GroupServiceGrpc
import ir.sndu.rpc.messaging.MessagingServiceGrpc
import ir.sndu.rpc.user.UserServiceGrpc

import scala.concurrent.ExecutionContext

object ElitemServerBuilder {

  def start(config: Config): Unit = {
    implicit val system: ActorSystem =
      ActorSystem(config.getString("server-name"), config)

    if (config.getList("akka.cluster.seed-nodes").isEmpty) {
      Cluster(system).join(Cluster(system).selfAddress)
    }

    implicit val ex: ExecutionContext = system.dispatcher

    Frontend.start(ServiceDescriptors.services)(config)
  }

}

object ServiceDescriptors {

  def services(implicit system: ActorSystem, ec: ExecutionContext): Seq[ServerServiceDefinition] = {
    Seq(
      AuthServiceGrpc.bindService(new AuthServiceImpl, ec),
      MessagingServiceGrpc.bindService(new MessagingServiceImpl, ec),
      ContactServiceGrpc.bindService(new ContactServiceImpl, ec),
      UserServiceGrpc.bindService(new UsersServiceImpl, ec),
      GroupServiceGrpc.bindService(new GroupServiceImpl, ec))
  }

}