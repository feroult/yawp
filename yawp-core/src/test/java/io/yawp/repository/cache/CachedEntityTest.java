package io.yawp.repository.cache;

import io.yawp.commons.utils.EndpointTestCase;
import io.yawp.repository.IdRef;
import io.yawp.repository.query.QueryType;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.assertSame;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class CachedEntityTest extends EndpointTestCase {

	@Test
	public void testCacheQueryViaList() {
		yawp.save(new CachedEntity("foo"));
		Map<String, Object> cacheList = CachedEntityHook.caches.get(QueryType.QUERY);

		assertThat(cacheList.size(), is(equalTo(0)));
		List<CachedEntity> list = yawp(CachedEntity.class).list();
		assertThat(cacheList.size(), is(equalTo(1)));
		assertThat(list.get(0).getText(), is(equalTo("foo")));

		List<CachedEntity> list2 = yawp(CachedEntity.class).list();
		assertThat(cacheList.size(), is(equalTo(1)));
		assertSame(list.get(0), list2.get(0)); // from cache
	}

	@Test
	public void testCacheFetch() {
		CachedEntity foo = yawp.save(new CachedEntity("foo"));
		Map<String, Object> cacheFetch = CachedEntityHook.caches.get(QueryType.FETCH);

		assertThat(cacheFetch.size(), is(equalTo(0)));
		CachedEntity refetch = foo.getId().refetch();
		assertThat(cacheFetch.size(), is(equalTo(1)));
		assertThat(refetch.getText(), is(equalTo("foo")));

		CachedEntity refetch2 = foo.getId().refetch();
		assertThat(cacheFetch.size(), is(equalTo(1)));
		assertSame(refetch, refetch2); // from cache
	}

	@Test
	public void testCacheQueryViaIds() {
		CachedEntity foo = yawp.save(new CachedEntity("foo"));
		Map<String, Object> cacheIds = CachedEntityHook.caches.get(QueryType.QUERY);

		assertThat(cacheIds.size(), is(equalTo(0)));
		List<IdRef<CachedEntity>> list = yawp(CachedEntity.class).ids();
		assertThat(cacheIds.size(), is(equalTo(1)));
		assertThat(list.get(0).toString(), is(equalTo(foo.getId().toString())));

		List<IdRef<CachedEntity>> list2 = yawp(CachedEntity.class).ids();
		assertThat(cacheIds.size(), is(equalTo(1)));
		assertSame(list, list2); // from cache
	}
}
