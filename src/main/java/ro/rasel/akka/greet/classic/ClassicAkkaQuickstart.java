package ro.rasel.akka.greet.classic;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;

import java.io.IOException;

public class ClassicAkkaQuickstart {
    public static void main(String[] args) {
        ActorSystem actorSystem = ActorSystem.create();
        final ActorRef greeterMain = actorSystem.actorOf(GreeterMain.props());

        greeterMain.tell(new GreeterMain.Hello("Charles"), null);

        try {
            System.out.println(">>> Press ENTER to exit <<<");
            System.in.read();
        } catch (IOException ignored) {
        } finally {
            actorSystem.terminate();
        }
    }
}
