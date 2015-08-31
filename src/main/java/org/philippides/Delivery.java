package org.philippides;

import java.io.ByteArrayOutputStream;

import org.philippides.data.DeliveryNumber;

public class Delivery {
    private DeliveryNumber first;
    private DeliveryNumber last;
    private ByteArrayOutputStream accumulatedPayload = new ByteArrayOutputStream();

    public void addTransfer(DeliveryNumber deliveryId, byte[] payload) {
        if (null == first) {
            first = deliveryId;
        }
        last = deliveryId;
        accumulatedPayload.write(payload, 0, payload.length);
    }

    public DeliveryNumber getFirst() {
        return first;
    }

    public DeliveryNumber getLast() {
        return last;
    }

    public byte[] getAccumulatedPayload() {
        return accumulatedPayload.toByteArray();
    }
}
