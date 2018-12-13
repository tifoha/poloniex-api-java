package com.cf.client.poloniex;

import com.cf.client.poloniex.wss.model.PoloniexOrderBookEntry;
import com.cf.client.poloniex.wss.model.PoloniexTradeEntry;
import com.cf.client.poloniex.wss.model.PoloniexWSSSubscription;
import com.cf.client.wss.handler.IMessageHandler;
import com.cf.client.wss.handler.LoggingMessageHandler;
import com.cf.client.wss.handler.OrderBookMessageHandler;
import io.netty.channel.*;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class PoloniexWSSClientRouter extends SimpleChannelInboundHandler<Object> {
    private final static Logger LOG = LogManager.getLogger();
    private static final int MAX_FRAME_LENGTH = 126214400;
    private static final int PULSE = 1010;

    private final WebSocketClientHandshaker handshaker;
    private ChannelPromise handshakeFuture;
    private volatile boolean running;

    private Map<Integer, IMessageHandler> subscriptions = new ConcurrentHashMap<>();
    private final IMessageHandler defaultSubscriptionMessageHandler;
    private Channel channel;

    public PoloniexWSSClientRouter(URI url) throws URISyntaxException {
        this(WebSocketClientHandshakerFactory
                .newHandshaker(url, WebSocketVersion.V13, null, true, new DefaultHttpHeaders(), MAX_FRAME_LENGTH));
    }

    public PoloniexWSSClientRouter(WebSocketClientHandshaker handshaker) {
        this.handshaker = handshaker;
        this.defaultSubscriptionMessageHandler = new LoggingMessageHandler();
    }

    public ChannelFuture handshakeFuture() {
        return handshakeFuture;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        handshakeFuture = ctx.newPromise();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        handshaker.handshake(channel);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        LOG.trace("WebSocket Client disconnected!");
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel ch = ctx.channel();
        if (!handshaker.isHandshakeComplete()) {
            try {
                handshaker.finishHandshake(ch, (FullHttpResponse) msg);
                running = true;
                LOG.trace("WebSocket Client connected!");
                handshakeFuture.setSuccess();
                channel = ctx.channel();
                subscribe(channel);
            } catch (WebSocketHandshakeException e) {
                LOG.trace("WebSocket Client failed to connect");
                running = false;
                handshakeFuture.setFailure(e);
            }
            return;
        }

        if (msg instanceof FullHttpResponse) {
            FullHttpResponse response = (FullHttpResponse) msg;
            throw new IllegalStateException("Unexpected FullHttpResponse (getStatus=" + response.status() + ", content="
                    + response.content().toString(CharsetUtil.UTF_8) + ')');
        }

        WebSocketFrame frame = (WebSocketFrame) msg;
        if (frame instanceof TextWebSocketFrame) {
            TextWebSocketFrame textFrame = (TextWebSocketFrame) frame;
            String text = textFrame.text();
            LOG.trace("WebSocket Client received message: " + text);
            int channelId = getChannelId(text);
            if (channelId == PULSE) {
                return;
            }
            this.subscriptions.getOrDefault(channelId, this.defaultSubscriptionMessageHandler).handle(text);

        } else if (frame instanceof CloseWebSocketFrame) {
            LOG.trace("WebSocket Client received closing");
            running = false;
            ch.close();
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LOG.error("POLONIEX WEBSOCKET ERROR");
        cause.printStackTrace();
//        if (!handshakeFuture.isDone()) {
//            handshakeFuture.setFailure(cause);
//        }
//        running = false;
//        ctx.close();
    }

    public boolean isRunning() {
        return running;
    }

    public void subscribeOnTrade(Integer currencyPairId, Consumer<PoloniexTradeEntry> tradeListener) {
        getOrderBookHandler(currencyPairId)
                .addTradeListener(tradeListener);
    }

    public void subscribeOnOrderBook(Integer currencyPairId, Consumer<PoloniexOrderBookEntry> orderBookListener) {
        getOrderBookHandler(currencyPairId)
                .addOrderBookListener(orderBookListener);
    }

    private void subscribe(Channel channel) {
        subscriptions
                .keySet()
                .stream()
                .peek(id -> LOG.trace("Subscribing on channel: {}", id))
                .map(this::toFrame)
                .forEach(channel::writeAndFlush);
    }

    private TextWebSocketFrame toFrame(Integer id) {
        return new TextWebSocketFrame(new PoloniexWSSSubscription(id).toString());
    }

    private int getChannelId(String text) {
        int endOfId = text.indexOf(','); //[121,252507198,[["o",0,"6357.13463942","0.00000000"],["o",0,"6361.88463940","0.60000000"]]]

        if (endOfId < 0) {
            endOfId = text.indexOf(']'); //[1010]
        }

        if (endOfId < 3) { //[] o_O
            return -1;
        }
        try {
            return Integer.parseInt(text.substring(1, endOfId));
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private OrderBookMessageHandler getOrderBookHandler(Integer currencyPairId) {
        return (OrderBookMessageHandler) subscriptions
                .computeIfAbsent(currencyPairId, id -> {
                    OrderBookMessageHandler orderBookMessageHandler = new OrderBookMessageHandler();
                    if (running) {
                        PoloniexWSSSubscription subscription = new PoloniexWSSSubscription(id);
                        WebSocketFrame frame = new TextWebSocketFrame(subscription.toString());
                        channel.writeAndFlush(frame);
                    }
                    return orderBookMessageHandler;
                });
    }

    public void unsubscribeOrderBook(Integer channelId, Consumer<PoloniexOrderBookEntry> listener) {
        subscriptions.computeIfPresent(channelId, (integer, iMessageHandler) -> {
            ((OrderBookMessageHandler) iMessageHandler).removeOrderBookListener(listener);
            return iMessageHandler;
        });
    }

    public void unsubscribeTrade(Integer channelId, Consumer<PoloniexTradeEntry> listener) {
        subscriptions.computeIfPresent(channelId, (integer, iMessageHandler) -> {
            ((OrderBookMessageHandler) iMessageHandler).removeTradeListener(listener);
            return iMessageHandler;
        });
    }

    public void stop() {
        running = false;
    }
}
