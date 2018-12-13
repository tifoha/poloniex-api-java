package com.cf.client.wss.handler;

import com.cf.client.poloniex.wss.model.PoloniexOrderBookEntry;
import com.cf.client.poloniex.wss.model.PoloniexTradeEntry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import static com.cf.util.JsonUtils.GSON;
import static java.time.Instant.ofEpochSecond;
import static java.time.ZonedDateTime.ofInstant;

/**
 * @author Vitalii Sereda
 */
public class OrderBookMessageHandler implements IMessageHandler {
    private final static Logger LOG = LogManager.getLogger();
    private static final ZoneOffset ZONE_OFFSET = ZoneOffset.UTC;
    private final Set<Consumer<PoloniexTradeEntry>> tradeListeners = ConcurrentHashMap.newKeySet();
    private final Set<Consumer<PoloniexOrderBookEntry>> orderBookListeners = ConcurrentHashMap.newKeySet();

    @SuppressWarnings("unchecked")
    @Override
    public void handle(String message) {
        boolean hasTradeListeners = !tradeListeners.isEmpty();
        boolean hasOrderBookListeners = !orderBookListeners.isEmpty();
        if ((hasTradeListeners && message.contains("\"t\"")) //has trade events
                || (hasOrderBookListeners && (message.contains("\"o\"") || message.contains("\"i\"")))) { //hale order book events

            List<?> event = toList(message);

            if (emptyEvent(event)) {
                return;
            }

            Double sequence = (Double) event.get(1);
            List<List> dataList = (List<List>) event.get(2);
            for (List<?> data : dataList) {
                if (hasTradeListeners && Objects.equals("t", data.get(0))) {
                    PoloniexTradeEntry tradeEntry = toTradeEntry(data);
                    broadcastSafety(tradeListeners, tradeEntry);
                } else if (hasOrderBookListeners && Objects.equals("o", data.get(0))) {
                    PoloniexOrderBookEntry orderBookEntry = toOrderBookEntry(data);
                    broadcastSafety(orderBookListeners, orderBookEntry);
                }
            }
        }
    }

    private <T> void broadcastSafety(Set<Consumer<T>> orderBookListeners, T entry) {
        for (Consumer<T> consumer : orderBookListeners) {
            try {
                consumer.accept(entry);
            } catch (Exception e) {
                LOG.warn(e);
            }
        }
    }

    private PoloniexTradeEntry toTradeEntry(List<?> data) {
        return new PoloniexTradeEntry(
                (String) data.get(1),
                Objects.equals(0.0, data.get(2)) ? "sell" : "buy",
                new BigDecimal((String) data.get(3)),
                new BigDecimal((String) data.get(4)),
                ofInstant(ofEpochSecond(((Double) data.get(5)).longValue()), ZONE_OFFSET));
    }

    private PoloniexOrderBookEntry toOrderBookEntry(List<?> data) {
        return new PoloniexOrderBookEntry(
                Objects.equals(0.0, data.get(1)) ? "sell" : "buy",
                new BigDecimal((String) data.get(2)),
                new BigDecimal((String) data.get(3))
        );
    }

    private static List<?> toList(String message) {
        return GSON.fromJson(message, List.class);
    }

    private static boolean emptyEvent(List data) {
        return data.size() < 3;
    }

    public void addTradeListener(Consumer<PoloniexTradeEntry> tradeListener) {
        tradeListeners.add(tradeListener);
    }

    public void addOrderBookListener(Consumer<PoloniexOrderBookEntry> orderBookListener) {
        orderBookListeners.add(orderBookListener);
    }

    public void removeOrderBookListener(Consumer<PoloniexOrderBookEntry> listener) {
        orderBookListeners.remove(listener);
    }

    public void removeTradeListener(Consumer<PoloniexTradeEntry> listener) {
        tradeListeners.remove(listener);
    }
}
