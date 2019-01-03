package io.yawp.repository.cache;

import io.yawp.commons.utils.EndpointTestCase;
import io.yawp.repository.IdRef;
import org.junit.Test;

import java.util.List;

import static junit.framework.TestCase.assertSame;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class CachedEntityTest extends EndpointTestCase {

    @Test
    public void testCacheList() {
        yawp.save(new CachedEntity("foo"));

        assertThat(CachedEntityHook.cacheList.size(), is(equalTo(0)));
        List<CachedEntity> list = yawp(CachedEntity.class).list();
        assertThat(CachedEntityHook.cacheList.size(), is(equalTo(1)));
        assertThat(list.get(0).getText(), is(equalTo("foo")));

        List<CachedEntity> list2 = yawp(CachedEntity.class).list();
        assertThat(CachedEntityHook.cacheList.size(), is(equalTo(1)));
        assertSame(list, list2); // from cache
    }

    @Test
    public void testCacheFetch() {
        CachedEntity foo = yawp.save(new CachedEntity("foo"));

        assertThat(CachedEntityHook.cacheFetch.size(), is(equalTo(0)));
        CachedEntity refetch = foo.getId().refetch();
        assertThat(CachedEntityHook.cacheFetch.size(), is(equalTo(1)));
        assertThat(refetch.getText(), is(equalTo("foo")));

        CachedEntity refetch2 = foo.getId().refetch();
        assertThat(CachedEntityHook.cacheFetch.size(), is(equalTo(1)));
        assertSame(refetch, refetch2); // from cache
    }

    @Test
    public void testCacheIds() {
        CachedEntity foo = yawp.save(new CachedEntity("foo"));

        assertThat(CachedEntityHook.cacheIds.size(), is(equalTo(0)));
        List<IdRef<CachedEntity>> list = yawp(CachedEntity.class).ids();
        assertThat(CachedEntityHook.cacheIds.size(), is(equalTo(1)));
        assertThat(list.get(0).toString(), is(equalTo(foo.getId().toString())));

        List<IdRef<CachedEntity>> list2 = yawp(CachedEntity.class).ids();
        assertThat(CachedEntityHook.cacheIds.size(), is(equalTo(1)));
        assertSame(list, list2); // from cache
    }
}
