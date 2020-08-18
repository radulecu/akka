package ro.rasel.akka.greet.typed;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

import java.util.Objects;
import java.util.StringJoiner;

public class GreeterBot extends AbstractBehavior<GreeterBot.GreetDetailsRequest> {

    private final int max;
    private int greetingCounter;

    private GreeterBot(ActorContext<GreetDetailsRequest> context, int max) {
        super(context);
        this.max = max;
    }

    public static Behavior<GreetDetailsRequest> create(int max) {
        return Behaviors.setup(context -> new GreeterBot(context, max));
    }

    @Override
    public Receive<GreetDetailsRequest> createReceive() {
        return newReceiveBuilder()
                .onMessage(GreetDetailsRequest.class, this::onGreeted)
                .build();
    }

    private Behavior<GreetDetailsRequest> onGreeted(GreetDetailsRequest message) {
        greetingCounter++;
        getContext().getLog().info("Greeting {} for {}", greetingCounter, message.getWhom());
        if (greetingCounter == max) {
            return Behaviors.stopped();
        } else {
            message.getFrom().tell(
                    new GreeterDetailsResponse(message.getWhom(), getContext().getSelf()));
            return this;
        }
    }

    public static final class GreetDetailsRequest {
        private final String whom;
        private final ActorRef<GreeterDetailsResponse> from;

        public GreetDetailsRequest(String whom, ActorRef<GreeterDetailsResponse> from) {
            this.whom = whom;
            this.from = from;
        }

        public String getWhom() {
            return whom;
        }

        public ActorRef<GreeterDetailsResponse> getFrom() {
            return from;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            GreetDetailsRequest that = (GreetDetailsRequest) o;
            return Objects.equals(whom, that.whom) &&
                    Objects.equals(from, that.from);
        }

        @Override
        public int hashCode() {
            return Objects.hash(whom, from);
        }

        @Override
        public String toString() {
            return new StringJoiner(", ", GreetDetailsRequest.class.getSimpleName() + "[", "]")
                    .add("whom='" + whom + "'")
                    .add("from=" + from)
                    .toString();
        }
    }

    public static final class GreeterDetailsResponse {
        final String whom;
        final ActorRef<GreeterBot.GreetDetailsRequest> replyTo;

        public GreeterDetailsResponse(String whom, ActorRef<GreeterBot.GreetDetailsRequest> replyTo) {
            this.whom = whom;
            this.replyTo = replyTo;
        }

        public String getWhom() {
            return whom;
        }

        public ActorRef<GreetDetailsRequest> getReplyTo() {
            return replyTo;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            GreeterDetailsResponse that = (GreeterDetailsResponse) o;
            return Objects.equals(whom, that.whom) &&
                    Objects.equals(replyTo, that.replyTo);
        }

        @Override
        public int hashCode() {
            return Objects.hash(whom, replyTo);
        }

        @Override
        public String toString() {
            return new StringJoiner(", ", GreeterDetailsResponse.class.getSimpleName() + "[", "]")
                    .add("whom='" + whom + "'")
                    .add("replyTo=" + replyTo)
                    .toString();
        }
    }

}
