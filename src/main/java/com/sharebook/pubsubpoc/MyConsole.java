package com.sharebook.pubsubpoc;

import com.google.api.core.ApiFuture;
import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.TopicName;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class MyConsole implements CommandLineRunner {

    String projectId = "pub-sub-poc-1674504020968";
    String topicId = "my-topic";
    TopicName topicName;

    @Override
    public void run(String... args) throws Exception {

        try {
            var mode = getMode(args);

            appInitialize();

            switch (mode){
                case "producer":
                    producer();
                    break;
                case "consumer1":
                    consumer(1);
                    break;
                case "consumer2":
                    consumer(2);
                    break;
            }
        }
        catch (Exception ex){
            System.out.println("\n\nOCORREU UM ERRO :/ \n\n");
            System.out.println(ex.getMessage());
        }


    }

    private String getMode(String[] args) throws Exception {
        if(args.length != 1) throw new Exception("Favor informar o modo de execucao. producer, consumer1 ou consumer2.");

        var mode = args[0];

        if(!mode.equals("producer") && !mode.equals("consumer1") && !mode.equals("consumer2"))
            throw new Exception("Modo de execucao invalido: " + mode + ". Informe producer, consumer1 ou consumer2.");

        return mode;
    }

    private void appInitialize(){
        topicName = TopicName.of(projectId, topicId);
    }

    private void producer() throws Exception {
        System.out.println("PRODUCER\n");

        System.out.println("Vou gerar uma mensagem a cada 3 segundos.");
        System.out.println("digite CTRL + C para sair.\n");


        Publisher publisher = null;

        var i = 0;

        while(true){
            System.out.println("Enviando mensagem pro tópico.");

            // Create a publisher instance with default settings bound to the topic
            publisher = Publisher.newBuilder(topicName).build();

            String message = "MESSAGE " + i;
            ByteString data = ByteString.copyFromUtf8(message);
            PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(data).build();

            // Once published, returns a server-assigned message id (unique within the topic)
            ApiFuture<String> messageIdFuture = publisher.publish(pubsubMessage);
            String messageId = messageIdFuture.get();
            System.out.println("Published message ID: " + messageId);

            Thread.sleep(3000);
            i++;
        }
    }

    private void consumer(int num) throws Exception {
        System.out.println("CONSUMER " + num + "\n");
        System.out.println("digite CTRL + C para sair.\n");

        ProjectSubscriptionName subscriptionName = ProjectSubscriptionName.of(projectId, "consumer" + num);

        // Callback
        MessageReceiver receiver =
                (PubsubMessage message, AckReplyConsumer consumer) -> {
                    // Handle incoming message, then ack the received message.
                    System.out.println("Id: " + message.getMessageId());
                    System.out.println("Data: " + message.getData().toStringUtf8());
                    consumer.ack();
                };

        Subscriber subscriber = Subscriber.newBuilder(subscriptionName, receiver).build();
        // Start the subscriber.
        subscriber.startAsync().awaitRunning();
        System.out.printf("Listening for messages on %s:\n", subscriptionName.toString());
        // Allow the subscriber to run for 30s unless an unrecoverable error occurs.
        subscriber.awaitTerminated(30, TimeUnit.SECONDS);

        while(true){
            Thread.sleep(1000);
        }
    }


}
