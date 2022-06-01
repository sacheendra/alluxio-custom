package alluxio.client.block;

import alluxio.client.block.stream.BlockInStream;

public class BlockStoreCacheMetricsRecorder {
    public static ThreadLocal<BlockStoreCacheMetrics> metrics;

    public static void reset() {
        metrics.set(new BlockStoreCacheMetrics());
    }

    public static void record(BlockInStream.BlockInStreamSource dataSourceType) {
        BlockStoreCacheMetrics current = metrics.get();

        current.blocksRead += 1;
        if (dataSourceType == BlockInStream.BlockInStreamSource.NODE_LOCAL) {
            current.localBlocksRead += 1;
        } else if (dataSourceType == BlockInStream.BlockInStreamSource.REMOTE) {
            current.remoteBlocksRead += 1;
        } else {
            current.ufsBlocksRead += 1;
        }

        metrics.set(current);
    }

    public static BlockStoreCacheMetrics get() {
        return metrics.get();
    }
}
