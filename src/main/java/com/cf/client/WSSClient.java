package com.cf.client;

import com.cf.client.poloniex.PoloniexWSSClientRouter;
import com.cf.client.poloniex.wss.model.PoloniexOrderBookEntry;
import com.cf.client.poloniex.wss.model.PoloniexTradeEntry;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketClientCompressionHandler;
import io.netty.handler.proxy.ProxyHandler;
import io.netty.handler.proxy.Socks5ProxyHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.function.Consumer;

/**
 * @author thiko
 */
public class WSSClient implements AutoCloseable {

    private static final int MAX_CONTENT_BYTES = 8192;
    private static final String SCHEME_WSS = "wss";

    private final URI uri;
    private final SslContext sslCtx;
    private final EventLoopGroup group;
    private ProxyHandler proxy;
    private final PoloniexWSSClientRouter router;

    public WSSClient(String url, ProxySettings proxySettings) throws Exception {
        if (proxySettings != null) {
            proxy = new Socks5ProxyHandler(new InetSocketAddress(proxySettings.getHost(), proxySettings.getPort()), proxySettings.getUsername(), proxySettings.getPassword());
        }

        uri = new URI(url);

        if (!SCHEME_WSS.equalsIgnoreCase(uri.getScheme())) {
            throw new IllegalArgumentException("Only WSS is supported");
        }

        // FIXME: use secure trust manager
        sslCtx = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        group = new NioEventLoopGroup();
        router = new PoloniexWSSClientRouter(uri);

    }

    public synchronized void run() throws InterruptedException, IOException, URISyntaxException {
        Bootstrap b = new Bootstrap();
        b
                .group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ChannelPipeline p = ch.pipeline();
                        if (proxy != null) {
                            p.addFirst(proxy);
                        }
                        p.addLast(sslCtx.newHandler(ch.alloc(), uri.getHost(), 443));
                        p.addLast(new HttpClientCodec(), new HttpObjectAggregator(MAX_CONTENT_BYTES),
                                WebSocketClientCompressionHandler.INSTANCE, router);
                    }
                });

        Channel channel = b
                .connect(uri.getHost(), 443)
                .sync()
                .channel();
        router
                .handshakeFuture()
                .sync();

//        for (Entry<PoloniexWSSSubscription, IMessageHandler> subscription : subscriptions.entrySet()) {
//            WebSocketFrame frame = new TextWebSocketFrame(subscription.getKey().toString());
//            channel.writeAndFlush(frame);
//        }

//        long startTime = System.currentTimeMillis();
//
//        while (router.isRunning() && (runTimeInMillis < 0 || (startTime + runTimeInMillis > System.currentTimeMillis()))) {
//            TimeUnit.MINUTES.sleep(1);
//        }
//
//        throw new InterruptedException("Runtime exceeded");
    }

    @Override
    public synchronized void close() throws Exception {
        router.stop();
        group.shutdownGracefully();
    }

    public void subscribeOnTrade(Integer currencyPairId, Consumer<PoloniexTradeEntry> listener) {
        router.subscribeOnTrade(currencyPairId, listener);
    }

    public void subscribeOnOrderBook(Integer currencyPairId, Consumer<PoloniexOrderBookEntry> listener) {
        router.subscribeOnOrderBook(currencyPairId, listener);
    }

    public void unsubscribeOrderBook(Integer currencyPairId, Consumer<PoloniexOrderBookEntry> listener) {
        router.unsubscribeOrderBook(currencyPairId, listener);
    }

    public void unsubscribeTrade(Integer currencyPairId, Consumer<PoloniexTradeEntry> listener) {
        router.unsubscribeTrade(currencyPairId, listener);
    }
}
