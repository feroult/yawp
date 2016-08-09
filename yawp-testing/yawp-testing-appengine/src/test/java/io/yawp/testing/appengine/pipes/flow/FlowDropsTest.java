package io.yawp.testing.appengine.pipes.flow;

import io.yawp.driver.appengine.AppengineDriver;
import io.yawp.driver.appengine.pipes.flow.Payload;
import io.yawp.driver.appengine.pipes.flow.Work;
import io.yawp.repository.IdRef;
import io.yawp.repository.pipes.SourceMarker;
import io.yawp.testing.EndpointTestCaseBase;
import io.yawp.testing.appengine.models.Counter;
import io.yawp.testing.appengine.models.Like;
import io.yawp.testing.appengine.models.LikeToCounterPipe;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public class FlowDropsTest extends EndpointTestCaseBase {

    private long nextLikeId = 1;

    @Before
    public void before() {
        nextLikeId = 1;
    }

    @Test
    public void testFlowDropsToSameSink() {
        saveOneLikeToCounterWork();
        saveOneLikeToCounterWork();

        new AppengineDriver().pipes().flowDrops();
        awaitAsync(10, TimeUnit.SECONDS);

        Counter counter = id(Counter.class, 1l).fetch();
        assertEquals(2, counter.getCount());

        assertEquals(0, yawp(Work.class).ids().size());
    }

    private void saveOneLikeToCounterWork() {
        Like like = createLike();
        SourceMarker likeSourceMarker = createLikeSourceMarker(like);

        Payload payload = new Payload();
        payload.setSinkUri(IdRef.create(yawp, Counter.class, 1l));
        payload.setPipeClazz(LikeToCounterPipe.class);
        payload.setSourceMarkerJson(likeSourceMarker);
        payload.setSourceJson(like);
        payload.setPresent(true);


        Work work = new Work("x", payload);

        yawp.save(work);
    }

    private Like createLike() {
        Like like = new Like();
        like.setId(id(Like.class, nextLikeId++));
        return like;
    }

    private SourceMarker createLikeSourceMarker(Like like) {
        IdRef<Like> likeId = like.getId();
        IdRef<SourceMarker> sourceMarkerId = likeId.createChildId(SourceMarker.class, likeId.asLong());

        SourceMarker sourceMarker = new SourceMarker();
        sourceMarker.setId(sourceMarkerId);
        sourceMarker.setParentId(likeId);
        sourceMarker.setVersion(1);
        return sourceMarker;
    }

}
