package org.r.framework.thrift.netty.events;

import org.r.framework.thrift.common.util.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

/**
 * date 2020/6/24 下午2:08
 *
 * @author casper
 **/
public class Postman<T extends ChannelConnectEvent> implements Closeable {

    private final Logger log = LoggerFactory.getLogger(Postman.class);

    private final BlockingQueue<T> queue;

    private final List<Subscriber<T>> subscribers;

    private final ExecutorService workers;

    private final Thread daemon;
    private final MailBox<T> mailBox;


    public Postman() {
        this(10);
    }

    public Postman(int workerNo) {
        this.queue = new LinkedBlockingQueue<>();
        subscribers = new LinkedList<>();
        mailBox = new MailBox<>(this);
        workers = new ThreadPoolExecutor(
                workerNo,
                workerNo,
                1,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(),
                r -> new Thread(r, "postman"),
                new ThreadPoolExecutor.DiscardOldestPolicy()
        );
        daemon = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        T take = queue.take();
                        log.info("Postman get mail");
                        if (CollectionUtils.isNotEmpty(subscribers)) {
                            for (Subscriber<T> subscriber : subscribers) {
                                workers.submit(new Runnable() {
                                    @Override
                                    public void run() {
                                        subscriber.readMail(take);
                                    }
                                });
                            }
                        }
                    } catch (InterruptedException e) {
//                    e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * 获取邮筒
     *
     * @return
     */
    public MailBox<T> getMailBox() {
        return mailBox;
    }

    /**
     * 派送邮件
     *
     * @param mail 邮件
     */
    public void delivery(T mail) {
        queue.add(mail);
    }

    public synchronized void subscript(Subscriber<T> subscriber) {
        this.subscribers.add(subscriber);
    }


    /**
     * 启动邮递员
     */
    public void start() {
        log.info("Postman started");
        this.daemon.start();
    }

    /**
     * 停止邮递员
     */
    public void stop() {
        this.daemon.interrupt();
    }

    @Override
    public void close() throws IOException {
        log.info("Postman shutdown................");
        this.stop();
        try {
            workers.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
            log.error("Postman shutdown unusually");
        }
        log.info("Postman shutdown");
    }
}
