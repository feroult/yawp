package io.yawp.repository.pipes.pump;

import java.io.Serializable;
import java.util.List;

public interface PumpGenerator<T> extends Serializable {

    boolean hasMore();

    List<T> more(int batchSize);

}
