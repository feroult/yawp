package io.yawp.repository.pipes.pump;

import java.util.List;

public class ListGenerator<T> implements PumpGenerator<T> {

    private final List<T> objects;

    private int index = 0;

    public ListGenerator(List<T> objects) {
        this.objects = objects;
    }

    public List<T> more(int batchSize) {
        int fromIndex = index;
        int toIndex = index + batchSize;

        if (toIndex >= objects.size()) {
            toIndex = objects.size();
        }

        index += toIndex - fromIndex;

        return objects.subList(fromIndex, toIndex);
    }

    public boolean hasMore() {
        return index < objects.size();
    }
}
