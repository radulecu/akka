package ro.rasel.akka.greet.typed;

import akka.actor.typed.ActorSystem;

import java.io.IOException;

public class TypedAkkaQuickstart {
    public static void main(String[] args) {
        final ActorSystem<GreeterMain.Hello> greeterMain = ActorSystem.create(GreeterMain.create(), "helloakka");

        greeterMain.tell(new GreeterMain.Hello("Charles"));

        try {
            System.out.println(">>> Press ENTER to exit <<<");
            System.in.read();
        } catch (IOException ignored) {
        } finally {
            greeterMain.terminate();
        }
    }
}
