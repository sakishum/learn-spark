import org.apache.commons.cli.*;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;

public class ConsoleConsumer {
    public static CommandLine commandLine = null;

    public static void main(String[] args) throws MQClientException {

        Options options = buildCommandlineOptions(new Options());
        CommandLine commandLine = parseCmdLine("consoleConsumer", args, buildCommandlineOptions(options), new DefaultParser());
        if (null == commandLine) {
            System.exit(-1);
        }
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("console-consumer-group");
        if(commandLine.hasOption('c')){
            consumer.setConsumerGroup(commandLine.getOptionValue('c'));
        }
        if(commandLine.hasOption('n')){
            consumer.setNamesrvAddr(commandLine.getOptionValue('n'));
            //TODO:没有时取系统变量
        }
        if(commandLine.hasOption('t')){
            consumer.subscribe(commandLine.getOptionValue('t'),"*");
        }
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);  //TODO:参数化

        consumer.registerMessageListener(new MessageListenerConcurrently() {

            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs,
                                                            ConsumeConcurrentlyContext context) {
                //System.out.printf("%s Receive New Messages: %s %n", Thread.currentThread().getName(), msgs.);
                for (MessageExt msg:msgs
                     ) {
                    System.out.println(new String(msg.getBody()));    //TODO
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });

        /*
         *  Launch the consumer instance.
         */
        consumer.start();

        System.out.printf("Consumer Started.%n");
    }

    public static Options buildCommandlineOptions(final Options options) {
        Option opt = new Option("h", "help", false, "Print help");
        opt.setRequired(false);
        options.addOption(opt);
        opt =
                new Option("n", "namesrvAddr", true,
                        "Name server address list, eg: 192.168.0.1:9876;192.168.0.2:9876");
        options.addOption(opt);

        opt = new Option("t", "topic", true, "topic name");
        opt.setRequired(true);
        options.addOption(opt);

        opt = new Option("q", "queue", true, "queue num, ie. 1");
        opt.setRequired(true);
        options.addOption(opt);

        opt = new Option("i", "index", true, "start queue index.");
        opt.setRequired(true);
        options.addOption(opt);

        opt = new Option("c", "count", true, "how many.");
        opt.setRequired(false);
        options.addOption(opt);

        opt = new Option("b", "broker", true, "broker addr.");
        opt.setRequired(false);
        options.addOption(opt);

        opt = new Option("g", "consumer", true, "consumer group.");
        opt.setRequired(false);
        options.addOption(opt);

        return options;
    }

    public static CommandLine parseCmdLine(final String appName, String[] args, Options options,
                                           CommandLineParser parser) {
        HelpFormatter hf = new HelpFormatter();
        hf.setWidth(110);
        CommandLine commandLine = null;
        try {
            commandLine = parser.parse(options, args);
            if (commandLine.hasOption('h')) {
                hf.printHelp(appName, options, true);
                return null;
            }
        } catch (ParseException e) {
            hf.printHelp(appName, options, true);
        }

        return commandLine;
    }
}