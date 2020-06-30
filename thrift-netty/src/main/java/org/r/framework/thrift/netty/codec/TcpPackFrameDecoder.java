package org.r.framework.thrift.netty.codec;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * date 2020/6/30 15:35
 *
 * @author casper
 */
public class TcpPackFrameDecoder extends LengthFieldBasedFrameDecoder {
    // TFramedTransport framing appears at the front of the message
    private static final int LENGTH_FIELD_OFFSET = 0;

    // TFramedTransport framing is four bytes long
    private static final int LENGTH_FIELD_LENGTH = 4;

    // TFramedTransport framing represents message size *not including* framing so no adjustment
    // is necessary
    private static final int LENGTH_ADJUSTMENT = 0;

    // The client expects to see only the message *without* any framing, this strips it off
    private static final int INITIAL_BYTES_TO_STRIP = LENGTH_FIELD_LENGTH;

    public TcpPackFrameDecoder(int maxFrameSize) {
        super(maxFrameSize,
                LENGTH_FIELD_OFFSET,
                LENGTH_FIELD_LENGTH,
                LENGTH_ADJUSTMENT,
                INITIAL_BYTES_TO_STRIP);
    }
}
